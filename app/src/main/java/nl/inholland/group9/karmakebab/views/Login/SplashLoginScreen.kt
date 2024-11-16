package nl.inholland.group9.karmakebab.views

import androidx.compose.animation.*
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import nl.inholland.group9.karmakebab.R

val MindsetFontFamily = FontFamily(
    Font(R.font.mindset) // Use your custom font file here
)

@Composable
fun SplashLoginScreen() {
    var startAnimation by remember { mutableStateOf(false) }
    var showLoginUI by remember { mutableStateOf(false) }

    // Animate logo position
    val logoPosition by animateDpAsState(
        targetValue = if (startAnimation) 80.dp else 300.dp,
        animationSpec = tween(durationMillis = 1000)
    )

    LaunchedEffect(Unit) {
        startAnimation = true
        delay(1000) // Wait for logo animation
        showLoginUI = true // Show login UI immediately after logo animates
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF5B2D86)), // Purple background
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(logoPosition)) // Animated position
            Image(
                painter = painterResource(id = R.drawable.kermakebab_logo),
                contentDescription = "Karma Kebab Logo",
                modifier = Modifier.size(150.dp)
            )

            // Login UI (Email, Password, Button)
            AnimatedVisibility(
                visible = showLoginUI,
                enter = fadeIn(animationSpec = tween(800)),
                exit = fadeOut()
            ) {
                LoginUI()
            }
        }
    }
}

@Composable
fun LoginUI() {
    // Define common TextField colors for reuse
    val textFieldColors = TextFieldDefaults.outlinedTextFieldColors(
        focusedBorderColor = Color(0xFFDA0175), // Pink
        unfocusedBorderColor = Color.Gray,
        textColor = Color.White,
        cursorColor = Color.White
    )

    // State variables for email and password
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start // Align all items to the start
    ) {
        // Welcome Message
        Text(
            text = "Hello There, Login to Continue",
            color = Color.White,
            fontSize = 18.sp,
            style = TextStyle(fontFamily = MindsetFontFamily), // Use custom font
            textAlign = TextAlign.Start, // Align text to start
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Email Text Field
        CustomOutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = "Email Address",
            placeholder = "Enter your email",
            colors = textFieldColors,
            font = MindsetFontFamily // Pass custom font to TextField
        )

        // Password Text Field
        CustomOutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = "Password",
            placeholder = "Enter your password",
            colors = textFieldColors,
            font = MindsetFontFamily, // Pass custom font to TextField
            visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                    Icon(
                        painter = painterResource(
                            id = if (isPasswordVisible) R.drawable.ic_eye else R.drawable.ic_eye
                        ),
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp) // Larger icon size
                    )
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Login Button
        Button(
            onClick = { /* Handle login */ },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFDA0175)), // Pink
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                "LOGIN",
                color = Color.White,
                fontSize = 16.sp,
                style = TextStyle(fontFamily = MindsetFontFamily) // Use custom font
            )
        }
    }
}

// Reusable OutlinedTextField Composable
@Composable
fun CustomOutlinedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    colors: TextFieldColors,
    font: androidx.compose.ui.text.font.FontFamily,
    modifier: Modifier = Modifier,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    trailingIcon: @Composable (() -> Unit)? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        label = { Text(label, color = Color.White, style = TextStyle(fontFamily = font)) },
        placeholder = { Text(placeholder, color = Color.Gray, style = TextStyle(fontFamily = font)) },
        colors = colors,
        shape = RoundedCornerShape(8.dp), // Rounded corners
        visualTransformation = visualTransformation,
        trailingIcon = trailingIcon
    )
}
