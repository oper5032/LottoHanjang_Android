package com.lotto.hanjang

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class LottoState(val ticket: Ticket = Ticket(), val ready: Boolean = false,
    val busy: Boolean = false, val message: String? = null)

class LottoViewModel(app: Application) : AndroidViewModel(app) {
    private val repository = TicketRepository(app)
    private val mutable = MutableStateFlow(LottoState())
    val state = mutable.asStateFlow()
    init {
        viewModelScope.launch {
            try {
                repository.refresh()
                repository.ticket.collect { mutable.value = mutable.value.copy(ticket = it, ready = true) }
            } catch (_: Exception) { fail("저장된 번호를 불러오지 못했어요. 앱을 다시 열어주세요.") }
        }
        viewModelScope.launch {
            while (true) { refresh(); delay(1000) }
        }
    }
    fun refresh() { viewModelScope.launch {
        try { repository.refresh() } catch (_: Exception) { fail("기기 저장소를 확인해주세요.") }
    } }
    private fun fail(message: String) { mutable.value = mutable.value.copy(busy = false, message = message) }
    fun clearMessage() { mutable.value = mutable.value.copy(message = null) }
    fun free() {
        if (!mutable.value.ready || mutable.value.busy) return
        mutable.value = mutable.value.copy(busy = true, message = null)
        viewModelScope.launch {
            try { repository.grant(); mutable.value = mutable.value.copy(busy = false) }
            catch (_: Exception) { fail("번호 저장에 실패했어요. 다시 시도해주세요.") }
        }
    }
    fun reward(start: (String) -> Unit) {
        if (!mutable.value.ready || mutable.value.busy) return
        mutable.value = mutable.value.copy(busy = true, message = null)
        viewModelScope.launch {
            try {
                val token = repository.beginReward()
                if (token == null) { mutable.value = mutable.value.copy(busy = false); return@launch }
                start(token)
            } catch (_: Exception) { fail("광고를 준비하지 못했어요. 다시 시도해주세요.") }
        }
    }
    fun earned(token: String) { viewModelScope.launch {
        try {
            val granted = repository.grant(token)
            mutable.value = mutable.value.copy(message = if (granted) "광고 보상으로 번호를 받았어요." else "날짜가 바뀌었거나 오늘 한도에 도달했어요.")
        } catch (_: Exception) { fail("보상 저장에 실패했어요. 기기 저장소를 확인해주세요.") }
    } }
    fun adClosed(earned: Boolean) {
        mutable.value = mutable.value.copy(busy = false,
            message = if (earned) mutable.value.message else "시청을 완료하지 않아 추가 번호가 지급되지 않았어요.")
    }
    fun adFailed() = fail("아직 광고를 불러오지 못했어요. 번호와 남은 횟수는 그대로예요. 잠시 후 다시 시도해주세요.")
}
