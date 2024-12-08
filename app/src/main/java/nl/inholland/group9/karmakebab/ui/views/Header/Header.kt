package nl.inholland.group9.karmakebab.ui.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import nl.inholland.group9.karmakebab.R
import nl.inholland.group9.karmakebab.ui.viewmodels.HeaderViewModel

@Composable
fun HomeHeader(viewModel: HeaderViewModel = hiltViewModel()) {
    val greeting by viewModel.greeting.collectAsState()
    val formattedDate by viewModel.formattedDate.collectAsState()
    val userInfo by viewModel.userInfo.collectAsState()
    val initials by viewModel.initials.collectAsState()

    val mindsetFont = androidx.compose.ui.text.font.FontFamily(
        androidx.compose.ui.text.font.Font(R.font.mindset)
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp)) // Rounded bottom corners
            .background(Color(0xFF5B2D86)) // Purple background
            .padding(16.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.Start,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Dynamic Greeting
            Text(
                text = greeting,
                color = Color.White,
                fontSize = 18.sp,
                style = TextStyle(fontFamily = mindsetFont)
            )
            // User Name
            Text(
                text = userInfo?.name ?: "Loading...",
                color = Color.White,
                fontSize = 24.sp,
                style = TextStyle(fontFamily = mindsetFont)
            )
            Spacer(modifier = Modifier.height(4.dp))

            // Dynamic Date
            Text(
                text = formattedDate,
                color = Color.White,
                fontSize = 14.sp,
                style = TextStyle(fontFamily = mindsetFont)
            )
        }

        // Profile Icon with Initials
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(Color(0xFFF29FA8), shape = CircleShape) // Updated background color
                .align(Alignment.TopEnd)
        ) {
            Text(
                text = initials,
                color = Color.White,
                fontSize = 16.sp,
                modifier = Modifier.align(Alignment.Center),
                style = TextStyle(fontFamily = mindsetFont)
            )
        }
    }
}


