package nl.inholland.group9.karmakebab.ui.views

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import nl.inholland.group9.karmakebab.ui.viewmodels.AppViewModel
import nl.inholland.group9.karmakebab.ui.viewmodels.Header.HeaderViewModel
import nl.inholland.group9.karmakebab.ui.viewmodels.HomePage.HomePageViewModel
import nl.inholland.group9.karmakebab.ui.views.BottomNavigationBar.BottomNavigationBar
import nl.inholland.group9.karmakebab.ui.views.Header.Header
import nl.inholland.group9.karmakebab.ui.views.Calendar.CalendarPage
import nl.inholland.group9.karmakebab.ui.views.HomePage.HomePageView
import nl.inholland.group9.karmakebab.ui.views.HomePage.ShiftDetailView
import nl.inholland.group9.karmakebab.ui.views.MyHours.MyHoursPage
import nl.inholland.group9.karmakebab.ui.views.Profile.ProfilePage


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppView(
    appViewModel: AppViewModel = hiltViewModel(),
    headerViewModel: HeaderViewModel = hiltViewModel(),
    homePageViewModel: HomePageViewModel = hiltViewModel()
) {
    val navController = rememberNavController()
    //navController.currentDestination.route
    val currentRoute by appViewModel.currentRoute.collectAsState()
    val selectedIndex by appViewModel.selectedIndex.collectAsState()
    val profileInitials by headerViewModel.initials.collectAsState()

    // Update `currentRoute` whenever the route changes
    navController.addOnDestinationChangedListener { _, destination, _ ->
        appViewModel.updateCurrentRoute(destination.route ?: "home")
    }

    Scaffold(
        bottomBar = {
            // Hide bottom navigation bar for specific routes
            if (currentRoute !in listOf("login", "splash") && !currentRoute.startsWith("shiftDetail")) {
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
            // Hide header for specific routes
            if (currentRoute !in listOf("profile", "login", "splash") && !currentRoute.startsWith("shiftDetail")) {
                Header()
            }

            NavHost(
                navController = navController,
                startDestination = "home",
                modifier = Modifier.fillMaxSize()
            ) {
                composable("home") {
                    HomePageView(
                        navController = navController,
                        appViewModel = appViewModel,
                        viewModel = homePageViewModel
                    )
                }
                composable(
                    "shiftDetail/{shiftId}",
                    arguments = listOf(navArgument("shiftId") { type = NavType.StringType })
                ) { backStackEntry ->
                    val shiftId = backStackEntry.arguments?.getString("shiftId")
                    val shifts by homePageViewModel.shifts.collectAsState()

                    val shift = shifts.find { it.shiftId == shiftId }
                    shift?.let {
                        ShiftDetailView(navController = navController, shift = it)
                    }
                }
                composable("calendar") { CalendarPage(navController = navController) }
                composable("myhours") { MyHoursPage() }
                composable("profile") { ProfilePage() }
            }
        }
    }
}
