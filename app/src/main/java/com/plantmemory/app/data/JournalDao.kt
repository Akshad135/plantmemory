package com.plantmemory.app.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for journal entries.
 */
@Dao
interface JournalDao {
    
    /**
     * Get all journal entries ordered by timestamp descending (newest first).
     */
    @Query("SELECT * FROM journal_entries ORDER BY timestamp DESC")
    fun getAllEntries(): Flow<List<JournalEntry>>
    
    /**
     * Get entries for a specific year.
     */
    @Query("""
        SELECT * FROM journal_entries 
        WHERE strftime('%Y', timestamp / 1000, 'unixepoch', 'localtime') = :year 
        ORDER BY timestamp DESC
    """)
    fun getEntriesByYear(year: String): Flow<List<JournalEntry>>
    
    /**
     * Get entries grouped by date for garden display.
     */
    @Query("SELECT * FROM journal_entries ORDER BY timestamp ASC")
    fun getAllEntriesAscending(): Flow<List<JournalEntry>>
    
    /**
     * Get a single entry by ID.
     */
    @Query("SELECT * FROM journal_entries WHERE id = :id")
    suspend fun getEntryById(id: Long): JournalEntry?
    
    /**
     * Insert a new journal entry.
     */
    @Insert
    suspend fun insert(entry: JournalEntry): Long
    
    /**
     * Update an existing entry.
     */
    @Update
    suspend fun update(entry: JournalEntry)
    
    /**
     * Delete an entry.
     */
    @Delete
    suspend fun delete(entry: JournalEntry)
    
    /**
     * Get the total count of entries.
     */
    @Query("SELECT COUNT(*) FROM journal_entries")
    fun getEntryCount(): Flow<Int>
    
    /**
     * Get the timestamp of the first entry (for calculating days of growth).
     */
    @Query("SELECT MIN(timestamp) FROM journal_entries")
    fun getFirstEntryTimestamp(): Flow<Long?>
    
    /**
     * Get distinct years that have entries.
     */
    @Query("SELECT DISTINCT strftime('%Y', timestamp / 1000, 'unixepoch', 'localtime') as year FROM journal_entries ORDER BY year DESC")
    fun getDistinctYears(): Flow<List<String>>
    
    /**
     * Get recent entries for widget display.
     */
    @Query("SELECT * FROM journal_entries ORDER BY timestamp DESC LIMIT :limit")
    suspend fun getRecentEntries(limit: Int): List<JournalEntry>
    
    /**
     * Get entries for widget filtered by year.
     */
    @Query("""
        SELECT * FROM journal_entries 
        WHERE strftime('%Y', timestamp / 1000, 'unixepoch', 'localtime') = :year 
        ORDER BY timestamp DESC 
        LIMIT :limit
    """)
    suspend fun getEntriesByYearSync(year: String, limit: Int): List<JournalEntry>
    
    /**
     * Find entry for a specific date (day/month/year).
     * Used for one-plant-per-day logic.
     * Uses 'localtime' to match the app's local date formatting.
     */
    @Query("""
        SELECT * FROM journal_entries 
        WHERE strftime('%Y-%m-%d', timestamp / 1000, 'unixepoch', 'localtime') = :dateString
        LIMIT 1
    """)
    suspend fun getEntryByDate(dateString: String): JournalEntry?
}

