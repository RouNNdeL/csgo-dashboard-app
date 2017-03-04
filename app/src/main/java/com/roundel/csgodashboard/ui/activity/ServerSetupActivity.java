package com.roundel.csgodashboard.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.Window;
import android.view.WindowManager;

import com.github.paolorotolo.appintro.AppIntro;
import com.roundel.csgodashboard.ui.slide.NoWifiSlide;
import com.roundel.csgodashboard.ui.slide.ServerSearchSlide;
import com.roundel.csgodashboard.ui.slide.SlideAction;
import com.roundel.csgodashboard.ui.slide.SlideBase;
import com.roundel.csgodashboard.ui.slide.WaitGameSlide;

/**
 * Created by Krzysiek on 2017-01-23.
 */
public class ServerSetupActivity extends AppIntro implements SlideAction, ServerSearchSlide.ServerConnectionInfo, NoWifiSlide.WifiConnectionListener
{
    private static final String TAG = ServerSetupActivity.class.getSimpleName();

    //<editor-fold desc="private variables">
    private NoWifiSlide noWifiSlide;
    private ServerSearchSlide serverSearchSlide;
    private WaitGameSlide waitGameSlide;
    private SlideBase mCurrentSlide;

    private Fragment currentSlide;
//</editor-fold>

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        showSkipButton(false);
        setSeparatorColor(getColor(android.R.color.transparent));

        noWifiSlide = NoWifiSlide.newInstance();
        serverSearchSlide = ServerSearchSlide.newInstance();
        waitGameSlide = WaitGameSlide.newInstance();

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
        if(mCurrentSlide.onBackPressed())
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
        mCurrentSlide = slide;
        /*window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(slide.getBackgroundColor());*/
    }
}
