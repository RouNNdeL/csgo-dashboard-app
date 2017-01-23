package com.roundel.csgodashboard.ui;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.roundel.csgodashboard.R;

/**
 * Created by Krzysiek on 2017-01-23.
 */
public class NoWifiSlide extends Fragment implements View.OnClickListener
{
    public static final String TAG = "NoWifiSlide";

    private static final String ARG_LAYOUT_RES_ID = "layoutResId";
    private int layoutResId;

    public static NoWifiSlide newInstance(int layoutResId) {
        NoWifiSlide sampleSlide = new NoWifiSlide();

        Bundle args = new Bundle();
        args.putInt(ARG_LAYOUT_RES_ID, layoutResId);
        sampleSlide.setArguments(args);

        return sampleSlide;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null && getArguments().containsKey(ARG_LAYOUT_RES_ID)) {
            layoutResId = getArguments().getInt(ARG_LAYOUT_RES_ID);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(layoutResId, container, false);
    }

    @Override
    public void onClick(View v)
    {
        switch(v.getId())
        {
            case R.id.setup_wifi_on:
                WifiManager wifiManager = (WifiManager) getActivity().getSystemService(Context.WIFI_SERVICE);
                wifiManager.setWifiEnabled(true);
        }
    }
}
