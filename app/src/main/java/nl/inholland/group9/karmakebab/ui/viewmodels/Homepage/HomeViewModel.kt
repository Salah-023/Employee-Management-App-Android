package nl.inholland.group9.karmakebab.ui.viewmodels.Homepage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import nl.inholland.group9.karmakebab.data.models.events.EventResponse
import nl.inholland.group9.karmakebab.data.models.events.FormattedEvent
import nl.inholland.group9.karmakebab.data.models.shift.Shift
import nl.inholland.group9.karmakebab.data.repositories.EventRepository
import nl.inholland.group9.karmakebab.data.repositories.ShiftRepository
import javax.inject.Inject


@HiltViewModel
class HomeViewModel @Inject constructor(
    private val shiftRepository: ShiftRepository,
    private val eventRepository: EventRepository // Inject the EventRepository
) : ViewModel() {

    private val _shifts = MutableStateFlow<List<Shift>>(emptyList())
    val shifts: StateFlow<List<Shift>> = _shifts.asStateFlow()

    private val _events = MutableStateFlow<List<EventResponse>>(emptyList())
    val events: StateFlow<List<EventResponse>> = _events.asStateFlow()

    private val _formattedEvents = MutableStateFlow<List<FormattedEvent>>(emptyList())
    val formattedEvents: StateFlow<List<FormattedEvent>> = _formattedEvents.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _isLoadingEvents = MutableStateFlow(false)
    val isLoadingEvents: StateFlow<Boolean> = _isLoadingEvents.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        fetchShifts()
        fetchAllEvents()
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

    private fun fetchAllEvents() {
        viewModelScope.launch {
            _isLoadingEvents.value = true
            val result = eventRepository.getEvent() // Updated to use the new repository method
            result.onSuccess { events ->
                _events.value = listOf(events)
            }.onFailure {
                _errorMessage.value = it.message
            }
            _isLoadingEvents.value = false
        }
    }

}

