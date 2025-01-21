package nl.inholland.group9.karmakebab.ui.views.HomePage

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import nl.inholland.group9.karmakebab.R
import nl.inholland.group9.karmakebab.data.models.shift.AssignedUser
import nl.inholland.group9.karmakebab.data.models.shift.Shift
import nl.inholland.group9.karmakebab.data.models.shift.Task
import nl.inholland.group9.karmakebab.ui.viewmodels.HomePage.TaskViewModel
import java.text.SimpleDateFormat
import java.util.Locale


@Composable
fun TaskPageView(
    shift: Shift,
    role: String,
    navController: NavController,
    viewModel: TaskViewModel = hiltViewModel()
) {
    val tasks by viewModel.tasks.collectAsState()
    val progress by viewModel.progress.collectAsState()
    val assignedUsers = remember { mutableStateOf<List<AssignedUser>>(emptyList()) }

    LaunchedEffect(Unit) {
        shift.id?.let { viewModel.loadTasksForShift(it, role) }

        if (role == "Head Trucker") {
            assignedUsers.value = shift.id?.let { viewModel.fetchAssignedUsers(it) }!!
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Back Button
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_back),
                    contentDescription = "Back",
                    tint = Color(0xFFE8468E),
                    modifier = Modifier
                        .size(32.dp) // Increased size
                        .clickable { navController.popBackStack() }
                        .padding(8.dp)
                )
            }
        }

        // Event Header Section
        item {
            TaskEventHeader(shift = shift, role)
        }

        // Task List Header
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            ) {
                Text(
                    text = "TASK LIST",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontFamily = FontFamily(Font(R.font.mindset))
                    ),
                    color = Color.Black
                )

                // Circular Progress and Completion Text
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.size(48.dp)
                    ) {
                        androidx.compose.material3.CircularProgressIndicator(
                            progress = progress.toFloat(),
                            color = Color(0xFFE8468E),
                            strokeWidth = 4.dp
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${(progress * tasks.size).toInt()}/${tasks.size} Complete",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontFamily = FontFamily(Font(R.font.colby_stmed))
                        ),
                        color = Color.Gray
                    )
                }
            }
        }

        // Tasks Section
        items(tasks) { task ->
            TaskItem(
                task = task,
                onMarkDone = {
                    shift.id?.let { viewModel.markTaskAsDone(it, task.id) }
                },
                onImageCaptured = { imagePath ->
                    shift.id?.let { viewModel.handleCapturedImage(task.id, it, imagePath) }
                }
            )
        }

        // Spacer between Tasks and Employee List
        if (role == "Head Trucker") {
            item { Spacer(modifier = Modifier.height(16.dp)) }

            // Employees Section Header
            item {
                Text(
                    text = "Employees",
                    style = TextStyle(
                        fontSize = 20.sp,
                        color = Color(0xFF445668),
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily(Font(R.font.colby_stmed))
                    ),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            // Employee Cards
            items(assignedUsers.value) { user ->
                EmployeeCard(user = user, loggedInUserId = viewModel.authRepository.getCurrentUser()?.uid ?: "")
            }
        }
    }
}

@Composable
fun EmployeeCard(user: AssignedUser, loggedInUserId: String) {
    val isLoggedInUser = user.userId == loggedInUserId
    val displayName = if (isLoggedInUser) "Me" else user.fullName ?: "Unknown Name"

    // Generate initials based on the actual full name
    val initials = user.fullName?.split(" ")
        ?.joinToString("") { it.firstOrNull()?.toString()?.uppercase() ?: "" }
        ?: ""

    val backgroundColor = remember(user.userId) {
        // Generate a unique color based on the user's ID
        Color(android.graphics.Color.HSVToColor(
            floatArrayOf((user.userId.hashCode() % 360).toFloat(), 0.5f, 0.85f)
        ))
    }

    Card(
        backgroundColor = Color.White,
        shape = RoundedCornerShape(8.dp),
        elevation = 2.dp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Profile Icon with initials and unique color
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(backgroundColor, shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = initials,
                    style = TextStyle(fontSize = 16.sp, color = Color.White)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(
                modifier = Modifier.weight(1f)
            ) {
                // Simplified logic: Display "Me" for logged-in user
                Text(
                    text = displayName,
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily(Font(R.font.colby_stmed)),
                        color = Color.Black
                    )
                )
                Text(
                    text = user.role,
                    style = TextStyle(
                        fontSize = 14.sp,
                        fontFamily = FontFamily(Font(R.font.colby_streg)),
                        color = Color.Gray
                    )
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (user.clockInTime != null)
                    "Clocked-in at ${SimpleDateFormat("HH:mm", Locale.getDefault()).format(user.clockInTime!!.toDate())}"
                else
                    "Not Clocked In",
                style = TextStyle(
                    fontSize = 14.sp,
                    fontFamily = FontFamily(Font(R.font.colby_streg)),
                    color = if (user.clockInTime != null) Color(0xFF4CAF50) else Color(0xFFF44336)
                )
            )
        }
    }
}





@Composable
fun TaskItem(
    task: Task,
    onMarkDone: () -> Unit,
    onImageCaptured: (String) -> Unit // Callback when the image is captured
) {
    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
        if (bitmap != null) {
            // Handle the captured image and mark the task as done
            onImageCaptured("ImageCapturedPath") // Replace with the actual path if saving locally
        }
    }

    Row(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable {
                // For tasks requiring an image, launch the camera
                if (task.requiresImage) {
                    cameraLauncher.launch(null)
                } else if (!task.isDone) {
                    // For tasks not requiring an image, mark as done
                    onMarkDone()
                }
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Custom Circle Checkbox
        Icon(
            painter = painterResource(
                id = if (task.isDone) R.drawable.ic_checked_circle else R.drawable.ic_check_circle
            ),
            contentDescription = null,
            tint = Color(0xFFE8468E),
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        // Task Details
        Box(
            modifier = Modifier
                .weight(1f)
                .background(if (task.isDone) Color(0xFFF2F2F2) else Color.White)
                .padding(16.dp)
        ) {
            Text(
                text = task.title,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontFamily = FontFamily(Font(R.font.colby_stmed))
                ),
                color = if (task.isDone) Color.Gray else Color.Black,
                textDecoration = if (task.isDone) TextDecoration.LineThrough else null
            )
        }

        // Camera Icon for Tasks Requiring a Picture
        if (task.requiresImage) {
            Icon(
                painter = painterResource(id = R.drawable.ic_camera),
                contentDescription = "Take Picture",
                tint = Color(0xFFE8468E),
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
fun TaskEventHeader(shift: Shift, role: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Event Title
        Text(
            text = shift.event?.venue ?: "Unknown Event",
            fontFamily = FontFamily(Font(R.font.mindset)),
            fontSize = 28.sp,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Event Location
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 8.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_location),
                contentDescription = "Location",
                tint = Color.Gray,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = shift.event?.address ?: "Unknown Address",
                fontFamily = FontFamily(Font(R.font.colby_streg)),
                fontSize = 16.sp,
                color = Color.Gray
            )
        }

        // Event Time and Role
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp)
        ) {
            // Time
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_clock),
                    contentDescription = "Time",
                    tint = Color.Gray,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = shift.startTime?.toDate()?.let { start ->
                        shift.endTime?.toDate()?.let { end ->
                            "${SimpleDateFormat("HH:mm", Locale.getDefault()).format(start)} - ${SimpleDateFormat("HH:mm", Locale.getDefault()).format(end)}"
                        }
                    } ?: "Unknown Time",
                    fontFamily = FontFamily(Font(R.font.colby_streg)),
                    fontSize = 16.sp,
                    color = Color.Gray
                )
            }

            // Role
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_role),
                    contentDescription = "Role",
                    tint = Color.Gray,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = role,
                    fontFamily = FontFamily(Font(R.font.colby_streg)),
                    fontSize = 16.sp,
                    color = Color.Gray
                )
            }
        }
    }
}





