package com.roundel.csgodashboard.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.Window;
import android.view.WindowManager;

import com.github.paolorotolo.appintro.AppIntro;
import com.roundel.csgodashboard.R;
import com.roundel.csgodashboard.SlideAction;

/**
 * Created by Krzysiek on 2017-01-23.
 */
public class ServerSetupActivity extends AppIntro implements SlideAction, ServerSearchSlide.ServerConnectionInfo, NoWifiSlide.WifiConnectionListener
{
    public static final String TAG = "ServerSetupActivity";

    private NoWifiSlide noWifiSlide;
    private ServerSearchSlide serverSearchSlide;
    private WaitGameSlide waitGameSlide;
    private boolean connecting = false;

    private Fragment currentSlide;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        showSkipButton(false);
        setSeparatorColor(getColor(android.R.color.transparent));

        noWifiSlide = NoWifiSlide.newInstance(R.layout.setup_no_wifi);
        serverSearchSlide = ServerSearchSlide.newInstance(R.layout.setup_server_search);
        waitGameSlide = WaitGameSlide.newInstance(R.layout.setup_wait_game);

        noWifiSlide.attachSlideActionInterface(this);
        noWifiSlide.attachWifiConnectionListener(this);
        serverSearchSlide.attachSlideActionInterface(this);
        serverSearchSlide.attachServerConnectionInfoInterface(this);

        addSlide(noWifiSlide);
        addSlide(serverSearchSlide);
        addSlide(waitGameSlide);
    }

    @Override
    public void onNextPageRequested(Fragment fragment)
    {
        nextButton.performClick();
    }

    @Override
    public void onPreviousPageRequested(Fragment fragment)
    {
        backButton.performClick();
    }

    @Override
    public void onSlideChanged(@Nullable Fragment oldFragment, @Nullable Fragment newFragment)
    {
        super.onSlideChanged(oldFragment, newFragment);
        currentSlide = newFragment;
        onNewSlide((SlideBase) currentSlide);
    }

    @Override
    public void onConnected()
    {

    }

    @Override
    public void onConnecting()
    {
        connecting = true;
    }

    @Override
    public void onFailed()
    {

    }

    @Override
    public void onWifiConnected()
    {
        serverSearchSlide.updateTitleWifi();
    }

    @Override
    public void onBackPressed()
    {
        if(connecting && currentSlide instanceof ServerSearchSlide)
        {
            connecting = false;
            serverSearchSlide.cancelConnectingProcess();
        }
        else
        {
            super.onBackPressed();
        }
    }

    public void onNewSlide(SlideBase slide)
    {
        if(slide instanceof ServerSearchSlide)
        {
            serverSearchSlide.startDiscovery();
        }
        /*window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(slide.getBackgroundColor());*/
    }
}
