package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "match_events")
data class MatchEventEntity(
    @PrimaryKey
    val id: String,
    val matchId: String,
    val minute: Int,
    val period: String, // "1st Half", "2nd Half", "FT"
    val type: String,   // "GOAL", "OPPONENT_GOAL", "SUBSTITUTION", "YELLOW_CARD", "RED_CARD", "WHISTLE"
    val playerId: String? = null,
    val playerName: String? = null,
    val playerJersey: Int? = null,
    val assistPlayerId: String? = null,
    val assistPlayerName: String? = null,
    val assistPlayerJersey: Int? = null,
    val subInPlayerId: String? = null,
    val subInPlayerName: String? = null,
    val subInPlayerJersey: Int? = null,
    val note: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
