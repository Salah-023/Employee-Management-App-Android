package nl.inholland.group9.karmakebab.data.models.shift

data class Shift(
    val shiftId: String,
    val startTime: String,
    val endTime: String,
    val employeeId: String,
    val shiftType: String,
    val status: String,
    val clockInTime: String?,
    val clockOutTime: String?,
    val shiftHours: Int
)