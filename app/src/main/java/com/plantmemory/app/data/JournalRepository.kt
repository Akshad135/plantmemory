package com.plantmemory.app.data

import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit
import kotlin.random.Random

/**
 * Repository for accessing journal entries.
 * Provides a clean API for the UI layer.
 */
class JournalRepository(private val journalDao: JournalDao) {
    
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    
    /**
     * Get all entries as a Flow.
     */
    fun getAllEntries(): Flow<List<JournalEntry>> = journalDao.getAllEntries()
    
    /**
     * Get all entries in ascending order (for garden display).
     */
    fun getAllEntriesAscending(): Flow<List<JournalEntry>> = journalDao.getAllEntriesAscending()
    
    /**
     * Get entries for a specific year.
     */
    fun getEntriesByYear(year: Int): Flow<List<JournalEntry>> = 
        journalDao.getEntriesByYear(year.toString())
    
    /**
     * Get entries for a specific year (sync version for widgets).
     */
    suspend fun getEntriesByYearSync(year: Int, limit: Int = 100): List<JournalEntry> =
        journalDao.getEntriesByYearSync(year.toString(), limit)
    
    /**
     * Get available years.
     */
    fun getDistinctYears(): Flow<List<String>> = journalDao.getDistinctYears()
    
    /**
     * Get entry count.
     */
    fun getEntryCount(): Flow<Int> = journalDao.getEntryCount()
    
    /**
     * Get the first entry timestamp.
     */
    fun getFirstEntryTimestamp(): Flow<Long?> = journalDao.getFirstEntryTimestamp()
    
    /**
     * Calculate days of growth from first entry to now.
     */
    suspend fun getDaysOfGrowth(firstTimestamp: Long?): Int {
        if (firstTimestamp == null) return 0
        val now = System.currentTimeMillis()
        val diffMillis = now - firstTimestamp
        return TimeUnit.MILLISECONDS.toDays(diffMillis).toInt() + 1
    }
    
    /**
     * Get entry for a specific date (for one-plant-per-day logic).
     */
    suspend fun getEntryByDate(timestamp: Long): JournalEntry? {
        val dateString = dateFormat.format(Date(timestamp))
        return journalDao.getEntryByDate(dateString)
    }
    
    /**
     * Create or update a journal entry for a specific date.
     * One plant per day - overwrites if entry exists for that date.
     */
    suspend fun createOrUpdateEntry(
        text: String,
        plantType: PlantType,
        timestamp: Long = System.currentTimeMillis()
    ): Long {
        val existingEntry = getEntryByDate(timestamp)
        
        return if (existingEntry != null) {
            // Update existing entry
            val updatedEntry = existingEntry.copy(
                text = text,
                plantType = plantType,
                plantVariant = Random.nextInt(1, 9) // New variant on update
            )
            journalDao.update(updatedEntry)
            existingEntry.id
        } else {
            // Create new entry
            val entry = JournalEntry(
                text = text,
                timestamp = timestamp,
                plantType = plantType,
                plantVariant = Random.nextInt(1, 9),
                gridX = Random.nextFloat(),
                gridY = Random.nextFloat()
            )
            journalDao.insert(entry)
        }
    }
    
    /**
     * Create a new journal entry with random grid positioning.
     * @deprecated Use createOrUpdateEntry for one-plant-per-day behavior
     */
    suspend fun createEntry(
        text: String,
        plantType: PlantType,
        timestamp: Long = System.currentTimeMillis()
    ): Long {
        return createOrUpdateEntry(text, plantType, timestamp)
    }
    
    /**
     * Get entry by ID.
     */
    suspend fun getEntryById(id: Long): JournalEntry? = journalDao.getEntryById(id)
    
    /**
     * Update an entry.
     */
    suspend fun updateEntry(entry: JournalEntry) = journalDao.update(entry)
    
    /**
     * Delete an entry.
     */
    suspend fun deleteEntry(entry: JournalEntry) = journalDao.delete(entry)
    
    /**
     * Get recent entries for widget.
     */
    suspend fun getRecentEntries(limit: Int = 50): List<JournalEntry> = 
        journalDao.getRecentEntries(limit)
}
