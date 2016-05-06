package com.ridecelltask.parkingreservation.fragments;

import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.ridecelltask.parkingreservation.Application.ParkingApplication;
import com.ridecelltask.parkingreservation.Models.ParkingModel;
import com.ridecelltask.parkingreservation.R;
import com.ridecelltask.parkingreservation.utilities.Utilities;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by Albarrak on 5/6/16.
 */
public class FragmentHistory extends FragmentBase {

    TextView    parkingDate,
                parkingTime,

                parkingNameTv,
                parkingAddressTv,
                extendBtn,
                parkingCost,
            parkingAvailability;

    ParkingModel parkingModel;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        try {
            parkingModel = new Gson().fromJson(Utilities.getValue("spotReservation"), ParkingModel.class);

        }
        catch (Exception e){

        }
        parkingNameTv = (TextView) view.findViewById(R.id.info_parkin_name_tv);
        parkingAddressTv = (TextView) view.findViewById(R.id.info_parking_address_tv);

        parkingDate = (TextView) view.findViewById(R.id.info_parking_date_tv);
        parkingTime = (TextView) view.findViewById(R.id.info_parking_time_tv);

        parkingCost = (TextView) view.findViewById(R.id.info_parking_cost_tv);
        parkingAvailability = (TextView) view.findViewById(R.id.info_isReserved_tv);

        extendBtn = (TextView) view.findViewById(R.id.info_parking_reserve_btn);

        if(parkingModel != null){
            Geocoder geocoder;
            List<Address> addresses;
            geocoder = new Geocoder(ParkingApplication.getAppContext(), Locale.US);
            try {
                addresses = geocoder.getFromLocation(Double.valueOf(parkingModel.getLat()), Double.valueOf(parkingModel.getLng()), 1);
                parkingAddressTv.setText(addresses.get(0).getAddressLine(0));

            } catch (IOException e) {
                e.printStackTrace();
            }
            parkingNameTv.setText(parkingModel.getName());
            parkingCost.setText("Cost \n"+parkingModel.getCost_per_minute()+"/min");
            if(parkingModel.is_reserved()){
                parkingAvailability.setTextColor(Color.parseColor("#ffffff"));
                parkingAvailability.setText("reserved");
            }
            else{
                parkingAvailability.setText("Available");
            }

            extendBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Bundle bundle = new Bundle();
                    bundle.putString("ParkingModel", new Gson().toJson(parkingModel));
                    ((FragmentsContainer) getParentFragment()).replaceFragment(new FragmentReservation(), true, bundle, null);

                }
            });
        }
        else{
            extendBtn.setText("New reservation");
            extendBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((FragmentsContainer) getParentFragment()).replaceFragment(new FragmentSearchMap(), true, null, null);
                }
            });
        }

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reservation_info, container, false);
        return view;
    }
}
