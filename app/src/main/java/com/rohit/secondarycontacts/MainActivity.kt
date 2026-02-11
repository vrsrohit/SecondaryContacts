package com.rohit.secondarycontacts

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.rohit.secondarycontacts.navigation.NavGraph
import com.rohit.secondarycontacts.ui.theme.SecondaryContactsTheme
import com.rohit.secondarycontacts.viewmodel.ContactViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SecondaryContactsTheme {
                val navController = rememberNavController()
                val viewModel: ContactViewModel = viewModel()
                NavGraph(navController = navController, viewModel = viewModel)
            }
        }
    }
}
