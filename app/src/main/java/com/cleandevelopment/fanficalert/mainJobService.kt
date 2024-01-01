package com.cleandevelopment.fanficalert

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.job.JobParameters
import android.app.job.JobService
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import android.system.Os.link
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import org.jsoup.Jsoup


class mainJobService : JobService() {


    override fun onStartJob(p0: JobParameters?): Boolean {
        Log.e("OnstartCommand Called", "Onstart has been Called")
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


        var db = dbHandler(this)
        Log.e("OnstartCommand Called", "Onstart has been Called")
        var fics = db.read()

        object : AsyncTask<Void, Void, Boolean>() {
            override fun onPreExecute() {
                super.onPreExecute()
            }

            @SuppressLint("StaticFieldLeak")
            override fun doInBackground(vararg params: Void?): Boolean {
                //do something
                checkFics(p0, db, fics)
                return true
            }

            override fun onPostExecute(result: Boolean?) {
                jobFinished(p0, false)

            }

        }.execute()

        return true
    }

    override fun onStopJob(p0: JobParameters?): Boolean {
        return false
    }


    private fun getData(url: String): String {
        val userAgent = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.152 Safari/537.36"
        val doc = Jsoup.connect("$url").timeout(5000).userAgent(userAgent).ignoreContentType(true).get()
        //Log.e("Connected?", "Yes")
        var purl = url.replace("https://", "")
        var site = purl[5]
        //Log.d("url","$site $purl")
        //Log.d("site", site.toString())
        var chap = "0"
        if(site.toString() == "v") {
            var chapters = doc.getElementsByClass("chapters")
            chap = chapters[1].text()
            chap = chap.split('/')[0]
        }
        if (site.toString() == "w"){
            var info = doc.getElementsByClass("story-stats hidden-xxs")
            var details = info[0].child(2).child(1).child(0).child(0).text()
            //Log.d("chap","$details")
            chap = details
        }
        if(site.toString() == "f"){
            var info = doc.getElementsByClass("no-wrap")
            var detail = info[0].child(1).text()
            chap = detail
        }
        return chap
    }

    @SuppressLint("WrongConstant")
    fun checkFics(params: JobParameters?, db: dbHandler, fics: ArrayList<fanfiction>){
        try {
            //Log.e("Tread Running", "YES")
            fics.forEach() {
                var chapter = getData(it.url)
                //Log.e("Chapter", "$chapter")
                if (chapter.toInt() != it.chapter) {
                    Log.e("Update", "Yes")
                    val notificationIntent = Intent(Intent.ACTION_VIEW)
                    notificationIntent.data = Uri.parse(it.url)

                    val pending = PendingIntent.getActivity(this,0,notificationIntent,
                        FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    )

                    var builder = NotificationCompat.Builder(baseContext, "CHANNEL_ID")
                            .setSmallIcon(R.drawable.ic_stat_name)
                            .setContentIntent(pending)
                            .setAutoCancel(true)
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
        jobFinished(params, false)
    }

}


