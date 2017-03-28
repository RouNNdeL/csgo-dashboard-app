package com.roundel.csgodashboard.view;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;

/**
 * Created by Krzysiek on 2017-03-28.
 */

@CoordinatorLayout.DefaultBehavior(MoveUpwardBehavior.class)
public class FloatingActionMenu extends com.github.clans.fab.FloatingActionMenu
{
    public FloatingActionMenu(Context context)
    {
        super(context);
    }

    public FloatingActionMenu(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public FloatingActionMenu(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
    }
}
