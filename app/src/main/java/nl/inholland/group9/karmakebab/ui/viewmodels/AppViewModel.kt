package nl.inholland.group9.karmakebab.ui.viewmodels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

class AppViewModel @Inject constructor() : ViewModel() {

    // Holds the index of the currently selected navigation item
    private val _selectedIndex = MutableStateFlow(0)
    val selectedIndex: StateFlow<Int> = _selectedIndex.asStateFlow()

    // Holds the current route
    private val _currentRoute = MutableStateFlow("home")
    val currentRoute: StateFlow<String> = _currentRoute.asStateFlow()

    // List of routes corresponding to the bottom navigation items
    private val routes = listOf("home", "calendar", "myhours", "profile")

    // Function to handle navigation item selection
    fun onNavigationItemSelected(index: Int) {
        _selectedIndex.value = index
        _currentRoute.value = routes[index]
    }

    // Get the current route (navigation destination)
    fun getCurrentRoute(): String {
        return _currentRoute.value
    }
}