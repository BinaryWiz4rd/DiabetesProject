package com.example.diabetesapp.code

/**
 * Data class representing a glucose measurement.
 *
 * @property value The glucose level measured in mg/dL.
 * @property time The timestamp of the measurement.
 * @property userId The ID of the user who made the measurement.
 * @property id The unique identifier for the measurement.
 */
data class GlucoseMeasurement(
    val value: Int = 0,
    val time: Long = System.currentTimeMillis(),
    val userId: String = "",
    val id: String = ""
)