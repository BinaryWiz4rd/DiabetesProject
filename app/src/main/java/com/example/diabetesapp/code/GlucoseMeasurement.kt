package com.example.diabetesapp.code

data class GlucoseMeasurement(
    var id: String = "",
    val value: Double = 0.0,
    val time: Double = 0.0,
    val userId: String = "" //żeby te dane były powiązane z userem
)
