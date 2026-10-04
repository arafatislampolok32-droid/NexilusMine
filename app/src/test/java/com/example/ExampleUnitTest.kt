package com.example

import com.example.data.model.NexilusConstants
import com.example.data.model.NexilusCrypto
import com.example.data.model.NexilusCurrency
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun currencyConversions_areExact() {
        // 250 Nexilus Coins = ৳1 BDT
        assertEquals(1.0, NexilusCurrency.coinsToBDT(250L), 0.0001)
        assertEquals(4.0, NexilusCurrency.coinsToBDT(1000L), 0.0001)

        // 333 Nexilus Coins = $0.01 USDT
        assertEquals(0.01, NexilusCurrency.coinsToUSDT(333L), 0.0001)
        assertEquals(0.03, NexilusCurrency.coinsToUSDT(999L), 0.0001)
    }

    @Test
    fun dailyBonusSchedule_is30DayReverse() {
        assertEquals(30, NexilusConstants.DAILY_BONUS_SCHEDULE.size)
        assertEquals(30L, NexilusConstants.DAILY_BONUS_SCHEDULE.first()) // Day 1 = 30
        assertEquals(29L, NexilusConstants.DAILY_BONUS_SCHEDULE[1])     // Day 2 = 29
        assertEquals(1L, NexilusConstants.DAILY_BONUS_SCHEDULE.last())  // Day 30 = 1
    }

    @Test
    fun withdrawalRegexAndCycleToken_areValid() {
        assertTrue(NexilusConstants.BINANCE_UID_REGEX.matches("482910492"))
        assertFalse(NexilusConstants.BINANCE_UID_REGEX.matches("123"))

        assertTrue(NexilusConstants.BKASH_NUMBER_REGEX.matches("01712345678"))
        assertFalse(NexilusConstants.BKASH_NUMBER_REGEX.matches("01112345678"))

        val token = NexilusCrypto.signCycleToken("cyc_123", "100001", "secret_key")
        assertTrue(NexilusCrypto.verifyCycleToken(token, "cyc_123", "100001", "secret_key"))
        assertFalse(NexilusCrypto.verifyCycleToken(token, "cyc_999", "100001", "secret_key"))
    }
}
