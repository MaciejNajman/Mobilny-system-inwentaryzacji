package com.example.mobilnysysteminwentaryzacji

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity


class ListItemsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_items)

        val listView = findViewById<ListView>(R.id.listView)

        if(intent.getStringArrayListExtra("qrScans") != null) {
            val qrCodes: MutableList<String> = intent.getStringArrayListExtra("qrScans")!!

            // Instanciating an array list (you don't need to do this,
            // you already have yours).
            //val qrScans: MutableList<String> = ArrayList()
            //qrScans.add("foo")
            //qrScans.add("bar")
            // This is the array adapter, it takes the context of the activity as a
            // first parameter, the type of list view as a second parameter and your
            // array as a third parameter.
            val arrayAdapter: ArrayAdapter<String> = ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                qrCodes
            )

            listView.adapter = arrayAdapter //nawet wyświetla listę, ale trzeba dodac powiadomienie o dodaniiu
        }
    }
}