package com.plantmemory.app

import android.app.Application
import com.plantmemory.app.data.AppDatabase
import com.plantmemory.app.data.JournalRepository
import com.plantmemory.app.debug.DebugConfig
import com.plantmemory.app.debug.DummyDataGenerator
import com.plantmemory.app.widget.WidgetUpdateWorker

/**
 * Application class for Plant Memory.
 * Initializes the database, repository, and schedules widget updates.
 */
class PlantMemoryApplication : Application() {
    
    val database by lazy { AppDatabase.getDatabase(this) }
    val repository by lazy { JournalRepository(database.journalDao()) }
    
    override fun onCreate() {
        super.onCreate()
        instance = this
        
        // Seed dummy data if enabled (for testing)
        if (DebugConfig.USE_DUMMY_DATA) {
            DummyDataGenerator.seedIfEmpty(database.journalDao())
        }
        
        // Schedule periodic widget updates every 6 hours
        WidgetUpdateWorker.schedule(this)
    }
    
    companion object {
        lateinit var instance: PlantMemoryApplication
            private set
    }
}
