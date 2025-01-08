package nl.inholland.group9.karmakebab.ui.views.HomePage

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import nl.inholland.group9.karmakebab.data.models.shift.Shift
import nl.inholland.group9.karmakebab.ui.viewmodels.AppViewModel
import nl.inholland.group9.karmakebab.ui.viewmodels.HomePage.HomePageViewModel


@Composable
fun HomePageView(
    navController: NavController,
    appViewModel: AppViewModel,
    viewModel: HomePageViewModel = hiltViewModel()
) {
    val shifts by viewModel.shifts.collectAsState()
    val errorMessage by viewModel.error.collectAsState()

    // Fetch shifts when the screen is loaded
    LaunchedEffect(Unit) {
        viewModel.fetchShiftsForCurrentUser()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header: Upcoming Shifts
        Text(
            text = "UPCOMING SHIFTS",
            fontSize = 16.sp,
            style = TextStyle(
                fontFamily = FontFamily(Font(R.font.mindset))
            ),
            color = Color.Black,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Error Message
        errorMessage?.let {
            Text(
                text = it,
                color = Color.Red,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }

        // Shifts List
        if (shifts.isEmpty()) {
            Text(
                text = "No upcoming shifts.",
                fontSize = 14.sp,
                color = Color.Gray,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        } else {
            shifts.forEach { shift ->
                UpcomingShiftCard(
                    shift = shift,
                    onClick = {
                        navController.navigate("shiftDetail/${shift.id}")
                    },
                    viewModel = viewModel // Pass the ViewModel
                )
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

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

        // "See More" Button
        SeeMoreButton(
            onClick = { navController.navigate("calendar") }
        )
    }
}

@Composable
fun UpcomingShiftCard(
    shift: Shift,
    onClick: () -> Unit,
    viewModel: HomePageViewModel // Add ViewModel parameter
) {
    // Format startTime and endTime as strings
    val startDate = viewModel.formatTimestampToString(shift.startTime)
    val startTime = viewModel.formatTimestampToString(shift.startTime)
    val endTime = viewModel.formatTimestampToString(shift.endTime)

    Card(
        shape = RoundedCornerShape(8.dp),
        backgroundColor = Color.White,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onClick() }
    ) {
        Row(modifier = Modifier.padding(16.dp)) {
            // Left Section: Shift Info
            Column(modifier = Modifier.weight(1f)) {
                // Shift Title
                Text(
                    text = shift.event?.venue ?: "Unknown Event",
                    fontSize = 18.sp,
                    style = TextStyle(fontFamily = FontFamily(Font(R.font.mindset))),
                    color = Color.Black,
                    modifier = Modifier.padding(bottom = 4.dp)
                )

                // Location
                Text(
                    text = shift.event?.address ?: "Unknown Location",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // Teammates Section
                Row(verticalAlignment = Alignment.CenterVertically) {
                    shift.assignedUsers.take(3).forEach { teammate ->
                        TeammateBubble(
                            initials = teammate.initials,
                            color = Color(0xFFE8468E)
                        )
                    }
                    if (shift.assignedUsers.size > 3) {
                        Text(
                            text = "+${shift.assignedUsers.size - 3}",
                            fontSize = 14.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
            }

            // Right Section: Date and Time
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = startDate, // Date as a string
                    fontSize = 14.sp,
                    color = Color.Black,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Text(
                    text = "$startTime - $endTime", // Time range as a string
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
            style = TextStyle(fontFamily = FontFamily(Font(R.font.mindset)))
        )
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
            appViewModel.onNavigationItemSelected(1) // Navigate to "my hours"
            navController.navigate("myhours") {
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
        colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF5B2D86)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                painter = painterResource(id = iconId),
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(24.dp)
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

//@Composable
//fun UpcomingEventCard(event: Event) {
//    Card(
//        shape = RoundedCornerShape(8.dp),
//        backgroundColor = Color.White,
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(vertical = 8.dp)
//    ) {
//        Row(modifier = Modifier.padding(16.dp)) {
//            // Left Section: Event Info
//            Column(modifier = Modifier.weight(1f)) {
//                // Event Title
//                Text(
//                    text = event.title,
//                    fontSize = 18.sp,
//                    style = TextStyle(fontFamily = FontFamily(Font(R.font.mindset))),
//                    color = Color.Black,
//                    modifier = Modifier.padding(bottom = 4.dp)
//                )
//
//                // Location
//                Text(
//                    text = event.location,
//                    fontSize = 14.sp,
//                    color = Color.Gray,
//                    modifier = Modifier.padding(bottom = 8.dp)
//                )
//            }
//
//            // Right Section: Date and Time
//            Column(horizontalAlignment = Alignment.End) {
//                Row(verticalAlignment = Alignment.CenterVertically) {
//                    Icon(
//                        painter = painterResource(id = R.drawable.ic_calendar_hour),
//                        contentDescription = "Calendar",
//                        tint = Color.Gray,
//                        modifier = Modifier.size(20.dp)
//                    )
//                    Spacer(modifier = Modifier.width(4.dp))
//                    Text(
//                        text = event.date,
//                        fontSize = 14.sp,
//                        color = Color.Gray
//                    )
//                }
//                Spacer(modifier = Modifier.height(4.dp))
//                Row(verticalAlignment = Alignment.CenterVertically) {
//                    Icon(
//                        painter = painterResource(id = R.drawable.ic_exclamation),
//                        contentDescription = "Clock",
//                        tint = Color.Red,
//                        modifier = Modifier.size(20.dp)
//                    )
//                    Spacer(modifier = Modifier.width(4.dp))
//                    Text(
//                        text = "${event.startTime} - ${event.endTime}",
//                        fontSize = 14.sp,
//                        color = Color.Red
//                    )
//                }
//            }
//        }
//    }
//}

