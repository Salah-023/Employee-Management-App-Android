package nl.inholland.group9.karmakebab.ui.views

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import nl.inholland.group9.karmakebab.ui.views.Calendar.CalendarPage
import nl.inholland.group9.karmakebab.ui.views.Myhours.MyHoursPage
import nl.inholland.group9.karmakebab.ui.views.Profile.ProfilePage
import nl.inholland.group9.karmakebab.viewmodels.AppViewModel
import nl.inholland.group9.karmakebab.views.BottomNavigationBar
import nl.inholland.group9.karmakebab.views.HomePage

@Composable
fun AppScreen(appViewModel: AppViewModel = hiltViewModel()) {
    val navController = rememberNavController()
    val currentRoute by appViewModel.currentRoute

    // Observe navigation changes and update navController
    LaunchedEffect(currentRoute) {
        navController.navigate(currentRoute) {
            popUpTo(navController.graph.startDestinationId) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    Scaffold(
        bottomBar = {
            // Show BottomNavigationBar for specific screens
            if (currentRoute != "login" && currentRoute != "splash") {
                BottomNavigationBar(
                    selectedIndex = appViewModel.selectedIndex.value,
                    onItemSelected = { index ->
                        appViewModel.onNavigationItemSelected(index)
                    }
                )
            }
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            // Conditional Header
            if (currentRoute != "profile" && currentRoute != "login" && currentRoute != "splash") {
                HomeHeader()
            }

            // Navigation Host for Pages
            NavHost(
                navController = navController,
                startDestination = "home",
                modifier = Modifier.fillMaxSize()
            ) {
                composable("home") { HomePage() }
                composable("calendar") { CalendarPage() }
                composable("myhours") { MyHoursPage() }
                composable("profile") { ProfilePage() }
            }
        }
    }
}
