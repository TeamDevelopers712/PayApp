package com.example.payapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.payapp.ui.theme.PayAppTheme
import com.example.payapp.ui.theme.entryManagement.AuthViewModel
import com.example.payapp.ui.theme.navigation.AppNavGraph

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            PayAppTheme {
                val navController = rememberNavController()
                val authViewModel: AuthViewModel = viewModel() // ✅ Correct ViewModel instance

                // ✅ Set up the navigation graph
                AppNavGraph(navController = navController, authViewModel = authViewModel)
            }
        }
    }
}