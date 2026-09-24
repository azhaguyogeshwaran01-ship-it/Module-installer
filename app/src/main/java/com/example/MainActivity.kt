package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.MainAppScaffold
import com.example.ui.theme.ModuleInserterTheme
import com.example.ui.viewmodel.ModuleViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ModuleInserterTheme {
                val moduleViewModel: ModuleViewModel = viewModel()
                MainAppScaffold(viewModel = moduleViewModel)
            }
        }
    }
}
