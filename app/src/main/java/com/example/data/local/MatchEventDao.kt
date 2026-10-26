package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.MatchEventEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MatchEventDao {
    @Query("SELECT * FROM match_events WHERE matchId = :matchId ORDER BY minute ASC, timestamp ASC")
    fun getEventsForMatch(matchId: String): Flow<List<MatchEventEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(event: MatchEventEntity)

    @Delete
    suspend fun delete(event: MatchEventEntity)

    @Query("DELETE FROM match_events WHERE id = (SELECT id FROM match_events WHERE matchId = :matchId ORDER BY timestamp DESC LIMIT 1)")
    suspend fun deleteLastEvent(matchId: String)

    @Query("DELETE FROM match_events WHERE matchId = :matchId")
    suspend fun deleteAllEventsForMatch(matchId: String)
}
