package com.cenkeraydin.ttagmobil

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.cenkeraydin.ttagmobil.ui.theme.TtagMobilTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TtagMobilTheme {
                val navController: NavHostController =rememberNavController()
                val context = this
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                   AppNavHost(navController,context)
                }
            }
        }
    }
}
