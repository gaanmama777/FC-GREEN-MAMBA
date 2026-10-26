package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "matches")
data class MatchEntity(
    @PrimaryKey
    val id: String,
    val opponentName: String = "Opponent FC",
    val matchFormat: String = "7-a-side",
    val formationName: String = "2-3-1",
    val durationMinutes: Int = 70,
    val halfDurationMinutes: Int = 35,
    val homeScore: Int = 0,
    val awayScore: Int = 0,
    val currentPeriod: String = "FIRST_HALF", // "NOT_STARTED", "FIRST_HALF", "HALF_TIME", "SECOND_HALF", "FULL_TIME"
    val elapsedSeconds: Int = 0,
    val isRunning: Boolean = false,
    val isCompleted: Boolean = false,
    val activeLineupJson: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
