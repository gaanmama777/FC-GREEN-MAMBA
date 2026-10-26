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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.FormationLibrary
import com.example.data.model.PitchPlayerSlot
import com.example.data.model.PlayerEntity
import com.example.ui.MambaViewModel
import com.example.ui.components.TacticalPitchView
import com.example.ui.theme.GoalGold
import com.example.ui.theme.MambaAccentMint
import com.example.ui.theme.MambaGreenBorder
import com.example.ui.theme.MambaGreenDark
import com.example.ui.theme.MambaGreenPrimary
import com.example.ui.theme.MambaGreenSurface
import com.example.ui.theme.PureWhite

@Composable
fun TacticalFormationScreen(
    viewModel: MambaViewModel,
    onNavigateToMatch: () -> Unit
) {
    val selectedFormat by viewModel.selectedFormat.collectAsState()
    val selectedFormationName by viewModel.selectedFormationName.collectAsState()
    val tacticalSlots by viewModel.tacticalBoardSlots.collectAsState()
    val squad by viewModel.squad.collectAsState()
    val activeMatch by viewModel.activeMatch.collectAsState()

    var editingSlot by remember { mutableStateOf<PitchPlayerSlot?>(null) }
    var isDragModeEnabled by remember { mutableStateOf(false) }

    val formatOptions = FormationLibrary.ALL_FORMATS
    val presetOptions = FormationLibrary.getPresetsForFormat(selectedFormat)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MambaGreenDark)
            .padding(12.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Tactical Formation Summary",
                    color = PureWhite,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = "Tap player to edit name, jersey # or drag positions",
                    color = PureWhite.copy(alpha = 0.7f),
                    fontSize = 12.sp
                )
            }

            // Mode toggle (Drag positions / Edit info)
            FilterChip(
                selected = isDragModeEnabled,
                onClick = { isDragModeEnabled = !isDragModeEnabled },
                label = {
                    Text(
                        if (isDragModeEnabled) "DRAGGING ON" else "TAP TO EDIT",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MambaAccentMint,
                    selectedLabelColor = MambaGreenDark,
                    labelColor = PureWhite
                )
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Format selector (5/6/7/8/9/11 a side)
        ScrollableTabRow(
            selectedTabIndex = formatOptions.indexOf(selectedFormat).coerceAtLeast(0),
            containerColor = MambaGreenSurface,
            contentColor = MambaAccentMint,
            edgePadding = 8.dp
        ) {
            formatOptions.forEach { fmt ->
                Tab(
                    selected = selectedFormat == fmt,
                    onClick = { viewModel.selectFormat(fmt) },
                    text = {
                        Text(
                            text = fmt,
                            fontWeight = if (selectedFormat == fmt) FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedFormat == fmt) MambaAccentMint else PureWhite.copy(alpha = 0.7f),
                            fontSize = 12.sp
                        )
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Presets Row for selected format
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(presetOptions) { preset ->
                val isSelected = selectedFormationName == preset.name
                FilterChip(
                    selected = isSelected,
                    onClick = { viewModel.selectFormationName(preset.name) },
                    label = { Text(preset.name, fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MambaGreenPrimary,
                        selectedLabelColor = PureWhite,
                        containerColor = MambaGreenSurface,
                        labelColor = PureWhite.copy(alpha = 0.8f)
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Interactive Pitch Visualizer
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            TacticalPitchView(
                slots = tacticalSlots,
                isInteractive = !isDragModeEnabled,
                isDraggable = isDragModeEnabled,
                onPlayerClick = { slot ->
                    editingSlot = slot
                },
                onPlayerDragEnd = { slot, newNormX, newNormY ->
                    viewModel.updateTacticalSlotPosition(slot.slotId, newNormX, newNormY)
                },
                modifier = Modifier.fillMaxSize()
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Action Buttons Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                onClick = { viewModel.loadTacticalFormation(selectedFormat, selectedFormationName) },
                border = androidx.compose.foundation.BorderStroke(1.dp, PureWhite.copy(alpha = 0.4f)),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.weight(1f).height(44.dp)
            ) {
                Icon(Icons.Default.Refresh, contentDescription = null, tint = PureWhite, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("RESET PRESET", color = PureWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = {
                    if (activeMatch != null) {
                        viewModel.applyTacticalLineupToActiveMatch()
                        onNavigateToMatch()
                    } else {
                        viewModel.startNewMatch(
                            opponent = "Opponent FC",
                            format = selectedFormat,
                            formation = selectedFormationName,
                            durationMinutes = 70
                        )
                        viewModel.applyTacticalLineupToActiveMatch()
                        onNavigateToMatch()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = MambaAccentMint),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .weight(1.3f)
                    .height(44.dp)
                    .testTag("apply_formation_button")
            ) {
                Icon(Icons.Default.PlayArrow, contentDescription = null, tint = MambaGreenDark, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = if (activeMatch != null) "APPLY TO MATCH" else "START WITH LINEUP",
                    color = MambaGreenDark,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black
                )
            }
        }
    }

    // DIALOG: Quick-Edit Player Slot (Name, Jersey Number, Role, Swap with Squad)
    if (editingSlot != null) {
        val slot = editingSlot!!
        var nameInput by remember { mutableStateOf(slot.playerName) }
        var jerseyInput by remember { mutableStateOf(slot.jerseyNumber.toString()) }
        var roleInput by remember { mutableStateOf(slot.roleLabel) }
        var showRosterPicker by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { editingSlot = null },
            containerColor = MambaGreenSurface,
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Edit Tactical Slot: ${slot.roleLabel}",
                        color = PureWhite,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp
                    )
                    Surface(
                        shape = CircleShape,
                        color = MambaGreenPrimary,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text("#${slot.jerseyNumber}", color = PureWhite, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Quick Pick from Registered Squad
                    Button(
                        onClick = { showRosterPicker = !showRosterPicker },
                        colors = ButtonDefaults.buttonColors(containerColor = MambaGreenDark),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.SwapHoriz, contentDescription = null, tint = MambaAccentMint)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("ASSIGN FROM SQUAD ROSTER", color = MambaAccentMint, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    if (showRosterPicker) {
                        Text("Select Player:", color = PureWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        LazyColumn(modifier = Modifier.height(130.dp)) {
                            items(squad) { p ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 2.dp)
                                        .background(MambaGreenDark, RoundedCornerShape(6.dp))
                                        .clickable {
                                            nameInput = p.name
                                            jerseyInput = p.jerseyNumber.toString()
                                            roleInput = p.position
                                            showRosterPicker = false
                                        }
                                        .padding(8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("#${p.jerseyNumber} ${p.name}", color = PureWhite, fontSize = 13.sp)
                                    Text(p.position, color = MambaAccentMint, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    OutlinedTextField(
                        value = nameInput,
                        onValueChange = { nameInput = it },
                        label = { Text("Player Name", color = PureWhite.copy(alpha = 0.7f)) },
                        colors = androidx.compose.material3.TextFieldDefaults.colors(
                            focusedTextColor = PureWhite,
                            unfocusedTextColor = PureWhite,
                            focusedContainerColor = MambaGreenDark,
                            unfocusedContainerColor = MambaGreenDark
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = jerseyInput,
                            onValueChange = { jerseyInput = it.filter { ch -> ch.isDigit() } },
                            label = { Text("Jersey #", color = PureWhite.copy(alpha = 0.7f)) },
                            colors = androidx.compose.material3.TextFieldDefaults.colors(
                                focusedTextColor = PureWhite,
                                unfocusedTextColor = PureWhite,
                                focusedContainerColor = MambaGreenDark,
                                unfocusedContainerColor = MambaGreenDark
                            ),
                            modifier = Modifier.weight(1f)
                        )

                        OutlinedTextField(
                            value = roleInput,
                            onValueChange = { roleInput = it.take(4).uppercase() },
                            label = { Text("Role (e.g. ST)", color = PureWhite.copy(alpha = 0.7f)) },
                            colors = androidx.compose.material3.TextFieldDefaults.colors(
                                focusedTextColor = PureWhite,
                                unfocusedTextColor = PureWhite,
                                focusedContainerColor = MambaGreenDark,
                                unfocusedContainerColor = MambaGreenDark
                            ),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val num = jerseyInput.toIntOrNull() ?: slot.jerseyNumber
                        viewModel.updateTacticalSlotPlayer(
                            slotId = slot.slotId,
                            newName = nameInput.ifBlank { slot.playerName },
                            newNumber = num,
                            newRole = roleInput.ifBlank { slot.roleLabel }
                        )
                        editingSlot = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MambaAccentMint)
                ) {
                    Text("SAVE CHANGES", color = MambaGreenDark, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { editingSlot = null }) {
                    Text("CANCEL", color = PureWhite)
                }
            }
        )
    }
}
