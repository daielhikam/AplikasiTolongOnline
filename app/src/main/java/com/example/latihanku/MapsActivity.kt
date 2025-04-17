package com.example.latihanku


import android.location.Geocoder
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.latihanku.databinding.ActivityMapsBinding
import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import java.util.Locale

class MapsActivity : AppCompatActivity() {

    private lateinit var map: MapView
    private lateinit var binding: ActivityMapsBinding
    private var currentMarker: Marker? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Configuration.getInstance().load(
            applicationContext,
            PreferenceManager.getDefaultSharedPreferences(applicationContext)
        )

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener {
            onBackPressed()
        }

        map = findViewById(R.id.map)
        map.setMultiTouchControls(true)

        val startPoint = GeoPoint(-6.200000, 106.816666)
        map.controller.setZoom(15.0)
        map.controller.setCenter(startPoint)

        currentMarker = Marker(map).apply {
            position = startPoint
            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            title = "Jakarta"
        }
        map.overlays.add(currentMarker)

        binding.etTo.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_SEARCH) {
                searchLocation(binding.etTo.text.toString())
                true
            } else {
                false
            }
        }

        binding.btnAdd.setOnClickListener {
            searchLocation(binding.etTo.text.toString())
        }
    }

    private fun searchLocation(locationName: String) {
        if (locationName.isNotEmpty()) {
            val geocoder = Geocoder(this, Locale.getDefault())
            val addressList = geocoder.getFromLocationName(locationName, 1)

            if (!addressList.isNullOrEmpty()) {
                val address = addressList[0]
                val geoPoint = GeoPoint(address.latitude, address.longitude)

                map.controller.setZoom(16.0)
                map.controller.setCenter(geoPoint)

                currentMarker?.let { map.overlays.remove(it) }

                currentMarker = Marker(map).apply {
                    position = geoPoint
                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                    title = locationName
                }
                map.overlays.add(currentMarker)
            } else {
                Toast.makeText(this, "Lokasi tidak ditemukan", Toast.LENGTH_SHORT).show()
            }
        }
    }
}