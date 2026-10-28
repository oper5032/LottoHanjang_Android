package com.lotto.hanjang

import org.junit.Assert.*
import org.junit.Test
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset

class LottoRulesTest {
    @Test fun generatedGamesHaveSixUniqueSortedNumbersWithinRange() {
        repeat(1000) {
            val numbers = LottoRules.generate()
            assertEquals(6, numbers.size)
            assertEquals(6, numbers.distinct().size)
            assertTrue(numbers.all { it in 1..45 })
            assertEquals(numbers.sorted(), numbers)
        }
    }
    @Test fun koreaMidnightIsIndependentOfDeviceZone() {
        fun date(instant: String) = LottoRules.today(Clock.fixed(Instant.parse(instant), ZoneOffset.ofHours(-7)))
        assertEquals("2026-10-28", date("2026-10-28T14:59:59Z"))
        assertEquals("2026-10-29", date("2026-10-28T15:00:00Z"))
    }
    @Test fun onlyFirstGameIsFreeAndFiveIsTheLimit() {
        assertTrue(LottoRules.canGrant(0, false))
        for (count in 1..4) {
            assertFalse(LottoRules.canGrant(count, false))
            assertTrue(LottoRules.canGrant(count, true))
        }
        assertFalse(LottoRules.canGrant(5, true))
        assertFalse(LottoRules.canGrant(5, false))
    }
}
