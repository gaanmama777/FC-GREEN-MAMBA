package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "players")
data class PlayerEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val jerseyNumber: Int,
    val position: String, // "GK", "DEF", "MID", "FWD"
    val nickname: String = "",
    val appearances: Int = 0,
    val totalGoals: Int = 0,
    val totalAssists: Int = 0,
    val isArchived: Boolean = false
)

enum class PlayerPosition(val label: String, val shortLabel: String) {
    GOALKEEPER("Goalkeeper", "GK"),
    DEFENDER("Defender", "DEF"),
    MIDFIELDER("Midfielder", "MID"),
    FORWARD("Forward", "FWD")
}
