package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.MatchEntity
import com.example.data.model.MatchEventEntity
import com.example.data.model.PlayerEntity

@Database(
    entities = [PlayerEntity::class, MatchEntity::class, MatchEventEntity::class],
    version = 1,
    exportSchema = false
)
abstract class MambaDatabase : RoomDatabase() {
    abstract fun playerDao(): PlayerDao
    abstract fun matchDao(): MatchDao
    abstract fun matchEventDao(): MatchEventDao

    companion object {
        @Volatile
        private var INSTANCE: MambaDatabase? = null

        fun getDatabase(context: Context): MambaDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MambaDatabase::class.java,
                    "fc_green_mamba.db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
