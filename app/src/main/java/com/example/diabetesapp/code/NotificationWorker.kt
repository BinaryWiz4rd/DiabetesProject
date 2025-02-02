package com.example.diabetesapp.code

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters

/**
 * Worker class for sending notifications at scheduled intervals.
 *
 * This class is responsible for sending reminder notifications to the user
 * to check their glucose levels.
 *
 * @property appContext The application context.
 * @property workerParams The parameters for the worker.
 */
class NotificationWorker(appContext: Context, workerParams: WorkerParameters) : Worker(appContext, workerParams) {
    override fun doWork(): Result {
        val notificationHelper = NotificationHelper(applicationContext)
        notificationHelper.sendNotification("Reminder", "Time to check your glucose levels!")
        return Result.success()
    }
}