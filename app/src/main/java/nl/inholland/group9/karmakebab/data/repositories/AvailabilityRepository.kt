package nl.inholland.group9.karmakebab.data.repositories

import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import nl.inholland.group9.karmakebab.data.models.Availability.Availability
import java.text.SimpleDateFormat

import java.util.Date
import java.util.Locale
import javax.inject.Inject

class AvailabilityRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val authRepository: AuthRepository
) {
    suspend fun getAvailability(startDate: Date, endDate: Date): Map<Date, Availability> {
        val userId = authRepository.getCurrentUser()?.uid
        val snapshot = userId?.let {
            firestore.collection("availability")
                .document(it)
                .collection("dates")
                .whereGreaterThanOrEqualTo("date", Timestamp(startDate))
                .whereLessThanOrEqualTo("date", Timestamp(endDate))
                .get()
                .await()
        }

        if (snapshot != null) {

            return snapshot.documents.mapNotNull { doc ->
                val data = doc.toObject(Availability::class.java) ?: return@mapNotNull null
                val date = data.date.toDate()
                date to data
            }.toMap()
        } else {
            Log.d("AvailabilityRepository", "No availability data fetched for the user")
            return emptyMap() // Return an empty map if snapshot is null
        }
    }

    suspend fun saveAvailability(date: Date, availability: Availability) {
        val userId = authRepository.getCurrentUser()?.uid
        val formattedDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(date)

        if (userId != null) {
            try {
                firestore.collection("availability")
                    .document(userId)
                    .collection("dates")
                    .document(formattedDate)
                    .set(availability.toMap())
                    .await()
            } catch (e: Exception) {
                Log.e("AvailabilityRepository", "Error saving availability: ${e.message}")
            }
        }
    }

    suspend fun deleteAvailability(date: Date) {
        val userId = authRepository.getCurrentUser()?.uid
        val formattedDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(date)
        if (userId != null) {
            firestore.collection("availability")
                .document(userId)
                .collection("dates")
                .document(formattedDate)
                .delete()
                .await()
        }
    }
}
