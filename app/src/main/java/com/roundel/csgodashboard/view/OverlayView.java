package com.roundel.csgodashboard.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by Krzysiek on 2017-03-24.
 */

public class OverlayView extends View
{
    //<editor-fold desc="private variables">
    private boolean mInterceptEvents;
//</editor-fold>

    public OverlayView(Context context)
    {
        super(context);
    }

    public OverlayView(Context context, @Nullable AttributeSet attrs)
    {
        super(context, attrs);
    }

    public OverlayView(Context context, @Nullable AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
    }

    public OverlayView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        if(mInterceptEvents)
            performClick();
        return mInterceptEvents;
    }

    public void setInterceptEvents(boolean interceptEvents)
    {
        mInterceptEvents = interceptEvents;
    }
}
