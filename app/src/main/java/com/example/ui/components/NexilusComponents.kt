package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.GroupAdd
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.PlayCircleFilled
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.AppSettingsEntity
import com.example.data.model.ChannelPostEntity
import com.example.data.model.NexilusConstants
import com.example.data.model.NexilusCurrency
import com.example.data.model.UserEntity
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
import com.example.ui.theme.NotoSansBengaliFontFamily
import com.example.ui.theme.SpaceGroteskFontFamily
import com.example.ui.viewmodel.MainTab
import com.example.ui.viewmodel.OverlayScreen
import com.example.ui.viewmodel.RewardedAdModalState
import com.example.ui.viewmodel.ToastNotification

@Composable
fun NexilusGradientButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    icon: ImageVector? = null,
    testTag: String = ""
) {
    val bgBrush = if (enabled) {
        NexilusHorizontalBrush
    } else {
        Brush.horizontalGradient(
            listOf(
                Color(0xFF1E293B),
                Color(0xFF334155)
            )
        )
    }

    Button(
        onClick = onClick,
        enabled = enabled,
        contentPadding = PaddingValues(0.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            disabledContainerColor = Color.Transparent
        ),
        shape = RoundedCornerShape(14.dp),
        modifier = modifier
            .heightIn(min = 48.dp)
            .let { if (testTag.isNotBlank()) it.testTag(testTag) else it }
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 48.dp)
                .background(bgBrush, RoundedCornerShape(14.dp))
                .padding(horizontal = 16.dp, vertical = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                if (icon != null) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = if (enabled) Color.White else Color(0xFF94A3B8),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(
                    text = text,
                    color = if (enabled) Color.White else Color(0xFF94A3B8),
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                )
            }
        }
    }
}

@Composable
fun GradientProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
    height: Dp = 10.dp
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = tween(durationMillis = 450),
        label = "progress_anim"
    )
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .clip(RoundedCornerShape(50))
            .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.45f))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(animatedProgress.coerceAtLeast(0.03f))
                .height(height)
                .clip(RoundedCornerShape(50))
                .background(NexilusHorizontalBrush)
        )
    }
}

@Composable
fun NexilusTopHeader(
    user: UserEntity,
    language: String,
    activeOverlay: OverlayScreen,
    onOpenLeaderboard: () -> Unit,
    onOpenPayLogs: () -> Unit,
    onToggleAdmin: () -> Unit,
    onOpenSettings: () -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.94f),
        tonalElevation = 4.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                // Avatar with Signature Gradient Ring
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(NexilusBrandBrush)
                        .padding(2.dp)
                        .clip(CircleShape)
                        .background(NexilusSurfaceDark),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = user.firstName.take(1).uppercase() + user.lastName.take(1).uppercase(),
                        style = TextStyle(
                            fontFamily = SpaceGroteskFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = NexilusCyan
                        )
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "${user.firstName} ${user.lastName}".trim(),
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.Verified,
                            contentDescription = "Verified Miner",
                            tint = NexilusCyan,
                            modifier = Modifier.size(15.dp)
                        )
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "@${user.username}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        val badgeColor = when (user.status) {
                            "ACTIVE" -> NexilusSuccess
                            "SUSPICIOUS" -> NexilusViolet
                            else -> NexilusDanger
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(badgeColor.copy(alpha = 0.16f))
                                .padding(horizontal = 6.dp, vertical = 1.dp)
                        ) {
                            Text(
                                text = user.status,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = badgeColor
                            )
                        }
                    }
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                IconButton(
                    onClick = onOpenLeaderboard,
                    modifier = Modifier.testTag("header_leaderboard_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.EmojiEvents,
                        contentDescription = "Open Leaderboard",
                        tint = if (activeOverlay == OverlayScreen.LEADERBOARD) NexilusCyan else MaterialTheme.colorScheme.onSurface
                    )
                }
                IconButton(
                    onClick = onOpenPayLogs,
                    modifier = Modifier.testTag("header_paylogs_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Campaign,
                        contentDescription = "Open @NexilusPayLogs Feed",
                        tint = NexilusCyan
                    )
                }
                IconButton(
                    onClick = onToggleAdmin,
                    modifier = Modifier.testTag("header_admin_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.AdminPanelSettings,
                        contentDescription = "Toggle Admin Dashboard",
                        tint = if (activeOverlay == OverlayScreen.ADMIN_CONSOLE) NexilusViolet else MaterialTheme.colorScheme.onSurface
                    )
                }
                IconButton(
                    onClick = onOpenSettings,
                    modifier = Modifier.testTag("header_settings_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Open Settings",
                        tint = if (activeOverlay == OverlayScreen.SETTINGS) NexilusCyan else MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

@Composable
fun BalanceCard(
    user: UserEntity,
    settings: AppSettingsEntity,
    selectedCurrency: String,
    language: String,
    onOpenWalletClick: () -> Unit
) {
    val levelSpec = user.getLevelSpec()
    val levelProgress = ((user.totalMines - levelSpec.minMinesRequired).toFloat() /
            (levelSpec.nextLevelMines - levelSpec.minMinesRequired).coerceAtLeast(1).toFloat()).coerceIn(0f, 1f)

    val minWd = settings.minWithdrawal.coerceAtLeast(1L)
    val wdProgress = (user.balance.toFloat() / minWd.toFloat()).coerceIn(0f, 1f)
    val coinsRemainingToUnlock = (minWd - user.balance).coerceAtLeast(0L)

    val primaryFiatText = if (selectedCurrency == "USDT") {
        "≈ ${NexilusCurrency.formatUSDT(user.balance, settings.coinsPerCentUSDT)} • (${NexilusCurrency.formatBDT(user.balance, settings.coinsPerBDT)})"
    } else {
        "≈ ${NexilusCurrency.formatBDT(user.balance, settings.coinsPerBDT)} • (${NexilusCurrency.formatUSDT(user.balance, settings.coinsPerCentUSDT)})"
    }

    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.2.dp, NexilusBrandBrush),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("balance_card")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        listOf(
                            NexilusCyan.copy(alpha = 0.08f),
                            NexilusBlue.copy(alpha = 0.06f),
                            NexilusPurple.copy(alpha = 0.10f)
                        )
                    )
                )
                .padding(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (language == "BN") "নেক্সিলাস ব্যালেন্স" else "TOTAL NEXILUS BALANCE",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Level Badge with signature gradient
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(NexilusHorizontalBrush)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "Lv.${levelSpec.level} ${if (language == "BN") levelSpec.titleBn else levelSpec.titleEn} (x${levelSpec.multiplier})",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Big Gradient Highlight Coin Balance
            Row(
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = NexilusCurrency.formatCoins(user.balance),
                    style = TextStyle(
                        fontFamily = SpaceGroteskFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 36.sp,
                        brush = NexilusHorizontalBrush
                    ),
                    modifier = Modifier.testTag("balance_coin_value")
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = settings.coinSymbol,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = NexilusCyan
                    ),
                    modifier = Modifier.padding(bottom = 5.dp)
                )
            }

            Text(
                text = primaryFiatText,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Level Progress
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = if (language == "BN") "পরবর্তী লেভেল প্রগ্রেস" else "Level Evolution Progress",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "${user.totalMines}/${levelSpec.nextLevelMines} Mines",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                    color = NexilusCyan
                )
            }
            Spacer(modifier = Modifier.height(5.dp))
            GradientProgressBar(progress = levelProgress, height = 7.dp)

            Spacer(modifier = Modifier.height(12.dp))

            // Withdrawal Progress to 1000 Coins
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (coinsRemainingToUnlock > 0) {
                        "$coinsRemainingToUnlock Coins more to unlock withdrawal"
                    } else {
                        "✅ Withdrawal Unlocked (${NexilusCurrency.formatCoins(minWd)}+ NXC)"
                    },
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                    color = if (coinsRemainingToUnlock > 0) MaterialTheme.colorScheme.onSurface else NexilusSuccess,
                    modifier = Modifier.testTag("withdrawal_unlock_status_text")
                )
                Text(
                    text = "${((wdProgress * 100).toInt())}%",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = NexilusViolet
                )
            }
            Spacer(modifier = Modifier.height(5.dp))
            GradientProgressBar(progress = wdProgress, height = 8.dp)
        }
    }
}

@Composable
fun MiningCard(
    user: UserEntity,
    settings: AppSettingsEntity,
    remainingSec: Long,
    language: String,
    onStartMining: () -> Unit,
    onClaimMining: () -> Unit,
    onFastForwardDemo: () -> Unit
) {
    val levelSpec = user.getLevelSpec()
    val cycleReward = Math.round(settings.miningReward * levelSpec.multiplier)

    val statusText = when (user.miningStatus) {
        "ACTIVE" -> "MINING IN PROGRESS"
        "READY" -> "MINING READY"
        else -> "MINING COMPLETED"
    }
    val statusColor = when (user.miningStatus) {
        "ACTIVE" -> NexilusCyan
        "READY" -> NexilusSuccess
        else -> NexilusViolet
    }

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("mining_card")
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Memory,
                        contentDescription = null,
                        tint = NexilusCyan,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (language == "BN") "কোয়ান্টাম মাইনিং নোড" else "Quantum Mining Node",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }

                // Status Pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(statusColor.copy(alpha = 0.15f))
                        .border(1.dp, statusColor.copy(alpha = 0.5f), RoundedCornerShape(50))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = statusText,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        ),
                        color = statusColor,
                        modifier = Modifier.testTag("mining_status_pill")
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = if (user.miningStatus == "ACTIVE") {
                            NexilusCurrency.formatCountdown(remainingSec)
                        } else if (user.miningStatus == "READY") {
                            "00:00:00"
                        } else {
                            NexilusCurrency.formatCountdown(settings.miningDurationSec)
                        },
                        style = TextStyle(
                            fontFamily = SpaceGroteskFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 30.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        ),
                        modifier = Modifier.testTag("mining_countdown_text")
                    )
                    Text(
                        text = "Cycle Output: +$cycleReward ${settings.coinSymbol} (2h / 7200s)",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (user.miningStatus == "ACTIVE") {
                    IconButton(
                        onClick = onFastForwardDemo,
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(NexilusSecondarySurfaceDark)
                            .testTag("fast_forward_mining_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.FastForward,
                            contentDescription = "Fast-forward mining cycle to READY for testing",
                            tint = NexilusCyan
                        )
                    }
                }
            }

            if (user.miningStatus == "ACTIVE") {
                val totalSec = settings.miningDurationSec.coerceAtLeast(1L)
                val elapsed = (totalSec - remainingSec).coerceAtLeast(0L)
                Spacer(modifier = Modifier.height(10.dp))
                GradientProgressBar(progress = elapsed.toFloat() / totalSec.toFloat(), height = 8.dp)
            }

            Spacer(modifier = Modifier.height(14.dp))

            when (user.miningStatus) {
                "IDLE" -> {
                    NexilusGradientButton(
                        text = if (language == "BN") "মাইনিং শুরু করুন (+$cycleReward NXC)" else "START MINING (+$cycleReward NXC)",
                        onClick = onStartMining,
                        icon = Icons.Default.Bolt,
                        testTag = "start_mining_button"
                    )
                }
                "READY" -> {
                    NexilusGradientButton(
                        text = if (language == "BN") "রিওয়ার্ড সংগ্রহ করুন (+$cycleReward NXC)" else "CLAIM REWARD (+$cycleReward NXC)",
                        onClick = onClaimMining,
                        icon = Icons.Default.CheckCircle,
                        testTag = "claim_mining_button"
                    )
                }
                else -> {
                    NexilusGradientButton(
                        text = "MINING IN PROGRESS (${NexilusCurrency.formatCountdown(remainingSec)})",
                        onClick = {},
                        enabled = false,
                        icon = Icons.Default.Memory,
                        testTag = "mining_active_disabled_button"
                    )
                }
            }
        }
    }
}

@Composable
fun DailyBonusCard(
    user: UserEntity,
    todayDateStr: String,
    language: String,
    onClaimDailyBonus: () -> Unit
) {
    val alreadyClaimedToday = user.dailyLastClaimDay == todayDateStr
    val taskDoneToday = user.hasCompletedDailyTaskToday(todayDateStr)
    val nextDayNumber = (user.dailyStreak % 30) + 1
    val nextReward = NexilusConstants.DAILY_BONUS_SCHEDULE[nextDayNumber - 1]

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("daily_bonus_card")
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = if (language == "BN") "৩০-দিনের দৈনিক বোনাস" else "30-Day Daily Bonus Cycle",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "Streak: ${user.dailyStreak}/30 Days • Next (Day $nextDayNumber): +$nextReward NXC",
                        style = MaterialTheme.typography.bodyMedium,
                        color = NexilusCyan
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(NexilusSecondarySurfaceDark)
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Text(
                        text = "Day $nextDayNumber = $nextReward NXC",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = NexilusViolet,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 30-day mini grid (5 rows x 6 columns) showing Day 1=30, Day 2=29, ..., Day 30=1
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                for (row in 0 until 5) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        for (col in 0 until 6) {
                            val dayIdx = row * 6 + col
                            val dayNum = dayIdx + 1
                            val rewardAmt = NexilusConstants.DAILY_BONUS_SCHEDULE[dayIdx]
                            val isClaimed = dayNum <= user.dailyStreak
                            val isCurrent = dayNum == nextDayNumber && !alreadyClaimedToday

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(
                                        when {
                                            isCurrent -> NexilusHorizontalBrush
                                            isClaimed -> Brush.horizontalGradient(
                                                listOf(
                                                    NexilusSuccess.copy(alpha = 0.25f),
                                                    NexilusSuccess.copy(alpha = 0.25f)
                                                )
                                            )
                                            else -> Brush.horizontalGradient(
                                                listOf(
                                                    NexilusSecondarySurfaceDark,
                                                    NexilusSecondarySurfaceDark
                                                )
                                            )
                                        }
                                    )
                                    .padding(vertical = 5.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "D$dayNum",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontSize = 9.sp,
                                            color = if (isCurrent) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    )
                                    Text(
                                        text = "+$rewardAmt",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = when {
                                                isCurrent -> Color.White
                                                isClaimed -> NexilusSuccess
                                                else -> NexilusCyan
                                            }
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (!taskDoneToday && !alreadyClaimedToday) {
                Text(
                    text = "দৈনিক বোনাস দাবি করতে আজ অন্তত ১টি বিজ্ঞাপন দেখুন বা মাইনিং শুরু করুন",
                    style = TextStyle(
                        fontFamily = NotoSansBengaliFontFamily,
                        fontSize = 12.sp,
                        color = NexilusViolet
                    ),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            NexilusGradientButton(
                text = when {
                    alreadyClaimedToday -> "CLAIMED TODAY (COME BACK TOMORROW)"
                    !taskDoneToday -> "COMPLETE 1 DAILY TASK TO UNLOCK (+$nextReward NXC)"
                    else -> "CLAIM DAY $nextDayNumber BONUS (+$nextReward NXC)"
                },
                onClick = onClaimDailyBonus,
                enabled = !alreadyClaimedToday && taskDoneToday,
                testTag = "claim_daily_bonus_button"
            )
        }
    }
}

@Composable
fun LeaderboardPodium(
    topThree: List<UserEntity>,
    metricLabel: (UserEntity) -> String
) {
    if (topThree.isEmpty()) return
    val first = topThree.getOrNull(0)
    val second = topThree.getOrNull(1)
    val third = topThree.getOrNull(2)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.Bottom
    ) {
        if (second != null) {
            PodiumColumn(user = second, rank = 2, metric = metricLabel(second), avatarSize = 56.dp, pillarHeight = 76.dp)
        }
        if (first != null) {
            PodiumColumn(user = first, rank = 1, metric = metricLabel(first), avatarSize = 70.dp, pillarHeight = 102.dp)
        }
        if (third != null) {
            PodiumColumn(user = third, rank = 3, metric = metricLabel(third), avatarSize = 52.dp, pillarHeight = 64.dp)
        }
    }
}

@Composable
private fun PodiumColumn(
    user: UserEntity,
    rank: Int,
    metric: String,
    avatarSize: Dp,
    pillarHeight: Dp
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(104.dp)
    ) {
        // Gradient Ring Podium Avatar
        Box(
            modifier = Modifier
                .size(avatarSize)
                .clip(CircleShape)
                .background(NexilusBrandBrush)
                .padding(3.dp)
                .clip(CircleShape)
                .background(NexilusSurfaceDark),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = user.firstName.take(1).uppercase(),
                style = TextStyle(
                    fontFamily = SpaceGroteskFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = if (rank == 1) 22.sp else 17.sp,
                    color = NexilusCyan
                )
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = user.firstName,
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = metric,
            style = MaterialTheme.typography.labelSmall.copy(
                color = NexilusCyan,
                fontWeight = FontWeight.SemiBold
            ),
            maxLines = 1
        )
        Spacer(modifier = Modifier.height(6.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(pillarHeight)
                .clip(RoundedCornerShape(topStart = 14.dp, topEnd = 14.dp))
                .background(
                    if (rank == 1) NexilusBrandBrush
                    else Brush.verticalGradient(
                        listOf(
                            NexilusSecondarySurfaceDark,
                            NexilusSurfaceDark
                        )
                    )
                )
                .border(1.dp, NexilusCyan.copy(alpha = 0.35f), RoundedCornerShape(topStart = 14.dp, topEnd = 14.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "#$rank",
                style = TextStyle(
                    fontFamily = SpaceGroteskFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    color = Color.White
                )
            )
        }
    }
}

@Composable
fun NexilusBottomNav(
    currentTab: MainTab,
    language: String,
    onSelectTab: (MainTab) -> Unit
) {
    val items = listOf(
        Triple(MainTab.HOME, if (language == "BN") "হোম" else "Home", Icons.Default.Home),
        Triple(MainTab.EARN, if (language == "BN") "আয়" else "Earn", Icons.Default.PlayCircleFilled),
        Triple(MainTab.MINING, if (language == "BN") "মাইনিং" else "Mining", Icons.Default.Memory),
        Triple(MainTab.INVITE, if (language == "BN") "ইনভাইট" else "Invite", Icons.Default.GroupAdd),
        Triple(MainTab.WALLET, if (language == "BN") "ওয়ালেট" else "Wallet", Icons.Default.AccountBalanceWallet)
    )

    Surface(
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.94f),
        tonalElevation = 12.dp,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.6f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.navigationBars)
                .padding(horizontal = 8.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEach { (tab, label, icon) ->
                val isSelected = currentTab == tab
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(min = 48.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            if (isSelected) NexilusHorizontalBrush
                            else Brush.horizontalGradient(listOf(Color.Transparent, Color.Transparent))
                        )
                        .clickable { onSelectTab(tab) }
                        .padding(vertical = 6.dp)
                        .testTag("nav_tab_${tab.name.lowercase()}"),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = icon,
                            contentDescription = label,
                            tint = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 11.sp
                            ),
                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RewardedAdModalDialog(
    state: RewardedAdModalState,
    onDismiss: () -> Unit,
    onCompleteClaim: () -> Unit
) {
    if (!state.isVisible) return

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = NexilusSurfaceDark),
            border = BorderStroke(1.5.dp, NexilusBrandBrush),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(NexilusHorizontalBrush)
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "ADSGRAM SDK • ${state.blockId}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close Ad",
                            tint = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Box(
                    modifier = Modifier
                        .size(76.dp)
                        .clip(CircleShape)
                        .background(NexilusSecondarySurfaceDark)
                        .border(2.dp, NexilusBrandBrush, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    if (state.secondsLeft > 0) {
                        Text(
                            text = "${state.secondsLeft}s",
                            style = TextStyle(
                                fontFamily = SpaceGroteskFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 26.sp,
                                color = NexilusCyan
                            )
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Ad Verified",
                            tint = NexilusSuccess,
                            modifier = Modifier.size(38.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = state.title,
                    style = MaterialTheme.typography.titleLarge.copy(color = Color.White),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = state.subtitle,
                    style = MaterialTheme.typography.bodyMedium.copy(color = Color(0xFF94A3B8)),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(12.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(NexilusSecondarySurfaceDark)
                        .padding(10.dp)
                ) {
                    Text(
                        text = "Cryptographic adProof: ${state.adProofToken}",
                        style = MaterialTheme.typography.labelSmall.copy(color = NexilusCyan),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                NexilusGradientButton(
                    text = if (state.secondsLeft > 0) {
                        "VERIFYING AD SPOT (${state.secondsLeft}s)..."
                    } else {
                        "VERIFY PROOF & CREDIT REWARD"
                    },
                    onClick = onCompleteClaim,
                    enabled = state.secondsLeft == 0,
                    testTag = "confirm_ad_reward_button"
                )
            }
        }
    }
}

@Composable
fun PayLogsModalDialog(
    visible: Boolean,
    posts: List<ChannelPostEntity>,
    onDismiss: () -> Unit
) {
    if (!visible) return

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = NexilusSurfaceDark),
            border = BorderStroke(1.2.dp, NexilusBrandBrush),
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 540.dp)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "@NexilusPayLogs & Bot Feed",
                            style = MaterialTheme.typography.titleLarge.copy(color = Color.White)
                        )
                        Text(
                            text = "Live Verified Payout Proofs & @NexilusMineBot DMs",
                            style = MaterialTheme.typography.labelSmall.copy(color = NexilusCyan)
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close Feed",
                            tint = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f, fill = false)
                ) {
                    items(posts, key = { it.postId }) { post ->
                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = NexilusSecondarySurfaceDark),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = post.channelUsername,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = NexilusCyan,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                    Text(
                                        text = "Msg #${post.messageId}",
                                        style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF94A3B8))
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = post.text,
                                    style = MaterialTheme.typography.bodyMedium.copy(color = Color.White)
                                )
                                if (post.deepLink.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = "🔗 ${post.deepLink}",
                                        style = MaterialTheme.typography.labelSmall.copy(color = NexilusViolet)
                                    )
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
fun NexilusToastBanner(
    toast: ToastNotification?,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = toast != null,
        enter = fadeIn(),
        exit = fadeOut(),
        modifier = modifier
    ) {
        if (toast != null) {
            val borderColor = if (toast.isError) NexilusDanger else NexilusCyan
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = NexilusSurfaceDark.copy(alpha = 0.96f)),
                border = BorderStroke(1.2.dp, borderColor),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .clickable { onDismiss() }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(borderColor)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = toast.message,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Medium
                        ),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}
