package com.roundel.csgodashboard.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatImageButton;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.IndicatorController;
import com.roundel.csgodashboard.R;
import com.roundel.csgodashboard.SlideAction;

/**
 * Created by Krzysiek on 2017-01-23.
 */
public class ServerSetupActivity extends AppIntro implements SlideAction
{
    public static final String TAG = "ServerSetupActivity";

    private NoWifiSlide noWifiSlide;
    private ServerSearchSlide serverSearchSlide;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        showSkipButton(false);
        setSeparatorColor(getColor(android.R.color.transparent));

        noWifiSlide = NoWifiSlide.newInstance(R.layout.setup_no_wifi);
        serverSearchSlide = ServerSearchSlide.newInstance(R.layout.setup_connecting);

        noWifiSlide.attachSlideActionInterface(this);

        addSlide(noWifiSlide);
        addSlide(serverSearchSlide);
    }

    @Override
    public void onNextPageRequested(Fragment fragment)
    {
        nextButton.performClick();
    }

    @Override
    public void onPreviousPageRequested(Fragment fragment)
    {

    }
}
