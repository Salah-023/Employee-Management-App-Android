package nl.inholland.group9.karmakebab.data.models.shift


import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.PropertyName
import nl.inholland.group9.karmakebab.data.models.Event.Event

@IgnoreExtraProperties
data class Shift(
    @DocumentId var id: String? = null,
    val eventId: String = "",
    @PropertyName("startTime") var startTime: Timestamp? = null,
    @PropertyName("endTime") var endTime: Timestamp? = null,
    val assignedUserIds: List<String> = emptyList(),
    var assignedUsers: List<AssignedUser> = emptyList(),
    var event: Event? = null
)

@IgnoreExtraProperties
data class AssignedUser(
    @PropertyName("userId") val userId: String = "",
    @PropertyName("role") val role: String = "",
    @PropertyName("fullName") var fullName: String? = null,
    @PropertyName("clockInTime") var clockInTime: Timestamp? = null,
    @PropertyName("clockOutTime") var clockOutTime: Timestamp? = null,
    @PropertyName("completedTasks") var completedTasks: List<Int> = emptyList()
) {
    val initials: String
        get() {
            val nameComponents = fullName?.split(" ") ?: emptyList()
            return nameComponents
                .mapNotNull { it.firstOrNull()?.uppercaseChar() }
                .take(2)
                .joinToString("")
        }


    companion object {
        fun fromFirestore(data: Map<String, Any?>): AssignedUser {
            return AssignedUser(
                userId = data["userId"] as? String ?: "",
                role = data["role"] as? String ?: "",
                fullName = data["fullName"] as? String,
                clockInTime = data["clockInTime"] as? Timestamp,
                clockOutTime = data["clockOutTime"] as? Timestamp
            )
        }
    }
}