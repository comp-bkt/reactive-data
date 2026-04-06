package com.example.reactivedata.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * Repository Pattern:
 * The Repository acts as a mediator between different data sources (Network, Database, or in-memory)
 * and the rest of the app. It provides a clean API for data access.
 *
 * Singleton Pattern:
 * We use a Singleton to ensure there is only one instance of the data throughout the app's life.
 */
class DataRepository private constructor() {

    /**
     * MutableStateFlow is a state-holder observable flow that emits the current and new state updates.
     * We keep it private to ensure it can only be modified within this class (Encapsulation).
     */
    private val _data = MutableStateFlow<List<String>>(emptyList())

    /**
     * We expose the flow as a read-only StateFlow to the outside world.
     * This prevents external classes from modifying the data directly.
     */
    val data: StateFlow<List<String>> = _data.asStateFlow()

    /**
     * update {} is a thread-safe way to modify the list.
     * Because strings and lists are immutable in Kotlin, we create a new list
     * with the new value added, which triggers an emission in the StateFlow.
     */
    fun addString(value: String) {
        _data.update { currentList ->
            currentList + value
        }
    }

    /**
     * Resets the data to its initial empty state.
     */
    fun clearData() {
        _data.value = emptyList()
    }

    companion object {
        @Volatile
        private var instance: DataRepository? = null

        /**
         * Thread-safe Singleton implementation to provide the repository instance.
         */
        fun getInstance(): DataRepository {
            return instance ?: synchronized(this) {
                instance ?: DataRepository().also { instance = it }
            }
        }
    }
}
