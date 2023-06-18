/*
 * MIT License
 *
 * Copyright (c) 2017 Yuriy Budiyev [yuriy.budiyev@yandex.ru]
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.example.mobilnysysteminwentaryzacji

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.budiyev.android.codescanner.*


private const val CAMERA_REQUEST_CODE = 101

class MainActivity : AppCompatActivity() {

    private lateinit var codeScanner: CodeScanner
    private lateinit var builder: AlertDialog.Builder //stworzenie AlertDialog z opóźnioną inicjacją
    private var qrScans: ArrayList<String>? = ArrayList() //tablica do przechowywania kodów (tekstowych)
    private var inventory: ArrayList<String> = arrayListOf("Półka mała", "Półka średnia", "Półka duża") //tablica do przechowywania towarów (lista z którą porównujemy kody qr), moja baza danych

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val scannerView = findViewById<CodeScannerView>(R.id.scanner_view)
        val textView = findViewById<TextView>(R.id.tv_textView)

        setupPermissions() //przy uruchomieniu sprawdza czy nadano uprawnienia
        builder = AlertDialog.Builder(this)

        //private fun codeScanner() {}

        codeScanner = CodeScanner(this, scannerView)

        codeScanner.apply {
            camera = CodeScanner.CAMERA_BACK
            formats = CodeScanner.ALL_FORMATS

            autoFocusMode = AutoFocusMode.SAFE
            scanMode =
                ScanMode.SINGLE //CONTINUOUS-Continuous scan, don't stop preview after decoding the code; SINGLE-Preview will stop after first decoded code
            isAutoFocusEnabled = true
            isFlashEnabled = false

            decodeCallback = DecodeCallback {
                runOnUiThread {
                    builder.setTitle(getText(R.string.alert))
                        .setMessage(getText(R.string.add_item_to_list))
                        .setCancelable(true)
                        .setPositiveButton(getText(R.string.yes)) { dialogInterface, it1 ->
                            qrScans?.add(it.text) // dodaje na koniec listy zeskanowany tekst z kodu
                        }
                        .setNegativeButton(getText(R.string.no)) { dialogInterface, it1 ->
                            dialogInterface.cancel()
                        }
                        //show the builder
                        .show()

                    // qrScans?.add(it.text) // dodaje na koniec listy zeskanowany tekst z kodu
                    textView.text = it.text //wyświetla zeskanowany kod w textView
                }
            }

            errorCallback = ErrorCallback {
                runOnUiThread {
                    Log.e("Main", "Camera initialization error: ${it.message}")
                }
            }

            scannerView.setOnClickListener {//jesli ScanMode jest w trybie SINGLE, to po kliknieciu na ekran zaczyna skanowac
                textView.text = getText(R.string.scan_something)
                codeScanner.startPreview()
            }
        }

        //przycisk do wyświetlania listy towarów
        findViewById<Button>(R.id.button_1)
            .setOnClickListener {
                //Log.d("BUTTONS", "User tapped the button_1")
                val intent = Intent(this, ListItemsActivity::class.java)
                intent.putStringArrayListExtra("qrScans", qrScans)
                startActivity(intent)
            }
    }

    override fun onResume() {
        super.onResume()
        codeScanner.startPreview() //jesli wyjdziesz z aplikacji i wejdziesz ponownie to spróbuje pobrac nowy QR kod
    }

    override fun onPause() {
        codeScanner.releaseResources() //zwolnienie zaalokowanej pamięci po wyjściu z aplikacji
        super.onPause()
    }

    private fun setupPermissions() { //sprawdza czy jest pozwolenie na używanie aparatu
        val permission = ContextCompat.checkSelfPermission(this,
        android.Manifest.permission.CAMERA)

        if (permission != PackageManager.PERMISSION_GRANTED) {
            makeRequest()
        }
    }

    private fun makeRequest() { //żądza pozwolenia na używanie aparatu
        ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.CAMERA),
        CAMERA_REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            CAMERA_REQUEST_CODE -> {
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, getText(R.string.camera_permission), Toast.LENGTH_SHORT).show()
                } else {
                    //successful Użytkownik zgodził się na użycie aparatu.
                }
            }
        }
    }

}