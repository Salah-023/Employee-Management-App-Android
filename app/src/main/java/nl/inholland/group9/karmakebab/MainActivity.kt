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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import nl.inholland.group9.karmakebab.data.utils.TokenManager
import nl.inholland.group9.karmakebab.ui.views.AppView
import nl.inholland.group9.karmakebab.ui.views.Login.SplashLoginScreen
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var tokenManager: TokenManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val navController = rememberNavController()
            val tokenState = tokenManager.accessToken.collectAsState(initial = null)

            // Only render content after token state is fully loaded
            if (tokenState.value != null) {
                val startDestination = if (tokenState.value.isNullOrEmpty()) "login" else "app"

                NavHost(navController = navController, startDestination = startDestination) {
                    composable("login") {
                        SplashLoginScreen(
                            onLoginSuccess = {
                                navController.navigate("app") {
                                    popUpTo("login") { inclusive = true }
                                }
                            }
                        )
                    }
                    composable("app") {
                        AppView() // App content screen
                    }
                }
            } else {
                // Optionally show a loading indicator until the token state is loaded
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}

