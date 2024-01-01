 package com.cleandevelopment.fanficalert

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings.Global.getString
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import java.util.*

class StartUpReceiver:BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        //Log.d("has boot?", "YES")

        val jobScheduler = context!!.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
        val jobInfo = JobInfo.Builder(123, ComponentName(context,mainJobService::class.java))
        val job =   jobInfo.setRequiresCharging(false)
                .setPeriodic(15*60*1000)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .build()
        jobScheduler.schedule(job)


//        Toast.makeText(context, " chapter", Toast.LENGTH_SHORT).show()
//        val am = context!!.getSystemService(Context.ALARM_SERVICE) as AlarmManager
//        val i = Intent(context, mainService::class.java)
//        val minutes = 1
//        val calendar: Calendar = Calendar.getInstance()
//        calendar.setTimeInMillis(System.currentTimeMillis())
//        calendar.add(Calendar.SECOND, 3)
//        val pi = PendingIntent.getService(context, 0, i, 0)
//
//        am.setInexactRepeating(AlarmManager.RTC_WAKEUP,
//                calendar.timeInMillis, (
//                minutes * 60 * 1000).toLong(), pi)
//        Log.e("Service Started", "Halilujia")


    }

}