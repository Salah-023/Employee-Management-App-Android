package nl.inholland.group9.karmakebab.ui.viewmodels.HomePage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import nl.inholland.group9.karmakebab.data.models.Event.Event
import nl.inholland.group9.karmakebab.data.models.shift.Shift
import nl.inholland.group9.karmakebab.data.repositories.ShiftRepository
import javax.inject.Inject


@HiltViewModel
class HomePageViewModel @Inject constructor(
    private val shiftRepository: ShiftRepository
) : ViewModel() {

    private val _shifts = MutableStateFlow<List<Shift>>(emptyList())
    val shifts: StateFlow<List<Shift>> = _shifts.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _events = MutableStateFlow(
        listOf(
            Event("OZ FESTIVAL", "Prinses 91, Utrecht", "2024-09-25", "07:00", "15:00"),
            Event("VEGAN MARKET", "Central Park, Utrecht", "2024-10-10", "09:00", "17:00")
        )
    )

    val events: StateFlow<List<Event>> = _events.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        fetchShifts()
    }

    private fun fetchShifts() {
        val employeeId = "8f62c72c-faf3-46c8-8f69-9f9d85f41198"
        viewModelScope.launch {
            _isLoading.value = true
            val result = shiftRepository.getShifts(employeeId)
            result.onSuccess { shifts ->
                _shifts.value = shifts
                _errorMessage.value = null
            }.onFailure { error ->
                _errorMessage.value = error.message
            }
            _isLoading.value = false
        }
    }
}