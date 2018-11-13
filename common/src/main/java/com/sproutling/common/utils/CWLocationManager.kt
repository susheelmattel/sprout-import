package com.sproutling.common.utils

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.IntentSender
import android.provider.Settings
import android.support.v4.app.ActivityCompat
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsStatusCodes
import com.sproutling.common.ui.view.BaseView
import java.util.ArrayList

object CWLocationManager{

    private val UPDATE_INTERVAL_IN_MILLISECONDS = 100000
    private val FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS / 2
    val PERMISSION_LOCATION_REQUEST_CODE = 16

    fun enableLocationSettings(activity: Activity) {
        val locationSettingsRequest = createLocationSettingsRequest()
        val result = LocationServices.getSettingsClient(activity).checkLocationSettings(locationSettingsRequest)
        result.addOnCompleteListener { task ->
            try {
                val response = task.getResult(ApiException::class.java)
                // All location settings are satisfied. The client can initialize location
                // requests here.

            } catch (exception: ApiException) {
                when (exception.statusCode) {
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED ->
                        // Location settings are not satisfied. But could be fixed by showing the
                        // user a dialog.
                        try {
                            // Cast to a resolvable exception.
                            val resolvable = exception as ResolvableApiException
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            resolvable.startResolutionForResult(
                                    activity,
                                    BaseView.REQUEST_CHECK_SETTINGS)
                        } catch (e: IntentSender.SendIntentException) {
                            // Ignore the error.
                        } catch (e: ClassCastException) {
                            // Ignore, should be an impossible error.
                        }

                    LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                    }

                    LocationSettingsStatusCodes.CANCELED -> {
                        //TODO handle when user doesn't provide access
                    }
                }
            }
        }
    }

    private fun createLocationSettingsRequest(): LocationSettingsRequest {

        val mLocationRequest = LocationRequest()
        // Sets the desired interval for active location updates. This interval is
        // inexact. You may not receive updates at all if no location sources are available, or
        // you may receive them slower than requested. You may also receive updates faster than
        // requested if other applications are requesting location at a faster interval.
        mLocationRequest.interval = UPDATE_INTERVAL_IN_MILLISECONDS.toLong()

        // Sets the fastest rate for active location updates. This interval is exact, and your
        // application will never receive updates faster than this value.
        mLocationRequest.fastestInterval = FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS.toLong()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        val builder = LocationSettingsRequest.Builder()
        builder.addLocationRequest(mLocationRequest)
        builder.setAlwaysShow(true)
        builder.setNeedBle(true)

        return builder.build()
    }


     fun requestLocationPermission(activity: Activity) {
        val permissions = ArrayList<String>()
        permissions.add(Manifest.permission.ACCESS_FINE_LOCATION)
        permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION)
        ActivityCompat.requestPermissions(activity, permissions.toTypedArray(),
                PERMISSION_LOCATION_REQUEST_CODE)
    }

     fun showLocationSettings(activity: Activity) {
        val callGPSSettingIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        activity.startActivityForResult(callGPSSettingIntent, BaseView.REQUEST_GPS_SETTINGS)
    }
}