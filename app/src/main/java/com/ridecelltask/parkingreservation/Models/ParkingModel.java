package com.ridecelltask.parkingreservation.Models;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

/**
 * Created by Albarrak on 5/4/16.
 */
public class ParkingModel {

    int     id,
            max_reserve_time_mins,
            min_reserve_time_mins;

    String  lat,
            lng,
            name,
            cost_per_minute,
            reserved_until;

    boolean is_reserved;

    ClusterItem clusterItem;

    public ClusterItem getClusterItem() {
        return new ClusterItem() {
            @Override
            public LatLng getPosition() {
                return new LatLng(Double.valueOf(getLat()), Double.valueOf(getLng()));
            }
        };
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMax_reserve_time_mins() {
        return max_reserve_time_mins;
    }

    public void setMax_reserve_time_mins(int max_reserve_time_mins) {
        this.max_reserve_time_mins = max_reserve_time_mins;
    }

    public int getMin_reserve_time_mins() {
        return min_reserve_time_mins;
    }

    public void setMin_reserve_time_mins(int min_reserve_time_mins) {
        this.min_reserve_time_mins = min_reserve_time_mins;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCost_per_minute() {
        return cost_per_minute;
    }

    public void setCost_per_minute(String cost_per_minute) {
        this.cost_per_minute = cost_per_minute;
    }

    public String getReserved_until() {
        return reserved_until;
    }

    public void setReserved_until(String reserved_until) {
        this.reserved_until = reserved_until;
    }

    public boolean is_reserved() {
        return is_reserved;
    }

    public void setIs_reserved(boolean is_reserved) {
        this.is_reserved = is_reserved;
    }
}
