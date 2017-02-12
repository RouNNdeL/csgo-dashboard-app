package com.roundel.csgodashboard.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Pair;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.roundel.csgodashboard.R;
import com.roundel.csgodashboard.entities.MoneyInfo;
import com.roundel.csgodashboard.util.LogHelper;

import java.util.ArrayList;
import java.util.List;

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

    private int mTextColor;
    private int mTextSize;

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
            mShowActivationArea = array.getBoolean(R.styleable.MoneyChart_showActivationArea, false);

            mLineColor = array.getColor(R.styleable.MoneyChart_lineColor, Color.parseColor("#ffffff"));
            mLineHalfColor = array.getColor(R.styleable.MoneyChart_halfLineColor, Color.parseColor("#ffffff"));
            mCircleColor = array.getColor(R.styleable.MoneyChart_circleColor, Color.parseColor("#ffffff"));
            mCircleSelectedColor = array.getColor(R.styleable.MoneyChart_selectedCircleColor, Color.parseColor("#ffffff"));
            mCircleHoleColor = array.getColor(R.styleable.MoneyChart_circleHoleColor, Color.parseColor("#000000"));

            mCircleSize = array.getDimensionPixelSize(
                    R.styleable.MoneyChart_circleSize,
                    (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 6f, getResources().getDisplayMetrics())
            );
            mCircleSelectedSize = array.getDimensionPixelSize(
                    R.styleable.MoneyChart_selectedCircleSize,
                    (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 9f, getResources().getDisplayMetrics())
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
            mLineHalfWidth = array.getDimensionPixelSize(
                    R.styleable.MoneyChart_halfLineWidth,
                    (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, 2f, getResources().getDisplayMetrics())
            );

            mActivationAreaSize = array.getDimensionPixelSize(
                    R.styleable.MoneyChart_activationArea,
                    (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, 10f, getResources().getDisplayMetrics())
            );
        }
        finally
        {
            array.recycle();
        }

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

        this.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        this.setClickable(true);

        init();
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
            if(previousX != -1 && previousY != -1 && entry.getRound() != values.getHalfGameRound() + 1)
                canvas.drawLine(previousX, previousY, entry.getX(), entry.getY(), mLinePaint);

            previousX = entry.getX();
            previousY = entry.getY();
        }

        //Draw circles
        for(MoneyInfo.Entry entry : values)
        {
            if(mShowActivationArea)
                canvas.drawCircle(entry.getX(), entry.getY(), mActivationAreaSize, mActivationAreaPaint);
            if(entry == selectedEntry)
            {
                canvas.drawCircle(entry.getX(), entry.getY(), mCircleSize + mLineWidth, mCircleSelectedPaint);
            }
            canvas.drawCircle(entry.getX(), entry.getY(), mCircleSize, mCirclePaint);
            if(mUseHole)
                canvas.drawCircle(entry.getX(), entry.getY(), mCircleHoleSize, mCircleHolePaint);
        }

        //Half game line
        if(values.size() > values.getHalfGameRound() && mShowHalfLine)
            canvas.drawLine(
                    ((values.getHalfGameRound() - 0.5f) * scaleX + paddingLeft),
                    paddingTop,
                    ((values.getHalfGameRound() - 0.5f) * scaleX + paddingLeft),
                    mGraphWidth + paddingBottom,
                    mLineHalfPaint
            );
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
                //TODO: Show a rectangle with more detailed information about he Entry
                Toast.makeText(getContext(), "Clicked on round " + closest.getRound(), Toast.LENGTH_SHORT).show();
                selectedEntry = closest;
                invalidate();
            }
            else
            {
                selectedEntry = null;
                invalidate();
            }
        }
        ;

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

        mActualHeight = width - xPadding;
        mActualWidth = height - yPadding;

        mGraphHeight = mActualHeight - (graphPaddingBottom + graphPaddingTop);
        mGraphWidth = mActualWidth - (graphPaddingLeft + graphPaddingRight);

        scaleX = mGraphHeight / values.getMaxRound();
        scaleY = mGraphWidth / values.getMaxMoney();

        valuesCoordinates = new ArrayList<>();

        for(MoneyInfo.Entry entry : values)
        {
            entry.setX(getCenterX(entry.getRound()));
            entry.setY(getCenterY(entry.getMoney()));
        }
    }

    private float getCenterX(int round)
    {
        return (round - 1) * scaleX + paddingLeft + graphPaddingLeft;
    }

    private float getCenterY(int money)
    {
        //We subtract because the canvas draws from the top-left corner
        return (mGraphWidth + paddingTop + graphPaddingTop) - money * scaleY;
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
     * and "second" is a {@link long} that is a distance to the nearest {@link
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

    private void init()
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
        values.add(new MoneyInfo.Entry(26, 1233));
        values.add(new MoneyInfo.Entry(27, 1300));
        values.add(new MoneyInfo.Entry(28, 12312));
        values.add(new MoneyInfo.Entry(29, 13233));
        values.add(new MoneyInfo.Entry(30, 8248));
        values.add(new MoneyInfo.Entry(31, 2834));
        values.add(new MoneyInfo.Entry(32, 1238));
        values.add(new MoneyInfo.Entry(33, 1773));
        values.add(new MoneyInfo.Entry(34, 2344));
        values.add(new MoneyInfo.Entry(35, 6845));
        values.add(new MoneyInfo.Entry(36, 800));
        values.add(new MoneyInfo.Entry(37, 9242));
        values.add(new MoneyInfo.Entry(38, 16000));
        values.add(new MoneyInfo.Entry(39, 7342));
        values.add(new MoneyInfo.Entry(40, 12383));
        values.add(new MoneyInfo.Entry(41, 10123));
        values.add(new MoneyInfo.Entry(42, 13238));
        values.add(new MoneyInfo.Entry(43, 12737));
        values.add(new MoneyInfo.Entry(44, 12311));
        values.add(new MoneyInfo.Entry(45, 81381));
        values.add(new MoneyInfo.Entry(46, 12638));
        values.add(new MoneyInfo.Entry(47, 8123));
        values.add(new MoneyInfo.Entry(48, 8247));
        values.add(new MoneyInfo.Entry(49, 6123));
        values.add(new MoneyInfo.Entry(50, 7234));
        values.add(new MoneyInfo.Entry(51, 1623));

        values.setHalfGameRound(15);
    }
}
