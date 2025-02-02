package com.example.diabetesapp.code

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.diabetesapp.R
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth

/**
 * Activity for user login.
 *
 * This activity allows users to log in using their email and password.
 * It also provides options for signing up and resetting the password.
 */
class LoginActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var loginEmail: EditText
    private lateinit var loginPassword: EditText
    private lateinit var loginButton: Button
    private lateinit var signupRedirectText: TextView
    private lateinit var forgotPassword: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()
        loginEmail = findViewById(R.id.login_email)
        loginPassword = findViewById(R.id.login_password)
        loginButton = findViewById(R.id.login_button)
        signupRedirectText = findViewById(R.id.signupRedirectText)
        forgotPassword = findViewById(R.id.forgot_password)

        loginButton.setOnClickListener {
            val email = loginEmail.text.toString()
            val pass = loginPassword.text.toString()

            if (email.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                if (pass.isNotEmpty()) {
                    auth.signInWithEmailAndPassword(email, pass)
                        .addOnSuccessListener { authResult: AuthResult ->
                            Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this, MainActivity::class.java))
                            finish()
                        }
                        .addOnFailureListener { e: Exception ->
                            Toast.makeText(this, "Login Failed: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    loginPassword.error = "Password cannot be empty"
                }
            } else if (email.isEmpty()) {
                loginEmail.error = "Email cannot be empty"
            } else {
                loginEmail.error = "Please enter a valid email"
            }
        }

        signupRedirectText.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }

        forgotPassword.setOnClickListener {
            showForgotPasswordDialog()
        }
    }

    /**
     * Displays a dialog for resetting the password.
     */
    private fun showForgotPasswordDialog() {
        val builder = AlertDialog.Builder(this)
        val dialogView = layoutInflater.inflate(R.layout.dialog_forgot, null)
        val emailBox = dialogView.findViewById<EditText>(R.id.emailBox)
        builder.setView(dialogView)
        val dialog = builder.create()
        dialogView.findViewById<View>(R.id.btnReset).setOnClickListener {
            val userEmail = emailBox.text.toString()
            if (TextUtils.isEmpty(userEmail) || !Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()) {
                Toast.makeText(this, "Enter your registered email id", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            auth.sendPasswordResetEmail(userEmail).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Reset email sent", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Error sending reset email", Toast.LENGTH_SHORT).show()
                }
            }
        }
        dialog.show()
    }
}