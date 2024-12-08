package nl.inholland.group9.karmakebab.data.repositories

import nl.inholland.group9.karmakebab.data.models.events.EventResponse
import nl.inholland.group9.karmakebab.data.services.EventService
import javax.inject.Inject
import java.text.SimpleDateFormat
import java.util.*

class EventRepository @Inject constructor(private val eventService: EventService) {

    suspend fun getEvents(): Result<List<EventResponse>> {
        return try {
            // Hardcode startDate (yesterday) and endDate (tomorrow)
            val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
            val startDate = Calendar.getInstance().apply { add(Calendar.DATE, -1) }.time
            val endDate = Calendar.getInstance().apply { add(Calendar.DATE, 1) }.time
            val formattedStartDate = dateFormat.format(startDate)
            val formattedEndDate = dateFormat.format(endDate)

            // Fetch events from API
            val response = eventService.getEvents(formattedStartDate, formattedEndDate)
            if (response.isSuccessful) {
                response.body()?.let { Result.success(it) }
                    ?: Result.failure(Exception("No events data found"))
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Unknown error"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getEvent(): Result<EventResponse> {
        return try {
            // Hardcode the event ID
            val eventId = "00000000-0000-0000-0000-000000000002"
            val response = eventService.getEventById(eventId)
            if (response.isSuccessful) {
                response.body()?.let { Result.success(it) }
                    ?: Result.failure(Exception("Event data not found"))
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Unknown error"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}