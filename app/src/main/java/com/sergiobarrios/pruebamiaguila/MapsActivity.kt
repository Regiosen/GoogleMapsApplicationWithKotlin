package com.sergiobarrios.pruebamiaguila

import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import kotlin.math.round



class MapsActivity : AppCompatActivity(), OnMapReadyCallback,
    GoogleMap.OnMarkerClickListener{
    override fun onMarkerClick(p0: Marker?) = false

    private lateinit var map: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var lastLocation: Location
    private lateinit var locationCallback: LocationCallback
    private lateinit var velocityText: TextView
    private var listOfRecentLocations = mutableListOf<Location>()
    var listOfRecentLocationMaxCount: Int = 3

    var velocity: Float = 0f

    lateinit var mainHandler: Handler

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        mainHandler = Handler(Looper.getMainLooper())
        velocityText = findViewById(R.id.velocidadAuto) as TextView

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        map.uiSettings.isZoomControlsEnabled = true
        map.setOnMarkerClickListener(this)

        val miAguila = LatLng(4.667426, -74.056624)
        val parqueVirrey = LatLng(4.672655, -74.054071)

        map.addMarker(MarkerOptions().position(miAguila).title("miAguila"))
        map.addMarker(MarkerOptions().position(parqueVirrey).title("parqueVirrey"))


        setUpMap()
    }
    private fun setUpMap() {
        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
            return
        }
        mainHandler.post(object : Runnable {
            override fun run() {
                UpdateCurrentPosition()
                UpdateSpeed()
                mainHandler.postDelayed(this, 3000)
            }
        })
    }
    private fun UpdateCurrentPosition()
    {
        // 1
        map.isMyLocationEnabled = true

        // 2
        fusedLocationClient.lastLocation.addOnSuccessListener(this)
        { location ->
            // Got last known location. In some rare situations this can be null.
            // 3
            if (location != null) {
                lastLocation = location
                if (listOfRecentLocations.size <= 0) {
                    val currentLatLng = LatLng(location.latitude, location.longitude)
                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 13f))
                }
                AddToListOfRecentLocations(lastLocation)
                AddNewPositionMarker()
            }
        }
    }
    private fun AddNewPositionMarker()
    {
        if (lastLocation != null)
        {
            val newLocation = LatLng(lastLocation.latitude, lastLocation.longitude)
            map.addMarker(MarkerOptions().position(newLocation).title("previousUserPosition")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)))
        }
    }
    private fun UpdateSpeed()
    {
        CalculateSpeedWithRecentLocations()
        val newVelocityString = "Velocidad:\n"+velocity.toString()+"m/s"
        velocityText.text = newVelocityString;
    }

    fun AddToListOfRecentLocations(newLocation: Location)
    {
        if (listOfRecentLocations.size < listOfRecentLocationMaxCount) {
            listOfRecentLocations.add(newLocation)
        }
        else
        {
            var i = 0;
            while (i < listOfRecentLocations.size) {
                if (i == listOfRecentLocations.size-1)
                {
                    listOfRecentLocations[i] = newLocation
                }
                else
                {
                    listOfRecentLocations[i] = listOfRecentLocations[i+1]
                }
                i++;
            }
        }
    }
    fun CalculateSpeedWithRecentLocations()
    {
        val distance = FloatArray(2)
        var sumOfDistances = 0f
        var i = 0;
        while (i < listOfRecentLocations.size)
        {
            if (i != listOfRecentLocations.size-1)
            {
                Location.distanceBetween(listOfRecentLocations[i].latitude,
                                         listOfRecentLocations[i].longitude,
                                         listOfRecentLocations[i+1].latitude,
                                         listOfRecentLocations[i+1].longitude,
                                         distance)

                sumOfDistances += distance[0];
            }
            i++;
        }
        if (listOfRecentLocations.size > 0)
            velocity = round((sumOfDistances.div(listOfRecentLocations.size*3))*100)/100
        else
        {
            velocity = 0f
        }
    }
}
