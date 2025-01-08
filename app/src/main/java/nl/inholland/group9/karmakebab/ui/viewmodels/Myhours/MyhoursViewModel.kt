package nl.inholland.group9.karmakebab.ui.viewmodels.Myhours

import android.icu.util.Calendar
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import nl.inholland.group9.karmakebab.data.models.Availability.Availability
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class MyHoursViewModel @Inject constructor() : ViewModel() {
    private val calendar: Calendar = Calendar.getInstance()

    private val _selectedDate = MutableStateFlow(LocalDate.now())
    val selectedDate: StateFlow<LocalDate> = _selectedDate

    private val _currentMonth = MutableStateFlow("")
    val currentMonth: StateFlow<String> = _currentMonth

    private val _currentYear = MutableStateFlow(0)
    val currentYear: StateFlow<Int> = _currentYear

    private val _daysInMonth = MutableStateFlow<List<Int?>>(emptyList())
    val daysInMonth: StateFlow<List<Int?>> = _daysInMonth

    private val _selectedDayAvailability = MutableStateFlow<Availability?>(null)
    val selectedDayAvailability: StateFlow<Availability?> = _selectedDayAvailability

    private val availabilityData: MutableMap<LocalDate, Availability> = mutableMapOf()

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            updateCalendar(LocalDate.now())
        } else {
            println("Current device does not support LocalDate (requires API 26 or higher)")
        }
    }

    fun goToPreviousMonth() {
        calendar.add(Calendar.MONTH, -1)
        val newDate = LocalDate.of(
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH) + 1,
            1
        )
        updateCalendar(newDate)
    }

    fun getAvailabilityForDay(day: Int): Availability? {
        val date = _selectedDate.value.withDayOfMonth(day)
        return availabilityData[date]
    }


    fun goToNextMonth() {
        calendar.add(Calendar.MONTH, 1)
        val newDate = LocalDate.of(
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH) + 1,
            1
        )
        updateCalendar(newDate)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun selectDate(day: Int) {
        val newDate = _selectedDate.value.withDayOfMonth(day)
        _selectedDate.value = newDate
        _selectedDayAvailability.value = availabilityData[newDate]
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun updateCalendar(date: LocalDate) {
        _selectedDate.value = date
        _currentMonth.value = date.month.name.capitalize()
        _currentYear.value = date.year
        _daysInMonth.value = generateDays(date)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun generateDays(date: LocalDate): List<Int?> {
        val firstDayOfMonth = date.withDayOfMonth(1)
        val lastDayOfMonth = date.withDayOfMonth(date.lengthOfMonth())
        val emptyDays = firstDayOfMonth.dayOfWeek.value - 1
        val days = (1..lastDayOfMonth.dayOfMonth).toList()
        return List(emptyDays) { null } + days
    }
}