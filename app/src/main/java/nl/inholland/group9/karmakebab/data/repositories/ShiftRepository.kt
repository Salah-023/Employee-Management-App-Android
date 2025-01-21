package nl.inholland.group9.karmakebab.data.repositories

import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import nl.inholland.group9.karmakebab.data.models.shift.AssignedUser
import nl.inholland.group9.karmakebab.data.models.shift.Shift
import javax.inject.Inject


class ShiftsRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {

    suspend fun getShiftsForUser(userId: String, startDate: Timestamp? = null, endDate: Timestamp? = null): List<Shift> {
        return try {
            var query = firestore.collection("shifts")
                .whereArrayContains("assignedUserIds", userId)
                .orderBy("startTime", Query.Direction.ASCENDING)

            if (startDate != null) {
                query = query.whereGreaterThanOrEqualTo("startTime", startDate)
            }
            if (endDate != null) {
                query = query.whereLessThanOrEqualTo("startTime", endDate)
            }

            val result = query.get().await()

            // Map query results to Shift objects
            val shifts = result.documents.mapNotNull { document ->
                val shift = document.toObject(Shift::class.java)?.copy(
                    assignedUsers = (document["assignedUsers"] as? List<Map<String, Any>>)?.mapNotNull { data ->
                    try {
                        AssignedUser(
                            userId = data["userId"] as? String ?: "",
                            role = data["role"] as? String ?: "",
                            fullName = data["fullName"] as? String,
                            clockInTime = data["clockInTime"] as? Timestamp,
                            clockOutTime = data["clockOutTime"] as? Timestamp,
                            completedTasks = data["completedTasks"] as? List<Int> ?: emptyList()
                        )
                    } catch (e: Exception) {
                        Log.e("AssignedUserParsing", "Error parsing assignedUser: $data", e)
                        null // Exclude invalid entries
                    }
                } ?: emptyList()
                )
                shift
            }
            shifts

        } catch (e: Exception) {
            Log.e("ShiftsRepository", "Failed to fetch shifts: ${e.localizedMessage}", e)
            emptyList()
        }
    }


    suspend fun getUserDetails(userId: String): AssignedUser? {
        return try {
            val document = firestore.collection("users").document(userId).get().await()
            document.toObject(AssignedUser::class.java)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun fetchTeammatesForShift(shift: Shift): List<AssignedUser> {
        val teammates = mutableListOf<AssignedUser>()
        for (userId in shift.assignedUserIds) {
            val userDetails = getUserDetails(userId)
            if (userDetails != null) {
                teammates.add(userDetails)
            } else {
                Log.d("TeammateRole", "User: $userId not found")
            }
        }
        return teammates
    }

    suspend fun clockIn(shiftId: String, userId: String) {
        try {
            val shiftRef = firestore.collection("shifts").document(shiftId)
            val shiftSnapshot = shiftRef.get().await()
            val assignedUsers = shiftSnapshot["assignedUsers"] as? List<Map<String, Any>> ?: return

            val updatedUsers = assignedUsers.map {
                if (it["userId"] == userId) {
                    it + ("clockInTime" to Timestamp.now())
                } else {
                    it
                }
            }

            shiftRef.update("assignedUsers", updatedUsers).await()
        } catch (e: Exception) {
            Log.e("ShiftsRepository", "Failed to clock in: ${e.localizedMessage}")
        }
    }



    suspend fun getShiftById(shiftId: String): Shift? {
        return try {
            val document = firestore.collection("shifts").document(shiftId).get().await()
            document.toObject(Shift::class.java)?.copy(id = document.id)
        } catch (e: Exception) {
            Log.e("ShiftsRepository", "Failed to fetch shift by ID: ${e.localizedMessage}")
            null
        }
    }

    suspend fun clockOutUser(shiftId: String, userId: String) {
        try {
            val shiftDocument = firestore.collection("shifts").document(shiftId)
            val snapshot = shiftDocument.get().await()

            val assignedUsers = snapshot["assignedUsers"] as? List<Map<String, Any>> ?: emptyList()
            val updatedUsers = assignedUsers.map { user ->
                if (user["userId"] == userId) {
                    user + ("clockOutTime" to com.google.firebase.Timestamp.now())
                } else {
                    user
                }
            }

            shiftDocument.update("assignedUsers", updatedUsers).await()
        } catch (e: Exception) {
            Log.e("ShiftsRepository", "Failed to clock out user: ${e.localizedMessage}")
        }
    }

}
