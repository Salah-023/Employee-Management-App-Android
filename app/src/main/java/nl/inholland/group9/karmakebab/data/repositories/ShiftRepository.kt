package nl.inholland.group9.karmakebab.data.repositories

import nl.inholland.group9.karmakebab.data.models.shift.Shift
import nl.inholland.group9.karmakebab.data.services.ShiftService
import javax.inject.Inject

class ShiftRepository @Inject constructor(private val shiftService: ShiftService) {

    suspend fun getShifts(employeeId: String): Result<List<Shift>> {
        return try {
            val response = shiftService.getShifts(employeeId)
            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyList())
            } else {
                Result.failure(Exception("Failed to fetch shifts: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}