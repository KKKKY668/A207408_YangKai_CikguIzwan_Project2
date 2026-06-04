package com.example.a207408_cikguizwan_lab02

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import java.util.Locale

object LocationHelper {

    fun getCurrentLocation(
        context: Context,
        onSuccess: (locationName: String, lat: Double, lng: Double) -> Unit,
        onFailure: () -> Unit
    ) {
        // 检查权限
        val hasFine = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val hasCoarse = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (!hasFine && !hasCoarse) {
            onFailure()
            return
        }

        val fusedClient = LocationServices.getFusedLocationProviderClient(context)
        val cts = CancellationTokenSource()

        fusedClient.getCurrentLocation(Priority.PRIORITY_BALANCED_POWER_ACCURACY, cts.token)
            .addOnSuccessListener { location ->
                if (location != null) {
                    val lat = location.latitude
                    val lng = location.longitude

                    // 用 Geocoder 把经纬度转成人类可读的地名
                    try {
                        val geocoder = Geocoder(context, Locale.getDefault())
                        @Suppress("DEPRECATION")
                        val addresses = geocoder.getFromLocation(lat, lng, 1)
                        val name = if (!addresses.isNullOrEmpty()) {
                            val addr = addresses[0]
                            // 优先显示 subLocality（区域）或 locality（城市）
                            addr.subLocality ?: addr.locality ?: addr.adminArea ?: "Nearby"
                        } else {
                            "Nearby"
                        }
                        onSuccess(name, lat, lng)
                    } catch (e: Exception) {
                        // Geocoder 失败就直接用坐标
                        onSuccess("${String.format("%.4f", lat)}, ${String.format("%.4f", lng)}", lat, lng)
                    }
                } else {
                    onFailure()
                }
            }
            .addOnFailureListener { onFailure() }
    }
}