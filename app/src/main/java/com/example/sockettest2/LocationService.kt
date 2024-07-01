package com.example.sockettest2

import android.Manifest
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.os.IBinder
import android.util.Log
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*
import io.socket.client.IO
import io.socket.client.Socket
import kotlinx.coroutines.*

class LocationService : Service() {

    private lateinit var socket: Socket
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val scope = CoroutineScope(Dispatchers.IO + Job())

    private val TAG = "LocationService"

    override fun onCreate() {
        super.onCreate()
        // Инициализация FusedLocationProviderClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            val someData = it.getStringExtra("host")

            try {
                socket = IO.socket(someData.toString())
                socket.connect()
            }
            catch (e: Exception) {
                Log.d(TAG, "Error ${e.message}")
            }

            startSendingLocation()
        }

        return START_STICKY
    }

    private fun startSendingLocation() {
        scope.launch {
            while (isActive) {
                if (ContextCompat.checkSelfPermission(this@LocationService, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                        location?.let {
                            val message = "${it.latitude},${it.longitude}"
                            socket.emit("location", message)
                        }
                    }
                }
                delay(5000)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        socket.disconnect()
        scope.cancel()
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }
}