package com.example.habittracker.data.worker

import android.Manifest
import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.habittracker.MainActivity
import com.example.habittracker.R
import com.example.habittracker.data.Constants.REMINDER_INTENT_DATA_HABIT_COMPLETED
import com.example.habittracker.data.Constants.REMINDER_INTENT_DATA_HABIT_DATE
import com.example.habittracker.data.Constants.REMINDER_INTENT_DATA_HABIT_ID
import com.example.habittracker.data.Constants.REMINDER_INTENT_DATA_NOTIFICATION_ID
import com.example.habittracker.data.Constants.REMINDER_NO_ACTION_INTENT
import com.example.habittracker.data.Constants.REMINDER_YES_ACTION_INTENT
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class HabitNotificationWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    companion object {
        private const val CHANNEL_ID = "notification_channel"
    }

    override suspend fun doWork(): Result = withContext(Dispatchers.Default) {

        createNotificationChannel()
        showNotification()

         Result.success()
    }

    private fun createNotificationChannel() {
        val name = "Channel Name"
        val descriptionText = "Channel Description"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
            description = descriptionText
        }


        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    private fun showNotification() {

        val habitId = inputData.getInt("habitId", 0)
        val habitName = inputData.getString("habitName")
        val habitDate = inputData.getLong("habitDate", 0L)
        val timestamp = System.currentTimeMillis()
        val notificationId = habitId.hashCode() + timestamp.toInt()

        val intent = Intent(applicationContext, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            habitId,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val yesIntent = Intent(REMINDER_YES_ACTION_INTENT).apply {
            putExtra(REMINDER_INTENT_DATA_HABIT_ID, habitId)
            putExtra(REMINDER_INTENT_DATA_HABIT_DATE, habitDate)
            putExtra(REMINDER_INTENT_DATA_HABIT_COMPLETED, true)
            putExtra(REMINDER_INTENT_DATA_NOTIFICATION_ID, notificationId)
        }

        val noIntent = Intent(REMINDER_NO_ACTION_INTENT).apply {
            putExtra(REMINDER_INTENT_DATA_HABIT_ID, habitId)
            putExtra(REMINDER_INTENT_DATA_HABIT_DATE, habitDate)
            putExtra(REMINDER_INTENT_DATA_HABIT_COMPLETED, false)
            putExtra(REMINDER_INTENT_DATA_NOTIFICATION_ID, notificationId)
        }

        val yesPendingIntent = PendingIntent.getBroadcast(
            applicationContext,
            habitId,
            yesIntent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val noPendingIntent = PendingIntent.getBroadcast(
            applicationContext,
            habitId,
            noIntent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.baseline_arrow_upward_black_24dp)
            .setContentTitle("Reminder: $habitName")
            .setContentText("Did you complete your $habitName today?")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .addAction(0, "Yes", yesPendingIntent)
            .addAction(0, "No", noPendingIntent)

        with(NotificationManagerCompat.from(applicationContext)) {
            if (ActivityCompat.checkSelfPermission(
                    applicationContext,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                val notificationPermissionRequestCode = 1001
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    ActivityCompat.requestPermissions(
                        applicationContext as Activity,
                        arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                        notificationPermissionRequestCode
                    )
                }
                return
            }


            notify(notificationId, builder.build())
        }
    }
}
