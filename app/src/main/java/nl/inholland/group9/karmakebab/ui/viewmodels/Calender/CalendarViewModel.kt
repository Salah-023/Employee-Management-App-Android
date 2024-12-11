package nl.inholland.group9.karmakebab.ui.viewmodels.Calender


import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import nl.inholland.group9.karmakebab.data.models.Event.Event
import nl.inholland.group9.karmakebab.data.models.shift.Shift
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.WeekFields
import javax.inject.Inject

class CalendarViewModel @Inject constructor() : ViewModel() {
    private val _selectedTab = MutableStateFlow(0)
    val selectedTab: StateFlow<Int> = _selectedTab

    @RequiresApi(Build.VERSION_CODES.O)
    private val _currentWeek = MutableStateFlow(LocalDate.now().with(DayOfWeek.MONDAY))
    @RequiresApi(Build.VERSION_CODES.O)
    val currentWeek: StateFlow<LocalDate> = _currentWeek

    @RequiresApi(Build.VERSION_CODES.O)
    private val _currentWeekNumber = MutableStateFlow(getWeekNumber(_currentWeek.value))
    @RequiresApi(Build.VERSION_CODES.O)
    val currentWeekNumber: StateFlow<Int> = _currentWeekNumber

    @RequiresApi(Build.VERSION_CODES.O)
    private val _currentWeekDates = MutableStateFlow(getWeekDates(_currentWeek.value))
    @RequiresApi(Build.VERSION_CODES.O)
    val currentWeekDates: StateFlow<String> = _currentWeekDates

    private val _shifts = MutableStateFlow<List<Shift>>(emptyList())
    val shifts: StateFlow<List<Shift>> = _shifts

    private val _events = MutableStateFlow<List<Event>>(emptyList())
    val events: StateFlow<List<Event>> = _events

    @RequiresApi(Build.VERSION_CODES.O)
    private val _isCurrentWeek = MutableStateFlow(true)
    @RequiresApi(Build.VERSION_CODES.O)
    val isCurrentWeek: StateFlow<Boolean> = _isCurrentWeek




    // Immutable dummy data
    private val dummyShifts = listOf(
        Shift(
            shiftId = "1",
            startTime = "2024-12-11T07:00",
            endTime = "2024-12-11T15:00",
            employeeId = "101",
            shiftType = "Trucker",
            status = "Scheduled",
            clockInTime = null,
            clockOutTime = null,
            shiftHours = 8
        ),
        Shift(
            shiftId = "2",
            startTime = "2024-12-12T08:00",
            endTime = "2024-12-12T16:00",
            employeeId = "102",
            shiftType = "Building Crew",
            status = "Scheduled",
            clockInTime = null,
            clockOutTime = null,
            shiftHours = 8
        ),
        Shift(
            shiftId = "3",
            startTime = "2024-12-13T09:00",
            endTime = "2024-12-13T17:00",
            employeeId = "103",
            shiftType = "Organizer",
            status = "Scheduled",
            clockInTime = null,
            clockOutTime = null,
            shiftHours = 8
        )
    )

    private val dummyEvents = listOf(
        Event(
            title = "Vegan Summer Festival",
            location = "Prinses 202, Utrecht",
            date = "2024-12-11",
            startTime = "10:00",
            endTime = "18:00"
        ),
        Event(
            title = "Karma Kebab Special Event",
            location = "Dam Square, Amsterdam",
            date = "2024-12-12",
            startTime = "12:00",
            endTime = "20:00"
        ),
        Event(
            title = "Amsterdam ADE",
            location = "Overveen 13, Haarlem",
            date = "2024-12-13",
            startTime = "08:00",
            endTime = "14:00"
        )
    )

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            updateWeekData()
            fetchContent()
            checkIfCurrentWeek() // Ensure the button's visibility is set correctly at the start
        } else {
            println("Current device does not support LocalDate (requires API 26 or higher)")
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun checkIfCurrentWeek() {
        _isCurrentWeek.value = _currentWeek.value == LocalDate.now().with(DayOfWeek.MONDAY)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun returnToCurrentWeek() {
        _currentWeek.value = LocalDate.now().with(DayOfWeek.MONDAY)
        updateWeekData()
        fetchContent()
        _isCurrentWeek.value = true
    }
    fun selectTab(tab: Int) {
        _selectedTab.value = tab
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun previousWeek() {
        _currentWeek.value = _currentWeek.value.minusWeeks(1)
        updateWeekData()
        fetchContent()
        checkIfCurrentWeek() // Check if the current week matches the current date
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun nextWeek() {
        _currentWeek.value = _currentWeek.value.plusWeeks(1)
        updateWeekData()
        fetchContent()
        checkIfCurrentWeek() // Check if the current week matches the current date
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun updateWeekData() {
        val week = _currentWeek.value
        _currentWeekNumber.value = getWeekNumber(week)
        _currentWeekDates.value = getWeekDates(week)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun fetchContent() {
        val startOfWeek = _currentWeek.value
        val endOfWeek = startOfWeek.plusDays(6)

        _shifts.value = filterByDateRange(dummyShifts) { LocalDate.parse(it.startTime.substring(0, 10)) in startOfWeek..endOfWeek }
        _events.value = filterByDateRange(dummyEvents) { LocalDate.parse(it.date) in startOfWeek..endOfWeek }
    }

    private fun <T> filterByDateRange(list: List<T>, predicate: (T) -> Boolean): List<T> {
        return list.filter { predicate(it) }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getWeekNumber(date: LocalDate): Int {
        return date.get(WeekFields.ISO.weekOfWeekBasedYear())
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getWeekDates(date: LocalDate): String {
        val start = date
        val end = start.plusDays(6)
        return "${start.format(DateTimeFormatter.ofPattern("d MMM"))} - ${end.format(DateTimeFormatter.ofPattern("d MMM"))}"
    }
}


