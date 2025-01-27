package com.example.diabetesapp.code

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters

class NotificationWorker(appContext: Context, workerParams: WorkerParameters) : Worker(appContext, workerParams) {
    override fun doWork(): Result {
        val notificationHelper = NotificationHelper(applicationContext)
        notificationHelper.sendNotification("Reminder", "Time to check your glucose levels!")
        return Result.success()
    }
}