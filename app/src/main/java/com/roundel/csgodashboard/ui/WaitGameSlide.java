package com.roundel.csgodashboard.ui;

import android.os.Bundle;
import android.view.ViewGroup;

import com.roundel.csgodashboard.SlideAction;

/**
 * Created by Krzysiek on 2017-01-24.
 */
public class WaitGameSlide extends SlideBase
{
    private static final String TAG = WaitGameSlide.class.getSimpleName();
    private static final String ARG_LAYOUT_RES_ID = "layoutResId";
    private int layoutResId;
    private SlideAction mSlideActionInterface;

    private ViewGroup root;

    public static WaitGameSlide newInstance(int layoutResId)
    {
        WaitGameSlide sampleSlide = new WaitGameSlide();

        Bundle args = new Bundle();
        args.putInt(ARG_LAYOUT_RES_ID, layoutResId);
        sampleSlide.setArguments(args);

        return sampleSlide;
    }

    public void attachSlideActionInterface(SlideAction slideAction)
    {
        this.mSlideActionInterface = slideAction;
    }
}
