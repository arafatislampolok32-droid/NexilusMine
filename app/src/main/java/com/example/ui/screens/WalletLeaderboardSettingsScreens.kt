package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.WarningAmber
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
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppSettingsEntity
import com.example.data.model.NexilusConstants
import com.example.data.model.NexilusCurrency
import com.example.data.model.UserEntity
import com.example.data.model.WithdrawalEntity
import com.example.ui.components.LeaderboardPodium
import com.example.ui.components.NexilusGradientButton
import com.example.ui.theme.NexilusBrandBrush
import com.example.ui.theme.NexilusCyan
import com.example.ui.theme.NexilusDanger
import com.example.ui.theme.NexilusHorizontalBrush
import com.example.ui.theme.NexilusSecondarySurfaceDark
import com.example.ui.theme.NexilusSuccess
import com.example.ui.theme.NexilusSurfaceDark
import com.example.ui.theme.NexilusViolet
import com.example.ui.theme.NotoSansBengaliFontFamily
import com.example.ui.theme.SpaceGroteskFontFamily
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun WalletScreen(
    user: UserEntity,
    settings: AppSettingsEntity,
    withdrawals: List<WithdrawalEntity>,
    language: String,
    onSubmitWithdrawal: (Long, String, String, () -> Unit) -> Unit
) {
    var selectedMethod by remember { mutableStateOf("BINANCE") } // BINANCE or BKASH
    var selectedTier by remember { mutableLongStateOf(1000L) }
    var destinationInput by remember { mutableStateOf("") }
    var isRechecked by remember { mutableStateOf(false) }

    val isUnlocked = user.balance >= settings.minWithdrawal
    val isValidDestination = remember(selectedMethod, destinationInput) {
        val trimmed = destinationInput.trim()
        if (selectedMethod == "BINANCE") {
            NexilusConstants.BINANCE_UID_REGEX.matches(trimmed)
        } else {
            NexilusConstants.BKASH_NUMBER_REGEX.matches(trimmed)
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("wallet_screen_list"),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Profile Mini Card + Balance + BDT + USDT
        item {
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.2.dp, NexilusBrandBrush),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(CircleShape)
                                    .background(NexilusBrandBrush),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = user.firstName.take(1).uppercase(),
                                    style = TextStyle(
                                        fontFamily = SpaceGroteskFontFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp,
                                        color = Color.White
                                    )
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "${user.firstName} ${user.lastName}".trim(),
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                )
                                Text(
                                    text = "@${user.username} • TG ID: ${user.uid}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(
                                    if (isUnlocked) NexilusSuccess.copy(alpha = 0.16f)
                                    else NexilusDanger.copy(alpha = 0.16f)
                                )
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Text(
                                text = if (isUnlocked) "WITHDRAWAL READY" else "LOCKED (<${settings.minWithdrawal})",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (isUnlocked) NexilusSuccess else NexilusDanger
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "${NexilusCurrency.formatCoins(user.balance)} ${settings.coinSymbol}",
                        style = TextStyle(
                            fontFamily = SpaceGroteskFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 32.sp,
                            brush = NexilusHorizontalBrush
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        FiatEstimateBox(
                            title = "BDT Estimate (250 NXC = ৳1)",
                            value = NexilusCurrency.formatBDT(user.balance, settings.coinsPerBDT),
                            modifier = Modifier.weight(1f)
                        )
                        FiatEstimateBox(
                            title = "USDT Estimate (333 NXC = $0.01)",
                            value = NexilusCurrency.formatUSDT(user.balance, settings.coinsPerCentUSDT),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // Withdrawal Form Card
        item {
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("withdrawal_form_card")
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = if (language == "BN") "উত্তোলন ফর্ম (Withdrawal Request)" else "Request Instant Withdrawal",
                        style = MaterialTheme.typography.titleLarge
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Select payout gateway, tier amount, and verify your destination account.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Method Select: BINANCE & BKASH
                    Text(
                        text = "1. Select Payout Method",
                        style = MaterialTheme.typography.labelLarge
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        MethodSelectionCard(
                            name = "BINANCE",
                            subtitle = "Binance Pay UID (USDT)",
                            icon = Icons.Default.AccountBalance,
                            selected = selectedMethod == "BINANCE",
                            onClick = {
                                selectedMethod = "BINANCE"
                                isRechecked = false
                            },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("method_select_binance")
                        )
                        MethodSelectionCard(
                            name = "BKASH",
                            subtitle = "bKash Personal (BDT)",
                            icon = Icons.Default.PhoneAndroid,
                            selected = selectedMethod == "BKASH",
                            onClick = {
                                selectedMethod = "BKASH"
                                isRechecked = false
                            },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("method_select_bkash")
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Amount Tiers: 1K, 2K, 5K, 10K
                    Text(
                        text = "2. Select Amount Tier",
                        style = MaterialTheme.typography.labelLarge
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        NexilusConstants.WITHDRAWAL_TIERS.forEach { tier ->
                            val label = "${tier / 1000}K"
                            val isSelected = selectedTier == tier
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .heightIn(min = 48.dp)
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
                                    .border(
                                        1.dp,
                                        if (isSelected) NexilusCyan else Color.Transparent,
                                        RoundedCornerShape(12.dp)
                                    )
                                    .clickable {
                                        selectedTier = tier
                                        isRechecked = false
                                    }
                                    .padding(vertical = 8.dp)
                                    .testTag("tier_select_$tier"),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = label,
                                        style = TextStyle(
                                            fontFamily = SpaceGroteskFontFamily,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp,
                                            color = Color.White
                                        )
                                    )
                                    Text(
                                        text = if (selectedMethod == "BKASH") {
                                            NexilusCurrency.formatBDT(tier, settings.coinsPerBDT).replace(" BDT", "")
                                        } else {
                                            NexilusCurrency.formatUSDT(tier, settings.coinsPerCentUSDT).replace(" USDT", "")
                                        },
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontSize = 10.sp,
                                            color = if (isSelected) Color.White else NexilusCyan
                                        )
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Destination Input with Format Validation
                    Text(
                        text = if (selectedMethod == "BINANCE") {
                            "3. Enter Binance UID (6–20 digits)"
                        } else {
                            "3. Enter bKash Personal Number (11 digits, 013–019)"
                        },
                        style = MaterialTheme.typography.labelLarge
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = destinationInput,
                        onValueChange = {
                            destinationInput = it
                            isRechecked = false
                        },
                        placeholder = {
                            Text(
                                if (selectedMethod == "BINANCE") "e.g. 482910492" else "e.g. 01712345678"
                            )
                        },
                        singleLine = true,
                        isError = destinationInput.isNotBlank() && !isValidDestination,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("withdrawal_destination_input")
                    )
                    if (destinationInput.isNotBlank() && !isValidDestination) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (selectedMethod == "BINANCE") {
                                "Format invalid: Binance UID must match /^\\d{6,20}$/"
                            } else {
                                "Format invalid: bKash number must match /^01[3-9]\\d{8}$/"
                            },
                            style = MaterialTheme.typography.labelSmall.copy(color = NexilusDanger)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Exact Mandatory Bangla Warning Message
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(NexilusViolet.copy(alpha = 0.14f))
                            .border(1.dp, NexilusViolet.copy(alpha = 0.45f), RoundedCornerShape(12.dp))
                            .padding(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.Top) {
                            Icon(
                                imageVector = Icons.Default.WarningAmber,
                                contentDescription = null,
                                tint = NexilusViolet,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "একবার উত্তোলন রিকোয়েস্ট করা হলে পুনরায় সেটা এডিট করা যাবে না। তাই আপনার Binance UID / bKash নাম্বার সঠিকভাবে প্রদান করুন।",
                                style = TextStyle(
                                    fontFamily = NotoSansBengaliFontFamily,
                                    fontSize = 12.sp,
                                    lineHeight = 18.sp,
                                    color = Color.White
                                ),
                                modifier = Modifier.testTag("withdrawal_bangla_warning")
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    if (!isUnlocked) {
                        NexilusGradientButton(
                            text = "WITHDRAWAL LOCKED (MIN ${settings.minWithdrawal} NXC)",
                            onClick = {},
                            enabled = false,
                            icon = Icons.Default.Lock,
                            testTag = "withdrawal_locked_button"
                        )
                    } else if (!isRechecked) {
                        NexilusGradientButton(
                            text = "RECHECK DETAILS (${NexilusCurrency.formatCoins(selectedTier)} NXC)",
                            onClick = { isRechecked = true },
                            enabled = isValidDestination && user.balance >= selectedTier,
                            icon = Icons.Default.VerifiedUser,
                            testTag = "recheck_withdrawal_button"
                        )
                    } else {
                        // Confirmation Recheck Step
                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = NexilusSecondarySurfaceDark),
                            border = BorderStroke(1.dp, NexilusCyan),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "Confirm Escrow & Payout Details:",
                                    style = MaterialTheme.typography.labelLarge.copy(color = NexilusCyan)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "• Method: $selectedMethod\n" +
                                            "• Destination: ${destinationInput.trim()}\n" +
                                            "• Escrow Debit: ${NexilusCurrency.formatCoins(selectedTier)} NXC (${NexilusCurrency.formatBDT(selectedTier, settings.coinsPerBDT)} / ${NexilusCurrency.formatUSDT(selectedTier, settings.coinsPerCentUSDT)})",
                                    style = MaterialTheme.typography.bodyMedium.copy(color = Color.White)
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    OutlinedButton(
                                        onClick = { isRechecked = false },
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier
                                            .weight(1f)
                                            .heightIn(min = 48.dp)
                                    ) {
                                        Text("EDIT", color = Color.White)
                                    }
                                    NexilusGradientButton(
                                        text = "SUBMIT WITHDRAWAL",
                                        onClick = {
                                            onSubmitWithdrawal(selectedTier, selectedMethod, destinationInput) {
                                                destinationInput = ""
                                                isRechecked = false
                                            }
                                        },
                                        icon = Icons.Default.CheckCircle,
                                        modifier = Modifier.weight(1.6f),
                                        testTag = "submit_withdrawal_button"
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Withdrawal History List
        item {
            Text(
                text = "Withdrawal History (${withdrawals.size})",
                style = MaterialTheme.typography.titleLarge
            )
        }

        if (withdrawals.isEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "No withdrawal requests submitted yet.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        } else {
            items(withdrawals, key = { it.wdId }) { wd ->
                WithdrawalHistoryItemCard(wd = wd)
            }
        }
    }
}

@Composable
private fun FiatEstimateBox(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(NexilusSecondarySurfaceDark)
            .padding(12.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = value,
            style = TextStyle(
                fontFamily = SpaceGroteskFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = NexilusCyan
            )
        )
    }
}

@Composable
private fun MethodSelectionCard(
    name: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) NexilusSecondarySurfaceDark else MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(
            width = if (selected) 1.5.dp else 1.dp,
            brush = if (selected) NexilusBrandBrush else Brush.horizontalGradient(
                listOf(
                    MaterialTheme.colorScheme.outline,
                    MaterialTheme.colorScheme.outline
                )
            )
        ),
        modifier = modifier.clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(if (selected) NexilusBrandBrush else Brush.horizontalGradient(listOf(NexilusSurfaceDark, NexilusSurfaceDark))),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = name,
                    tint = if (selected) Color.White else NexilusCyan,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = name,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun WithdrawalHistoryItemCard(wd: WithdrawalEntity) {
    val statusColor = when (wd.status) {
        "APPROVED" -> NexilusSuccess
        "REJECTED" -> NexilusDanger
        "PROCESSING" -> NexilusCyan
        else -> NexilusViolet
    }
    val dateStr = SimpleDateFormat("MMM dd, HH:mm", Locale.US).format(Date(wd.createdAt))

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, statusColor.copy(alpha = 0.4f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${wd.method} • ${wd.destination}",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(statusColor.copy(alpha = 0.16f))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = wd.status,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = statusColor,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${NexilusCurrency.formatCoins(wd.coins)} NXC • ${wd.fiatEstimate}",
                style = MaterialTheme.typography.bodyMedium.copy(color = NexilusCyan, fontWeight = FontWeight.SemiBold)
            )
            if (wd.txId.isNotBlank()) {
                Text(
                    text = "🔗 TxID: ${wd.txId} (${wd.channelDeepLink})",
                    style = MaterialTheme.typography.labelSmall.copy(color = NexilusSuccess)
                )
            }
            if (wd.adminNote.isNotBlank()) {
                Text(
                    text = "Note: ${wd.adminNote}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = "ID: ${wd.wdId} • $dateStr",
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )
        }
    }
}

// ─── LEADERBOARD SCREEN (Top Referrers & Top Earners Tabs + Podium + List) ───
@Composable
fun LeaderboardScreen(
    topReferrers: List<UserEntity>,
    topEarners: List<UserEntity>,
    currentUserUid: String,
    onBack: () -> Unit
) {
    BackHandler(onBack = onBack)

    var selectedTab by remember { mutableStateOf("REFERRERS") } // REFERRERS or EARNERS
    val activeList = if (selectedTab == "REFERRERS") topReferrers else topEarners

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("leaderboard_screen")
    ) {
        // Sub-header with Back button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.testTag("leaderboard_back_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "Nexilus Global Leaderboard",
                style = MaterialTheme.typography.headlineMedium
            )
        }

        // Two Tabs: Top Referrers, Top Earners
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            listOf("REFERRERS" to "Top Referrers", "EARNERS" to "Top Earners").forEach { (key, title) ->
                val isSelected = selectedTab == key
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(min = 48.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            if (isSelected) NexilusHorizontalBrush
                            else Brush.horizontalGradient(
                                listOf(
                                    NexilusSecondarySurfaceDark,
                                    NexilusSecondarySurfaceDark
                                )
                            )
                        )
                        .clickable { selectedTab = key }
                        .padding(vertical = 10.dp)
                        .testTag("leaderboard_tab_${key.lowercase()}"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                Card(
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.2.dp, NexilusBrandBrush),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        LeaderboardPodium(
                            topThree = activeList.take(3),
                            metricLabel = { u ->
                                if (selectedTab == "REFERRERS") "${u.referralCount} Referrals"
                                else "${NexilusCurrency.formatCoins(u.totalEarned)} NXC"
                            }
                        )
                    }
                }
            }

            itemsIndexed(activeList.drop(3), key = { _, u -> u.uid }) { idx, u ->
                val rank = idx + 4
                val isCurrentUser = u.uid == currentUserUid
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isCurrentUser) NexilusSecondarySurfaceDark else MaterialTheme.colorScheme.surface
                    ),
                    border = BorderStroke(
                        1.dp,
                        if (isCurrentUser) NexilusCyan else MaterialTheme.colorScheme.outline
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "#$rank",
                                style = TextStyle(
                                    fontFamily = SpaceGroteskFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = NexilusCyan
                                ),
                                modifier = Modifier.width(36.dp)
                            )
                            Column {
                                Text(
                                    text = "${u.firstName} ${u.lastName}".trim() + if (isCurrentUser) " (You)" else "",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                )
                                Text(
                                    text = "@${u.username}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Text(
                            text = if (selectedTab == "REFERRERS") {
                                "${u.referralCount} Referrals"
                            } else {
                                "${NexilusCurrency.formatCoins(u.totalEarned)} NXC"
                            },
                            style = TextStyle(
                                fontFamily = SpaceGroteskFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = NexilusViolet
                            )
                        )
                    }
                }
            }
        }
    }
}

// ─── SETTINGS SCREEN (Theme, Currency, Language, Toggles, Legal — STRICTLY NO LOGOUT BUTTON) ───
@Composable
fun SettingsScreen(
    isDarkTheme: Boolean,
    selectedCurrency: String,
    selectedLanguage: String,
    notificationsEnabled: Boolean,
    soundVibrationEnabled: Boolean,
    botUsername: String,
    onSetDarkTheme: (Boolean) -> Unit,
    onSetCurrency: (String) -> Unit,
    onSetLanguage: (String) -> Unit,
    onSetNotifications: (Boolean) -> Unit,
    onSetSoundVibration: (Boolean) -> Unit,
    onBack: () -> Unit
) {
    BackHandler(onBack = onBack)

    val context = LocalContext.current
    var infoModalTitle by remember { mutableStateOf<String?>(null) }
    var infoModalBody by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("settings_screen")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.testTag("settings_back_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = if (selectedLanguage == "BN") "সেটিংস (Settings)" else "Settings & Preferences",
                style = MaterialTheme.typography.headlineMedium
            )
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Theme (Dark / Light)
            item {
                PreferenceSelectorCard(
                    title = "Appearance Theme",
                    subtitle = "Switch between Quantum Dark (#050B1F) and Soft Blue Light mode",
                    options = listOf("DARK" to "Dark Mode", "LIGHT" to "Light Mode"),
                    selectedKey = if (isDarkTheme) "DARK" else "LIGHT",
                    onSelect = { onSetDarkTheme(it == "DARK") }
                )
            }

            // Currency (BDT / USDT)
            item {
                PreferenceSelectorCard(
                    title = "Display Currency",
                    subtitle = "250 NXC = ৳1 BDT • 333 NXC = $0.01 USDT",
                    options = listOf("BDT" to "BDT (৳ Taka)", "USDT" to "USDT ($ Dollar)"),
                    selectedKey = selectedCurrency,
                    onSelect = onSetCurrency
                )
            }

            // Language (Bangla / English)
            item {
                PreferenceSelectorCard(
                    title = "App Language (ভাষা)",
                    subtitle = "Select primary interface language",
                    options = listOf("EN" to "English", "BN" to "বাংলা (Bangla)"),
                    selectedKey = selectedLanguage,
                    onSelect = onSetLanguage
                )
            }

            // Notifications & Haptics Toggles
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Mining & Payout Notifications", style = MaterialTheme.typography.titleMedium)
                                Text(
                                    "Receive alerts when 2-hour mining cycles finish",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Switch(
                                checked = notificationsEnabled,
                                onCheckedChange = onSetNotifications,
                                colors = SwitchDefaults.colors(checkedThumbColor = NexilusCyan),
                                modifier = Modifier.testTag("toggle_notifications")
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Sound & Haptic Feedback", style = MaterialTheme.typography.titleMedium)
                                Text(
                                    "Tactile vibration on mining claims and task completions",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Switch(
                                checked = soundVibrationEnabled,
                                onCheckedChange = onSetSoundVibration,
                                colors = SwitchDefaults.colors(checkedThumbColor = NexilusCyan),
                                modifier = Modifier.testTag("toggle_haptics")
                            )
                        }
                    }
                }
            }

            // Terms, Privacy, Support, App Version (NO LOGOUT BUTTON)
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        SettingsLinkRow(
                            label = "Terms & Conditions",
                            onClick = {
                                infoModalTitle = "Nexilus Mine Terms & Conditions"
                                infoModalBody = "1. One Telegram account per device is permitted.\n2. Self-referrals or emulator clustering are automatically flagged by server-side anti-fraud.\n3. Minimum withdrawal is 1,000 Nexilus Coins via verified Binance UID or bKash Personal number.\n4. Once submitted, withdrawal destination numbers cannot be edited."
                            }
                        )
                        SettingsLinkRow(
                            label = "Privacy Policy",
                            onClick = {
                                infoModalTitle = "Nexilus Mine Privacy Policy"
                                infoModalBody = "We authenticate exclusively via Telegram WebApp HMAC-SHA256 verification. Your Telegram UID, username, and anti-fraud deviceHash are used strictly to secure your Nexilus Coin ledger and process payouts."
                            }
                        )
                        SettingsLinkRow(
                            label = "Official Support (@$botUsername)",
                            onClick = {
                                try {
                                    context.startActivity(
                                        Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/$botUsername"))
                                    )
                                } catch (_: Exception) {
                                }
                            }
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "App Version",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "v1.0.0 (Nexilus Production)",
                                style = MaterialTheme.typography.labelLarge.copy(color = NexilusCyan)
                            )
                        }
                    }
                }
            }
        }
    }

    if (infoModalTitle != null) {
        AlertDialog(
            onDismissRequest = { infoModalTitle = null },
            title = { Text(infoModalTitle ?: "") },
            text = { Text(infoModalBody) },
            confirmButton = {
                TextButton(onClick = { infoModalTitle = null }) {
                    Text("Close", color = NexilusCyan)
                }
            }
        )
    }
}

@Composable
private fun PreferenceSelectorCard(
    title: String,
    subtitle: String,
    options: List<Pair<String, String>>,
    selectedKey: String,
    onSelect: (String) -> Unit
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = title, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                options.forEach { (key, label) ->
                    val isSelected = selectedKey == key
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .heightIn(min = 48.dp)
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
                            .clickable { onSelect(key) }
                            .padding(vertical = 10.dp)
                            .testTag("pref_option_${key.lowercase()}"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelLarge.copy(
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingsLinkRow(
    label: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 48.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(NexilusSecondarySurfaceDark.copy(alpha = 0.6f))
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium))
        Icon(
            imageVector = Icons.AutoMirrored.Filled.OpenInNew,
            contentDescription = label,
            tint = NexilusCyan,
            modifier = Modifier.size(18.dp)
        )
    }
}
