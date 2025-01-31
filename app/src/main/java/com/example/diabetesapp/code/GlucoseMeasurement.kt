package com.example.diabetesapp.code

data class GlucoseMeasurement(
    val value: Int = 0,
    val time: Long = 0,
    val userId: String = ""
)
