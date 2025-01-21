package nl.inholland.group9.karmakebab.ui.views.HomePage

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import nl.inholland.group9.karmakebab.R
import nl.inholland.group9.karmakebab.data.models.shift.AssignedUser
import nl.inholland.group9.karmakebab.data.models.shift.Shift
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun ShiftDetailView(navController: NavController, shift: Shift, userRole: String) {
    val colbyRegFont = FontFamily(Font(R.font.colby_streg))
    val colbyMedFont = FontFamily(Font(R.font.colby_stmed))
    val mindset = FontFamily(Font(R.font.mindset))

    // Format the date and time
    val formattedDate = shift.startTime?.let { timestamp ->
        SimpleDateFormat("EEEE, MMMM dd", Locale.getDefault()).format(timestamp.toDate())
    } ?: "Unknown Date"

    val formattedTime = shift.startTime?.let { start ->
        val startFormatted = SimpleDateFormat("HH:mm", Locale.getDefault()).format(start.toDate())
        val endFormatted = shift.endTime?.let { end ->
            SimpleDateFormat("HH:mm", Locale.getDefault()).format(end.toDate())
        }
        "$startFormatted - $endFormatted"
    } ?: "Unknown Time"

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7F7F7))
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.Start,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Back Button and Home Text
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.size(56.dp)
                ) {
                    androidx.compose.material.Icon(
                        painter = painterResource(id = R.drawable.ic_back),
                        contentDescription = "Back",
                        tint = Color(0xFFE8468E),
                        modifier = Modifier.size(30.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Home",
                    fontFamily = colbyRegFont,
                    fontSize = 20.sp,
                    color = Color(0xFFE8468E)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Title and Date
            Text(
                text = shift.event?.venue ?: "Unknown Event",
                fontFamily = mindset,
                fontSize = 26.sp,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                text = formattedDate, // Dynamically formatted date
                fontFamily = colbyRegFont,
                fontSize = 18.sp,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Shift Details
            Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                DetailItem(
                    header = "Location",
                    value = shift.event?.address ?: "Unknown Location",
                    headerFont = colbyMedFont,
                    valueFont = colbyRegFont
                )
                DetailItem(
                    header = "Scheduled For",
                    value = formattedTime, // Dynamically formatted time range
                    headerFont = colbyMedFont,
                    valueFont = colbyRegFont
                )
                DetailItem(
                    header = "Role",
                    value = userRole, // Dynamic role
                    headerFont = colbyMedFont,
                    valueFont = colbyRegFont
                )
                DetailItem(
                    header = "Break",
                    value = "30min",
                    headerFont = colbyMedFont,
                    valueFont = colbyRegFont
                )
                DetailItem(
                    header = "Number of Employees",
                    value = shift.assignedUsers.size.toString(),
                    headerFont = colbyMedFont,
                    valueFont = colbyRegFont
                )
                AdditionalInformationItem(
                    text = shift.event?.note ?: "No additional information available.",
                    headerFont = colbyMedFont,
                    valueFont = colbyRegFont
                )
            }

        }
    }
}

@Composable
fun DetailItem(header: String, value: String, headerFont: FontFamily, valueFont: FontFamily) {
    Column {
        Text(
            text = header,
            style = TextStyle(
                fontFamily = headerFont,
                fontSize = 18.sp, // Increased header font size
                color = Color(0xFF445668) // Updated header color
            )
        )
        Text(
            text = value,
            style = TextStyle(
                fontFamily = valueFont,
                fontSize = 20.sp, // Increased value font size
                color = Color(0xFF445668) // Updated value color
            ),
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Composable
fun AdditionalInformationItem(text: String, headerFont: FontFamily, valueFont: FontFamily) {
    Column {
        Text(
            text = "Additional Information",
            style = TextStyle(
                fontFamily = headerFont,
                fontSize = 18.sp,
                color = Color(0xFF445668)
            )
        )
        Spacer(modifier = Modifier.height(8.dp))
        Card(
            backgroundColor = Color.White,
            shape = RoundedCornerShape(12.dp),
            elevation = 4.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = text,
                style = TextStyle(
                    fontFamily = valueFont,
                    fontSize = 18.sp, // Adjusted font size for readability
                    color = Color(0xFF445668)
                ),
                modifier = Modifier.padding(12.dp)
            )
        }
    }
}

