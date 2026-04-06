package com.example.reactivedata

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.reactivedata.ui.DataViewModel
import com.example.reactivedata.ui.MainContent
import com.example.reactivedata.ui.MainUiState
import com.example.reactivedata.ui.theme.ReactiveDataTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ReactiveDataTheme {
                val viewModel: DataViewModel = viewModel()
                
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    /**
                     * observeAsState() is used to convert LiveData into a State 
                     * that Compose can react to.
                     */
                    val uiState by viewModel.uiState.observeAsState(MainUiState())
                    
                    MainContent(
                        items = uiState.items,
                        onAddItem = { viewModel.addString(it) },
                        onClearItems = { viewModel.clearData() },
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}
