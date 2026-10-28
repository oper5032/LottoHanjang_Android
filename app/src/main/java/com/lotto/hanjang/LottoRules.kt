package com.lotto.hanjang

import java.security.SecureRandom
import java.time.Clock
import java.time.LocalDate
import java.time.ZoneId

object LottoRules {
    const val MAX_GAMES = 5
    val korea: ZoneId = ZoneId.of("Asia/Seoul")
    fun today(clock: Clock = Clock.systemUTC()): String = LocalDate.now(clock.withZone(korea)).toString()
    fun generate(): List<Int> {
        val pool = (1..45).toMutableList()
        val random = SecureRandom()
        return List(6) { pool.removeAt(random.nextInt(pool.size)) }.sorted()
    }
    fun canGrant(count: Int, rewarded: Boolean) = count < MAX_GAMES && (count == 0 || rewarded)
}

data class Ticket(val date: String = LottoRules.today(), val games: List<List<Int>> = emptyList())
