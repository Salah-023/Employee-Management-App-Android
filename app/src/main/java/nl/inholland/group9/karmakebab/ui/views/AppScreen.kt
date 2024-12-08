package nl.inholland.group9.karmakebab.ui.views

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import nl.inholland.group9.karmakebab.ui.viewmodels.HeaderViewModel
import nl.inholland.group9.karmakebab.ui.views.Calendar.CalendarPage
import nl.inholland.group9.karmakebab.ui.views.Myhours.MyHoursPage
import nl.inholland.group9.karmakebab.ui.views.Profile.ProfilePage
import nl.inholland.group9.karmakebab.viewmodels.AppViewModel
import nl.inholland.group9.karmakebab.views.BottomNavigationBar
import nl.inholland.group9.karmakebab.views.HomePageView

@Composable
fun AppScreen(
    appViewModel: AppViewModel = hiltViewModel(),
    headerViewModel: HeaderViewModel = hiltViewModel()
) {
    val navController = rememberNavController()
    val currentRoute by appViewModel.currentRoute.collectAsState()
    val selectedIndex by appViewModel.selectedIndex.collectAsState()
    val profileInitials by headerViewModel.initials.collectAsState()

    Scaffold(
        bottomBar = {
            if (currentRoute != "login" && currentRoute != "splash") {
                BottomNavigationBar(
                    selectedIndex = selectedIndex,
                    onItemSelected = { index ->
                        appViewModel.onNavigationItemSelected(index)
                        navController.navigate(appViewModel.getCurrentRoute()) {
                            popUpTo(navController.graph.startDestinationId) { inclusive = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    profileInitials = profileInitials
                )
            }
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            if (currentRoute != "profile" && currentRoute != "login" && currentRoute != "splash") {
                HomeHeader()
            }

            NavHost(
                navController = navController,
                startDestination = "home",
                modifier = Modifier.fillMaxSize()
            ) {
                composable("home") {
                    HomePageView(navController = navController, appViewModel = appViewModel)
                }
                composable("calendar") { CalendarPage() }
                composable("myhours") { MyHoursPage() }
                composable("profile") { ProfilePage() }
            }
        }
    }
}
