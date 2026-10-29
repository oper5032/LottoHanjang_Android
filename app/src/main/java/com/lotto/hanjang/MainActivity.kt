package com.lotto.hanjang

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

private val Background = Color(0xFFF7F6F0)
private val Ink = Color(0xFF19392F)
private val Green = Color(0xFF196B4D)
private val Muted = Color(0xFF6E7B73)
private val Soft = Color(0xFFE6EEE5)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val adPrivacy = AdPrivacy(this)
        adPrivacy.refresh()
        setContent {
            MaterialTheme(colorScheme = lightColorScheme(primary = Green, background = Background,
                surface = Color.White, onSurface = Ink, onBackground = Ink)) {
                val vm: LottoViewModel = viewModel()
                val state by vm.state.collectAsStateWithLifecycle()
                val lifecycle = LocalLifecycleOwner.current.lifecycle
                DisposableEffect(lifecycle) {
                    val observer = LifecycleEventObserver { _, event ->
                        if (event == Lifecycle.Event.ON_RESUME) vm.refresh()
                    }
                    lifecycle.addObserver(observer)
                    onDispose { lifecycle.removeObserver(observer) }
                }
                LottoScreen(state, adPrivacy, vm::free, {
                    if (adPrivacy.canRequestAds && !adPrivacy.busy) vm.reward { token -> RewardAds.show(this,
                        earned = { vm.earned(token) }, closed = vm::adClosed, failed = vm::adFailed) }
                }, vm::clearMessage)
            }
        }
    }
}

@Composable
private fun LottoScreen(state: LottoState, adPrivacy: AdPrivacy, free: () -> Unit, reward: () -> Unit, clear: () -> Unit) {
    var settings by remember { mutableStateOf(false) }
    var rewardInfo by remember { mutableStateOf(false) }
    var privacyPolicy by remember { mutableStateOf(false) }
    val ticket = if (state.ticket.date == LottoRules.today()) state.ticket else Ticket()
    val count = ticket.games.size
    Surface(color = Background, modifier = Modifier.fillMaxSize()) {
        Box(Modifier.safeDrawingPadding(), contentAlignment = Alignment.TopCenter) {
            Column(Modifier.widthIn(max = 438.dp).fillMaxWidth().verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 18.dp), verticalArrangement = Arrangement.spacedBy(18.dp)) {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("로또 한장", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    TextButton(onClick = { settings = true }) { Text("설정", color = Muted) }
                }
                Text(when (count) {
                    0 -> "오늘의 작은 기대,\n한 장부터 시작해요."
                    1 -> "첫 번째 번호가\n준비됐어요."
                    5 -> "오늘의 한 장,\n모두 완성했어요."
                    else -> "한 줄 더,\n내 번호가 늘었어요."
                }, fontSize = 28.sp, lineHeight = 42.sp, fontWeight = FontWeight.Bold)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    repeat(5) { i -> Box(Modifier.weight(1f).height(5.dp)
                        .background(if (i < count) Green else Color(0xFFDDE3D9), CircleShape)) }
                }
                Text("오늘 $count / 5게임  ·  매일 00:00 초기화 (한국 시간)", fontSize = 11.sp, color = Muted)
                Surface(shape = RoundedCornerShape(20.dp), color = Color.White) {
                    Column(Modifier.fillMaxWidth().padding(17.dp), verticalArrangement = Arrangement.spacedBy(13.dp)) {
                        Text("TODAY’S TICKET", fontSize = 11.sp, color = Green)
                        repeat(5) { index ->
                            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(('A' + index).toString(), modifier = Modifier.width(14.dp),
                                    color = if (index < count) Green else Muted, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                repeat(6) { col ->
                                    val number = ticket.games.getOrNull(index)?.get(col)
                                    Box(Modifier.weight(1f).aspectRatio(1f).background(ballColor(number), CircleShape),
                                        contentAlignment = Alignment.Center) {
                                        Text(number?.toString()?.padStart(2, '0') ?: "·", fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold, color = if (number == null) Muted else Ink)
                                    }
                                }
                            }
                        }
                        Text(when(count) { 0 -> "첫 게임은 광고 없이 무료로 받아요."
                            5 -> "오늘의 한 장이 완성됐어요."
                            else -> "다음 줄은 광고 시청 완료 후 채워져요." }, fontSize = 11.sp, color = Muted)
                    }
                }
                state.message?.let { message ->
                    Surface(color = Soft, shape = RoundedCornerShape(12.dp)) {
                        Text(message, Modifier.padding(12.dp), fontSize = 13.sp, color = Green)
                    }
                }
                Button(onClick = { if (count == 0) free() else rewardInfo = true },
                    enabled = state.ready && !state.busy && count < 5 &&
                        (count == 0 || (adPrivacy.canRequestAds && !adPrivacy.busy)),
                    modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp), shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(disabledContainerColor = Soft, disabledContentColor = Ink)) {
                    if (state.busy) {
                        CircularProgressIndicator(Modifier.size(18.dp), strokeWidth = 2.dp, color = Ink)
                        Spacer(Modifier.width(10.dp))
                    }
                    Text(when { !state.ready -> "번호 불러오는 중…"; state.busy -> "잠시만 기다려주세요…"
                        count == 0 -> "오늘 첫 번호 받기"; count == 5 -> "오늘의 5게임을 모두 받았어요"
                        else -> "광고 보고 1게임 더 받기" }, fontWeight = FontWeight.Bold)
                }
                Text(if (count == 5) "내일 00:00에 다시 번호를 받을 수 있어요."
                    else "첫 게임 무료 · 이후 광고 1회당 1게임", fontSize = 12.sp, color = Green)
                if (count in 1..4 && (!adPrivacy.canRequestAds || adPrivacy.busy)) {
                    Text("광고 개인정보 설정 확인 후 추가 번호를 받을 수 있어요. 기존 번호는 계속 볼 수 있어요.",
                        fontSize = 12.sp, color = Muted)
                    TextButton(onClick = adPrivacy::refresh, enabled = !adPrivacy.busy) {
                        Text(if (adPrivacy.busy) "광고 설정 확인 중…" else "광고 설정 다시 확인")
                    }
                }
                adPrivacy.message?.let { Text(it, fontSize = 12.sp, color = Muted) }
                Text("무작위 번호 추천이며 당첨을 보장하지 않아요.\n실제 복권 구매·결제 기능은 제공하지 않아요.",
                    fontSize = 11.sp, lineHeight = 17.sp, color = Muted)
            }
        }
    }
    if (rewardInfo) AlertDialog(onDismissRequest = { rewardInfo = false },
        title = { Text("다음 한 줄을 채워볼까요?") },
        text = { Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("+1 GAME", color = Green, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Text("광고 1회의 보상 조건을 완료하면 무작위 번호 6개로 된 1게임이 추가돼요. 실제 복권이나 현금 보상이 아니며, 당첨 확률을 높여주지 않아요.\n보상 조건 완료 전에 닫으면 추가 번호는 지급되지 않지만 기존 번호는 계속 볼 수 있어요.\n한국 시간 자정에는 기존 번호와 횟수가 초기화되며, 날짜가 바뀌면 이전 날짜의 광고 보상은 지급되지 않아요.")
            Text("현재 $count / 5게임 · 추가 가능 ${5-count}게임", color = Muted)
        } }, confirmButton = { TextButton(onClick = { rewardInfo = false; clear(); reward() }) { Text("광고 보고 1게임 더 받기") } },
        dismissButton = { TextButton(onClick = { rewardInfo = false }) { Text("닫기") } })
    if (settings) AlertDialog(onDismissRequest = { settings = false }, title = { Text("로또 한장 설정") },
        text = { Column(Modifier.verticalScroll(rememberScrollState())) {
            Text("하루 무료 1게임 + 광고 보상 최대 4게임\n\n한국 시간 자정에 번호와 횟수가 초기화돼요. 번호는 이 기기에만 저장돼요." +
            (if (BuildConfig.DEBUG) "\n\n개발 버전 · Google 테스트 광고 사용" else "") +
            "\n\n공식 복권 서비스와 무관한 독립 번호 추천 앱이에요.")
            TextButton(onClick = { privacyPolicy = true }) { Text("개인정보처리방침") }
            if (adPrivacy.optionsRequired) {
                TextButton(onClick = adPrivacy::showOptions, enabled = !adPrivacy.busy && !state.busy) {
                    Text("광고 개인정보 선택 변경")
                }
            }
            if (!adPrivacy.canRequestAds) {
                TextButton(onClick = adPrivacy::refresh, enabled = !adPrivacy.busy) { Text("광고 설정 다시 확인") }
            }
        } },
        confirmButton = { TextButton(onClick = { settings = false }) { Text("닫기") } })
    if (privacyPolicy) AlertDialog(onDismissRequest = { privacyPolicy = false },
        title = { Text("개인정보처리방침") },
        text = { Text(PrivacyPolicy.text, Modifier.heightIn(max = 440.dp).verticalScroll(rememberScrollState())) },
        confirmButton = { TextButton(onClick = { privacyPolicy = false }) { Text("닫기") } })
}

private fun ballColor(number: Int?): Color = when (number) {
    null -> Background
    in 1..10 -> Color(0xFFF4C76A)
    in 11..20 -> Color(0xFF6DAED5)
    in 21..30 -> Color(0xFFE88D85)
    in 31..40 -> Color(0xFFA5AEA8)
    else -> Soft
}
