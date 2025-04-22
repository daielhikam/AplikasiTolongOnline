package com.example.latihanku

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.latihanku.databinding.ActivityMapsBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import org.json.JSONObject
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.Locale

class MapsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMapsBinding
    private lateinit var map: MapView
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private var userMarker: Marker? = null
    private var destinationMarker: Marker? = null
    private var routePolyline: Polyline? = null

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 100
        private val DEFAULT_LOCATION = GeoPoint(-6.200000, 106.816666)
        private const val DEFAULT_ZOOM = 17.0
        private const val API_KEY = "5b3ce3597851110001cf6248192acbd78c324e98a3b72cfd8c5c1179"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        Configuration.getInstance().load(applicationContext, PreferenceManager.getDefaultSharedPreferences(applicationContext))
        Configuration.getInstance().userAgentValue = packageName

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        initMap()
        initListeners()
        checkLocationPermission()
    }

    private fun initMap() {
        map = binding.map
        map.setTileSource(TileSourceFactory.MAPNIK)
        map.setMultiTouchControls(true)
        map.controller.apply {
            setZoom(DEFAULT_ZOOM)
            setCenter(DEFAULT_LOCATION)
        }
    }

    private fun initListeners() {
        binding.btnBack.setOnClickListener { onBackPressed() }

        binding.btnMyLocation.setOnClickListener { showCurrentLocation() }

        binding.btnAdd.setOnClickListener {
            searchLocation(binding.etTo.text.toString())
        }
        binding.btnShare.setOnClickListener {
            // Pastikan userMarker sudah ada, yang berarti lokasi pengguna sudah terdeteksi
            if (userMarker != null) {
                // Ambil latitude dan longitude dari userMarker
                val latitude = userMarker!!.position.latitude
                val longitude = userMarker!!.position.longitude

                // Buat string share yang berisi link Google Maps dengan koordinat
                val shareText = "Ini lokasi saya sekarang:\nhttps://www.google.com/maps?q=$latitude,$longitude"

                // Buat intent untuk membagikan teks melalui berbagai aplikasi
                val shareIntent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, shareText)
                    type = "text/plain"
                }

                // Memulai activity untuk memilih aplikasi yang akan digunakan untuk berbagi
                startActivity(Intent.createChooser(shareIntent, "Bagikan lokasi saya via"))
            } else {
                // Jika userMarker belum ada, berarti lokasi pengguna belum terdeteksi
                Toast.makeText(this, "Lokasi saya belum tersedia", Toast.LENGTH_SHORT).show()
            }
        }

        binding.etTo.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_SEARCH) {
                searchLocation(binding.etTo.text.toString())
                true
            } else false
        }
    }

    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        } else {
            showCurrentLocation()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE &&
            grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            showCurrentLocation()
        } else {
            Toast.makeText(this, "Izin lokasi ditolak", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showCurrentLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) return

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            location?.let {
                val myLocation = GeoPoint(it.latitude, it.longitude)
                addUserMarker(myLocation)
                binding.etFrom.setText("${it.latitude}, ${it.longitude}")
            }
        }
    }

    private fun addUserMarker(location: GeoPoint) {
        userMarker?.let { map.overlays.remove(it) }

        userMarker = Marker(map).apply {
            position = location
            title = "Lokasi Saya"
            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        }

        map.overlays.add(userMarker)
        map.controller.setCenter(location)
    }

    private fun addDestinationMarker(location: GeoPoint, title: String) {
        destinationMarker?.let { map.overlays.remove(it) }

        destinationMarker = Marker(map).apply {
            position = location
            this.title = title
            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        }

        map.overlays.add(destinationMarker)
        map.controller.setCenter(location)
    }

    private fun searchLocation(locationName: String) {

        if (locationName.isEmpty()) return

        val geocoder = Geocoder(this, Locale.getDefault())
        val addressList = geocoder.getFromLocationName(locationName, 1)

        if (!addressList.isNullOrEmpty()) {
            val destination = addressList[0]
            val destPoint = GeoPoint(destination.latitude, destination.longitude)

            addDestinationMarker(destPoint, locationName)

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
                fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                    location?.let {
                        val userLocation = GeoPoint(it.latitude, it.longitude)
                        val distance = calculateDistance(it.latitude, it.longitude, destination.latitude, destination.longitude)
                        val distanceText = "Jarak ke $locationName: ${"%.2f".format(distance)} km"

                        Toast.makeText(this, distanceText, Toast.LENGTH_LONG).show()
                        binding.tvDistance.text = distanceText

                        getRoute(userLocation, destPoint)
                    }
                }
            }
        } else {
            Toast.makeText(this, "Lokasi tidak ditemukan", Toast.LENGTH_SHORT).show()
        }
    }

    private fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val R = 6371.0 // km
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                Math.sin(dLon / 2) * Math.sin(dLon / 2)
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
        return R * c
    }

    private fun getRoute(startPoint: GeoPoint, endPoint: GeoPoint) {
        val url = "https://api.openrouteservice.org/v2/directions/driving-car?api_key=$API_KEY&start=${startPoint.longitude},${startPoint.latitude}&end=${endPoint.longitude},${endPoint.latitude}"

        Thread {
            try {
                val connection = URL(url).openConnection() as HttpURLConnection
                connection.setRequestProperty("Accept", "application/json")
                connection.connect()

                // Periksa status kode HTTP
                val responseCode = connection.responseCode
                if (responseCode != HttpURLConnection.HTTP_OK) {
                    runOnUiThread {
                        Toast.makeText(this, "Gagal mengambil rute. Kode status: $responseCode", Toast.LENGTH_SHORT).show()
                    }
                    return@Thread
                }

                val reader = BufferedReader(InputStreamReader(connection.inputStream))
                val response = StringBuilder()
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    response.append(line)
                }

                // Tampilkan respons untuk debugging
                println("Respons API: $response")

                val jsonResponse = JSONObject(response.toString())
                val routes = jsonResponse.getJSONArray("features")

                if (routes.length() > 0) {
                    val geometry = routes.getJSONObject(0).getJSONObject("geometry")
                    val coordinates = geometry.getJSONArray("coordinates")

                    val routePoints = mutableListOf<GeoPoint>()
                    for (i in 0 until coordinates.length()) {
                        val coord = coordinates.getJSONArray(i)
                        val lat = coord.getDouble(1) // Latitude berada di index 1
                        val lon = coord.getDouble(0) // Longitude berada di index 0
                        routePoints.add(GeoPoint(lat, lon))
                    }

                    runOnUiThread {
                        drawRoute(routePoints)
                    }
                } else {
                    runOnUiThread {
                        Toast.makeText(this, "Rute tidak ditemukan", Toast.LENGTH_SHORT).show()
                    }
                }

            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread {
                    Toast.makeText(this, "Terjadi kesalahan dalam mengambil rute: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                }
            }
        }.start()
    }

    private fun drawRoute(routePoints: List<GeoPoint>) {
        routePolyline?.let { map.overlays.remove(it) }

        routePolyline = Polyline().apply {
            setPoints(routePoints)
            outlinePaint.color = android.graphics.Color.BLUE
            outlinePaint.strokeWidth = 5f
        }

        map.overlays.add(routePolyline)
        map.invalidate()
    }
}