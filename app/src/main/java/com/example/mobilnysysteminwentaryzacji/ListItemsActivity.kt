package com.example.mobilnysysteminwentaryzacji

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity


class ListItemsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_items)

        val listView = findViewById<ListView>(R.id.listView)

        //dodawanie nagłówka do listy towarów
        val header: View = layoutInflater.inflate(R.layout.header_view, null)
        listView.addHeaderView(header)

        //jesli qrScans nie jest null to przenies dane do qrCodes
        if(intent.getStringArrayListExtra("qrScans") != null) {
            val qrCodes: MutableList<String> = intent.getStringArrayListExtra("qrScans")!!

            // This is the array adapter, it takes the context of the activity as a
            // first parameter, the type of list view as a second parameter and your
            // array as a third parameter.
            val arrayAdapter: ArrayAdapter<String> = ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                qrCodes
            )

            listView.adapter = arrayAdapter
            //Podsumowanie
            //Wyświetla listę, po zeskanowaniu wyswietla alert czy dodac towar do listy. Lista towarów ma nagłówek "Towary".
            //Trzeba dodać porównywanie Inventory (lista towarów w magazynie) z QRScans (lista towarów zeskanowanych) i "Potwierdzono istnienie".
        }
    }
}