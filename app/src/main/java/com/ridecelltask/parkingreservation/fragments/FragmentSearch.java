package com.ridecelltask.parkingreservation.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ridecelltask.parkingreservation.R;

public class FragmentSearch extends FragmentsContainer {

    private boolean mIsViewInitialed;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.container_fragment, null);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (!mIsViewInitialed) {
            mIsViewInitialed = true;
            initView();
        }
    }

    private void initView() {
        replaceFragment(new FragmentSearchMap(), false, null, "Initiate");
    }
}
