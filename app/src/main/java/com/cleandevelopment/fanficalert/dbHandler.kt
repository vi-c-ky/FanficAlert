package com.cleandevelopment.fanficalert

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.icu.text.CaseMap
import android.widget.Toast


val DATABASE_NAME = "fanfic_alert"
val TABLE_NAME = "fanfics"
val COL_ID = "_ID"
val COL_TITLE = "Title"
val COL_URL = "URL"
val COL_CHAP = "Chapters"

class dbHandler(var context:Context): SQLiteOpenHelper(
        context, DATABASE_NAME, null,
        1) {

    override fun onCreate(db: SQLiteDatabase?) {
        val createTable = "CREATE TABLE IF NOT EXISTS $TABLE_NAME($COL_ID INTEGER PRIMARY KEY AUTOINCREMENT,$COL_TITLE VARCHAR(256),$COL_URL VARCHAR(256),$COL_CHAP INTEGER)"
        db?.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, p1: Int, p2: Int) {
        if (db != null) {
            db.execSQL("DROP TABLE IF EXISTS" + TABLE_NAME)
        }
        onCreate(db)
    }

    fun insert(title:String,url:String,chapter:Int){
        val database = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(COL_TITLE,title)
        contentValues.put(COL_URL,url)
        contentValues.put(COL_CHAP,chapter)

        val result = database.insert(TABLE_NAME, null, contentValues)

        if (result == (0).toLong()) {
            Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show()
        }
    }

    fun read():ArrayList<fanfiction>{
        val list: ArrayList<fanfiction> = ArrayList()
        val db = this.readableDatabase
        val query = "Select * from ${TABLE_NAME}"
        val result = db.rawQuery(query, null)
        if (result.moveToFirst()) {
            do {
                val id= result.getInt(result.getColumnIndex(COL_ID))
                val title = result.getString(result.getColumnIndex(COL_TITLE))
                val url= result.getString(result.getColumnIndex(COL_URL))
                val chap= result.getInt(result.getColumnIndex(COL_CHAP))
                val item = fanfiction(title, url, chap, id)
                list.add(item)
            } while (result.moveToNext())
        }
        return list
    }

    fun update(db:SQLiteDatabase,id:Int,chap:Int){
        val args = ContentValues()
        args.put(COL_CHAP,chap)
        db.update("$TABLE_NAME", args, String.format("%s = ?", COL_ID), arrayOf(id.toString()))
    }

    fun delete(rowId: Int): Boolean {
        val db = this.writableDatabase
        return db.delete(TABLE_NAME, COL_ID.toString() + "=" + rowId, null) > 0
    }
}