package com.plantmemory.app.debug

import com.plantmemory.app.data.JournalDao
import com.plantmemory.app.data.JournalEntry
import com.plantmemory.app.data.PlantType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar
import kotlin.random.Random

/**
 * Generates dummy journal entries for testing purposes.
 * 
 * Creates:
 * - 100 random days in 2023
 * - 200 random days in 2024
 * - 365 days in 2025 (all filled)
 */
object DummyDataGenerator {
    
    private val plantTypes = PlantType.entries.toTypedArray()
    private val sampleTexts = listOf(
        "Today was a good day!",
        "Feeling grateful for the little things.",
        "Made progress on my goals.",
        "Spent time in nature.",
        "Had a peaceful morning.",
        "Learning something new every day.",
        "Grateful for friends and family.",
        "Taking things one step at a time.",
        "Found joy in simplicity.",
        "Reflecting on my journey.",
        "Embracing change.",
        "Focused on self-care today.",
        "Celebrated a small win!",
        "Practiced mindfulness.",
        "Enjoyed the sunshine."
    )
    
    /**
     * Seeds the database with dummy data if it's empty.
     * Runs on IO dispatcher to avoid blocking the main thread.
     */
    fun seedIfEmpty(journalDao: JournalDao) {
        CoroutineScope(Dispatchers.IO).launch {
            // Check if database already has entries
            val existingCount = journalDao.getEntryCountSync()
            if (existingCount > 0) {
                android.util.Log.d("DummyData", "Database already has $existingCount entries, skipping seed")
                return@launch
            }
            
            android.util.Log.d("DummyData", "Seeding database with dummy data...")
            
            val entries = mutableListOf<JournalEntry>()
            
            // 2023: 100 random days
            entries.addAll(generateEntriesForYear(2023, 100))
            
            // 2024: 200 random days
            entries.addAll(generateEntriesForYear(2024, 200))
            
            // 2025: All 365 days
            entries.addAll(generateEntriesForYear(2025, 365))
            
            // Insert all entries
            journalDao.insertAll(entries)
            
            android.util.Log.d("DummyData", "Seeded ${entries.size} dummy entries")
        }
    }
    
    /**
     * Generates entries for a specific year.
     * 
     * @param year The year to generate entries for
     * @param count Number of days to fill (if less than days in year, random days are selected)
     */
    private fun generateEntriesForYear(year: Int, count: Int): List<JournalEntry> {
        val calendar = Calendar.getInstance()
        calendar.set(year, Calendar.JANUARY, 1, 12, 0, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        
        // Get total days in this year
        val isLeapYear = (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)
        val totalDays = if (isLeapYear) 366 else 365
        
        // Select which days to fill
        val daysToFill = if (count >= totalDays) {
            // Fill all days
            (0 until totalDays).toList()
        } else {
            // Randomly select 'count' unique days
            (0 until totalDays).shuffled().take(count).sorted()
        }
        
        return daysToFill.map { dayOfYear ->
            val entryCalendar = Calendar.getInstance()
            entryCalendar.set(year, Calendar.JANUARY, 1, 12, 0, 0)
            entryCalendar.set(Calendar.MILLISECOND, 0)
            entryCalendar.add(Calendar.DAY_OF_YEAR, dayOfYear)
            
            val dayNumber = dayOfYear + 1
            
            JournalEntry(
                text = "${sampleTexts.random()} (Day $dayNumber of $year)",
                timestamp = entryCalendar.timeInMillis,
                plantType = plantTypes.random(),
                plantVariant = Random.nextInt(1, 9),
                gridX = Random.nextFloat(),
                gridY = Random.nextFloat()
            )
        }
    }
}
