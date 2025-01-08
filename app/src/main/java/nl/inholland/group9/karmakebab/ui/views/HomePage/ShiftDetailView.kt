package nl.inholland.group9.karmakebab.ui.views.HomePage

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import nl.inholland.group9.karmakebab.R
import nl.inholland.group9.karmakebab.data.models.shift.Shift

@Composable
fun ShiftDetailView(navController: NavController, shift: Shift) {
    val colbyRegFont = FontFamily(Font(R.font.colby_streg))
    val colbyMedFont = FontFamily(Font(R.font.colby_stmed))
    val mindset = FontFamily(Font(R.font.mindset))

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7F7F7)) // Updated background color
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
                    modifier = Modifier.size(56.dp) // Bigger button size
                ) {
                    androidx.compose.material.Icon(
                        painter = painterResource(id = R.drawable.ic_back),
                        contentDescription = "Back",
                        tint = Color(0xFFE8468E), // Updated color
                        modifier = Modifier.size(30.dp) // Icon size
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Home",
                    fontFamily = colbyRegFont,
                    fontSize = 20.sp, // Larger font for emphasis
                    color = Color(0xFFE8468E) // Updated color
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Title and Date
            Text(
                text = "OZ FESTIVAL",
                fontFamily = mindset,
                fontSize = 26.sp, // Larger bold title
                color = Color.Black,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                text = "Tuesday, October 30",
                fontFamily = colbyRegFont,
                fontSize = 18.sp, // Smaller date text
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Shift Details
            Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                DetailItem(
                    header = "Location",
                    value = "Prinses 202, Utrecht 2067LM",
                    headerFont = colbyMedFont,
                    valueFont = colbyRegFont
                )
                DetailItem(
                    header = "Scheduled For",
                    value = "07:00 - 15:00",
                    headerFont = colbyMedFont,
                    valueFont = colbyRegFont
                )
                DetailItem(
                    header = "Role",
                    value =   "Unknown" ,
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
                    value = "Unknown",
                    headerFont = colbyMedFont,
                    valueFont = colbyRegFont
                )
                AdditionalInformationItem(
                    text = "Attention, bring extra something because someone, will need something somewhere",
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