package com.example.agrochain

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.ViewModelProvider
import com.example.agrochain.ui.AgroChainApp
import com.example.agrochain.ui.theme.AgroChainTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AgroChainTheme {
                val viewModel: AgroChainViewModel = viewModel(
                    factory = object : ViewModelProvider.Factory {
                        override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                            @Suppress("UNCHECKED_CAST")
                            return AgroChainViewModel(application) as T
                        }
                    }
                )
                Surface(modifier = Modifier) {
                    AgroChainApp(viewModel = viewModel)
                }
            }
        }
    }
}

