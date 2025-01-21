package nl.inholland.group9.karmakebab.data.models.Event

import com.google.firebase.Timestamp
import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class Event(
    val id: String = "",
    val name: String = "",
    val startTime: Timestamp? = null,
    val endTime: Timestamp? = null,
    val address: String = "",
    val venue: String = "",
    val description: String = "",
    val status: String = "",
    val note: String? = null,
)
