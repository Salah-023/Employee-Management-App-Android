package nl.inholland.group9.karmakebab.ui.viewmodels.Header

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import nl.inholland.group9.karmakebab.data.models.auth.UserInfoResponse
import nl.inholland.group9.karmakebab.data.repositories.AuthRepository
import nl.inholland.group9.karmakebab.data.utils.TokenManager
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class HeaderViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _greeting = MutableStateFlow("")
    val greeting: StateFlow<String> = _greeting.asStateFlow()

    private val _formattedDate = MutableStateFlow("")
    val formattedDate: StateFlow<String> = _formattedDate.asStateFlow()

    private val _userInfo = MutableStateFlow<UserInfoResponse?>(null)
    val userInfo: StateFlow<UserInfoResponse?> = _userInfo.asStateFlow()

    private val _initials = MutableStateFlow("NN")
    val initials: StateFlow<String> = _initials.asStateFlow()

    init {
        viewModelScope.launch {
            updateGreetingAndDate()
            fetchUserInfo()
        }
    }

    private fun updateGreetingAndDate() {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)

        val greetingMessage = when {
            hour in 6..11 -> "Good Morning"
            hour in 12..17 -> "Good Afternoon"
            hour in 18..21 -> "Good Evening"
            else -> "Good Night"
        }
        _greeting.value = greetingMessage

        val dateFormat = SimpleDateFormat("dd MMM, EEEE", Locale.getDefault())
        _formattedDate.value = dateFormat.format(calendar.time)
    }

    private suspend fun fetchUserInfo() {
        val userInfo = authRepository.getUserData()
        if (userInfo != null) {
            _userInfo.value = userInfo
            updateInitials(userInfo) // Pass the immutable `userInfo` directly
        } else {
            setDefaultUserInfo()
        }
    }

    private fun updateInitials(userInfo: UserInfoResponse) {
        val firstInitial = userInfo.fullName.firstOrNull()?.uppercaseChar() ?: "N"
        val lastInitial = userInfo.fullName.split(" ").lastOrNull()?.firstOrNull()?.uppercaseChar() ?: "N"
        _initials.value = "$firstInitial$lastInitial"
    }

    private fun setDefaultUserInfo() {
        _userInfo.value = null
        _initials.value = "NN"
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout() // Call the repository's logout function
        }
    }
}

