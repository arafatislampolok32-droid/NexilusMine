package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.NexilusDatabase
import com.example.data.model.AppSettingsEntity
import com.example.data.model.AuditLogEntity
import com.example.data.model.ChannelPostEntity
import com.example.data.model.MissionEntity
import com.example.data.model.TransactionEntity
import com.example.data.model.UserEntity
import com.example.data.model.WithdrawalEntity
import com.example.data.repository.NexilusRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.UUID

enum class MainTab {
    HOME, EARN, MINING, INVITE, WALLET
}

enum class OverlayScreen {
    NONE, LEADERBOARD, SETTINGS, ADMIN_CONSOLE
}

enum class AdminSubPage(val title: String) {
    OVERVIEW("Overview & KPIs"),
    WITHDRAWALS("Withdrawals Queue"),
    USERS("Users & Balances"),
    MINING_REWARDS("Mining & Rewards"),
    ADS_MISSIONS("AdsGram & Missions"),
    CURRENCY_TIERS("Currency & Tiers"),
    BOT_TEMPLATES("Bot & Templates"),
    SECURITY_SYSTEM("Anti-Fraud & Cron"),
    PAYMENT_LOGS("@NexilusPayLogs"),
    AUDIT_LOGS("Audit Logs")
}

data class RewardedAdModalState(
    val isVisible: Boolean = false,
    val title: String = "AdsGram Rewarded Spot",
    val subtitle: String = "Watch full spot to verify cryptographic adProof",
    val blockId: String = "block-4821",
    val secondsLeft: Int = 0,
    val adProofToken: String = "",
    val purpose: String = "WATCH_AD", // WATCH_AD, MINING_CLAIM, MISSION_AD
    val mission: MissionEntity? = null
)

data class ToastNotification(
    val id: Long = System.currentTimeMillis(),
    val message: String,
    val isError: Boolean = false
)

class NexilusViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = application.getSharedPreferences("nexilus_prefs", Context.MODE_PRIVATE)
    private val dao = NexilusDatabase.getInstance(application).nexilusDao()
    val repository = NexilusRepository(dao)

    // Navigation State
    private val _currentTab = MutableStateFlow(MainTab.HOME)
    val currentTab: StateFlow<MainTab> = _currentTab.asStateFlow()

    private val _overlayScreen = MutableStateFlow(OverlayScreen.NONE)
    val overlayScreen: StateFlow<OverlayScreen> = _overlayScreen.asStateFlow()

    private val _adminSubPage = MutableStateFlow(AdminSubPage.OVERVIEW)
    val adminSubPage: StateFlow<AdminSubPage> = _adminSubPage.asStateFlow()

    private val _showPayLogsSheet = MutableStateFlow(false)
    val showPayLogsSheet: StateFlow<Boolean> = _showPayLogsSheet.asStateFlow()

    // Persisted Preferences (Dark/Light, BDT/USDT, EN/BN, Notifications, Haptics — NO LOGOUT)
    private val _isDarkTheme = MutableStateFlow(prefs.getBoolean("dark_theme", true))
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme.asStateFlow()

    private val _selectedCurrency = MutableStateFlow(prefs.getString("currency", "BDT") ?: "BDT")
    val selectedCurrency: StateFlow<String> = _selectedCurrency.asStateFlow()

    private val _selectedLanguage = MutableStateFlow(prefs.getString("language", "EN") ?: "EN")
    val selectedLanguage: StateFlow<String> = _selectedLanguage.asStateFlow()

    private val _notificationsEnabled = MutableStateFlow(prefs.getBoolean("notifications", true))
    val notificationsEnabled: StateFlow<Boolean> = _notificationsEnabled.asStateFlow()

    private val _soundVibrationEnabled = MutableStateFlow(prefs.getBoolean("sound_vibration", true))
    val soundVibrationEnabled: StateFlow<Boolean> = _soundVibrationEnabled.asStateFlow()

    // Live Server Clock Mining Countdown
    private val _miningRemainingSec = MutableStateFlow(0L)
    val miningRemainingSec: StateFlow<Long> = _miningRemainingSec.asStateFlow()

    // Interactive AdsGram Rewarded Ad State
    private val _adModalState = MutableStateFlow(RewardedAdModalState())
    val adModalState: StateFlow<RewardedAdModalState> = _adModalState.asStateFlow()

    // Toast State
    private val _toast = MutableStateFlow<ToastNotification?>(null)
    val toast: StateFlow<ToastNotification?> = _toast.asStateFlow()

    // Database Flows
    val currentUser: StateFlow<UserEntity?> = repository.primaryUserFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val allUsers: StateFlow<List<UserEntity>> = repository.allUsersFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val topReferrers: StateFlow<List<UserEntity>> = repository.topReferrersFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val topEarners: StateFlow<List<UserEntity>> = repository.topEarnersFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val userTransactions: StateFlow<List<TransactionEntity>> = repository.primaryTransactionsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allTransactions: StateFlow<List<TransactionEntity>> = repository.allTransactionsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val userWithdrawals: StateFlow<List<WithdrawalEntity>> = repository.primaryWithdrawalsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allWithdrawals: StateFlow<List<WithdrawalEntity>> = repository.allWithdrawalsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeMissions: StateFlow<List<MissionEntity>> = repository.activeMissionsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val settings: StateFlow<AppSettingsEntity> = repository.settingsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AppSettingsEntity())
        .let { flow ->
            val mapped = MutableStateFlow(AppSettingsEntity())
            viewModelScope.launch {
                flow.collect { if (it != null) mapped.value = it }
            }
            mapped.asStateFlow()
        }

    val auditLogs: StateFlow<List<AuditLogEntity>> = repository.auditLogsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val channelPosts: StateFlow<List<ChannelPostEntity>> = repository.channelPostsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            repository.ensureSeeded()
        }
        startServerClockMiningTicker()
    }

    private fun startServerClockMiningTicker() {
        viewModelScope.launch {
            while (isActive) {
                val u = currentUser.value
                if (u != null) {
                    if (u.miningStatus == "ACTIVE") {
                        val now = System.currentTimeMillis()
                        val rem = ((u.miningEndsAt - now) / 1000L).coerceAtLeast(0L)
                        _miningRemainingSec.value = rem
                        if (rem == 0L && u.miningEndsAt > 0L) {
                            repository.syncMiningReadyStateIfElapsed(u.uid)
                        }
                    } else {
                        _miningRemainingSec.value = 0L
                    }
                }
                delay(1000L)
            }
        }
    }

    fun showToast(message: String, isError: Boolean = false) {
        val notification = ToastNotification(message = message, isError = isError)
        _toast.value = notification
        viewModelScope.launch {
            delay(3600L)
            if (_toast.value?.id == notification.id) {
                _toast.value = null
            }
        }
    }

    fun dismissToast() {
        _toast.value = null
    }

    // Navigation Handlers
    fun selectTab(tab: MainTab) {
        _overlayScreen.value = OverlayScreen.NONE
        _currentTab.value = tab
    }

    fun openOverlay(overlay: OverlayScreen) {
        _overlayScreen.value = overlay
    }

    fun closeOverlay() {
        _overlayScreen.value = OverlayScreen.NONE
    }

    fun selectAdminSubPage(page: AdminSubPage) {
        _adminSubPage.value = page
    }

    fun togglePayLogsSheet(show: Boolean) {
        _showPayLogsSheet.value = show
    }

    // Settings Preferences Handlers
    fun setDarkTheme(dark: Boolean) {
        _isDarkTheme.value = dark
        prefs.edit().putBoolean("dark_theme", dark).apply()
        showToast(if (dark) "Dark Quantum Theme Active" else "Light Theme Active")
    }

    fun setCurrency(currency: String) {
        _selectedCurrency.value = currency
        prefs.edit().putString("currency", currency).apply()
        showToast("Primary Currency set to $currency")
    }

    fun setLanguage(lang: String) {
        _selectedLanguage.value = lang
        prefs.edit().putString("language", lang).apply()
        showToast(if (lang == "BN") "ভাষা বাংলায় পরিবর্তন করা হয়েছে" else "Language changed to English")
    }

    fun setNotificationsEnabled(enabled: Boolean) {
        _notificationsEnabled.value = enabled
        prefs.edit().putBoolean("notifications", enabled).apply()
    }

    fun setSoundVibrationEnabled(enabled: Boolean) {
        _soundVibrationEnabled.value = enabled
        prefs.edit().putBoolean("sound_vibration", enabled).apply()
    }

    // ─── USER ACTIONS ───
    fun startMiningCycle() {
        val uid = currentUser.value?.uid ?: NexilusRepository.PRIMARY_UID
        viewModelScope.launch {
            val res = repository.startMining(uid)
            res.fold(
                onSuccess = {
                    showToast("⛏ Mining Cycle Started! Next claim in 2 hours (7200s).")
                },
                onFailure = {
                    showToast(it.message ?: "Failed to start mining", isError = true)
                }
            )
        }
    }

    fun fastForwardMiningForDemo() {
        val uid = currentUser.value?.uid ?: NexilusRepository.PRIMARY_UID
        viewModelScope.launch {
            val res = repository.fastForwardMiningCycle(uid)
            res.fold(
                onSuccess = {
                    _miningRemainingSec.value = 0L
                    showToast("⚡ Mining cycle fast-forwarded to READY! You can now claim your reward.")
                },
                onFailure = {
                    showToast(it.message ?: "Could not fast-forward cycle", isError = true)
                }
            )
        }
    }

    fun initiateMiningClaimWithAd() {
        val user = currentUser.value ?: return
        val cfg = settings.value
        val proof = "adp_mine_${UUID.randomUUID().toString().replace("-", "").take(10)}"
        launchRewardedAdSpot(
            title = "Mining Claim Verification Ad",
            subtitle = "Verify cycle ${user.miningCycleId.ifBlank { "READY" }} via AdsGram (${cfg.adsRewardedId})",
            blockId = cfg.adsRewardedId,
            purpose = "MINING_CLAIM",
            adProofToken = proof
        )
    }

    fun initiateWatchAndEarnAd() {
        val cfg = settings.value
        val proof = "adp_watch_${UUID.randomUUID().toString().replace("-", "").take(10)}"
        launchRewardedAdSpot(
            title = "Watch & Earn (+${cfg.adReward} NXC)",
            subtitle = "AdsGram Rewarded Video Spot • Block ${cfg.adsRewardedId}",
            blockId = cfg.adsRewardedId,
            purpose = "WATCH_AD",
            adProofToken = proof
        )
    }

    fun initiateMissionExecution(mission: MissionEntity) {
        val user = currentUser.value ?: return
        if (user.isMissionCompleted(mission.missionId)) {
            showToast("Mission already verified!", isError = true)
            return
        }
        if (mission.type == "AD") {
            val cfg = settings.value
            val proof = "adp_msn_${mission.missionId}_${UUID.randomUUID().toString().take(6)}"
            launchRewardedAdSpot(
                title = mission.title,
                subtitle = "Complete partner spot to earn +${mission.reward} NXC",
                blockId = cfg.adsInterstitial1,
                purpose = "MISSION_AD",
                adProofToken = proof,
                mission = mission
            )
        } else {
            // Telegram Channel Join & Verify via bot.getChatMember
            viewModelScope.launch {
                val res = repository.completeAndVerifyMission(user.uid, mission)
                res.fold(
                    onSuccess = { reward ->
                        showToast("✅ Verified membership in ${mission.channelUsername.ifBlank { "Telegram Channel" }}! +$reward NXC credited.")
                    },
                    onFailure = {
                        showToast(it.message ?: "Verification failed", isError = true)
                    }
                )
            }
        }
    }

    private fun launchRewardedAdSpot(
        title: String,
        subtitle: String,
        blockId: String,
        purpose: String,
        adProofToken: String,
        mission: MissionEntity? = null
    ) {
        _adModalState.value = RewardedAdModalState(
            isVisible = true,
            title = title,
            subtitle = subtitle,
            blockId = blockId,
            secondsLeft = 3,
            adProofToken = adProofToken,
            purpose = purpose,
            mission = mission
        )
        viewModelScope.launch {
            for (sec in 3 downTo 1) {
                if (!_adModalState.value.isVisible) return@launch
                _adModalState.value = _adModalState.value.copy(secondsLeft = sec)
                delay(1000L)
            }
            if (_adModalState.value.isVisible) {
                _adModalState.value = _adModalState.value.copy(secondsLeft = 0)
            }
        }
    }

    fun closeRewardedAdModal() {
        _adModalState.value = RewardedAdModalState(isVisible = false)
    }

    fun confirmRewardedAdCompleted() {
        val state = _adModalState.value
        if (!state.isVisible || state.secondsLeft > 0) return
        val user = currentUser.value ?: return
        _adModalState.value = RewardedAdModalState(isVisible = false)

        viewModelScope.launch {
            when (state.purpose) {
                "MINING_CLAIM" -> {
                    val cycleToken = repository.getCycleTokenForUser(user)
                    val res = repository.claimMiningReward(user.uid, cycleToken, state.adProofToken)
                    res.fold(
                        onSuccess = { reward ->
                            showToast("🎉 Mining Claimed! +$reward Nexilus Coins credited to your wallet.")
                        },
                        onFailure = {
                            showToast(it.message ?: "Claim failed", isError = true)
                        }
                    )
                }
                "WATCH_AD" -> {
                    val res = repository.claimAdReward(user.uid, state.adProofToken)
                    res.fold(
                        onSuccess = { reward ->
                            showToast("🎬 Ad Verified! +$reward Nexilus Coins earned.")
                        },
                        onFailure = {
                            showToast(it.message ?: "Ad claim failed", isError = true)
                        }
                    )
                }
                "MISSION_AD" -> {
                    val msn = state.mission ?: return@launch
                    val res = repository.completeAndVerifyMission(user.uid, msn)
                    res.fold(
                        onSuccess = { reward ->
                            showToast("🚀 Mission Completed! +$reward Nexilus Coins credited.")
                        },
                        onFailure = {
                            showToast(it.message ?: "Mission claim failed", isError = true)
                        }
                    )
                }
            }
        }
    }

    fun claimDailyBonus() {
        val uid = currentUser.value?.uid ?: NexilusRepository.PRIMARY_UID
        viewModelScope.launch {
            val res = repository.claimDailyBonus(uid)
            res.fold(
                onSuccess = { (day, reward) ->
                    showToast("🔥 Day $day Daily Bonus Claimed! +$reward Nexilus Coins")
                },
                onFailure = {
                    showToast(it.message ?: "Daily bonus claim failed", isError = true)
                }
            )
        }
    }

    fun simulateFriendReferral(friendUsername: String, testSameDeviceFraud: Boolean = false) {
        val uid = currentUser.value?.uid ?: NexilusRepository.PRIMARY_UID
        viewModelScope.launch {
            val res = repository.simulateNewReferralJoin(uid, friendUsername, testSameDeviceFraud)
            res.fold(
                onSuccess = { msg -> showToast(msg) },
                onFailure = { err -> showToast(err.message ?: "Referral failed", isError = true) }
            )
        }
    }

    fun claimMilestone(milestoneId: String) {
        val uid = currentUser.value?.uid ?: NexilusRepository.PRIMARY_UID
        viewModelScope.launch {
            val res = repository.claimReferralMilestone(uid, milestoneId)
            res.fold(
                onSuccess = { reward ->
                    showToast("🏆 Milestone Claimed! +$reward Nexilus Coins added.")
                },
                onFailure = {
                    showToast(it.message ?: "Could not claim milestone", isError = true)
                }
            )
        }
    }

    fun submitWithdrawalRequest(
        coins: Long,
        method: String,
        destination: String,
        onSuccessCallback: () -> Unit
    ) {
        val uid = currentUser.value?.uid ?: NexilusRepository.PRIMARY_UID
        viewModelScope.launch {
            val res = repository.submitWithdrawal(uid, coins, method, destination)
            res.fold(
                onSuccess = { wd ->
                    showToast("✅ Withdrawal #${wd.wdId} Submitted! ${wd.coins} NXC placed in escrow.")
                    onSuccessCallback()
                },
                onFailure = {
                    showToast(it.message ?: "Withdrawal submission failed", isError = true)
                }
            )
        }
    }

    // ─── ADMIN ACTIONS ───
    fun adminApproveWithdrawal(wdId: String, txId: String, note: String) {
        viewModelScope.launch {
            val res = repository.approveWithdrawal(wdId, txId, note)
            res.fold(
                onSuccess = {
                    showToast("✅ Approved $wdId & broadcast payout to @NexilusPayLogs!")
                },
                onFailure = {
                    showToast(it.message ?: "Approval failed", isError = true)
                }
            )
        }
    }

    fun adminRejectWithdrawal(wdId: String, reason: String) {
        viewModelScope.launch {
            val res = repository.rejectWithdrawal(wdId, reason)
            res.fold(
                onSuccess = {
                    showToast("↩ Rejected $wdId & atomically refunded ${it.coins} NXC to user.")
                },
                onFailure = {
                    showToast(it.message ?: "Rejection failed", isError = true)
                }
            )
        }
    }

    fun adminMarkProcessing(wdId: String, note: String) {
        viewModelScope.launch {
            val res = repository.markWithdrawalProcessing(wdId, note)
            res.fold(
                onSuccess = {
                    showToast("⏳ Marked $wdId as PROCESSING.")
                },
                onFailure = {
                    showToast(it.message ?: "Update failed", isError = true)
                }
            )
        }
    }

    fun adminAdjustUserCoins(targetUid: String, delta: Long, reason: String) {
        viewModelScope.launch {
            val res = repository.adminAdjustUserBalance(targetUid, delta, reason)
            res.fold(
                onSuccess = {
                    showToast("✅ Updated @${it.username} balance to ${it.balance} NXC (Audit logged).")
                },
                onFailure = {
                    showToast(it.message ?: "Adjustment failed", isError = true)
                }
            )
        }
    }

    fun adminUpdateUserStatus(targetUid: String, status: String, reason: String) {
        viewModelScope.launch {
            val res = repository.adminSetUserStatus(targetUid, status, reason)
            res.fold(
                onSuccess = {
                    showToast("🛡 Set @${it.username} status to $status (Audit logged).")
                },
                onFailure = {
                    showToast(it.message ?: "Status change failed", isError = true)
                }
            )
        }
    }

    fun adminResetMining(targetUid: String, reason: String) {
        viewModelScope.launch {
            val res = repository.adminResetUserMining(targetUid, reason)
            res.fold(
                onSuccess = {
                    showToast("🔄 Reset mining state for @${it.username}.")
                },
                onFailure = {
                    showToast(it.message ?: "Reset failed", isError = true)
                }
            )
        }
    }

    fun adminResetDailyStreak(targetUid: String, reason: String) {
        viewModelScope.launch {
            val res = repository.adminResetUserDailyStreak(targetUid, reason)
            res.fold(
                onSuccess = {
                    showToast("🔄 Reset daily streak for @${it.username}.")
                },
                onFailure = {
                    showToast(it.message ?: "Reset failed", isError = true)
                }
            )
        }
    }

    fun adminUpdateAppSettings(updated: AppSettingsEntity, sectionName: String, reason: String) {
        viewModelScope.launch {
            repository.adminUpdateSettings(updated, sectionName, reason)
            showToast("✅ Saved $sectionName & wrote Audit Log entry.")
        }
    }

    fun adminSaveMission(mission: MissionEntity, reason: String) {
        viewModelScope.launch {
            repository.adminSaveMission(mission, reason)
            showToast("✅ Saved mission '${mission.title}'.")
        }
    }

    fun adminDeleteMission(missionId: String) {
        viewModelScope.launch {
            repository.adminDeleteMission(missionId)
            showToast("🗑 Deleted mission $missionId.")
        }
    }

    fun adminRunHourlyCron(postOfflineNotice: Boolean) {
        viewModelScope.launch {
            val res = repository.executeDailyCronJob(postOfflineNotice)
            res.fold(
                onSuccess = { msg -> showToast(msg) },
                onFailure = { err -> showToast(err.message ?: "Cron failed", isError = true) }
            )
        }
    }
}
