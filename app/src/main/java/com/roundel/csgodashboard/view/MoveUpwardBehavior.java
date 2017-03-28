package com.roundel.csgodashboard.view;

import android.graphics.Rect;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.view.View;

/**
 * Created by Krzysiek on 2017-03-28.
 */

public class MoveUpwardBehavior extends CoordinatorLayout.Behavior<FloatingActionMenu>
{
    private int originalPadding = 0;
    private float previousTranslation;
    private boolean didIntersect;

    private static boolean areViewsOverlapping(View firstView, View secondView)
    {

        int[] firstPosition = new int[2];
        int[] secondPosition = new int[2];

        firstView.getLocationOnScreen(firstPosition);
        secondView.getLocationOnScreen(secondPosition);

        // Rect constructor parameters: left, top, right, bottom
        Rect rectFirstView = new Rect(firstPosition[0], firstPosition[1],
                firstPosition[0] + firstView.getMeasuredWidth(), firstPosition[1] + firstView.getMeasuredHeight()
        );
        Rect rectSecondView = new Rect(secondPosition[0], secondPosition[1],
                secondPosition[0] + secondView.getMeasuredWidth(), secondPosition[1] + secondView.getMeasuredHeight()
        );
        return rectFirstView.intersect(rectSecondView);
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, FloatingActionMenu child, View dependency)
    {
        return dependency instanceof Snackbar.SnackbarLayout;
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, FloatingActionMenu child, View dependency)
    {
        float translationY = Math.min(0, dependency.getTranslationY() - dependency.getHeight());
        if(translationY == 0)
        {
            originalPadding = child.getPaddingBottom();
            didIntersect = false;
        }
        final boolean intersect = getViewBounds(dependency).intersect(getViewBounds(child.getMenuIconView()));
        didIntersect = intersect || didIntersect;
        if(intersect || (translationY != previousTranslation && didIntersect))
        {
            child.setPadding(child.getPaddingLeft(), child.getPaddingTop(), child.getPaddingRight(), (int) (originalPadding - translationY));
            return true;
        }
        previousTranslation = translationY;
        return false;
    }

    private Rect getViewBounds(View view)
    {
        int[] l = new int[2];
        view.getLocationOnScreen(l);
        return new Rect(l[0], l[1], l[0] + view.getWidth(), l[1] + view.getHeight());
    }
}