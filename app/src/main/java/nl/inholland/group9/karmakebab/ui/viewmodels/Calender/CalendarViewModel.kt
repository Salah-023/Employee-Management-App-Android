package nl.inholland.group9.karmakebab.ui.viewmodels.Calender


import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import nl.inholland.group9.karmakebab.data.models.Event.Event
import nl.inholland.group9.karmakebab.data.models.shift.Shift
import nl.inholland.group9.karmakebab.data.repositories.AuthRepository
import nl.inholland.group9.karmakebab.data.repositories.EventRepository
import nl.inholland.group9.karmakebab.data.repositories.ShiftsRepository
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.WeekFields
import javax.inject.Inject

@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val shiftsRepository: ShiftsRepository,
    private val eventRepository: EventRepository,
    private val authRepository: AuthRepository
) : ViewModel() {
    private val _selectedTab = MutableStateFlow(0)
    val selectedTab: StateFlow<Int> = _selectedTab

    private val _currentWeek = MutableStateFlow(LocalDate.now().with(DayOfWeek.MONDAY))
    val currentWeek: StateFlow<LocalDate> = _currentWeek

    private val _currentWeekNumber = MutableStateFlow(getWeekNumber(_currentWeek.value))
    val currentWeekNumber: StateFlow<Int> = _currentWeekNumber

    private val _currentWeekDates = MutableStateFlow(getWeekDates(_currentWeek.value))
    val currentWeekDates: StateFlow<String> = _currentWeekDates

    private val _shifts = MutableStateFlow<List<Pair<Shift, String>>>(emptyList())
    val shifts: StateFlow<List<Pair<Shift, String>>> = _shifts

    private val _events = MutableStateFlow<List<Event>>(emptyList())
    val events: StateFlow<List<Event>> = _events

    private val _isCurrentWeek = MutableStateFlow(true)

    val isCurrentWeek: StateFlow<Boolean> = _isCurrentWeek

    private val _isLoadingShifts = MutableStateFlow(false)
    val isLoadingShifts: StateFlow<Boolean> = _isLoadingShifts

    private val _isLoadingEvents = MutableStateFlow(false)
    val isLoadingEvents: StateFlow<Boolean> = _isLoadingEvents


    init {
            updateWeekData()
            fetchContent()
            checkIfCurrentWeek()
    }

    fun checkIfCurrentWeek() {
        _isCurrentWeek.value = _currentWeek.value == LocalDate.now().with(DayOfWeek.MONDAY)
    }

    fun returnToCurrentWeek() {
        _currentWeek.value = LocalDate.now().with(DayOfWeek.MONDAY)
        updateWeekData()
        fetchContent()
        _isCurrentWeek.value = true
    }

    fun selectTab(tab: Int) {
        _selectedTab.value = tab
    }

    fun previousWeek() {
        _currentWeek.value = _currentWeek.value.minusWeeks(1)
        updateWeekData()
        fetchContent()
        checkIfCurrentWeek()
    }

    fun nextWeek() {
        _currentWeek.value = _currentWeek.value.plusWeeks(1)
        updateWeekData()
        fetchContent()
        checkIfCurrentWeek()
    }

    private fun updateWeekData() {
        val week = _currentWeek.value
        _currentWeekNumber.value = getWeekNumber(week)
        _currentWeekDates.value = getWeekDates(week)
    }

    private fun fetchContent() {
        val startOfWeek = Timestamp(java.util.Date.from(_currentWeek.value.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()))
        val endOfWeek = Timestamp(java.util.Date.from(_currentWeek.value.plusDays(6).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()))

        viewModelScope.launch {
            try {
                // Set loading states to true
                _isLoadingShifts.value = true
                _isLoadingEvents.value = true

                val userId = authRepository.getCurrentUser()?.uid
                if (userId != null) {
                    val shiftsList = shiftsRepository.getShiftsForUser(userId, startOfWeek, endOfWeek)
                    val shiftsWithRoles = shiftsList.map { shift ->
                        val teammates = shiftsRepository.fetchTeammatesForShift(shift)
                        val event = eventRepository.getEventById(shift.eventId)
                        val userRole = shift.assignedUsers.find { it.userId == userId }?.role ?: "Unknown Role"
                        shift.copy(assignedUsers = teammates, event = event) to userRole
                    }
                    _shifts.value = shiftsWithRoles
                } else {
                    println("Error: User ID is null.")
                }

                val eventsList = eventRepository.getEvents(startOfWeek, endOfWeek)
                _events.value = eventsList
            } catch (e: Exception) {
                println("Error fetching content: ${e.message}")
            } finally {
                // Set loading states to false
                _isLoadingShifts.value = false
                _isLoadingEvents.value = false
            }
        }
    }

    private fun getWeekNumber(date: LocalDate): Int {
        return date.get(WeekFields.ISO.weekOfWeekBasedYear())
    }

    private fun getWeekDates(date: LocalDate): String {
        val start = date
        val end = start.plusDays(6)
        return "${start.format(DateTimeFormatter.ofPattern("d MMM"))} - ${end.format(DateTimeFormatter.ofPattern("d MMM"))}"
    }


}

