package com.example.diabetesapp.code

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.diabetesapp.R
import com.example.diabetesapp.databinding.ActivityMainBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import java.util.concurrent.TimeUnit

/**
 * Main activity of the Diabetes App.
 *
 * This activity serves as the main entry point for the app, allowing users to navigate
 * between different fragments and manage their glucose measurements.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var notificationHelper: NotificationHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        notificationHelper = NotificationHelper(this)
        requestNotificationPermission()

        replaceFragment(MeasurementsFragment())
        binding.bottomNavigationView.background = null

        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.measurements -> replaceFragment(MeasurementsFragment())
                R.id.history -> replaceFragment(HistoryFragment())
                R.id.calculator -> replaceFragment(CalculatorFragment())
                R.id.library -> replaceFragment(LibraryFragment())
            }
            true
        }

        val fab: FloatingActionButton = findViewById(R.id.floatingActionButton)
        fab.setOnClickListener {
            showInputDialog()
        }

        schedulePeriodicNotifications()

        val logoutButton: Button = findViewById(R.id.logoutButton)
        logoutButton.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }

    /**
     * Displays a dialog for adding a glucose measurement.
     */
    private fun showInputDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_data_point, null)
        val glucoseInput = dialogView.findViewById<EditText>(R.id.editTextGlucose)

        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setTitle("Add Glucose Measurement")
            .setView(dialogView)
            .setPositiveButton("Submit") { dialog, _ ->
                val glucoseValue = glucoseInput.text.toString().toIntOrNull()
                val currentTime = System.currentTimeMillis()

                if (glucoseValue != null) {
                    addGlucoseMeasurement(glucoseValue, currentTime)
                } else {
                    Toast.makeText(this, "Invalid glucose value. Please provide a valid number.", Toast.LENGTH_SHORT).show()
                }
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }

        dialogBuilder.create().show()
    }

    /**
     * Adds a glucose measurement to the HistoryFragment.
     *
     * @param value The glucose value to add.
     * @param time The timestamp of the measurement.
     */
    private fun addGlucoseMeasurement(value: Int, time: Long) {
        val fragment = supportFragmentManager.findFragmentById(R.id.frame_layout) as? HistoryFragment
        if (fragment != null) {
            fragment.addGlucoseMeasurement(value, time)
        } else {
            replaceFragment(HistoryFragment())
            supportFragmentManager.executePendingTransactions()
            (supportFragmentManager.findFragmentById(R.id.frame_layout) as? HistoryFragment)?.addGlucoseMeasurement(value, time)
        }
    }

    /**
     * Replaces the current fragment with the specified fragment.
     *
     * @param fragment The fragment to display.
     */
    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager: FragmentManager = supportFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout, fragment)
        fragmentTransaction.commit()
    }

    /**
     * Requests notification permission for Android 13 and above.
     */
    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    PERMISSION_REQUEST_CODE
                )
            } else {
                sendReminderNotification()
            }
        } else {
            sendReminderNotification()
        }
    }

    /**
     * Sends a reminder notification to the user.
     */
    private fun sendReminderNotification() {
        notificationHelper.sendNotification("Reminder", "Check your glucose levels!")
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                sendReminderNotification()
            }
        }
    }

    /**
     * Schedules periodic notifications to remind the user to check their glucose levels.
     */
    private fun schedulePeriodicNotifications() {
        val workRequest = PeriodicWorkRequestBuilder<NotificationWorker>(30, TimeUnit.MINUTES)
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "NotificationWork",
            ExistingPeriodicWorkPolicy.REPLACE,
            workRequest
        )
    }

    companion object {
        private const val PERMISSION_REQUEST_CODE = 101
    }
}