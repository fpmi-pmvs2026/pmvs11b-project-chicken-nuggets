package com.example.movies

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

object ReminderManager {
    fun scheduleReminder(context: Context, movieId: Int, movieTitle: String, releaseDate: String) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        
        val intent = Intent(context, ReminderReceiver::class.java).apply {
            putExtra("MOVIE_ID", movieId.toString())
            putExtra("MOVIE_TITLE", movieTitle)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            movieId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        try {
            val date = sdf.parse(releaseDate)
            if (date != null) {
                val calendar = Calendar.getInstance().apply {
                    time = date
                    // Set to morning of release day, e.g., 9:00 AM
                    set(Calendar.HOUR_OF_DAY, 9)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                }

                if (calendar.timeInMillis > System.currentTimeMillis()) {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        calendar.timeInMillis,
                        pendingIntent
                    )
                    Log.d("ReminderManager", "Scheduled reminder for $movieTitle at ${calendar.time}")
                }
            }
        } catch (e: Exception) {
            Log.e("ReminderManager", "Error scheduling reminder", e)
        }
    }

    fun cancelReminder(context: Context, movieId: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, ReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            movieId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
        Log.d("ReminderManager", "Canceled reminder for movie ID $movieId")
    }
}
