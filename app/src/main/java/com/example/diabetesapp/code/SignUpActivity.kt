package com.example.diabetesapp.code

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.diabetesapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FirestoreClass {
    private val firestore = FirebaseFirestore.getInstance()

    fun registerOrUpdateUser(user: User) {
        firestore.collection("users")
            .document(user.id)
            .set(user)
            .addOnSuccessListener {
                println("User successfully added to Firestore")
            }
            .addOnFailureListener { e ->
                println("Error adding user to Firestore: ${e.message}")
            }
    }
}


class SignUpActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var signupEmail: EditText
    private lateinit var signupPassword: EditText
    private lateinit var signupButton: Button
    private lateinit var loginRedirectText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        auth = FirebaseAuth.getInstance()
        signupEmail = findViewById(R.id.signup_email)
        signupPassword = findViewById(R.id.signup_password)
        signupButton = findViewById(R.id.signup_button)
        loginRedirectText = findViewById(R.id.loginRedirectText)

        signupButton.setOnClickListener {
            val user = signupEmail.text.toString().trim()
            val pass = signupPassword.text.toString().trim()

            if (user.isEmpty()) {
                signupEmail.error = "Email cannot be empty"
                return@setOnClickListener
            }
            if (pass.isEmpty()) {
                signupPassword.error = "Password cannot be empty"
                return@setOnClickListener
            }

            auth.createUserWithEmailAndPassword(user, pass)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "SignUp Successful", Toast.LENGTH_SHORT).show()
                        val firebaseUser = auth.currentUser ?: throw Exception("User creation failed.")

                        val newUser = User(
                            id = firebaseUser.uid,
                            mail = firebaseUser.email ?: "No email"
                        )
                        saveUserToFirestore(newUser)
                        startActivity(Intent(this, LoginActivity::class.java))
                    } else {
                        Toast.makeText(this, "SignUp Failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
        }

        loginRedirectText.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }

    private fun saveUserToFirestore(user: User) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val firestoreClass = FirestoreClass()
                firestoreClass.registerOrUpdateUser(user)
                runOnUiThread {
                    Toast.makeText(this@SignUpActivity, "User data saved successfully!", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(this@SignUpActivity, "Failed to save user data: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
