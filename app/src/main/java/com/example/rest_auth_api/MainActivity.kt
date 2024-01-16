package com.example.rest_auth_api

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.rest_auth_api.model.util.ContextProvider
import com.example.rest_auth_api.ui.theme.Rest_Auth_APITheme
import com.example.rest_auth_api.ui.theme.presentation.views.Homescreen

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ContextProvider.initialize(this) // ensure app context

        setContent {
            Rest_Auth_APITheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                        Homescreen()
                }
            }
        }
    }
}


