package nl.inholland.group9.karmakebab.ui.viewmodels.Myhours


import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import nl.inholland.group9.karmakebab.data.models.Availability.Availability
import nl.inholland.group9.karmakebab.data.repositories.AvailabilityRepository
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class AvailabilityViewModel @Inject constructor(
    private val repository: AvailabilityRepository
) : ViewModel() {

    private val calendar = Calendar.getInstance()

    // UI states
    val selectedDate = MutableStateFlow(Date())
    private val _availabilityData = MutableStateFlow<Map<Date, Availability>>(emptyMap())
    val availabilityData: StateFlow<Map<Date, Availability>> get() = _availabilityData

    val currentMonth = MutableStateFlow("")
    val currentYear = MutableStateFlow(0)
    val daysInGrid = MutableStateFlow<List<Date?>>(emptyList())

    init {
        updateCalendar(selectedDate.value)
        loadAvailability()
    }

    fun loadAvailability() {
        viewModelScope.launch {
            try {
                val startOfMonth = getStartOfMonth(selectedDate.value)
                val endOfMonth = getEndOfMonth(selectedDate.value)
                val data = repository.getAvailability(startOfMonth, endOfMonth)

                // Normalize keys for availabilityData
                _availabilityData.value = data.mapKeys { normalizeDate(it.key) }

                Log.d("AvailabilityViewModel", "_availabilityData : ${_availabilityData.value}")
            } catch (e: Exception) {
                Log.e("AvailabilityViewModel", "Error loading availability: ${e.message}")
            }
        }
    }

    fun normalizeDate(date: Date): Date {
        calendar.time = date
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.time
    }

    fun saveAvailability(status: String, startTime: String? = null, endTime: String? = null) {
        val formattedDate = startOfDay(selectedDate.value)
        val normalizedStatus = when (status) {
            "Unavailable Entire Day" -> "unavailable"
            "Partially Available" -> "partial"
            else -> "available"
        }

        val timeRange = if (normalizedStatus == "partial") "$startTime - $endTime" else null


        val availability = Availability(
            date = Timestamp(formattedDate),
            timeRange = timeRange ?: "",
            status = normalizedStatus,
            startTime = startTime ?: "",
            endTime = endTime ?: ""
        )

        Log.d("AvailabilityViewModel", "Saving availability for date: $formattedDate with status: $normalizedStatus, startTime: $startTime, endTime: $endTime")
        Log.d("AvailabilityViewModel", "The availability that we are saving: $availability")

        viewModelScope.launch {
            try {
                repository.saveAvailability(formattedDate, availability)
                Log.d("AvailabilityViewModel", "Availability saved successfully for date: $formattedDate")
                loadAvailability() // Reload availability to reflect changes in the UI
            } catch (e: Exception) {
                Log.e("AvailabilityViewModel", "Error saving availability: ${e.message}")
            }
        }
    }


    fun saveAvailabilityForDateRange(
        startDate: Date,
        endDate: Date,
        status: String
    ) {
        viewModelScope.launch {
            try {
                Log.d(
                    "AvailabilityViewModel1",
                    "Starting to save availability for range: $startDate to $endDate with status: $status"
                )

                val currentCalendar = Calendar.getInstance()
                currentCalendar.time = startOfDay(startDate) // Normalize start date to midnight
                val normalizedEndDate = startOfDay(endDate)  // Normalize end date to midnight

                while (!currentCalendar.time.after(normalizedEndDate)) { // Include endDate in iteration
                    val date = currentCalendar.time
                    Log.d("AvailabilityViewModel1", "Processing date: $date for status: $status")

                    // Save availability for the current date
                    val formattedDate = startOfDay(date)
                    val availability = Availability(
                        date = Timestamp(formattedDate),
                        timeRange = null.toString(),
                        status = status,
                        startTime = null,
                        endTime = null
                    )

                    repository.saveAvailability(formattedDate, availability)
                    Log.d("AvailabilityViewModel1", "Availability saved for date: $date with status: $status")

                    // Move to the next day
                    currentCalendar.add(Calendar.DAY_OF_MONTH, 1)
                }

                Log.d(
                    "AvailabilityViewModel1",
                    "Finished saving availability for range: $startDate to $endDate"
                )

                loadAvailability() // Reload availability to reflect changes in the UI
            } catch (e: Exception) {
                Log.e("AvailabilityViewModel1", "Error saving availability for range: ${e.message}")
            }
        }
    }



    fun deleteAvailability() {
        val formattedDate = startOfDay(selectedDate.value)
        viewModelScope.launch {
            repository.deleteAvailability(formattedDate)
            loadAvailability()
        }
    }

    fun previousMonth() {
        viewModelScope.launch {
            val localCalendar = Calendar.getInstance()
            localCalendar.time = selectedDate.value
            localCalendar.add(Calendar.MONTH, -1)
            val newDate = localCalendar.time
            selectedDate.value = newDate
            updateCalendar(newDate)
            loadAvailability()
        }
    }

    fun nextMonth() {
        viewModelScope.launch {
            val localCalendar = Calendar.getInstance()
            localCalendar.time = selectedDate.value
            localCalendar.add(Calendar.MONTH, 1)
            val newDate = localCalendar.time
            selectedDate.value = newDate
            updateCalendar(newDate)
            loadAvailability()
        }
    }

    private fun updateCalendar(date: Date) {
        calendar.time = date
        currentMonth.value = SimpleDateFormat("MMMM", Locale.getDefault()).format(date)
        currentYear.value = calendar.get(Calendar.YEAR)
        daysInGrid.value = generateDaysInGrid()
    }

    private fun generateDaysInGrid(): List<Date?> {
        val tempCalendar = Calendar.getInstance()
        tempCalendar.time = calendar.time

        // Set to the first day of the month
        tempCalendar.set(Calendar.DAY_OF_MONTH, 1)
        val firstDayOfMonth = tempCalendar.time

        // Get the day of the week for the first day of the month
        val startDayOfWeek = tempCalendar.get(Calendar.DAY_OF_WEEK)

        // Adjust for Monday as the first day of the week (if necessary)
        val adjustedStartDayOfWeek = if (startDayOfWeek == Calendar.SUNDAY) 7 else startDayOfWeek - 1

        // Find the last day of the month
        tempCalendar.add(Calendar.MONTH, 1)
        tempCalendar.add(Calendar.DAY_OF_MONTH, -1)
        val lastDayOfMonth = tempCalendar.time

        // Build the grid
        val dates = mutableListOf<Date?>()
        repeat(adjustedStartDayOfWeek - 1) { dates.add(null) } // Add empty slots for days before the 1st
        tempCalendar.time = firstDayOfMonth
        while (tempCalendar.time <= lastDayOfMonth) {
            dates.add(tempCalendar.time)
            tempCalendar.add(Calendar.DAY_OF_MONTH, 1)
        }
        return dates
    }


    private fun getStartOfMonth(date: Date): Date {
        val tempCalendar = Calendar.getInstance()
        tempCalendar.time = date
        tempCalendar.set(Calendar.DAY_OF_MONTH, 1)
        tempCalendar.set(Calendar.HOUR_OF_DAY, 0)
        tempCalendar.set(Calendar.MINUTE, 0)
        tempCalendar.set(Calendar.SECOND, 0)
        tempCalendar.set(Calendar.MILLISECOND, 0)
        return tempCalendar.time
    }

    private fun getEndOfMonth(date: Date): Date {
        val tempCalendar = Calendar.getInstance()
        tempCalendar.time = date
        tempCalendar.set(Calendar.DAY_OF_MONTH, tempCalendar.getActualMaximum(Calendar.DAY_OF_MONTH))
        tempCalendar.set(Calendar.HOUR_OF_DAY, 23)
        tempCalendar.set(Calendar.MINUTE, 59)
        tempCalendar.set(Calendar.SECOND, 59)
        tempCalendar.set(Calendar.MILLISECOND, 999)
        return tempCalendar.time
    }

    private fun startOfDay(date: Date): Date {
        val tempCalendar = Calendar.getInstance()
        tempCalendar.time = date
        tempCalendar.set(Calendar.HOUR_OF_DAY, 0)
        tempCalendar.set(Calendar.MINUTE, 0)
        tempCalendar.set(Calendar.SECOND, 0)
        tempCalendar.set(Calendar.MILLISECOND, 0)
        return tempCalendar.time
    }
}
