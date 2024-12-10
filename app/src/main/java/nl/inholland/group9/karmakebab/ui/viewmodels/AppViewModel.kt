package nl.inholland.group9.karmakebab.ui.viewmodels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

class AppViewModel @Inject constructor() : ViewModel() {

    private val _selectedIndex = MutableStateFlow(0)
    val selectedIndex: StateFlow<Int> = _selectedIndex.asStateFlow()

    private val _currentRoute = MutableStateFlow("home")
    val currentRoute: StateFlow<String> = _currentRoute.asStateFlow()

    private val routes = listOf("home", "calendar", "myhours", "profile")

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