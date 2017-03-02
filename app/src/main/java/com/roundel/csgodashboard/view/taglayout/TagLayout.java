package com.roundel.csgodashboard.view.taglayout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.roundel.csgodashboard.R;
import com.zhy.view.flowlayout.FlowLayout;

/**
 * Created by Krzysiek on 2017-02-18.
 */
public class TagLayout extends FlowLayout implements TagAdapter.OnDataChangedListener
{
    private static final String TAG = TagLayout.class.getSimpleName();
    //<editor-fold desc="private variables">
    private TagAdapter mTagAdapter;
//</editor-fold>

    public TagLayout(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }

    public TagLayout(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public TagLayout(Context context)
    {
        super(context);
    }


    //Copied from the super class
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);
        int modeWidth = MeasureSpec.getMode(widthMeasureSpec);
        int sizeHeight = MeasureSpec.getSize(heightMeasureSpec);
        int modeHeight = MeasureSpec.getMode(heightMeasureSpec);

        // wrap_content
        int width = 0;
        int height = 0;

        int lineWidth = 0;
        int lineHeight = 0;

        //This is to prevent tags jumping to the next line when expanded (remove icon is shown)
        int removeIconSize = getResources().getDimensionPixelSize(R.dimen.utility_tag_icon_margin_start) +
                getResources().getDimensionPixelSize(R.dimen.utility_tag_icon_size);
        sizeWidth -= removeIconSize;

        int cCount = getChildCount();

        for(int i = 0; i < cCount; i++)
        {
            View child = getChildAt(i);
            if(child.getVisibility() == View.GONE)
            {
                if(i == cCount - 1)
                {
                    width = Math.max(lineWidth, width);
                    height += lineHeight;
                }
                continue;
            }
            measureChild(child, widthMeasureSpec, heightMeasureSpec);
            MarginLayoutParams lp = (MarginLayoutParams) child
                    .getLayoutParams();

            int childWidth = child.getMeasuredWidth() + lp.leftMargin
                    + lp.rightMargin;
            int childHeight = child.getMeasuredHeight() + lp.topMargin
                    + lp.bottomMargin;

            if(lineWidth + childWidth > sizeWidth - getPaddingLeft() - getPaddingRight())
            {
                //Jump to the next line
                width = Math.max(width, lineWidth);
                lineWidth = childWidth;
                height += lineHeight;
                lineHeight = childHeight;
            }
            else
            {
                //Continue in the same line
                lineWidth += childWidth;
                lineHeight = Math.max(lineHeight, childHeight);
            }
            if(i == cCount - 1)
            {
                width = Math.max(lineWidth, width);
                height += lineHeight;
            }
        }
        setMeasuredDimension(
                //
                modeWidth == MeasureSpec.EXACTLY ? sizeWidth : width + getPaddingLeft() + getPaddingRight(),
                modeHeight == MeasureSpec.EXACTLY ? sizeHeight : height + getPaddingTop() + getPaddingBottom()//
        );
    }

    @Override
    public void onDataSetChanged()
    {
        refreshLayout();
    }

    @Override
    public void onItemInserted(int position, View view)
    {
        addView(view, position);
    }

    @Override
    public void onItemRemoved(int position)
    {
        removeViewAt(position);
    }

    public void setAdapter(TagAdapter adapter)
    {
        mTagAdapter = adapter;
        mTagAdapter.setOnDataChangedListener(this);
        mTagAdapter.onAttachedToTagLayout(this);
        refreshLayout();
    }

    private void refreshLayout()
    {
        removeAllViews();
        if(mTagAdapter == null)
            return;
        for(int i = 0; i < mTagAdapter.getCount(); i++)
        {
            addView(mTagAdapter.getView(this, i));
        }
    }
}
