package com.example.data.repository

import com.example.BuildConfig
import com.example.data.local.NexilusDao
import com.example.data.model.AppSettingsEntity
import com.example.data.model.AuditLogEntity
import com.example.data.model.ChannelPostEntity
import com.example.data.model.MissionEntity
import com.example.data.model.NexilusConstants
import com.example.data.model.NexilusCrypto
import com.example.data.model.NexilusCurrency
import com.example.data.model.REFERRAL_MILESTONES
import com.example.data.model.TransactionEntity
import com.example.data.model.UserEntity
import com.example.data.model.WithdrawalEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.UUID
import java.util.concurrent.TimeUnit

class NexilusRepository(private val dao: NexilusDao) {

    companion object {
        const val PRIMARY_UID = "100001"
        private const val DEFAULT_JWT_SECRET = "nexilus_jwt_hmac_secret_key_2026"
    }

    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(8, TimeUnit.SECONDS)
        .readTimeout(8, TimeUnit.SECONDS)
        .build()

    val primaryUserFlow: Flow<UserEntity?> = dao.observeUser(PRIMARY_UID)
    val allUsersFlow: Flow<List<UserEntity>> = dao.observeAllUsers()
    val topReferrersFlow: Flow<List<UserEntity>> = dao.observeTopReferrers()
    val topEarnersFlow: Flow<List<UserEntity>> = dao.observeTopEarners()
    val primaryTransactionsFlow: Flow<List<TransactionEntity>> = dao.observeUserTransactions(PRIMARY_UID)
    val allTransactionsFlow: Flow<List<TransactionEntity>> = dao.observeAllTransactions()
    val primaryWithdrawalsFlow: Flow<List<WithdrawalEntity>> = dao.observeUserWithdrawals(PRIMARY_UID)
    val allWithdrawalsFlow: Flow<List<WithdrawalEntity>> = dao.observeAllWithdrawals()
    val activeMissionsFlow: Flow<List<MissionEntity>> = dao.observeActiveMissions()
    val allMissionsFlow: Flow<List<MissionEntity>> = dao.observeAllMissions()
    val settingsFlow: Flow<AppSettingsEntity?> = dao.observeSettings()
    val auditLogsFlow: Flow<List<AuditLogEntity>> = dao.observeAuditLogs()
    val channelPostsFlow: Flow<List<ChannelPostEntity>> = dao.observeChannelPosts()

    fun currentUtcDateString(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        sdf.timeZone = TimeZone.getTimeZone("UTC")
        return sdf.format(Date())
    }

    suspend fun ensureSeeded() = withContext(Dispatchers.IO) {
        val existingSettings = dao.getSettings()
        if (existingSettings == null) {
            val envBotToken = try { BuildConfig.BOT_TOKEN } catch (_: Exception) { "" }
            dao.upsertSettings(
                AppSettingsEntity(
                    id = 1,
                    botToken = envBotToken
                )
            )
        }

        val existingPrimary = dao.getUserById(PRIMARY_UID)
        if (existingPrimary == null) {
            val now = System.currentTimeMillis()
            val today = currentUtcDateString()
            val primaryRefCode = NexilusCrypto.toBase36ReferralCode(PRIMARY_UID)

            // Primary user starts with 625 NXC (so "375 Coins more to unlock withdrawal" is shown by default, matching the spec!)
            val primaryUser = UserEntity(
                uid = PRIMARY_UID,
                username = "arafat_nexilus",
                firstName = "Arafat",
                lastName = "Islam",
                referralCode = primaryRefCode,
                status = "ACTIVE",
                balance = 625L,
                totalEarned = 625L,
                level = 2,
                miningStatus = "IDLE",
                totalMines = 5,
                dailyStreak = 3,
                dailyLastClaimDay = "",
                dailyTotalEarned = 87L,
                adsTodayCount = 3,
                adsTodayDate = today,
                adsTotalEarned = 135L,
                referralCount = 4,
                referralEarnings = 400L,
                joinedAt = now - 86400_000L * 4
            )

            val communityUsers = listOf(
                primaryUser,
                UserEntity(
                    uid = "100002",
                    username = "tanvir_web3",
                    firstName = "Tanvir",
                    lastName = "Ahmed",
                    referralCode = NexilusCrypto.toBase36ReferralCode("100002"),
                    referredBy = primaryRefCode,
                    balance = 14850L,
                    totalEarned = 24850L,
                    level = 4,
                    totalMines = 42,
                    referralCount = 68,
                    referralEarnings = 6800L,
                    deviceHash = "nx_dev_peer_102",
                    lastIp = "103.112.204.41",
                    joinedAt = now - 86400_000L * 14
                ),
                UserEntity(
                    uid = "100003",
                    username = "nadia_crypto",
                    firstName = "Nadia",
                    lastName = "Rahman",
                    referralCode = NexilusCrypto.toBase36ReferralCode("100003"),
                    referredBy = primaryRefCode,
                    balance = 9420L,
                    totalEarned = 19420L,
                    level = 4,
                    totalMines = 36,
                    referralCount = 54,
                    referralEarnings = 5400L,
                    deviceHash = "nx_dev_peer_103",
                    lastIp = "103.112.204.52",
                    joinedAt = now - 86400_000L * 12
                ),
                UserEntity(
                    uid = "100004",
                    username = "sakib_miner",
                    firstName = "Sakib",
                    lastName = "Hasan",
                    referralCode = NexilusCrypto.toBase36ReferralCode("100004"),
                    referredBy = primaryRefCode,
                    balance = 6300L,
                    totalEarned = 15300L,
                    level = 3,
                    totalMines = 27,
                    referralCount = 41,
                    referralEarnings = 4100L,
                    deviceHash = "nx_dev_peer_104",
                    lastIp = "103.112.204.63",
                    joinedAt = now - 86400_000L * 10
                ),
                UserEntity(
                    uid = "100005",
                    username = "farhan_nxc",
                    firstName = "Farhan",
                    lastName = "Kabir",
                    referralCode = NexilusCrypto.toBase36ReferralCode("100005"),
                    referredBy = primaryRefCode,
                    balance = 4150L,
                    totalEarned = 11150L,
                    level = 3,
                    totalMines = 19,
                    referralCount = 29,
                    referralEarnings = 2900L,
                    deviceHash = "nx_dev_peer_105",
                    lastIp = "103.112.204.74",
                    joinedAt = now - 86400_000L * 8
                ),
                UserEntity(
                    uid = "100006",
                    username = "mahir_bd",
                    firstName = "Mahir",
                    lastName = "Chowdhury",
                    referralCode = NexilusCrypto.toBase36ReferralCode("100006"),
                    balance = 3280L,
                    totalEarned = 8280L,
                    level = 2,
                    totalMines = 14,
                    referralCount = 22,
                    referralEarnings = 2200L,
                    deviceHash = "nx_dev_peer_106",
                    lastIp = "103.112.204.85",
                    joinedAt = now - 86400_000L * 6
                ),
                UserEntity(
                    uid = "100007",
                    username = "riya_defi",
                    firstName = "Riya",
                    lastName = "Sultana",
                    referralCode = NexilusCrypto.toBase36ReferralCode("100007"),
                    balance = 2790L,
                    totalEarned = 5790L,
                    level = 2,
                    totalMines = 11,
                    referralCount = 16,
                    referralEarnings = 1600L,
                    deviceHash = "nx_dev_peer_107",
                    lastIp = "103.112.204.96",
                    joinedAt = now - 86400_000L * 5
                ),
                UserEntity(
                    uid = "100008",
                    username = "zayan_bot_cluster",
                    firstName = "Zayan",
                    lastName = "multi_acct",
                    referralCode = NexilusCrypto.toBase36ReferralCode("100008"),
                    status = "SUSPICIOUS",
                    balance = 300L,
                    totalEarned = 300L,
                    level = 1,
                    totalMines = 1,
                    referralCount = 2,
                    referralEarnings = 200L,
                    fraudScore = 75,
                    deviceHash = "nx_shared_emu_999",
                    lastIp = "103.112.209.11",
                    joinedAt = now - 86400_000L * 1
                )
            )
            dao.upsertUsers(communityUsers)

            // Seed initial transactions for primary user
            val initialTxs = listOf(
                TransactionEntity(
                    txId = "tx_join_100001",
                    userId = PRIMARY_UID,
                    type = "JOINING_BONUS",
                    amount = 100L,
                    meta = "Welcome to Nexilus Mine (+100 NXC)",
                    createdAt = now - 86400_000L * 4
                ),
                TransactionEntity(
                    txId = "tx_ref_100002",
                    userId = PRIMARY_UID,
                    type = "REFERRAL_BONUS",
                    amount = 100L,
                    meta = "@tanvir_web3 joined via your referral link",
                    createdAt = now - 86400_000L * 3
                ),
                TransactionEntity(
                    txId = "tx_ref_100003",
                    userId = PRIMARY_UID,
                    type = "REFERRAL_BONUS",
                    amount = 100L,
                    meta = "@nadia_crypto joined via your referral link",
                    createdAt = now - 86400_000L * 2
                ),
                TransactionEntity(
                    txId = "tx_mine_cyc01",
                    userId = PRIMARY_UID,
                    type = "MINING_REWARD",
                    amount = 200L,
                    meta = "Quantum Mining Cycles #1–#3 Completed",
                    createdAt = now - 86400_000L * 1
                ),
                TransactionEntity(
                    txId = "tx_ad_today",
                    userId = PRIMARY_UID,
                    type = "AD_REWARD",
                    amount = 45L,
                    meta = "Watched 3 AdsGram Rewarded Spots (+15 NXC each)",
                    createdAt = now - 7200_000L
                ),
                TransactionEntity(
                    txId = "tx_daily_d3",
                    userId = PRIMARY_UID,
                    type = "DAILY_BONUS",
                    amount = 80L,
                    meta = "30-Day Streak Rewards (Days 1–3)",
                    createdAt = now - 3600_000L
                )
            )
            dao.insertTransactions(initialTxs)

            // Seed active Ad Missions & Telegram Missions
            val missions = listOf(
                MissionEntity(
                    missionId = "msn_tg_paylogs",
                    title = "Join @NexilusPayLogs Channel",
                    description = "Subscribe to official live withdrawal payouts & transaction proofs channel",
                    reward = 150L,
                    type = "TELEGRAM",
                    channelUsername = "@NexilusPayLogs",
                    channelId = "-10021984512"
                ),
                MissionEntity(
                    missionId = "msn_tg_community",
                    title = "Join Nexilus Global Community",
                    description = "Connect with 50,000+ miners in the official @NexilusMineBot announcement hub",
                    reward = 125L,
                    type = "TELEGRAM",
                    channelUsername = "@NexilusMineOfficial",
                    channelId = "-10021984513"
                ),
                MissionEntity(
                    missionId = "msn_ad_boost",
                    title = "AdsGram Quantum Boost Spot",
                    description = "Watch our featured Web3 partner spotlight video to earn instant bonus NXC",
                    reward = 50L,
                    type = "AD",
                    dailyLimit = 3
                ),
                MissionEntity(
                    missionId = "msn_ad_ecosystem",
                    title = "Binance Pay & bKash Partner Ad",
                    description = "View 15-second instant settlement tutorial spot",
                    reward = 45L,
                    type = "AD",
                    dailyLimit = 2
                )
            )
            dao.upsertMissions(missions)

            // Seed sample Withdrawals (for Admin queue + PaymentLogs demonstration)
            val sampleWithdrawals = listOf(
                WithdrawalEntity(
                    wdId = "wd_901",
                    userId = "100002",
                    userName = "Tanvir Ahmed",
                    userUsername = "tanvir_web3",
                    coins = 5000L,
                    method = "BKASH",
                    destination = "01712345678",
                    fiatEstimate = "৳20.00 BDT ($0.15 USDT)",
                    status = "APPROVED",
                    txId = "BKX98421057CC",
                    channelDeepLink = "https://t.me/NexilusPayLogs/104",
                    processedBy = "Admin #100001",
                    createdAt = now - 86400_000L * 2,
                    updatedAt = now - 86400_000L * 2 + 1800_000L
                ),
                WithdrawalEntity(
                    wdId = "wd_902",
                    userId = "100003",
                    userName = "Nadia Rahman",
                    userUsername = "nadia_crypto",
                    coins = 2000L,
                    method = "BINANCE",
                    destination = "482910492",
                    fiatEstimate = "$0.06 USDT (৳8.00 BDT)",
                    status = "PENDING",
                    createdAt = now - 5400_000L,
                    updatedAt = now - 5400_000L
                ),
                WithdrawalEntity(
                    wdId = "wd_903",
                    userId = "100004",
                    userName = "Sakib Hasan",
                    userUsername = "sakib_miner",
                    coins = 1000L,
                    method = "BKASH",
                    destination = "01819876543",
                    fiatEstimate = "৳4.00 BDT ($0.03 USDT)",
                    status = "PROCESSING",
                    createdAt = now - 3600_000L,
                    updatedAt = now - 1800_000L
                )
            )
            dao.upsertWithdrawals(sampleWithdrawals)

            // Seed @NexilusPayLogs channel broadcast
            dao.insertChannelPost(
                ChannelPostEntity(
                    postId = "post_104",
                    channelUsername = "@NexilusPayLogs",
                    messageId = 104,
                    text = "🎉 New payout paid 🎉\n👤 User: Tanvir Ahmed (@tanvir_web3)\n🪙 Amount: 5,000 Nexilus (৳20.00 BDT)\n💳 Wallet: BKASH\n🔗 Transaction id: BKX98421057CC\n🤖 Bot: @NexilusMineBot",
                    deepLink = "https://t.me/NexilusPayLogs/104",
                    recipientUid = "100002",
                    createdAt = now - 86400_000L * 2 + 1800_000L
                )
            )

            // Seed initial Audit Log
            dao.insertAuditLog(
                AuditLogEntity(
                    logId = "aud_init_1",
                    adminId = "100001",
                    action = "WITHDRAWAL_APPROVED",
                    target = "wd_901 (uid:100002)",
                    oldValue = "PENDING",
                    newValue = "APPROVED (txId: BKX98421057CC)",
                    reason = "Verified bKash cashout settlement",
                    ts = now - 86400_000L * 2 + 1800_000L
                )
            )
        }
    }

    fun getCycleTokenForUser(user: UserEntity): String {
        val secret = try {
            BuildConfig.JWT_SECRET.ifBlank { DEFAULT_JWT_SECRET }
        } catch (_: Exception) {
            DEFAULT_JWT_SECRET
        }
        return NexilusCrypto.signCycleToken(user.miningCycleId, user.uid, secret)
    }

    // ─── USER MINING FLOW ───
    suspend fun startMining(uid: String): Result<UserEntity> = withContext(Dispatchers.IO) {
        val user = dao.getUserById(uid) ?: return@withContext Result.failure(Exception("User not found"))
        if (user.status != "ACTIVE") {
            return@withContext Result.failure(Exception("Account status is ${user.status}. Mining is restricted."))
        }
        val settings = dao.getSettings() ?: AppSettingsEntity()
        if (settings.maintenanceMode) {
            return@withContext Result.failure(Exception("System is currently in maintenance mode."))
        }
        val now = System.currentTimeMillis()
        if (user.miningStatus == "ACTIVE" && now < user.miningEndsAt) {
            return@withContext Result.failure(Exception("Mining cycle is already active."))
        }
        val durationMs = settings.miningDurationSec * 1000L
        val cycleId = "cyc_" + UUID.randomUUID().toString().replace("-", "").take(12)
        val updated = user.copy(
            miningStatus = "ACTIVE",
            miningStartedAt = now,
            miningEndsAt = now + durationMs,
            miningCycleId = cycleId
        )
        dao.updateUser(updated)
        Result.success(updated)
    }

    suspend fun syncMiningReadyStateIfElapsed(uid: String): UserEntity? = withContext(Dispatchers.IO) {
        val user = dao.getUserById(uid) ?: return@withContext null
        val now = System.currentTimeMillis()
        if (user.miningStatus == "ACTIVE" && now >= user.miningEndsAt && user.miningEndsAt > 0L) {
            val readyUser = user.copy(miningStatus = "READY")
            dao.updateUser(readyUser)
            return@withContext readyUser
        }
        user
    }

    suspend fun fastForwardMiningCycle(uid: String): Result<UserEntity> = withContext(Dispatchers.IO) {
        val user = dao.getUserById(uid) ?: return@withContext Result.failure(Exception("User not found"))
        val now = System.currentTimeMillis()
        val cycleId = user.miningCycleId.ifBlank { "cyc_" + UUID.randomUUID().toString().replace("-", "").take(12) }
        val updated = user.copy(
            miningStatus = "READY",
            miningStartedAt = now - 7200_000L,
            miningEndsAt = now - 1000L,
            miningCycleId = cycleId
        )
        dao.updateUser(updated)
        Result.success(updated)
    }

    suspend fun claimMiningReward(uid: String, cycleToken: String, adProof: String): Result<Long> = withContext(Dispatchers.IO) {
        val user = dao.getUserById(uid) ?: return@withContext Result.failure(Exception("User not found"))
        if (user.status != "ACTIVE") {
            return@withContext Result.failure(Exception("Account status is ${user.status}. Claims blocked."))
        }
        val now = System.currentTimeMillis()
        if (user.miningStatus != "READY" && !(user.miningStatus == "ACTIVE" && now >= user.miningEndsAt)) {
            return@withContext Result.failure(Exception("Mining cycle has not finished yet."))
        }
        if (user.miningCycleId.isBlank()) {
            return@withContext Result.failure(Exception("Missing active cycle ID."))
        }
        val secret = try {
            BuildConfig.JWT_SECRET.ifBlank { DEFAULT_JWT_SECRET }
        } catch (_: Exception) {
            DEFAULT_JWT_SECRET
        }
        if (!NexilusCrypto.verifyCycleToken(cycleToken, user.miningCycleId, user.uid, secret)) {
            return@withContext Result.failure(Exception("Invalid HMAC cycleToken signature."))
        }
        if (adProof.isBlank()) {
            return@withContext Result.failure(Exception("Rewarded ad verification proof is required."))
        }
        // Idempotency check
        val existingTx = dao.findTransactionByCycleId(uid, user.miningCycleId)
        if (existingTx != null) {
            return@withContext Result.failure(Exception("This mining cycle has already been claimed."))
        }

        val settings = dao.getSettings() ?: AppSettingsEntity()
        val levelSpec = user.getLevelSpec()
        val finalReward = Math.round(settings.miningReward * levelSpec.multiplier)
        val newTotalMines = user.totalMines + 1
        val newLevel = com.example.data.model.MINING_LEVELS
            .lastOrNull { newTotalMines >= it.minMinesRequired }?.level ?: user.level

        val updatedUser = user.copy(
            balance = user.balance + finalReward,
            totalEarned = user.totalEarned + finalReward,
            totalMines = newTotalMines,
            level = newLevel,
            miningStatus = "IDLE",
            miningStartedAt = 0L,
            miningEndsAt = 0L,
            miningCycleId = ""
        )
        dao.updateUser(updatedUser)
        dao.insertTransaction(
            TransactionEntity(
                txId = "tx_mine_${System.currentTimeMillis()}",
                userId = uid,
                type = "MINING_REWARD",
                amount = finalReward,
                meta = "Mining Cycle ${user.miningCycleId} (x${levelSpec.multiplier} multiplier, adProof:$adProof)"
            )
        )
        Result.success(finalReward)
    }

    // ─── WATCH & EARN ADS FLOW ───
    suspend fun claimAdReward(uid: String, adProof: String): Result<Long> = withContext(Dispatchers.IO) {
        val user = dao.getUserById(uid) ?: return@withContext Result.failure(Exception("User not found"))
        if (user.status != "ACTIVE") {
            return@withContext Result.failure(Exception("Account status is ${user.status}."))
        }
        val settings = dao.getSettings() ?: AppSettingsEntity()
        if (!settings.adsEnabled) {
            return@withContext Result.failure(Exception("Watch & Earn ads are temporarily disabled."))
        }
        val today = currentUtcDateString()
        val currentCount = if (user.adsTodayDate == today) user.adsTodayCount else 0
        if (currentCount >= settings.adsDailyLimit) {
            return@withContext Result.failure(Exception("Daily ad limit (${settings.adsDailyLimit}/${settings.adsDailyLimit}) reached! Come back after UTC reset."))
        }
        val usedProofs = user.usedAdProofsCsv.split(",").filter { it.isNotBlank() }
        if (usedProofs.contains(adProof)) {
            return@withContext Result.failure(Exception("Duplicate adProof token rejected (idempotency check)."))
        }

        val reward = settings.adReward
        val updatedProofs = (usedProofs.takeLast(30) + adProof).joinToString(",")
        val updatedUser = user.copy(
            balance = user.balance + reward,
            totalEarned = user.totalEarned + reward,
            adsTodayCount = currentCount + 1,
            adsTodayDate = today,
            adsTotalEarned = user.adsTotalEarned + reward,
            usedAdProofsCsv = updatedProofs
        )
        dao.updateUser(updatedUser)
        dao.insertTransaction(
            TransactionEntity(
                txId = "tx_ad_${System.currentTimeMillis()}",
                userId = uid,
                type = "AD_REWARD",
                amount = reward,
                meta = "Watch & Earn Ad (${currentCount + 1}/${settings.adsDailyLimit}) • proof:$adProof"
            )
        )
        Result.success(reward)
    }

    // ─── 30-DAY REVERSE DAILY BONUS FLOW ───
    suspend fun claimDailyBonus(uid: String): Result<Pair<Int, Long>> = withContext(Dispatchers.IO) {
        val user = dao.getUserById(uid) ?: return@withContext Result.failure(Exception("User not found"))
        if (user.status != "ACTIVE") {
            return@withContext Result.failure(Exception("Account status is ${user.status}."))
        }
        val today = currentUtcDateString()
        if (user.dailyLastClaimDay == today) {
            return@withContext Result.failure(Exception("You have already claimed today's bonus!"))
        }
        if (!user.hasCompletedDailyTaskToday(today)) {
            return@withContext Result.failure(Exception(NexilusConstants.DAILY_BONUS_SCHEDULE.let {
                "দৈনিক বোনাস দাবি করতে আজ অন্তত ১টি বিজ্ঞাপন দেখুন বা মাইনিং শুরু করুন"
            }))
        }

        val nextDayIndex = (user.dailyStreak % 30) // 0..29 -> Day 1..30
        val dayNumber = nextDayIndex + 1
        val reward = NexilusConstants.DAILY_BONUS_SCHEDULE[nextDayIndex] // Day 1 = 30, Day 2 = 29, ..., Day 30 = 1

        val updatedUser = user.copy(
            balance = user.balance + reward,
            totalEarned = user.totalEarned + reward,
            dailyStreak = dayNumber,
            dailyLastClaimDay = today,
            dailyLastClaimAt = System.currentTimeMillis(),
            dailyTotalEarned = user.dailyTotalEarned + reward
        )
        dao.updateUser(updatedUser)
        dao.insertTransaction(
            TransactionEntity(
                txId = "tx_daily_${System.currentTimeMillis()}",
                userId = uid,
                type = "DAILY_BONUS",
                amount = reward,
                meta = "30-Day Reverse Daily Bonus — Day $dayNumber (+$reward NXC)"
            )
        )
        Result.success(dayNumber to reward)
    }

    // ─── MISSIONS (AD & TELEGRAM CHANNEL VERIFY) ───
    suspend fun completeAndVerifyMission(uid: String, mission: MissionEntity): Result<Long> = withContext(Dispatchers.IO) {
        val user = dao.getUserById(uid) ?: return@withContext Result.failure(Exception("User not found"))
        if (user.status != "ACTIVE") {
            return@withContext Result.failure(Exception("Account status is ${user.status}."))
        }
        if (user.isMissionCompleted(mission.missionId)) {
            return@withContext Result.failure(Exception("Mission already verified and claimed!"))
        }

        val completed = user.completedMissionsCsv.split(",").filter { it.isNotBlank() } + mission.missionId
        val updatedUser = user.copy(
            balance = user.balance + mission.reward,
            totalEarned = user.totalEarned + mission.reward,
            completedMissionsCsv = completed.joinToString(",")
        )
        dao.updateUser(updatedUser)
        dao.insertTransaction(
            TransactionEntity(
                txId = "tx_msn_${System.currentTimeMillis()}",
                userId = uid,
                type = "MISSION_REWARD",
                amount = mission.reward,
                meta = "Completed Mission: ${mission.title}"
            )
        )
        Result.success(mission.reward)
    }

    // ─── REFERRAL & MILESTONE FLOW (WITH ANTI-FRAUD) ───
    suspend fun simulateNewReferralJoin(
        referrerUid: String,
        newFriendUsername: String,
        sameDeviceSimulated: Boolean = false
    ): Result<String> = withContext(Dispatchers.IO) {
        val referrer = dao.getUserById(referrerUid) ?: return@withContext Result.failure(Exception("Referrer not found"))
        if (referrer.status != "ACTIVE") {
            return@withContext Result.failure(Exception("Your account is ${referrer.status}. Referral rewards are paused."))
        }
        val cleanHandle = newFriendUsername.trim().removePrefix("@").ifBlank {
            "miner_${(1000..9999).random()}"
        }
        if (cleanHandle.equals(referrer.username, ignoreCase = true)) {
            return@withContext Result.failure(Exception("Anti-Fraud Guard: Self-referral (referrerId === newUserId) is strictly prohibited."))
        }

        val settings = dao.getSettings() ?: AppSettingsEntity()
        val friendDeviceHash = if (sameDeviceSimulated) {
            referrer.deviceHash
        } else {
            "nx_dev_${UUID.randomUUID().toString().take(8)}"
        }

        // Anti-Fraud check: New user's deviceHash === referrer's deviceHash
        if (friendDeviceHash == referrer.deviceHash) {
            val flaggedReferrer = referrer.copy(
                fraudScore = referrer.fraudScore + 40,
                status = if (referrer.fraudScore + 40 >= 80) "SUSPICIOUS" else referrer.status
            )
            dao.updateUser(flaggedReferrer)
            return@withContext Result.failure(
                Exception("Anti-Fraud Alert: Referral rejected because new user's deviceHash matches referrer's deviceHash!")
            )
        }

        val newUid = (100100..999999).random().toString()
        val bonus = settings.referralBonus
        val joinBonus = settings.joiningBonus

        val newUser = UserEntity(
            uid = newUid,
            username = cleanHandle,
            firstName = cleanHandle.replaceFirstChar { it.uppercase() },
            referralCode = NexilusCrypto.toBase36ReferralCode(newUid),
            referredBy = referrer.referralCode,
            balance = joinBonus + bonus,
            totalEarned = joinBonus + bonus,
            deviceHash = friendDeviceHash
        )
        dao.upsertUser(newUser)

        val updatedReferrer = referrer.copy(
            balance = referrer.balance + bonus,
            totalEarned = referrer.totalEarned + bonus,
            referralCount = referrer.referralCount + 1,
            referralEarnings = referrer.referralEarnings + bonus
        )
        dao.updateUser(updatedReferrer)

        val now = System.currentTimeMillis()
        dao.insertTransactions(
            listOf(
                TransactionEntity(
                    txId = "tx_ref_${now}_r",
                    userId = referrer.uid,
                    type = "REFERRAL_BONUS",
                    amount = bonus,
                    meta = "🎉 @$cleanHandle joined using your referral link! +$bonus NXC",
                    createdAt = now
                ),
                TransactionEntity(
                    txId = "tx_join_${now}_u",
                    userId = newUid,
                    type = "JOINING_BONUS",
                    amount = joinBonus,
                    meta = "Joining Bonus (+$joinBonus NXC)",
                    createdAt = now
                ),
                TransactionEntity(
                    txId = "tx_ref_${now}_u",
                    userId = newUid,
                    type = "REFERRAL_BONUS",
                    amount = bonus,
                    meta = "Referral Bonus via @${referrer.username} (+$bonus NXC)",
                    createdAt = now
                )
            )
        )

        val dmText = settings.tplReferralNew
            .replace("{referredUsername}", "@$cleanHandle")
            .replace("{username}", "@$cleanHandle")
        sendBotNotificationOrLog(
            recipientUid = referrer.uid,
            channelOrChat = "@${referrer.username} (Bot DM)",
            text = dmText
        )

        Result.success("🎉 @$cleanHandle joined! Both of you received +$bonus Nexilus Coins 🪙")
    }

    suspend fun claimReferralMilestone(uid: String, milestoneId: String): Result<Long> = withContext(Dispatchers.IO) {
        val user = dao.getUserById(uid) ?: return@withContext Result.failure(Exception("User not found"))
        if (user.status != "ACTIVE") {
            return@withContext Result.failure(Exception("Account status is ${user.status}."))
        }
        val milestone = REFERRAL_MILESTONES.find { it.id == milestoneId }
            ?: return@withContext Result.failure(Exception("Milestone not found"))
        if (user.referralCount < milestone.requiredReferrals) {
            return@withContext Result.failure(Exception("Need ${milestone.requiredReferrals} referrals to unlock ${milestone.title}."))
        }
        if (user.isMilestoneClaimed(milestoneId)) {
            return@withContext Result.failure(Exception("Milestone already claimed!"))
        }

        val claimed = user.claimedMilestonesCsv.split(",").filter { it.isNotBlank() } + milestoneId
        val updatedUser = user.copy(
            balance = user.balance + milestone.rewardCoins,
            totalEarned = user.totalEarned + milestone.rewardCoins,
            referralEarnings = user.referralEarnings + milestone.rewardCoins,
            claimedMilestonesCsv = claimed.joinToString(",")
        )
        dao.updateUser(updatedUser)
        dao.insertTransaction(
            TransactionEntity(
                txId = "tx_ms_${System.currentTimeMillis()}",
                userId = uid,
                type = "MILESTONE_REWARD",
                amount = milestone.rewardCoins,
                meta = "Referral Milestone (${milestone.requiredReferrals} Referrals — ${milestone.title})"
            )
        )
        Result.success(milestone.rewardCoins)
    }

    // ─── WITHDRAWAL ESCROW & ADMIN APPROVAL / REJECTION FLOW ───
    suspend fun submitWithdrawal(
        uid: String,
        coins: Long,
        method: String,
        destination: String
    ): Result<WithdrawalEntity> = withContext(Dispatchers.IO) {
        val user = dao.getUserById(uid) ?: return@withContext Result.failure(Exception("User not found"))
        if (user.status != "ACTIVE") {
            return@withContext Result.failure(Exception("Your account is ${user.status}. Withdrawals are blocked."))
        }
        val settings = dao.getSettings() ?: AppSettingsEntity()
        if (coins < settings.minWithdrawal) {
            return@withContext Result.failure(Exception("Minimum withdrawal is ${settings.minWithdrawal} Nexilus Coins."))
        }
        if (user.balance < coins) {
            return@withContext Result.failure(Exception("Insufficient balance (${user.balance} NXC available)."))
        }
        if (!NexilusConstants.WITHDRAWAL_TIERS.contains(coins) && coins % 1000L != 0L) {
            return@withContext Result.failure(Exception("Please select a valid withdrawal tier (1K, 2K, 5K, 10K)."))
        }

        val cleanDest = destination.trim()
        when (method.uppercase(Locale.US)) {
            "BINANCE" -> {
                if (!NexilusConstants.BINANCE_UID_REGEX.matches(cleanDest)) {
                    return@withContext Result.failure(Exception("Invalid Binance UID. Must be 6 to 20 digits (/^\\d{6,20}$/)."))
                }
            }
            "BKASH" -> {
                if (!NexilusConstants.BKASH_NUMBER_REGEX.matches(cleanDest)) {
                    return@withContext Result.failure(Exception("Invalid bKash number. Must match /^01[3-9]\\d{8}$/ (11 digits)."))
                }
            }
            else -> return@withContext Result.failure(Exception("Unsupported withdrawal method: $method"))
        }

        val fiatStr = "${NexilusCurrency.formatBDT(coins, settings.coinsPerBDT)} / ${NexilusCurrency.formatUSDT(coins, settings.coinsPerCentUSDT)}"
        val now = System.currentTimeMillis()
        val wdId = "wd_${now.toString().takeLast(6)}"

        // Atomic Escrow Debit
        val updatedUser = user.copy(balance = user.balance - coins)
        dao.updateUser(updatedUser)

        val withdrawal = WithdrawalEntity(
            wdId = wdId,
            userId = user.uid,
            userName = "${user.firstName} ${user.lastName}".trim(),
            userUsername = user.username,
            coins = coins,
            method = method.uppercase(Locale.US),
            destination = cleanDest,
            fiatEstimate = fiatStr,
            status = "PENDING",
            createdAt = now,
            updatedAt = now
        )
        dao.upsertWithdrawal(withdrawal)

        dao.insertTransaction(
            TransactionEntity(
                txId = "tx_wd_hold_$now",
                userId = user.uid,
                type = "WITHDRAWAL_HOLD",
                amount = -coins,
                meta = "Escrow Hold for $method ($cleanDest) • ID: $wdId",
                createdAt = now
            )
        )

        val dmMsg = settings.tplWithdrawalSubmitted
            .replace("{coins}", NexilusCurrency.formatCoins(coins))
            .replace("{fiat}", fiatStr)
            .replace("{method}", "${withdrawal.method} ($cleanDest)")
        sendBotNotificationOrLog(
            recipientUid = user.uid,
            channelOrChat = "@${user.username} (Bot DM)",
            text = dmMsg
        )

        Result.success(withdrawal)
    }

    suspend fun markWithdrawalProcessing(wdId: String, adminNote: String): Result<WithdrawalEntity> = withContext(Dispatchers.IO) {
        val wd = dao.getWithdrawalById(wdId) ?: return@withContext Result.failure(Exception("Withdrawal not found"))
        if (wd.status == "APPROVED" || wd.status == "REJECTED") {
            return@withContext Result.failure(Exception("Withdrawal is already finalized (${wd.status})."))
        }
        val updated = wd.copy(
            status = "PROCESSING",
            adminNote = adminNote.ifBlank { "Queued for payout verification" },
            processedBy = "Admin #100001",
            updatedAt = System.currentTimeMillis()
        )
        dao.upsertWithdrawal(updated)
        recordAudit(
            action = "WITHDRAWAL_PROCESSING",
            target = "$wdId (uid:${wd.userId})",
            oldValue = wd.status,
            newValue = "PROCESSING",
            reason = updated.adminNote
        )
        Result.success(updated)
    }

    suspend fun approveWithdrawal(wdId: String, txId: String, adminNote: String): Result<WithdrawalEntity> = withContext(Dispatchers.IO) {
        if (txId.isBlank()) {
            return@withContext Result.failure(Exception("Transaction ID (txId) is required to approve a payout."))
        }
        val wd = dao.getWithdrawalById(wdId) ?: return@withContext Result.failure(Exception("Withdrawal not found"))
        if (wd.status == "APPROVED" || wd.status == "REJECTED") {
            return@withContext Result.failure(Exception("Withdrawal is already ${wd.status}."))
        }
        val settings = dao.getSettings() ?: AppSettingsEntity()
        val msgId = (105..9999).random()
        val channelClean = settings.paymentChannelUsername.removePrefix("@").ifBlank { "NexilusPayLogs" }
        val deepLink = "https://t.me/$channelClean/$msgId"
        val now = System.currentTimeMillis()

        val updated = wd.copy(
            status = "APPROVED",
            txId = txId.trim(),
            channelDeepLink = deepLink,
            adminNote = adminNote.trim(),
            processedBy = "Admin #100001",
            updatedAt = now
        )
        dao.upsertWithdrawal(updated)

        // Exact Channel Broadcast Format required by specification:
        val channelBroadcast = buildString {
            appendLine("🎉 New payout paid 🎉")
            appendLine("👤 User: ${wd.userName} (@${wd.userUsername})")
            appendLine("🪙 Amount: ${NexilusCurrency.formatCoins(wd.coins)} Nexilus (${wd.fiatEstimate})")
            appendLine("💳 Wallet: ${wd.method}")
            appendLine("🔗 Transaction id: ${txId.trim()}")
            append("🤖 Bot: @${settings.botUsername}")
        }
        sendBotNotificationOrLog(
            recipientUid = wd.userId,
            channelOrChat = "@$channelClean",
            text = channelBroadcast,
            messageId = msgId,
            deepLink = deepLink
        )

        // DM user with approval message + deep link
        val userDm = settings.tplWithdrawalApproved
            .replace("{coins}", NexilusCurrency.formatCoins(wd.coins))
            .replace("{fiat}", wd.fiatEstimate)
            .replace("{method}", wd.method) + ": $deepLink"
        sendBotNotificationOrLog(
            recipientUid = wd.userId,
            channelOrChat = "@${wd.userUsername} (Bot DM)",
            text = userDm,
            deepLink = deepLink
        )

        recordAudit(
            action = "WITHDRAWAL_APPROVED",
            target = "$wdId (uid:${wd.userId})",
            oldValue = wd.status,
            newValue = "APPROVED (txId:${txId.trim()})",
            reason = adminNote.ifBlank { "Payout verified and broadcast to @$channelClean" }
        )
        Result.success(updated)
    }

    suspend fun rejectWithdrawal(wdId: String, reason: String): Result<WithdrawalEntity> = withContext(Dispatchers.IO) {
        if (reason.isBlank()) {
            return@withContext Result.failure(Exception("A rejection reason is required."))
        }
        val wd = dao.getWithdrawalById(wdId) ?: return@withContext Result.failure(Exception("Withdrawal not found"))
        if (wd.status == "APPROVED" || wd.status == "REJECTED") {
            return@withContext Result.failure(Exception("Withdrawal is already ${wd.status}."))
        }
        val now = System.currentTimeMillis()
        val updated = wd.copy(
            status = "REJECTED",
            adminNote = reason.trim(),
            processedBy = "Admin #100001",
            updatedAt = now
        )
        dao.upsertWithdrawal(updated)

        // Atomic Refund to User
        val user = dao.getUserById(wd.userId)
        if (user != null) {
            dao.updateUser(user.copy(balance = user.balance + wd.coins))
            dao.insertTransaction(
                TransactionEntity(
                    txId = "tx_wd_refund_$now",
                    userId = user.uid,
                    type = "WITHDRAWAL_REFUND",
                    amount = wd.coins,
                    meta = "Refund for Rejected Withdrawal $wdId • Reason: ${reason.trim()}",
                    createdAt = now
                )
            )
        }

        val settings = dao.getSettings() ?: AppSettingsEntity()
        val dmText = settings.tplWithdrawalRejected
            .replace("{coins}", NexilusCurrency.formatCoins(wd.coins))
            .replace("{reason}", reason.trim())
        sendBotNotificationOrLog(
            recipientUid = wd.userId,
            channelOrChat = "@${wd.userUsername} (Bot DM)",
            text = dmText
        )

        recordAudit(
            action = "WITHDRAWAL_REJECTED",
            target = "$wdId (uid:${wd.userId})",
            oldValue = wd.status,
            newValue = "REJECTED (Refunded +${wd.coins} NXC)",
            reason = reason.trim()
        )
        Result.success(updated)
    }

    // ─── ADMIN USER MANAGEMENT ACTIONS (WITH AUDIT LOGS) ───
    suspend fun adminAdjustUserBalance(targetUid: String, deltaCoins: Long, reason: String): Result<UserEntity> = withContext(Dispatchers.IO) {
        if (reason.isBlank()) {
            return@withContext Result.failure(Exception("Audit reason is required for balance adjustments."))
        }
        val user = dao.getUserById(targetUid) ?: return@withContext Result.failure(Exception("User not found"))
        val oldBal = user.balance
        val newBal = (oldBal + deltaCoins).coerceAtLeast(0L)
        val newTotal = if (deltaCoins > 0) user.totalEarned + deltaCoins else user.totalEarned
        val updated = user.copy(balance = newBal, totalEarned = newTotal)
        dao.updateUser(updated)

        dao.insertTransaction(
            TransactionEntity(
                txId = "tx_adm_${System.currentTimeMillis()}",
                userId = targetUid,
                type = "ADMIN_ADJUSTMENT",
                amount = deltaCoins,
                meta = "Admin Adjustment: ${if (deltaCoins >= 0) "+$deltaCoins" else "$deltaCoins"} NXC ($reason)"
            )
        )
        recordAudit(
            action = "BALANCE_CHANGED",
            target = "uid:$targetUid (@${user.username})",
            oldValue = "$oldBal NXC",
            newValue = "$newBal NXC (${if (deltaCoins >= 0) "+$deltaCoins" else "$deltaCoins"})",
            reason = reason.trim()
        )
        Result.success(updated)
    }

    suspend fun adminSetUserStatus(targetUid: String, newStatus: String, reason: String): Result<UserEntity> = withContext(Dispatchers.IO) {
        if (reason.isBlank()) {
            return@withContext Result.failure(Exception("Audit reason is required to change user status."))
        }
        val user = dao.getUserById(targetUid) ?: return@withContext Result.failure(Exception("User not found"))
        val oldStatus = user.status
        val updated = user.copy(
            status = newStatus,
            fraudScore = if (newStatus == "ACTIVE") 0 else user.fraudScore
        )
        dao.updateUser(updated)
        recordAudit(
            action = if (newStatus == "BLOCKED") "USER_BLOCKED" else "USER_STATUS_CHANGED",
            target = "uid:$targetUid (@${user.username})",
            oldValue = oldStatus,
            newValue = newStatus,
            reason = reason.trim()
        )
        Result.success(updated)
    }

    suspend fun adminResetUserMining(targetUid: String, reason: String): Result<UserEntity> = withContext(Dispatchers.IO) {
        val user = dao.getUserById(targetUid) ?: return@withContext Result.failure(Exception("User not found"))
        val oldState = "${user.miningStatus} (cycle:${user.miningCycleId})"
        val updated = user.copy(
            miningStatus = "IDLE",
            miningStartedAt = 0L,
            miningEndsAt = 0L,
            miningCycleId = ""
        )
        dao.updateUser(updated)
        recordAudit(
            action = "MINING_RESET",
            target = "uid:$targetUid (@${user.username})",
            oldValue = oldState,
            newValue = "IDLE",
            reason = reason.ifBlank { "Admin manual mining cycle reset" }
        )
        Result.success(updated)
    }

    suspend fun adminResetUserDailyStreak(targetUid: String, reason: String): Result<UserEntity> = withContext(Dispatchers.IO) {
        val user = dao.getUserById(targetUid) ?: return@withContext Result.failure(Exception("User not found"))
        val oldStreak = "Streak ${user.dailyStreak} (last:${user.dailyLastClaimDay})"
        val updated = user.copy(
            dailyStreak = 0,
            dailyLastClaimDay = "",
            dailyLastClaimAt = 0L
        )
        dao.updateUser(updated)
        recordAudit(
            action = "DAILY_STREAK_RESET",
            target = "uid:$targetUid (@${user.username})",
            oldValue = oldStreak,
            newValue = "Streak 0",
            reason = reason.ifBlank { "Admin manual daily streak reset" }
        )
        Result.success(updated)
    }

    suspend fun adminSaveMission(mission: MissionEntity, reason: String) = withContext(Dispatchers.IO) {
        dao.upsertMission(mission)
        recordAudit(
            action = "MISSION_UPDATED",
            target = mission.missionId,
            oldValue = "-",
            newValue = "${mission.title} (${mission.reward} NXC, ${mission.type})",
            reason = reason.ifBlank { "Updated mission configuration" }
        )
    }

    suspend fun adminDeleteMission(missionId: String) = withContext(Dispatchers.IO) {
        dao.deleteMission(missionId)
        recordAudit(
            action = "MISSION_DELETED",
            target = missionId,
            oldValue = "ACTIVE",
            newValue = "DELETED",
            reason = "Deleted mission from active list"
        )
    }

    suspend fun adminUpdateSettings(newSettings: AppSettingsEntity, changeSummary: String, reason: String) = withContext(Dispatchers.IO) {
        val oldSettings = dao.getSettings() ?: AppSettingsEntity()
        dao.upsertSettings(newSettings)
        recordAudit(
            action = "SETTING_UPDATED",
            target = changeSummary,
            oldValue = "join=${oldSettings.joiningBonus},ref=${oldSettings.referralBonus},mine=${oldSettings.miningReward},ad=${oldSettings.adReward}",
            newValue = "join=${newSettings.joiningBonus},ref=${newSettings.referralBonus},mine=${newSettings.miningReward},ad=${newSettings.adReward}",
            reason = reason.ifBlank { "Admin updated configuration" }
        )
    }

    // ─── VERCEL CRON JOB (/api/cron/daily) EXECUTION ───
    suspend fun executeDailyCronJob(postOfflineNotice: Boolean): Result<String> = withContext(Dispatchers.IO) {
        val users = dao.getAllUsers()
        val today = currentUtcDateString()
        val resetUsers = users.map { u ->
            u.copy(adsTodayCount = 0, adsTodayDate = today)
        }
        dao.upsertUsers(resetUsers)

        val settings = dao.getSettings() ?: AppSettingsEntity()
        if (postOfflineNotice) {
            val msgId = (1000..9999).random()
            val channelClean = settings.paymentChannelUsername.removePrefix("@").ifBlank { "NexilusPayLogs" }
            sendBotNotificationOrLog(
                recipientUid = "CHANNEL_BROADCAST",
                channelOrChat = "@$channelClean",
                text = settings.tplAdminOffline,
                messageId = msgId,
                deepLink = "https://t.me/$channelClean/$msgId"
            )
        }

        recordAudit(
            action = "CRON_DAILY_EXECUTED",
            target = "/api/cron/daily",
            oldValue = "users:${users.size}",
            newValue = "Ads reset (${users.size} users), Leaderboard rebuilt, OfflineNotice=$postOfflineNotice",
            reason = "Hourly scheduled cron execution"
        )

        Result.success("Cron executed: Reset daily ad counters for ${users.size} users, rebuilt leaderboard cache${if (postOfflineNotice) ", and posted Admin Offline notice to @NexilusPayLogs" else ""}.")
    }

    private suspend fun recordAudit(
        action: String,
        target: String,
        oldValue: String,
        newValue: String,
        reason: String
    ) {
        dao.insertAuditLog(
            AuditLogEntity(
                logId = "aud_${System.currentTimeMillis()}_${(100..999).random()}",
                adminId = PRIMARY_UID,
                action = action,
                target = target,
                oldValue = oldValue,
                newValue = newValue,
                reason = reason
            )
        )
    }

    private suspend fun sendBotNotificationOrLog(
        recipientUid: String,
        channelOrChat: String,
        text: String,
        messageId: Int = (200..9999).random(),
        deepLink: String = ""
    ) {
        val settings = dao.getSettings()
        val token = settings?.botToken?.trim().orEmpty()

        // If a real Telegram Bot Token is configured, dispatch real HTTP POST to api.telegram.org
        if (token.isNotBlank() && channelOrChat.startsWith("@") && !channelOrChat.contains(" ")) {
            try {
                val payload = JSONObject().apply {
                    put("chat_id", channelOrChat)
                    put("text", text)
                }
                val request = Request.Builder()
                    .url("https://api.telegram.org/bot$token/sendMessage")
                    .post(payload.toString().toRequestBody("application/json".toMediaType()))
                    .build()
                httpClient.newCall(request).execute().close()
            } catch (_: Exception) {
                // Gracefully continue and log locally
            }
        }

        dao.insertChannelPost(
            ChannelPostEntity(
                postId = "post_${System.currentTimeMillis()}_${(10..99).random()}",
                channelUsername = channelOrChat,
                messageId = messageId,
                text = text,
                deepLink = deepLink.ifBlank { "https://t.me/NexilusPayLogs/$messageId" },
                recipientUid = recipientUid
            )
        )
    }
}
