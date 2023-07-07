package com.zimp.zimpcarsharing

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.zimp.zimpcarsharing.models.Auto


class MappaActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var googleMap: GoogleMap
    private lateinit var mapBounds: LatLngBounds
    private lateinit var mapFragment: SupportMapFragment

    private var auto: Auto? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mappa)

        val extras: Bundle? = intent.extras
        if (extras != null){
            auto = extras.getSerializable("auto", Auto::class.java)
        }
        mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(map: GoogleMap) {
        map.addMarker(MarkerOptions().position(LatLng(auto!!.latitudine, auto!!.longitudine)).title("${auto!!.marca} ${auto!!.modello}"))
        googleMap = map
        setCameraView()
    }

    private fun setCameraView(){
        val bottomBoundary:Double = auto!!.latitudine - .005
        val leftBoundary:Double = auto!!.longitudine - .005
        val topBoundary:Double = auto!!.latitudine + .005
        val rightBoundary:Double = auto!!.longitudine + .005

        mapBounds = LatLngBounds(
            LatLng(bottomBoundary, leftBoundary),
            LatLng(topBoundary, rightBoundary)
        )

        googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(mapBounds, 0))
    }

}