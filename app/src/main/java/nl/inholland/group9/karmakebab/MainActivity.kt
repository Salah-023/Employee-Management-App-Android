package nl.inholland.group9.karmakebab

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import nl.inholland.group9.karmakebab.data.utils.TokenManager
import nl.inholland.group9.karmakebab.ui.views.AppScreen
import nl.inholland.group9.karmakebab.views.SplashLoginScreen
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var tokenManager: TokenManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()

            // Check for token existence
            val tokenExists = runBlocking { tokenManager.accessToken.firstOrNull() != null }

            NavHost(
                navController = navController,
                startDestination = if (tokenExists) "app" else "login"
            ) {
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
                    AppScreen()
                }
            }
        }
    }
}

