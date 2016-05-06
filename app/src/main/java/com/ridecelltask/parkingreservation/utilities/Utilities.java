package com.ridecelltask.parkingreservation.utilities;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.ridecelltask.parkingreservation.Application.ParkingApplication;

/**
 * Created by Albarrak on 5/4/16.
 */
public class Utilities {

    private final static String PREFS_NAME = "parking";


    public static void store(String key,String value){
        SharedPreferences store = ParkingApplication.getAppContext().getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = store.edit();
        editor.putString(key,value);
        editor.commit();

    }
    public static String getValue(String key){
        SharedPreferences store = ParkingApplication.getAppContext().getSharedPreferences(PREFS_NAME, 0);
        String value = store.getString(key, "");
        return value;
    }

    public static void logout() {
        SharedPreferences store = ParkingApplication.getAppContext().getSharedPreferences(PREFS_NAME, 0);
        store.edit().clear().commit();
    }

}
