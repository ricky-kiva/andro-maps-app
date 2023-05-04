package com.rickyslash.googlemapsapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.rickyslash.googlemapsapp.databinding.ActivityMapsBinding

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
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
        mMap = googleMap

        // Add a marker in Sydney and move the camera
        val sydney = LatLng(-34.0, 151.0)
        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true
    }
}

// Google Maps: key feature is marking location (Marker), make route (Polyline), make area (Circle), etc
// Location Tracker: knowing accurate location efficiently using Fused Location Provider from Google Play Service Location
// - Feature: set new location in certain interval (Running App)
// GeoFencing: Define radius to give notification in certain location using Google Play Service Location

// Can get Google Maps API for Android using this link:
// - https://console.developers.google.com/flows/enableapi?apiid=maps_android_backend&keyType=CLIENT_SIDE_ANDROID&r={SHA1}%3B{com.packagename.appname}
// --- change {SHA1} & {com.packagename.appname}. Remove the curly braces `{}` also
// --- to see SHA1, run: `./gradlew signingReport`

// 2 ways of showing map:
// - using SupportMapFragment. Benefit is don't bother to set map's lifecycle manually
// - using MapView to display map in Object View. All lifecycle function need to be overridden to prevent memory leak:
// --- onCreate(), onStart(), onResume(), onPause(), onStop(), onDestroy(), onSaveInstanceState(), onLowMemory()

// Things to code in maps:
// - Map's type & style
// - Data selection. Data that need to be shown
// - Add Marker
// - Configure zoom & animation
// - Response user's action (pinch, drag, or click)

// Maps type: Normal (focus on data, road, & river), Satellite, Terrain (topography), Hybrid (satellite & location data)
// Zoom level: 1 (all world), 5 (continent & islands), 10 (city), 15 (roads), 20 (buildings)
// Map UI Control:
// - Zoom Control (+/-)
// - Indoor Level Picker (set building level to be shown)
// - Compass
// - Toolbar (Navigation, Google Maps link, etc)
// - My Location (to show user location)

// POI (Point of Interest) is location data that is identified with Lat/Long