package nl.inholland.group9.karmakebab.data.models.shift

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class Shift(
    val shiftId: String,
    val startTime: String,
    val endTime: String,
    val employeeId: String,
    val shiftType: String,
    val status: String,
    val clockInTime: String? = null,
    val clockOutTime: String? = null,
    val shiftHours: Int
) : Parcelable