package com.example.bank_sampah_app2023

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.location.Geocoder
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.mapbox.geojson.Point
import com.mapbox.maps.EdgeInsets
import com.mapbox.maps.MapView
import com.mapbox.maps.dsl.cameraOptions
import com.mapbox.maps.plugin.gestures.OnMapClickListener
import com.mapbox.maps.plugin.gestures.OnMapLongClickListener
import com.mapbox.maps.plugin.gestures.addOnMapClickListener
import com.mapbox.maps.plugin.gestures.addOnMapLongClickListener
import java.text.DecimalFormat
import java.util.Locale

class MapsAdmin : AppCompatActivity(), OnMapLongClickListener {

    private lateinit var mapView: MapView
    private lateinit var icon: Bitmap
    private lateinit var markerManager: MarkerManager
    private var marker: Marker? = null
    private var customMarker: Marker? = null

    private lateinit var btnBack : FloatingActionButton
    private lateinit var btnAddLok : FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps_admin)
        mapView = findViewById(R.id.mapView)

        btnBack = findViewById(R.id.back_buttonmapsadmin)
        btnBack.setOnClickListener {
            back()
        }

        icon = BitmapUtils.bitmapFromDrawableRes(
            this@MapsAdmin,
            R.drawable.blue_marker_view
        )!!
        val newWidth = 100
        val newHeight = 160
        icon = Bitmap.createScaledBitmap(icon, newWidth, newHeight, false)

        mapView.getMapboxMap().apply {
            setCamera(
                cameraOptions {
                    center(Point.fromLngLat(LONGITUDE, LATITUDE))
                    zoom(12.0)
                }
            )
            getStyle {
                mapView.getMapboxMap().addOnMapClickListener(object : OnMapClickListener {
                    override fun onMapClick(point: Point): Boolean {
                        val location = mapView.getMapboxMap().cameraForCoordinates(
                            listOf(point),
                            EdgeInsets(50.0, 50.0, 50.0, 50.0)
                        )

                        lokasilat = location.center!!.latitude()
                        lokasilong = location.center!!.longitude()
                        val myLocation = Geocoder(applicationContext, Locale.getDefault())
                        val myList = myLocation.getFromLocation(lokasilat, lokasilong, 1)
                        val addressLines: List<String> = if (myList!!.isNotEmpty()) myList[0].getAddressLine(0).split(",") else emptyList()
                        alamat = addressLines.toString()
                        val lat = lokasilat.toString()
                        val long = lokasilong.toString()

                        btnAddLok = findViewById(R.id.fab_add_location)

                        btnAddLok.setOnClickListener {
                            val resultIntent = Intent()
                            resultIntent.putExtra("lokasilat", lat)
                            resultIntent.putExtra("lokasilong", long)
                            resultIntent.putExtra("alamat", alamat)
                            setResult(Activity.RESULT_OK, resultIntent)
                            finish()
                        }

                        marker?.let {
                            markerManager.removeMarker(marker!!)
                        }
                        markerManager = MarkerManager(mapView)
                        marker = markerManager.addMarker(
                            Marker(
                                title = "Lokasi Bank Sampah",
                                snippet = "$alamat \n Latitude: $lokasilat \n Longitude: $lokasilong",
                                icon = icon,
                                position = Point.fromLngLat(lokasilong, lokasilat)
                            )
                        )
                        markerManager.selectMarker(marker!!)

                        return true
                    }
                })

            }
            addOnMapLongClickListener(this@MapsAdmin)
        }
    }

    override fun onMapLongClick(point: Point): Boolean {
        customMarker?.let {
            markerManager.removeMarker(it)
        }

        customMarker = markerManager.addMarker(
            Marker(
                position = point,
                icon = icon,
                title = "Custom marker",
                snippet = "${DecimalFormat("#.#####").format(point.latitude())}, ${DecimalFormat("#.#####").format(point.longitude())}"
            )
        )
        return true
    }

    override fun onDestroy() {
        markerManager.destroy()
        super.onDestroy()
    }

    private fun back() {
        startActivity(Intent(this, MainActivity::class.java).apply {
            putExtra(MainActivity.SIGNIN, "backfrommapsadmin")
        })
    }

    companion object {
        private const val LATITUDE = -0.0352231
        private const val LONGITUDE = 109.331888
        var lokasilat = 0.0
        var lokasilong = 0.0
        var alamat = ""
    }
}