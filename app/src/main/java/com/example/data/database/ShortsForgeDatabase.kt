package com.example.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.dao.*
import com.example.data.model.*

@Database(
    entities = [
        ProjectEntity::class,
        ClipEntity::class,
        JobEntity::class,
        ExportEntity::class,
        ChatMessageEntity::class,
        UserSettingsEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class ShortsForgeDatabase : RoomDatabase() {
    abstract fun projectDao(): ProjectDao
    abstract fun clipDao(): ClipDao
    abstract fun jobDao(): JobDao
    abstract fun exportDao(): ExportDao
    abstract fun chatMessageDao(): ChatMessageDao
    abstract fun userSettingsDao(): UserSettingsDao

    companion object {
        @Volatile
        private var INSTANCE: ShortsForgeDatabase? = null

        fun getInstance(context: Context): ShortsForgeDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ShortsForgeDatabase::class.java,
                    "shortsforge_database"
                )
                    .fallbackToDestructiveMigration(true)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
