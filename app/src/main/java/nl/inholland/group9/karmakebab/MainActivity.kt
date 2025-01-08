package nl.inholland.group9.karmakebab

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.FirebaseApp
import dagger.hilt.android.AndroidEntryPoint
import nl.inholland.group9.karmakebab.ui.viewmodels.AppState
import nl.inholland.group9.karmakebab.ui.viewmodels.AppViewModel
import nl.inholland.group9.karmakebab.ui.views.AppView
import nl.inholland.group9.karmakebab.ui.views.Login.SplashLoginScreen


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize Firebase
        FirebaseApp.initializeApp(this)

        setContent {
            val appViewModel: AppViewModel = viewModel()
            val navController = rememberNavController()
            val appState by appViewModel.appState.collectAsState(initial = AppState.Loading)

            LaunchedEffect(Unit) {
                appViewModel.checkTokenState() // Check token state on app startup
            }

            when (appState) {
                is AppState.Loading -> {
                    // Show a loading screen
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator() // Simple loading indicator
                    }
                }
                else -> {
                    // Display the navigation graph after the loading state
                    NavHost(
                        navController = navController,
                        startDestination = if (appState is AppState.App) "app" else "login"
                    ) {
                        composable("login") {
                            SplashLoginScreen(
                                onLoginSuccess = {
                                    appViewModel.onLoginSuccess() // Update app state on login success
                                    navController.navigate("app") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                }
                            )
                        }
                        composable("app") {
                            AppView(
                                onLogout = {
                                    navController.navigate("login") { // Navigate back to login
                                        popUpTo("app") { inclusive = true } // Clear app route from the stack
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
