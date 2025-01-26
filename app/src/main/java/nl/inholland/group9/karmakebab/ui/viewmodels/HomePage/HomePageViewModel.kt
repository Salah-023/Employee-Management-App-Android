package nl.inholland.group9.karmakebab.ui.viewmodels.HomePage

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import nl.inholland.group9.karmakebab.data.models.Event.Event
import nl.inholland.group9.karmakebab.data.models.shift.Shift
import nl.inholland.group9.karmakebab.data.repositories.AuthRepository
import nl.inholland.group9.karmakebab.data.repositories.EventRepository
import nl.inholland.group9.karmakebab.data.repositories.ShiftsRepository
import nl.inholland.group9.karmakebab.data.repositories.TaskRepository
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject


@HiltViewModel
class HomePageViewModel @Inject constructor(
    private val shiftsRepository: ShiftsRepository,
    private val authRepository: AuthRepository,
    private val eventRepository: EventRepository,
    private val taskRepository: TaskRepository
) : ViewModel() {

    private val _shiftsWithRoles = MutableStateFlow<List<Pair<Shift, String>>>(emptyList())
    val shiftsWithRoles: StateFlow<List<Pair<Shift, String>>> = _shiftsWithRoles.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()


    private val _upcomingEvents = MutableStateFlow<List<Event>>(emptyList())
    val upcomingEvents: StateFlow<List<Event>> = _upcomingEvents.asStateFlow()

    private val _isLoadingShifts = MutableStateFlow(false)
    val isLoadingShifts: StateFlow<Boolean> = _isLoadingShifts.asStateFlow()

    private val _isLoadingEvents = MutableStateFlow(false)
    val isLoadingEvents: StateFlow<Boolean> = _isLoadingEvents.asStateFlow()

    private val _shiftStatuses = MutableStateFlow<Map<String, String>>(emptyMap())
    val shiftStatuses: StateFlow<Map<String, String>> = _shiftStatuses


    fun fetchShiftsForCurrentUser() {
        _isLoadingShifts.value = true
        val userId = authRepository.getCurrentUser()?.uid ?: return

        viewModelScope.launch {
            try {
                val calendar = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }
                val startDate = Timestamp(calendar.time)

                // Fetch shifts from the repository
                val shifts = shiftsRepository.getShiftsForUser(
                    userId = userId,
                    startDate = startDate
                )

                // Map to roles within the ViewModel
                val shiftsWithRoles = shifts.map { shift ->
                    shift.id?.let { getShiftStatus(it) }
                    val userRole = shift.assignedUsers.find { it.userId == userId }?.role ?: "Unknown Role"
                    shift to userRole
                }

                _shiftsWithRoles.value = shiftsWithRoles.take(3) // Limit to 3 shifts

            } catch (e: Exception) {
                _error.value = "Failed to fetch shifts: ${e.localizedMessage}"
            } finally {
                _isLoadingShifts.value = false
            }
        }
    }

    fun formatTimestampToString(timestamp: Any?, format: String = "yyyy-MM-dd HH:mm"): String {
        val sdf = SimpleDateFormat(format, Locale.getDefault())
        return when (timestamp) {
            is Long -> sdf.format(Date(timestamp)) // If it's a Long, treat as milliseconds
            is Timestamp -> sdf.format(timestamp.toDate()) // Convert Firebase Timestamp to Date
            else -> "Invalid Date" // Fallback for unsupported types
        }
    }

    fun clockIn(shiftId: String) {
        val userId = authRepository.getCurrentUser()?.uid ?: return
        viewModelScope.launch {
            try {
                // Update the clock-in state in the database
                shiftsRepository.clockIn(shiftId, userId)

            } catch (e: Exception) {
                Log.e("HomePageViewModel", "Failed to clock in: ${e.localizedMessage}")
            }
        }
    }

    fun getCurrentUserId(): String? {
        return authRepository.getCurrentUser()?.uid
    }

    fun fetchUpcomingEvents() {
        _isLoadingEvents.value = true
        viewModelScope.launch {
            try {
                val currentDate = Timestamp.now()
                val allEvents = eventRepository.getEvents(
                    startDate = currentDate,
                    endDate = null // No upper limit
                )
                _upcomingEvents.value = allEvents.take(3) // Limit to 3 upcoming events
            } catch (e: Exception) {
                Log.e("HomePageViewModel", "Failed to fetch events: ${e.localizedMessage}")
            }finally {
                _isLoadingEvents.value = false
            }
        }
    }

    fun getShiftStatus(shiftId: String) {
        val userId = getCurrentUserId()

        if (userId != null) {
            viewModelScope.launch {
                try {
                    val shift = shiftsRepository.getShiftById(shiftId)
                    val user = shift?.assignedUsers?.find { it.userId == userId }

                    val newStatus = when {
                        user == null -> "CLOCK-IN" // Default to CLOCK-IN if the user is not found
                        user.clockInTime == null -> "CLOCK-IN"
                        user.clockInTime != null && user.clockOutTime == null -> {
                            val allTasks = taskRepository.fetchTasksForRole(user.role)
                            val completedTasks = taskRepository.fetchCompletedTasks(shiftId, userId)
                            if (completedTasks.size == allTasks.size) "CLOCK-OUT" else "GO-TO-TASKS"
                        }
                        user.clockInTime != null && user.clockOutTime != null -> "COMPLETED"
                        else -> "UNKNOWN"
                    }

                    // Update the shift status in the map
                    _shiftStatuses.value = _shiftStatuses.value.toMutableMap().apply {
                        this[shiftId] = newStatus
                    }
                } catch (e: Exception) {
                    Log.e("HomePageViewModel", "Error fetching shift status: ${e.message}")

                    // Set error status
                    _shiftStatuses.value = _shiftStatuses.value.toMutableMap().apply {
                        this[shiftId] = "ERROR"
                    }
                }
            }
        } else {
            // Set error status
            _shiftStatuses.value = _shiftStatuses.value.toMutableMap().apply {
                this[shiftId] = "ERROR"
            }
        }
    }


    fun clockOut(shiftId: String) {
        viewModelScope.launch {
            try {
                val userId = getCurrentUserId() ?: return@launch
                shiftsRepository.clockOutUser(shiftId, userId)
            } catch (e: Exception) {
                Log.e("HomePageViewModel", "Failed to clock out: ${e.localizedMessage}")
            }
        }
    }

}
