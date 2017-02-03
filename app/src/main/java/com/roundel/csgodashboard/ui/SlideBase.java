package com.roundel.csgodashboard.ui;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.roundel.csgodashboard.OnBackPressedListener;
import com.roundel.csgodashboard.SlideAction;

/**
 * Created by Krzysiek on 2017-01-25.
 */
public class SlideBase extends Fragment implements OnBackPressedListener
{
    private static final String TAG = SlideBase.class.getSimpleName();
    private static final String ARG_LAYOUT_RES_ID = "layoutResId";

    private ViewGroup root;
    private int layoutResId;
    private SlideAction mSlideActionInterface;

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
        root = (ViewGroup) inflater.inflate(layoutResId, container, false);
        return root;
    }

    public static SlideBase newInstance(int layoutResId)
    {
        SlideBase sampleSlide = new SlideBase();

        Bundle args = new Bundle();
        args.putInt(ARG_LAYOUT_RES_ID, layoutResId);
        sampleSlide.setArguments(args);

        return sampleSlide;
    }

    public void attachSlideActionInterface(SlideAction slideAction)
    {
        this.mSlideActionInterface = slideAction;
    }

    @ColorInt
    public int getBackgroundColor()
    {
        return ((ColorDrawable) root.getBackground()).getColor();
    }

    public ViewGroup getRoot()
    {
        return root;
    }

    public SlideAction getSlideActionInterface()
    {
        return mSlideActionInterface;
    }

    @Override
    public boolean onBackPressed()
    {
        return true;
    }
}
