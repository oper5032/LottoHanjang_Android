package com.lotto.hanjang

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map
import org.json.JSONArray
import java.util.UUID

private val Context.ticketStore by preferencesDataStore("daily_ticket")

class TicketRepository(context: Context) {
    private val store = context.applicationContext.ticketStore
    private val dayKey = stringPreferencesKey("korea_date")
    private val gamesKey = stringPreferencesKey("games")
    private val pendingKey = stringPreferencesKey("pending_reward")
    val ticket = store.data.map { prefs ->
        Ticket(prefs[dayKey] ?: LottoRules.today(), decode(prefs[gamesKey]))
    }

    private fun decode(raw: String?): List<List<Int>> {
        if (raw == null) return emptyList()
        val array = JSONArray(raw)
        return List(array.length()) { row ->
            val values = array.getJSONArray(row)
            List(values.length()) { values.getInt(it) }.also {
                check(it.size == 6 && it.distinct().size == 6 && it.all { n -> n in 1..45 } && it == it.sorted())
            }
        }.also { check(it.size <= LottoRules.MAX_GAMES) }
    }

    suspend fun refresh() {
        store.edit { prefs ->
            val today = LottoRules.today()
            if (prefs[dayKey] != today) {
                prefs[dayKey] = today
                prefs[gamesKey] = "[]"
                prefs.remove(pendingKey)
            }
        }
    }

    suspend fun beginReward(): String? {
        refresh()
        var token: String? = null
        store.edit { prefs ->
            val count = decode(prefs[gamesKey]).size
            if (count in 1 until LottoRules.MAX_GAMES) {
                token = "${prefs[dayKey]}:${UUID.randomUUID()}"
                prefs[pendingKey] = token!!
            }
        }
        return token
    }

    // DataStore serializes the date, entitlement and number write in one transaction.
    suspend fun grant(rewardToken: String? = null): Boolean {
        refresh()
        var granted = false
        store.edit { prefs ->
            val games = decode(prefs[gamesKey])
            val rewarded = rewardToken != null && prefs[pendingKey] == rewardToken &&
                rewardToken.startsWith("${LottoRules.today()}:")
            if (prefs[dayKey] == LottoRules.today() &&
                LottoRules.canGrant(games.size, rewarded) &&
                (rewardToken == null && games.isEmpty() || rewarded && games.isNotEmpty())) {
                prefs[gamesKey] = JSONArray(games + listOf(LottoRules.generate())).toString()
                prefs.remove(pendingKey)
                granted = true
            }
        }
        return granted
    }
}
