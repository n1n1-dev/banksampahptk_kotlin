package com.example.bank_sampah_app2023

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.location.LocationManagerCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bank_sampah_app2023.adapter.viewholder.BSMapsAdapter
import com.example.bank_sampah_app2023.model.BankSampah
import com.example.bank_sampah_app2023.model.BankSampahMaps
import com.example.bank_sampah_app2023.model.SampahItem
import com.google.android.gms.location.*
import com.google.android.material.chip.Chip
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.dsl.cameraOptions
import kotlin.math.max


class MapsUsersFragment : Fragment(), OnLocationClickListener {

    private lateinit var mapView : MapView
    private lateinit var icon: Bitmap
    private lateinit var markerManager: MarkerManager
    private val LATITUDE = -0.0352231
    private val LONGITUDE = 109.331888
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var lat = 0.0
    private var long = 0.0

    private val firestore = FirebaseFirestore.getInstance()
    private val bsDocumentReference = firestore.collection("banksampah")

    private val bsMaps = mutableListOf<BankSampah>()
    private val bankSampahDataList: MutableList<BankSampahMaps> = mutableListOf()
    private var jarak = 0.0
    private var rekomendasiJarak = 0.0
    private var rekomendasiTerbaik = 0.0
    private var rekomendasiHarga = 0.0

    private lateinit var rvMapsUsers: RecyclerView
    private lateinit var chipTerdekat: Chip
    private lateinit var chipTermahal: Chip
    private lateinit var chipTerbaik: Chip
    private lateinit var fabLok: FloatingActionButton
    private lateinit var adapter: BSMapsAdapter

    private var fusedLocationProvider: FusedLocationProviderClient? = null
    private val locationRequest: LocationRequest = LocationRequest().apply {
        interval = 30
        fastestInterval = 10
        priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
        maxWaitTime = 60
    }

    private var locationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val locationList = locationResult.locations
            if (locationList.isNotEmpty()) {
                val location = locationList.last()
            }
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_maps_users, container, false)
        mapView = view.findViewById(R.id.mapViewUsers)

        fusedLocationProvider = LocationServices.getFusedLocationProviderClient(requireContext())
        checkLocationPermission()

        onMapReady()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        rvMapsUsers =  view.findViewById(R.id.drawerBSMapsRecyclerView)
        chipTerdekat = view.findViewById(R.id.chipTerdekat)
        chipTermahal = view.findViewById(R.id.chipTermahal)
        chipTerbaik = view.findViewById(R.id.chipTerbaik)

        val isLocationEnabled = isLocationEnabled(requireContext())

        chipTerdekat.setOnClickListener {
            if (isLocationEnabled && ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                fusedLocationClient.lastLocation
                    .addOnSuccessListener { location ->
                        if (location != null) {
                            if (chipTerdekat.isChecked) {
                                chipTerdekat.isChecked = true
                                chipTermahal.isChecked = false
                                chipTerbaik.isChecked = false
                                val filteredData = bankSampahDataList.filter { it.rekomendasiJarak >= 50.0 }
                                adapter.updateData(filteredData)
                            }
                            else {
                                val filteredData = bankSampahDataList
                                adapter.updateData(filteredData)
                            }
                        }
                    }
            } else {
                chipTerdekat.isChecked = false
                chipTermahal.isChecked = false
                chipTerbaik.isChecked = false
                Toast.makeText(context, "Izin akses lokasi belum diberikan atau lokasi perangkat belum diaktifkan", Toast.LENGTH_LONG).show()
            }
        }

        chipTermahal.setOnClickListener {
            if (isLocationEnabled && ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                fusedLocationClient.lastLocation
                    .addOnSuccessListener { location ->
                        if (location != null) {
                            if (chipTermahal.isChecked) {
                                chipTerdekat.isChecked = false
                                chipTermahal.isChecked = true
                                chipTerbaik.isChecked = false
                                val bankSampahTertinggi = bankSampahDataList.maxByOrNull { sampahItemList ->
                                    sampahItemList.daftarSampah.sumByDouble { it.rekomendasiHarga }
                                }
                                if (bankSampahTertinggi != null) {
                                    val totalTertinggi =
                                        bankSampahTertinggi.daftarSampah.sumByDouble { it.rekomendasiHarga }
                                    val filteredData = bankSampahDataList.filter { sampahItemList ->
                                        sampahItemList.daftarSampah.sumByDouble { it.rekomendasiHarga } == totalTertinggi
                                    }
                                    adapter.updateData(filteredData)
                                }
                            }
                            else {
                                val filteredData = bankSampahDataList
                                adapter.updateData(filteredData)
                            }
                        }
                    }
            } else {
                chipTerdekat.isChecked = false
                chipTermahal.isChecked = false
                chipTerbaik.isChecked = false
                Toast.makeText(context, "Izin akses lokasi belum diberikan atau lokasi perangkat belum diaktifkan", Toast.LENGTH_LONG).show()
            }
        }

        chipTerbaik.setOnClickListener {
            if (isLocationEnabled && ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                fusedLocationClient.lastLocation
                    .addOnSuccessListener { location ->
                        if (location != null) {
                            if (chipTerbaik.isChecked) {
                                chipTerdekat.isChecked = false
                                chipTermahal.isChecked = false
                                chipTerbaik.isChecked = true
                                val bankSampahTerbaik = bankSampahDataList.maxByOrNull { sampahItemList ->
                                    sampahItemList.daftarSampah.sumByDouble { it.rekomendasiTerbaik }
                                }
                                if (bankSampahTerbaik != null) {
                                    val totalTerbaik =
                                        bankSampahTerbaik.daftarSampah.sumByDouble { it.rekomendasiTerbaik }
                                    val filteredData =
                                        bankSampahDataList.filter { sampahItemList ->
                                            sampahItemList.daftarSampah.sumByDouble { it.rekomendasiTerbaik } == totalTerbaik
                                        }
                                    adapter.updateData(filteredData)
                                }
                            }
                            else {
                                val filteredData = bankSampahDataList
                                adapter.updateData(filteredData)
                            }
                        }
                    }
            } else {
                chipTerdekat.isChecked = false
                chipTermahal.isChecked = false
                chipTerbaik.isChecked = false
                Toast.makeText(context, "Izin akses lokasi belum diberikan atau lokasi perangkat belum diaktifkan", Toast.LENGTH_LONG).show()
            }
        }

        fabLok = view.findViewById(R.id.fabLok)
        fabLok.setOnClickListener {
            getPositionDevice()
        }

        return view
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun onMapReady() {
        bsDocumentReference.get()
            .addOnSuccessListener { querySnapshot: QuerySnapshot? ->
                if (querySnapshot != null) {
                    bsMaps.clear() // Hapus data
                    for (document: QueryDocumentSnapshot in querySnapshot) {
                        val documentData = document.data
                        val idBS = document.id
                        val namaBS = documentData["nama"] as String
                        val legalBS = documentData["legalitas"] as String
                        val lokasiBS = documentData["lokasi"] as GeoPoint
                        val alamatBS = documentData["alamat"] as String

                        // Tambahkan data
                        bsMaps.add(BankSampah(idBS, namaBS, legalBS, lokasiBS, alamatBS))

                        val dataListBS = BankSampahMaps(idBS, namaBS, legalBS, lokasiBS.latitude, lokasiBS.longitude, alamatBS, mutableListOf(), jarak, rekomendasiJarak)
                        bankSampahDataList.add(dataListBS)

                        val targetRef = FirebaseFirestore.getInstance().document("banksampah/$idBS")

                        firestore.collection("sampahbanksampah").whereEqualTo("banksampahRef", targetRef).get()
                            .addOnSuccessListener { querySBS ->
                                if (querySBS.size() > 0) {
                                    for (dataSBS in querySBS) {
                                        val idSBS = dataSBS.id
                                        val sampahRef = dataSBS.get("sampahRef") as DocumentReference
                                        val hargaBeli = dataSBS.get("hargaBeli") as Long

                                        val dataSampah = sampahRef.path
                                        val idSampah = dataSampah.split("/")

                                        firestore.collection("sampah").document(idSampah[1]).get()
                                            .addOnSuccessListener { docSampah ->
                                                if (docSampah != null && docSampah.exists()) {
                                                    val sampahData = docSampah.data
                                                    if (sampahData != null) {
                                                        val nama = sampahData["nama"] as String
                                                        val satuan = sampahData["satuan"] as String

                                                        rekomendasiHarga = tsukamotoHargaRekomendasi(hargaBeli.toDouble())

                                                        val sampahItem = SampahItem(idBS, idSBS, nama, satuan, hargaBeli.toInt(), rekomendasiHarga, rekomendasiTerbaik)
                                                        val foundElement = bankSampahDataList.find { it.idBankSampah == idBS }

                                                        if (foundElement != null) {
                                                            foundElement.daftarSampah.add(sampahItem)
                                                            for (data in foundElement.daftarSampah) {
                                                                data.rekomendasiTerbaik = rekomendasi(foundElement.jarakBankSampah, data.hargaBeli.toDouble())
                                                                println("terbaik: ${data.rekomendasiTerbaik} jarak: ${foundElement.jarakBankSampah} hargabeli: ${data.hargaBeli.toDouble()}")
                                                            }
                                                            adapter.notifyDataSetChanged()
                                                        }
                                                    }
                                                }
                                            }
                                    }
                                }
                            }

                        val isLocationEnabled = isLocationEnabled(requireContext())
                        if (isLocationEnabled) {
                            if (ContextCompat.checkSelfPermission(
                                    requireContext(),
                                    Manifest.permission.ACCESS_FINE_LOCATION
                                ) == PackageManager.PERMISSION_GRANTED
                            ) {
                                fusedLocationClient.lastLocation
                                    .addOnSuccessListener { location ->
                                        if (location != null) {
                                            lat = location.latitude
                                            long = location.longitude

                                            val calculate = calculateDistance(
                                                lokasiBS.latitude,
                                                lokasiBS.longitude,
                                                lat,
                                                long
                                            ).toDouble()

                                            jarak = Math.round(calculate * 100.0) / 100.00
                                            rekomendasiJarak = tsukamotoJarakRekomendasi(jarak)

                                            val foundElement = bankSampahDataList.find { it.idBankSampah == idBS }
                                            if (foundElement != null) {
                                                foundElement.jarakBankSampah = jarak
                                                foundElement.rekomendasiJarak = rekomendasiJarak

                                            }
                                        }

                                    }
                            }
                        }

                        adapter = BSMapsAdapter(bankSampahDataList, this)
                        rvMapsUsers.layoutManager = LinearLayoutManager(requireContext())
                        rvMapsUsers.adapter = adapter
                    }
                    // Perbarui data peta
                    updateMapWithGeoPoints()
                }
            }
            .addOnFailureListener { exception: Exception ->
                Toast.makeText(requireContext(), exception.toString(), Toast.LENGTH_LONG).show()
            }

    }

    private fun updateMapWithGeoPoints() {
        icon = BitmapUtils.bitmapFromDrawableRes(
            requireContext(),
            R.drawable.blue_marker_view
        )!!
        val newWidth = 100
        val newHeight = 160
        val bsIconLarge = Bitmap.createScaledBitmap(icon, newWidth, newHeight, false)

        mapView.getMapboxMap().apply {
            setCamera(
                cameraOptions {
                    center(Point.fromLngLat(LONGITUDE,LATITUDE))
                    zoom(10.0)
                }
            )
            getStyle {
                markerManager = MarkerManager(mapView)
                for (dataMaps in bsMaps) {
                    markerManager.addMarker(
                        Marker(
                            title = dataMaps.nama,
                            snippet = dataMaps.alamat,
                            position = Point.fromLngLat(dataMaps.lokasi!!.longitude, dataMaps.lokasi!!.latitude),
                            icon = bsIconLarge
                        )
                    )
                }

                markerPositionDevice()
            }
        }
    }

    private fun markerPositionDevice() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        val isLocationEnabled = isLocationEnabled(requireContext())
        if (isLocationEnabled) {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                fusedLocationClient.lastLocation
                    .addOnSuccessListener { location ->
                        if (location != null) {
                            val mylat = location.latitude
                            val mylong = location.longitude

                            val currenticon = BitmapUtils.bitmapFromDrawableRes(
                                requireContext(),
                                R.drawable.purple_marker_view
                            )!!
                            val newWidth = 100
                            val newHeight = 160
                            val currenticonLarge = Bitmap.createScaledBitmap(currenticon, newWidth, newHeight, false)

                            val marker = markerManager.addMarker(
                                Marker(
                                    title = "Lokasi Anda",
                                    snippet = "Latitude: $mylat \n Longitude: $mylong",
                                    icon = currenticonLarge,
                                    position = Point.fromLngLat(mylong, mylat)
                                )
                            )
                            // open InfoWindow at startup
                            markerManager.selectMarker(marker)
                        }
                    }
            }
        }
    }

    private fun getPositionDevice() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        val isLocationEnabled = isLocationEnabled(requireContext())
        if (isLocationEnabled) {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                fusedLocationClient.lastLocation
                    .addOnSuccessListener { location ->
                        if (location != null) {
                            val mylat = location.latitude
                            val mylong = location.longitude
                            onClickLocation(mylong, mylat)
                        }
                    }
            }
            else {
                Toast.makeText(context, "Izin akses lokasi belum diberikan", Toast.LENGTH_LONG).show()
            }
        }else {
            Toast.makeText(context, "Lokasi perangkat belum diaktifkan", Toast.LENGTH_LONG).show()
        }
    }

    private fun isLocationEnabled(context: Context): Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return LocationManagerCompat.isLocationEnabled(locationManager)
    }

    override fun onClickLocation(lgn: Double, lat: Double) {
        val targetPoint = Point.fromLngLat(lgn,lat)
        mapView.getMapboxMap().setCamera(
            CameraOptions.Builder()
                .center(targetPoint)
                .zoom(17.0)
                .pitch(75.0)
                .bearing(130.0)
                .build()
        )
    }

    override fun onDestroy(){
        markerManager.destroy()
        super.onDestroy()
    }

    fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Float {
        val startPoint = Location("location")
        startPoint.setLatitude(lat1)
        startPoint.setLongitude(lon1)

        val endPoint = Location("location")
        endPoint.setLatitude(lat2)
        endPoint.setLongitude(lon2)

        val distance: Float = startPoint.distanceTo(endPoint)/1000

        return distance
    }

    // Fungsi Keanggotaan untuk variabel Jarak
    fun dekat(jarak: Double): Double {
        if (jarak <= 0) return 1.0
        if (jarak >= 2) return 0.0
        return (2 - jarak) / 2
    }

    fun sedang(jarak: Double): Double {
        if (jarak <= 1 || jarak >= 5) return 0.0
        if (jarak >= 1 && jarak <= 3) return (jarak - 1) / 2
        return (5 - jarak) / 2
    }

    fun jauh(jarak: Double): Double {
        if (jarak <= 4) return 0.0
        return 1.0
    }

    // Fungsi Keanggotaan untuk variabel Harga Beli Sampah
    fun murah(harga: Double): Double {
        if (harga <= 0) return 1.0
        if (harga >= 5000) return 0.0
        return (5000 - harga) / 5000
    }

    fun hargaSedang(harga: Double): Double {
        if (harga <= 3000 || harga >= 10000) return 0.0
        if (harga >= 3000 && harga <= 6500) return (harga - 3000) / 3500
        return (10000 - harga) / 3500
    }

    fun mahal(harga: Double): Double {
        if (harga <= 8000) return 0.0
        return 1.0
    }

    fun tsukamotoJarakRekomendasi(jarak: Double): Double {
        val a1 = dekat(jarak)
        val a2 = sedang(jarak)
        val a3 = jauh(jarak)

        // Defuzzifikasi menggunakan metode centroid
        val centroidDirekomendasikan = 100.0
        val centroidPertimbangan = 50.0
        val centroidTidakDirekomendasikan = 0.0

        val hasilRekomendasi = (a1 * centroidDirekomendasikan +
                a2 * centroidPertimbangan +
                a3 * centroidTidakDirekomendasikan) /
                (a1 + a2 + a3)

        return hasilRekomendasi
    }

    fun tsukamotoHargaRekomendasi(harga: Double): Double {
        val a1 = mahal(harga)
        val a2 = hargaSedang(harga)
        val a3 = murah(harga)

        // Defuzzifikasi menggunakan metode centroid
        val centroidDirekomendasikan = 100.0
        val centroidPertimbangan = 50.0
        val centroidTidakDirekomendasikan = 0.0

        val hasilRekomendasi = (a1 * centroidDirekomendasikan +
                a2 * centroidPertimbangan +
                a3 * centroidTidakDirekomendasikan) /
                (a1 + a2 + a3)

        return hasilRekomendasi
    }

    // Inferensi Tsukamoto
    fun rekomendasi(jarak: Double, harga: Double): Double {
        val a1 = max(dekat(jarak), mahal(harga))
        val a2 = max(sedang(jarak), hargaSedang(harga))
        val a3 = max(jauh(jarak), murah(harga))

        // Defuzzifikasi menggunakan metode centroid
        val centroidDirekomendasikan = 100.0
        val centroidPertimbangan = 50.0
        val centroidTidakDirekomendasikan = 0.0

        val hasilRekomendasi = (a1 * centroidDirekomendasikan +
                a2 * centroidPertimbangan +
                a3 * centroidTidakDirekomendasikan) /
                (a1 + a2 + a3)

        return hasilRekomendasi
    }

    override fun onResume() {
        super.onResume()
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED
        ) {

            fusedLocationProvider?.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        }
    }

    override fun onPause() {
        super.onPause()
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            == PackageManager.PERMISSION_GRANTED
        ) {

            fusedLocationProvider?.removeLocationUpdates(locationCallback)
        }
    }

    private fun checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    requireActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {
                AlertDialog.Builder(requireContext())
                    .setTitle("Izin Lokasi Diperlukan")
                    .setMessage("Aplikasi bank sampah pontianak memerlukan izin Lokasi, harap terima untuk menggunakan fungsi lokasi")
                    .setPositiveButton(
                        "OK"
                    ) { _, _ ->
                        requestLocationPermission()
                    }
                    .create()
                    .show()
            } else {
                requestLocationPermission()
            }
        } else {
            checkBackgroundLocation()
        }
    }

    private fun checkBackgroundLocation() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestBackgroundLocationPermission()
        }
    }

    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
            ),
            MY_PERMISSIONS_REQUEST_LOCATION
        )
    }

    private fun requestBackgroundLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
                ),
                MY_PERMISSIONS_REQUEST_BACKGROUND_LOCATION
            )
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                MY_PERMISSIONS_REQUEST_LOCATION
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_LOCATION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    if (ContextCompat.checkSelfPermission(
                            requireContext(),
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        fusedLocationProvider?.requestLocationUpdates(
                            locationRequest,
                            locationCallback,
                            Looper.getMainLooper()
                        )
                        checkBackgroundLocation()
                    }

                } else {
                    Toast.makeText(requireContext(), "izin ditolak", Toast.LENGTH_LONG).show()
                    if (!ActivityCompat.shouldShowRequestPermissionRationale(
                            requireActivity(),
                            Manifest.permission.ACCESS_FINE_LOCATION
                        )
                    ) {
                        val packageUri = Uri.fromParts("package", requireContext().packageName, null)
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, packageUri)
                        startActivity(intent)
                    }
                }
                return
            }
            MY_PERMISSIONS_REQUEST_BACKGROUND_LOCATION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    if (ContextCompat.checkSelfPermission(
                            requireContext(),
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        fusedLocationProvider?.requestLocationUpdates(
                            locationRequest,
                            locationCallback,
                            Looper.getMainLooper()
                        )

                        Toast.makeText(
                            requireContext(),
                            "Diberikan Izin Lokasi Latar Belakang",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                } else {
                    Toast.makeText(requireContext(), "izin ditolak", Toast.LENGTH_LONG).show()
                }
                return

            }
        }
    }

    companion object {
        private const val MY_PERMISSIONS_REQUEST_LOCATION = 99
        private const val MY_PERMISSIONS_REQUEST_BACKGROUND_LOCATION = 66
    }

}

interface OnLocationClickListener {
    fun onClickLocation(lgn: Double, lat: Double)
}
