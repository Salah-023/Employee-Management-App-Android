package nl.inholland.group9.karmakebab.ui.views.Profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import nl.inholland.group9.karmakebab.R
import nl.inholland.group9.karmakebab.ui.viewmodels.Header.HeaderViewModel

@Composable
fun ProfilePage(viewModel: HeaderViewModel = hiltViewModel(),
                onLogout: () -> Unit) {
    // Collect user data from the ViewModel
    val userInfo by viewModel.userInfo.collectAsState()
    val initials by viewModel.initials.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F8F8))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Profile Icon with Initials
        Box(
            modifier = Modifier
                .size(120.dp)
                .background(Color(0xFFF29FA8), shape = CircleShape), // Background color
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = initials,
                color = Color(0xFF662C83), // Initials text color
                fontSize = 40.sp,
                style = TextStyle(
                    fontFamily = androidx.compose.ui.text.font.FontFamily(
                        androidx.compose.ui.text.font.Font(R.font.mindset)
                    )
                )
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // User Name
        Text(
            text = userInfo?.fullName ?: "Unknown User",
            fontSize = 24.sp,
            color = Color.Black,
            style = TextStyle(fontFamily = androidx.compose.ui.text.font.FontFamily(
                androidx.compose.ui.text.font.Font(R.font.mindset)
            ))
        )

        Spacer(modifier = Modifier.height(32.dp)) // Added more space below the username

        // Email Card
        ProfileInfoCard(
            icon = R.drawable.ic_email,
            text = userInfo?.email ?: "Unknown User"
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Phone Number Card
        ProfileInfoCard(
            icon = R.drawable.ic_phone,
            text = userInfo?.phone ?: "Unknown Phone Number"
        )

        Spacer(modifier = Modifier.weight(1f)) // Pushes logout button to the bottom

        // Logout Button
        Button(
            onClick = {
                viewModel.logout() // Call the logout function in the ViewModel
                onLogout() // Trigger navigation to login
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .padding(bottom = 16.dp), // Bottom padding to avoid touching navbar
            colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFE8468E)), // Pink button
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = "LOGOUT",
                fontSize = 16.sp,
                color = Color.White
            )
        }
    }
}

@Composable
fun ProfileInfoCard(icon: Int, text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, shape = RoundedCornerShape(8.dp))
            .padding(top = 8.dp, start = 16.dp, end = 16.dp, bottom = 16.dp), // Added top padding
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(50.dp) // Icon size
                .background(Color(0xFFF29FA8), shape = CircleShape), // Background color
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = null,
                tint = Color(0xFF662C83), // Icon color
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = text,
            fontSize = 16.sp,
            color = Color.Black
        )
    }
}