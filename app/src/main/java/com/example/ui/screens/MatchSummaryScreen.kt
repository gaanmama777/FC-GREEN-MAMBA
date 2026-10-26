package com.example.ui.screens

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MatchEntity
import com.example.data.model.MatchEventEntity
import com.example.ui.MambaViewModel
import com.example.ui.theme.AssistCyan
import com.example.ui.theme.CardRed
import com.example.ui.theme.CardYellow
import com.example.ui.theme.GoalGold
import com.example.ui.theme.MambaAccentMint
import com.example.ui.theme.MambaGold
import com.example.ui.theme.MambaGreenBorder
import com.example.ui.theme.MambaGreenDark
import com.example.ui.theme.MambaGreenPrimary
import com.example.ui.theme.MambaGreenSurface
import com.example.ui.theme.PureWhite
import com.example.ui.theme.SubBlue
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun MatchSummaryScreen(
    viewModel: MambaViewModel
) {
    val context = LocalContext.current
    val activeMatch by viewModel.activeMatch.collectAsState()
    val allMatches by viewModel.allMatches.collectAsState()
    val activeEvents by viewModel.activeMatchEvents.collectAsState()

    var selectedHistoricalMatch by remember { mutableStateOf<MatchEntity?>(null) }

    // If a historical match is tapped, show that, otherwise show active match or latest completed match
    val displayMatch = selectedHistoricalMatch ?: activeMatch ?: allMatches.firstOrNull()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MambaGreenDark)
            .padding(12.dp)
    ) {
        if (displayMatch == null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No match history yet.\nStart your first match to see detailed stats & reports!",
                    color = PureWhite.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center,
                    fontSize = 14.sp
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    // Match Header with Result
                    val isWin = displayMatch.homeScore > displayMatch.awayScore
                    val isDraw = displayMatch.homeScore == displayMatch.awayScore
                    val resultBannerColor = if (isWin) MambaAccentMint else if (isDraw) CardYellow else CardRed
                    val resultText = if (isWin) "VICTORY 🏆" else if (isDraw) "DRAW 🤝" else "DEFEAT"

                    Card(
                        colors = CardDefaults.cardColors(containerColor = MambaGreenSurface),
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, MambaGreenBorder),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Surface(
                                color = resultBannerColor.copy(alpha = 0.2f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, resultBannerColor),
                                shape = RoundedCornerShape(20.dp)
                            ) {
                                Text(
                                    text = resultText,
                                    color = resultBannerColor,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Black,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Score line
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("FC GREEN MAMBA", color = MambaAccentMint, fontWeight = FontWeight.Black, fontSize = 14.sp)
                                    Text(displayMatch.homeScore.toString(), color = PureWhite, fontWeight = FontWeight.ExtraBold, fontSize = 44.sp)
                                }
                                Text("-", color = PureWhite.copy(alpha = 0.6f), fontWeight = FontWeight.Bold, fontSize = 36.sp)
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(displayMatch.opponentName.uppercase(), color = PureWhite, fontWeight = FontWeight.Black, fontSize = 14.sp)
                                    Text(displayMatch.awayScore.toString(), color = PureWhite, fontWeight = FontWeight.ExtraBold, fontSize = 44.sp)
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "${displayMatch.matchFormat} • ${displayMatch.formationName} • ${displayMatch.durationMinutes} mins",
                                color = PureWhite.copy(alpha = 0.7f),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )

                            val dateFormatted = SimpleDateFormat("EEE, dd MMM yyyy", Locale.getDefault()).format(Date(displayMatch.createdAt))
                            Text(
                                text = dateFormatted,
                                color = PureWhite.copy(alpha = 0.5f),
                                fontSize = 11.sp
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            // Share Report Button
                            Button(
                                onClick = {
                                    val reportText = viewModel.generateMatchReportText(displayMatch, activeEvents)
                                    val sendIntent = Intent().apply {
                                        action = Intent.ACTION_SEND
                                        putExtra(Intent.EXTRA_TEXT, reportText)
                                        type = "text/plain"
                                    }
                                    val shareIntent = Intent.createChooser(sendIntent, "Share FC Green Mamba Report")
                                    context.startActivity(shareIntent)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = MambaAccentMint),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(46.dp)
                                    .testTag("share_report_button")
                            ) {
                                Icon(Icons.Default.Share, contentDescription = null, tint = MambaGreenDark, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("SHARE REPORT TO TEAM CHAT", color = MambaGreenDark, fontWeight = FontWeight.Black, fontSize = 13.sp)
                            }
                        }
                    }
                }

                // Match Key Contributions
                item {
                    Text(
                        text = "MATCH HIGHLIGHTS & TIMELINE",
                        color = MambaAccentMint,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                if (activeEvents.isEmpty()) {
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MambaGreenSurface),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "No log events recorded for this match.",
                                color = PureWhite.copy(alpha = 0.6f),
                                modifier = Modifier.padding(14.dp),
                                fontSize = 12.sp
                            )
                        }
                    }
                } else {
                    items(activeEvents) { event ->
                        val (icon, color) = when (event.type) {
                            "GOAL" -> "⚽" to GoalGold
                            "OPPONENT_GOAL" -> "🔴" to CardRed
                            "SUBSTITUTION" -> "🔁" to SubBlue
                            "YELLOW_CARD" -> "🟨" to CardYellow
                            "RED_CARD" -> "🟥" to CardRed
                            else -> "⏱️" to PureWhite
                        }

                        Card(
                            colors = CardDefaults.cardColors(containerColor = MambaGreenSurface),
                            border = androidx.compose.foundation.BorderStroke(1.dp, MambaGreenBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${event.minute}'",
                                    color = MambaAccentMint,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 14.sp,
                                    modifier = Modifier.width(36.dp)
                                )
                                Text(
                                    text = icon,
                                    fontSize = 18.sp,
                                    modifier = Modifier.padding(horizontal = 8.dp)
                                )
                                Text(
                                    text = event.note,
                                    color = PureWhite,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }

                // Match History List
                item {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "RECENT MATCHES HISTORY (${allMatches.size})",
                        color = PureWhite.copy(alpha = 0.8f),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                items(allMatches) { match ->
                    val isSelected = displayMatch.id == match.id
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) MambaGreenPrimary.copy(alpha = 0.3f) else MambaGreenSurface
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isSelected) MambaAccentMint else MambaGreenBorder
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedHistoricalMatch = match }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "FC Green Mamba ${match.homeScore} - ${match.awayScore} ${match.opponentName}",
                                    color = PureWhite,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = "${match.matchFormat} • ${match.formationName} • ${if (match.isCompleted) "Completed" else "Live"}",
                                    color = PureWhite.copy(alpha = 0.6f),
                                    fontSize = 11.sp
                                )
                            }
                            IconButton(
                                onClick = { viewModel.deleteMatch(match.id) },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete Match", tint = PureWhite.copy(alpha = 0.5f))
                            }
                        }
                    }
                }
            }
        }
    }
}
