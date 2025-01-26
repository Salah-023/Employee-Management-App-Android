package nl.inholland.group9.karmakebab.data.repositories

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import nl.inholland.group9.karmakebab.data.models.shift.AssignedUser
import nl.inholland.group9.karmakebab.data.models.shift.Task
import javax.inject.Inject

class TaskRepository @Inject constructor(
    val firestore: FirebaseFirestore,
    val authRepository: AuthRepository,
    val shiftRepository: ShiftsRepository
) {

    suspend fun fetchTasksForRole(role: String): List<Task> {
        return try {
            val result = firestore.collection("roles")
                .whereEqualTo("title", role)
                .get()
                .await()
            val tasks = result.documents.firstOrNull()?.get("tasks") as? List<Map<String, Any>> ?: emptyList()
            tasks.mapNotNull { Task.fromMap(it) }
        } catch (e: Exception) {
            Log.e("TaskRepository", "Failed to fetch tasks: ${e.localizedMessage}")
            emptyList()
        }
    }


    suspend fun markTaskAsDone(shiftId: String, userId: String, taskId: Int) {
        try {
            val document = firestore.collection("shifts").document(shiftId).get().await()
            val assignedUsers = document["assignedUsers"] as? List<Map<String, Any>> ?: return
            val updatedUsers = assignedUsers.map {
                if (it["userId"] == userId) {
                    val completedTasks = (it["completedTasks"] as? List<Int> ?: emptyList()).toMutableList()
                    if (!completedTasks.contains(taskId)) {
                        completedTasks.add(taskId)
                    }
                    it + ("completedTasks" to completedTasks)
                } else {
                    it
                }
            }
            firestore.collection("shifts").document(shiftId).update("assignedUsers", updatedUsers).await()
        } catch (e: Exception) {
            Log.e("TaskRepository", "Failed to mark task as done: ${e.localizedMessage}")
        }
    }

    suspend fun uploadImage(shiftId: String, taskId: Int, imagePath: String) {
        // Logic to upload the image to cloud storage or database
        val shiftDocument = firestore.collection("shifts").document(shiftId)
        val snapshot = shiftDocument.get().await()

        val assignedUsers = snapshot["assignedUsers"] as? List<Map<String, Any>> ?: emptyList()
        val updatedUsers = assignedUsers.map { user ->
            if (user["userId"] == authRepository.getCurrentUser()?.uid) {
                val completedTasks = (user["completedTasks"] as? List<Int>)?.toMutableList() ?: mutableListOf()
                completedTasks.add(taskId)
                user + ("completedTasks" to completedTasks)
            } else {
                user
            }
        }
        // Update the shift document with the new completed task and image path
        shiftDocument.update("assignedUsers", updatedUsers).await()
    }

    suspend fun fetchCompletedTasks(shiftId: String, userId: String): List<Int> {
        return try {
            // Fetch the shift document from Firestore
            val shiftDocument = firestore.collection("shifts").document(shiftId).get().await()

            // Extract assignedUsers and log it
            val assignedUsers = shiftDocument["assignedUsers"] as? List<Map<String, Any>> ?: emptyList()

            // Find the current user's entry
            val user = assignedUsers.find { it["userId"] == userId }

            // Return the list of completed task IDs and log it
            val completedTasks = user?.get("completedTasks") as? List<Int> ?: emptyList()

            completedTasks
        } catch (e: Exception) {
            Log.e("TaskRepository", "Error fetching completed tasks: ${e.localizedMessage}")
            emptyList()
        }
    }

    suspend fun fetchAssignedUsers(shiftId: String): List<AssignedUser> {
        return try {
            // Fetch the shift document from Firestore
            val shiftDocument = firestore.collection("shifts").document(shiftId).get().await()

            // Extract assigned users from the document
            val assignedUsers = shiftDocument["assignedUsers"] as? List<Map<String, Any>> ?: emptyList()

            // Map assigned users and enrich them with details from ShiftRepository
            assignedUsers.mapNotNull { rawUser ->
                val baseUser = AssignedUser.fromFirestore(rawUser)

                // Fetch detailed user information
                val detailedUser = shiftRepository.getUserDetails(baseUser.userId)
                detailedUser?.copy(
                    userId = baseUser.userId, // Explicitly retain the userId
                    role = baseUser.role,
                    clockInTime = baseUser.clockInTime,
                    clockOutTime = baseUser.clockOutTime,
                    completedTasks = baseUser.completedTasks
                ) ?: baseUser // Fallback to baseUser if detailed info is unavailable
            }
        } catch (e: Exception) {
            Log.e("TaskRepository, fetchAssignedUsers", "Error fetching assigned users: ${e.localizedMessage}")
            emptyList()
        }
    }


}
