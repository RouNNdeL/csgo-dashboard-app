package com.roundel.csgodashboard.ui.slide;

import android.support.v4.app.Fragment;

/**
 * Created by Krzysiek on 2017-01-24.
 */

public interface SlideAction
{
    void onNextPageRequested(Fragment fragment);

    void onPreviousPageRequested(Fragment fragment);
}
