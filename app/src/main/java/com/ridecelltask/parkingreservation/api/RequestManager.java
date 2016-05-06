package com.ridecelltask.parkingreservation.api;

import android.content.Context;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.HttpClientStack;
import com.android.volley.toolbox.Volley;

/**
 * Created by Albarrak on 5/4/16.
 */
public class RequestManager {
    private static RequestManager instance;
    private RequestQueue mDataRequestQueue;
    Context mContext;

    public static RequestManager getInstance() {
        return instance;
    }

    private RequestManager(Context context) {
        this.mContext = context;

    }

    public static synchronized RequestManager initializeRequestManager(Context context) {
        if (instance == null) {
            instance = new RequestManager(context);
        }
        return instance;
    }


    private synchronized RequestQueue getDataRequestQueue() {
        if (mDataRequestQueue == null) {
            mDataRequestQueue = Volley.newRequestQueue(mContext.getApplicationContext());
            mDataRequestQueue.start();
        }
        return mDataRequestQueue;
    }

    public static <T> void addRequest(Request<T> pRequest) {
        if (instance == null) {
            throw new IllegalStateException(RequestManager.class.getSimpleName() +
                    " is not initialized, call initializeWith(..) method first.");
        }
        if (pRequest.getTag() == null) {
            new IllegalArgumentException("Request Object Tag is not specified.");
        }
        pRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS , 3, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue queue = instance.getDataRequestQueue();

        queue.add(pRequest);
    }

}
