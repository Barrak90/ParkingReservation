package com.ridecelltask.parkingreservation.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import com.ridecelltask.parkingreservation.R;

/**
 * Created by Albarrak on 5/4/16.
 */
public class FragmentsContainer extends Fragment {


    public void replaceFragment(Fragment fragment, boolean addToBackStack, Bundle bundle, String tag) {
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        if(addToBackStack)
        {
            getChildFragmentManager().popBackStack(tag, FragmentManager.POP_BACK_STACK_INCLUSIVE);

            transaction.addToBackStack(tag);
        }
        if(bundle!=null){
            fragment.setArguments(bundle);
        }

        transaction.replace(R.id.container_frame_layout, fragment, tag);
        transaction.commit();
        getChildFragmentManager().beginTransaction();
    }


    public boolean popFragment() {
        boolean isPop = false;
        if (getChildFragmentManager().getBackStackEntryCount() > 0)
        {

            isPop = true;
            getChildFragmentManager().popBackStack();
        }
        return isPop;
    }

}
