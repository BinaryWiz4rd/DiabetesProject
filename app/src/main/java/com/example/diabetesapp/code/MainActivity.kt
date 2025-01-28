package com.example.diabetesapp.code

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.diabetesapp.R
import com.example.diabetesapp.databinding.ActivityMainBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.concurrent.TimeUnit

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

        scheduleDailyNotifications()
    }

    private fun showInputDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_data_point, null)
        val glucoseInput = dialogView.findViewById<EditText>(R.id.editTextGlucose)
        val timeInput = dialogView.findViewById<EditText>(R.id.editTextTime)

        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setTitle("Add Glucose Measurement")
            .setView(dialogView)
            .setPositiveButton("Submit") { dialog, _ ->
                val glucoseValue = glucoseInput.text.toString()
                val timeValue = timeInput.text.toString()

                if (glucoseValue.isNotEmpty() && timeValue.isNotEmpty()) {
                    Toast.makeText(this, "Glucose: $glucoseValue at $timeValue", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                }
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }

        dialogBuilder.create().show()
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager: FragmentManager = supportFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout, fragment)
        fragmentTransaction.commit()
    }

    private fun scheduleDailyNotifications() {
        val workRequest = PeriodicWorkRequestBuilder<NotificationWorker>(1, TimeUnit.DAYS)
            .build()
        WorkManager.getInstance(this).enqueue(workRequest)
    }

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

    companion object {
        private const val PERMISSION_REQUEST_CODE = 101
    }
}