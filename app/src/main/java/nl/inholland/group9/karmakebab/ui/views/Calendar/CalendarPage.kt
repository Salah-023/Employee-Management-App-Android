package nl.inholland.group9.karmakebab.ui.views.Calendar


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
import nl.inholland.group9.karmakebab.ui.viewmodels.Calender.CalendarViewModel
import nl.inholland.group9.karmakebab.ui.viewmodels.HomePage.HomePageViewModel
import nl.inholland.group9.karmakebab.ui.views.HomePage.UpcomingEventCard
import nl.inholland.group9.karmakebab.ui.views.HomePage.UpcomingShiftCard


@Composable
fun CalendarPage(navController: NavController, calendarViewModel: CalendarViewModel = hiltViewModel(), homePageViewModel: HomePageViewModel = hiltViewModel()) {
    val selectedTab by calendarViewModel.selectedTab.collectAsState()
    val currentWeekNumber by calendarViewModel.currentWeekNumber.collectAsState()
    val currentWeekDates by calendarViewModel.currentWeekDates.collectAsState()
    val shifts by calendarViewModel.shifts.collectAsState()
    val events by calendarViewModel.events.collectAsState()
    val isCurrentWeek by calendarViewModel.isCurrentWeek.collectAsState()
    val isLoadingShifts by calendarViewModel.isLoadingShifts.collectAsState()
    val isLoadingEvents by calendarViewModel.isLoadingEvents.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        // Updated Tab Selector
        CustomTabSelector(
            selectedTab = selectedTab,
            onTabSelected = { calendarViewModel.selectTab(it) }
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Week Selector
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { calendarViewModel.previousWeek() }) {
                Icon(
                    painter = painterResource(R.drawable.ic_back_calendar),
                    contentDescription = "Previous Week",
                    tint = Color(0xFFE8468E),
                    modifier = Modifier.size(32.dp)
                )
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Week $currentWeekNumber",
                    fontSize = 18.sp,
                    color = Color.Black,
                    style = TextStyle(fontFamily = FontFamily(Font(R.font.colby_stmed)))
                )
                Text(
                    text = currentWeekDates,
                    fontSize = 14.sp,
                    color = Color.Gray,
                    style = TextStyle(fontFamily = FontFamily(Font(R.font.colby_streg)))
                )
            }
            IconButton(onClick = { calendarViewModel.nextWeek() }) {
                Icon(
                    painter = painterResource(R.drawable.ic_next_calendar),
                    contentDescription = "Next Week",
                    tint = Color(0xFFE8468E),
                    modifier = Modifier.size(32.dp)
                )
            }
        }

        // "Return to Current Week" Button
        if (!isCurrentWeek) {
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { calendarViewModel.returnToCurrentWeek() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFFFE3E3)),
                shape = RoundedCornerShape(24.dp)
            ) {
                Text(
                    text = "Return to current week",
                    color = Color.Black,
                    fontSize = 16.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Display Shifts or Events
        if (selectedTab == 0) {
            if (isLoadingShifts) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color(0xFFE8468E))
                }
            } else if (shifts.isEmpty()) {
                Text(
                    text = "No shifts this week",
                    fontSize = 18.sp,
                    color = Color.Black,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            } else {
                shifts.forEach { (shift, userRole) ->
                    UpcomingShiftCard(
                        shift = shift,
                        userRole = userRole,
                        onClick = { navController.navigate("shiftDetail/${shift.id}") },
                        viewModel = homePageViewModel,
                        navController = navController,
                        showClockInButton = false
                    )
                }
            }
        } else {
            if (isLoadingEvents) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color(0xFFE8468E))
                }
            } else if (events.isEmpty()) {
                Text(
                    text = "No events this week",
                    fontSize = 18.sp,
                    color = Color.Black,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            } else {
                events.forEach { event ->
                    UpcomingEventCard(
                        event = event,
                        viewModel = homePageViewModel
                    )
                }
            }
        }
    }
}

@Composable
fun CustomTabSelector(selectedTab: Int, onTabSelected: (Int) -> Unit) {
    val tabBackgroundColor = Color(0xFFE8468E)
    val selectedTabColor = Color(0xFFA23163)
    val mindsetFont = FontFamily(Font(R.font.mindset))

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .background(tabBackgroundColor, shape = RoundedCornerShape(24.dp)), // Rounded box
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // My Schedule Tab
        Box(
            modifier = Modifier
                .weight(1f) // Equal width for both tabs
                .fillMaxHeight()
                .clickable { onTabSelected(0) },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "My Schedule",
                fontFamily = mindsetFont,
                fontSize = 17.sp,
                color = Color.White,
                modifier = Modifier
                    .padding(horizontal = 15.dp)
                    .background(
                        if (selectedTab == 0) selectedTabColor else Color.Transparent,
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(vertical = 4.dp, horizontal = 16.dp) // Add padding inside the box
            )
        }

        // Upcoming Events Tab
        Box(
            modifier = Modifier
                .weight(1f) // Equal width for both tabs
                .fillMaxHeight()
                .clickable { onTabSelected(1) },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Upcoming Events",
                fontFamily = mindsetFont,
                fontSize = 18.sp,
                color = Color.White,
                modifier = Modifier
                    .padding(horizontal = 15.dp)
                    .background(
                        if (selectedTab == 1) selectedTabColor else Color.Transparent,
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(vertical = 4.dp, horizontal = 16.dp) // Add padding inside the box
            )
        }
    }
}