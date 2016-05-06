package com.ridecelltask.parkingreservation.Application;

import android.app.Application;
import android.content.Context;
import android.os.Bundle;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.ridecelltask.parkingreservation.api.RequestManager;
import io.fabric.sdk.android.Fabric;

/**
 * Created by Albarrak on 5/4/16.
 */
public class ParkingApplication extends Application implements   GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{

    private static Context mContext;

    public static Context getAppContext() {
        return ParkingApplication.mContext;
    }



    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics(), new Answers());

        ParkingApplication.mContext = getApplicationContext();
        RequestManager.initializeRequestManager(getApplicationContext());
    }

    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}
