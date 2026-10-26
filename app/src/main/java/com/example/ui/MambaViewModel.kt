package com.example.ui

import android.app.Application
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.MambaDatabase
import com.example.data.model.FormationLibrary
import com.example.data.model.MatchEntity
import com.example.data.model.MatchEventEntity
import com.example.data.model.PitchPlayerSlot
import com.example.data.model.PlayerEntity
import com.example.data.model.PlayerPosition
import com.example.data.repository.MambaRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.UUID

class MambaViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: MambaRepository

    init {
        val db = MambaDatabase.getDatabase(application)
        repository = MambaRepository(db)
        viewModelScope.launch {
            repository.ensureDefaultSquadLoaded()
        }
    }

    val squad: StateFlow<List<PlayerEntity>> = repository.allPlayers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allMatches: StateFlow<List<MatchEntity>> = repository.allMatches
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeMatch: StateFlow<MatchEntity?> = repository.activeMatch
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val _activeMatchEvents = MutableStateFlow<List<MatchEventEntity>>(emptyList())
    val activeMatchEvents: StateFlow<List<MatchEventEntity>> = _activeMatchEvents.asStateFlow()

    private val _pitchSlots = MutableStateFlow<List<PitchPlayerSlot>>(emptyList())
    val pitchSlots: StateFlow<List<PitchPlayerSlot>> = _pitchSlots.asStateFlow()

    // Formations Screen State
    private val _selectedFormat = MutableStateFlow("7-a-side")
    val selectedFormat: StateFlow<String> = _selectedFormat.asStateFlow()

    private val _selectedFormationName = MutableStateFlow("2-3-1 (Classic 7s)")
    val selectedFormationName: StateFlow<String> = _selectedFormationName.asStateFlow()

    private val _tacticalBoardSlots = MutableStateFlow<List<PitchPlayerSlot>>(emptyList())
    val tacticalBoardSlots: StateFlow<List<PitchPlayerSlot>> = _tacticalBoardSlots.asStateFlow()

    // Timer Job
    private var timerJob: Job? = null

    init {
        // Collect active match and sync events/slots
        viewModelScope.launch {
            activeMatch.collect { match ->
                if (match != null) {
                    val slots = repository.deserializeSlotsFromJson(match.activeLineupJson)
                    _pitchSlots.value = slots

                    // Collect events for active match
                    launch {
                        repository.getMatchEvents(match.id).collect { events ->
                            _activeMatchEvents.value = events
                        }
                    }

                    if (match.isRunning && timerJob?.isActive != true) {
                        startTimerLoop()
                    } else if (!match.isRunning) {
                        timerJob?.cancel()
                    }
                } else {
                    _pitchSlots.value = emptyList()
                    _activeMatchEvents.value = emptyList()
                    timerJob?.cancel()
                }
            }
        }

        // Initialize tactical board
        viewModelScope.launch {
            squad.collect { list ->
                if (list.isNotEmpty() && _tacticalBoardSlots.value.isEmpty()) {
                    loadTacticalFormation(_selectedFormat.value, _selectedFormationName.value)
                }
            }
        }
    }

    // Vibrator feedback for quick-tap inputs
    private fun triggerHaptic() {
        try {
            val context = getApplication<Application>()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vibratorManager?.defaultVibrator?.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK))
            } else {
                @Suppress("DEPRECATION")
                val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator?.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))
                } else {
                    @Suppress("DEPRECATION")
                    vibrator?.vibrate(50)
                }
            }
        } catch (_: Exception) {}
    }

    private fun startTimerLoop() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (isActive) {
                delay(1000)
                val current = activeMatch.value
                if (current != null && current.isRunning) {
                    val nextSec = current.elapsedSeconds + 1
                    repository.updateMatch(current.copy(elapsedSeconds = nextSec))
                }
            }
        }
    }

    // MATCH CONTROLS
    fun startNewMatch(
        opponent: String = "Opponent FC",
        format: String = "7-a-side",
        formation: String = "2-3-1",
        durationMinutes: Int = 70
    ) {
        viewModelScope.launch {
            repository.createNewMatch(opponent, format, formation, durationMinutes)
            triggerHaptic()
        }
    }

    fun toggleMatchTimer() {
        val current = activeMatch.value ?: return
        viewModelScope.launch {
            val updated = current.copy(isRunning = !current.isRunning)
            repository.updateMatch(updated)
            triggerHaptic()
        }
    }

    fun adjustTimer(deltaMinutes: Int) {
        val current = activeMatch.value ?: return
        viewModelScope.launch {
            val newSec = maxOf(0, current.elapsedSeconds + deltaMinutes * 60)
            repository.updateMatch(current.copy(elapsedSeconds = newSec))
            triggerHaptic()
        }
    }

    fun advanceMatchPeriod() {
        val current = activeMatch.value ?: return
        viewModelScope.launch {
            val (nextPeriod, isRunning) = when (current.currentPeriod) {
                "NOT_STARTED" -> "FIRST_HALF" to true
                "FIRST_HALF" -> "HALF_TIME" to false
                "HALF_TIME" -> "SECOND_HALF" to true
                "SECOND_HALF" -> "FULL_TIME" to false
                else -> "FULL_TIME" to false
            }

            // Log whistle event
            val whistleEvent = MatchEventEntity(
                id = UUID.randomUUID().toString(),
                matchId = current.id,
                minute = current.elapsedSeconds / 60,
                period = nextPeriod,
                type = "WHISTLE",
                note = "Whistle blown: $nextPeriod"
            )
            repository.recordEvent(whistleEvent, updateScores = false)

            if (nextPeriod == "FULL_TIME") {
                repository.finishMatch(current)
            } else {
                repository.updateMatch(current.copy(currentPeriod = nextPeriod, isRunning = isRunning))
            }
            triggerHaptic()
        }
    }

    fun finishCurrentMatch() {
        val current = activeMatch.value ?: return
        viewModelScope.launch {
            repository.finishMatch(current)
            triggerHaptic()
        }
    }

    fun deleteMatch(matchId: String) {
        viewModelScope.launch {
            repository.deleteMatch(matchId)
        }
    }

    // STATS & EVENT RECORDING
    fun recordGoal(
        scorer: PitchPlayerSlot,
        assist: PitchPlayerSlot? = null
    ) {
        val current = activeMatch.value ?: return
        viewModelScope.launch {
            val minute = (current.elapsedSeconds / 60) + 1
            val event = MatchEventEntity(
                id = UUID.randomUUID().toString(),
                matchId = current.id,
                minute = minute,
                period = current.currentPeriod,
                type = "GOAL",
                playerId = scorer.playerId,
                playerName = scorer.playerName,
                playerJersey = scorer.jerseyNumber,
                assistPlayerId = assist?.playerId,
                assistPlayerName = assist?.playerName,
                assistPlayerJersey = assist?.jerseyNumber,
                note = if (assist != null) "Goal by #${scorer.jerseyNumber} ${scorer.playerName} (Assist: #${assist.jerseyNumber} ${assist.playerName})"
                       else "Goal by #${scorer.jerseyNumber} ${scorer.playerName}"
            )
            repository.recordEvent(event)

            // Update on-pitch slot stats
            val updatedSlots = _pitchSlots.value.map { slot ->
                when (slot.slotId) {
                    scorer.slotId -> slot.copy(goalsInMatch = slot.goalsInMatch + 1)
                    assist?.slotId -> slot.copy(assistsInMatch = slot.assistsInMatch + 1)
                    else -> slot
                }
            }
            _pitchSlots.value = updatedSlots
            repository.updateMatch(current.copy(
                activeLineupJson = repository.serializeSlotsToJson(updatedSlots),
                homeScore = current.homeScore + 1
            ))
            triggerHaptic()
        }
    }

    fun recordOpponentGoal() {
        val current = activeMatch.value ?: return
        viewModelScope.launch {
            val minute = (current.elapsedSeconds / 60) + 1
            val event = MatchEventEntity(
                id = UUID.randomUUID().toString(),
                matchId = current.id,
                minute = minute,
                period = current.currentPeriod,
                type = "OPPONENT_GOAL",
                note = "Goal for ${current.opponentName}"
            )
            repository.recordEvent(event)
            repository.updateMatch(current.copy(awayScore = current.awayScore + 1))
            triggerHaptic()
        }
    }

    fun recordSubstitution(slotOut: PitchPlayerSlot, playerIn: PlayerEntity) {
        val current = activeMatch.value ?: return
        viewModelScope.launch {
            val minute = (current.elapsedSeconds / 60) + 1
            val event = MatchEventEntity(
                id = UUID.randomUUID().toString(),
                matchId = current.id,
                minute = minute,
                period = current.currentPeriod,
                type = "SUBSTITUTION",
                playerId = slotOut.playerId,
                playerName = slotOut.playerName,
                playerJersey = slotOut.jerseyNumber,
                subInPlayerId = playerIn.id,
                subInPlayerName = playerIn.name,
                subInPlayerJersey = playerIn.jerseyNumber,
                note = "Sub: #${playerIn.jerseyNumber} ${playerIn.name} ON ⇄ #${slotOut.jerseyNumber} ${slotOut.playerName} OFF"
            )
            repository.recordEvent(event, updateScores = false)

            // Swap player in on-pitch slot
            val updatedSlots = _pitchSlots.value.map { slot ->
                if (slot.slotId == slotOut.slotId) {
                    slot.copy(
                        playerId = playerIn.id,
                        playerName = playerIn.name,
                        jerseyNumber = playerIn.jerseyNumber,
                        category = playerIn.position
                    )
                } else slot
            }
            _pitchSlots.value = updatedSlots
            repository.updateMatch(current.copy(activeLineupJson = repository.serializeSlotsToJson(updatedSlots)))
            triggerHaptic()
        }
    }

    fun recordCard(slot: PitchPlayerSlot, isRed: Boolean) {
        val current = activeMatch.value ?: return
        viewModelScope.launch {
            val minute = (current.elapsedSeconds / 60) + 1
            val type = if (isRed) "RED_CARD" else "YELLOW_CARD"
            val event = MatchEventEntity(
                id = UUID.randomUUID().toString(),
                matchId = current.id,
                minute = minute,
                period = current.currentPeriod,
                type = type,
                playerId = slot.playerId,
                playerName = slot.playerName,
                playerJersey = slot.jerseyNumber,
                note = if (isRed) "Red Card to #${slot.jerseyNumber} ${slot.playerName}" else "Yellow Card to #${slot.jerseyNumber} ${slot.playerName}"
            )
            repository.recordEvent(event, updateScores = false)

            val updatedSlots = _pitchSlots.value.map { s ->
                if (s.slotId == slot.slotId) {
                    if (isRed) s.copy(redCards = s.redCards + 1) else s.copy(yellowCards = s.yellowCards + 1)
                } else s
            }
            _pitchSlots.value = updatedSlots
            repository.updateMatch(current.copy(activeLineupJson = repository.serializeSlotsToJson(updatedSlots)))
            triggerHaptic()
        }
    }

    fun undoLastEvent() {
        val current = activeMatch.value ?: return
        viewModelScope.launch {
            repository.undoLastEvent(current.id)
            triggerHaptic()
        }
    }

    // TACTICAL FORMATION BOARD
    fun selectFormat(format: String) {
        _selectedFormat.value = format
        val presets = FormationLibrary.getPresetsForFormat(format)
        val defaultPreset = presets.firstOrNull()?.name ?: "Formation"
        _selectedFormationName.value = defaultPreset
        loadTacticalFormation(format, defaultPreset)
    }

    fun selectFormationName(name: String) {
        _selectedFormationName.value = name
        loadTacticalFormation(_selectedFormat.value, name)
    }

    fun loadTacticalFormation(format: String, formationName: String) {
        viewModelScope.launch {
            val slots = repository.generateInitialSlotsForFormation(format, formationName)
            _tacticalBoardSlots.value = slots
        }
    }

    fun updateTacticalSlotPlayer(slotId: String, newName: String, newNumber: Int, newRole: String) {
        _tacticalBoardSlots.value = _tacticalBoardSlots.value.map { slot ->
            if (slot.slotId == slotId) {
                slot.copy(playerName = newName, jerseyNumber = newNumber, roleLabel = newRole)
            } else slot
        }
    }

    fun updateTacticalSlotPosition(slotId: String, normX: Float, normY: Float) {
        _tacticalBoardSlots.value = _tacticalBoardSlots.value.map { slot ->
            if (slot.slotId == slotId) {
                slot.copy(normX = normX, normY = normY)
            } else slot
        }
    }

    fun applyTacticalLineupToActiveMatch() {
        val current = activeMatch.value ?: return
        viewModelScope.launch {
            val lineupJson = repository.serializeSlotsToJson(_tacticalBoardSlots.value)
            repository.updateMatch(current.copy(
                matchFormat = _selectedFormat.value,
                formationName = _selectedFormationName.value,
                activeLineupJson = lineupJson
            ))
            _pitchSlots.value = _tacticalBoardSlots.value
            triggerHaptic()
        }
    }

    // SQUAD MANAGEMENT
    fun addOrUpdatePlayer(player: PlayerEntity) {
        viewModelScope.launch {
            repository.savePlayer(player)
            triggerHaptic()
        }
    }

    fun deletePlayer(player: PlayerEntity) {
        viewModelScope.launch {
            repository.deletePlayer(player)
            triggerHaptic()
        }
    }

    // JSON EXPORT / IMPORT
    suspend fun exportDataJson(): String = repository.exportDataJson()

    suspend fun importDataJson(jsonStr: String): Boolean = repository.importDataJson(jsonStr)

    // SUMMARY REPORT TEXT GENERATOR
    fun generateMatchReportText(match: MatchEntity, events: List<MatchEventEntity>): String {
        val sb = StringBuilder()
        sb.append("⚽ *FC GREEN MAMBA MATCH REPORT* ⚽\n")
        sb.append("━━━━━━━━━━━━━━━━━━━━\n")
        val resultEmoji = if (match.homeScore > match.awayScore) "🏆 WIN"
                          else if (match.homeScore == match.awayScore) "🤝 DRAW"
                          else "❌ DEFEAT"
        sb.append("Status: $resultEmoji\n")
        sb.append("FC GREEN MAMBA ${match.homeScore} - ${match.awayScore} ${match.opponentName}\n")
        sb.append("Format: ${match.matchFormat} (${match.formationName})\n")
        val mins = match.elapsedSeconds / 60
        sb.append("Duration: ${mins} mins played\n\n")

        val goals = events.filter { it.type == "GOAL" }
        if (goals.isNotEmpty()) {
            sb.append("🎯 *Mamba Goals & Assists:*\n")
            goals.forEach { g ->
                val assistPart = if (!g.assistPlayerName.isNullOrBlank()) " (Assist: #${g.assistPlayerJersey} ${g.assistPlayerName})" else ""
                sb.append("• ${g.minute}' ⚽ #${g.playerJersey} ${g.playerName}$assistPart\n")
            }
            sb.append("\n")
        }

        val subs = events.filter { it.type == "SUBSTITUTION" }
        if (subs.isNotEmpty()) {
            sb.append("🔁 *Substitutions:*\n")
            subs.forEach { s ->
                sb.append("• ${s.minute}' ⇄ #${s.subInPlayerJersey} ${s.subInPlayerName} IN for #${s.playerJersey} ${s.playerName}\n")
            }
            sb.append("\n")
        }

        sb.append("📋 *Full Timeline:*\n")
        events.forEach { e ->
            val icon = when (e.type) {
                "GOAL" -> "⚽"
                "OPPONENT_GOAL" -> "🔴"
                "SUBSTITUTION" -> "🔁"
                "YELLOW_CARD" -> "🟨"
                "RED_CARD" -> "🟥"
                else -> "⏱️"
            }
            sb.append("${e.minute}' $icon ${e.note}\n")
        }
        sb.append("\n#FCGreenMamba #GreenAndWhite #TeamFirst")
        return sb.toString()
    }
}
