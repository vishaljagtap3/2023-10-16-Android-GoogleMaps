package com.bitcodetech.googlemaps

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.Point
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.bitcodetech.googlemaps.databinding.ActivityMainBinding
import com.bitcodetech.googlemaps.databinding.InfoWindowViewBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolygonOptions

class MainActivity : AppCompatActivity() {

    private lateinit var map: GoogleMap
    private lateinit var puneMarker: Marker
    private lateinit var mumMarker: Marker

    private val markersList = ArrayList<Marker>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        (supportFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment)
            .getMapAsync(MyOnMapReadyCallback())
    }

    private inner class MyOnMapReadyCallback : OnMapReadyCallback {
        override fun onMapReady(googleMap: GoogleMap) {
            mt("Map ready")
            map = googleMap

            initUISettings()
            addMarkers()
            setMapListeners()
            setUpCustomInfoWindow()
            drawShapes()
            setUpMoveCameraListener()

        }
    }

    private fun projection() {
        val projection = map.projection
        val position : Point = projection.toScreenLocation(puneMarker.position)
        mt("${position.toString()}")

        val latLng = projection.fromScreenLocation(position)
        mt("${latLng.toString()}")
    }

    private fun setUpMoveCameraListener() {
        findViewById<Button>(R.id.btnMoveCamera).setOnClickListener {
            //val cameraUpdate = CameraUpdateFactory.zoomIn()
            /*val cameraUpdate = CameraUpdateFactory.newLatLngZoom(
                puneMarker.position,
                18F
            )*/

            val cameraUpdate =
                CameraUpdateFactory.newCameraPosition(
                    CameraPosition.Builder()
                        .target(puneMarker.position)
                        .zoom(19F)
                        .tilt(80F)
                        .bearing(60F)
                        .build()

                )

            //map.moveCamera(cameraUpdate)
            map.animateCamera(
                cameraUpdate,
                5000,
                    object : GoogleMap.CancelableCallback {
                        override fun onCancel() {
                            mt("Animation cancelled")
                        }

                        override fun onFinish() {
                            mt("Animation finished")
                            projection()
                        }
                    }
                )
        }
    }

    private fun drawShapes() {

        val circle = map.addCircle(
            CircleOptions()
                .radius(5000.0)
                .center(puneMarker.position)
                .zIndex(40f)
                .fillColor(Color.argb(90, 255, 0, 0))
                .strokeColor(Color.BLACK)
        )
        //circle.remove()

        val polygon = map.addPolygon(
            PolygonOptions()
                .add(LatLng(21.1458, 79.0882))
                .add(LatLng(22.7196, 75.8577))
                .add(LatLng(20.2961, 85.8245))
                .add(LatLng(21.2514, 81.6296))
                .strokeColor(Color.BLACK)

        )

    }

    private fun setUpCustomInfoWindow() {
        map.setInfoWindowAdapter(MyInfoWindowAdapter())
    }

    private inner class MyInfoWindowAdapter : InfoWindowAdapter {
        override fun getInfoWindow(marker: Marker): View? {
            return null
        }

        override fun getInfoContents(marker: Marker): View? {
            val binding = InfoWindowViewBinding.inflate(layoutInflater)
            binding.img.setImageResource(R.drawable.map_icon)
            binding.txt.text = marker.title
            return binding.root
        }
    }

    private fun setMapListeners() {
        /*map.setOnMapClickListener {

        }*/

        map.setOnMapClickListener(
            object : GoogleMap.OnMapClickListener {
                override fun onMapClick(location: LatLng) {
                    markersList.add(
                        map.addMarker(
                            MarkerOptions()
                                .title("FAv location")
                                .position(location)
                        )!!
                    )
                }
            }
        )

        map.setOnMarkerClickListener(
            object : GoogleMap.OnMarkerClickListener {
                override fun onMarkerClick(marker: Marker): Boolean {
                    mt("Marker clicked: ${marker.title}")
                    return false
                }
            }
        )

        map.setOnInfoWindowClickListener(
            object : GoogleMap.OnInfoWindowClickListener {
                override fun onInfoWindowClick(marker: Marker) {
                    mt("Info window clicked: ${marker.title}")
                }
            }
        )

        map.setOnMarkerDragListener(
            object : GoogleMap.OnMarkerDragListener {
                override fun onMarkerDragStart(marker: Marker) {
                    Log.e("tag", "Drag started ${marker.position}")
                }

                override fun onMarkerDrag(marker: Marker) {
                    Log.e("tag", "Drag: ${marker.position}")
                }

                override fun onMarkerDragEnd(marker: Marker) {
                    Log.e("tag", "Drag end ${marker.position}")
                }
            }
        )
    }

    private fun addMarkers() {
        val puneMarkerOptions = MarkerOptions()
        puneMarkerOptions.position(LatLng(18.5623, 73.9167))
        puneMarkerOptions.title("Pune")
        puneMarkerOptions.snippet("This is Pune")
        puneMarkerOptions.draggable(true)
        puneMarkerOptions.rotation(45F)
        puneMarker = map.addMarker(puneMarkerOptions)!!

        val icon = BitmapDescriptorFactory.fromResource(R.drawable.map_icon)


        mumMarker = map.addMarker(
            MarkerOptions()
                .title("Mumbai")
                .snippet("This is Mum")
                .position(LatLng(19.0760, 72.8777))
                .icon(icon)
        )!!

        //mumMarker.remove()
    }

    @SuppressLint("MissingPermission")
    private fun initUISettings() {
        val uiSettings = map.uiSettings
        uiSettings.isCompassEnabled = true
        uiSettings.isIndoorLevelPickerEnabled = true
        uiSettings.isMapToolbarEnabled = true
        uiSettings.isMyLocationButtonEnabled = true
        uiSettings.isRotateGesturesEnabled = true
        uiSettings.isScrollGesturesEnabled = true
        uiSettings.isScrollGesturesEnabledDuringRotateOrZoom = true
        uiSettings.isTiltGesturesEnabled = true
        uiSettings.isZoomControlsEnabled = true
        uiSettings.isZoomGesturesEnabled = true

        map.mapType = GoogleMap.MAP_TYPE_SATELLITE

        map.isBuildingsEnabled = true
        map.isIndoorEnabled = true
        map.isMyLocationEnabled = true
        map.isTrafficEnabled = true

    }

    private fun mt(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
    }
}