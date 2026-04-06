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
 *
 * GOOD PRACTICE: Using a single data class for UI state promotes "Unidirectional Data Flow" (UDF).
 * Instead of having multiple independent flows for items, loading, and errors, 
 * we bundle them together. This ensures the UI always receives a consistent "snapshot" 
 * of the screen state.
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
     *
     * GOOD PRACTICE: The ViewModel follows the "Dependency Inversion" principle. It doesn't 
     * care how the data is stored (Network vs. Local Database); it only knows it can 
     * get it from the Repository.
     */
    private val repository = DataRepository.getInstance()
    
    /**
     * Internal mutable state holder for our UI.
     *
     * GOOD PRACTICE: We use MutableStateFlow to hold the state. Unlike LiveData, 
     * StateFlow requires an initial value, which avoids null-safety issues.
     */
    private val _uiState = MutableStateFlow(MainUiState())

    /**
     * Exposed read-only state for the UI to observe.
     *
     * GOOD PRACTICE: By exposing a StateFlow (read-only) instead of a MutableStateFlow, 
     * we ensure that only the ViewModel can modify the state (Encapsulation).
     */
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    init {
        /**
         * viewModelScope is a coroutine scope tied to the ViewModel's lifecycle.
         * When the ViewModel is cleared, any work launched in this scope is automatically cancelled.
         *
         * GOOD PRACTICE: We collect the flow from the repository within this scope. 
         * This ensures that as long as the ViewModel is alive, it stays in sync with 
         * the data layer.
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
     * Event Handling (Input Action):
     * 
     * GOOD PRACTICE: User interactions are treated as "Actions." The UI notifies 
     * the ViewModel, which then interacts with the repository. This keeps the 
     * logic for "how" to add data centralized in the ViewModel/Repository.
     */
    fun addString(value: String) {
        if (value.isNotBlank()) {
            repository.addString(value)
        }
    }

    /**
     * Event Handling (Clear Action):
     * Passes the clear action to the repository.
     */
    fun clearData() {
        repository.clearData()
    }
}
