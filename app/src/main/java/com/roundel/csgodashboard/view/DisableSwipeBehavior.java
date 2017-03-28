package com.roundel.csgodashboard.view;

import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.SwipeDismissBehavior;
import android.view.View;

/**
 * Created by Krzysiek on 2017-03-28.
 */

public class DisableSwipeBehavior extends SwipeDismissBehavior<Snackbar.SnackbarLayout>
{
    @Override
    public boolean canSwipeDismissView(@NonNull View view)
    {
        return false;
    }
}