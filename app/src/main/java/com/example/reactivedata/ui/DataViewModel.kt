package com.example.reactivedata.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.reactivedata.data.DataRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * UI State Pattern:
 * Represents the complete state of the UI in a single data class.
 * This makes it easier to reason about what the UI should show at any given time.
 */
data class MainUiState(
    val items: List<String> = emptyList(),
    val isLoading: Boolean = false
)

/**
 * ViewModel:
 * Its purpose is to prepare and manage data for the UI.
 * It survives configuration changes (like screen rotation) because it is tied to the Activity's lifecycle.
 */
class DataViewModel : ViewModel() {
    /**
     * Get the Singleton instance of the Repository.
     */
    private val repository = DataRepository.getInstance()
    
    /**
     * Internal mutable state holder for our UI.
     */
    private val _uiState = MutableStateFlow(MainUiState())

    /**
     * Exposed read-only state for the UI to observe.
     * The UI (Activity/Compose) will collect from this flow to update itself.
     */
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    init {
        /**
         * viewModelScope is a coroutine scope tied to the ViewModel's lifecycle.
         * When the ViewModel is cleared, any work launched in this scope is automatically cancelled.
         *
         * Here, we start observing the repository's data flow.
         */
        viewModelScope.launch {
            repository.data.collect { items ->
                /**
                 * Whenever the data in the repository changes, we update our UI state.
                 * copy() allows us to update only specific properties while keeping the rest.
                 */
                _uiState.update { it.copy(items = items) }
            }
        }
    }

    /**
     * Passes the user action to the repository.
     */
    fun addString(value: String) {
        if (value.isNotBlank()) {
            repository.addString(value)
        }
    }

    /**
     * Passes the clear action to the repository.
     */
    fun clearData() {
        repository.clearData()
    }
}
