package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PlayerEntity
import com.example.data.model.PlayerPosition
import com.example.ui.MambaViewModel
import com.example.ui.theme.AssistCyan
import com.example.ui.theme.GoalGold
import com.example.ui.theme.MambaAccentMint
import com.example.ui.theme.MambaGreenBorder
import com.example.ui.theme.MambaGreenDark
import com.example.ui.theme.MambaGreenPrimary
import com.example.ui.theme.MambaGreenSurface
import com.example.ui.theme.PureWhite
import kotlinx.coroutines.launch
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SquadManagementScreen(
    viewModel: MambaViewModel
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val squad by viewModel.squad.collectAsState()

    var selectedFilter by remember { mutableStateOf("ALL") }
    var editingPlayer by remember { mutableStateOf<PlayerEntity?>(null) }
    var isAddingNewPlayer by remember { mutableStateOf(false) }
    var showImportDialog by remember { mutableStateOf(false) }

    val filterOptions = listOf("ALL", "GK", "DEF", "MID", "FWD")
    val filteredSquad = when (selectedFilter) {
        "ALL" -> squad
        else -> squad.filter { it.position == selectedFilter }
    }

    Scaffold(
        containerColor = MambaGreenDark,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { isAddingNewPlayer = true },
                containerColor = MambaAccentMint,
                contentColor = MambaGreenDark,
                modifier = Modifier.testTag("add_player_fab")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Player")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "FC GREEN MAMBA SQUAD",
                        color = PureWhite,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = "${squad.size} registered players • Offline-ready",
                        color = PureWhite.copy(alpha = 0.7f),
                        fontSize = 12.sp
                    )
                }

                // Sync / Export Button
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    IconButton(
                        onClick = {
                            coroutineScope.launch {
                                val json = viewModel.exportDataJson()
                                val sendIntent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_TEXT, json)
                                    type = "text/plain"
                                }
                                context.startActivity(Intent.createChooser(sendIntent, "Share FC Green Mamba Data"))
                            }
                        },
                        modifier = Modifier
                            .background(MambaGreenSurface, RoundedCornerShape(8.dp))
                            .border(1.dp, MambaGreenBorder, RoundedCornerShape(8.dp))
                            .size(40.dp)
                    ) {
                        Icon(Icons.Default.Share, contentDescription = "Export Data", tint = MambaAccentMint)
                    }

                    IconButton(
                        onClick = { showImportDialog = true },
                        modifier = Modifier
                            .background(MambaGreenSurface, RoundedCornerShape(8.dp))
                            .border(1.dp, MambaGreenBorder, RoundedCornerShape(8.dp))
                            .size(40.dp)
                    ) {
                        Icon(Icons.Default.Download, contentDescription = "Import Data", tint = PureWhite)
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Filter Tabs
            ScrollableTabRow(
                selectedTabIndex = filterOptions.indexOf(selectedFilter).coerceAtLeast(0),
                containerColor = MambaGreenSurface,
                contentColor = MambaAccentMint,
                edgePadding = 8.dp
            ) {
                filterOptions.forEach { filter ->
                    Tab(
                        selected = selectedFilter == filter,
                        onClick = { selectedFilter = filter },
                        text = {
                            Text(
                                text = filter,
                                fontWeight = if (selectedFilter == filter) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedFilter == filter) MambaAccentMint else PureWhite.copy(alpha = 0.7f),
                                fontSize = 12.sp
                            )
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Player List
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(filteredSquad) { player ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MambaGreenSurface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MambaGreenBorder),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { editingPlayer = player }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            // Left: Jersey Badge & Info
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = CircleShape,
                                    color = MambaGreenPrimary,
                                    modifier = Modifier.size(42.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = "#${player.jerseyNumber}",
                                            color = PureWhite,
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 15.sp
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = player.name,
                                            color = PureWhite,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp
                                        )
                                        if (player.nickname.isNotBlank()) {
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = "\"${player.nickname}\"",
                                                color = MambaAccentMint,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Medium
                                            )
                                        }
                                    }
                                    Text(
                                        text = "${player.position} • ${player.appearances} Apps",
                                        color = PureWhite.copy(alpha = 0.6f),
                                        fontSize = 12.sp
                                    )
                                }
                            }

                            // Right: Career Goals & Assists + Edit Icon
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (player.totalGoals > 0) {
                                    Text(
                                        text = "⚽ ${player.totalGoals}",
                                        color = GoalGold,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        modifier = Modifier.padding(end = 8.dp)
                                    )
                                }
                                if (player.totalAssists > 0) {
                                    Text(
                                        text = "👟 ${player.totalAssists}",
                                        color = AssistCyan,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        modifier = Modifier.padding(end = 8.dp)
                                    )
                                }
                                IconButton(
                                    onClick = { editingPlayer = player },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(Icons.Default.Edit, contentDescription = "Edit Player", tint = PureWhite.copy(alpha = 0.7f))
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // DIALOG: Add or Edit Player
    if (isAddingNewPlayer || editingPlayer != null) {
        val player = editingPlayer
        var nameInput by remember { mutableStateOf(player?.name ?: "") }
        var jerseyInput by remember { mutableStateOf(player?.jerseyNumber?.toString() ?: "") }
        var positionInput by remember { mutableStateOf(player?.position ?: "MID") }
        var nicknameInput by remember { mutableStateOf(player?.nickname ?: "") }

        val positions = listOf("GK", "DEF", "MID", "FWD")

        AlertDialog(
            onDismissRequest = {
                isAddingNewPlayer = false
                editingPlayer = null
            },
            containerColor = MambaGreenSurface,
            title = {
                Text(
                    text = if (player == null) "Add Squad Member" else "Edit ${player.name}",
                    color = PureWhite,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
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
                            value = nicknameInput,
                            onValueChange = { nicknameInput = it },
                            label = { Text("Nickname", color = PureWhite.copy(alpha = 0.7f)) },
                            colors = androidx.compose.material3.TextFieldDefaults.colors(
                                focusedTextColor = PureWhite,
                                unfocusedTextColor = PureWhite,
                                focusedContainerColor = MambaGreenDark,
                                unfocusedContainerColor = MambaGreenDark
                            ),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Text("Position", color = PureWhite, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        positions.forEach { pos ->
                            OutlinedButton(
                                onClick = { positionInput = pos },
                                colors = ButtonDefaults.outlinedButtonColors(
                                    containerColor = if (positionInput == pos) MambaAccentMint else Color.Transparent,
                                    contentColor = if (positionInput == pos) MambaGreenDark else PureWhite
                                ),
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (positionInput == pos) MambaAccentMint else PureWhite.copy(alpha = 0.4f)
                                ),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(pos, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val num = jerseyInput.toIntOrNull() ?: 10
                        val target = PlayerEntity(
                            id = player?.id ?: UUID.randomUUID().toString(),
                            name = nameInput.ifBlank { "Green Mamba Player" },
                            jerseyNumber = num,
                            position = positionInput,
                            nickname = nicknameInput,
                            appearances = player?.appearances ?: 0,
                            totalGoals = player?.totalGoals ?: 0,
                            totalAssists = player?.totalAssists ?: 0
                        )
                        viewModel.addOrUpdatePlayer(target)
                        isAddingNewPlayer = false
                        editingPlayer = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MambaAccentMint),
                    enabled = nameInput.isNotBlank()
                ) {
                    Text("SAVE", color = MambaGreenDark, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                Row {
                    if (player != null) {
                        TextButton(
                            onClick = {
                                viewModel.deletePlayer(player)
                                editingPlayer = null
                            }
                        ) {
                            Text("DELETE", color = Color(0xFFFF5252))
                        }
                    }
                    TextButton(
                        onClick = {
                            isAddingNewPlayer = false
                            editingPlayer = null
                        }
                    ) {
                        Text("CANCEL", color = PureWhite)
                    }
                }
            }
        )
    }

    // DIALOG: Import Data JSON
    if (showImportDialog) {
        var jsonText by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showImportDialog = false },
            containerColor = MambaGreenSurface,
            title = {
                Text("Import Squad / Sync Data", color = PureWhite, fontWeight = FontWeight.Bold)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Paste JSON exported from a teammate's device to sync your squad offline instantly:",
                        color = PureWhite.copy(alpha = 0.8f),
                        fontSize = 12.sp
                    )
                    OutlinedTextField(
                        value = jsonText,
                        onValueChange = { jsonText = it },
                        placeholder = { Text("Paste JSON here...", color = PureWhite.copy(alpha = 0.4f)) },
                        colors = androidx.compose.material3.TextFieldDefaults.colors(
                            focusedTextColor = PureWhite,
                            unfocusedTextColor = PureWhite,
                            focusedContainerColor = MambaGreenDark,
                            unfocusedContainerColor = MambaGreenDark
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        coroutineScope.launch {
                            val success = viewModel.importDataJson(jsonText)
                            if (success) {
                                Toast.makeText(context, "Squad synced successfully!", Toast.LENGTH_SHORT).show()
                                showImportDialog = false
                            } else {
                                Toast.makeText(context, "Invalid JSON format", Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MambaAccentMint),
                    enabled = jsonText.isNotBlank()
                ) {
                    Text("IMPORT & SYNC", color = MambaGreenDark, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showImportDialog = false }) {
                    Text("CANCEL", color = PureWhite)
                }
            }
        )
    }
}
