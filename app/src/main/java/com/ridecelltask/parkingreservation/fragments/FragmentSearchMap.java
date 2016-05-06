package com.ridecelltask.parkingreservation.fragments;

import android.animation.Animator;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.datetimepicker.date.DatePickerDialog;
import com.android.datetimepicker.time.RadialPickerLayout;
import com.android.datetimepicker.time.TimePickerDialog;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;
import com.ridecelltask.parkingreservation.Application.ParkingApplication;
import com.ridecelltask.parkingreservation.Models.ParkingModel;
import com.ridecelltask.parkingreservation.R;
import com.ridecelltask.parkingreservation.api.RequestManager;
import com.ridecelltask.parkingreservation.utilities.AnimUtils;
import com.ridecelltask.parkingreservation.utilities.Constants;
import com.ridecelltask.parkingreservation.utilities.LocationUtility;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * Created by Albarrak on 5/4/16.
 */
public class FragmentSearchMap extends FragmentBase implements LocationUtility.LocationCallback, TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener{

    private static final String TIME_PATTERN = "HH:mm";

    private GoogleMap googleMap;

    private SupportMapFragment supportMapFragment;

    private Location location;

    public static List<ParkingModel> parkingModels = new ArrayList<>();

    private TextView    parkingNameTv,
                        parkingAddressTv,
                        parkingCost,
                        parkingDistance;

    TextView    searchParkingDate,
                searchParkingTime,
                searchParkingDurationTv,

                searchBtn;

    String  sDay,
            sMonth,
            dateString,
            durationString;

    private Calendar calendar;
    private DateFormat dateFormat;
    private SimpleDateFormat timeFormat;

    DatePickerDialog datePickerDialog;
    TimePickerDialog timePickerDialog;

    EditText searchParkingLocationTv;

    SeekBar     searchParkingDurationSK;


    private Button      reserveSpot;

    ImageView searchImg;

    RelativeLayout searchSliding;
    LinearLayout searchDialog;


    private View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        try {
            view = inflater.inflate(R.layout.fragment_search_map, container, false);
        }catch (InflateException e){
            e.printStackTrace();
        }

        try {
            initializeMap(view);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return view;
    }

    private  void getSpots(Location location){
        final Gson gson = new Gson();
        String locationsURL;
        if(location != null){
            locationsURL = Constants.API_URLS.PARKING_LOCATIONS+"search?lat="+location.getLatitude()+"&lng="+location.getLongitude();
        }
        else{
            locationsURL = Constants.API_URLS.PARKING_LOCATIONS;
        }
        RequestManager.addRequest(new JsonArrayRequest(locationsURL, new Response.Listener<JSONArray>() {


            @Override
            public void onResponse(JSONArray response) {
                try {
                    parkingModels.clear();
                    for(int i = 0; i < response.length(); i++){
                        parkingModels.add(gson.fromJson(response.get(i).toString(), ParkingModel.class));
                    }
                    if(LocationUtility.isConnected && googleMap != null){
                        markParkingLocations();
                    }
                } catch (JsonParseException e){
                    e.printStackTrace();
                } catch (JSONException e){
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(error.networkResponse != null && error.networkResponse.data != null){
                    Toast.makeText(ParkingApplication.getAppContext(), new String(error.networkResponse.data), Toast.LENGTH_LONG).show();
                }

            }
        }));
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        updateDateAndTime();

        datePickerDialog = DatePickerDialog.newInstance(FragmentSearchMap.this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.setMinDate(calendar);
        timePickerDialog = TimePickerDialog.newInstance(FragmentSearchMap.this, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);

    }


    private void initializeMap(View view) {

        calendar = Calendar.getInstance();
        dateFormat = DateFormat.getDateInstance(DateFormat.LONG, Locale.getDefault());
        timeFormat = new SimpleDateFormat(TIME_PATTERN, Locale.getDefault());
        if(supportMapFragment == null){
            supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        }
        if(supportMapFragment != null){

            supportMapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(final GoogleMap googleMap) {
                    FragmentSearchMap.this.googleMap = googleMap;
                    // check if map is created successfully or not
                    if (googleMap == null) {
                        Toast.makeText(ParkingApplication.getAppContext(), "Sorry! unable to create maps", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    LocationUtility.buildGoogleApiClient(FragmentSearchMap.this, googleMap);

                    final View parkingInfoView = getActivity().getLayoutInflater().inflate(R.layout.parking_info_view, null);



                    googleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

                        // Use default InfoWindow frame
                        @Override
                        public View getInfoWindow(Marker arg0) {
                            parkingNameTv = (TextView) parkingInfoView.findViewById(R.id.map_parking_name_tv);
                            parkingAddressTv = (TextView) parkingInfoView.findViewById(R.id.map_parking_address_tv);
                            parkingDistance = (TextView) parkingInfoView.findViewById(R.id.map_parking_distance_tv);
                            parkingCost = (TextView) parkingInfoView.findViewById(R.id.map_parking_cost_tv);
                            reserveSpot = (Button) parkingInfoView.findViewById(R.id.map_parking_reserve_btn);

                            final ParkingModel parkingModel = getParkingModel(arg0.getPosition());

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
                            Location distanceLocation = new Location("");
                            distanceLocation.setLatitude(arg0.getPosition().latitude);
                            distanceLocation.setLongitude(arg0.getPosition().longitude);
                            DecimalFormat df = new DecimalFormat("#.0");
                            parkingDistance.setText("Distance\n"+df.format(location.distanceTo(distanceLocation))+" miles");



                            googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                                @Override
                                public void onInfoWindowClick(Marker marker) {
                                    Bundle bundle = new Bundle();
                                    bundle.putString("ParkingModel", new Gson().toJson(parkingModel));
                                    ((FragmentsContainer) getParentFragment()).replaceFragment(new FragmentReservation(), true, bundle, null);

                                }
                            });

                            return parkingInfoView;
                        }


                        // Defines the contents of the InfoWindow
                        @Override
                        public View getInfoContents(Marker arg0) {





                            return parkingInfoView;

                        }
                    });

                }
            });
        }







    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        getSpots(null);

        searchParkingDate = (TextView) view.findViewById(R.id.map_parking_date_tv);
        searchParkingTime = (TextView) view.findViewById(R.id.map_parking_time_tv);
        searchParkingDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePickerDialog.show(getActivity().getFragmentManager(), "datePicker");

            }
        });
        searchParkingTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timePickerDialog.show(getActivity().getFragmentManager(), "timePicker");

            }
        });
        searchParkingDurationTv = (TextView) view.findViewById(R.id.map_parking_duration_tv);
        searchParkingLocationTv = (EditText) view.findViewById(R.id.map_parking_location_tv);

        searchParkingDurationSK = (SeekBar) view.findViewById(R.id.map_parking_duration);
        searchParkingDurationSK.setMax(110);
        searchParkingDurationSK.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                int time = i+10;
                searchParkingDurationTv.setText(time+"min");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        searchBtn = (TextView) view.findViewById(R.id.map_parking_search_btn);
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getSpots(location);
                searchDialogAction();
            }
        });


        searchDialog = (LinearLayout) view.findViewById(R.id.search_dialog);
        searchSliding = (RelativeLayout) view.findViewById(R.id.sliding_search);
        searchSliding.setTag("Close");
        searchDialogAction();
        searchImg = (ImageView) view.findViewById(R.id.search_img);
        searchImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchDialogAction();
            }
        });

    }
    private void updateDateAndTime() {
        searchParkingDate.setText(dateFormat.format(calendar.getTime()));
        searchParkingTime.setText(timeFormat.format(calendar.getTime()));


    }

    private void searchDialogAction(){
        if (searchSliding.getTag().equals("Search")) {
            searchSliding.animate().setDuration(300).translationY(0).setListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {

                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    searchDialog.setVisibility(View.VISIBLE);
                    searchSliding.setTag("Close");
                }

                @Override
                public void onAnimationCancel(Animator animator) {

                }

                @Override
                public void onAnimationRepeat(Animator animator) {

                }
            });





        } else {
            //close
            searchSliding.startAnimation(new AnimUtils().inFromBottomAnimation());
            searchSliding.animate().setDuration(300).translationY(0).setListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {
                    searchSliding.setTag("Search");
                    searchDialog.setVisibility(View.GONE);

                }

                @Override
                public void onAnimationEnd(Animator animator) {

                }

                @Override
                public void onAnimationCancel(Animator animator) {

                }

                @Override
                public void onAnimationRepeat(Animator animator) {

                }
            });


        }
    }

    @Override
    public void onConnected(Location location) {
        this.location = location;
//        markParkingLocations();
        getSpots(null);
    }

    private void markParkingLocations(){
        if(parkingModels.size() > 0 && googleMap != null){
            googleMap.clear();

            if(parkingModels.size() > 500){
                ClusterManager<ClusterItem> mClusterManager = new ClusterManager<>(ParkingApplication.getAppContext(), googleMap);
                googleMap.setOnCameraChangeListener(mClusterManager);
                for(int i = 0 ; i < parkingModels.size(); i++) {
                    mClusterManager.addItem( parkingModels.get(i).getClusterItem());

                }
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(parkingModels.get(0).getClusterItem().getPosition(), 12));

            }
            else{
                googleMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
                    @Override
                    public void onCameraChange(CameraPosition cameraPosition) {
                        location.setLatitude(cameraPosition.target.latitude);
                        location.setLongitude(cameraPosition.target.longitude);
                    }
                });
                for(int i = 0 ; i < parkingModels.size(); i++) {
                    googleMap.addMarker(new MarkerOptions()
                            .position(new LatLng(Double.valueOf(parkingModels.get(i).getLat()), Double.valueOf(parkingModels.get(i).getLng())))
                            .visible(true).snippet(String.valueOf(i)));
                }
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(parkingModels.get(0).getClusterItem().getPosition(), 25));

            }






        }
    }

    @Override
    public void onDisconnected() {

    }


    private ParkingModel getParkingModel(LatLng latLng){
        ParkingModel parkingModel = new ParkingModel();
        for(int i =0; i < parkingModels.size(); i++){
            if(parkingModels.get(i).getClusterItem().getPosition().latitude == latLng.latitude && parkingModels.get(i).getClusterItem().getPosition().longitude == latLng.longitude){
                parkingModel = parkingModels.get(i);
                break;
            }
        }
        return parkingModel;
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
}
