package com.ridecelltask.parkingreservation.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.ridecelltask.parkingreservation.Application.ParkingApplication;
import com.ridecelltask.parkingreservation.R;
import com.ridecelltask.parkingreservation.utilities.Utilities;

/**
 * Created by Albarrak on 5/6/16.
 */
public class FragmentMyCar extends FragmentBase {


    Button clearBtn;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_car, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        clearBtn = (Button) view.findViewById(R.id.reset_button);
        clearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utilities.logout();
                Toast.makeText(ParkingApplication.getAppContext(), "You have cleared your history successfully", Toast.LENGTH_LONG).show();
            }
        });
    }
}
