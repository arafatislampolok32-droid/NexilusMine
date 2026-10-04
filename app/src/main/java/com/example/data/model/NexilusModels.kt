package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.text.NumberFormat
import java.util.Locale
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

object NexilusConstants {
    const val APP_NAME = "Nexilus Mine"
    const val COIN_NAME = "Nexilus Coins"
    const val COIN_SYMBOL = "NXC"
    const val BOT_USERNAME = "NexilusMineBot"
    const val PAYMENT_CHANNEL_USERNAME = "@NexilusPayLogs"

    // Exact Currency & Reward Constants
    const val COINS_PER_BDT = 250.0           // 250 Nexilus Coins = ৳1 BDT
    const val COINS_PER_CENT_USDT = 333.0     // 333 Nexilus Coins = $0.01 USDT
    const val MIN_WITHDRAWAL = 1000L
    const val JOINING_BONUS = 100L
    const val REFERRAL_BONUS = 100L
    const val MINING_DURATION_SEC = 7200L     // 2 hours = 7200 seconds
    const val MINING_REWARD = 80L
    const val AD_REWARD = 15L
    const val DAILY_AD_LIMIT = 10

    // 30-day cycle: day 1 = 30 coins, day 2 = 29, ..., day 30 = 1
    val DAILY_BONUS_SCHEDULE: List<Long> = (1..30).map { day -> (31 - day).toLong() }

    val WITHDRAWAL_TIERS = listOf(1000L, 2000L, 5000L, 10000L)

    val BINANCE_UID_REGEX = Regex("^\\d{6,20}$")
    val BKASH_NUMBER_REGEX = Regex("^01[3-9]\\d{8}$")

    // Default Notification Templates
    const val TEMPLATE_WELCOME = "🎉 Welcome to Nexilus Mine, {username}! Start mining to earn Nexilus Coins."
    const val TEMPLATE_REFERRAL_NEW = "🎉 {referredUsername} joined using your referral link! You got +100 Nexilus Coins 🪙"
    const val TEMPLATE_MINING_READY = "⛏ Your mining cycle is ready! Claim now to earn {coins} Nexilus Coins."
    const val TEMPLATE_WITHDRAWAL_SUBMITTED = "✅ Withdrawal Request Submitted!\n🪙 Amount: {coins} Nexilus Coins\n💰 {fiat}\n⛓ Method: {method}"
    const val TEMPLATE_WITHDRAWAL_APPROVED = "✅ Withdrawal Approved!\n🪙 Amount: {coins} Nexilus Coins\n💰 {fiat}\n⛓ Wallet: {method}\n\n🔗 View Transaction"
    const val TEMPLATE_WITHDRAWAL_REJECTED = "❌ Withdrawal Rejected.\n🪙 Amount: {coins} Nexilus Coins\nReason: {reason}\nCoins refunded to your balance."
    const val TEMPLATE_ADMIN_OFFLINE = "📢 System Notice: Admin Offline\n\n💤 Administrative staff are currently away.\n📥 You may continue to queue your withdrawal requests.\n🛡 All pending withdrawals will be cleared in approximately 9 hours. 🤘"
}

data class MiningLevelSpec(
    val level: Int,
    val titleEn: String,
    val titleBn: String,
    val multiplier: Double,
    val minMinesRequired: Int,
    val nextLevelMines: Int
)

val MINING_LEVELS = listOf(
    MiningLevelSpec(1, "Starter", "স্টার্টার", 1.0, 0, 5),
    MiningLevelSpec(2, "Developed", "ডেভেলপড", 1.25, 5, 15),
    MiningLevelSpec(3, "Refined", "রিফাইন্ড", 1.5, 15, 30),
    MiningLevelSpec(4, "Advanced", "অ্যাডভান্সড", 2.0, 30, 60)
)

data class ReferralMilestoneSpec(
    val id: String,
    val requiredReferrals: Int,
    val rewardCoins: Long,
    val title: String
)

val REFERRAL_MILESTONES = listOf(
    ReferralMilestoneSpec("m_5", 5, 250L, "Network Pioneer"),
    ReferralMilestoneSpec("m_10", 10, 500L, "Node Builder"),
    ReferralMilestoneSpec("m_25", 25, 1500L, "Cluster Commander"),
    ReferralMilestoneSpec("m_50", 50, 3500L, "Nexilus Ambassador")
)

object NexilusCurrency {
    fun coinsToBDT(coins: Long, coinsPerBdt: Double = NexilusConstants.COINS_PER_BDT): Double {
        return coins.toDouble() / coinsPerBdt
    }

    fun coinsToUSDT(coins: Long, coinsPerCentUsdt: Double = NexilusConstants.COINS_PER_CENT_USDT): Double {
        return (coins.toDouble() / coinsPerCentUsdt) * 0.01
    }

    fun formatBDT(coins: Long, coinsPerBdt: Double = NexilusConstants.COINS_PER_BDT): String {
        val bdt = coinsToBDT(coins, coinsPerBdt)
        return String.format(Locale.US, "৳%.2f BDT", bdt)
    }

    fun formatUSDT(coins: Long, coinsPerCentUsdt: Double = NexilusConstants.COINS_PER_CENT_USDT): String {
        val usdt = coinsToUSDT(coins, coinsPerCentUsdt)
        return String.format(Locale.US, "$%.2f USDT", usdt)
    }

    fun formatCoins(coins: Long): String {
        return NumberFormat.getIntegerInstance(Locale.US).format(coins)
    }

    fun formatCountdown(seconds: Long): String {
        val clamped = seconds.coerceAtLeast(0L)
        val hrs = clamped / 3600
        val mins = (clamped % 3600) / 60
        val secs = clamped % 60
        return String.format(Locale.US, "%02d:%02d:%02d", hrs, mins, secs)
    }
}

object NexilusCrypto {
    fun hmacSha256Bytes(key: ByteArray, data: String): ByteArray {
        val mac = Mac.getInstance("HmacSHA256")
        mac.init(SecretKeySpec(key, "HmacSHA256"))
        return mac.doFinal(data.toByteArray(Charsets.UTF_8))
    }

    fun bytesToHex(bytes: ByteArray): String =
        bytes.joinToString("") { "%02x".format(it) }

    /**
     * Exact Telegram Mini App initData HMAC-SHA256 verification:
     * secret_key = HMAC_SHA256("WebAppData", BOT_TOKEN)
     * computed_hash = HMAC_SHA256(secret_key, sorted key=value lines)
     */
    fun verifyInitDataHmac(initDataRaw: String, botToken: String): Boolean {
        if (initDataRaw.isBlank() || botToken.isBlank()) return false
        val pairs = initDataRaw.split("&").mapNotNull { part ->
            val idx = part.indexOf('=')
            if (idx <= 0) null else part.substring(0, idx) to java.net.URLDecoder.decode(part.substring(idx + 1), "UTF-8")
        }.toMap()
        val receivedHash = pairs["hash"] ?: return false
        val dataCheckString = pairs.entries
            .filter { it.key != "hash" }
            .sortedBy { it.key }
            .joinToString("\n") { "${it.key}=${it.value}" }
        val secretKey = hmacSha256Bytes("WebAppData".toByteArray(Charsets.UTF_8), botToken)
        val computedHash = bytesToHex(hmacSha256Bytes(secretKey, dataCheckString))
        return computedHash.equals(receivedHash, ignoreCase = true)
    }

    fun signCycleToken(cycleId: String, uid: String, secret: String): String {
        val sig = bytesToHex(hmacSha256Bytes(secret.toByteArray(Charsets.UTF_8), "$cycleId:$uid")).take(24)
        return "$cycleId.$sig"
    }

    fun verifyCycleToken(token: String, cycleId: String, uid: String, secret: String): Boolean {
        val expected = signCycleToken(cycleId, uid, secret)
        return token == expected
    }

    fun toBase36ReferralCode(uid: String): String {
        val num = uid.filter { it.isDigit() }.toLongOrNull() ?: uid.hashCode().toLong().and(0x7fffffffL)
        return "NX" + java.lang.Long.toString(num, 36).uppercase(Locale.US)
    }
}

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val uid: String,
    val username: String,
    val firstName: String,
    val lastName: String = "",
    val photoUrl: String = "",
    val languageCode: String = "en",
    val joinedAt: Long = System.currentTimeMillis(),
    val referralCode: String,
    val referredBy: String? = null,
    val status: String = "ACTIVE", // ACTIVE, SUSPICIOUS, BLOCKED
    val balance: Long = NexilusConstants.JOINING_BONUS,
    val totalEarned: Long = NexilusConstants.JOINING_BONUS,
    val level: Int = 1,
    // Mining state
    val miningStatus: String = "IDLE", // IDLE, ACTIVE, READY
    val miningStartedAt: Long = 0L,
    val miningEndsAt: Long = 0L,
    val miningCycleId: String = "",
    val totalMines: Int = 0,
    // Daily Bonus state
    val dailyStreak: Int = 0,
    val dailyLastClaimDay: String = "",
    val dailyLastClaimAt: Long = 0L,
    val dailyTotalEarned: Long = 0L,
    // Ads state
    val adsTodayCount: Int = 0,
    val adsTodayDate: String = "",
    val adsTotalEarned: Long = 0L,
    val usedAdProofsCsv: String = "",
    // Referral state
    val referralCount: Int = 0,
    val referralEarnings: Long = 0L,
    val claimedMilestonesCsv: String = "",
    // Missions state
    val completedMissionsCsv: String = "",
    // Anti-Fraud telemetry
    val fraudScore: Int = 0,
    val deviceHash: String = "nx_dev_primary_89f2a",
    val lastIp: String = "103.112.204.18"
) {
    fun getLevelSpec(): MiningLevelSpec {
        return MINING_LEVELS.lastOrNull { totalMines >= it.minMinesRequired } ?: MINING_LEVELS.first()
    }

    fun hasCompletedDailyTaskToday(todayDateStr: String): Boolean {
        val watchedAdToday = adsTodayDate == todayDateStr && adsTodayCount > 0
        val minedToday = miningStatus == "ACTIVE" || miningStatus == "READY" || totalMines > 0
        return watchedAdToday || minedToday
    }

    fun isMilestoneClaimed(milestoneId: String): Boolean =
        claimedMilestonesCsv.split(",").filter { it.isNotBlank() }.contains(milestoneId)

    fun isMissionCompleted(missionId: String): Boolean =
        completedMissionsCsv.split(",").filter { it.isNotBlank() }.contains(missionId)
}

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey val txId: String,
    val userId: String,
    val type: String, // JOINING_BONUS, REFERRAL_BONUS, MINING_REWARD, AD_REWARD, DAILY_BONUS, MISSION_REWARD, MILESTONE_REWARD, WITHDRAWAL_HOLD, WITHDRAWAL_REFUND, ADMIN_ADJUSTMENT
    val amount: Long,
    val meta: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "withdrawals")
data class WithdrawalEntity(
    @PrimaryKey val wdId: String,
    val userId: String,
    val userName: String,
    val userUsername: String,
    val coins: Long,
    val method: String, // BINANCE, BKASH
    val destination: String,
    val fiatEstimate: String,
    val status: String, // PENDING, PROCESSING, APPROVED, REJECTED
    val txId: String = "",
    val channelDeepLink: String = "",
    val adminNote: String = "",
    val processedBy: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "missions")
data class MissionEntity(
    @PrimaryKey val missionId: String,
    val title: String,
    val description: String,
    val reward: Long,
    val type: String, // AD, TELEGRAM
    val dailyLimit: Int = 1,
    val status: String = "ACTIVE", // ACTIVE, EXPIRED
    val channelUsername: String = "",
    val channelId: String = ""
)

@Entity(tableName = "audit_logs")
data class AuditLogEntity(
    @PrimaryKey val logId: String,
    val adminId: String,
    val action: String,
    val target: String,
    val oldValue: String,
    val newValue: String,
    val reason: String,
    val ip: String = "103.112.204.18",
    val ts: Long = System.currentTimeMillis()
)

@Entity(tableName = "channel_posts")
data class ChannelPostEntity(
    @PrimaryKey val postId: String,
    val channelUsername: String = "@NexilusPayLogs",
    val messageId: Int,
    val text: String,
    val deepLink: String,
    val recipientUid: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "app_settings")
data class AppSettingsEntity(
    @PrimaryKey val id: Int = 1,
    // Rewards
    val joiningBonus: Long = NexilusConstants.JOINING_BONUS,
    val referralBonus: Long = NexilusConstants.REFERRAL_BONUS,
    val miningReward: Long = NexilusConstants.MINING_REWARD,
    val adReward: Long = NexilusConstants.AD_REWARD,
    // Mining
    val miningDurationSec: Long = NexilusConstants.MINING_DURATION_SEC,
    // Ads
    val adsRewardedId: String = "block-4821",
    val adsInterstitial1: String = "block-4822",
    val adsInterstitial2: String = "block-4823",
    val adsInterstitial3: String = "block-4824",
    val adsDailyLimit: Int = NexilusConstants.DAILY_AD_LIMIT,
    val adsEnabled: Boolean = true,
    // Currency
    val coinsPerBDT: Double = NexilusConstants.COINS_PER_BDT,
    val coinsPerCentUSDT: Double = NexilusConstants.COINS_PER_CENT_USDT,
    val minWithdrawal: Long = NexilusConstants.MIN_WITHDRAWAL,
    // Bot & Channel
    val botUsername: String = NexilusConstants.BOT_USERNAME,
    val botToken: String = "",
    val miniAppUrl: String = "https://nexilus-mine.vercel.app",
    val paymentChannelId: String = "@NexilusPayLogs",
    val paymentChannelUsername: String = "NexilusPayLogs",
    // Templates
    val tplWelcome: String = NexilusConstants.TEMPLATE_WELCOME,
    val tplReferralNew: String = NexilusConstants.TEMPLATE_REFERRAL_NEW,
    val tplMiningReady: String = NexilusConstants.TEMPLATE_MINING_READY,
    val tplWithdrawalSubmitted: String = NexilusConstants.TEMPLATE_WITHDRAWAL_SUBMITTED,
    val tplWithdrawalApproved: String = NexilusConstants.TEMPLATE_WITHDRAWAL_APPROVED,
    val tplWithdrawalRejected: String = NexilusConstants.TEMPLATE_WITHDRAWAL_REJECTED,
    val tplAdminOffline: String = NexilusConstants.TEMPLATE_ADMIN_OFFLINE,
    // System & Fraud
    val maintenanceMode: Boolean = false,
    val announcement: String = "Nexilus Mainnet Phase 2 Mining Live — Instant bKash & Binance UID Payouts Active!",
    val dailyResetHourUtc: Int = 0,
    val leaderboardRefreshHours: Int = 1,
    val ipClusterThreshold: Int = 3,
    val rewardVelocityLimit: Int = 20,
    val selfReferralWindowMin: Int = 10,
    // Branding
    val coinName: String = NexilusConstants.COIN_NAME,
    val coinSymbol: String = NexilusConstants.COIN_SYMBOL
)
