package nl.inholland.group9.karmakebab.data.services
import nl.inholland.group9.karmakebab.data.models.events.EventResponse
import retrofit2.Response
import retrofit2.http.*

interface EventService {
    @GET("/events")
    suspend fun getEvents(
        @Query("startDate") startDate: String,
        @Query("endDate") endDate: String
    ): Response<List<EventResponse>>

    @GET("/events/{id}")
    suspend fun getEventById(
        @Path("id") eventId: String
    ): Response<EventResponse>
}