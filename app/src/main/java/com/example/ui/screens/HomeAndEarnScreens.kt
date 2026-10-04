package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.OndemandVideo
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.AppSettingsEntity
import com.example.data.model.MissionEntity
import com.example.data.model.NexilusCurrency
import com.example.data.model.TransactionEntity
import com.example.data.model.UserEntity
import com.example.ui.components.BalanceCard
import com.example.ui.components.DailyBonusCard
import com.example.ui.components.GradientProgressBar
import com.example.ui.components.MiningCard
import com.example.ui.components.NexilusGradientButton
import com.example.ui.theme.NexilusBrandBrush
import com.example.ui.theme.NexilusCyan
import com.example.ui.theme.NexilusDanger
import com.example.ui.theme.NexilusHorizontalBrush
import com.example.ui.theme.NexilusSecondarySurfaceDark
import com.example.ui.theme.NexilusSuccess
import com.example.ui.theme.NexilusViolet
import com.example.ui.theme.SpaceGroteskFontFamily
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HomeScreen(
    user: UserEntity,
    settings: AppSettingsEntity,
    remainingMiningSec: Long,
    todayDateStr: String,
    selectedCurrency: String,
    language: String,
    recentTransactions: List<TransactionEntity>,
    onStartMining: () -> Unit,
    onClaimMining: () -> Unit,
    onFastForwardMining: () -> Unit,
    onClaimDailyBonus: () -> Unit,
    onNavigateToWallet: () -> Unit,
    onNavigateToEarn: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("home_screen_list"),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Banner with Generated Quantum Mining Core Image
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(118.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.img_mining_core_banner_1791128105439),
                        contentDescription = "Nexilus Mine Quantum Reactor Core",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.horizontalGradient(
                                    listOf(
                                        Color(0xFF050B1F).copy(alpha = 0.88f),
                                        Color(0xFF050B1F).copy(alpha = 0.45f)
                                    )
                                )
                            )
                            .padding(16.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Campaign,
                                    contentDescription = null,
                                    tint = NexilusCyan,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "NEXILUS PROTOCOL V2 • @${settings.botUsername}",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = NexilusCyan,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = settings.announcement,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                ),
                                maxLines = 2
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "250 NXC = ৳1 BDT • 333 NXC = $0.01 USDT • Min Payout: 1,000 NXC",
                                style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFFEAF7FF))
                            )
                        }
                    }
                }
            }
        }

        // Balance Card
        item {
            BalanceCard(
                user = user,
                settings = settings,
                selectedCurrency = selectedCurrency,
                language = language,
                onOpenWalletClick = onNavigateToWallet
            )
        }

        // Mining Card
        item {
            MiningCard(
                user = user,
                settings = settings,
                remainingSec = remainingMiningSec,
                language = language,
                onStartMining = onStartMining,
                onClaimMining = onClaimMining,
                onFastForwardDemo = onFastForwardMining
            )
        }

        // Daily Bonus 30-Day Reverse Schedule Card
        item {
            DailyBonusCard(
                user = user,
                todayDateStr = todayDateStr,
                language = language,
                onClaimDailyBonus = onClaimDailyBonus
            )
        }

        // Recent Ledger Activity
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
                                imageVector = Icons.Default.History,
                                contentDescription = null,
                                tint = NexilusCyan,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (language == "BN") "সাম্প্রতিক লেনদেন" else "Recent Ledger Activity",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                        Text(
                            text = "${recentTransactions.size} entries",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    recentTransactions.take(5).forEach { tx ->
                        TransactionRowItem(tx = tx, coinSymbol = settings.coinSymbol)
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun TransactionRowItem(tx: TransactionEntity, coinSymbol: String) {
    val isPositive = tx.amount >= 0
    val amountColor = if (isPositive) NexilusSuccess else NexilusDanger
    val timeFmt = SimpleDateFormat("MMM dd, HH:mm", Locale.US).format(Date(tx.createdAt))

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(NexilusSecondarySurfaceDark.copy(alpha = 0.65f))
            .padding(horizontal = 12.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = tx.type.replace("_", " "),
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
            )
            if (tx.meta.isNotBlank()) {
                Text(
                    text = tx.meta,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
            }
            Text(
                text = timeFmt,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "${if (isPositive) "+" else ""}${tx.amount} $coinSymbol",
            style = TextStyle(
                fontFamily = SpaceGroteskFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = amountColor
            )
        )
    }
}

@Composable
fun EarnScreen(
    user: UserEntity,
    settings: AppSettingsEntity,
    todayDateStr: String,
    missions: List<MissionEntity>,
    language: String,
    onWatchRewardedAd: () -> Unit,
    onExecuteMission: (MissionEntity) -> Unit
) {
    val context = LocalContext.current
    val todayAdsCount = if (user.adsTodayDate == todayDateStr) user.adsTodayCount else 0
    val maxAds = settings.adsDailyLimit.coerceAtLeast(1)
    val earnedTodayCoins = todayAdsCount * settings.adReward
    val remainingAds = (maxAds - todayAdsCount).coerceAtLeast(0)
    val remainingCoins = remainingAds * settings.adReward
    val adProgress = (todayAdsCount.toFloat() / maxAds.toFloat()).coerceIn(0f, 1f)

    val adMissions = missions.filter { it.type == "AD" }
    val telegramMissions = missions.filter { it.type == "TELEGRAM" }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("earn_screen_list"),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header: "Earn Nexilus" / "WATCH ADS • COMPLETE TASKS • EARN COINS"
        item {
            Column {
                Text(
                    text = if (language == "BN") "Earn Nexilus (আয় করুন)" else "Earn Nexilus",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        brush = NexilusHorizontalBrush
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "WATCH ADS • COMPLETE TASKS • EARN COINS",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.1.sp,
                        color = NexilusCyan
                    )
                )
            }
        }

        // Watch & Earn Big Card
        item {
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.2.dp, NexilusBrandBrush),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("watch_and_earn_card")
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
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(NexilusBrandBrush),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.OndemandVideo,
                                    contentDescription = null,
                                    tint = Color.White
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Watch & Earn AdsGram",
                                    style = MaterialTheme.typography.titleLarge
                                )
                                Text(
                                    text = "+${settings.adReward} NXC per ad • Max ${maxAds * settings.adReward} NXC/day",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(NexilusSecondarySurfaceDark)
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "$todayAdsCount/$maxAds",
                                style = TextStyle(
                                    fontFamily = SpaceGroteskFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = NexilusCyan
                                ),
                                modifier = Modifier.testTag("ad_progress_counter")
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    GradientProgressBar(progress = adProgress, height = 10.dp)
                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        StatMiniPill(
                            label = "Earned Today",
                            value = "+$earnedTodayCoins NXC",
                            modifier = Modifier.weight(1f)
                        )
                        StatMiniPill(
                            label = "Remaining Today",
                            value = "$remainingCoins NXC ($remainingAds ads)",
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    NexilusGradientButton(
                        text = if (remainingAds > 0) {
                            "WATCH AD (+${settings.adReward} Nexilus Coins)"
                        } else {
                            "DAILY AD LIMIT REACHED ($maxAds/$maxAds)"
                        },
                        onClick = onWatchRewardedAd,
                        enabled = remainingAds > 0 && settings.adsEnabled,
                        icon = Icons.Default.PlayArrow,
                        testTag = "watch_ad_button"
                    )
                }
            }
        }

        // Ad Missions List
        item {
            Text(
                text = "Ad Missions (${adMissions.size})",
                style = MaterialTheme.typography.titleLarge
            )
        }

        items(adMissions, key = { it.missionId }) { mission ->
            val completed = user.isMissionCompleted(mission.missionId)
            MissionCardItem(
                mission = mission,
                isCompleted = completed,
                primaryActionLabel = if (completed) "COMPLETED" else "WATCH SPOT (+${mission.reward} NXC)",
                onPrimaryAction = { onExecuteMission(mission) },
                onSecondaryOpenChannel = null
            )
        }

        // Telegram Missions List (JOIN -> VERIFY)
        item {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Telegram Missions (JOIN → VERIFY)",
                style = MaterialTheme.typography.titleLarge
            )
        }

        items(telegramMissions, key = { it.missionId }) { mission ->
            val completed = user.isMissionCompleted(mission.missionId)
            MissionCardItem(
                mission = mission,
                isCompleted = completed,
                primaryActionLabel = if (completed) "VERIFIED" else "VERIFY (+${mission.reward} NXC)",
                onPrimaryAction = { onExecuteMission(mission) },
                onSecondaryOpenChannel = {
                    val handle = mission.channelUsername.removePrefix("@").ifBlank { "NexilusPayLogs" }
                    try {
                        context.startActivity(
                            Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/$handle"))
                        )
                    } catch (_: Exception) {
                    }
                }
            )
        }
    }
}

@Composable
private fun StatMiniPill(
    label: String,
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
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = value,
            style = TextStyle(
                fontFamily = SpaceGroteskFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = NexilusCyan
            )
        )
    }
}

@Composable
private fun MissionCardItem(
    mission: MissionEntity,
    isCompleted: Boolean,
    primaryActionLabel: String,
    onPrimaryAction: () -> Unit,
    onSecondaryOpenChannel: (() -> Unit)?
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(
            1.dp,
            if (isCompleted) NexilusSuccess.copy(alpha = 0.5f) else MaterialTheme.colorScheme.outline
        ),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("mission_card_${mission.missionId}")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(
                                    if (mission.type == "TELEGRAM") NexilusCyan.copy(alpha = 0.16f)
                                    else NexilusViolet.copy(alpha = 0.16f)
                                )
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = mission.type,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (mission.type == "TELEGRAM") NexilusCyan else NexilusViolet
                                )
                            )
                        }
                        if (mission.channelUsername.isNotBlank()) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = mission.channelUsername,
                                style = MaterialTheme.typography.labelSmall.copy(color = NexilusCyan)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = mission.title,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = mission.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(NexilusHorizontalBrush)
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "+${mission.reward} NXC",
                        style = MaterialTheme.typography.labelLarge.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (onSecondaryOpenChannel != null && !isCompleted) {
                    OutlinedButton(
                        onClick = onSecondaryOpenChannel,
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, NexilusCyan),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = NexilusCyan),
                        modifier = Modifier
                            .weight(1f)
                            .heightIn(min = 48.dp)
                            .testTag("join_channel_${mission.missionId}")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("1. JOIN", fontWeight = FontWeight.Bold)
                    }
                }

                NexilusGradientButton(
                    text = if (onSecondaryOpenChannel != null && !isCompleted) "2. $primaryActionLabel" else primaryActionLabel,
                    onClick = onPrimaryAction,
                    enabled = !isCompleted,
                    icon = if (isCompleted) Icons.Default.CheckCircle else Icons.Default.Verified,
                    modifier = Modifier.weight(1.2f),
                    testTag = "verify_mission_${mission.missionId}"
                )
            }
        }
    }
}
