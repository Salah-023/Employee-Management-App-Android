package nl.inholland.group9.karmakebab.ui.views.Calendar

import android.os.Build
import androidx.annotation.RequiresApi
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
import nl.inholland.group9.karmakebab.ui.views.HomePage.UpcomingEventCard
import nl.inholland.group9.karmakebab.ui.views.HomePage.UpcomingShiftCard

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CalendarPage(navController: NavController,viewModel: CalendarViewModel = hiltViewModel()) {
    val selectedTab by viewModel.selectedTab.collectAsState()
    val currentWeekNumber by viewModel.currentWeekNumber.collectAsState()
    val currentWeekDates by viewModel.currentWeekDates.collectAsState()
    val shifts by viewModel.shifts.collectAsState()
    val events by viewModel.events.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        // Updated Tab Selector
        CustomTabSelector(
            selectedTab = selectedTab,
            onTabSelected = { viewModel.selectTab(it) }
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Week Selector
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { viewModel.previousWeek() }) {
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
            IconButton(onClick = { viewModel.nextWeek() }) {
                Icon(
                    painter = painterResource(R.drawable.ic_next_calendar),
                    contentDescription = "Next Week",
                    tint = Color(0xFFE8468E),
                    modifier = Modifier.size(32.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Display Shifts or Events
        if (selectedTab == 0) {
            if (shifts.isEmpty()) {
                Text(
                    text = "No shifts this week",
                    fontSize = 18.sp,
                    color = Color.Black,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            } else {
                shifts.forEach { shift ->
                    UpcomingShiftCard(
                        shift = shift,
                        onClick = { navController.navigate("shiftDetail/${shift.shiftId}") }
                    )
                }
            }
        } else {
            if (events.isEmpty()) {
                Text(
                    text = "No events this week",
                    fontSize = 18.sp,
                    color = Color.Black,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            } else {
                events.forEach { event ->
                    UpcomingEventCard(event = event)
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