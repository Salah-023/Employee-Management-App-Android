package nl.inholland.group9.karmakebab.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class HeaderViewModel @Inject constructor() : ViewModel() {

    private val _greeting = MutableStateFlow("")
    val greeting: StateFlow<String> = _greeting.asStateFlow()

    private val _formattedDate = MutableStateFlow("")
    val formattedDate: StateFlow<String> = _formattedDate.asStateFlow()

    init {
        viewModelScope.launch {
            updateGreetingAndDate()
        }
    }

    private fun updateGreetingAndDate() {
        // Get the current date and time
        val calendar = Calendar.getInstance()

        // Format the date
        val dateFormat = SimpleDateFormat("dd MMM, EEEE", Locale.getDefault())
        val date = dateFormat.format(calendar.time)
        _formattedDate.value = date

        // Determine the greeting based on the hour of the day
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val greetingMessage = when {
            hour in 6..11 -> "Good Morning"
            hour in 12..17 -> "Good Afternoon"
            hour in 18..21 -> "Good Evening"
            else -> "Good Night"
        }
        _greeting.value = greetingMessage
    }
}
