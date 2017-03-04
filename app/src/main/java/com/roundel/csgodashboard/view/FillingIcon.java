package com.roundel.csgodashboard.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.ColorInt;
import android.support.annotation.Px;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import com.roundel.csgodashboard.R;
import com.transitionseverywhere.PathParser;

/**
 * Created by Krzysiek on 2017-02-14.
 */
public class FillingIcon extends View
{
    private static final String TAG = FillingIcon.class.getSimpleName();
    //<editor-fold desc="private variables">
    private Paint mDebugPaint;
    private Paint mStrokePaint;
    private Paint mFillPaint;
    private Paint mBackgroundPaint;

    private @ColorInt int mStrokeColor;
    private @ColorInt int mFillColor;
    private @ColorInt int mBackgroundColor;

    private boolean mAnimateValueChanges;

    private @Px int mStrokeWidth;

    private String mPathData;

    private Path mPath;
    private Rect mFillRect = new Rect();
    private RectF mPathBounds = new RectF();

    private float mFillValue = 0.01f;

    private int mDirection;

    private int mValueAnimationDuration;
    //</editor-fold>


    public FillingIcon(Context context)
    {
        super(context);
    }

    public FillingIcon(Context context, AttributeSet attrs)
    {
        this(context, attrs, 0);
    }

    public FillingIcon(Context context, AttributeSet attrs, int defStyleAttr)
    {
        this(context, attrs, defStyleAttr, 0);
    }

    public FillingIcon(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);

        mValueAnimationDuration = getResources().getInteger(R.integer.filling_icon_anim_duration);

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.FillingIcon, defStyleAttr, defStyleRes);
        try
        {
            mAnimateValueChanges = array.getBoolean(R.styleable.FillingIcon_animateValueChanges, true);

            mFillColor = array.getColor(R.styleable.FillingIcon_fillColor, Color.WHITE);
            mStrokeColor = array.getColor(R.styleable.FillingIcon_strokeColor, Color.WHITE);
            mBackgroundColor = array.getColor(R.styleable.FillingIcon_backgroundColor, Color.TRANSPARENT);

            mStrokeWidth = array.getDimensionPixelSize(
                    R.styleable.FillingIcon_strokeWidth,
                    (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2f, getResources().getDisplayMetrics())
            );

            mDirection = array.getInteger(R.styleable.FillingIcon_direction, 0);

            if(!array.hasValue(R.styleable.FillingIcon_pathData))
                throw new RuntimeException("Path data is required to use the ValueFillingIcon view");
            mPathData = array.getString(R.styleable.FillingIcon_pathData);
        }
        finally
        {
            array.recycle();
        }

        if(isInEditMode())
            mPathData = "L10 10 L10 0 Z";
        mPath = PathParser.createPathFromPathData(mPathData);

        mFillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mFillPaint.setColor(mFillColor);
        mFillPaint.setStyle(Paint.Style.FILL);

        mStrokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mStrokePaint.setColor(mStrokeColor);
        mStrokePaint.setStyle(Paint.Style.STROKE);
        mStrokePaint.setStrokeWidth(mStrokeWidth);

        mBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBackgroundPaint.setColor(mBackgroundColor);
        mBackgroundPaint.setStyle(Paint.Style.FILL);

        mDebugPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mDebugPaint.setColor(getContext().getColor(R.color.redFailure));
        mDebugPaint.setStyle(Paint.Style.STROKE);
        mDebugPaint.setStrokeWidth(10);

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh)
    {
        int actualWidth = w - (getPaddingRight() + getPaddingLeft());
        int actualHeight = h - (getPaddingBottom() + getPaddingTop());

        mPath.computeBounds(mPathBounds, false);

        float scaleY = actualHeight / mPathBounds.height();
        float scaleX = actualWidth / mPathBounds.width();

        float scale = Math.min(scaleX, scaleY);

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(scale, scale);
        mPath.transform(scaleMatrix);

        mPath.computeBounds(mPathBounds, false);

        mPath.offset(getPaddingLeft(), getPaddingTop());

        Matrix offsetMatrix = new Matrix();
        if(mPathBounds.height() < actualHeight)
        {
            offsetMatrix.setTranslate(0, (actualHeight - mPathBounds.height()) / 2);
        }
        if(mPathBounds.width() < actualWidth)
        {
            offsetMatrix.setTranslate((actualWidth - mPathBounds.width()) / 2, 0);
        }
        mPath.transform(offsetMatrix);

        mPath.computeBounds(mPathBounds, false);

        computeFillBounds();
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);

        canvas.drawPath(mPath, mStrokePaint);

        canvas.clipPath(mPath);
        canvas.drawRect(mPathBounds, mBackgroundPaint);
        canvas.drawRect(mFillRect, mFillPaint);
    }

    private void computeFillBounds()
    {
        final int layoutDirection = getResources().getConfiguration().getLayoutDirection();

        if((mDirection == 2 && layoutDirection == LAYOUT_DIRECTION_LTR) || (mDirection == 3 && layoutDirection == LAYOUT_DIRECTION_RTL))
            mFillRect.left = (int) Math.ceil(mPathBounds.left + (mPathBounds.width() - mStrokeWidth / 2) * (1 - mFillValue));
        else
            mFillRect.left = (int) Math.ceil(mPathBounds.left);

        if((mDirection == 3 && layoutDirection == LAYOUT_DIRECTION_LTR) || (mDirection == 2 && layoutDirection == LAYOUT_DIRECTION_RTL))
            mFillRect.right = (int) Math.ceil(mPathBounds.right - (mPathBounds.width() - mStrokeWidth / 2) * (1 - mFillValue));
        else
            mFillRect.right = (int) Math.ceil(mPathBounds.right);

        if(mDirection == 0)
            mFillRect.top = (int) Math.ceil(mPathBounds.top + (mPathBounds.height() - mStrokeWidth / 2) * (1 - mFillValue));  //Account for stroke going into the fill area
        else
            mFillRect.top = (int) Math.ceil(mPathBounds.top - mStrokeWidth / 2);

        if(mDirection == 1)
            mFillRect.bottom = (int) Math.ceil(mPathBounds.bottom - (mPathBounds.height() - mStrokeWidth / 2) * (1 - mFillValue));  //Account for stroke going into the fill area
        else
            mFillRect.bottom = (int) Math.ceil(mPathBounds.bottom - mStrokeWidth / 2);   //Account for stroke going into the fill area
    }

    private void animateFillValue(float from, float to)
    {
        ValueAnimator animator = ValueAnimator.ofFloat(from, to);
        animator.addUpdateListener(animation -> {
            if(animation.getAnimatedValue() != null)
            {
                mFillValue = (float) animation.getAnimatedValue();
                computeFillBounds();
                invalidate();
            }
        });
        int duration = (int) (mValueAnimationDuration * Math.abs(from - to));
        animator.setDuration(duration);
        animator.start();
    }

    public float getFillValue()
    {
        return mFillValue;
    }

    public void setFillValue(float fillValue)
    {
        if(fillValue > 1 || fillValue < 0)
            throw new IllegalArgumentException("The fillValue has to be between 0 and 1, was " + fillValue);

        if(mAnimateValueChanges)
        {
            animateFillValue(mFillValue, fillValue);
        }
        else
        {
            mFillValue = fillValue;
            invalidate();
        }

    }

    @ColorInt
    public int getBackgroundColor()
    {
        return mBackgroundColor;
    }

    public void setBackgroundColor(@ColorInt int mBackgroundColor)
    {
        this.mBackgroundColor = mBackgroundColor;
        this.mBackgroundPaint.setColor(mBackgroundColor);
        invalidate();
    }

    public boolean getAnimateValueChanges()
    {
        return mAnimateValueChanges;
    }

    public void setAnimateValueChanges(boolean mAnimateValueChanges)
    {
        this.mAnimateValueChanges = mAnimateValueChanges;
    }

    @Px
    public int getStrokeWidth()
    {
        return mStrokeWidth;
    }

    public void setStrokeWidth(@Px int mStrokeWidth)
    {
        this.mStrokeWidth = mStrokeWidth;
        this.mStrokePaint.setStrokeWidth(mStrokeWidth);
        invalidate();
    }

    public String getPathData()
    {
        return mPathData;
    }

    public void setPathData(String mPathData)
    {
        this.mPathData = mPathData;
        invalidate();
    }

    @ColorInt
    public int getStrokeColor()
    {
        return mStrokeColor;
    }

    public void setStrokeColor(@ColorInt int mStrokeColor)
    {
        this.mStrokeColor = mStrokeColor;
        this.mStrokePaint.setColor(mStrokeColor);
        invalidate();
    }

    @ColorInt
    public int getFillColor()
    {
        return mFillColor;
    }

    public void setFillColor(@ColorInt int mFillColor)
    {
        this.mFillColor = mFillColor;
        this.mFillPaint.setColor(mFillColor);
        invalidate();
    }
}
