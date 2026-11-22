package com.example.util

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.coroutines.resume

object LocationHelper {

    fun hasLocationPermission(context: Context): Boolean {
        val coarse = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val fine = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        return coarse || fine
    }

    @SuppressLint("MissingPermission")
    suspend fun getCurrentCoordinates(context: Context): Pair<Double, Double>? {
        if (!hasLocationPermission(context)) return null

        // Try FusedLocationProviderClient first with timeout
        val fusedResult = withTimeoutOrNull(7000L) {
            suspendCancellableCoroutine<Location?> { cont ->
                val client = LocationServices.getFusedLocationProviderClient(context)
                val cts = CancellationTokenSource()
                cont.invokeOnCancellation { cts.cancel() }

                client.getCurrentLocation(Priority.PRIORITY_BALANCED_POWER_ACCURACY, cts.token)
                    .addOnSuccessListener { loc ->
                        if (loc != null) {
                            if (cont.isActive) cont.resume(loc)
                        } else {
                            client.lastLocation
                                .addOnSuccessListener { last -> if (cont.isActive) cont.resume(last) }
                                .addOnFailureListener { if (cont.isActive) cont.resume(null) }
                        }
                    }
                    .addOnFailureListener {
                        client.lastLocation
                            .addOnSuccessListener { last -> if (cont.isActive) cont.resume(last) }
                            .addOnFailureListener { if (cont.isActive) cont.resume(null) }
                    }
            }
        }

        if (fusedResult != null) {
            return fusedResult.latitude to fusedResult.longitude
        }

        // Fallback to Android LocationManager lastKnownLocation
        val lm = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager ?: return null
        val providers = listOf(LocationManager.NETWORK_PROVIDER, LocationManager.GPS_PROVIDER)
        for (provider in providers) {
            val loc = runCatching { lm.getLastKnownLocation(provider) }.getOrNull()
            if (loc != null) return loc.latitude to loc.longitude
        }
        return null
    }
}
