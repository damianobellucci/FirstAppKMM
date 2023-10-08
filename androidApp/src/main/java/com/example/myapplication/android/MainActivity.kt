package com.example.myapplication.android

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.net.wifi.ScanResult
import android.net.wifi.WifiManager
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.time.Instant
import java.time.format.DateTimeFormatter
import kotlin.math.abs


class MainActivity : ComponentActivity() {
    private val updateScanWiFiFrequency: Long = 10000
    private var okPermissions: Boolean = false
    val context = this
    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("WiFiScan", "started application.")

        fun wifiScan(): MutableList<ScanResult>? {
            val wifiManager: WifiManager =
                applicationContext.getSystemService(WIFI_SERVICE) as WifiManager;
            var scannedWifi = wifiManager.scanResults;
            scannedWifi.sortBy { el-> abs(el.level) }
            //scannedWifi = scannedWifi.take(10)
            scannedWifi.forEach { el -> Log.d("WiFiScan", "${el.wifiSsid}, ${el.level}");}
            return scannedWifi
        }

        fun updateListView(wifiList:  MutableList<ScanResult>?) {
            setContentView(R.layout.activity_main)

            // getting the recyclerview by its id
            val recyclerview = findViewById<RecyclerView>(R.id.recyclerview)

            // this creates a vertical layout Manager
            recyclerview.layoutManager = LinearLayoutManager(this)

            // ArrayList of class ItemsViewModel
            val data = ArrayList<ItemsViewModel>()

            // This loop will create 20 Views containing
            // the image with the count of view
            wifiList?.forEach { el -> data.add(ItemsViewModel(1, el.wifiSsid.toString())) }

            // This will pass the ArrayList to our Adapter
            val adapter = CustomAdapter(data)
            adapter.update(data)

            // Setting the Adapter with the recyclerview
            recyclerview.adapter = adapter
        }

        fun checkPermissions(){
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                Log.d("WiFiScan", "entered ask permissions.");
                val locationPermissionRequest = registerForActivityResult(
                    ActivityResultContracts.RequestMultiplePermissions()
                ) { permissions ->
                    when {
                        permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                            Log.d("WiFiScan", "Precise location access granted.");
                            updateListView(wifiScan())
                            val handler = Handler()
                            val runnable: Runnable = object : Runnable {
                                override fun run() {
                                    updateListView(wifiScan())
                                    Log.d("WiFiScan", "Iteration")
                                    handler.postDelayed(this, 10000)
                                }
                            }
                            handler.postDelayed(runnable, this.updateScanWiFiFrequency)
                        }
                        permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                            Log.d("WiFiScan", "Approximate location access granted.");
                        } else -> {
                        Log.d("WiFiScan", "No location access granted.");
                    }
                    }
                }
                locationPermissionRequest.launch(arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION))
            }
            else {
                updateListView(wifiScan())
                val handler = Handler()
                val runnable: Runnable = object : Runnable {
                    override fun run() {
                        updateListView(wifiScan())
                        Log.d("WiFiScan", "Iteration")
                        handler.postDelayed(this, 10000)
                    }
                }
                handler.postDelayed(runnable, this.updateScanWiFiFrequency)
            }
        }

        checkPermissions()


    }


}




@Composable
fun GreetingView(text: String) {
    Text(text = text)
}

@Preview
@Composable
fun DefaultPreview() {
    MyApplicationTheme {
        GreetingView("Hello, Android!")
    }
}
