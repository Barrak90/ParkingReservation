package com.ridecelltask.parkingreservation;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;

import com.ridecelltask.parkingreservation.fragments.FragmentHistory;
import com.ridecelltask.parkingreservation.fragments.FragmentHistoryBase;
import com.ridecelltask.parkingreservation.fragments.FragmentMyCar;
import com.ridecelltask.parkingreservation.fragments.FragmentReservation;
import com.ridecelltask.parkingreservation.fragments.FragmentsContainer;
import com.ridecelltask.parkingreservation.utilities.LocationUtility;
import com.ridecelltask.parkingreservation.fragments.FragmentSearch;

public class HomeActivity extends AppCompatActivity {


    private FragmentTabHost mTabHost;
    private TabWidget tabWidget;


    String [] tabTitles;
    int [] tabIcons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        createTabs();
        tabTitles = new String[]{"Search", "Find", "Reservation", "My Car"};
//        tabIcons =
    }

    private void createTabs(){
        tabWidget = (TabWidget) findViewById(android.R.id.tabs);

        mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        mTabHost.setup(this, getSupportFragmentManager(),
                R.id.tab_content);

        mTabHost.addTab(
                setIndicator(mTabHost.newTabSpec("Search"), R.drawable.selector_search),
                FragmentSearch.class, null);


        mTabHost.addTab(
                setIndicator(mTabHost.newTabSpec("Reservation"), R.drawable.selector_reservation),
                FragmentHistoryBase.class, null);

        mTabHost.addTab(
                setIndicator(mTabHost.newTabSpec("My Car"), R.drawable.selector_my_car),
                FragmentMyCar.class, null);
    }

    public TabHost.TabSpec setIndicator(TabHost.TabSpec spec, int imageId) {
        View tabView = LayoutInflater.from(this).inflate(R.layout.inflate_tab_bottom, null);
        TextView text = (TextView) tabView.findViewById(R.id.inflate_tab_title);
        text.setText(spec.getTag());

        spec.setContent(new TabHost.TabContentFactory() {
            public View createTabContent(String tag) {
                return findViewById(R.id.tab_content);
            }
        });

        ImageView imageView = (ImageView) tabView.findViewById(R.id.inflate_tab_icon);
        imageView.setImageResource(imageId);
        return spec.setIndicator(tabView);
    }

    @Override
    public void onBackPressed() {
        boolean isPopFragment = false;
        try {
            String currentTabTag = mTabHost.getCurrentTabTag();
            if (currentTabTag.equals("Search")) {
                isPopFragment = ((FragmentsContainer) getSupportFragmentManager().findFragmentByTag("Search")).popFragment();
            } else if (currentTabTag.equals("Reservation")) {
                isPopFragment = ((FragmentsContainer) getSupportFragmentManager().findFragmentByTag("Reservation")).popFragment();
            }
            else if (currentTabTag.equals("My Car")) {
                isPopFragment = ((FragmentsContainer) getSupportFragmentManager().findFragmentByTag("My Car")).popFragment();
            }
            if (!isPopFragment) {
                finish();
            }
        } catch (Exception e) {
            e.printStackTrace();

        }

    }
}
