package nl.inholland.group9.karmakebab.ui.viewmodels

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
    private val authRepository: AuthRepository,
    private val tokenManager: TokenManager
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
        tokenManager.accessToken.collect { token ->
            if (token.isNullOrEmpty()) {
                setDefaultUserInfo()
                return@collect
            }

            val result = authRepository.getUserInfo("eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJ3WjJuZW9NOERTSUhwZzVZNk5aM3pQdmZ1RTg3N3luZHVKMTBia3JXQTZzIn0.eyJleHAiOjE3MzMxNTcyNzEsImlhdCI6MTczMzE1Njk3MSwianRpIjoiMDUwZWJjODMtNjZjMC00OWQ1LWE3MzEtZjZlYjRiNGExODU0IiwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo4MDgwL3JlYWxtcy9rYXJtYS1rZWJhYi1yZWFsbSIsInN1YiI6ImJmMGU3YTg4LTAzMDUtNGU4MC05MzU3LWQ0YjdlZDQ0ZjRkOCIsInR5cCI6IkJlYXJlciIsImF6cCI6Imthcm1hLWtlYmFiLWNsaWVudCIsInNpZCI6ImY3ZDI2OTA0LTVkOTQtNDZiNC05MTUxLTIxZTkyOTY3ZjRhYSIsImFjciI6IjEiLCJhbGxvd2VkLW9yaWdpbnMiOlsiaHR0cDovL2xvY2FsaG9zdDozMDA1IiwiaHR0cDovL2xvY2FsaG9zdDozMDA0IiwiaHR0cDovL2xvY2FsaG9zdDozMDAzIiwiaHR0cDovL2xvY2FsaG9zdDozMDAyIiwiaHR0cDovL2xvY2FsaG9zdDozMDA2IiwiaHR0cDovL2xvY2FsaG9zdDozMDAxIl0sInNjb3BlIjoib3BlbmlkIHByb2ZpbGUgZW1haWwiLCJlbWFpbF92ZXJpZmllZCI6dHJ1ZSwibmFtZSI6IlRlc3QgVXNlciIsInByZWZlcnJlZF91c2VybmFtZSI6InRlc3R1c2VyIiwiZ2l2ZW5fbmFtZSI6IlRlc3QiLCJmYW1pbHlfbmFtZSI6IlVzZXIiLCJlbWFpbCI6InRlc3R1c2VyQGV4YW1wbGUuY29tIn0.PjQOkeCD3Wi5odsdjN9v4HjaNedW8FprNcTx-HnQz4JR_aJw1Yeny45Bv5ht88AhiMqOndKd3qwD9x6p1nu1DGN-5LWnnmM0KY11msMrO3pfJdYXebKUgFV1Bl31FjY-UeX9uYtok-KMiLrdrOpBz2gnX2esrtlikB9JxHmJPJvnB7_P6lfCi1i9oFt9M8m9dZZlLIU7FojkIJoSsWcPACP0TG0OtxxDgwflOx4qKUtIt6_y3Ve950rYIRSIotMMoHduKaqMt3EWvNxbBaTtJJezGLsp4TacU1v4PcaCS6_78rfpFk2Ff8lZl7gbkxWb-byrvcC4J7-EtXlPuCILcA")
            result.onSuccess { userInfo ->
                _userInfo.value = userInfo
                updateInitials(userInfo)
            }.onFailure {
                setDefaultUserInfo()
            }
        }
    }

    private fun updateInitials(userInfo: UserInfoResponse) {
        val firstInitial = userInfo.given_name.firstOrNull()?.uppercaseChar() ?: "N"
        val lastInitial = userInfo.family_name.firstOrNull()?.uppercaseChar() ?: "N"
        _initials.value = "$firstInitial$lastInitial"
    }

    private fun setDefaultUserInfo() {
        _userInfo.value = null
        _initials.value = "NN"
    }
}

