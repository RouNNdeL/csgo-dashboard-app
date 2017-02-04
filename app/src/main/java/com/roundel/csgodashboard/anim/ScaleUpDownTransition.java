package com.roundel.csgodashboard.anim;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.support.annotation.NonNull;
import android.support.transition.Transition;
import android.support.transition.TransitionValues;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Krzysiek on 2017-02-03.
 */
public class ScaleUpDownTransition extends Transition
{
    private static final String TAG = ScaleUpDownTransition.class.getSimpleName();
    private static final String PROPERTY_HEIGHT = "com.roundel.csgodashboard:scaleupdown:height";
    private static final String PROPERTY_WIDTH = "com.roundel.csgodashboard:scaleupdown:width";
    private static final String PROPERTY_VISIBILITY = "com.roundel.csgodashboard:scaleupdown:visibility";

    @Override
    public void captureStartValues(@NonNull TransitionValues transitionValues)
    {
        //super.captureStartValues(transitionValues);
        captureValues(transitionValues);
    }

    @Override
    public void captureEndValues(@NonNull TransitionValues transitionValues)
    {
        //super.captureEndValues(transitionValues);
        captureValues(transitionValues);
    }

    @Override
    public Animator createAnimator(@NonNull ViewGroup sceneRoot, TransitionValues startValues, TransitionValues endValues)
    {
        int startVisibility = (int) startValues.values.get(PROPERTY_VISIBILITY);
        int endVisibility = (int) endValues.values.get(PROPERTY_VISIBILITY);

        if(startVisibility == endVisibility)
            return null;

        if(startVisibility == View.GONE)
        {
            ObjectAnimator scaleUpX = ObjectAnimator.ofFloat(startValues.view, "scaleX", 0f, 1.0f);
            ObjectAnimator scaleUpY = ObjectAnimator.ofFloat(startValues.view, "scaleY", 0f, 1.0f);

            AnimatorSet set = new AnimatorSet();
            set.play(scaleUpX).with(scaleUpY);
            set.setStartDelay(getDuration());
            return set;
        }
        else if(endVisibility == View.GONE)
        {
            ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(startValues.view, "scaleX", 1.0f, 0f);
            ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(startValues.view, "scaleY", 1.0f, 0f);

            AnimatorSet set = new AnimatorSet();
            set.play(scaleDownX).with(scaleDownY);
            set.setStartDelay(0);
            return set;
        }
        return null;
    }

    private void captureValues(TransitionValues transitionValues)
    {
        View view = transitionValues.view;

        transitionValues.values.put(PROPERTY_VISIBILITY, view.getVisibility());
    }
}
