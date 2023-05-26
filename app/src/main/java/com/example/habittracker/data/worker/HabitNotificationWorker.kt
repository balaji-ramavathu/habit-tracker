package com.example.habittracker.data.worker

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.habittracker.R
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
        val builder = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.baseline_arrow_upward_black_24dp)
            .setContentTitle("Notification Title")
            .setContentText("Notification Content")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        with(NotificationManagerCompat.from(applicationContext)) {
            if (ActivityCompat.checkSelfPermission(
                    applicationContext,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return
            }

            val uniqueId = "" // Replace with your unique identifier
            val timestamp = System.currentTimeMillis()
            val notificationId = uniqueId.hashCode() + timestamp.toInt()
            notify(notificationId, builder.build())
        }
    }


//    private fun showNotification(context: Context, habitId: Int, habitName: String?) {
//        // Create an intent to open the habit details screen when the notification is clicked
//        val intent = Intent(context, MainActivity::class.java)
//        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//        val pendingIntent = PendingIntent.getActivity(
//            context,
//            habitId,
//            intent,
//            PendingIntent.FLAG_UPDATE_CURRENT
//        )
//
//        // Create the notification
//        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
//            .setContentTitle("Habit Reminder")
//            .setContentText(habitName)
////            .setSmallIcon(R.drawable.notification_icon)
//            .setContentIntent(pendingIntent)
//            .setAutoCancel(true)
//            .build()
//
//        // Show the notification using the NotificationManager
//        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE)
//                as NotificationManager
//        notificationManager.notify(habitId, notification)
//    }
//
//    companion object {
//        private const val HABIT_ID_KEY = "habit_id"
//        private const val HABIT_NAME_KEY = "habit_name"
//        private const val CHANNEL_ID = "habit_channel"
//
//        fun createInputData(habit: Habit): Data {
//            return Data.Builder()
//                .putInt(HABIT_ID_KEY, habit.id)
//                .putString(HABIT_NAME_KEY, habit.name)
//                .build()
//        }
//
//        fun enqueueNotification(context: Context, habitId: Int, habitName: String?, delay: Long) {
//            val data = Data.Builder()
//                .putInt(HABIT_ID_KEY, habitId)
//                .putString(HABIT_NAME_KEY, habitName)
//                .build()
//
//            val constraints = Constraints.Builder()
//                .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
//                .build()
//
//            val notificationWorkRequest = OneTimeWorkRequestBuilder<HabitNotificationWorker>()
//                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
//                .setInputData(data)
//                .setConstraints(constraints)
//                .build()
//
//            WorkManager.getInstance(context).enqueue(notificationWorkRequest)
//
//        }
//    }
}
