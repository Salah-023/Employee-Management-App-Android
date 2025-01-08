package nl.inholland.group9.karmakebab.ui.views.MyHours

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
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
import nl.inholland.group9.karmakebab.R
import nl.inholland.group9.karmakebab.ui.viewmodels.Myhours.MyHoursViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MyHoursPage(viewModel: MyHoursViewModel = hiltViewModel()) {
    val currentMonth by viewModel.currentMonth.collectAsState()
    val currentYear by viewModel.currentYear.collectAsState()
    val daysInMonth by viewModel.daysInMonth.collectAsState()
    val selectedDate by viewModel.selectedDate.collectAsState()
    val selectedDayAvailability by viewModel.selectedDayAvailability.collectAsState()
    var showModal by remember { mutableStateOf(false) } // To control modal visibility

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header
        Text(
            text = "MY WORKING HOURS",
            style = TextStyle(
                fontFamily = FontFamily(Font(R.font.mindset)),
                fontSize = 20.sp
            ),
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Month and Year Navigation
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { viewModel.goToPreviousMonth() }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_back_calendar),
                    contentDescription = "Previous Month",
                    tint = Color(0xFFE8468E),
                    modifier = Modifier.size(32.dp)
                )
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = currentMonth,
                    fontSize = 18.sp,
                    color = Color.Black
                )
                Text(
                    text = currentYear.toString(),
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
            IconButton(onClick = { viewModel.goToNextMonth() }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_next_calendar),
                    contentDescription = "Next Month",
                    tint = Color(0xFFE8468E),
                    modifier = Modifier.size(32.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(daysInMonth.size) { index ->
                val day = daysInMonth[index]
                val availability = day?.let { viewModel.getAvailabilityForDay(it) } // Get availability for the day

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    // Circle for the day number
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(
                                if (selectedDate.dayOfMonth == day) Color(0xFFE8468E) else Color.Transparent,
                                shape = RoundedCornerShape(50)
                            )
                            .clickable {
                                if (day != null) viewModel.selectDate(day)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = day?.toString() ?: "",
                            color = if (selectedDate.dayOfMonth == day) Color.White else Color.Black,
                            fontSize = 14.sp
                        )
                    }

                    // Availability dot
                    if (day != null) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(
                                    color = when (availability?.timeRange) {
                                        "All Day" -> Color.Green
                                        null -> Color.Red
                                        else -> Color.Yellow
                                    },
                                    shape = RoundedCornerShape(50)
                                )
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Selected Date and Availability Section
        if (selectedDayAvailability != null || selectedDate != null) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .background(Color.White, shape = RoundedCornerShape(10.dp))
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Availability Indicator
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .background(
                            color = when {
                                selectedDayAvailability == null -> Color.Red // Not available
                                selectedDayAvailability!!.timeRange == "All Day" -> Color.Green // Available all day
                                else -> Color.Yellow // Partially available
                            },
                            shape = RoundedCornerShape(50)
                        )
                )
                Spacer(modifier = Modifier.width(8.dp))

                // Date and Time Range
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = selectedDate.toString(),
                        fontSize = 16.sp,
                        color = Color.Black
                    )
                    Text(
                        text = selectedDayAvailability?.timeRange ?: "Not Available",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }

                // Edit Button
                IconButton(
                    onClick = { showModal = true },
                    modifier = Modifier.size(48.dp) // Set the size of the button
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_edit_availibility),
                        contentDescription = "Edit Availability",
                        tint = Color(0xFFE8468E),
                        modifier = Modifier.size(24.dp) // Set the size of the icon inside the button
                    )
                }
            }
        }

        // Inside MyHoursPage Composable
        if (showModal) {
            AlertDialog(
                onDismissRequest = { showModal = false },
                title = {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center // Center the title
                    ) {
                        Text(
                            text = "Change Availability",
                            fontSize = 20.sp,
                            color = Color.Black,
                            fontFamily = FontFamily(Font(R.font.mindset))
                        )
                    }
                },
                text = {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        var selectedOption by remember { mutableStateOf(0) }
                        val options = listOf("Available Entire Day", "Unavailable Entire Day", "Available From")

                        options.forEachIndexed { index, option ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp)
                                    .clickable { selectedOption = index }
                            ) {
                                Text(
                                    text = option,
                                    fontSize = 16.sp,
                                    color = Color.Black,
                                    modifier = Modifier.weight(1f) // Text takes full width
                                )
                                RadioButton(
                                    selected = selectedOption == index,
                                    onClick = { selectedOption = index },
                                    colors = RadioButtonDefaults.colors(
                                        selectedColor = Color(0xFFE8468E),
                                        unselectedColor = Color.Gray
                                    ),
                                    modifier = Modifier.padding(start = 8.dp) // Positioned on the right
                                )
                            }
                        }

                        if (selectedOption == 2) {
                            // Time Range Picker
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                            ) {
                                OutlinedTextField(
                                    value = "07:00",
                                    onValueChange = {}, // Replace with logic to update start time
                                    modifier = Modifier.weight(1f),
                                    textStyle = TextStyle(fontSize = 14.sp),
                                    colors = TextFieldDefaults.outlinedTextFieldColors(
                                        focusedBorderColor = Color(0xFFE8468E),
                                        unfocusedBorderColor = Color.Gray
                                    )
                                )
                                Text(
                                    text = "-",
                                    fontSize = 16.sp,
                                    color = Color.Black
                                )
                                OutlinedTextField(
                                    value = "15:00",
                                    onValueChange = {}, // Replace with logic to update end time
                                    modifier = Modifier.weight(1f),
                                    textStyle = TextStyle(fontSize = 14.sp),
                                    colors = TextFieldDefaults.outlinedTextFieldColors(
                                        focusedBorderColor = Color(0xFFE8468E),
                                        unfocusedBorderColor = Color.Gray
                                    )
                                )
                            }
                        }
                    }
                },
                buttons = {
                    Row(
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp)
                    ) {
                        Button(
                            onClick = { showModal = false },
                            colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFE8468E)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.padding(horizontal = 8.dp)
                        ) {
                            Text(
                                text = "Cancel",
                                fontSize = 16.sp,
                                fontFamily = FontFamily(Font(R.font.mindset)), // Mindset font
                                color = Color.White
                            )
                        }
                        Button(
                            onClick = {
                                // Save the availability changes logic here
                                showModal = false
                            },
                            colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFE8468E)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.padding(horizontal = 8.dp)
                        ) {
                            Text(
                                text = "Save",
                                fontSize = 16.sp,
                                fontFamily = FontFamily(Font(R.font.mindset)), // Mindset font
                                color = Color.White
                            )
                        }
                    }
                },
                shape = RoundedCornerShape(16.dp),
                backgroundColor = Color.White,
                modifier = Modifier
                    .fillMaxWidth(0.95f) // Adjust width to be as wide as possible
                    .padding(24.dp) // Add padding around the dialog
            )
        }

    }
}