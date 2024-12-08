package nl.inholland.group9.karmakebab.data.services

import nl.inholland.group9.karmakebab.data.models.shift.Shift
import retrofit2.Response
import retrofit2.http.*

interface ShiftService {
    @GET("/shifts")
    suspend fun getShifts(@Query("employeeId") employeeId: String): Response<List<Shift>>
}