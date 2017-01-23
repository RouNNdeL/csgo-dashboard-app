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

/**
 * Created by Krzysiek on 2017-01-23.
 */
public class ServerSetupActivity extends AppIntro
{
    public static final String TAG = "ServerSetupActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        showSkipButton(false);
        setSeparatorColor(getColor(android.R.color.transparent));
        addSlide(NoWifiSlide.newInstance(R.layout.setup_no_wifi));
        addSlide(ServerSearchSlide.newInstance(R.layout.setup_connecting));

        //TODO: Fork AppIntro repo and add functions to prevent going to the next page
        //canNavigateToNextPage(isWiFiOn);
    }

    @Override
    public void onSlideChanged(@Nullable Fragment oldFragment, @Nullable Fragment newFragment)
    {
        /*if(newFragment instanceof NoWifiSlide)
        {
            Log.d(TAG, "Instance of NoWifiSlide");
            //setIndicatorColor(R.color.fuckingRED, R.color.fuckingRED);
            setNextArrowColor(getColor(android.R.color.white));
            setColorDoneText(getColor(android.R.color.white));
        }
        else if(newFragment instanceof ServerSearchSlide)
        {
            Log.d(TAG, "Instance of ServerSearchSlide");
            //setIndicatorColor();
            setNextArrowColor(getColor(R.color.colorMaterialDark54));
            setColorDoneText(getColor(R.color.colorMaterialDark54));
        }*/
        super.onSlideChanged(oldFragment, newFragment);
    }
}
