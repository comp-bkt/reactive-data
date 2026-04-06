package com.example.reactivedata.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import com.example.reactivedata.data.DataRepository

/**
 * UI State Pattern (LiveData version):
 * Represents the complete state of the UI in a single data class.
 *
 * GOOD PRACTICE: Using a single data class for UI state promotes "Unidirectional Data Flow" (UDF).
 * Instead of having multiple independent LiveData objects for items, loading, and errors, 
 * we bundle them together. This ensures the UI always receives a consistent "snapshot" 
 * of the screen state.
 */
data class MainUiState(
    val items: List<String> = emptyList(),
    val isLoading: Boolean = false
)

/**
 * ViewModel:
 * Manages data for the UI using LiveData.
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
     * LiveData implementation of the UI state.
     * 
     * GOOD PRACTICE: LiveData is "Lifecycle-Aware." Unlike standard Observables, it only 
     * notifies the UI if the Activity/Fragment is in an "Active" state (Started/Resumed). 
     * This automatically prevents crashes related to updating a UI that is in the background.
     *
     * We use the `.map` operator to transform raw data from the Repository into a 
     * MainUiState. This keeps the UI "logic-less"—it simply displays whatever 
     * the uiState tells it to.
     */
    val uiState: LiveData<MainUiState> = repository.data.map { items ->
        MainUiState(items = items)
    }

    /**
     * Event Handling (Input Action):
     * 
     * GOOD PRACTICE: User interactions (clicks, text input) are treated as "Actions." 
     * The UI notifies the ViewModel of the action, and the ViewModel decides how to 
     * update the data layer.
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
