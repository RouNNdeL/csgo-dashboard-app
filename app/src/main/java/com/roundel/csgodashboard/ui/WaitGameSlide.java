package com.roundel.csgodashboard.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.roundel.csgodashboard.SlideAction;

/**
 * Created by Krzysiek on 2017-01-24.
 */
public class WaitGameSlide extends Fragment
{
    private static final String TAG = WaitGameSlide.class.getSimpleName();

    private static final String ARG_LAYOUT_RES_ID = "layoutResId";
    private int layoutResId;
    private SlideAction mSlideActionInterface;

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

        return view;
    }
}
