package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.PlayerEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlayerDao {
    @Query("SELECT * FROM players WHERE isArchived = 0 ORDER BY jerseyNumber ASC")
    fun getAllActivePlayers(): Flow<List<PlayerEntity>>

    @Query("SELECT * FROM players WHERE id = :id LIMIT 1")
    suspend fun getPlayerById(id: String): PlayerEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(player: PlayerEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(players: List<PlayerEntity>)

    @Update
    suspend fun update(player: PlayerEntity)

    @Delete
    suspend fun delete(player: PlayerEntity)

    @Query("UPDATE players SET appearances = appearances + 1 WHERE id IN (:playerIds)")
    suspend fun incrementAppearances(playerIds: List<String>)

    @Query("UPDATE players SET totalGoals = totalGoals + 1 WHERE id = :playerId")
    suspend fun incrementGoals(playerId: String)

    @Query("UPDATE players SET totalAssists = totalAssists + 1 WHERE id = :playerId")
    suspend fun incrementAssists(playerId: String)
}
