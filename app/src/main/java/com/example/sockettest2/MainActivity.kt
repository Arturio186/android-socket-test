package com.example.sockettest2

import android.util.Log
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.Manifest
import android.content.pm.PackageManager
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val el = findViewById<TextView>(R.id.status)

        if (checkLocationPermission()) {
            el.setText("Запущен")
            startLocationService()
        } else {
            requestLocationPermission()
        }
    }

    private fun checkLocationPermission(): Boolean {
        val fineLocationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        val coarseLocationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
        return fineLocationPermission == PackageManager.PERMISSION_GRANTED && coarseLocationPermission == PackageManager.PERMISSION_GRANTED
    }

    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
            LOCATION_PERMISSION_REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                startLocationService()
            } else {
                // Permission denied
            }
        }
    }

    private fun startLocationService() {
        Log.d("LocationService", "Create Intent")
        val intent = Intent(this, LocationService::class.java)
        intent.putExtra("host", "http://31.128.42.41:3001/")
        startService(intent)
    }
}