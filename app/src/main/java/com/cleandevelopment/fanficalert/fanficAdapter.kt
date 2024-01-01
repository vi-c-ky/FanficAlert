package com.cleandevelopment.fanficalert

import android.app.Activity
import android.graphics.Paint
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.fragment.app.FragmentActivity

class fanficAdapter(private val context: Activity?, private val title: Array<fanfiction>)
: ArrayAdapter<fanfiction>(context!!, R.layout.fanfic_row, title) {

    override fun getView(position: Int, view: View?, parent: ViewGroup): View {
        val inflater = context?.layoutInflater
        val rowView = inflater?.inflate(R.layout.fanfic_row, null, true)

        val nameText = rowView?.findViewById(R.id.ficTitle) as TextView
        val dateText = rowView?.findViewById(R.id.link) as TextView


        nameText.text = title[position].title
        dateText.text = title[position].url


        return rowView
    }
}