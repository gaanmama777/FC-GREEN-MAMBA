package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FastRewind
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.FormationLibrary
import com.example.data.model.MatchEntity
import com.example.data.model.MatchEventEntity
import com.example.data.model.PitchPlayerSlot
import com.example.data.model.PlayerEntity
import com.example.ui.MambaViewModel
import com.example.ui.components.TacticalPitchView
import com.example.ui.theme.AssistCyan
import com.example.ui.theme.CardRed
import com.example.ui.theme.CardYellow
import com.example.ui.theme.GoalGold
import com.example.ui.theme.MambaAccentMint
import com.example.ui.theme.MambaGreenBorder
import com.example.ui.theme.MambaGreenDark
import com.example.ui.theme.MambaGreenPrimary
import com.example.ui.theme.MambaGreenSurface
import com.example.ui.theme.PureWhite
import com.example.ui.theme.SubBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LiveMatchScreen(
    viewModel: MambaViewModel,
    onNavigateToSummary: () -> Unit
) {
    val activeMatch by viewModel.activeMatch.collectAsState()
    val pitchSlots by viewModel.pitchSlots.collectAsState()
    val events by viewModel.activeMatchEvents.collectAsState()
    val squad by viewModel.squad.collectAsState()

    var showNewMatchDialog by remember { mutableStateOf(false) }
    var showGoalDialog by remember { mutableStateOf(false) }
    var showSubDialog by remember { mutableStateOf(false) }
    var showCardDialog by remember { mutableStateOf(false) }
    var showEditOpponentDialog by remember { mutableStateOf(false) }
    var selectedPlayerForAction by remember { mutableStateOf<PitchPlayerSlot?>(null) }
    var selectedTab by remember { mutableIntStateOf(0) } // 0 = Formation Pitch, 1 = Squad & Subs, 2 = Events

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MambaGreenDark)
            .padding(horizontal = 12.dp)
    ) {
        if (activeMatch == null) {
            // Empty State: Prompt to start match
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MambaGreenSurface),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, MambaGreenBorder),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = MambaGreenPrimary,
                            modifier = Modifier.size(72.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.SportsSoccer,
                                    contentDescription = null,
                                    tint = PureWhite,
                                    modifier = Modifier.size(40.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No Live Match Active",
                            color = PureWhite,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Start a 5, 6, 7, 8, or 9-a-side match for FC Green Mamba with live timer, formation summary, and quick-tap tracking.",
                            color = PureWhite.copy(alpha = 0.8f),
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Button(
                            onClick = { showNewMatchDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = MambaAccentMint),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .testTag("start_match_button")
                        ) {
                            Text(
                                text = "KICK OFF NEW MATCH",
                                color = MambaGreenDark,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }
                }
            }
        } else {
            val match = activeMatch!!

            // ESPN-Style Live Scoreboard
            Card(
                colors = CardDefaults.cardColors(containerColor = MambaGreenSurface),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, MambaGreenBorder),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Top Bar: Format badge & Period
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            color = MambaGreenPrimary,
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "${match.matchFormat} • ${match.formationName}",
                                color = PureWhite,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }

                        // Period Badge
                        val periodLabel = when (match.currentPeriod) {
                            "FIRST_HALF" -> "1ST HALF"
                            "HALF_TIME" -> "HALF TIME"
                            "SECOND_HALF" -> "2ND HALF"
                            "FULL_TIME" -> "FULL TIME"
                            else -> "LIVE"
                        }
                        Surface(
                            color = if (match.isRunning) MambaAccentMint else CardYellow,
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = periodLabel,
                                color = MambaGreenDark,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Scoreboard Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Home Team (FC GREEN MAMBA)
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = "FC GREEN MAMBA",
                                color = MambaAccentMint,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Black,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = match.homeScore.toString(),
                                color = PureWhite,
                                fontSize = 42.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }

                        // Colon separator & Time
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(horizontal = 4.dp)
                        ) {
                            val mins = match.elapsedSeconds / 60
                            val secs = match.elapsedSeconds % 60
                            val timeStr = String.format("%02d:%02d", mins, secs)

                            Text(
                                text = timeStr,
                                color = PureWhite,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Black
                            )
                            Text(
                                text = "/ ${match.durationMinutes}'",
                                color = PureWhite.copy(alpha = 0.6f),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // Away Team (Opponent)
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .weight(1f)
                                .clickable { showEditOpponentDialog = true }
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = match.opponentName.uppercase(),
                                    color = PureWhite,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Black,
                                    textAlign = TextAlign.Center,
                                    maxLines = 1
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Edit Opponent",
                                    tint = PureWhite.copy(alpha = 0.6f),
                                    modifier = Modifier.size(12.dp)
                                )
                            }
                            Text(
                                text = match.awayScore.toString(),
                                color = PureWhite,
                                fontSize = 42.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }

                    // Timer & Whistle Control Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // -1 Min
                        IconButton(
                            onClick = { viewModel.adjustTimer(-1) },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(Icons.Default.FastRewind, contentDescription = "-1 Min", tint = PureWhite)
                        }

                        // Play/Pause Main Button
                        Button(
                            onClick = { viewModel.toggleMatchTimer() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (match.isRunning) CardYellow else MambaAccentMint
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.height(40.dp).testTag("timer_toggle_button")
                        ) {
                            Icon(
                                imageVector = if (match.isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = "Play/Pause",
                                tint = MambaGreenDark,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (match.isRunning) "PAUSE" else "START",
                                color = MambaGreenDark,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Black
                            )
                        }

                        // +1 Min
                        IconButton(
                            onClick = { viewModel.adjustTimer(1) },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(Icons.Default.FastForward, contentDescription = "+1 Min", tint = PureWhite)
                        }

                        // Next Period / Whistle Button
                        OutlinedButton(
                            onClick = { viewModel.advanceMatchPeriod() },
                            border = androidx.compose.foundation.BorderStroke(1.dp, PureWhite.copy(alpha = 0.5f)),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.height(40.dp).testTag("whistle_advance_button")
                        ) {
                            Text(
                                text = when (match.currentPeriod) {
                                    "FIRST_HALF" -> "HALF TIME"
                                    "HALF_TIME" -> "2ND HALF"
                                    "SECOND_HALF" -> "FULL TIME"
                                    else -> "FINISH"
                                },
                                color = PureWhite,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Quick-Tap Action Bar (High contrast fast logging buttons)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Goal Button (+ Scorer)
                Button(
                    onClick = { showGoalDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = GoalGold),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .weight(1.3f)
                        .height(46.dp)
                        .testTag("quick_goal_button")
                ) {
                    Text(
                        text = "⚽ GOAL",
                        color = Color.Black,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black
                    )
                }

                // Sub Button
                Button(
                    onClick = { showSubDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = SubBlue),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .weight(1.1f)
                        .height(46.dp)
                        .testTag("quick_sub_button")
                ) {
                    Text(
                        text = "🔁 SUB",
                        color = PureWhite,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black
                    )
                }

                // Card Button
                Button(
                    onClick = { showCardDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = CardYellow),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .weight(1.1f)
                        .height(46.dp)
                        .testTag("quick_card_button")
                ) {
                    Text(
                        text = "🟨 CARD",
                        color = Color.Black,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black
                    )
                }

                // Opponent Goal Button
                Button(
                    onClick = { viewModel.recordOpponentGoal() },
                    colors = ButtonDefaults.buttonColors(containerColor = CardRed),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .weight(1.2f)
                        .height(46.dp)
                        .testTag("quick_opp_goal_button")
                ) {
                    Text(
                        text = "+1 OPP",
                        color = PureWhite,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black
                    )
                }

                // Undo Button
                IconButton(
                    onClick = { viewModel.undoLastEvent() },
                    modifier = Modifier
                        .size(46.dp)
                        .background(MambaGreenSurface, RoundedCornerShape(10.dp))
                        .border(1.dp, MambaGreenBorder, RoundedCornerShape(10.dp))
                        .testTag("undo_button")
                ) {
                    Icon(Icons.Default.Undo, contentDescription = "Undo Event", tint = PureWhite)
                }
            }

            // Tabs: Formation Pitch | Squad & Subs | Event Timeline
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MambaGreenDark,
                contentColor = MambaAccentMint,
                divider = {}
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = {
                        Text(
                            text = "PITCH (${pitchSlots.size})",
                            fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedTab == 0) MambaAccentMint else PureWhite.copy(alpha = 0.7f),
                            fontSize = 12.sp
                        )
                    }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = {
                        Text(
                            text = "SQUAD / SUBS",
                            fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedTab == 1) MambaAccentMint else PureWhite.copy(alpha = 0.7f),
                            fontSize = 12.sp
                        )
                    }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = {
                        Text(
                            text = "TIMELINE (${events.size})",
                            fontWeight = if (selectedTab == 2) FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedTab == 2) MambaAccentMint else PureWhite.copy(alpha = 0.7f),
                            fontSize = 12.sp
                        )
                    }
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Tab Content
            when (selectedTab) {
                0 -> {
                    // Interactive Pitch View
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f)
                    ) {
                        TacticalPitchView(
                            slots = pitchSlots,
                            isInteractive = true,
                            isDraggable = false,
                            onPlayerClick = { slot ->
                                selectedPlayerForAction = slot
                                showGoalDialog = true
                            },
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
                1 -> {
                    // Squad & Subs Bench View
                    val onPitchPlayerIds = pitchSlots.mapNotNull { it.playerId }.toSet()
                    val benchPlayers = squad.filter { !onPitchPlayerIds.contains(it.id) }

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f)
                    ) {
                        item {
                            Text(
                                text = "ON PITCH (${pitchSlots.size})",
                                color = MambaAccentMint,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(vertical = 4.dp)
                            )
                        }
                        items(pitchSlots) { slot ->
                            Card(
                                colors = CardDefaults.cardColors(containerColor = MambaGreenSurface),
                                border = androidx.compose.foundation.BorderStroke(1.dp, MambaGreenBorder),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 3.dp)
                                    .clickable {
                                        selectedPlayerForAction = slot
                                        showSubDialog = true
                                    }
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Surface(
                                            shape = CircleShape,
                                            color = MambaGreenPrimary,
                                            modifier = Modifier.size(36.dp)
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Text(
                                                    text = "#${slot.jerseyNumber}",
                                                    color = PureWhite,
                                                    fontSize = 13.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Text(
                                                text = slot.playerName,
                                                color = PureWhite,
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Text(
                                                text = "${slot.roleLabel} • ${slot.category}",
                                                color = PureWhite.copy(alpha = 0.6f),
                                                fontSize = 11.sp
                                            )
                                        }
                                    }
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        if (slot.goalsInMatch > 0) {
                                            Text(
                                                text = "⚽ ${slot.goalsInMatch}",
                                                color = GoalGold,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(end = 6.dp)
                                            )
                                        }
                                        if (slot.assistsInMatch > 0) {
                                            Text(
                                                text = "👟 ${slot.assistsInMatch}",
                                                color = AssistCyan,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(end = 6.dp)
                                            )
                                        }
                                        TextButton(
                                            onClick = {
                                                selectedPlayerForAction = slot
                                                showSubDialog = true
                                            }
                                        ) {
                                            Text("SUB", color = SubBlue, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }

                        item {
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "BENCH RESERVES (${benchPlayers.size})",
                                color = PureWhite.copy(alpha = 0.7f),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(vertical = 4.dp)
                            )
                        }
                        items(benchPlayers) { player ->
                            Card(
                                colors = CardDefaults.cardColors(containerColor = MambaGreenDark),
                                border = androidx.compose.foundation.BorderStroke(1.dp, MambaGreenBorder.copy(alpha = 0.5f)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 3.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Surface(
                                            shape = CircleShape,
                                            color = MambaGreenDark,
                                            border = androidx.compose.foundation.BorderStroke(1.dp, PureWhite.copy(alpha = 0.4f)),
                                            modifier = Modifier.size(34.dp)
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Text(
                                                    text = "#${player.jerseyNumber}",
                                                    color = PureWhite,
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Text(
                                                text = player.name,
                                                color = PureWhite,
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Text(
                                                text = "${player.position} • ${player.nickname.ifBlank { "Squad" }}",
                                                color = PureWhite.copy(alpha = 0.6f),
                                                fontSize = 11.sp
                                            )
                                        }
                                    }
                                    Text(
                                        text = "AVAILABLE",
                                        color = MambaAccentMint,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
                2 -> {
                    // Match Timeline
                    if (events.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No match events recorded yet.\nTap Goal, Sub, or Card above to log moments.",
                                color = PureWhite.copy(alpha = 0.6f),
                                textAlign = TextAlign.Center,
                                fontSize = 13.sp
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .weight(1f)
                        ) {
                            items(events.reversed()) { event ->
                                val (icon, iconColor) = when (event.type) {
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
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 3.dp)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(10.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "${event.minute}'",
                                            color = MambaAccentMint,
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 14.sp,
                                            modifier = Modifier.width(36.dp)
                                        )
                                        Text(
                                            text = icon,
                                            fontSize = 18.sp,
                                            modifier = Modifier.padding(horizontal = 6.dp)
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
                    }
                }
            }

            // Bottom Full-Time / View Report Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = { viewModel.finishCurrentMatch() },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = CardYellow),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CardYellow),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1f).height(44.dp)
                ) {
                    Text("END MATCH", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = { onNavigateToSummary() },
                    colors = ButtonDefaults.buttonColors(containerColor = MambaGreenPrimary),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1f).height(44.dp)
                ) {
                    Text("MATCH REPORT", color = PureWhite, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }

    // DIALOG: Start New Match
    if (showNewMatchDialog) {
        var opponentName by remember { mutableStateOf("Red Vipers FC") }
        var selectedFormat by remember { mutableStateOf("7-a-side") }
        var selectedFormation by remember { mutableStateOf("2-3-1 (Classic 7s)") }
        var durationMinutes by remember { mutableIntStateOf(70) }

        val formatOptions = listOf("5-a-side", "6-a-side", "7-a-side", "8-a-side", "9-a-side", "11-a-side")
        val formationOptions = FormationLibrary.getPresetsForFormat(selectedFormat)

        AlertDialog(
            onDismissRequest = { showNewMatchDialog = false },
            containerColor = MambaGreenSurface,
            title = {
                Text(
                    text = "Kick Off New Match",
                    color = PureWhite,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = opponentName,
                        onValueChange = { opponentName = it },
                        label = { Text("Opponent Team Name", color = PureWhite.copy(alpha = 0.7f)) },
                        colors = androidx.compose.material3.TextFieldDefaults.colors(
                            focusedTextColor = PureWhite,
                            unfocusedTextColor = PureWhite,
                            focusedContainerColor = MambaGreenDark,
                            unfocusedContainerColor = MambaGreenDark
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text("Match Format", color = PureWhite, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    ScrollableTabRow(
                        selectedTabIndex = formatOptions.indexOf(selectedFormat).coerceAtLeast(0),
                        containerColor = MambaGreenDark,
                        edgePadding = 0.dp
                    ) {
                        formatOptions.forEach { fmt ->
                            Tab(
                                selected = selectedFormat == fmt,
                                onClick = {
                                    selectedFormat = fmt
                                    selectedFormation = FormationLibrary.getPresetsForFormat(fmt).first().name
                                },
                                text = {
                                    Text(
                                        fmt,
                                        color = if (selectedFormat == fmt) MambaAccentMint else PureWhite.copy(alpha = 0.6f),
                                        fontSize = 11.sp
                                    )
                                }
                            )
                        }
                    }

                    Text("Starting Formation", color = PureWhite, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        formationOptions.take(3).forEach { f ->
                            FilterChip(
                                selected = selectedFormation == f.name,
                                onClick = { selectedFormation = f.name },
                                label = { Text(f.name.split(" ").first(), fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MambaAccentMint,
                                    selectedLabelColor = MambaGreenDark,
                                    labelColor = PureWhite
                                )
                            )
                        }
                    }

                    Text("Duration (Mins)", color = PureWhite, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(50, 60, 70, 80, 90).forEach { mins ->
                            FilterChip(
                                selected = durationMinutes == mins,
                                onClick = { durationMinutes = mins },
                                label = { Text("${mins}'", fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MambaAccentMint,
                                    selectedLabelColor = MambaGreenDark,
                                    labelColor = PureWhite
                                )
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.startNewMatch(
                            opponent = opponentName,
                            format = selectedFormat,
                            formation = selectedFormation,
                            durationMinutes = durationMinutes
                        )
                        showNewMatchDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MambaAccentMint)
                ) {
                    Text("START MATCH", color = MambaGreenDark, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showNewMatchDialog = false }) {
                    Text("CANCEL", color = PureWhite)
                }
            }
        )
    }

    // DIALOG: Record Goal & Assist
    if (showGoalDialog) {
        var selectedScorer by remember { mutableStateOf<PitchPlayerSlot?>(selectedPlayerForAction ?: pitchSlots.firstOrNull()) }
        var selectedAssist by remember { mutableStateOf<PitchPlayerSlot?>(null) }

        AlertDialog(
            onDismissRequest = {
                showGoalDialog = false
                selectedPlayerForAction = null
            },
            containerColor = MambaGreenSurface,
            title = {
                Text(
                    text = "⚽ Record Green Mamba Goal",
                    color = GoalGold,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Who Scored?", color = PureWhite, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    LazyColumn(modifier = Modifier.height(130.dp)) {
                        items(pitchSlots) { slot ->
                            val isSelected = selectedScorer?.slotId == slot.slotId
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 2.dp)
                                    .background(
                                        if (isSelected) MambaAccentMint.copy(alpha = 0.2f) else Color.Transparent,
                                        RoundedCornerShape(6.dp)
                                    )
                                    .border(
                                        1.dp,
                                        if (isSelected) MambaAccentMint else Color.Transparent,
                                        RoundedCornerShape(6.dp)
                                    )
                                    .clickable { selectedScorer = slot }
                                    .padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "#${slot.jerseyNumber} ${slot.playerName} (${slot.roleLabel})",
                                    color = if (isSelected) MambaAccentMint else PureWhite,
                                    fontSize = 13.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                                if (isSelected) {
                                    Icon(Icons.Default.Check, contentDescription = null, tint = MambaAccentMint)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Who Assisted? (Optional)", color = PureWhite, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    LazyColumn(modifier = Modifier.height(130.dp)) {
                        item {
                            val isNoAssist = selectedAssist == null
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 2.dp)
                                    .background(
                                        if (isNoAssist) AssistCyan.copy(alpha = 0.2f) else Color.Transparent,
                                        RoundedCornerShape(6.dp)
                                    )
                                    .clickable { selectedAssist = null }
                                    .padding(8.dp)
                            ) {
                                Text(
                                    text = "Solo Goal / No Assist",
                                    color = if (isNoAssist) AssistCyan else PureWhite.copy(alpha = 0.7f),
                                    fontSize = 13.sp
                                )
                            }
                        }
                        items(pitchSlots.filter { it.slotId != selectedScorer?.slotId }) { slot ->
                            val isSelected = selectedAssist?.slotId == slot.slotId
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 2.dp)
                                    .background(
                                        if (isSelected) AssistCyan.copy(alpha = 0.2f) else Color.Transparent,
                                        RoundedCornerShape(6.dp)
                                    )
                                    .border(
                                        1.dp,
                                        if (isSelected) AssistCyan else Color.Transparent,
                                        RoundedCornerShape(6.dp)
                                    )
                                    .clickable { selectedAssist = slot }
                                    .padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "#${slot.jerseyNumber} ${slot.playerName} (${slot.roleLabel})",
                                    color = if (isSelected) AssistCyan else PureWhite,
                                    fontSize = 13.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                                if (isSelected) {
                                    Icon(Icons.Default.Check, contentDescription = null, tint = AssistCyan)
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val scorer = selectedScorer
                        if (scorer != null) {
                            viewModel.recordGoal(scorer, selectedAssist)
                        }
                        showGoalDialog = false
                        selectedPlayerForAction = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GoalGold),
                    enabled = selectedScorer != null
                ) {
                    Text("SAVE GOAL", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showGoalDialog = false
                    selectedPlayerForAction = null
                }) {
                    Text("CANCEL", color = PureWhite)
                }
            }
        )
    }

    // DIALOG: Substitution (Player Out & Player In)
    if (showSubDialog) {
        val onPitchPlayerIds = pitchSlots.mapNotNull { it.playerId }.toSet()
        val benchPlayers = squad.filter { !onPitchPlayerIds.contains(it.id) }
        var selectedOut by remember { mutableStateOf<PitchPlayerSlot?>(selectedPlayerForAction ?: pitchSlots.firstOrNull()) }
        var selectedIn by remember { mutableStateOf<PlayerEntity?>(benchPlayers.firstOrNull()) }

        AlertDialog(
            onDismissRequest = {
                showSubDialog = false
                selectedPlayerForAction = null
            },
            containerColor = MambaGreenSurface,
            title = {
                Text(
                    text = "🔁 Make Substitution",
                    color = SubBlue,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Player Coming OFF", color = PureWhite, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    LazyColumn(modifier = Modifier.height(110.dp)) {
                        items(pitchSlots) { slot ->
                            val isSelected = selectedOut?.slotId == slot.slotId
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 2.dp)
                                    .background(
                                        if (isSelected) CardRed.copy(alpha = 0.2f) else Color.Transparent,
                                        RoundedCornerShape(6.dp)
                                    )
                                    .clickable { selectedOut = slot }
                                    .padding(8.dp)
                            ) {
                                Text(
                                    text = "🔴 #${slot.jerseyNumber} ${slot.playerName} (${slot.roleLabel})",
                                    color = if (isSelected) CardRed else PureWhite,
                                    fontSize = 13.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text("Player Coming ON (From Bench)", color = PureWhite, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    if (benchPlayers.isEmpty()) {
                        Text(
                            text = "No players on the bench. Add more players in Squad management.",
                            color = PureWhite.copy(alpha = 0.6f),
                            fontSize = 12.sp
                        )
                    } else {
                        LazyColumn(modifier = Modifier.height(110.dp)) {
                            items(benchPlayers) { player ->
                                val isSelected = selectedIn?.id == player.id
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 2.dp)
                                        .background(
                                            if (isSelected) MambaAccentMint.copy(alpha = 0.2f) else Color.Transparent,
                                            RoundedCornerShape(6.dp)
                                        )
                                        .clickable { selectedIn = player }
                                        .padding(8.dp)
                                ) {
                                    Text(
                                        text = "🟢 #${player.jerseyNumber} ${player.name} (${player.position})",
                                        color = if (isSelected) MambaAccentMint else PureWhite,
                                        fontSize = 13.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val out = selectedOut
                        val inP = selectedIn
                        if (out != null && inP != null) {
                            viewModel.recordSubstitution(out, inP)
                        }
                        showSubDialog = false
                        selectedPlayerForAction = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SubBlue),
                    enabled = selectedOut != null && selectedIn != null
                ) {
                    Text("CONFIRM SUB", color = PureWhite, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showSubDialog = false
                    selectedPlayerForAction = null
                }) {
                    Text("CANCEL", color = PureWhite)
                }
            }
        )
    }

    // DIALOG: Card (Yellow / Red)
    if (showCardDialog) {
        var selectedCardSlot by remember { mutableStateOf<PitchPlayerSlot?>(pitchSlots.firstOrNull()) }
        var isRedCard by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { showCardDialog = false },
            containerColor = MambaGreenSurface,
            title = {
                Text(
                    text = "Book Player",
                    color = if (isRedCard) CardRed else CardYellow,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { isRedCard = false },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (!isRedCard) CardYellow else Color.Gray
                            ),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("🟨 YELLOW", color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                        Button(
                            onClick = { isRedCard = true },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isRedCard) CardRed else Color.Gray
                            ),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("🟥 RED", color = PureWhite, fontWeight = FontWeight.Bold)
                        }
                    }

                    Text("Select Player:", color = PureWhite, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    LazyColumn(modifier = Modifier.height(140.dp)) {
                        items(pitchSlots) { slot ->
                            val isSelected = selectedCardSlot?.slotId == slot.slotId
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 2.dp)
                                    .background(
                                        if (isSelected) MambaAccentMint.copy(alpha = 0.2f) else Color.Transparent,
                                        RoundedCornerShape(6.dp)
                                    )
                                    .clickable { selectedCardSlot = slot }
                                    .padding(8.dp)
                            ) {
                                Text(
                                    text = "#${slot.jerseyNumber} ${slot.playerName} (${slot.roleLabel})",
                                    color = if (isSelected) MambaAccentMint else PureWhite,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val slot = selectedCardSlot
                        if (slot != null) {
                            viewModel.recordCard(slot, isRedCard)
                        }
                        showCardDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isRedCard) CardRed else CardYellow
                    )
                ) {
                    Text("BOOK", color = if (isRedCard) PureWhite else Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCardDialog = false }) {
                    Text("CANCEL", color = PureWhite)
                }
            }
        )
    }

    // DIALOG: Edit Opponent Name
    if (showEditOpponentDialog && activeMatch != null) {
        var opponentNameInput by remember { mutableStateOf(activeMatch!!.opponentName) }

        AlertDialog(
            onDismissRequest = { showEditOpponentDialog = false },
            containerColor = MambaGreenSurface,
            title = {
                Text("Edit Opponent Name", color = PureWhite, fontWeight = FontWeight.Bold)
            },
            text = {
                OutlinedTextField(
                    value = opponentNameInput,
                    onValueChange = { opponentNameInput = it },
                    label = { Text("Opponent Team", color = PureWhite.copy(alpha = 0.7f)) },
                    colors = androidx.compose.material3.TextFieldDefaults.colors(
                        focusedTextColor = PureWhite,
                        unfocusedTextColor = PureWhite,
                        focusedContainerColor = MambaGreenDark,
                        unfocusedContainerColor = MambaGreenDark
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.startNewMatch(
                            opponent = opponentNameInput,
                            format = activeMatch!!.matchFormat,
                            formation = activeMatch!!.formationName,
                            durationMinutes = activeMatch!!.durationMinutes
                        )
                        showEditOpponentDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MambaAccentMint)
                ) {
                    Text("UPDATE", color = MambaGreenDark, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditOpponentDialog = false }) {
                    Text("CANCEL", color = PureWhite)
                }
            }
        )
    }
}
