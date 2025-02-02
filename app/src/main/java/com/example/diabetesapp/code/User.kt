package com.example.diabetesapp.code

/**
 * Data class representing a user in the application.
 *
 * @property id The unique identifier for the user.
 * @property mail The email address of the user.
 */
data class User(
    val id: String = "",
    val mail: String = ""
)