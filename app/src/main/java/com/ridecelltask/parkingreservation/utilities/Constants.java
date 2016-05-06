package com.ridecelltask.parkingreservation.utilities;

import com.ridecelltask.parkingreservation.BuildConfig;

/**
 * Created by Albarrak on 5/4/16.
 */
public class Constants {

    public interface API_URLS {
        String PARKING_LOCATIONS = BuildConfig.BASE_URL + "parkinglocations/";
        String PARKING_RESERVE = "/reserve/";
        String SEARCH = "search";
    }
}
