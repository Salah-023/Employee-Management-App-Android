package nl.inholland.group9.karmakebab.ui.views.HomePage

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.google.firebase.Timestamp
import nl.inholland.group9.karmakebab.R
import nl.inholland.group9.karmakebab.data.models.Event.Event
import nl.inholland.group9.karmakebab.data.models.shift.Shift
import nl.inholland.group9.karmakebab.ui.viewmodels.AppViewModel
import nl.inholland.group9.karmakebab.ui.viewmodels.HomePage.HomePageViewModel


@Composable
fun HomePageView(
    navController: NavController,
    appViewModel: AppViewModel,
    viewModel: HomePageViewModel = hiltViewModel()
) {
    val shiftsWithRoles by viewModel.shiftsWithRoles.collectAsState()
    val upcomingEvents by viewModel.upcomingEvents.collectAsState()
    val errorMessage by viewModel.error.collectAsState()
    val isLoadingShifts by viewModel.isLoadingShifts.collectAsState()
    val isLoadingEvents by viewModel.isLoadingEvents.collectAsState()

    val refreshKey by remember { mutableStateOf(0) } // State to trigger a refresh

    // Trigger fetches based on the refresh key
    LaunchedEffect(refreshKey) {
        viewModel.fetchShiftsForCurrentUser()
        viewModel.fetchUpcomingEvents()
    }

    // Use LazyColumn for scrolling
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header: Upcoming Shifts
        item {
            Text(
                text = "UPCOMING SHIFTS",
                fontSize = 20.sp,
                style = TextStyle(fontFamily = FontFamily(Font(R.font.mindset))),
                color = Color.Black,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        // Loading Indicator for Shifts
        if (isLoadingShifts) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color(0xFFE8468E))
                }
            }
        }

        // Shifts List
        if (!isLoadingShifts && shiftsWithRoles.isEmpty()) {
            item {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No upcoming shifts.",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
            }
        } else {
            items(
                items = shiftsWithRoles,
                key = { it.first.id!! } // Use the shift ID as the key to force recomposition when state changes
            ) { (shift, userRole) ->
                UpcomingShiftCard(
                    shift = shift,
                    userRole = userRole,
                    onClick = { navController.navigate("shiftDetail/${shift.id}") },
                    viewModel = viewModel,
                    navController = navController
                )
            }
        }

        item { Spacer(modifier = Modifier.height(10.dp)) }

        // Unavailable Button
        item {
            UnavailableButton(appViewModel = appViewModel, navController = navController)
        }

        item { Spacer(modifier = Modifier.height(10.dp)) }

        // Header: Upcoming Events
        item {
            Text(
                text = "UPCOMING EVENTS",
                fontSize = 20.sp,
                style = TextStyle(fontFamily = FontFamily(Font(R.font.mindset))),
                color = Color.Black,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        // Loading Indicator for Events
        if (isLoadingEvents) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color(0xFFE8468E))
                }
            }
        }

        // Events List
        if (!isLoadingEvents && upcomingEvents.isEmpty()) {
            item {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No upcoming events.",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
            }
        } else {
            items(upcomingEvents) { event ->
                UpcomingEventCard(event = event, viewModel)
            }
        }

        // "See More" Button
        item {
            SeeMoreButton(onClick = { navController.navigate("calendar") })
        }
    }
}

@Composable
fun UpcomingShiftCard(
    shift: Shift,
    userRole: String,
    onClick: () -> Unit,
    viewModel: HomePageViewModel,
    navController: NavController,
    showClockInButton: Boolean = true
) {
    // Observe clock-out and clock-out click states
    val clockOutStates by viewModel.clockOutStates.collectAsState()
    val clockOutClickedStates by viewModel.clockOutClickedStates.collectAsState()

    // States for Clock-In and Clock-Out Dialog Visibility
    var showClockInDialog by remember { mutableStateOf(false) }
    var showClockOutDialog by remember { mutableStateOf(false) }

    val canClockOut = clockOutStates[shift.id] ?: false
    val isClockOutClicked = clockOutClickedStates[shift.id] ?: false

    // Trigger state update when the composable is recomposed
    LaunchedEffect(shift.id) {
        viewModel.checkClockOutState(shift.id.toString())
    }

    // Predefined colors for teammates
    val teammateColors = listOf(Color(0xFFE8468E), Color(0xFFFFBFC6), Color(0xFFDFBFFF), Color(0xFF662C83))

    // Check if the shift is today
    val todayDate = viewModel.formatTimestampToString(Timestamp.now(), "yyyy-MM-dd")
    val shiftDate = shift.startTime?.let {
        viewModel.formatTimestampToString(it, "yyyy-MM-dd")
    }

    Card(
        shape = RoundedCornerShape(8.dp),
        backgroundColor = Color.White,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header: Event and Location
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    // Event Title
                    Text(
                        text = shift.event?.venue ?: "Unknown Event",
                        fontSize = 19.sp,
                        style = TextStyle(fontFamily = FontFamily(Font(R.font.mindset))),
                        color = Color(0xFF0D1829),
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    // Location
                    Text(
                        text = shift.event?.address ?: "Unknown Location",
                        fontSize = 16.sp,
                        style = TextStyle(fontFamily = FontFamily(Font(R.font.colby_stmed))),
                        color = Color(0xFF0D1829),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
                // Date and Time
                Column(horizontalAlignment = Alignment.End) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_calendar_hour),
                            contentDescription = null,
                            tint = Color(0xFF445668),
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = shift.startTime?.let {
                                viewModel.formatTimestampToString(it, "MMM dd, yyyy")
                            } ?: "Unknown Date",
                            fontSize = 16.sp,
                            style = TextStyle(fontFamily = FontFamily(Font(R.font.mindset))),
                            color = Color(0xFF445668)
                        )
                    }
                    Text(
                        text = "${shift.startTime?.let { viewModel.formatTimestampToString(it, "HH:mm") } ?: "?"} - ${
                            shift.endTime?.let { viewModel.formatTimestampToString(it, "HH:mm") } ?: "?"
                        }",
                        fontSize = 16.sp,
                        style = TextStyle(fontFamily = FontFamily(Font(R.font.colby_stmed))),
                        color = Color(0xFF445668)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Show Teammates and Role Section
            if (!showClockInButton || todayDate != shiftDate) {
                Column {
                    // Teammates Title
                    Text(
                        text = "Teammates",
                        fontSize = 14.sp,
                        style = TextStyle(fontFamily = FontFamily(Font(R.font.colby_stmed))),
                        color = Color(0xFF0D1829),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Teammates Section
                        Row {
                            shift.assignedUsers.take(4).forEachIndexed { index, teammate ->
                                TeammateBubble(
                                    initials = teammate.initials,
                                    color = teammateColors[index % teammateColors.size]
                                )
                            }
                            if (shift.assignedUsers.size > 4) {
                                Text(
                                    text = "+${shift.assignedUsers.size - 4}",
                                    fontSize = 14.sp,
                                    color = Color.Gray,
                                    modifier = Modifier.padding(start = 8.dp)
                                )
                            }
                        }

                        // Role Section
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.End
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_role),
                                contentDescription = "Role Icon",
                                tint = Color.Unspecified, // Show original icon color
                                modifier = Modifier
                                    .size(20.dp)
                                    .padding(end = 4.dp)
                            )
                            Text(
                                text = userRole,
                                fontSize = 14.sp,
                                style = TextStyle(fontFamily = FontFamily(Font(R.font.colby_streg))),
                                color = Color(0xFF0D1829)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            // Show the button only if the shift is today AND the card is on the homepage
            if (showClockInButton && todayDate == shiftDate) {
                Button(
                    onClick = {
                        if (canClockOut) {
                            showClockOutDialog = true // Show Clock-Out Dialog
                        } else if (!viewModel.clockedInState[shift.id]!!) {
                            showClockInDialog = true // Show Clock-In Dialog
                        } else {
                            navController.navigate("tasks/${shift.id}/${userRole}")
                        }
                    },
                    enabled = !isClockOutClicked,
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = if (isClockOutClicked) Color(0xFFFFC0CB) else if (canClockOut) Color.Red else Color(0xFFE8468E)
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = when {
                            isClockOutClicked -> "COMPLETED"
                            canClockOut -> "CLOCK-OUT"
                            viewModel.clockedInState[shift.id] == true -> "Go to Tasks"
                            else -> "CLOCK-IN"
                        },
                        fontFamily = FontFamily(Font(R.font.mindset)),
                        color = Color.White,
                        fontSize = 16.sp
                    )
                }
            }

        }
    }

    // Clock-In Confirmation Dialog
    if (showClockInDialog) {
        ConfirmationDialog(
            title = "Clock-In",
            message = "Are you sure you want to clock in?",
            onConfirm = {
                shift.id?.let {
                    // Perform clock-in logic
                    viewModel.clockIn(it)
                    viewModel.checkClockOutState(it) // Refresh clockOutStates

                    // No need for updateClockedInState; updatedShiftStates already handles it
                }
                showClockInDialog = false
            },
            onDismiss = { showClockInDialog = false }
        )
    }


    // Clock-Out Confirmation Dialog
    if (showClockOutDialog) {
        ConfirmationDialog(
            title = "Clock-Out",
            message = "Are you sure you want to clock out?",
            onConfirm = {
                shift.id?.let { viewModel.clockOut(it) }
                showClockOutDialog = false
            },
            onDismiss = { showClockOutDialog = false }
        )
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
fun UpcomingEventCard(event: Event, viewModel: HomePageViewModel) {
    Card(
        shape = RoundedCornerShape(8.dp),
        backgroundColor = Color.White,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp)) {
            // Left Section: Event Info
            Column(modifier = Modifier.weight(1f)) {
                // Event Title
                Text(
                    text = event.venue,
                    fontSize = 18.sp,
                    style = TextStyle(fontFamily = FontFamily(Font(R.font.mindset))),
                    color = Color.Black,
                    modifier = Modifier.padding(bottom = 4.dp)
                )

                // Location
                Text(
                    text = event.address,
                    fontSize = 14.sp,
                    fontFamily = FontFamily(Font(R.font.colby_stmed)), // Font changed to colby_stmed
                    color = Color(0xFF565D69), // Color changed to 565D69
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            // Right Section: Date and Time
            Column(horizontalAlignment = Alignment.End) {
                // Date
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_calendar_hour),
                        contentDescription = "Calendar",
                        tint = Color(0xFF445668), // Color changed to 445668
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = event.startTime?.let { viewModel.formatTimestampToString(it, "MMM dd, yyyy") }
                            ?: "Unknown Date",
                        fontSize = 14.sp,
                        fontFamily = FontFamily(Font(R.font.mindset)), // Font changed to mindset
                        color = Color(0xFF445668) // Color changed to 445668
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                // Time
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (event.status == "draft") {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_exclamation),
                            contentDescription = "Draft Status",
                            tint = Color.Red,
                            modifier = Modifier.size(20.dp).padding(end = 4.dp)
                        )
                    }
                    Text(
                        text = "${event.startTime?.let { viewModel.formatTimestampToString(it, "HH:mm") } ?: "?"} - ${
                            event.endTime?.let { viewModel.formatTimestampToString(it, "HH:mm") } ?: "?"
                        }",
                        fontSize = 14.sp,
                        fontFamily = FontFamily(Font(R.font.colby_stmed)), // Font changed to colby_stmed
                        color = if (event.status == "draft") Color.Red else Color.Gray
                    )
                }
            }
        }
    }
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

@Composable
fun ConfirmationDialog(
    title: String,
    message: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = title,
                fontSize = 18.sp,
                fontFamily = FontFamily(Font(R.font.colby_stmed)),
                color = Color.Black
            )
        },
        text = {
            Text(
                text = message,
                fontSize = 16.sp,
                fontFamily = FontFamily(Font(R.font.colby_streg)),
                color = Color.Gray
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(
                    text = "Confirm",
                    fontSize = 16.sp,
                    color = Color(0xFFE8468E),
                    fontFamily = FontFamily(Font(R.font.colby_stmed))
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = "Cancel",
                    fontSize = 16.sp,
                    color = Color.Gray,
                    fontFamily = FontFamily(Font(R.font.colby_streg))
                )
            }
        }
    )
}



