package com.plantmemory.app

import android.app.Application
import com.plantmemory.app.data.AppDatabase
import com.plantmemory.app.data.JournalRepository

/**
 * Application class for Plant Memory.
 * Initializes the database and repository.
 */
class PlantMemoryApplication : Application() {
    
    val database by lazy { AppDatabase.getDatabase(this) }
    val repository by lazy { JournalRepository(database.journalDao()) }
    
    override fun onCreate() {
        super.onCreate()
        instance = this
    }
    
    companion object {
        lateinit var instance: PlantMemoryApplication
            private set
    }
}
