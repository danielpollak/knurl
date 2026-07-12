package com.knurl.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.knurl.app.ui.navigation.KnurlApp
import com.knurl.app.ui.theme.KnurlTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KnurlTheme {
                KnurlApp()
            }
        }
    }
}
