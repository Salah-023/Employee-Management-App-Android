package nl.inholland.group9.karmakebab.views

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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import nl.inholland.group9.karmakebab.R

@Composable
fun BottomNavigationBar(
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit,
    profileInitials: String // Dynamically passed initials
) {
    val items = listOf("Home", "Calendar", "My Hours", "Profile")
    val icons = listOf(
        R.drawable.ic_home,
        R.drawable.ic_calendar,
        R.drawable.ic_timer,
        null // Placeholder for Profile since it's a dynamic icon
    )

    val mindsetFont = androidx.compose.ui.text.font.FontFamily(
        androidx.compose.ui.text.font.Font(R.font.mindset)
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .background(Color(0xFFF8F8F8)),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        items.forEachIndexed { index, label ->
            Column(
                modifier = Modifier
                    .clickable { onItemSelected(index) }
                    .padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Icon or Profile Initials Box
                if (icons[index] != null) {
                    Box(
                        modifier = Modifier
                            .width(56.dp)
                            .height(36.dp)
                            .background(
                                if (selectedIndex == index) Color(0xFFE8468E) else Color.Transparent,
                                shape = RoundedCornerShape(20.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = icons[index]!!),
                            contentDescription = label,
                            modifier = Modifier.size(24.dp),
                            tint = Color(0xFF445668)
                        )
                    }
                } else {
                    // Dynamic Profile Icon with Initials
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(
                                if (selectedIndex == index) Color(0xFFE8468E) else Color(0xFF445668),
                                shape = RoundedCornerShape(18.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = profileInitials,
                            color = Color.White,
                            fontSize = 14.sp,
                            style = TextStyle(fontFamily = mindsetFont)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Label with larger text
                Text(
                    text = label,
                    style = TextStyle(fontFamily = mindsetFont),
                    fontSize = 14.sp,
                    color = if (selectedIndex == index) Color(0xFFE8468E) else Color(0xFF445668)
                )
            }
        }
    }
}




