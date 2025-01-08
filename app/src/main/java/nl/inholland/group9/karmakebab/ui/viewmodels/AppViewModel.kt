package nl.inholland.group9.karmakebab.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import nl.inholland.group9.karmakebab.data.repositories.AuthRepository
import java.time.Instant
import javax.inject.Inject

@HiltViewModel
class AppViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _appState = MutableStateFlow<AppState>(AppState.Loading)
    val appState: StateFlow<AppState> = _appState.asStateFlow()

    private val _selectedIndex = MutableStateFlow(0)
    val selectedIndex: StateFlow<Int> = _selectedIndex.asStateFlow()

    private val _currentRoute = MutableStateFlow("home")
    val currentRoute: StateFlow<String> = _currentRoute.asStateFlow()

    private val routes = listOf("home", "calendar", "myhours", "profile")

    fun checkTokenState() {
        viewModelScope.launch {
            try {

                val currentTime = Instant.now()
                val accessTokenExpiration = authRepository.getAccessTokenExpiration()
                val refreshTokenExpiration = authRepository.getRefreshTokenExpiration()

                when {
                    refreshTokenExpiration != null && currentTime.isAfter(refreshTokenExpiration) -> {
                        // Both access and refresh tokens are expired
                        _appState.value = AppState.Login
                    }
                    accessTokenExpiration != null && currentTime.isAfter(accessTokenExpiration) -> {
                        // Access token is expired, refresh it
                        authRepository.refreshToken()
                        _appState.value = AppState.App
                    }
                    else -> {
                        // Access token is still valid
                        _appState.value = AppState.App
                    }
                }
            } catch (e: Exception) {
                _appState.value = AppState.Login // Handle errors by navigating to login
            }
        }
    }

    fun onLoginSuccess() {
        _appState.value = AppState.App
    }

    fun onNavigationItemSelected(index: Int) {
        _selectedIndex.value = index
        _currentRoute.value = routes[index]
    }

    fun getCurrentRoute(): String {
        return _currentRoute.value
    }

    // New method to dynamically update the current route
    fun updateCurrentRoute(route: String) {
        _currentRoute.value = route
    }
}

sealed class AppState {
    object Loading : AppState()
    object Login : AppState()
    object App : AppState()
}


