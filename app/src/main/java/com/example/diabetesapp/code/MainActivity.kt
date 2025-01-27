package com.example.diabetesapp.code

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.diabetesapp.R
import com.example.diabetesapp.databinding.ActivityMainBinding
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

        binding.bottomNavigationView.setOnClickListener {
            val currentFragment = supportFragmentManager.findFragmentById(R.id.frame_layout)
            // if (currentFragment is MeasurementsFragment) {
            //     currentFragment.showAddDataPointDialog()
            // }
        }

        // Schedule daily notifications
        scheduleDailyNotifications()
    }

    /**
     * Replaces the current fragment with the provided fragment.
     */
    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager: FragmentManager = supportFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout, fragment)
        fragmentTransaction.commit()
    }

    /**
     * Schedules daily notifications using WorkManager.
     */
    private fun scheduleDailyNotifications() {
        val workRequest = PeriodicWorkRequestBuilder<NotificationWorker>(1, TimeUnit.DAYS)
            .build()
        WorkManager.getInstance(this).enqueue(workRequest)
    }

    /**
     * Requests the POST_NOTIFICATIONS permission for Android 13+ and sends a notification if granted.
     */
    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // request permission
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    PERMISSION_REQUEST_CODE
                )
            } else {
                // permission already granted; send a notification
                sendReminderNotification()
            }
        } else {
            // for older Android versions, directly send the notification
            sendReminderNotification()
        }
    }

    /**
     * Sends a reminder notification using NotificationHelper.
     */
    private fun sendReminderNotification() {
        notificationHelper.sendNotification("Reminder", "Check your glucose levels!")
    }

    /**
     * Handles the result of the POST_NOTIFICATIONS permission request.
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // permission granted; send a notification
                sendReminderNotification()
            } else {
                // permission denied
            }
        }
    }

    companion object {
        private const val PERMISSION_REQUEST_CODE = 101 // unique code for POST_NOTIFICATIONS permission
    }
}
