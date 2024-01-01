package com.cleandevelopment.fanficalert

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.app.job.JobScheduler
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import org.jsoup.Jsoup


class mainService:Service() {


    override fun onBind(p0: Intent?): IBinder? {
        //Log.e("OnBind Called", "Onstart has been Called")


        return null
    }

    override fun onStart(intent: Intent?, startId: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create the NotificationChannel
            val name = getString(R.string.channel_name)
            val descriptionText = "description"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val mChannel = NotificationChannel("CHANNEL_ID", name, importance)
            mChannel.description = descriptionText
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(mChannel)
        }

    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        var db = dbHandler(this)
        Log.e("OnstartCommand Called", "Onstart has been Called")
        var fics = db.read()
        Thread(Runnable {
            try {
                //Log.e("Tread Running", "YES")
                fics.forEach() {
                    var chapter = getData(it.url)
                    Log.e("Chapter", "$chapter")
                    if (chapter.toInt() != it.chapter) {
                        Log.e("Update", "Yes")
                        var builder = NotificationCompat.Builder(this, "CHANNEL_ID")
                            .setSmallIcon(R.drawable.ic_launcher_foreground)
                            .setContentTitle("Fanfiction has been updated.")
                            .setContentText("${it.title} now has $chapter chapters")
                            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        with(NotificationManagerCompat.from(baseContext)) {
                            // notificationId is a unique int for each notification that you must define
                            notify(it.id, builder.build())
                        }
                        db.update(db.writableDatabase, it.id, chapter.toInt())
                    }
                }


            } catch (e: Exception) {
                //Log.e("Error", "True")
                //e.printStackTrace()
            }
        }).start()


        return START_NOT_STICKY
    }

    override fun onCreate() {
        super.onCreate()
    }

    private fun getData(url: String) :String{
        val userAgent = "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.87 Safari/537.36"
        val doc = Jsoup.connect("$url").timeout(5000).userAgent(userAgent).get()
        //Log.e("Connected?", "Yes")
        var chapters = doc.getElementsByClass("chapters")
        var chap =chapters[1].text()
        chap = chap.split('/')[0]

        return chap
    }



}


