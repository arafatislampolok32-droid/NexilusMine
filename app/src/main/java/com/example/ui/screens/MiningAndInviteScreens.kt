package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.GroupAdd
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppSettingsEntity
import com.example.data.model.MINING_LEVELS
import com.example.data.model.NexilusCurrency
import com.example.data.model.REFERRAL_MILESTONES
import com.example.data.model.TransactionEntity
import com.example.data.model.UserEntity
import com.example.ui.components.GradientProgressBar
import com.example.ui.components.LeaderboardPodium
import com.example.ui.components.NexilusGradientButton
import com.example.ui.theme.NexilusBlue
import com.example.ui.theme.NexilusBrandBrush
import com.example.ui.theme.NexilusCyan
import com.example.ui.theme.NexilusGradientColors
import com.example.ui.theme.NexilusHorizontalBrush
import com.example.ui.theme.NexilusPurple
import com.example.ui.theme.NexilusSecondarySurfaceDark
import com.example.ui.theme.NexilusSuccess
import com.example.ui.theme.NexilusViolet
import com.example.ui.theme.SpaceGroteskFontFamily

@Composable
fun MiningScreen(
    user: UserEntity,
    settings: AppSettingsEntity,
    remainingSec: Long,
    miningHistory: List<TransactionEntity>,
    cycleTokenPreview: String,
    language: String,
    onStartMining: () -> Unit,
    onClaimMining: () -> Unit,
    onFastForwardDemo: () -> Unit
) {
    val levelSpec = user.getLevelSpec()
    val nextReward = Math.round(settings.miningReward * levelSpec.multiplier)
    val totalSec = settings.miningDurationSec.coerceAtLeast(1L)
    val progressFraction = when (user.miningStatus) {
        "ACTIVE" -> ((totalSec - remainingSec).coerceAtLeast(0L).toFloat() / totalSec.toFloat()).coerceIn(0f, 1f)
        "READY" -> 1f
        else -> 0f
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("mining_screen_list"),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Level & Multiplier Header Card
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
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
                        Column {
                            Text(
                                text = "MINING RIG EVOLUTION",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = NexilusCyan,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                )
                            )
                            Text(
                                text = "Level ${levelSpec.level} • ${levelSpec.titleEn}",
                                style = MaterialTheme.typography.headlineMedium
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(NexilusHorizontalBrush)
                                .padding(horizontal = 14.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "Multiplier x${levelSpec.multiplier}",
                                style = MaterialTheme.typography.labelLarge.copy(
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    val lvProgress = ((user.totalMines - levelSpec.minMinesRequired).toFloat() /
                            (levelSpec.nextLevelMines - levelSpec.minMinesRequired).coerceAtLeast(1).toFloat()).coerceIn(0f, 1f)
                    GradientProgressBar(progress = lvProgress, height = 8.dp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Completed Cycles: ${user.totalMines} • Next Evolution at ${levelSpec.nextLevelMines} Cycles",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Big Circular Countdown Reactor Core
        item {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier.size(220.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val strokeW = 14.dp.toPx()
                            val diameter = size.minDimension - strokeW * 2
                            val topLeft = Offset((size.width - diameter) / 2f, (size.height - diameter) / 2f)

                            // Subtle radial glow
                            drawCircle(
                                brush = Brush.radialGradient(
                                    listOf(
                                        NexilusCyan.copy(alpha = 0.16f),
                                        NexilusPurple.copy(alpha = 0.08f),
                                        Color.Transparent
                                    )
                                ),
                                radius = size.minDimension / 2f
                            )

                            // Track ring
                            drawArc(
                                color = Color(0xFF1E2D56),
                                startAngle = -90f,
                                sweepAngle = 360f,
                                useCenter = false,
                                topLeft = topLeft,
                                size = Size(diameter, diameter),
                                style = Stroke(width = strokeW, cap = StrokeCap.Round)
                            )

                            // Gradient Progress Arc
                            drawArc(
                                brush = Brush.sweepGradient(NexilusGradientColors),
                                startAngle = -90f,
                                sweepAngle = 360f * progressFraction.coerceAtLeast(0.04f),
                                useCenter = false,
                                topLeft = topLeft,
                                size = Size(diameter, diameter),
                                style = Stroke(width = strokeW, cap = StrokeCap.Round)
                            )
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = when (user.miningStatus) {
                                    "ACTIVE" -> "MINING ACTIVE"
                                    "READY" -> "READY TO CLAIM"
                                    else -> "IDLE NODE"
                                },
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = if (user.miningStatus == "READY") NexilusSuccess else NexilusCyan,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = when (user.miningStatus) {
                                    "ACTIVE" -> NexilusCurrency.formatCountdown(remainingSec)
                                    "READY" -> "00:00:00"
                                    else -> NexilusCurrency.formatCountdown(settings.miningDurationSec)
                                },
                                style = TextStyle(
                                    fontFamily = SpaceGroteskFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 32.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "+$nextReward ${settings.coinSymbol}",
                                style = TextStyle(
                                    fontFamily = SpaceGroteskFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp,
                                    color = NexilusViolet
                                )
                            )
                        }
                    }

                    if (user.miningCycleId.isNotBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "HMAC CycleToken: $cycleTokenPreview",
                            style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    when (user.miningStatus) {
                        "IDLE" -> {
                            NexilusGradientButton(
                                text = "START 2-HOUR MINING CYCLE (+$nextReward NXC)",
                                onClick = onStartMining,
                                icon = Icons.Default.Bolt,
                                testTag = "mining_screen_start_button"
                            )
                        }
                        "READY" -> {
                            NexilusGradientButton(
                                text = "VERIFY AD & CLAIM +$nextReward NXC",
                                onClick = onClaimMining,
                                icon = Icons.Default.CheckCircle,
                                testTag = "mining_screen_claim_button"
                            )
                        }
                        else -> {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                OutlinedButton(
                                    onClick = onFastForwardDemo,
                                    shape = RoundedCornerShape(14.dp),
                                    border = BorderStroke(1.dp, NexilusCyan),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = NexilusCyan),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .heightIn(min = 48.dp)
                                        .testTag("mining_screen_fast_forward_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.FastForward,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("FAST-FORWARD TO READY (TEST CLAIM)", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Level Evolution List (Starter, Developed, Refined, Advanced)
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Level Evolution Tiers",
                        style = MaterialTheme.typography.titleLarge
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    MINING_LEVELS.forEach { tier ->
                        val isUnlocked = user.totalMines >= tier.minMinesRequired
                        val isCurrent = levelSpec.level == tier.level
                        val tierReward = Math.round(settings.miningReward * tier.multiplier)

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    if (isCurrent) NexilusHorizontalBrush
                                    else Brush.horizontalGradient(
                                        listOf(
                                            NexilusSecondarySurfaceDark,
                                            NexilusSecondarySurfaceDark
                                        )
                                    )
                                )
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Lv.${tier.level} ${tier.titleEn} (${tier.titleBn})",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                )
                                Text(
                                    text = "Requires ${tier.minMinesRequired}+ Completed Cycles",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = if (isCurrent) Color.White.copy(alpha = 0.85f) else Color(0xFF94A3B8)
                                    )
                                )
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "x${tier.multiplier} (+$tierReward NXC)",
                                    style = TextStyle(
                                        fontFamily = SpaceGroteskFontFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = if (isCurrent) Color.White else NexilusCyan
                                    )
                                )
                                Text(
                                    text = if (isCurrent) "ACTIVE TIER" else if (isUnlocked) "UNLOCKED" else "LOCKED",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 10.sp,
                                        color = if (isUnlocked) NexilusSuccess else Color(0xFF94A3B8)
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }

        // Mining Rules Card
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Server-Verified Mining Rules",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "• Cycle Duration: 2 Hours (7,200 seconds) synced strictly to UTC server clock.\n" +
                                "• Base Reward: ${settings.miningReward} Nexilus Coins × your active Level Multiplier.\n" +
                                "• Every claim verifies an HMAC-SHA256 cycleToken and AdsGram adProof for zero-cheat idempotency.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Mining History List
        item {
            Text(
                text = "Mining Cycle History (${miningHistory.size})",
                style = MaterialTheme.typography.titleLarge
            )
        }

        items(miningHistory, key = { it.txId }) { tx ->
            TransactionRowItem(tx = tx, coinSymbol = settings.coinSymbol)
        }
    }
}

@Composable
fun InviteScreen(
    user: UserEntity,
    settings: AppSettingsEntity,
    topReferrers: List<UserEntity>,
    language: String,
    onSimulateReferralJoin: (String, Boolean) -> Unit,
    onClaimMilestone: (String) -> Unit,
    onOpenFullLeaderboard: () -> Unit,
    onShowToast: (String) -> Unit
) {
    val context = LocalContext.current
    var simulatedHandle by remember { mutableStateOf("") }
    val referralLink = "https://t.me/${settings.botUsername}?start=${user.referralCode}"
    val userRank = (topReferrers.indexOfFirst { it.uid == user.uid } + 1).let { if (it <= 0) topReferrers.size + 1 else it }

    val nextMilestone = REFERRAL_MILESTONES.firstOrNull { user.referralCount < it.requiredReferrals }
        ?: REFERRAL_MILESTONES.last()
    val milestoneProgress = (user.referralCount.toFloat() / nextMilestone.requiredReferrals.toFloat()).coerceIn(0f, 1f)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("invite_screen_list"),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Big Header: "Your Network"
        item {
            Column {
                Text(
                    text = if (language == "BN") "Your Network (আপনার নেটওয়ার্ক)" else "Your Network",
                    style = MaterialTheme.typography.headlineLarge.copy(brush = NexilusHorizontalBrush)
                )
                Text(
                    text = "Invite friends & earn +${settings.referralBonus} NXC each on instant join!",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Network Stats Row (Total Referrals, Referral Earnings, Global Rank)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                NetworkStatBox(
                    title = "Total Referrals",
                    value = "${user.referralCount}",
                    subtitle = "Verified Peers",
                    modifier = Modifier.weight(1f)
                )
                NetworkStatBox(
                    title = "Referral Earnings",
                    value = "${NexilusCurrency.formatCoins(user.referralEarnings)} NXC",
                    subtitle = "+${settings.referralBonus}/invite",
                    modifier = Modifier.weight(1f)
                )
                NetworkStatBox(
                    title = "Global Rank",
                    value = "#$userRank",
                    subtitle = "Leaderboard",
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Referral Link + COPY LINK + INVITE VIA TELEGRAM
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.2.dp, NexilusBrandBrush),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Your Personal Referral Link",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(NexilusSecondarySurfaceDark)
                            .padding(12.dp)
                    ) {
                        Text(
                            text = referralLink,
                            style = TextStyle(
                                fontFamily = SpaceGroteskFontFamily,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp,
                                color = NexilusCyan
                            ),
                            modifier = Modifier.testTag("referral_link_text")
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                clipboard.setPrimaryClip(ClipData.newPlainText("Nexilus Referral Link", referralLink))
                                onShowToast("📋 Referral link copied to clipboard!")
                            },
                            shape = RoundedCornerShape(14.dp),
                            border = BorderStroke(1.dp, NexilusCyan),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = NexilusCyan),
                            modifier = Modifier
                                .weight(1f)
                                .heightIn(min = 48.dp)
                                .testTag("copy_referral_link_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = "Copy Referral Link",
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("COPY LINK", fontWeight = FontWeight.Bold)
                        }

                        NexilusGradientButton(
                            text = "INVITE VIA TELEGRAM",
                            onClick = {
                                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(
                                        Intent.EXTRA_TEXT,
                                        "🚀 Join me on Nexilus Mine and get +100 Nexilus Coins instant joining bonus! Start mining here: $referralLink"
                                    )
                                }
                                context.startActivity(Intent.createChooser(shareIntent, "Invite via Telegram"))
                            },
                            icon = Icons.Default.Share,
                            modifier = Modifier.weight(1.3f),
                            testTag = "share_telegram_button"
                        )
                    }
                }
            }
        }

        // Milestone Progress (5 / 10 / 25 / 50 Referrals)
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Referral Milestones (5 / 10 / 25 / 50)",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "${user.referralCount}/${nextMilestone.requiredReferrals}",
                            style = MaterialTheme.typography.labelLarge.copy(color = NexilusCyan)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    GradientProgressBar(progress = milestoneProgress, height = 8.dp)
                    Spacer(modifier = Modifier.height(12.dp))

                    REFERRAL_MILESTONES.forEach { ms ->
                        val isClaimed = user.isMilestoneClaimed(ms.id)
                        val canClaim = user.referralCount >= ms.requiredReferrals && !isClaimed

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(NexilusSecondarySurfaceDark)
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "${ms.requiredReferrals} Referrals • ${ms.title}",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                                Text(
                                    text = "Bonus Reward: +${ms.rewardCoins} NXC",
                                    style = MaterialTheme.typography.labelSmall.copy(color = NexilusCyan)
                                )
                            }

                            OutlinedButton(
                                onClick = { onClaimMilestone(ms.id) },
                                enabled = canClaim,
                                shape = RoundedCornerShape(10.dp),
                                border = BorderStroke(
                                    1.dp,
                                    when {
                                        isClaimed -> NexilusSuccess
                                        canClaim -> NexilusCyan
                                        else -> Color(0xFF475569)
                                    }
                                ),
                                modifier = Modifier
                                    .heightIn(min = 48.dp)
                                    .testTag("claim_milestone_${ms.id}")
                            ) {
                                Text(
                                    text = when {
                                        isClaimed -> "CLAIMED"
                                        canClaim -> "CLAIM +${ms.rewardCoins}"
                                        else -> "${user.referralCount}/${ms.requiredReferrals}"
                                    },
                                    color = when {
                                        isClaimed -> NexilusSuccess
                                        canClaim -> NexilusCyan
                                        else -> Color(0xFF94A3B8)
                                    },
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }

        // Interactive Referral Webhook Simulator & Anti-Fraud Verification Card
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.GroupAdd,
                            contentDescription = null,
                            tint = NexilusCyan,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Test Referral Join & Anti-Fraud Guard",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Simulate a new Telegram user opening @${settings.botUsername} with your startParam (${user.referralCode}):",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = simulatedHandle,
                        onValueChange = { simulatedHandle = it },
                        label = { Text("New Telegram @username (optional)") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("simulate_referral_username_input")
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        NexilusGradientButton(
                            text = "SIMULATE VALID JOIN (+100 NXC)",
                            onClick = {
                                onSimulateReferralJoin(simulatedHandle, false)
                                simulatedHandle = ""
                            },
                            modifier = Modifier.weight(1.2f),
                            testTag = "simulate_valid_referral_button"
                        )
                        OutlinedButton(
                            onClick = { onSimulateReferralJoin(simulatedHandle, true) },
                            shape = RoundedCornerShape(14.dp),
                            border = BorderStroke(1.dp, NexilusViolet),
                            modifier = Modifier
                                .weight(1f)
                                .heightIn(min = 48.dp)
                                .testTag("simulate_fraud_referral_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Security,
                                contentDescription = null,
                                tint = NexilusViolet,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Same Device",
                                color = NexilusViolet,
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                    }
                }
            }
        }

        // Top-3 Referrer Podium Preview
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
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
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.EmojiEvents,
                                contentDescription = null,
                                tint = NexilusCyan,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Top Referrers Podium",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                        OutlinedButton(
                            onClick = onOpenFullLeaderboard,
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, NexilusCyan),
                            modifier = Modifier
                                .heightIn(min = 48.dp)
                                .testTag("invite_open_leaderboard_button")
                        ) {
                            Text("View Top 50", color = NexilusCyan, fontWeight = FontWeight.Bold)
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    LeaderboardPodium(
                        topThree = topReferrers.take(3),
                        metricLabel = { "${it.referralCount} Refs" }
                    )
                }
            }
        }
    }
}

@Composable
private fun NetworkStatBox(
    title: String,
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
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                style = TextStyle(
                    fontFamily = SpaceGroteskFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = NexilusCyan
                ),
                maxLines = 1
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
