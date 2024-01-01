package com.cleandevelopment.fanficalert

import android.app.Activity
import android.app.Dialog
import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton


class MainActivity : AppCompatActivity() {
    lateinit var dialog: Dialog
    lateinit var deleteDialog: Dialog
    lateinit var data: Array<fanfiction>
    lateinit var list: ListView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if(!isJobSchedulerRunning(this)){
            val jobScheduler = this!!.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
            val jobInfo = JobInfo.Builder(123, ComponentName(this,mainJobService::class.java))
            val job =   jobInfo.setRequiresCharging(false)
                .setPeriodic(15*60*1000)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .build()
            jobScheduler.schedule(job)
        }


        var db = dbHandler(this)
        db.onCreate(db.writableDatabase)

        dialog = Dialog(this)
        deleteDialog = Dialog(this)
        list = findViewById(R.id.urls)
        var adapter = setupAdapter(db)
        list.adapter = adapter

        list.setOnItemClickListener(){ parent, view, position, id ->
            val webpage: Uri = Uri.parse(data[position].url)
            val intent = Intent(Intent.ACTION_VIEW, webpage)
            intent.setPackage("com.android.chrome");
            //if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
            //}
            true
        }

        list.setOnItemLongClickListener() { parent, view, position, id ->
            showDeleteDialog(view,db,position,list)
            true
        }
        var FAB: FloatingActionButton = findViewById(R.id.addFic)
        FAB.setOnClickListener() {
            showDialog(this, db)

        }

    }

    private fun setupAdapter(db: dbHandler): fanficAdapter {
        var fics = db.read()
        data = fics.toTypedArray()
        var adapter = fanficAdapter(this, data)
        return adapter
    }

    public fun showDialog(view: Activity, db: dbHandler) {
        dialog.setContentView(R.layout.add_dialog)
        var close: Button = dialog.findViewById(R.id.cancle)
        var add: Button = dialog.findViewById(R.id.add)
        var titleField: EditText = dialog.findViewById(R.id.title)
        var urlField: EditText = dialog.findViewById(R.id.url)
        var chapterField: EditText = dialog.findViewById(R.id.chapter)

        close.setOnClickListener() {
            dialog.dismiss()
        }

        add.setOnClickListener() {
            var title = titleField.text.toString()
            var url = urlField.text.toString()
            var chapter = chapterField.text.toString().toInt()
            db.insert(title, url, chapter)
            var adapter = setupAdapter(db)
            list.adapter = adapter
            dialog.dismiss()
        }
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
    }

    public fun showDeleteDialog(v: View, db: dbHandler, pos: Int, list: ListView) {
        deleteDialog.setContentView(R.layout.write_delete)
        val deleteButton: Button = deleteDialog.findViewById(R.id.deleteButton)


        deleteButton.setOnClickListener() {
            var id = data[pos].id
            db.delete(id)

            var data_clear = data.toMutableList()
            data_clear.clear()
            data = data_clear.toTypedArray()
            var adapter = fanficAdapter(this, data)
            list.adapter = adapter
            adapter = setupAdapter(db)
            list.adapter = adapter
            deleteDialog.dismiss()
        }
        deleteDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        deleteDialog.show()
    }

    fun isJobSchedulerRunning(context: Context): Boolean {
        val jobScheduler = context.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
        return jobScheduler.allPendingJobs.size > 0
    }
}