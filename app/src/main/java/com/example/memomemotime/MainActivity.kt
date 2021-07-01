package com.example.memomemotime

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
//import androidx.appcompat.app.ActivityCompat
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng

class MainActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var mMap: GoogleMap
    private val MY_PERMISSION_REQUEST_ACCESS_FINE_LOCATION = 1

    private lateinit var fusedLacoationProviderClient : FusedLocationProviderClient
    private lateinit var lastLocation : Location
    private var locationCallback : LocationCallback? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // 画面をスリープにしない
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map)
        mapFragment.getMapAsync(this)
        fusedLacoationProviderClient = LocationServices.getFusedLocationProviderClient(this)
    }
    
    override fun onMapReady(googleMap: GoogleMap){
        mMap = googleMap

        checkPermission()
    }
    
    private fun checkPermission() {
        if (ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED){
            myLocationEnable()
        } else {
            requestLocationPermission()
        }
    }

    private fun requestLocationPermission() {
        if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION)) {
            // 許可を求め拒否されていた場合
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), MY_PERMISSION_REQUEST_ACCESS_FINE_LOCATION)

        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), MY_PERMISSION_REQUEST_ACCESS_FINE_LOCATION)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            MY_PERMISSION_REQUEST_ACCESS_FINE_LOCATION->{
                if (permissions.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    myLocationEnable()
                } else {
                    showToast("現在値値は表示できません")
                }
            }
        }
    }

    private fun myLocationEnable(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){
            mMap.isMyLocationEnabled = true
            val locationRequest = LocationRequest().apply {
                interval = 10000
                fastestInterval = 5000
                priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            }
            locationCallback = object: LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult?){
                    if(locationResult?.lastLocation != null) {
                        lastLocation = locationResult?.lastLocation
                        val currentLatLng = LatLng(lastLocation.latitude,lastLocation.longitude)
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLatLng))
                        textView.text = "Lat:${lastLocation.latitude}, Lng:${lastLocation.longitude}"
                        
                    }
                }
            }
            fusedLocationProviderClient.requestLocationUpdates(locationRequest,locationCallback)
        }
    }

    private fun showToast(msg: String) {
        val toast = Toast.makeText(this,msg,Toast.LENGTH_LONG)
        toast.show()
    }
}