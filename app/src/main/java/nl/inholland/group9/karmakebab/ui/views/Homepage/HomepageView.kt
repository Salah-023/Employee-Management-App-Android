package nl.inholland.group9.karmakebab.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import nl.inholland.group9.karmakebab.R
import nl.inholland.group9.karmakebab.data.models.events.EventResponse
import nl.inholland.group9.karmakebab.data.models.shift.Shift
import nl.inholland.group9.karmakebab.ui.viewmodels.Homepage.HomeViewModel
import nl.inholland.group9.karmakebab.viewmodels.AppViewModel

@Composable
fun HomePageView(
    navController: NavController,
    appViewModel: AppViewModel,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val shifts by viewModel.shifts.collectAsState()
    val events by viewModel.events.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val isLoadingEvents by viewModel.isLoadingEvents.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // Header: Upcoming Shifts
        Text(
            text = "UPCOMING SHIFTS",
            fontSize = 16.sp,
            style = TextStyle(fontFamily = androidx.compose.ui.text.font.FontFamily(
                androidx.compose.ui.text.font.Font(R.font.mindset)
            )),
            color = Color.Black,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Loading Indicator
        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        }

        // Error Message
        errorMessage?.let {
            Text(
                text = it,
                color = Color.Red,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }

        // Shifts List
        shifts.forEach { shift ->
            UpcomingShiftCard(shift = shift)
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Unavailable Button
        UnavailableButton(
            appViewModel = appViewModel,
            navController = navController
        )

        Spacer(modifier = Modifier.height(40.dp))

        // Header: Upcoming Events
        Text(
            text = "UPCOMING EVENTS",
            fontSize = 16.sp,
            style = TextStyle(fontFamily = FontFamily(Font(R.font.mindset))),
            color = Color.Black,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Loading Indicator for Events
        if (isLoadingEvents) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        } else {
            events.forEach { event ->
                EventCard(event = event)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        // "See More" Button
        SeeMoreButton(
            onClick = { navController.navigate("events") }
        )
    }
}

@Composable
fun UpcomingShiftCard(shift: Shift) {
    // Extracting date and time from startTime and endTime
    val startDate = shift.startTime.substring(0, 10) // YYYY-MM-DD
    val startTime = shift.startTime.substring(11, 16) // HH:MM
    val endTime = shift.endTime.substring(11, 16) // HH:MM

    Card(
        shape = RoundedCornerShape(8.dp),
        backgroundColor = Color.White,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp)) {
            // Left Section: Shift Info
            Column(modifier = Modifier.weight(1f)) {
                // Shift Title (hardcoded for now)
                Text(
                    text = "VEGAN SUMMER FESTIVAL",
                    fontSize = 18.sp,
                    style = TextStyle(fontFamily = androidx.compose.ui.text.font.FontFamily(
                        androidx.compose.ui.text.font.Font(R.font.mindset)
                    )),
                    color = Color.Black,
                    modifier = Modifier.padding(bottom = 4.dp)
                )

                // Location (hardcoded for now)
                Text(
                    text = "Prinses 202, Utrecht",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // Teammates Section (hardcoded initials for now)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    TeammateBubble("AZ", Color(0xFFE8468E))
                    TeammateBubble("BR", Color(0xFFF29FA8))
                    TeammateBubble("SZ", Color(0xFF5B2D86))
                    Text(
                        text = "+3",
                        fontSize = 14.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }

            // Right Section: Dynamic Date and Time
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = startDate, // Display the date (YYYY-MM-DD format)
                    fontSize = 14.sp,
                    color = Color.Black,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Text(
                    text = "$startTime - $endTime", // Display the time range (HH:MM - HH:MM)
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
fun TeammateBubble(initials: String, color: Color) {
    Box(
        modifier = Modifier
            .size(32.dp)
            .background(color, shape = RoundedCornerShape(16.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = initials,
            color = Color.White,
            fontSize = 14.sp,
            style = TextStyle(fontFamily = androidx.compose.ui.text.font.FontFamily(
                androidx.compose.ui.text.font.Font(R.font.mindset)
            ))
        )
    }
}


@Composable
fun EventCard(event: EventResponse) {
    Card(
        shape = RoundedCornerShape(8.dp),
        backgroundColor = Color.White,
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = event.description,
                fontSize = 18.sp,
                style = TextStyle(fontFamily = FontFamily(Font(R.font.mindset))),
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = event.address, fontSize = 14.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Date: ${event.date.substring(0, 10)} - Time: ${event.date.substring(11, 16)}",
                fontSize = 14.sp,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun UnavailableButton(
    appViewModel: AppViewModel,
    navController: NavController
) {
    SharedButton(
        text = "Unavailable?",
        iconId = R.drawable.ic_unavailable,
        onClick = {
            appViewModel.onNavigationItemSelected(1) // Set to calendar index
            navController.navigate("calendar") {
                launchSingleTop = true
                restoreState = true
            }
        }
    )
}

@Composable
fun SeeMoreButton(onClick: () -> Unit) {
    SharedButton(
        text = "See more",
        iconId = R.drawable.ic_see_more,
        onClick = onClick
    )
}

@Composable
fun SharedButton(
    text: String,
    iconId: Int,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
        colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF5B2D86)), // Purple background
        shape = RoundedCornerShape(12.dp) // Rounded corners
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                painter = painterResource(id = iconId), // Pass the icon dynamically
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(24.dp) // Ensure consistent icon size
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = text,
                color = Color.White,
                fontSize = 16.sp
            )
        }
    }
}
