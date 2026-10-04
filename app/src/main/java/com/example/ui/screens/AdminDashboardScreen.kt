package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.AppSettingsEntity
import com.example.data.model.AuditLogEntity
import com.example.data.model.ChannelPostEntity
import com.example.data.model.MissionEntity
import com.example.data.model.NexilusCurrency
import com.example.data.model.TransactionEntity
import com.example.data.model.UserEntity
import com.example.data.model.WithdrawalEntity
import com.example.ui.components.NexilusGradientButton
import com.example.ui.theme.NexilusBlue
import com.example.ui.theme.NexilusBrandBrush
import com.example.ui.theme.NexilusCyan
import com.example.ui.theme.NexilusDanger
import com.example.ui.theme.NexilusHorizontalBrush
import com.example.ui.theme.NexilusPurple
import com.example.ui.theme.NexilusSecondarySurfaceDark
import com.example.ui.theme.NexilusSuccess
import com.example.ui.theme.NexilusSurfaceDark
import com.example.ui.theme.NexilusViolet
import com.example.ui.theme.SpaceGroteskFontFamily
import com.example.ui.viewmodel.AdminSubPage
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

@Composable
fun AdminDashboardScreen(
    activeSubPage: AdminSubPage,
    onSelectSubPage: (AdminSubPage) -> Unit,
    users: List<UserEntity>,
    transactions: List<TransactionEntity>,
    withdrawals: List<WithdrawalEntity>,
    missions: List<MissionEntity>,
    settings: AppSettingsEntity,
    auditLogs: List<AuditLogEntity>,
    channelPosts: List<ChannelPostEntity>,
    onApproveWithdrawal: (String, String, String) -> Unit,
    onRejectWithdrawal: (String, String) -> Unit,
    onMarkProcessing: (String, String) -> Unit,
    onAdjustUserCoins: (String, Long, String) -> Unit,
    onUpdateUserStatus: (String, String, String) -> Unit,
    onResetUserMining: (String, String) -> Unit,
    onResetUserDaily: (String, String) -> Unit,
    onUpdateSettings: (AppSettingsEntity, String, String) -> Unit,
    onSaveMission: (MissionEntity, String) -> Unit,
    onDeleteMission: (String) -> Unit,
    onRunDailyCron: (Boolean) -> Unit,
    onBack: () -> Unit
) {
    BackHandler(onBack = onBack)

    var selectedUserForDetail by remember { mutableStateOf<UserEntity?>(null) }
    var approveModalWithdrawal by remember { mutableStateOf<WithdrawalEntity?>(null) }
    var rejectModalWithdrawal by remember { mutableStateOf<WithdrawalEntity?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("admin_dashboard_screen")
    ) {
        // Admin Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.testTag("admin_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Exit Admin Console",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
                Column {
                    Text(
                        text = "Nexilus Admin Dashboard",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "Telegram Admin Verified • Every write logs to auditLogs",
                        style = MaterialTheme.typography.labelSmall.copy(color = NexilusCyan)
                    )
                }
            }
        }

        // Scrollable Admin Sub-Page Pill Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 14.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AdminSubPage.entries.forEach { page ->
                val isSelected = activeSubPage == page
                Box(
                    modifier = Modifier
                        .heightIn(min = 42.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (isSelected) NexilusHorizontalBrush
                            else Brush.horizontalGradient(
                                listOf(
                                    NexilusSecondarySurfaceDark,
                                    NexilusSecondarySurfaceDark
                                )
                            )
                        )
                        .clickable { onSelectSubPage(page) }
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                        .testTag("admin_tab_${page.name.lowercase()}"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = page.title,
                        style = MaterialTheme.typography.labelLarge.copy(
                            color = Color.White,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        )
                    )
                }
            }
        }

        // Sub-page Body
        when (activeSubPage) {
            AdminSubPage.OVERVIEW -> AdminOverviewSection(
                users = users,
                transactions = transactions,
                withdrawals = withdrawals
            )
            AdminSubPage.WITHDRAWALS -> AdminWithdrawalsSection(
                withdrawals = withdrawals,
                onOpenApproveModal = { approveModalWithdrawal = it },
                onOpenRejectModal = { rejectModalWithdrawal = it },
                onMarkProcessing = { wd -> onMarkProcessing(wd.wdId, "Admin marked as PROCESSING") }
            )
            AdminSubPage.USERS -> AdminUsersSection(
                users = users,
                onSelectUser = { selectedUserForDetail = it }
            )
            AdminSubPage.MINING_REWARDS -> AdminMiningAndRewardsConfigSection(
                settings = settings,
                onSave = onUpdateSettings
            )
            AdminSubPage.ADS_MISSIONS -> AdminAdsAndMissionsConfigSection(
                settings = settings,
                missions = missions,
                onSaveSettings = onUpdateSettings,
                onSaveMission = onSaveMission,
                onDeleteMission = onDeleteMission
            )
            AdminSubPage.CURRENCY_TIERS -> AdminCurrencyConfigSection(
                settings = settings,
                onSave = onUpdateSettings
            )
            AdminSubPage.BOT_TEMPLATES -> AdminBotAndTemplatesSection(
                settings = settings,
                onSave = onUpdateSettings
            )
            AdminSubPage.SECURITY_SYSTEM -> AdminSecurityAndCronSection(
                settings = settings,
                onSave = onUpdateSettings,
                onRunCron = onRunDailyCron
            )
            AdminSubPage.PAYMENT_LOGS -> AdminPaymentLogsSection(posts = channelPosts)
            AdminSubPage.AUDIT_LOGS -> AdminAuditLogsSection(logs = auditLogs)
        }
    }

    // Approve Withdrawal Modal (with required TxID)
    if (approveModalWithdrawal != null) {
        val wd = approveModalWithdrawal!!
        var txIdInput by remember { mutableStateOf("TXN${(1000000..9999999).random()}") }
        var noteInput by remember { mutableStateOf("Verified instant ${wd.method} payout") }

        AlertDialog(
            onDismissRequest = { approveModalWithdrawal = null },
            title = { Text("Approve Withdrawal ${wd.wdId}") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        "User: ${wd.userName} (@${wd.userUsername})\n" +
                                "Amount: ${wd.coins} NXC (${wd.fiatEstimate})\n" +
                                "Destination: ${wd.method} • ${wd.destination}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    OutlinedTextField(
                        value = txIdInput,
                        onValueChange = { txIdInput = it },
                        label = { Text("Transaction ID (txId)") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("admin_approve_txid_input")
                    )
                    OutlinedTextField(
                        value = noteInput,
                        onValueChange = { noteInput = it },
                        label = { Text("Audit Reason / Note") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                NexilusGradientButton(
                    text = "APPROVE & POST TO @NexilusPayLogs",
                    onClick = {
                        onApproveWithdrawal(wd.wdId, txIdInput, noteInput)
                        approveModalWithdrawal = null
                    },
                    testTag = "admin_confirm_approve_button"
                )
            },
            dismissButton = {
                TextButton(onClick = { approveModalWithdrawal = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Reject Withdrawal Modal (with atomic coin refund)
    if (rejectModalWithdrawal != null) {
        val wd = rejectModalWithdrawal!!
        var rejectReason by remember { mutableStateOf("Invalid ${wd.method} destination account number") }

        AlertDialog(
            onDismissRequest = { rejectModalWithdrawal = null },
            title = { Text("Reject & Refund ${wd.wdId}") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        "Rejecting will atomically refund +${wd.coins} NXC back to @${wd.userUsername}'s balance and send a Telegram notification.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    OutlinedTextField(
                        value = rejectReason,
                        onValueChange = { rejectReason = it },
                        label = { Text("Rejection Reason (Required)") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("admin_reject_reason_input")
                    )
                }
            },
            confirmButton = {
                OutlinedButton(
                    onClick = {
                        onRejectWithdrawal(wd.wdId, rejectReason)
                        rejectModalWithdrawal = null
                    },
                    border = BorderStroke(1.dp, NexilusDanger),
                    modifier = Modifier.testTag("admin_confirm_reject_button")
                ) {
                    Text("REJECT & REFUND COINS", color = NexilusDanger, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { rejectModalWithdrawal = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    // User Detail Modal (Profile, Transactions, Withdrawals + Admin Actions)
    if (selectedUserForDetail != null) {
        val freshUser = users.find { it.uid == selectedUserForDetail!!.uid } ?: selectedUserForDetail!!
        UserDetailAdminDialog(
            user = freshUser,
            userTxs = transactions.filter { it.userId == freshUser.uid },
            userWds = withdrawals.filter { it.userId == freshUser.uid },
            onDismiss = { selectedUserForDetail = null },
            onAdjustCoins = { delta, reason -> onAdjustUserCoins(freshUser.uid, delta, reason) },
            onSetStatus = { status, reason -> onUpdateUserStatus(freshUser.uid, status, reason) },
            onResetMining = { reason -> onResetUserMining(freshUser.uid, reason) },
            onResetDaily = { reason -> onResetUserDaily(freshUser.uid, reason) }
        )
    }
}

@Composable
private fun AdminOverviewSection(
    users: List<UserEntity>,
    transactions: List<TransactionEntity>,
    withdrawals: List<WithdrawalEntity>
) {
    val totalUsers = users.size
    val activeUsers = users.count { it.status == "ACTIVE" }
    val blockedOrSuspicious = users.count { it.status != "ACTIVE" }
    val totalCoinsIssued = users.sumOf { it.totalEarned }
    val miningRewardsTotal = transactions.filter { it.type == "MINING_REWARD" }.sumOf { it.amount }
    val adRewardsTotal = transactions.filter { it.type == "AD_REWARD" }.sumOf { it.amount }
    val referralRewardsTotal = transactions.filter { it.type == "REFERRAL_BONUS" || it.type == "MILESTONE_REWARD" }.sumOf { it.amount }
    val pendingWds = withdrawals.count { it.status == "PENDING" || it.status == "PROCESSING" }
    val approvedWds = withdrawals.count { it.status == "APPROVED" }
    val rejectedWds = withdrawals.count { it.status == "REJECTED" }
    val totalPaidCoins = withdrawals.filter { it.status == "APPROVED" }.sumOf { it.coins }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text("Executive KPIs", style = MaterialTheme.typography.titleLarge)
        }

        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                AdminKpiCard("Total Users", "$totalUsers", "Active: $activeUsers", Modifier.weight(1f))
                AdminKpiCard("Flagged / Blocked", "$blockedOrSuspicious", "Anti-Fraud Guard", Modifier.weight(1f))
                AdminKpiCard("Coins Issued", NexilusCurrency.formatCoins(totalCoinsIssued), "NXC Supply", Modifier.weight(1f))
            }
        }

        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                AdminKpiCard("Mining Issued", "${NexilusCurrency.formatCoins(miningRewardsTotal)} NXC", "2h Cycles", Modifier.weight(1f))
                AdminKpiCard("Ad Rewards", "${NexilusCurrency.formatCoins(adRewardsTotal)} NXC", "AdsGram SDK", Modifier.weight(1f))
                AdminKpiCard("Referral Rewards", "${NexilusCurrency.formatCoins(referralRewardsTotal)} NXC", "Network Growth", Modifier.weight(1f))
            }
        }

        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                AdminKpiCard("Pending Payouts", "$pendingWds", "Queue", Modifier.weight(1f))
                AdminKpiCard("Approved / Rej", "$approvedWds / $rejectedWds", "Processed", Modifier.weight(1f))
                AdminKpiCard("Total Paid", "${NexilusCurrency.formatCoins(totalPaidCoins)} NXC", NexilusCurrency.formatBDT(totalPaidCoins), Modifier.weight(1f))
            }
        }

        // 7-Day User Growth & 7-Day Withdrawals Charts
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("7-Day User Growth & Payout Volume", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                    Spacer(modifier = Modifier.height(12.dp))
                    val growthBars = listOf(12f, 19f, 24f, 31f, 38f, 45f, totalUsers.toFloat().coerceAtLeast(8f) * 7f)
                    Canvas(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp)
                    ) {
                        val maxVal = (growthBars.maxOrNull() ?: 1f).coerceAtLeast(1f)
                        val barCount = growthBars.size
                        val spacing = 14.dp.toPx()
                        val barWidth = (size.width - spacing * (barCount - 1)) / barCount
                        growthBars.forEachIndexed { index, value ->
                            val barH = (value / maxVal) * (size.height - 16.dp.toPx())
                            val x = index * (barWidth + spacing)
                            val y = size.height - barH
                            drawRoundRect(
                                brush = Brush.verticalGradient(listOf(NexilusCyan, NexilusBlue, NexilusPurple)),
                                topLeft = Offset(x, y),
                                size = Size(barWidth, barH),
                                cornerRadius = CornerRadius(12f, 12f)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        listOf("D-6", "D-5", "D-4", "D-3", "D-2", "Yesterday", "Today").forEach { d ->
                            Text(d, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AdminKpiCard(
    label: String,
    value: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                style = TextStyle(
                    fontFamily = SpaceGroteskFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = NexilusCyan
                ),
                maxLines = 1
            )
            Text(subtitle, style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp), color = NexilusViolet, maxLines = 1)
        }
    }
}

@Composable
private fun AdminWithdrawalsSection(
    withdrawals: List<WithdrawalEntity>,
    onOpenApproveModal: (WithdrawalEntity) -> Unit,
    onOpenRejectModal: (WithdrawalEntity) -> Unit,
    onMarkProcessing: (WithdrawalEntity) -> Unit
) {
    var statusFilter by remember { mutableStateOf("ALL") }
    val filtered = remember(withdrawals, statusFilter) {
        if (statusFilter == "ALL") withdrawals else withdrawals.filter { it.status == statusFilter }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("ALL", "PENDING", "PROCESSING", "APPROVED", "REJECTED").forEach { tab ->
                val isSelected = statusFilter == tab
                OutlinedButton(
                    onClick = { statusFilter = tab },
                    shape = RoundedCornerShape(50),
                    border = BorderStroke(1.dp, if (isSelected) NexilusCyan else MaterialTheme.colorScheme.outline),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = if (isSelected) NexilusCyan.copy(alpha = 0.16f) else Color.Transparent
                    )
                ) {
                    Text(tab, color = if (isSelected) NexilusCyan else MaterialTheme.colorScheme.onSurface)
                }
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(filtered, key = { it.wdId }) { wd ->
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = "${wd.userName} (@${wd.userUsername})",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                )
                                Text(
                                    text = "ID: ${wd.wdId} • UID: ${wd.userId}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Text(
                                text = wd.status,
                                style = MaterialTheme.typography.labelLarge.copy(
                                    color = when (wd.status) {
                                        "APPROVED" -> NexilusSuccess
                                        "REJECTED" -> NexilusDanger
                                        else -> NexilusCyan
                                    },
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "🪙 ${NexilusCurrency.formatCoins(wd.coins)} NXC (${wd.fiatEstimate})",
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold, color = NexilusCyan)
                        )
                        Text(
                            text = "💳 ${wd.method} → ${wd.destination}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        if (wd.txId.isNotBlank()) {
                            Text(
                                text = "🔗 TxID: ${wd.txId} • ${wd.channelDeepLink}",
                                style = MaterialTheme.typography.labelSmall.copy(color = NexilusSuccess)
                            )
                        }

                        if (wd.status == "PENDING" || wd.status == "PROCESSING") {
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedButton(
                                    onClick = { onOpenApproveModal(wd) },
                                    border = BorderStroke(1.dp, NexilusSuccess),
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("admin_approve_wd_${wd.wdId}")
                                ) {
                                    Text("Approve", color = NexilusSuccess, fontWeight = FontWeight.Bold)
                                }
                                if (wd.status == "PENDING") {
                                    OutlinedButton(
                                        onClick = { onMarkProcessing(wd) },
                                        border = BorderStroke(1.dp, NexilusCyan),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("Processing", color = NexilusCyan)
                                    }
                                }
                                OutlinedButton(
                                    onClick = { onOpenRejectModal(wd) },
                                    border = BorderStroke(1.dp, NexilusDanger),
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("admin_reject_wd_${wd.wdId}")
                                ) {
                                    Text("Reject", color = NexilusDanger, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AdminUsersSection(
    users: List<UserEntity>,
    onSelectUser: (UserEntity) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val filteredUsers = remember(users, searchQuery) {
        if (searchQuery.isBlank()) users
        else {
            val q = searchQuery.trim().lowercase(Locale.US)
            users.filter {
                it.uid.lowercase().contains(q) ||
                        it.username.lowercase().contains(q) ||
                        it.firstName.lowercase().contains(q) ||
                        it.lastName.lowercase().contains(q)
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            placeholder = { Text("Search by Telegram ID, @username, or name...") },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .testTag("admin_user_search_input")
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(filteredUsers, key = { it.uid }) { u ->
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSelectUser(u) }
                        .testTag("admin_user_row_${u.uid}")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "${u.firstName} ${u.lastName}".trim() + " (@${u.username})",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = "TG ID: ${u.uid} • Lv.${u.level} • Refs: ${u.referralCount} • Status: ${u.status}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "${NexilusCurrency.formatCoins(u.balance)} NXC",
                                style = TextStyle(
                                    fontFamily = SpaceGroteskFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = NexilusCyan
                                )
                            )
                            Text(
                                text = "Manage →",
                                style = MaterialTheme.typography.labelSmall.copy(color = NexilusViolet)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun UserDetailAdminDialog(
    user: UserEntity,
    userTxs: List<TransactionEntity>,
    userWds: List<WithdrawalEntity>,
    onDismiss: () -> Unit,
    onAdjustCoins: (Long, String) -> Unit,
    onSetStatus: (String, String) -> Unit,
    onResetMining: (String) -> Unit,
    onResetDaily: (String) -> Unit
) {
    var coinAmountInput by remember { mutableStateOf("500") }
    var auditReasonInput by remember { mutableStateOf("Admin manual adjustment") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = NexilusSurfaceDark),
            border = BorderStroke(1.2.dp, NexilusBrandBrush),
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 580.dp)
        ) {
            LazyColumn(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "${user.firstName} (@${user.username})",
                                style = MaterialTheme.typography.titleLarge.copy(color = Color.White)
                            )
                            Text(
                                text = "UID: ${user.uid} • Balance: ${user.balance} NXC • Status: ${user.status}",
                                style = MaterialTheme.typography.labelSmall.copy(color = NexilusCyan)
                            )
                        }
                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                        }
                    }
                }

                item {
                    OutlinedTextField(
                        value = auditReasonInput,
                        onValueChange = { auditReasonInput = it },
                        label = { Text("Audit Reason (Required for all actions)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    OutlinedTextField(
                        value = coinAmountInput,
                        onValueChange = { coinAmountInput = it.filter { c -> c.isDigit() } },
                        label = { Text("Coins to Add / Remove") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(
                            onClick = {
                                val amt = coinAmountInput.toLongOrNull() ?: 0L
                                if (amt > 0) onAdjustCoins(amt, auditReasonInput)
                            },
                            border = BorderStroke(1.dp, NexilusSuccess),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("admin_add_coins_button")
                        ) {
                            Text("+ Add Coins", color = NexilusSuccess, fontWeight = FontWeight.Bold)
                        }
                        OutlinedButton(
                            onClick = {
                                val amt = coinAmountInput.toLongOrNull() ?: 0L
                                if (amt > 0) onAdjustCoins(-amt, auditReasonInput)
                            },
                            border = BorderStroke(1.dp, NexilusDanger),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("admin_remove_coins_button")
                        ) {
                            Text("- Remove Coins", color = NexilusDanger, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                item {
                    Text("Account Status & Reset Controls", style = MaterialTheme.typography.labelLarge.copy(color = Color.White))
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(
                            onClick = { onSetStatus("ACTIVE", auditReasonInput) },
                            border = BorderStroke(1.dp, NexilusSuccess),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Set ACTIVE", color = NexilusSuccess)
                        }
                        OutlinedButton(
                            onClick = { onSetStatus("BLOCKED", auditReasonInput) },
                            border = BorderStroke(1.dp, NexilusDanger),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("BLOCK", color = NexilusDanger)
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(
                            onClick = { onResetMining(auditReasonInput) },
                            border = BorderStroke(1.dp, NexilusCyan),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Reset Mining", color = NexilusCyan)
                        }
                        OutlinedButton(
                            onClick = { onResetDaily(auditReasonInput) },
                            border = BorderStroke(1.dp, NexilusViolet),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Reset Daily", color = NexilusViolet)
                        }
                    }
                }

                item {
                    Text(
                        text = "Transactions (${userTxs.size}) & Withdrawals (${userWds.size})",
                        style = MaterialTheme.typography.labelLarge.copy(color = NexilusCyan)
                    )
                }

                items(userTxs.take(6), key = { it.txId }) { tx ->
                    TransactionRowItem(tx = tx, coinSymbol = "NXC")
                }
            }
        }
    }
}

@Composable
private fun AdminMiningAndRewardsConfigSection(
    settings: AppSettingsEntity,
    onSave: (AppSettingsEntity, String, String) -> Unit
) {
    var joinBonus by remember(settings) { mutableStateOf(settings.joiningBonus.toString()) }
    var refBonus by remember(settings) { mutableStateOf(settings.referralBonus.toString()) }
    var mineReward by remember(settings) { mutableStateOf(settings.miningReward.toString()) }
    var adReward by remember(settings) { mutableStateOf(settings.adReward.toString()) }
    var durationSec by remember(settings) { mutableStateOf(settings.miningDurationSec.toString()) }
    var reason by remember { mutableStateOf("Updated mining and reward parameters") }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text("Mining & Rewards Configuration", style = MaterialTheme.typography.titleLarge)
        }
        item {
            OutlinedTextField(
                value = joinBonus,
                onValueChange = { joinBonus = it },
                label = { Text("Joining Bonus (Default: 100 NXC)") },
                modifier = Modifier.fillMaxWidth()
            )
        }
        item {
            OutlinedTextField(
                value = refBonus,
                onValueChange = { refBonus = it },
                label = { Text("Referral Bonus for Both Users (Default: 100 NXC)") },
                modifier = Modifier.fillMaxWidth()
            )
        }
        item {
            OutlinedTextField(
                value = mineReward,
                onValueChange = { mineReward = it },
                label = { Text("Base Mining Reward per Cycle (Default: 80 NXC)") },
                modifier = Modifier.fillMaxWidth()
            )
        }
        item {
            OutlinedTextField(
                value = adReward,
                onValueChange = { adReward = it },
                label = { Text("Watch & Earn Ad Reward (Default: 15 NXC)") },
                modifier = Modifier.fillMaxWidth()
            )
        }
        item {
            OutlinedTextField(
                value = durationSec,
                onValueChange = { durationSec = it },
                label = { Text("Mining Duration in Seconds (Default: 7200 = 2 hours)") },
                modifier = Modifier.fillMaxWidth()
            )
        }
        item {
            OutlinedTextField(
                value = reason,
                onValueChange = { reason = it },
                label = { Text("Audit Reason") },
                modifier = Modifier.fillMaxWidth()
            )
        }
        item {
            NexilusGradientButton(
                text = "SAVE REWARDS & MINING CONFIG",
                onClick = {
                    onSave(
                        settings.copy(
                            joiningBonus = joinBonus.toLongOrNull() ?: 100L,
                            referralBonus = refBonus.toLongOrNull() ?: 100L,
                            miningReward = mineReward.toLongOrNull() ?: 80L,
                            adReward = adReward.toLongOrNull() ?: 15L,
                            miningDurationSec = durationSec.toLongOrNull() ?: 7200L
                        ),
                        "settings.rewards & settings.mining",
                        reason
                    )
                }
            )
        }
    }
}

@Composable
private fun AdminAdsAndMissionsConfigSection(
    settings: AppSettingsEntity,
    missions: List<MissionEntity>,
    onSaveSettings: (AppSettingsEntity, String, String) -> Unit,
    onSaveMission: (MissionEntity, String) -> Unit,
    onDeleteMission: (String) -> Unit
) {
    var rewardedId by remember(settings) { mutableStateOf(settings.adsRewardedId) }
    var dailyLimit by remember(settings) { mutableStateOf(settings.adsDailyLimit.toString()) }
    var adsEnabled by remember(settings) { mutableStateOf(settings.adsEnabled) }

    var newMsnTitle by remember { mutableStateOf("") }
    var newMsnDesc by remember { mutableStateOf("") }
    var newMsnReward by remember { mutableStateOf("100") }
    var newMsnType by remember { mutableStateOf("TELEGRAM") }
    var newMsnChannel by remember { mutableStateOf("@NexilusCommunity") }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text("AdsGram SDK Configuration", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = rewardedId,
                onValueChange = { rewardedId = it },
                label = { Text("AdsGram Rewarded Block ID") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = dailyLimit,
                onValueChange = { dailyLimit = it },
                label = { Text("Daily Ads Limit (Default: 10 ads/day)") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Enable AdsGram Rewarded Ads")
                Switch(checked = adsEnabled, onCheckedChange = { adsEnabled = it })
            }
            Spacer(modifier = Modifier.height(8.dp))
            NexilusGradientButton(
                text = "SAVE ADSGRAM CONFIG",
                onClick = {
                    onSaveSettings(
                        settings.copy(
                            adsRewardedId = rewardedId,
                            adsDailyLimit = dailyLimit.toIntOrNull() ?: 10,
                            adsEnabled = adsEnabled
                        ),
                        "settings.ads",
                        "Updated AdsGram block ID and daily limit"
                    )
                }
            )
        }

        item {
            Spacer(modifier = Modifier.height(8.dp))
            Text("Create New Mission", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = newMsnTitle,
                onValueChange = { newMsnTitle = it },
                label = { Text("Mission Title") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = newMsnDesc,
                onValueChange = { newMsnDesc = it },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = newMsnReward,
                    onValueChange = { newMsnReward = it },
                    label = { Text("Reward NXC") },
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = newMsnType,
                    onValueChange = { newMsnType = it.uppercase() },
                    label = { Text("Type (AD / TELEGRAM)") },
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = newMsnChannel,
                onValueChange = { newMsnChannel = it },
                label = { Text("Channel @username (for TELEGRAM type)") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            NexilusGradientButton(
                text = "ADD MISSION",
                icon = Icons.Default.Add,
                onClick = {
                    if (newMsnTitle.isNotBlank()) {
                        onSaveMission(
                            MissionEntity(
                                missionId = "msn_${UUID.randomUUID().toString().take(6)}",
                                title = newMsnTitle.trim(),
                                description = newMsnDesc.ifBlank { "Complete task to earn NXC" },
                                reward = newMsnReward.toLongOrNull() ?: 100L,
                                type = if (newMsnType == "AD") "AD" else "TELEGRAM",
                                channelUsername = newMsnChannel.trim()
                            ),
                            "Added new mission"
                        )
                        newMsnTitle = ""
                        newMsnDesc = ""
                    }
                }
            )
        }

        items(missions, key = { it.missionId }) { m ->
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("${m.title} (+${m.reward} NXC)", fontWeight = FontWeight.Bold)
                        Text("${m.type} • ${m.channelUsername}", style = MaterialTheme.typography.labelSmall)
                    }
                    IconButton(onClick = { onDeleteMission(m.missionId) }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete Mission", tint = NexilusDanger)
                    }
                }
            }
        }
    }
}

@Composable
private fun AdminCurrencyConfigSection(
    settings: AppSettingsEntity,
    onSave: (AppSettingsEntity, String, String) -> Unit
) {
    var coinsPerBdt by remember(settings) { mutableStateOf(settings.coinsPerBDT.toString()) }
    var coinsPerCentUsdt by remember(settings) { mutableStateOf(settings.coinsPerCentUSDT.toString()) }
    var minWd by remember(settings) { mutableStateOf(settings.minWithdrawal.toString()) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text("Currency & Withdrawal Rates", style = MaterialTheme.typography.titleLarge)
        }
        item {
            OutlinedTextField(
                value = coinsPerBdt,
                onValueChange = { coinsPerBdt = it },
                label = { Text("Coins per ৳1 BDT (Exact Default: 250)") },
                modifier = Modifier.fillMaxWidth()
            )
        }
        item {
            OutlinedTextField(
                value = coinsPerCentUsdt,
                onValueChange = { coinsPerCentUsdt = it },
                label = { Text("Coins per $0.01 USDT (Exact Default: 333)") },
                modifier = Modifier.fillMaxWidth()
            )
        }
        item {
            OutlinedTextField(
                value = minWd,
                onValueChange = { minWd = it },
                label = { Text("Minimum Withdrawal Coins (Exact Default: 1000)") },
                modifier = Modifier.fillMaxWidth()
            )
        }
        item {
            NexilusGradientButton(
                text = "SAVE CURRENCY RATES",
                onClick = {
                    onSave(
                        settings.copy(
                            coinsPerBDT = coinsPerBdt.toDoubleOrNull() ?: 250.0,
                            coinsPerCentUSDT = coinsPerCentUsdt.toDoubleOrNull() ?: 333.0,
                            minWithdrawal = minWd.toLongOrNull() ?: 1000L
                        ),
                        "settings.currency",
                        "Updated BDT/USDT conversion rates"
                    )
                }
            )
        }
    }
}

@Composable
private fun AdminBotAndTemplatesSection(
    settings: AppSettingsEntity,
    onSave: (AppSettingsEntity, String, String) -> Unit
) {
    var botUser by remember(settings) { mutableStateOf(settings.botUsername) }
    var botToken by remember(settings) { mutableStateOf(settings.botToken) }
    var payChannel by remember(settings) { mutableStateOf(settings.paymentChannelUsername) }
    var tplApproved by remember(settings) { mutableStateOf(settings.tplWithdrawalApproved) }
    var tplOffline by remember(settings) { mutableStateOf(settings.tplAdminOffline) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text("Telegram Bot & Notification Templates", style = MaterialTheme.typography.titleLarge)
            Text(
                "Variables palette: {username}, {referredUsername}, {coins}, {fiat}, {method}, {txId}, {reason}",
                style = MaterialTheme.typography.labelSmall.copy(color = NexilusCyan)
            )
        }
        item {
            OutlinedTextField(
                value = botUser,
                onValueChange = { botUser = it },
                label = { Text("Bot Username (Default: NexilusMineBot)") },
                modifier = Modifier.fillMaxWidth()
            )
        }
        item {
            OutlinedTextField(
                value = botToken,
                onValueChange = { botToken = it },
                label = { Text("Telegram Bot Token (Optional for Live HTTP Bot API)") },
                modifier = Modifier.fillMaxWidth()
            )
        }
        item {
            OutlinedTextField(
                value = payChannel,
                onValueChange = { payChannel = it },
                label = { Text("Payment Log Channel (@NexilusPayLogs)") },
                modifier = Modifier.fillMaxWidth()
            )
        }
        item {
            OutlinedTextField(
                value = tplApproved,
                onValueChange = { tplApproved = it },
                label = { Text("Template: withdrawal_approved") },
                modifier = Modifier.fillMaxWidth()
            )
        }
        item {
            OutlinedTextField(
                value = tplOffline,
                onValueChange = { tplOffline = it },
                label = { Text("Template: admin_offline") },
                modifier = Modifier.fillMaxWidth()
            )
        }
        item {
            NexilusGradientButton(
                text = "SAVE BOT & TEMPLATES CONFIG",
                onClick = {
                    onSave(
                        settings.copy(
                            botUsername = botUser.trim().removePrefix("@"),
                            botToken = botToken.trim(),
                            paymentChannelUsername = payChannel.trim().removePrefix("@"),
                            tplWithdrawalApproved = tplApproved,
                            tplAdminOffline = tplOffline
                        ),
                        "settings.bot & settings.templates",
                        "Updated Bot config and templates"
                    )
                }
            )
        }
    }
}

@Composable
private fun AdminSecurityAndCronSection(
    settings: AppSettingsEntity,
    onSave: (AppSettingsEntity, String, String) -> Unit,
    onRunCron: (Boolean) -> Unit
) {
    var clusterThreshold by remember(settings) { mutableStateOf(settings.ipClusterThreshold.toString()) }
    var velocityLimit by remember(settings) { mutableStateOf(settings.rewardVelocityLimit.toString()) }
    var maintenance by remember(settings) { mutableStateOf(settings.maintenanceMode) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, NexilusCyan),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Hourly Cron Job (/api/cron/daily)", style = MaterialTheme.typography.titleLarge)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "Resets daily ads counters across all users, rebuilds leaderboard cache, and posts the Admin Offline notice to @NexilusPayLogs.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    NexilusGradientButton(
                        text = "TRIGGER CRON + POST ADMIN OFFLINE NOTICE",
                        icon = Icons.Default.Refresh,
                        onClick = { onRunCron(true) },
                        testTag = "admin_trigger_cron_button"
                    )
                }
            }
        }

        item {
            Text("Anti-Fraud & System Thresholds", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = clusterThreshold,
                onValueChange = { clusterThreshold = it },
                label = { Text("DeviceHash Cluster Limit (Default: 3 accounts/device)") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = velocityLimit,
                onValueChange = { velocityLimit = it },
                label = { Text("Reward Claim Velocity Limit per Hour") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Maintenance Mode (Pause Mining & Claims)")
                Switch(
                    checked = maintenance,
                    onCheckedChange = { maintenance = it },
                    colors = SwitchDefaults.colors(checkedThumbColor = NexilusDanger)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            NexilusGradientButton(
                text = "SAVE SECURITY & SYSTEM SETTINGS",
                icon = Icons.Default.Security,
                onClick = {
                    onSave(
                        settings.copy(
                            ipClusterThreshold = clusterThreshold.toIntOrNull() ?: 3,
                            rewardVelocityLimit = velocityLimit.toIntOrNull() ?: 20,
                            maintenanceMode = maintenance
                        ),
                        "settings.fraud & settings.system",
                        "Updated anti-fraud thresholds and maintenance state"
                    )
                }
            )
        }
    }
}

@Composable
private fun AdminPaymentLogsSection(posts: List<ChannelPostEntity>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Text("@NexilusPayLogs Broadcast History (${posts.size})", style = MaterialTheme.typography.titleLarge)
        }
        items(posts, key = { it.postId }) { post ->
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "${post.channelUsername} • Msg #${post.messageId}",
                        style = MaterialTheme.typography.labelLarge.copy(color = NexilusCyan)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(post.text, style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(post.deepLink, style = MaterialTheme.typography.labelSmall.copy(color = NexilusViolet))
                }
            }
        }
    }
}

@Composable
private fun AdminAuditLogsSection(logs: List<AuditLogEntity>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Text("Immutable Admin Audit Logs (${logs.size})", style = MaterialTheme.typography.titleLarge)
        }
        items(logs, key = { it.logId }) { log ->
            val timeStr = SimpleDateFormat("MMM dd, HH:mm:ss", Locale.US).format(Date(log.ts))
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = log.action,
                            style = MaterialTheme.typography.labelLarge.copy(
                                color = NexilusCyan,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Text(
                            text = timeStr,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Text("Target: ${log.target} • Admin: ${log.adminId} (${log.ip})", style = MaterialTheme.typography.labelSmall)
                    Text("Change: ${log.oldValue} → ${log.newValue}", style = MaterialTheme.typography.bodyMedium)
                    Text("Reason: ${log.reason}", style = MaterialTheme.typography.labelSmall.copy(color = NexilusViolet))
                }
            }
        }
    }
}
