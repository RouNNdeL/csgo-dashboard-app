package com.roundel.csgodashboard.ui.slide;

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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.github.paolorotolo.appintro.ISlidePolicy;
import com.roundel.csgodashboard.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Krzysiek on 2017-01-23.
 */
public class NoWifiSlide extends SlideBase implements View.OnClickListener, ISlidePolicy
{
    private static final String TAG = NoWifiSlide.class.getSimpleName();
    private static final String ARG_LAYOUT_RES_ID = "layoutResId";

    @BindView(R.id.setup_wifi_on) Button mTurnWifiButton;
    @BindView(R.id.setup_wifi_settings) Button mOpenWifiSettingsButton;
    @BindView(R.id.setup_wifi_progress) ProgressBar mWifiProgress;

    private SlideAction mSlideActionInterface;
    private WifiConnectionListener mWifiConnectionListener;


    public static NoWifiSlide newInstance()
    {
        NoWifiSlide sampleSlide = new NoWifiSlide();

        Bundle args = new Bundle();
        args.putInt(ARG_LAYOUT_RES_ID, R.layout.setup_no_wifi);
        sampleSlide.setArguments(args);

        return sampleSlide;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        ButterKnife.bind(this, view);

        mOpenWifiSettingsButton.setOnClickListener(this);
        mTurnWifiButton.setOnClickListener(this);

        mTurnWifiButton.setVisibility(View.VISIBLE);
        mWifiProgress.setVisibility(View.GONE);
        mOpenWifiSettingsButton.setVisibility(View.GONE);

        return view;
    }

    @Override
    public void attachSlideActionInterface(SlideAction slideAction)
    {
        super.attachSlideActionInterface(slideAction);
        mSlideActionInterface = getSlideActionInterface();
    }

    @Override
    public void onClick(View v)
    {
        switch(v.getId())
        {
            case R.id.setup_wifi_on:
                turnOnWifi();
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

    public void attachWifiConnectionListener(WifiConnectionListener listener)
    {
        this.mWifiConnectionListener = listener;
    }

    private void turnOnWifi()
    {
        WifiManager wifiManager = (WifiManager) getContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifiManager.setWifiEnabled(true);

        BroadcastReceiver receiver = new BroadcastReceiver()
        {
            @Override
            public void onReceive(Context context, Intent intent)
            {
                WifiManager wifi = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                if(wifi.isWifiEnabled())
                {
                    mOpenWifiSettingsButton.setVisibility(View.VISIBLE);
                    mWifiProgress.setVisibility(View.GONE);
                }
                ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                try
                {
                    if(networkInfo.getType() == ConnectivityManager.TYPE_WIFI && networkInfo.isConnected())
                    {
                        Toast.makeText(context, R.string.setup_wifi_connected, Toast.LENGTH_SHORT).show();
                        if(mSlideActionInterface != null)
                        {
                            new Handler().postDelayed(() -> mSlideActionInterface.onNextPageRequested(getParentFragment()), 250);
                        }
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

        mWifiProgress.setVisibility(View.VISIBLE);
        mTurnWifiButton.setVisibility(View.GONE);
    }

    public interface WifiConnectionListener
    {
        void onWifiConnected();
    }
}
