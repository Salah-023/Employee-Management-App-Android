package nl.inholland.group9.karmakebab.data.models.events

data class EventResponse(
    val id: String,
    val date: String,
    val address: String,
    val venue: String,
    val description: String,
    val money: Int,
    val eventStatus: String,
    val eventPerson: EventPerson,
    val note: String
)