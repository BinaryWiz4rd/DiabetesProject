package com.example.diabetesapp.code

data class GlucoseMeasurement(
    val value: Int = 0,
    val time: Long = System.currentTimeMillis(),
    val userId: String = "",
    val id: String = ""
)
