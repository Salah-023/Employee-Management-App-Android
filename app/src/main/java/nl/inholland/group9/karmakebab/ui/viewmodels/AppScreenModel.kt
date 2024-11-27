package nl.inholland.group9.karmakebab.viewmodels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class AppViewModel : ViewModel() {

    // Holds the index of the currently selected navigation item
    val selectedIndex = mutableStateOf(0)

    // Holds the current route
    val currentRoute = mutableStateOf("home")

    // List of routes corresponding to the bottom navigation items
    private val routes = listOf("home", "calendar", "myhours", "profile")

    // Function to handle navigation item selection
    fun onNavigationItemSelected(index: Int) {
        selectedIndex.value = index
        currentRoute.value = routes[index]
    }

    // Function to get the route based on the selected index
    fun getRoute(index: Int): String {
        return routes[index]
    }
}
