package com.example.data.repository

import com.example.data.local.MambaDatabase
import com.example.data.model.FormationLibrary
import com.example.data.model.MatchEntity
import com.example.data.model.MatchEventEntity
import com.example.data.model.PitchPlayerSlot
import com.example.data.model.PlayerEntity
import com.example.data.model.PlayerPosition
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

class MambaRepository(private val database: MambaDatabase) {
    val allPlayers: Flow<List<PlayerEntity>> = database.playerDao().getAllActivePlayers()
    val allMatches: Flow<List<MatchEntity>> = database.matchDao().getAllMatches()
    val activeMatch: Flow<MatchEntity?> = database.matchDao().getActiveMatch()

    fun getMatchEvents(matchId: String): Flow<List<MatchEventEntity>> =
        database.matchEventDao().getEventsForMatch(matchId)

    suspend fun ensureDefaultSquadLoaded() = withContext(Dispatchers.IO) {
        val current = allPlayers.first()
        if (current.isEmpty()) {
            val defaults = listOf(
                PlayerEntity(UUID.randomUUID().toString(), "Sam Wall", 1, PlayerPosition.GOALKEEPER.shortLabel, "The Wall", appearances = 12),
                PlayerEntity(UUID.randomUUID().toString(), "Jamal Stone", 4, PlayerPosition.DEFENDER.shortLabel, "Rock", appearances = 14, totalGoals = 1),
                PlayerEntity(UUID.randomUUID().toString(), "David Quick", 3, PlayerPosition.DEFENDER.shortLabel, "Speedy", appearances = 10, totalAssists = 2),
                PlayerEntity(UUID.randomUUID().toString(), "Omar Shield", 5, PlayerPosition.DEFENDER.shortLabel, "Shield", appearances = 11),
                PlayerEntity(UUID.randomUUID().toString(), "Karim Silva", 10, PlayerPosition.MIDFIELDER.shortLabel, "Maestro", appearances = 15, totalGoals = 8, totalAssists = 12),
                PlayerEntity(UUID.randomUUID().toString(), "Lucas Modric", 8, PlayerPosition.MIDFIELDER.shortLabel, "Engine", appearances = 14, totalGoals = 3, totalAssists = 6),
                PlayerEntity(UUID.randomUUID().toString(), "Mateo Busquets", 6, PlayerPosition.MIDFIELDER.shortLabel, "Anchor", appearances = 13, totalAssists = 3),
                PlayerEntity(UUID.randomUUID().toString(), "Tariq Mamba", 9, PlayerPosition.FORWARD.shortLabel, "Viper", appearances = 15, totalGoals = 18, totalAssists = 5),
                PlayerEntity(UUID.randomUUID().toString(), "Alex Cruz", 7, PlayerPosition.FORWARD.shortLabel, "Sniper", appearances = 12, totalGoals = 9, totalAssists = 4),
                PlayerEntity(UUID.randomUUID().toString(), "Marcus Cole", 11, PlayerPosition.FORWARD.shortLabel, "Flash", appearances = 8, totalGoals = 4, totalAssists = 2),
                PlayerEntity(UUID.randomUUID().toString(), "Nathan Young", 14, PlayerPosition.MIDFIELDER.shortLabel, "Playmaker", appearances = 7, totalGoals = 2, totalAssists = 3),
                PlayerEntity(UUID.randomUUID().toString(), "Leo Safe", 13, PlayerPosition.GOALKEEPER.shortLabel, "Safehands", appearances = 4)
            )
            database.playerDao().insertAll(defaults)
        }
    }

    suspend fun savePlayer(player: PlayerEntity) = withContext(Dispatchers.IO) {
        database.playerDao().insertOrUpdate(player)
    }

    suspend fun deletePlayer(player: PlayerEntity) = withContext(Dispatchers.IO) {
        database.playerDao().delete(player)
    }

    suspend fun createNewMatch(
        opponent: String,
        format: String,
        formation: String,
        durationMinutes: Int
    ): MatchEntity = withContext(Dispatchers.IO) {
        val matchId = UUID.randomUUID().toString()
        val slots = generateInitialSlotsForFormation(format, formation)
        val lineupJson = serializeSlotsToJson(slots)

        val newMatch = MatchEntity(
            id = matchId,
            opponentName = opponent.ifBlank { "Opponent FC" },
            matchFormat = format,
            formationName = formation,
            durationMinutes = durationMinutes,
            halfDurationMinutes = durationMinutes / 2,
            homeScore = 0,
            awayScore = 0,
            currentPeriod = "FIRST_HALF",
            elapsedSeconds = 0,
            isRunning = false,
            isCompleted = false,
            activeLineupJson = lineupJson,
            createdAt = System.currentTimeMillis()
        )
        database.matchDao().insertOrUpdate(newMatch)
        newMatch
    }

    suspend fun updateMatch(match: MatchEntity) = withContext(Dispatchers.IO) {
        database.matchDao().update(match)
    }

    suspend fun deleteMatch(matchId: String) = withContext(Dispatchers.IO) {
        database.matchEventDao().deleteAllEventsForMatch(matchId)
        database.matchDao().deleteMatchById(matchId)
    }

    suspend fun recordEvent(event: MatchEventEntity, updateScores: Boolean = true) = withContext(Dispatchers.IO) {
        database.matchEventDao().insert(event)
        
        // Update player stats and match scores if needed
        val match = database.matchDao().getMatchById(event.matchId)
        if (match != null && updateScores) {
            when (event.type) {
                "GOAL" -> {
                    val newHomeScore = match.homeScore + 1
                    database.matchDao().update(match.copy(homeScore = newHomeScore))
                    event.playerId?.let { database.playerDao().incrementGoals(it) }
                    event.assistPlayerId?.let { database.playerDao().incrementAssists(it) }
                }
                "OPPONENT_GOAL" -> {
                    val newAwayScore = match.awayScore + 1
                    database.matchDao().update(match.copy(awayScore = newAwayScore))
                }
            }
        }
    }

    suspend fun undoLastEvent(matchId: String) = withContext(Dispatchers.IO) {
        val events = database.matchEventDao().getEventsForMatch(matchId).first()
        if (events.isNotEmpty()) {
            val last = events.last()
            val match = database.matchDao().getMatchById(matchId)
            if (match != null) {
                if (last.type == "GOAL" && match.homeScore > 0) {
                    database.matchDao().update(match.copy(homeScore = match.homeScore - 1))
                } else if (last.type == "OPPONENT_GOAL" && match.awayScore > 0) {
                    database.matchDao().update(match.copy(awayScore = match.awayScore - 1))
                }
            }
            database.matchEventDao().delete(last)
        }
    }

    suspend fun finishMatch(match: MatchEntity) = withContext(Dispatchers.IO) {
        val completed = match.copy(
            isRunning = false,
            isCompleted = true,
            currentPeriod = "FULL_TIME"
        )
        database.matchDao().update(completed)

        // Increment appearances for players on pitch
        val slots = deserializeSlotsFromJson(match.activeLineupJson)
        val playerIds = slots.mapNotNull { it.playerId }.distinct()
        if (playerIds.isNotEmpty()) {
            database.playerDao().incrementAppearances(playerIds)
        }
    }

    suspend fun generateInitialSlotsForFormation(format: String, formationName: String): List<PitchPlayerSlot> {
        val presets = FormationLibrary.getPresetsForFormat(format)
        val def = presets.find { it.name.contains(formationName) } ?: presets.firstOrNull() ?: FormationLibrary.getPresetsForFormat("7-a-side").first()
        val players = allPlayers.first()

        // Assign players smartly to slots according to position category
        val assignedSlots = def.slots.mapIndexed { index, slot ->
            val matchingPlayer = players.filter { it.position == slot.category }
                .getOrNull(index % maxOf(1, players.count { it.position == slot.category }))
                ?: players.getOrNull(index)

            slot.copy(
                playerId = matchingPlayer?.id,
                playerName = matchingPlayer?.name ?: "Player ${index + 1}",
                jerseyNumber = matchingPlayer?.jerseyNumber ?: (index + 1)
            )
        }
        return assignedSlots
    }

    fun serializeSlotsToJson(slots: List<PitchPlayerSlot>): String {
        val array = JSONArray()
        slots.forEach { slot ->
            val obj = JSONObject().apply {
                put("slotId", slot.slotId)
                put("roleLabel", slot.roleLabel)
                put("category", slot.category)
                put("normX", slot.normX.toDouble())
                put("normY", slot.normY.toDouble())
                put("playerId", slot.playerId ?: "")
                put("playerName", slot.playerName)
                put("jerseyNumber", slot.jerseyNumber)
                put("goalsInMatch", slot.goalsInMatch)
                put("assistsInMatch", slot.assistsInMatch)
                put("yellowCards", slot.yellowCards)
                put("redCards", slot.redCards)
                put("isSubbedOut", slot.isSubbedOut)
            }
            array.put(obj)
        }
        return array.toString()
    }

    fun deserializeSlotsFromJson(json: String): List<PitchPlayerSlot> {
        if (json.isBlank()) return emptyList()
        val list = mutableListOf<PitchPlayerSlot>()
        try {
            val array = JSONArray(json)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                val pid = obj.optString("playerId", "").ifBlank { null }
                list.add(
                    PitchPlayerSlot(
                        slotId = obj.optString("slotId", UUID.randomUUID().toString()),
                        roleLabel = obj.optString("roleLabel", "POS"),
                        category = obj.optString("category", "MID"),
                        normX = obj.optDouble("normX", 0.5).toFloat(),
                        normY = obj.optDouble("normY", 0.5).toFloat(),
                        playerId = pid,
                        playerName = obj.optString("playerName", "Player"),
                        jerseyNumber = obj.optInt("jerseyNumber", 10),
                        goalsInMatch = obj.optInt("goalsInMatch", 0),
                        assistsInMatch = obj.optInt("assistsInMatch", 0),
                        yellowCards = obj.optInt("yellowCards", 0),
                        redCards = obj.optInt("redCards", 0),
                        isSubbedOut = obj.optBoolean("isSubbedOut", false)
                    )
                )
            }
        } catch (_: Exception) {}
        return list
    }

    suspend fun exportDataJson(): String = withContext(Dispatchers.IO) {
        val players = allPlayers.first()
        val matches = allMatches.first()
        val root = JSONObject()
        val playersArr = JSONArray()
        players.forEach { p ->
            playersArr.put(JSONObject().apply {
                put("id", p.id)
                put("name", p.name)
                put("jerseyNumber", p.jerseyNumber)
                put("position", p.position)
                put("nickname", p.nickname)
                put("appearances", p.appearances)
                put("totalGoals", p.totalGoals)
                put("totalAssists", p.totalAssists)
            })
        }
        root.put("team", "FC GREEN MAMBA")
        root.put("version", 1)
        root.put("players", playersArr)
        root.put("matchCount", matches.size)
        root.toString(2)
    }

    suspend fun importDataJson(jsonStr: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val root = JSONObject(jsonStr)
            val playersArr = root.optJSONArray("players") ?: return@withContext false
            val importedList = mutableListOf<PlayerEntity>()
            for (i in 0 until playersArr.length()) {
                val o = playersArr.getJSONObject(i)
                importedList.add(
                    PlayerEntity(
                        id = o.optString("id", UUID.randomUUID().toString()),
                        name = o.getString("name"),
                        jerseyNumber = o.getInt("jerseyNumber"),
                        position = o.getString("position"),
                        nickname = o.optString("nickname", ""),
                        appearances = o.optInt("appearances", 0),
                        totalGoals = o.optInt("totalGoals", 0),
                        totalAssists = o.optInt("totalAssists", 0)
                    )
                )
            }
            if (importedList.isNotEmpty()) {
                database.playerDao().insertAll(importedList)
                true
            } else {
                false
            }
        } catch (_: Exception) {
            false
        }
    }
}
