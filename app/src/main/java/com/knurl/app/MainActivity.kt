package com.knurl.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.knurl.app.ui.home.HomeUiState
import com.knurl.app.ui.home.HomeViewModel
import com.knurl.app.ui.theme.KnurlTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KnurlTheme {
                val viewModel: HomeViewModel = viewModel(factory = HomeViewModel.Factory)
                val uiState by viewModel.uiState.collectAsState()
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    SeedSummary(uiState = uiState, modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun SeedSummary(uiState: HomeUiState, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(text = "Knurl", style = MaterialTheme.typography.headlineMedium)
        Text(text = "Exercise library: ${uiState.exerciseCount}")
        Text(text = "Active program: ${uiState.activeProgramName}")
        Text(text = "Main lift weights:")
        uiState.mainLiftWeights.forEach { (name, weight) ->
            Text(text = "  $name: ${weight.toInt()} lb")
        }
    }
}
