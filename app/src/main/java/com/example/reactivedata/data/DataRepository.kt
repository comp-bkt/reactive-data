package com.example.reactivedata.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

/**
 * Repository Pattern (LiveData version):
 * The Repository acts as a mediator between different data sources.
 *
 * Singleton Pattern:
 * We use a Singleton to ensure there is only one instance of the data throughout the app's life.
 */
class DataRepository private constructor() {

    /**
     * MutableLiveData is a data holder class that can be observed within a given lifecycle.
     * We keep it private to ensure it can only be modified within this class.
     */
    private val _data = MutableLiveData<List<String>>(emptyList())

    /**
     * We expose the data as read-only LiveData to the outside world.
     */
    val data: LiveData<List<String>> = _data

    /**
     * Updates the list and notifies observers.
     * We get the current value, create a new list with the new item, and set the value.
     */
    fun addString(value: String) {
        val currentList = _data.value ?: emptyList()
        _data.value = currentList + value
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
