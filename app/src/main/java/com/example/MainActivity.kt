package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.NexilusBottomNav
import com.example.ui.components.NexilusToastBanner
import com.example.ui.components.NexilusTopHeader
import com.example.ui.components.PayLogsModalDialog
import com.example.ui.components.RewardedAdModalDialog
import com.example.ui.screens.AdminDashboardScreen
import com.example.ui.screens.EarnScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.InviteScreen
import com.example.ui.screens.LeaderboardScreen
import com.example.ui.screens.MiningScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.WalletScreen
import com.example.ui.theme.NexilusBrandBrush
import com.example.ui.theme.NexilusCyan
import com.example.ui.theme.NexilusHorizontalBrush
import com.example.ui.theme.NexilusMineTheme
import com.example.ui.theme.SpaceGroteskFontFamily
import com.example.ui.viewmodel.MainTab
import com.example.ui.viewmodel.NexilusViewModel
import com.example.ui.viewmodel.OverlayScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val vm: NexilusViewModel = viewModel()
            val isDark by vm.isDarkTheme.collectAsStateWithLifecycle()

            NexilusMineTheme(darkTheme = isDark) {
                NexilusAppRoot(vm = vm)
            }
        }
    }
}

@Composable
fun NexilusAppRoot(vm: NexilusViewModel) {
    val user by vm.currentUser.collectAsStateWithLifecycle()
    val settings by vm.settings.collectAsStateWithLifecycle()
    val currentTab by vm.currentTab.collectAsStateWithLifecycle()
    val overlayScreen by vm.overlayScreen.collectAsStateWithLifecycle()
    val adminSubPage by vm.adminSubPage.collectAsStateWithLifecycle()
    val showPayLogs by vm.showPayLogsSheet.collectAsStateWithLifecycle()

    val isDark by vm.isDarkTheme.collectAsStateWithLifecycle()
    val currency by vm.selectedCurrency.collectAsStateWithLifecycle()
    val language by vm.selectedLanguage.collectAsStateWithLifecycle()
    val notificationsEnabled by vm.notificationsEnabled.collectAsStateWithLifecycle()
    val soundVibrationEnabled by vm.soundVibrationEnabled.collectAsStateWithLifecycle()

    val miningRemainingSec by vm.miningRemainingSec.collectAsStateWithLifecycle()
    val adModalState by vm.adModalState.collectAsStateWithLifecycle()
    val toast by vm.toast.collectAsStateWithLifecycle()

    val userTransactions by vm.userTransactions.collectAsStateWithLifecycle()
    val allTransactions by vm.allTransactions.collectAsStateWithLifecycle()
    val userWithdrawals by vm.userWithdrawals.collectAsStateWithLifecycle()
    val allWithdrawals by vm.allWithdrawals.collectAsStateWithLifecycle()
    val activeMissions by vm.activeMissions.collectAsStateWithLifecycle()
    val allUsers by vm.allUsers.collectAsStateWithLifecycle()
    val topReferrers by vm.topReferrers.collectAsStateWithLifecycle()
    val topEarners by vm.topEarners.collectAsStateWithLifecycle()
    val auditLogs by vm.auditLogs.collectAsStateWithLifecycle()
    val channelPosts by vm.channelPosts.collectAsStateWithLifecycle()

    // Handle system back from non-Home tabs when no overlay is open
    BackHandler(enabled = overlayScreen == OverlayScreen.NONE && currentTab != MainTab.HOME) {
        vm.selectTab(MainTab.HOME)
    }

    val currentUserVal = user
    if (currentUserVal == null) {
        NexilusLoadingSplash()
        return
    }

    val todayUtc = vm.repository.currentUtcDateString()

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            Column(
                modifier = Modifier.windowInsetsPadding(WindowInsets.statusBars)
            ) {
                NexilusTopHeader(
                    user = currentUserVal,
                    language = language,
                    activeOverlay = overlayScreen,
                    onOpenLeaderboard = {
                        vm.openOverlay(
                            if (overlayScreen == OverlayScreen.LEADERBOARD) OverlayScreen.NONE
                            else OverlayScreen.LEADERBOARD
                        )
                    },
                    onOpenPayLogs = { vm.togglePayLogsSheet(true) },
                    onToggleAdmin = {
                        vm.openOverlay(
                            if (overlayScreen == OverlayScreen.ADMIN_CONSOLE) OverlayScreen.NONE
                            else OverlayScreen.ADMIN_CONSOLE
                        )
                    },
                    onOpenSettings = {
                        vm.openOverlay(
                            if (overlayScreen == OverlayScreen.SETTINGS) OverlayScreen.NONE
                            else OverlayScreen.SETTINGS
                        )
                    }
                )
            }
        },
        bottomBar = {
            if (overlayScreen == OverlayScreen.NONE) {
                NexilusBottomNav(
                    currentTab = currentTab,
                    language = language,
                    onSelectTab = { vm.selectTab(it) }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            when (overlayScreen) {
                OverlayScreen.LEADERBOARD -> {
                    LeaderboardScreen(
                        topReferrers = topReferrers,
                        topEarners = topEarners,
                        currentUserUid = currentUserVal.uid,
                        onBack = { vm.closeOverlay() }
                    )
                }
                OverlayScreen.SETTINGS -> {
                    SettingsScreen(
                        isDarkTheme = isDark,
                        selectedCurrency = currency,
                        selectedLanguage = language,
                        notificationsEnabled = notificationsEnabled,
                        soundVibrationEnabled = soundVibrationEnabled,
                        botUsername = settings.botUsername,
                        onSetDarkTheme = { vm.setDarkTheme(it) },
                        onSetCurrency = { vm.setCurrency(it) },
                        onSetLanguage = { vm.setLanguage(it) },
                        onSetNotifications = { vm.setNotificationsEnabled(it) },
                        onSetSoundVibration = { vm.setSoundVibrationEnabled(it) },
                        onBack = { vm.closeOverlay() }
                    )
                }
                OverlayScreen.ADMIN_CONSOLE -> {
                    AdminDashboardScreen(
                        activeSubPage = adminSubPage,
                        onSelectSubPage = { vm.selectAdminSubPage(it) },
                        users = allUsers,
                        transactions = allTransactions,
                        withdrawals = allWithdrawals,
                        missions = activeMissions,
                        settings = settings,
                        auditLogs = auditLogs,
                        channelPosts = channelPosts,
                        onApproveWithdrawal = { wdId, txId, note ->
                            vm.adminApproveWithdrawal(wdId, txId, note)
                        },
                        onRejectWithdrawal = { wdId, reason ->
                            vm.adminRejectWithdrawal(wdId, reason)
                        },
                        onMarkProcessing = { wdId, note ->
                            vm.adminMarkProcessing(wdId, note)
                        },
                        onAdjustUserCoins = { uid, delta, reason ->
                            vm.adminAdjustUserCoins(uid, delta, reason)
                        },
                        onUpdateUserStatus = { uid, status, reason ->
                            vm.adminUpdateUserStatus(uid, status, reason)
                        },
                        onResetUserMining = { uid, reason ->
                            vm.adminResetMining(uid, reason)
                        },
                        onResetUserDaily = { uid, reason ->
                            vm.adminResetDailyStreak(uid, reason)
                        },
                        onUpdateSettings = { updated, section, reason ->
                            vm.adminUpdateAppSettings(updated, section, reason)
                        },
                        onSaveMission = { msn, reason ->
                            vm.adminSaveMission(msn, reason)
                        },
                        onDeleteMission = { id ->
                            vm.adminDeleteMission(id)
                        },
                        onRunDailyCron = { postNotice ->
                            vm.adminRunHourlyCron(postNotice)
                        },
                        onBack = { vm.closeOverlay() }
                    )
                }
                OverlayScreen.NONE -> {
                    when (currentTab) {
                        MainTab.HOME -> {
                            HomeScreen(
                                user = currentUserVal,
                                settings = settings,
                                remainingMiningSec = miningRemainingSec,
                                todayDateStr = todayUtc,
                                selectedCurrency = currency,
                                language = language,
                                recentTransactions = userTransactions,
                                onStartMining = { vm.startMiningCycle() },
                                onClaimMining = { vm.initiateMiningClaimWithAd() },
                                onFastForwardMining = { vm.fastForwardMiningForDemo() },
                                onClaimDailyBonus = { vm.claimDailyBonus() },
                                onNavigateToWallet = { vm.selectTab(MainTab.WALLET) },
                                onNavigateToEarn = { vm.selectTab(MainTab.EARN) }
                            )
                        }
                        MainTab.EARN -> {
                            EarnScreen(
                                user = currentUserVal,
                                settings = settings,
                                todayDateStr = todayUtc,
                                missions = activeMissions,
                                language = language,
                                onWatchRewardedAd = { vm.initiateWatchAndEarnAd() },
                                onExecuteMission = { vm.initiateMissionExecution(it) }
                            )
                        }
                        MainTab.MINING -> {
                            MiningScreen(
                                user = currentUserVal,
                                settings = settings,
                                remainingSec = miningRemainingSec,
                                miningHistory = userTransactions.filter { it.type == "MINING_REWARD" },
                                cycleTokenPreview = vm.repository.getCycleTokenForUser(currentUserVal),
                                language = language,
                                onStartMining = { vm.startMiningCycle() },
                                onClaimMining = { vm.initiateMiningClaimWithAd() },
                                onFastForwardDemo = { vm.fastForwardMiningForDemo() }
                            )
                        }
                        MainTab.INVITE -> {
                            InviteScreen(
                                user = currentUserVal,
                                settings = settings,
                                topReferrers = topReferrers,
                                language = language,
                                onSimulateReferralJoin = { handle, fraud ->
                                    vm.simulateFriendReferral(handle, fraud)
                                },
                                onClaimMilestone = { vm.claimMilestone(it) },
                                onOpenFullLeaderboard = { vm.openOverlay(OverlayScreen.LEADERBOARD) },
                                onShowToast = { vm.showToast(it) }
                            )
                        }
                        MainTab.WALLET -> {
                            WalletScreen(
                                user = currentUserVal,
                                settings = settings,
                                withdrawals = userWithdrawals,
                                language = language,
                                onSubmitWithdrawal = { coins, method, dest, onDone ->
                                    vm.submitWithdrawalRequest(coins, method, dest, onDone)
                                }
                            )
                        }
                    }
                }
            }

            // Floating Toast Banner
            NexilusToastBanner(
                toast = toast,
                onDismiss = { vm.dismissToast() },
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }
    }

    // AdsGram Rewarded Ad Verification Dialog
    RewardedAdModalDialog(
        state = adModalState,
        onDismiss = { vm.closeRewardedAdModal() },
        onCompleteClaim = { vm.confirmRewardedAdCompleted() }
    )

    // @NexilusPayLogs & Bot Notification Feed Modal
    PayLogsModalDialog(
        visible = showPayLogs,
        posts = channelPosts,
        onDismiss = { vm.togglePayLogsSheet(false) }
    )
}

@Composable
fun NexilusLoadingSplash() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF050B1F)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(84.dp)
                    .clip(CircleShape)
                    .background(NexilusBrandBrush),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "N",
                    style = TextStyle(
                        fontFamily = SpaceGroteskFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 42.sp,
                        color = Color.White
                    )
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Nexilus Mine",
                style = TextStyle(
                    fontFamily = SpaceGroteskFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp,
                    brush = NexilusHorizontalBrush
                )
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "VERIFYING TELEGRAM WEBAPP HMAC & QUANTUM LEDGER...",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = NexilusCyan,
                    letterSpacing = 1.sp
                )
            )
            Spacer(modifier = Modifier.height(20.dp))
            CircularProgressIndicator(color = NexilusCyan)
        }
    }
}
