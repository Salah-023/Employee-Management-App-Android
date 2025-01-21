package nl.inholland.group9.karmakebab.data.models.Availability

import com.google.firebase.Timestamp

// Model for Availability
data class Availability(
    val id: String? = null,
    val date: Timestamp,
    val timeRange: String,
    val status: String,
    val startTime: String? = null,
    val endTime: String? = null
) {
    constructor() : this(null, Timestamp.now(), "", "", null, null)

    fun toMap(): Map<String, Any?> {
        return mapOf(
            "date" to date,
            "timeRange" to timeRange,
            "status" to status,
            "startTime" to startTime,
            "endTime" to endTime
        )
    }
}

