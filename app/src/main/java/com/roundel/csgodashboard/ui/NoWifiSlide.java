package com.roundel.csgodashboard.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.transition.Slide;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.github.paolorotolo.appintro.ISlidePolicy;
import com.roundel.csgodashboard.R;
import com.roundel.csgodashboard.SlideAction;


import static android.view.View.*;
import static android.view.View.GONE;

/**
 * Created by Krzysiek on 2017-01-23.
 */
public class NoWifiSlide extends Fragment implements OnClickListener, ISlidePolicy
{
    public static final String TAG = "NoWifiSlide";

    private static final String ARG_LAYOUT_RES_ID = "layoutResId";
    private int layoutResId;

    private Button mTurnWifiButton;
    private Button mOpenWifiSettingsButton;
    private ProgressBar mWifiProgress;
    private SlideAction mSlideActionInterface;

    public static NoWifiSlide newInstance(int layoutResId)
    {
        NoWifiSlide sampleSlide = new NoWifiSlide();

        Bundle args = new Bundle();
        args.putInt(ARG_LAYOUT_RES_ID, layoutResId);
        sampleSlide.setArguments(args);

        return sampleSlide;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        if(getArguments() != null && getArguments().containsKey(ARG_LAYOUT_RES_ID))
        {
            layoutResId = getArguments().getInt(ARG_LAYOUT_RES_ID);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        final View view = inflater.inflate(layoutResId, container, false);

        mTurnWifiButton = (Button) view.findViewById(R.id.setup_wifi_on);
        mOpenWifiSettingsButton = (Button) view.findViewById(R.id.setup_wifi_settings);
        mWifiProgress = (ProgressBar) view.findViewById(R.id.setup_wifi_progress);

        mOpenWifiSettingsButton.setOnClickListener(this);
        mTurnWifiButton.setOnClickListener(this);
        return view;
    }

    public void attachSlideActionInterface(SlideAction slideAction)
    {
        this.mSlideActionInterface = slideAction;
    }

    @Override
    public void onClick(View v)
    {
        switch(v.getId())
        {
            case R.id.setup_wifi_on:
                WifiManager wifiManager = (WifiManager) getActivity().getSystemService(Context.WIFI_SERVICE);
                wifiManager.setWifiEnabled(true);

                BroadcastReceiver receiver = new BroadcastReceiver()
                {
                    @Override
                    public void onReceive(Context context, Intent intent)
                    {
                        WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                        if(wifi.isWifiEnabled())
                        {
                            mOpenWifiSettingsButton.setVisibility(VISIBLE);
                            mWifiProgress.setVisibility(GONE);
                        }
                        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                        try
                        {
                            if(networkInfo.getType() == ConnectivityManager.TYPE_WIFI && networkInfo.isConnected())
                            {
                                Toast.makeText(context, R.string.setup_wifi_connected, Toast.LENGTH_SHORT).show();
                                new Handler().postDelayed(new Runnable()
                                {
                                    @Override
                                    public void run()
                                    {
                                        if(mSlideActionInterface != null)
                                            mSlideActionInterface.onNextPageRequested(getParentFragment());
                                    }
                                }, 500);
                            }
                        }
                        catch(NullPointerException e)
                        {
                        }
                    }
                };
                IntentFilter intentFilter = new IntentFilter();
                intentFilter.addAction(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION);
                intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
                getContext().registerReceiver(receiver, intentFilter);

                mWifiProgress.setVisibility(VISIBLE);
                mTurnWifiButton.setVisibility(GONE);
                break;
            case R.id.setup_wifi_settings:
                startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
        }
    }

    @Override
    public boolean isPolicyRespected()
    {
        ConnectivityManager connectivityManager = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        try
        {
            return networkInfo.getType() == ConnectivityManager.TYPE_WIFI && networkInfo.isConnected();
        }
        catch(NullPointerException e)
        {
            return false;
        }
    }

    @Override
    public void onUserIllegallyRequestedNextPage()
    {
        Toast.makeText(getContext(), R.string.setup_wifi_connect_hint, Toast.LENGTH_SHORT).show();
    }
}
