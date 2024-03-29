package com.roundel.csgodashboard.ui.slide;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.roundel.csgodashboard.ui.OnBackPressedListener;

/**
 * Created by Krzysiek on 2017-01-25.
 */
public class SlideBase extends Fragment implements OnBackPressedListener
{
    private static final String TAG = SlideBase.class.getSimpleName();
    private static final String ARG_LAYOUT_RES_ID = "layoutResId";
    private int layoutResId;
    private SlideAction mSlideActionInterface;

    public static SlideBase newInstance(int layoutResId)
    {
        SlideBase sampleSlide = new SlideBase();

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
        return inflater.inflate(layoutResId, container, false);
    }

    @Override
    public boolean onBackPressed()
    {
        return true;
    }

    public void attachSlideActionInterface(SlideAction slideAction)
    {
        this.mSlideActionInterface = slideAction;
    }

    public SlideAction getSlideActionInterface()
    {
        return mSlideActionInterface;
    }
}
