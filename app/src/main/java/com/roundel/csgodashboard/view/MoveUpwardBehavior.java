package com.roundel.csgodashboard.view;

import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.view.View;

import com.roundel.csgodashboard.R;

/**
 * Created by Krzysiek on 2017-03-28.
 */

public class MoveUpwardBehavior extends CoordinatorLayout.Behavior<FloatingActionMenu>
{
    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, FloatingActionMenu child, View dependency)
    {
        return dependency instanceof Snackbar.SnackbarLayout;
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, FloatingActionMenu child, View dependency)
    {
        final Context context = parent.getContext();
        if(!context.getResources().getBoolean(R.bool.isTablet))
        {
            float translationY = Math.min(0, ViewCompat.getTranslationY(dependency) - dependency.getHeight());
            ViewCompat.setTranslationY(child, translationY);
        }
        else if(getViewBounds(child).intersect(getViewBounds(dependency)))
        {
            throw new IllegalStateException(
                    "Snackbar is overlapping FloatingActionButton on a Tablet: " + Build.BRAND + " " + Build.DEVICE);
        }

        return false;
    }

    @Override
    public void onDependentViewRemoved(CoordinatorLayout parent, FloatingActionMenu child, View dependency)
    {
        if(ViewCompat.getTranslationY(child) != 0.0F)
        {
            ViewCompat.animate(child)
                    .translationY(0.0F)
                    .scaleX(1.0F)
                    .scaleY(1.0F)
                    .alpha(1.0F)
                    .setInterpolator(new FastOutSlowInInterpolator())
                    .start();
        }
    }

    private Rect getViewBounds(View view)
    {
        int[] l = new int[2];
        view.getLocationOnScreen(l);
        return new Rect(l[0], l[1], l[0] + view.getWidth(), l[1] + view.getHeight());
    }
}