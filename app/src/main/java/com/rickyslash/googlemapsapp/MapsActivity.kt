package com.rickyslash.googlemapsapp

import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.rickyslash.googlemapsapp.databinding.ActivityMapsBinding
import android.Manifest
import android.content.res.Resources
import com.google.android.gms.maps.model.MapStyleOptions

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

        /* // Add a marker in Sydney and move the camera
        val sydney = LatLng(-34.0, 151.0)
        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))*/

        // set new marker
        val locUGM = LatLng(-7.770717,110.377724)
        mMap.addMarker(
            MarkerOptions()
                .position(locUGM)
                .title("Universitas Gadjah Mada")
                .snippet(" Bulaksumur, Sleman, DIY")
        )
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(locUGM, 15f))

        // set UI components to be shown
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true

        // new Marker on map click-hold
        mMap.setOnMapLongClickListener { latLng ->
            mMap.addMarker(
                MarkerOptions()
                    .position(latLng)
                    .title("New Marker")
                    .snippet("Lat: ${limitDoubleToString(latLng.latitude)}, Long: ${limitDoubleToString(latLng.longitude)}")
                    .icon(vectorToBitmap(R.drawable.baseline_person_pin_24, Color.parseColor("#E53935")))
            )
        }

        // Marker when POI selected
        mMap.setOnPoiClickListener { poi ->
            val poiMarker = mMap.addMarker(
                MarkerOptions()
                    .position(poi.latLng)
                    .title(poi.name)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
            )
            poiMarker?.showInfoWindow()
        }

        getMyLocation()
        setMapStyle()
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            getMyLocation()
        }
    }

    private fun getMyLocation() {
        // check permission
        if (ContextCompat.checkSelfPermission(
                this.applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // set map UI component
            mMap.isMyLocationEnabled = true
        } else {
            // request permission when insufficient permission
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun setMapStyle() {
        try {
            val success = mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style))
            if (!success) {
                Log.e(TAG, "Style parsing failed.")
            }
        } catch (e: Resources.NotFoundException) {
            Log.e(TAG, "Ca't find style. Error:", e)
        }
    }

    private fun vectorToBitmap(@DrawableRes id: Int, @ColorInt color: Int): BitmapDescriptor {
        // load vector from drawable resource
        val vectorDrawable = ResourcesCompat.getDrawable(resources, id, null)
        if (vectorDrawable == null) {
            Log.e("BitmapHelper", "Resource not found")
            return BitmapDescriptorFactory.defaultMarker()
        }

        // creates new Bitmap with same size as vectorDrawable & configuration of ARGB_8888
        val bitmap = Bitmap.createBitmap(
            vectorDrawable.intrinsicWidth,
            vectorDrawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )

        // make canvas to draw vector onto Bitmap object
        val canvas = Canvas(bitmap)
        // set bounds of vector drawable to match the size of the canvas
        vectorDrawable.setBounds(0, 0, canvas.width, canvas.height)
        // apply color to vectorDrawable
        DrawableCompat.setTint(vectorDrawable, color)
        // draw vector drawable to canvas
        vectorDrawable.draw(canvas)
        // convert bitmap to BitmapDescriptor
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

    private fun limitDoubleToString(d: Double): String {
        return if (d.isNaN() || d.isInfinite()) {
            d.toString()
        } else {
            d.toString().substring(0, minOf(6, d.toString().length))
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.map_options, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.normal_type -> {
                mMap.mapType = GoogleMap.MAP_TYPE_NORMAL
                true
            }
            R.id.satellite_type -> {
                mMap.mapType = GoogleMap.MAP_TYPE_SATELLITE
                true
            }
            R.id.terrain_type -> {
                mMap.mapType = GoogleMap.MAP_TYPE_TERRAIN
                true
            }
            R.id.hybrid_type -> {
                mMap.mapType = GoogleMap.MAP_TYPE_HYBRID
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    companion object {
        val TAG = MapsActivity::class.java.simpleName
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

// Google Map styling can be done here:
// - https://mapstyle.withgoogle.com/

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