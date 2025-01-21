package nl.inholland.group9.karmakebab.data.repositories

import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import nl.inholland.group9.karmakebab.data.models.Event.Event
import javax.inject.Inject

class EventRepository  @Inject constructor(private val firestore: FirebaseFirestore) {

    suspend fun getEventById(eventId: String): Event? {
        return try {
            val document = firestore.collection("events").document(eventId).get().await()
            val event = document.toObject(Event::class.java)
            event
        } catch (e: Exception) {
            Log.e("EventRepository", "Error fetching event: ${e.localizedMessage}", e)
            null
        }
    }

    suspend fun getEvents(startDate: Timestamp?, endDate: Timestamp?): List<Event> {
        return try {
            var query = firestore.collection("events")
                .orderBy("startTime", Query.Direction.ASCENDING)

            if (startDate != null) {
                query = query.whereGreaterThanOrEqualTo("startTime", startDate)
            }
            if (endDate != null) {
                query = query.whereLessThanOrEqualTo("startTime", endDate)
            }

            val result = query.get().await()
            result.documents.mapNotNull { it.toObject(Event::class.java)?.copy(id = it.id) }
        } catch (e: Exception) {
            Log.e("EventRepository", "Failed to fetch events: ${e.localizedMessage}")
            emptyList()
        }
    }


}