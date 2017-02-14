package com.roundel.csgodashboard.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Pair;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import com.roundel.csgodashboard.R;
import com.roundel.csgodashboard.entities.MoneyInfo;
import com.roundel.csgodashboard.util.LogHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by Krzysiek on 2017-02-12.
 */
public class MoneyChart extends View
{
    private static final String TAG = MoneyChart.class.getSimpleName();

    //<editor-fold desc="private variables">
    private MoneyInfo values = new MoneyInfo();
    private List<Pair<Float, Float>> valuesCoordinates = new ArrayList<>();

    private int paddingLeft;
    private int paddingRight;
    private int paddingTop;
    private int paddingBottom;

    private int graphPaddingLeft;
    private int graphPaddingRight;
    private int graphPaddingTop;
    private int graphPaddingBottom;

    private Rect mGraphBounds = new Rect();
    private Rect mViewBounds = new Rect();

    private int popupPaddingLeft;
    private int popupPaddingRight;
    private int popupPaddingTop;
    private int popupPaddingBottom;

    private float mGraphHeight;
    private float mGraphWidth;
    private float mActualHeight;
    private float mActualWidth;

    private float scaleY;
    private float scaleX;

    private Paint mLinePaint;
    private Paint mCirclePaint;
    private Paint mCircleSelectedPaint;
    private Paint mCircleHolePaint;
    private Paint mLineHalfPaint;
    private Paint mTextPaint;
    private Paint mPopupPaint;
    private Paint mDebugPaint;

    private int mLineColor;
    private int mLineHalfColor;
    private int mCircleColor;
    private int mCircleSelectedColor;
    private int mCircleHoleColor;

    private float mLineWidth;
    private float mLineHalfWidth;
    private float mCircleSize;
    private float mCircleSelectedSize;
    private float mCircleHoleSize;

    private float mActivationAreaSize;
    private Paint mActivationAreaPaint;
    private boolean mShowActivationArea;

    private boolean mUseHole;
    private boolean mShowHalfLine;
    private boolean mAutoSizeHole;
    private boolean mUseHoleOnSelected;

    private int mTextColor;
    private int mTextSize;
    private int mPopupColor;

    private int mPopupCornerRadius;

    private MoneyInfo.Entry selectedEntry;
    //</editor-fold>

    public MoneyChart(Context context, AttributeSet attrs)
    {
        this(context, attrs, 0);
    }

    public MoneyChart(Context context, AttributeSet attrs, int defStyleAttr)
    {
        this(context, attrs, defStyleAttr, 0);
    }

    public MoneyChart(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);
        final TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.MoneyChart, defStyleAttr, defStyleRes);

        try
        {
            mUseHole = array.getBoolean(R.styleable.MoneyChart_useHole, false);
            mShowHalfLine = array.getBoolean(R.styleable.MoneyChart_showHalfLine, true);
            mAutoSizeHole = array.getBoolean(R.styleable.MoneyChart_autoSizeHole, false);
            mUseHoleOnSelected = array.getBoolean(R.styleable.MoneyChart_showActivationArea, false);

            mLineColor = array.getColor(R.styleable.MoneyChart_lineColor, Color.parseColor("#ffffff"));
            mLineHalfColor = array.getColor(R.styleable.MoneyChart_halfLineColor, Color.parseColor("#ffffff"));
            mCircleColor = array.getColor(R.styleable.MoneyChart_circleColor, Color.parseColor("#ffffff"));
            mCircleSelectedColor = array.getColor(R.styleable.MoneyChart_selectedCircleColor, Color.parseColor("#ffffff"));
            mCircleHoleColor = array.getColor(R.styleable.MoneyChart_circleHoleColor, Color.parseColor("#000000"));
            mTextColor = array.getColor(R.styleable.MoneyChart_textColor, Color.parseColor("#000000"));
            mPopupColor = array.getColor(R.styleable.MoneyChart_popupColor, Color.parseColor("#ffffff"));

            mCircleSize = array.getDimensionPixelSize(
                    R.styleable.MoneyChart_circleSize,
                    (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 6f, getResources().getDisplayMetrics())
            );
            mLineWidth = array.getDimensionPixelSize(
                    R.styleable.MoneyChart_lineWidth,
                    (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3f, getResources().getDisplayMetrics())
            );
            mCircleHoleSize = mAutoSizeHole ?
                    Math.min(mCircleSize - mLineWidth, 0) :
                    array.getDimensionPixelSize(
                            R.styleable.MoneyChart_circleHoleSize,
                            (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3f, getResources().getDisplayMetrics())
                    );
            mCircleSelectedSize = array.getDimensionPixelSize(
                    R.styleable.MoneyChart_selectedCircleSize,
                    (int) (mCircleSize + mLineWidth)
            );
            mLineHalfWidth = array.getDimensionPixelSize(
                    R.styleable.MoneyChart_halfLineWidth,
                    (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, 2f, getResources().getDisplayMetrics())
            );
            mTextSize = array.getDimensionPixelSize(
                    R.styleable.MoneyChart_textSize,
                    (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12f, getResources().getDisplayMetrics())
            );

            mActivationAreaSize = array.getDimensionPixelSize(
                    R.styleable.MoneyChart_activationArea,
                    (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, 10f, getResources().getDisplayMetrics())
            );
            mPopupCornerRadius = array.getDimensionPixelSize(
                    R.styleable.MoneyChart_popupCornerRadius,
                    (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, 19f, getResources().getDisplayMetrics())
            );
        }
        finally
        {
            array.recycle();
        }

        popupPaddingLeft = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, 8f, getResources().getDisplayMetrics());
        popupPaddingRight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, 8f, getResources().getDisplayMetrics());
        popupPaddingTop = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, 8f, getResources().getDisplayMetrics());
        popupPaddingBottom = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, 8f, getResources().getDisplayMetrics());

        graphPaddingLeft = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, 24f, getResources().getDisplayMetrics());
        graphPaddingRight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, 24f, getResources().getDisplayMetrics());
        graphPaddingTop = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, 24f, getResources().getDisplayMetrics());
        graphPaddingBottom = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, 24f, getResources().getDisplayMetrics());

        mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLinePaint.setColor(mLineColor);
        mLinePaint.setStyle(Paint.Style.STROKE);
        mLinePaint.setStrokeWidth(mLineWidth);

        mCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCirclePaint.setColor(mCircleColor);

        mCircleSelectedPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCircleSelectedPaint.setColor(mCircleSelectedColor);

        mCircleHolePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCircleHolePaint.setColor(mCircleHoleColor);

        mLineHalfPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLineHalfPaint.setColor(mLineHalfColor);
        mLineHalfPaint.setStrokeWidth(mLineHalfWidth);
        mLineHalfPaint.setPathEffect(new DashPathEffect(new float[]{10, 10}, 0));

        mActivationAreaPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mActivationAreaPaint.setColor(getContext().getColor(R.color.redFailure));

        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextSize(mTextSize);
        mTextPaint.setColor(mTextColor);

        mPopupPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPopupPaint.setColor(mPopupColor);
        mPopupPaint.setShadowLayer(12, 0, 0, Color.BLACK);

        mDebugPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mDebugPaint.setColor(getContext().getColor(R.color.redFailure));
        mDebugPaint.setStyle(Paint.Style.STROKE);
        mDebugPaint.setStrokeWidth(10);


        this.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        this.setClickable(true);

        if(isInEditMode())
            preview();
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);

        //Draw lines
        float previousX = -1;
        float previousY = -1;
        for(MoneyInfo.Entry entry : values)
        {
            if(previousX != -1 && previousY != -1 && !values.getHalfGameRounds().contains(entry.getRound() - 1))
                canvas.drawLine(previousX, previousY, entry.getX(), entry.getY(), mLinePaint);

            previousX = entry.getX();
            previousY = entry.getY();
        }

        //Draw circles
        for(MoneyInfo.Entry entry : values)
        {
            if(mShowActivationArea)
                canvas.drawCircle(entry.getX(), entry.getY(), mActivationAreaSize, mActivationAreaPaint);
            canvas.drawCircle(entry.getX(), entry.getY(), mCircleSize, mCirclePaint);
            if(mUseHole)
                canvas.drawCircle(entry.getX(), entry.getY(), mCircleHoleSize, mCircleHolePaint);
        }
        if(selectedEntry != null)
        {
            //Draw a selection circle
            if(mUseHoleOnSelected)
            {
                canvas.drawCircle(selectedEntry.getX(), selectedEntry.getY(), mCircleSelectedSize, mCircleSelectedPaint);
                canvas.drawCircle(selectedEntry.getX(), selectedEntry.getY(), mCircleSize, mCirclePaint);
                canvas.drawCircle(selectedEntry.getX(), selectedEntry.getY(), mCircleHoleSize, mCircleHolePaint);
            }
            else
            {
                canvas.drawCircle(selectedEntry.getX(), selectedEntry.getY(), mCircleSelectedSize, mCircleSelectedPaint);
                canvas.drawCircle(selectedEntry.getX(), selectedEntry.getY(), mCircleSize, mCirclePaint);
                canvas.drawCircle(selectedEntry.getX(), selectedEntry.getY(), mCircleHoleSize, mCircleSelectedPaint);
            }

        }


        //Half game line
        for(int halfRound : values.getHalfGameRounds())
            canvas.drawLine(
                    ((halfRound - 0.5f) * scaleX + mGraphBounds.left),
                    mViewBounds.top,
                    ((halfRound - 0.5f) * scaleX + mGraphBounds.left),
                    mViewBounds.bottom,
                    mLineHalfPaint
            );

        if(selectedEntry != null)
            drawPopup(canvas, selectedEntry);

        if(isInEditMode())
        {
            canvas.drawRect(mViewBounds, mDebugPaint);
            canvas.drawRect(mGraphBounds, mDebugPaint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        if(event.getAction() == MotionEvent.ACTION_UP)
        {
            final Pair<MoneyInfo.Entry, Float> closestPair = findClosest(event.getX(), event.getY());

            //final Pair<MoneyInfo.Entry, Float> closestPairNew = findClosest(event.getX(), event.getY(), mActivationAreaSize);

            MoneyInfo.Entry closest = closestPair.first;
            float distance = closestPair.second;
            LogHelper.d(TAG, "Closest entry: Round " + closest.getRound() + ", Money " + closest.getMoney() + " Distance: " + distance);
            if(distance <= mActivationAreaSize)
            {
                selectedEntry = closest;
                invalidate();
            }
            else
            {
                selectedEntry = null;
                invalidate();
            }
        }

        return super.onTouchEvent(event);
    }

    @Override
    protected void onSizeChanged(int width, int height, int oldWidth, int oldHeight)
    {
        super.onSizeChanged(width, height, oldWidth, oldHeight);

        paddingLeft = getPaddingLeft();
        paddingRight = getPaddingRight();
        paddingTop = getPaddingTop();
        paddingBottom = getPaddingBottom();


        float xPadding = (float) (paddingLeft + paddingRight);
        float yPadding = (float) (paddingTop + paddingBottom);

        mActualWidth = width - xPadding;
        mActualHeight = height - yPadding;

        mGraphHeight = mActualHeight - (graphPaddingBottom + graphPaddingTop);
        mGraphWidth = mActualWidth - (graphPaddingLeft + graphPaddingRight);

        mViewBounds.left = paddingLeft;
        mViewBounds.right = width - paddingRight;
        mViewBounds.top = paddingLeft;
        mViewBounds.bottom = height - paddingBottom;

        mGraphBounds.left = mViewBounds.left + graphPaddingLeft;
        mGraphBounds.right = mViewBounds.right - graphPaddingRight;
        mGraphBounds.top = mViewBounds.top + graphPaddingTop;
        mGraphBounds.bottom = mViewBounds.bottom - graphPaddingBottom;

        scaleX = mGraphWidth / (values.getMaxRound() - 1); //We subtract 1 to make the last point appear at the end of the mGraphBounds
        scaleY = mGraphHeight / values.getMaxMoney();

        valuesCoordinates = new ArrayList<>();

        for(MoneyInfo.Entry entry : values)
        {
            entry.setX(getCenterX(entry.getRound()));
            entry.setY(getCenterY(entry.getMoney()));
        }

        if(values.size() > 0 && isInEditMode())
            selectedEntry = values.get((int) (Math.random() * values.size()));
    }

    private float getCenterX(int round)
    {
        return (round - 1) * scaleX + paddingLeft + graphPaddingLeft;
    }

    private float getCenterY(int money)
    {
        //We subtract because the canvas draws from the top-left corner
        return (mGraphHeight + paddingTop + graphPaddingTop) - money * scaleY;
    }

    private float measureDistance(float x1, float y1, float x2, float y2)
    {
        return (float) Math.sqrt(Math.pow(Math.abs(x1 - x2), 2) + Math.pow(Math.abs(y1 - y2), 2));
    }

    /**
     * @param x coordinate of the touch event
     * @param y coordinate of the touch event
     *
     * @return {@link Pair<>} where "first" is the nearest {@link com.roundel.csgodashboard.entities.MoneyInfo.Entry}
     * and "second" is a {@link Long} that is a distance to the nearest {@link
     * com.roundel.csgodashboard.entities.MoneyInfo.Entry}
     */
    private Pair<MoneyInfo.Entry, Float> findClosest(float x, float y)
    {
        MoneyInfo.Entry closest = new MoneyInfo.Entry(-1, -1);
        float closestDistance = -1;
        for(MoneyInfo.Entry entry : values)
        {
            final float distance = measureDistance(entry.getX(), entry.getY(), x, y);
            if(closestDistance == -1 || distance < closestDistance)
            {
                closest = entry;
                closestDistance = distance;
            }
        }
        return new Pair<>(closest, closestDistance);
    }

    private Pair<MoneyInfo.Entry, Float> findClosest(float x, float y, float max_distance)
    {
        MoneyInfo.Entry closest = new MoneyInfo.Entry(-1, -1);
        float closestDistance = -1;
        double sqrt2 = Math.sqrt(2);
        for(MoneyInfo.Entry entry : values)
        {
            if(Math.max(Math.abs(x - entry.getX()), Math.abs(y - entry.getY())) * sqrt2 > max_distance)
                continue;
            final float distance = measureDistance(entry.getX(), entry.getY(), x, y);
            if(closestDistance == -1 || distance < closestDistance)
            {
                closest = entry;
                closestDistance = distance;
            }
        }
        if(closestDistance == -1)
            return null;
        return new Pair<>(closest, closestDistance);
    }

    private void drawPopup(Canvas canvas, MoneyInfo.Entry selectedEntry)
    {
        String roundString = String.format(Locale.getDefault(), "Round %d", selectedEntry.getRound());
        String moneyString = String.format(Locale.getDefault(), "$" +
                "%d", selectedEntry.getMoney());

        Rect roundBounds = new Rect();
        Rect moneyBounds = new Rect();

        mTextPaint.getTextBounds(roundString, 0, roundString.length(), roundBounds);
        mTextPaint.getTextBounds(moneyString, 0, moneyString.length(), moneyBounds);

        int popupHeight = roundBounds.height() + moneyBounds.height() + (popupPaddingTop + popupPaddingBottom * 2);
        int popupWidth = Math.max(roundBounds.width(), moneyBounds.width()) + (popupPaddingLeft + popupPaddingRight * 2);

        Rect popupBounds = new Rect(0, 0, popupWidth, popupHeight);

        offsetRect(roundBounds, (int) selectedEntry.getX() + popupPaddingLeft, (int) selectedEntry.getY() + roundBounds.height() + popupPaddingTop);
        offsetRect(moneyBounds, (int) selectedEntry.getX() + popupPaddingLeft, (int) selectedEntry.getY() + moneyBounds.height() + popupPaddingTop);
        offsetRect(popupBounds, (int) selectedEntry.getX(), (int) selectedEntry.getY());

        /* Offset to center relative to the point
         *       O                        O
         * |-----------|                  |-----------|
         * | Round  21 |    instead of    | Round 21  |
         * | $16000    |                  | $16000    |
         * |-----------|                  |-----------|
         */
        offsetRect(roundBounds, -popupWidth / 2, 0);
        offsetRect(moneyBounds, -popupWidth / 2, roundBounds.height()); //Move down by roundBounds.height() to position right under it
        offsetRect(popupBounds, -popupWidth / 2, 0);

        if(popupBounds.right > mGraphBounds.right)
        {
            final int offset = mGraphBounds.right - popupBounds.right;
            offsetRect(roundBounds, offset, 0);
            offsetRect(moneyBounds, offset, 0);
            offsetRect(popupBounds, offset, 0);
        }
        else if(popupBounds.left < mGraphBounds.left)
        {
            final int offset = mGraphBounds.left - popupBounds.left;
            offsetRect(roundBounds, offset, 0);
            offsetRect(moneyBounds, offset, 0);
            offsetRect(popupBounds, offset, 0);

        }
        if(popupBounds.top < mGraphBounds.top)
        {
            final int offset = popupHeight;
            offsetRect(roundBounds, 0, offset);
            offsetRect(moneyBounds, 0, offset);
            offsetRect(popupBounds, 0, offset);
        }
        else if(popupBounds.bottom > mGraphBounds.bottom)
        {
            final int offset = -popupHeight;
            offsetRect(roundBounds, 0, offset);
            offsetRect(moneyBounds, 0, offset);
            offsetRect(popupBounds, 0, offset);
        }

        canvas.drawRoundRect(new RectF(popupBounds), mPopupCornerRadius, mPopupCornerRadius, mPopupPaint);
        canvas.drawText(roundString, roundBounds.left, roundBounds.bottom, mTextPaint);
        canvas.drawText(moneyString, moneyBounds.left, moneyBounds.bottom, mTextPaint);
    }

    private void offsetRect(Rect source, int x, int y)
    {
        source.left += x;
        source.right += x;
        source.top += y;
        source.bottom += y;
    }

    private void preview()
    {
        values.add(new MoneyInfo.Entry(1, 800));
        values.add(new MoneyInfo.Entry(2, 2400));
        values.add(new MoneyInfo.Entry(3, 1550));
        values.add(new MoneyInfo.Entry(4, 3850));
        values.add(new MoneyInfo.Entry(5, 1900));
        values.add(new MoneyInfo.Entry(6, 4500));
        values.add(new MoneyInfo.Entry(7, 6700));
        values.add(new MoneyInfo.Entry(8, 10800));
        values.add(new MoneyInfo.Entry(9, 12900));
        values.add(new MoneyInfo.Entry(10, 8000));
        values.add(new MoneyInfo.Entry(11, 6570));
        values.add(new MoneyInfo.Entry(12, 2300));
        values.add(new MoneyInfo.Entry(13, 2400));
        values.add(new MoneyInfo.Entry(14, 4900));
        values.add(new MoneyInfo.Entry(15, 7500));
        values.add(new MoneyInfo.Entry(16, 800));
        values.add(new MoneyInfo.Entry(17, 4400));
        values.add(new MoneyInfo.Entry(18, 7000));
        values.add(new MoneyInfo.Entry(19, 10250));
        values.add(new MoneyInfo.Entry(20, 13500));
        values.add(new MoneyInfo.Entry(21, 16000));
        values.add(new MoneyInfo.Entry(22, 13123));
        values.add(new MoneyInfo.Entry(23, 3237));
        values.add(new MoneyInfo.Entry(24, 2313));
        values.add(new MoneyInfo.Entry(25, 8123));
        values.add(new MoneyInfo.Entry(26, 0));
        values.add(new MoneyInfo.Entry(27, 1300));
        values.add(new MoneyInfo.Entry(28, 12312));
        values.add(new MoneyInfo.Entry(29, 13233));
        values.add(new MoneyInfo.Entry(30, 8248));
        values.add(new MoneyInfo.Entry(31, 16000));
        values.add(new MoneyInfo.Entry(32, 11000));
        values.add(new MoneyInfo.Entry(33, 4600));
        values.add(new MoneyInfo.Entry(34, 6000));
        values.add(new MoneyInfo.Entry(35, 6845));
        values.add(new MoneyInfo.Entry(36, 16000));
        values.add(new MoneyInfo.Entry(37, 12700));

        values.addHalfGameRound(15);
        values.addHalfGameRound(30);
        values.addHalfGameRound(35);
    }
}
