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

    private val _clockedInState = mutableMapOf<String, Boolean>() // Map to track clock-in states
    val clockedInState: Map<String, Boolean> get() = _clockedInState

    private val _upcomingEvents = MutableStateFlow<List<Event>>(emptyList())
    val upcomingEvents: StateFlow<List<Event>> = _upcomingEvents.asStateFlow()

    private val _isLoadingShifts = MutableStateFlow(false)
    val isLoadingShifts: StateFlow<Boolean> = _isLoadingShifts.asStateFlow()

    private val _isLoadingEvents = MutableStateFlow(false)
    val isLoadingEvents: StateFlow<Boolean> = _isLoadingEvents.asStateFlow()

    private val _updatedShiftStates = MutableStateFlow<Map<String, Boolean>>(emptyMap())
    val updatedShiftStates: StateFlow<Map<String, Boolean>> = _updatedShiftStates.asStateFlow()

    private val _clockOutStates = MutableStateFlow<Map<String, Boolean>>(emptyMap())
    val clockOutStates: StateFlow<Map<String, Boolean>> get() = _clockOutStates


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

                // Fetch shifts from today onwards, limited to 3
                val allShifts = shiftsRepository.getShiftsForUser(
                    userId = userId,
                    startDate = startDate,
                    endDate = null
                )

                val shiftsWithRoles = allShifts.take(3).map { shift ->
                    val teammates = shiftsRepository.fetchTeammatesForShift(shift)
                    val event = eventRepository.getEventById(shift.eventId)
                    val userRole = shift.assignedUsers.find { it.userId == userId }?.role ?: "Unknown Role"

                    shift.copy(assignedUsers = teammates, event = event) to userRole
                }

                // Refresh clock-in states for all fetched shifts
                refreshClockInStates(allShifts)

                _shiftsWithRoles.value = shiftsWithRoles
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

                // Immediately update the local state and notify observers
                _clockedInState[shiftId] = true
                _updatedShiftStates.value = _clockedInState.toMap() // Trigger recomposition
            } catch (e: Exception) {
                Log.e("HomePageViewModel", "Failed to clock in: ${e.localizedMessage}")
            }
        }
    }



    // Called when the app starts to refresh clock-in states
    fun refreshClockInStates(shifts: List<Shift>) {
        val userId = getCurrentUserId()
        val updatedStates = shifts.associate { shift ->
            shift.id!! to shift.assignedUsers.any { it.userId == userId && it.clockInTime != null }
        }
        _clockedInState.putAll(updatedStates) // Persist state locally
        _updatedShiftStates.value = _clockedInState
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

    fun checkClockOutState(shiftId: String) {
        val userId = getCurrentUserId() ?: return

        viewModelScope.launch {
            try {
                Log.d("HomePageViewModel", "Checking clock-out state for shift: $shiftId, user: $userId")

                // Fetch the shift and the user's role
                val shift = shiftsRepository.getShiftById(shiftId)
                val role = shift?.assignedUsers?.find { it.userId == userId }?.role ?: return@launch

                Log.d("HomePageViewModel", "Role for user in shift: $role")

                // Fetch all tasks for the role
                val allTasks = taskRepository.fetchTasksForRole(role)
                val allTaskIds = allTasks.map { it.id }.sorted()

                // Fetch completed tasks
                val completedTasks = taskRepository.fetchCompletedTasks(shiftId, userId).distinct()
                val completedTaskIds = completedTasks.sorted()

                Log.d("HomePageViewModel", "All task IDs: $allTaskIds")
                Log.d("HomePageViewModel", "Completed task IDs: $completedTaskIds")

                // Compare task IDs
                val allTasksCompleted = allTaskIds.size == completedTaskIds.size
                Log.d("HomePageViewModel", "All tasks completed: $allTasksCompleted")

                // Update the state for this shift
                _clockOutStates.value = _clockOutStates.value.toMutableMap().apply {
                    this[shiftId] = allTasksCompleted
                }
            } catch (e: Exception) {
                Log.e("HomePageViewModel", "Error checking clock-out state: ${e.localizedMessage}")
            }
        }
    }



    private val _clockOutClickedStates = MutableStateFlow<Map<String, Boolean>>(emptyMap())
    val clockOutClickedStates: StateFlow<Map<String, Boolean>> get() = _clockOutClickedStates

    fun clockOut(shiftId: String) {
        viewModelScope.launch {
            try {
                val userId = getCurrentUserId() ?: return@launch
                shiftsRepository.clockOutUser(shiftId, userId)

                // Update local state
                _clockedInState[shiftId] = false
                _updatedShiftStates.value = _clockedInState

                // Mark the shift as clocked out
                _clockOutClickedStates.value = _clockOutClickedStates.value.toMutableMap().apply {
                    this[shiftId] = true
                }

                Log.d("HomePageViewModel", "User clocked out for shift: $shiftId")
            } catch (e: Exception) {
                Log.e("HomePageViewModel", "Failed to clock out: ${e.localizedMessage}")
            }
        }
    }

}
