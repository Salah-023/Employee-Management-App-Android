package nl.inholland.group9.karmakebab.data.repositories

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import nl.inholland.group9.karmakebab.data.models.Event.Event
import nl.inholland.group9.karmakebab.data.models.shift.AssignedUser
import nl.inholland.group9.karmakebab.data.models.shift.Shift
import nl.inholland.group9.karmakebab.data.services.ShiftService
import javax.inject.Inject


class ShiftsRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {

    suspend fun getShiftsForUser(userId: String): List<Shift> {
        return try {
            val result = firestore.collection("shifts")
                .whereArrayContains("assignedUserIds", userId)
                .orderBy("startTime", Query.Direction.ASCENDING)
                .get()
                .await()

            result.documents.mapNotNull { document ->
                document.toObject(Shift::class.java)?.copy(id = document.id)
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getEventById(eventId: String): Event? {
        return try {
            val document = firestore.collection("events").document(eventId).get().await()
            document.toObject(Event::class.java)?.copy(id = document.id)
        } catch (e: Exception) {
            null
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
            }
        }
        return teammates
    }
}
