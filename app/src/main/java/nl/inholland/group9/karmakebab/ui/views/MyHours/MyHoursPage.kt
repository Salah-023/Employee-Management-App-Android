package nl.inholland.group9.karmakebab.ui.views.MyHours


import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import nl.inholland.group9.karmakebab.ui.viewmodels.Myhours.AvailabilityViewModel
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.window.Dialog
import nl.inholland.group9.karmakebab.R
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


@Composable
fun MyHoursPage(viewModel: AvailabilityViewModel = hiltViewModel()) {
    val daysInGrid by viewModel.daysInGrid.collectAsState()
    val availabilityData by viewModel.availabilityData.collectAsState()
    val selectedDate by viewModel.selectedDate.collectAsState()
    val currentMonth by viewModel.currentMonth.collectAsState()
    val currentYear by viewModel.currentYear.collectAsState()

    val isModalVisible = remember { mutableStateOf(false) }
    val selectedOption = remember { mutableStateOf("Available Entire Day") }
    val startTime = remember { mutableStateOf("") }
    val endTime = remember { mutableStateOf("") }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Month Navigation
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { viewModel.previousMonth() }) { // Navigate to the previous month
                Icon(
                    painter = painterResource(R.drawable.ic_back_calendar),
                    contentDescription = "Previous Month",
                    tint = Color(0xFFE8468E),
                    modifier = Modifier.size(24.dp)
                )
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = currentMonth,
                    fontSize = 18.sp,
                    color = Color.Black,
                    style = MaterialTheme.typography.h6
                )
                Text(
                    text = currentYear.toString(),
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
            IconButton(onClick = { viewModel.nextMonth() }) { // Navigate to the next month
                Icon(
                    painter = painterResource(R.drawable.ic_next_calendar),
                    contentDescription = "Next Month",
                    tint = Color(0xFFE8468E),
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        // Weekday Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun").forEach {
                Text(
                    text = it,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center,
                    color = Color.Gray,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Calendar Grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            items(daysInGrid.size) { index ->
                val date = daysInGrid[index]
                val normalizedDate = date?.let { viewModel.normalizeDate(it) }
                Log.d(
                    "MyHoursPage",
                    "Day: $normalizedDate, Status: ${availabilityData[normalizedDate]?.status}"
                )

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .padding(vertical = 4.dp)
                        .size(48.dp)
                        .clickable { // Add clickable modifier here
                            date?.let {
                                val normalized = viewModel.normalizeDate(it)
                                viewModel.selectedDate.value = normalized
                                Log.d("MyHoursPage", "Selected Date Updated: $normalized")
                            }
                        }
                ) {
                    // Day Number
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(36.dp)
                            .background(
                                if (normalizedDate == viewModel.normalizeDate(selectedDate)) {
                                    Color(0xFFE8468E) // Selected date circle color
                                } else {
                                    Color.Transparent
                                },
                                shape = CircleShape
                            )
                    ) {
                        date?.let {
                            Text(
                                text = SimpleDateFormat("d", Locale.getDefault()).format(it),
                                color = if (normalizedDate == viewModel.normalizeDate(selectedDate)) {
                                    Color.White
                                } else {
                                    Color.Black
                                },
                                fontSize = 14.sp
                            )
                        }
                    }


                    // Availability Dot Below Day Number
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(
                                when (availabilityData[normalizedDate]?.status) {
                                    "unavailable" -> Color.Red
                                    "partial" -> Color.Yellow
                                    else -> Color.Green
                                },
                                shape = CircleShape
                            )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Selected Day Availability
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFF7F7F7), RoundedCornerShape(8.dp))
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .background(
                            when (availabilityData[selectedDate]?.status) {
                                "unavailable" -> Color.Red
                                "partial" -> Color.Yellow
                                else -> Color.Green
                            },
                            shape = CircleShape
                        )
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "${
                        SimpleDateFormat("EEEE, d MMMM", Locale.getDefault()).format(
                            selectedDate
                        )
                    } - " +
                            when (availabilityData[selectedDate]?.status) {
                                "unavailable" -> "Unavailable Entire Day"
                                "partial" -> "Partially Available\n${availabilityData[selectedDate]?.startTime} - ${availabilityData[selectedDate]?.endTime}"
                                else -> "Available Entire Day"
                            },
                    fontSize = 14.sp,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.weight(1f))
                IconButton(onClick = { isModalVisible.value = true }) {
                    Icon(
                        painter = painterResource(R.drawable.ic_edit_availibility),
                        contentDescription = "Edit Availability",
                        tint = Color(0xFFE8468E),
                        modifier = Modifier.size(25.dp)
                    )
                }
            }
        }

        if (isModalVisible.value) {
            AvailabilityModal(
                isShowingModal = isModalVisible,
                selectedOption = selectedOption,
                selectedDate= selectedDate,
                startTime = startTime,
                endTime = endTime
            ) { option, start, end ->
                Log.d("MyHoursPage", "Modal Save triggered with option: $option, startTime: $start, endTime: $end")

                if (option == "Available Entire Day") {
                    viewModel.deleteAvailability()
                } else if (option == "Unavailable Until") {
                    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    val parsedEndDate = dateFormat.parse(end!!)
                    viewModel.saveAvailabilityForDateRange(
                        startDate = viewModel.selectedDate.value,
                        endDate = parsedEndDate!!,
                        status = "unavailable"
                    )
                } else {
                    viewModel.saveAvailability(option, start, end)
                }
            }

        }
    }
}

@Composable
fun AvailabilityModal(
    isShowingModal: MutableState<Boolean>,
    selectedOption: MutableState<String>,
    startTime: MutableState<String>,
    endTime: MutableState<String>,
    selectedDate: Date,
    onSave: (String, String?, String?) -> Unit
) {
    val options = listOf("Available Entire Day", "Unavailable Entire Day", "Partially Available", "Unavailable Until")
    val errorMessage = remember { mutableStateOf<String?>(null) }
    val timeFormatRegex = Regex("^([01]?\\d|2[0-3]):[0-5]\\d$") // Validates HH:mm format

    val today = Calendar.getInstance()
    val restrictedDate = Calendar.getInstance().apply { add(Calendar.DAY_OF_MONTH, 14) }
    val isRestrictedDate = selectedDate.before(today.time) || selectedDate.before(restrictedDate.time)

    Dialog(onDismissRequest = { isShowingModal.value = false }) {
        Surface(
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.padding(16.dp),
            color = Color.White
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "CHANGE AVAILABILITY",
                    style = MaterialTheme.typography.h6,
                    color = Color.Black,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                options.forEach { option ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedOption.value = option }
                            .padding(vertical = 8.dp)
                    ) {
                        RadioButton(
                            selected = selectedOption.value == option,
                            onClick = { selectedOption.value = option },
                            colors = RadioButtonDefaults.colors(selectedColor = Color(0xFFE8468E))
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = option, color = Color.Black)
                    }
                }

                if (selectedOption.value == "Partially Available") {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp)
                    ) {
                        Text(text = "From:", color = Color.Black)
                        Spacer(modifier = Modifier.width(8.dp))
                        TextField(
                            value = startTime.value,
                            onValueChange = { startTime.value = it; errorMessage.value = null },
                            singleLine = true,
                            placeholder = { Text(text = "HH:mm") },
                            isError = errorMessage.value != null,
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "To:", color = Color.Black)
                        Spacer(modifier = Modifier.width(8.dp))
                        TextField(
                            value = endTime.value,
                            onValueChange = { endTime.value = it; errorMessage.value = null },
                            singleLine = true,
                            placeholder = { Text(text = "HH:mm") },
                            isError = errorMessage.value != null,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }else if (selectedOption.value == "Unavailable Until") {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp)
                    ) {
                        Text(text = "End Date:", color = Color.Black)
                        Spacer(modifier = Modifier.width(8.dp))
                        TextField(
                            value = endTime.value,
                            onValueChange = { endTime.value = it; errorMessage.value = null },
                            singleLine = true,
                            placeholder = { Text(text = "YYYY-MM-DD") },
                            isError = errorMessage.value != null,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                if (isRestrictedDate) {
                    Text(
                        text = "You cannot change availability for dates within 14 days from today.",
                        color = Color.Red,
                        modifier = Modifier.padding(top = 16.dp)
                    )
                }

                errorMessage.value?.let {
                    Text(
                        text = it,
                        color = Color.Red,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }


                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        if (selectedOption.value == "Partially Available" &&
                            (!timeFormatRegex.matches(startTime.value) || !timeFormatRegex.matches(endTime.value))
                        ) {
                            errorMessage.value = "Time format must be HH:mm"
                        } else if (selectedOption.value == "Unavailable Until") {
                            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                            try {
                                val parsedEndDate = dateFormat.parse(endTime.value)
                                if (parsedEndDate != null && parsedEndDate.after(Date())) {
                                    errorMessage.value = null
                                    onSave(
                                        selectedOption.value,
                                        null,
                                        dateFormat.format(parsedEndDate) // Pass the parsed end date as a formatted string
                                    )
                                    isShowingModal.value = false
                                } else {
                                    errorMessage.value = "End date must be in the future and valid (YYYY-MM-DD)"
                                }
                            } catch (e: Exception) {
                                errorMessage.value = "Invalid end date format (YYYY-MM-DD)"
                            }
                        } else {
                            errorMessage.value = null
                            onSave(
                                selectedOption.value,
                                if (selectedOption.value == "Partially Available") startTime.value else null,
                                if (selectedOption.value == "Partially Available") endTime.value else null
                            )
                            isShowingModal.value = false
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFE8468E)),
                    enabled = !isRestrictedDate
                ) {
                    Text(text = "SAVE", color = Color.White)
                }
            }
        }
    }
}

