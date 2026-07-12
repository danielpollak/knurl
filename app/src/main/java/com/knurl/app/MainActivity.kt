package com.knurl.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.knurl.app.ui.theme.KnurlTheme
import com.knurl.app.ui.workout.WorkoutScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KnurlTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    WorkoutScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}
