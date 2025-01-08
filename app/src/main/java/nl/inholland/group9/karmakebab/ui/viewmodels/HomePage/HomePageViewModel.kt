package nl.inholland.group9.karmakebab.ui.viewmodels.HomePage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import nl.inholland.group9.karmakebab.data.models.shift.Shift
import nl.inholland.group9.karmakebab.data.repositories.AuthRepository
import nl.inholland.group9.karmakebab.data.repositories.ShiftsRepository
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject


@HiltViewModel
class HomePageViewModel @Inject constructor(
    private val shiftsRepository: ShiftsRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _shifts = MutableStateFlow<List<Shift>>(emptyList())
    val shifts: StateFlow<List<Shift>> = _shifts.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun fetchShiftsForCurrentUser() {
        val userId = authRepository.getCurrentUser()?.uid ?: return

        viewModelScope.launch {
            try {
                val userShifts = shiftsRepository.getShiftsForUser(userId)
                _shifts.value = userShifts.map { shift ->
                    val teammates = shiftsRepository.fetchTeammatesForShift(shift)
                    val event = shiftsRepository.getEventById(shift.eventId)
                    shift.copy(assignedUsers = teammates, event = event)
                }
            } catch (e: Exception) {
                _error.value = "Failed to fetch shifts: ${e.localizedMessage}"
            }
        }
    }

    fun formatTimestampToString(timestamp: Timestamp?): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        return sdf.format(timestamp)
    }
}
