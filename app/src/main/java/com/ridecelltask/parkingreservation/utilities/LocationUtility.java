package com.ridecelltask.parkingreservation.utilities;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.ridecelltask.parkingreservation.Application.ParkingApplication;

/**
 * Created by Albarrak on 5/4/16.
 */
public class LocationUtility {

    static GoogleApiClient googleApiClient;

    public static boolean isConnected, isFirst;

    public interface LocationCallback {
        void onConnected(Location location);

        void onDisconnected();
    }

    public static synchronized boolean buildGoogleApiClient(final LocationCallback locationCallback, final GoogleMap googleMap) {
        googleApiClient = new GoogleApiClient.Builder(ParkingApplication.getAppContext())
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle bundle) {
                        isConnected = true;
                        findLocation(googleMap, locationCallback);

                    }

                    @Override
                    public void onConnectionSuspended(int i) {
                        isConnected = false;
                        locationCallback.onDisconnected();
                    }
                }).addApi(LocationServices.API).build();

        googleApiClient.connect();
        return isConnected;


    }


    public static boolean checkLocation(Activity activity){
        LocationManager locationManager;
        locationManager = (LocationManager)activity.getSystemService(Context.LOCATION_SERVICE);


        if(locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){

            return true;
        }
        else if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){

            return true;
        }
        else {
            showLocationAlert(activity);
            return false;
        }

    }

    public static void showLocationAlert(final Activity activity){
        AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
        dialog.setTitle("Oops");
        dialog.setMessage("Seems your location is disabled, please enable location service in your device to be able to locate and reserve parking spots");
        dialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                activity.startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), 0);
            }
        });
        dialog.show();
    }



    public static void findLocation(final GoogleMap googleMap, final LocationCallback locationCallback) {

        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setExpirationDuration(30 * 1000);
        locationRequest.setFastestInterval(500);
        locationRequest.setSmallestDisplacement(0);

        googleMap.setMyLocationEnabled(true);


        isFirst = true;
        if (ActivityCompat.checkSelfPermission(ParkingApplication.getAppContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(ParkingApplication.getAppContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            Log.d("LocationPermission", "Not Granted");
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if (isFirst) {
                    locationCallback.onConnected(location);
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 15));

                    isFirst = false;
                }

            }
        });


    }

}
