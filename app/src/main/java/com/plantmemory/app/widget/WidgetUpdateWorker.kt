package com.plantmemory.app.widget

import android.content.Context
import androidx.glance.appwidget.updateAll
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import java.util.concurrent.TimeUnit

/**
 * Worker that periodically updates all widgets.
 * Runs every 6 hours to keep widgets fresh.
 */
class WidgetUpdateWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        return try {
            // Update all widget types
            TinyPlantWidget().updateAll(applicationContext)
            SmallPlantWidget().updateAll(applicationContext)
            PlantMemoryWidget().updateAll(applicationContext)
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }

    companion object {
        private const val WORK_NAME = "widget_periodic_update"
        
        /**
         * Schedule periodic widget updates every 6 hours.
         */
        fun schedule(context: Context) {
            val request = PeriodicWorkRequestBuilder<WidgetUpdateWorker>(
                repeatInterval = 6,
                repeatIntervalTimeUnit = TimeUnit.HOURS
            ).build()
            
            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                request
            )
        }
        
        /**
         * Update all widgets immediately.
         */
        suspend fun updateAllWidgetsNow(context: Context) {
            TinyPlantWidget().updateAll(context)
            SmallPlantWidget().updateAll(context)
            PlantMemoryWidget().updateAll(context)
        }
    }
}
