package nl.inholland.group9.karmakebab.data.models.shift


import com.google.firebase.Timestamp
import nl.inholland.group9.karmakebab.data.models.Event.Event

data class Shift(
    val id: String = "",
    val eventId: String = "",
    val startTime: Timestamp? = null,
    val endTime: Timestamp? = null,
    val assignedUserIds: List<String> = emptyList(),
    var assignedUsers: List<AssignedUser> = emptyList(),
    var event: Event? = null
)

data class AssignedUser(
    val userId: String = "",
    val role: String = "",
    var fullName: String? = null,
    var clockInTime: Timestamp? = null,
    var clockOutTime: Timestamp? = null
) {
    val initials: String
        get() {
            val nameComponents = fullName?.split(" ") ?: emptyList()
            return nameComponents
                .mapNotNull { it.firstOrNull()?.uppercaseChar() }
                .take(2)
                .joinToString("")
        }
}