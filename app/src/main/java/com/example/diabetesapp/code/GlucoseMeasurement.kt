package com.example.diabetesapp.code

/**
 * Represents a glucose measurement.
 *
 * @property id Unique identifier for the measurement.
 * @property value Glucose level value.
 * @property time Timestamp of the measurement.
 * @property userId ID of the user associated with the measurement.
 */
data class GlucoseMeasurement(
    var id: String = "",
    val value: Double = 0.0,
    val time: Long = 0L,
    val userId: String = ""
)