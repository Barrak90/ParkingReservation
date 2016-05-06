package com.ridecelltask.parkingreservation.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.datetimepicker.date.DatePickerDialog;
import com.android.datetimepicker.time.RadialPickerLayout;
import com.android.datetimepicker.time.TimePickerDialog;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;
import com.ridecelltask.parkingreservation.Application.ParkingApplication;
import com.ridecelltask.parkingreservation.Models.ParkingModel;
import com.ridecelltask.parkingreservation.R;
import com.ridecelltask.parkingreservation.api.RequestManager;
import com.ridecelltask.parkingreservation.utilities.Constants;
import com.ridecelltask.parkingreservation.utilities.LocationUtility;
import com.ridecelltask.parkingreservation.utilities.Utilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by Albarrak on 5/4/16.
 */
public class FragmentReservation extends FragmentBase implements View.OnClickListener,  TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener{


    private static final String TIME_PATTERN = "HH:mm";

    public ParkingModel parkingModel = new ParkingModel();

    TextView    parkingDate,
                parkingTime,
                parkingDurationTv,
                parkingMaxDurationTv,

                parkingNameTv,
                parkingAddressTv,

                parkingCost,
                parkingDistance,
                parkingAvailability,

                reserveBtn;

    SeekBar parkingDurationSK;


    String  sDay,
            sMonth,
            dateString;

    private Calendar calendar;
    private DateFormat dateFormat;
    private SimpleDateFormat timeFormat;

    DatePickerDialog datePickerDialog;
    TimePickerDialog timePickerDialog;

    int time;
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_reservation, container, false);

        calendar = Calendar.getInstance();
        dateFormat = DateFormat.getDateInstance(DateFormat.LONG, Locale.getDefault());
        timeFormat = new SimpleDateFormat(TIME_PATTERN, Locale.getDefault());

        parkingNameTv = (TextView) view.findViewById(R.id.parkin_name_tv);
        parkingAddressTv = (TextView) view.findViewById(R.id.parking_address_tv);

        parkingDate = (TextView) view.findViewById(R.id.parking_date_tv);
        parkingTime = (TextView) view.findViewById(R.id.parking_time_tv);
        parkingDurationTv = (TextView) view.findViewById(R.id.parkin_duration_tv);

        parkingCost = (TextView) view.findViewById(R.id.parking_cost_tv);
        parkingDistance = (TextView) view.findViewById(R.id.parking_distance_tv);
        parkingAvailability = (TextView) view.findViewById(R.id.isReserved_tv);

        parkingMaxDurationTv = (TextView) view.findViewById(R.id.parkin_duration_tv);
        reserveBtn = (TextView) view.findViewById(R.id.parking_reserve_btn);

        parkingDurationSK = (SeekBar) view.findViewById(R.id.parking_duration);

        parkingDate.setOnClickListener(this);
        parkingTime.setOnClickListener(this);
        reserveBtn.setOnClickListener(this);




        return view;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if(bundle != null && bundle.getString("ParkingModel","").length() > 0){
            try {
                parkingModel = new Gson().fromJson(bundle.getString("ParkingModel"), ParkingModel.class);

            }
            catch (Exception e){

            }
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        updateDateAndTime();

        datePickerDialog = DatePickerDialog.newInstance(FragmentReservation.this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.setMinDate(calendar);
        timePickerDialog = TimePickerDialog.newInstance(FragmentReservation.this, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
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
            parkingAvailability.setText("reserved");
        }
        else{
            parkingAvailability.setText("Available");
        }
        parkingDurationSK.setMax(110);
        time = 10;
        parkingDurationSK.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                time = i+10;
                parkingDurationTv.setText(time+"min/ $"+(Double.valueOf(parkingModel.getCost_per_minute())*time));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }

    @Override
    public void onClick(View view){
        switch (view.getId()){
            case R.id.parking_date_tv:{
                datePickerDialog.show(getActivity().getFragmentManager(), "datePicker");
                break;
            }
            case R.id.parking_time_tv:{
                timePickerDialog.show(getActivity().getFragmentManager(), "timePicker");
                break;
            }
            case R.id.parking_reserve_btn:{
                String reservationURL = Constants.API_URLS.PARKING_LOCATIONS+parkingModel.getId()+Constants.API_URLS.PARKING_RESERVE;
                try {

                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("minutes",time );
                    RequestManager.addRequest(new JsonObjectRequest(Request.Method.POST, reservationURL, jsonObject, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            showConfirmationDialog();
                            Utilities.store("spotReservation", new Gson().toJson(new Gson().fromJson(response.toString(), ParkingModel.class)));
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            if(error.networkResponse != null && error.networkResponse.data != null){
                                Toast.makeText(ParkingApplication.getAppContext(), new String(error.networkResponse.data), Toast.LENGTH_LONG).show();
                            }
                        }
                    }));
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(ParkingApplication.getAppContext(), e.getLocalizedMessage(), Toast.LENGTH_LONG).show();

                }


                break;
            }

        }
    }

    private void updateDateAndTime() {
        parkingDate.setText(dateFormat.format(calendar.getTime()));
        parkingTime.setText(timeFormat.format(calendar.getTime()));


    }

    @Override
    public void onDateSet(DatePickerDialog dialog, int year, int monthOfYear, int dayOfMonth) {
        calendar.set(year, monthOfYear, dayOfMonth);

        if(String.valueOf(monthOfYear+1).length() < 2){
            sMonth = "0"+(monthOfYear+1);
        }
        else {
            sMonth = String.valueOf(monthOfYear+1);
        }

        if(String.valueOf(dayOfMonth).length() < 2){
            sDay = "0"+dayOfMonth;
        }
        else {
            sDay = String.valueOf(dayOfMonth);
        }
        dateString = year + "-" + sMonth+ "-" + sDay;

        updateDateAndTime();
    }

    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute) {
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);
        updateDateAndTime();
    }


    private void showConfirmationDialog(){
        Activity activity = getActivity();
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        View convertView = inflater.inflate(R.layout.confirmation_dialog, null);
        alertDialog.setView(convertView);
        alertDialog.setCancelable(false);

        final AlertDialog dialog = alertDialog.show();
//        dialog.getWindow().setLayout(activity.getResources().getDimensionPixelSize(R.dimen.popup_width), activity.getResources().getDimensionPixelSize(R.dimen.popup_height));

        TextView checkReservation = (TextView) convertView.findViewById(R.id.confirmation_check_bnt);
        TextView cancel = (TextView) convertView.findViewById(R.id.confirmation_cancel_bnt);
        checkReservation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                ((FragmentsContainer) getParentFragment()).replaceFragment(new FragmentHistory(), true, null, null);

            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }

}
