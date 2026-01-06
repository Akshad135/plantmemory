package com.plantmemory.app.ui.garden

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.plantmemory.app.data.JournalEntry
import com.plantmemory.app.data.JournalRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/**
 * Data class representing a group of entries for a specific date.
 */
data class DateGroup(
    val dateLabel: String,  // e.g., "monday, 10.27"
    val monthYear: String,  // e.g., "October 2025"
    val entries: List<JournalEntry>
)

/**
 * UI state for the Garden screen.
 */
data class GardenUiState(
    val entries: List<JournalEntry> = emptyList(),
    val dateGroups: List<DateGroup> = emptyList(),
    val selectedYear: Int = Calendar.getInstance().get(Calendar.YEAR),
    val availableYears: List<Int> = emptyList(),
    val entryCount: Int = 0,
    val daysOfGrowth: Int = 0,
    val isLoading: Boolean = true
)

class GardenViewModel(
    private val repository: JournalRepository
) : ViewModel() {
    
    private val _selectedYear = MutableStateFlow(Calendar.getInstance().get(Calendar.YEAR))
    
    val uiState: StateFlow<GardenUiState> = combine(
        repository.getAllEntriesAscending(),
        repository.getDistinctYears(),
        repository.getEntryCount(),
        repository.getFirstEntryTimestamp(),
        _selectedYear
    ) { entries, years, count, firstTimestamp, selectedYear ->
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        val yearList = years.mapNotNull { it.toIntOrNull() }.toMutableSet()
        // Always include current year
        yearList.add(currentYear)
        val sortedYears = yearList.sorted().reversed()
        
        val filteredEntries = entries.filter { entry ->
            Calendar.getInstance().apply { timeInMillis = entry.timestamp }
                .get(Calendar.YEAR) == selectedYear
        }
        val daysOfGrowth = repository.getDaysOfGrowth(firstTimestamp)
        
        GardenUiState(
            entries = filteredEntries,
            dateGroups = groupEntriesByDate(filteredEntries),
            selectedYear = selectedYear,
            availableYears = sortedYears,
            entryCount = count,
            daysOfGrowth = daysOfGrowth,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = GardenUiState()
    )
    
    fun selectYear(year: Int) {
        _selectedYear.value = year
    }
    
    fun deleteEntry(entry: JournalEntry) {
        val deletedYear = Calendar.getInstance().apply { 
            timeInMillis = entry.timestamp 
        }.get(Calendar.YEAR)
        
        viewModelScope.launch {
            repository.deleteEntry(entry)
            
            // After deletion, check if we should switch to current year
            // This prevents showing empty page when deleting last entry of a past year
            val currentYear = Calendar.getInstance().get(Calendar.YEAR)
            if (deletedYear != currentYear && _selectedYear.value == deletedYear) {
                _selectedYear.value = currentYear
            }
        }
    }
    
    private fun groupEntriesByDate(entries: List<JournalEntry>): List<DateGroup> {
        val dateFormat = SimpleDateFormat("EEEE, MM.dd", Locale.US)
        val monthYearFormat = SimpleDateFormat("MMMM yyyy", Locale.US)
        
        return entries.groupBy { entry ->
            val calendar = Calendar.getInstance().apply { timeInMillis = entry.timestamp }
            Triple(
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
        }.map { (_, dayEntries) ->
            val firstEntry = dayEntries.first()
            val date = java.util.Date(firstEntry.timestamp)
            DateGroup(
                dateLabel = dateFormat.format(date).lowercase(),
                monthYear = monthYearFormat.format(date),
                entries = dayEntries
            )
        }
    }
    
    class Factory(private val repository: JournalRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return GardenViewModel(repository) as T
        }
    }
}
