package com.plantmemory.app.ui.entry

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.updateAll
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.plantmemory.app.data.JournalRepository
import com.plantmemory.app.data.PlantType
import com.plantmemory.app.widget.PlantMemoryWidget
import com.plantmemory.app.widget.SmallPlantWidget
import com.plantmemory.app.widget.TinyPlantWidget
import kotlinx.coroutines.launch
import java.util.Calendar

/**
 * UI state for the Entry screen.
 */
data class EntryUiState(
    val text: String = "",
    val selectedPlantType: PlantType = PlantType.SIMPLE,
    val selectedDate: Long = System.currentTimeMillis(),
    val isSaving: Boolean = false,
    val saveComplete: Boolean = false
)

class EntryViewModel(
    private val repository: JournalRepository,
    private val context: Context? = null
) : ViewModel() {
    
    var uiState by mutableStateOf(EntryUiState())
        private set
    
    private val today: Long
        get() {
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.HOUR_OF_DAY, 12)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            return calendar.timeInMillis
        }
    
    fun updateText(newText: String) {
        uiState = uiState.copy(text = newText)
    }
    
    fun selectPlantType(plantType: PlantType) {
        uiState = uiState.copy(selectedPlantType = plantType)
    }
    
    fun selectDate(timestamp: Long) {
        // Only allow today or past dates
        val maxDate = System.currentTimeMillis()
        val clampedDate = timestamp.coerceAtMost(maxDate)
        uiState = uiState.copy(selectedDate = clampedDate)
    }
    
    fun goToPreviousDay() {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = uiState.selectedDate
        calendar.add(Calendar.DAY_OF_YEAR, -1)
        uiState = uiState.copy(selectedDate = calendar.timeInMillis)
    }
    
    fun goToNextDay() {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = uiState.selectedDate
        calendar.add(Calendar.DAY_OF_YEAR, 1)
        
        // Don't go past today
        val now = System.currentTimeMillis()
        if (calendar.timeInMillis <= now) {
            uiState = uiState.copy(selectedDate = calendar.timeInMillis)
        }
    }
    
    fun canGoToNextDay(): Boolean {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = uiState.selectedDate
        calendar.add(Calendar.DAY_OF_YEAR, 1)
        
        val todayCalendar = Calendar.getInstance()
        return calendar.get(Calendar.DAY_OF_YEAR) <= todayCalendar.get(Calendar.DAY_OF_YEAR) &&
                calendar.get(Calendar.YEAR) <= todayCalendar.get(Calendar.YEAR)
    }
    
    fun saveEntry(onComplete: () -> Unit) {
        if (uiState.text.isBlank()) return
        
        viewModelScope.launch {
            uiState = uiState.copy(isSaving = true)
            
            repository.createEntry(
                text = uiState.text.trim(),
                plantType = uiState.selectedPlantType,
                timestamp = uiState.selectedDate
            )
            
            // Update all widgets
            context?.let { ctx ->
                try {
                    TinyPlantWidget().updateAll(ctx)
                    SmallPlantWidget().updateAll(ctx)
                    PlantMemoryWidget().updateAll(ctx)
                } catch (e: Exception) {
                    // Ignore widget update errors
                }
            }
            
            uiState = uiState.copy(isSaving = false, saveComplete = true)
            onComplete()
        }
    }
    
    fun reset() {
        uiState = EntryUiState()
    }
    
    class Factory(
        private val repository: JournalRepository,
        private val context: Context? = null
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return EntryViewModel(repository, context) as T
        }
    }
}
