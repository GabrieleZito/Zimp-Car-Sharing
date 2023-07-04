package com.zimp.zimpcarsharing.utils

import com.google.android.gms.maps.model.LatLng
import com.zimp.zimpcarsharing.models.Auto
import kotlin.math.asin
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

class SortLocations(private val currentLocation: LatLng):Comparator<Auto> {
    override fun compare(a0: Auto?, a1: Auto?): Int {
        val lat1 = a0?.latitudine
        val long1 = a0?.longitudine
        val lat2 = a1?.latitudine
        val long2 = a1?.longitudine

        val distanza1:Double = distance(currentLocation.latitude, currentLocation.longitude, lat1!!, long1!!)
        val distanza2:Double = distance(currentLocation.latitude, currentLocation.longitude, lat2!!, long2!!)

        return (distanza1 - distanza2).toInt()
    }

    private fun distance(fromLat: Double, fromLon: Double, toLat: Double, toLon: Double): Double {
        val radius = 6378137.0 // approximate Earth radius, *in meters*
        val deltaLat = toLat - fromLat
        val deltaLon = toLon - fromLon
        val angle = 2 * asin(sqrt(sin(deltaLat / 2).pow(2.0) + cos(fromLat) * cos(toLat) * sin(deltaLon / 2).pow(2.0)))
        return radius * angle
    }


}