package com.roundel.csgodashboard.util;

import android.support.design.widget.AppBarLayout;

/*
 * From: http://stackoverflow.com/a/33891727/4061413
 */

public abstract class AppBarStateChangeListener implements AppBarLayout.OnOffsetChangedListener
{

    private State mCurrentState = State.IDLE;

    @Override
    public final void onOffsetChanged(AppBarLayout appBarLayout, int i)
    {
        if(i == 0)
        {
            if(mCurrentState != State.EXPANDED)
            {
                onStateChanged(appBarLayout, State.EXPANDED);
            }
            mCurrentState = State.EXPANDED;
        }
        else if(Math.abs(i) >= appBarLayout.getTotalScrollRange())
        {
            if(mCurrentState != State.COLLAPSED)
            {
                onStateChanged(appBarLayout, State.COLLAPSED);
            }
            mCurrentState = State.COLLAPSED;
        }
        else
        {
            if(mCurrentState != State.IDLE)
            {
                onStateChanged(appBarLayout, State.IDLE);
            }
            mCurrentState = State.IDLE;
        }
    }

    public abstract void onStateChanged(AppBarLayout appBarLayout, State state);

    public enum State
    {
        EXPANDED,
        COLLAPSED,
        IDLE
    }
}