package nl.inholland.group9.karmakebab.ui.viewmodels.HomePage

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import nl.inholland.group9.karmakebab.data.models.shift.AssignedUser
import nl.inholland.group9.karmakebab.data.models.shift.Task
import nl.inholland.group9.karmakebab.data.repositories.AuthRepository
import nl.inholland.group9.karmakebab.data.repositories.TaskRepository
import javax.inject.Inject

@HiltViewModel
class TaskViewModel @Inject constructor(
    private val taskRepository: TaskRepository,
    val authRepository: AuthRepository
) : ViewModel() {

    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks: StateFlow<List<Task>> = _tasks.asStateFlow()

    private val _progress = MutableStateFlow(0.0)
    val progress: StateFlow<Double> = _progress.asStateFlow()

    private val userId = authRepository.getCurrentUser()?.uid ?: ""

    fun loadTasksForShift(shiftId: String, role: String) {
        viewModelScope.launch {
            try {
                // Fetch predefined tasks for the role
                val predefinedTasks = taskRepository.fetchTasksForRole(role)
                Log.d("TaskViewModel", "Predefined Tasks: $predefinedTasks")

                // Fetch completed tasks from the repository
                val completedTaskIds = taskRepository.fetchCompletedTasks(shiftId, userId)
                Log.d("TaskViewModel", "Fetched Completed Tasks: $completedTaskIds")

                // Normalize IDs and map tasks
                val updatedTasks = predefinedTasks.map { task ->
                    val isTaskDone = completedTaskIds.map { it.toString() }.contains(task.id.toString())
                    Log.d(
                        "TaskViewModel",
                        "Task ID: ${task.id}, Is Done: $isTaskDone, Completed Tasks: $completedTaskIds"
                    )
                    task.copy(isDone = isTaskDone)
                }

                // Update the UI state
                _tasks.value = updatedTasks
                _progress.value = updatedTasks.count { it.isDone }.toDouble() / updatedTasks.size

                Log.d("TaskViewModel", "Updated Tasks: $updatedTasks")
            } catch (e: Exception) {
                Log.e("TaskViewModel", "Error loading tasks: ${e.localizedMessage}")
            }
        }
    }

    fun markTaskAsDone(shiftId: String, taskId: Int) {
        viewModelScope.launch {
            taskRepository.markTaskAsDone(shiftId, userId, taskId)
            val updatedTasks = _tasks.value.map {
                if (it.id == taskId) it.copy(isDone = true) else it
            }
            _tasks.value = updatedTasks
            _progress.value = updatedTasks.count { it.isDone }.toDouble() / updatedTasks.size
        }
    }

    fun handleCapturedImage(taskId: Int, shiftId: String, imagePath: String) {
        viewModelScope.launch {
            // Logic to store or upload the image
            taskRepository.uploadImage(shiftId, taskId, imagePath)

            // Update the task as done
            val updatedTasks = _tasks.value.map {
                if (it.id == taskId) it.copy(isDone = true) else it
            }
            _tasks.value = updatedTasks
            _progress.value = updatedTasks.count { it.isDone }.toDouble() / updatedTasks.size
        }
    }

    fun fetchAssignedUsers(shiftId: String): List<AssignedUser> {
        return runBlocking {
            taskRepository.fetchAssignedUsers(shiftId)
        }
    }
}
