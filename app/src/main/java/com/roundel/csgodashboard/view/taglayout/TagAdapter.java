package com.roundel.csgodashboard.view.taglayout;

import android.content.Context;
import android.support.transition.AutoTransition;
import android.support.transition.Transition;
import android.support.transition.TransitionManager;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.roundel.csgodashboard.R;
import com.zhy.view.flowlayout.FlowLayout;

import java.util.List;

/**
 * Created by Krzysiek on 2017-02-18.
 */
public class TagAdapter
{
    private static final String TAG = TagAdapter.class.getSimpleName();
    //<editor-fold desc="private variables">
    private LayoutInflater mInflater;

    private OnDataChangedListener mOnDataChangedListener;
    private TagActionListener mTagActionListener;
    private TagLayout mParentTagLayout;
    private int mExpandedPosition = -1;
    private Context mContext;
    private OnTagAddRequested mOnTagAddRequestedListener = new OnTagAddRequested();
    private OnTagRemoveRequested mOnTagRemoveRequested = new OnTagRemoveRequested();
    private boolean mRequestFocus;

    private List<String> mDataSet;

    private int mTagNameMinLength = 2;
    private int mTagNameMaxLength = 15;
    private int mTagNameMaxEms = 8;
    //</editor-fold>

    public TagAdapter(List<String> data, Context context)
    {
        this.mDataSet = data;
        this.mContext = context;
        mInflater = LayoutInflater.from(context);
    }

    public String getItem(int position)
    {
        return mDataSet.get(position);
    }

    public int getCount()
    {
        return mDataSet.size() + 1;
    }

    public View getView(FlowLayout parent, int position)
    {
        View view;
        if(position >= getCount() - 1)
        {
            view = mInflater.inflate(R.layout.utility_tag_add, parent, false);
            final ImageView addIcon = (ImageView) view.findViewById(R.id.utility_tag_add);
            final EditText editText = (EditText) view.findViewById(R.id.utility_tag_edittext);

            addIcon.setOnClickListener(mOnTagAddRequestedListener);
            editText.setOnKeyListener(mOnTagAddRequestedListener);
            editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(mTagNameMaxLength)});
            if(mRequestFocus)
                editText.requestFocus();
        }
        else
        {
            view = mInflater.inflate(R.layout.utility_tag_layout, parent, false);

            final TextView name = (TextView) view.findViewById(R.id.utility_tag_name);
            final ImageView removeIcon = (ImageView) view.findViewById(R.id.utility_tag_remove);

            removeIcon.setOnClickListener(mOnTagRemoveRequested);
            name.setText(getItem(position));
            name.setMaxEms(mTagNameMaxEms);

            //TODO: Fix expanding when removing an icon
            view.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    int position = mParentTagLayout.indexOfChild(v);
                    final boolean isExpanded = position == mExpandedPosition;

                    final Transition transition = new AutoTransition();
                    transition.setDuration(150);

                    TransitionManager.beginDelayedTransition(mParentTagLayout, transition);
                    removeIcon.setVisibility(View.VISIBLE);
                    if(mExpandedPosition != -1)
                    {
                        View child = mParentTagLayout.getChildAt(mExpandedPosition);
                        if(child != null)
                        {
                            final ImageView i = (ImageView) child.findViewById(R.id.utility_tag_remove);
                            if(i != null)
                            {
                                i.setVisibility(View.GONE);
                                mExpandedPosition = -1;
                            }
                        }
                        else
                        {
                            mExpandedPosition = -1;
                        }
                    }
                    mExpandedPosition = isExpanded ? -1 : position;
                }
            });
        }
        return view;
    }

    private void onTagAdded(View view)
    {
        EditText editText = (EditText) view.findViewById(R.id.utility_tag_edittext);
        if(editText != null)
        {
            String tagName = editText.getText().toString().toLowerCase();

            if(mTagActionListener != null &&
                    !TextUtils.isEmpty(tagName) &&
                    tagName.length() >= mTagNameMinLength &&
                    tagName.length() <= mTagNameMaxLength)
            {
                if(mTagActionListener.onTagAdded(tagName))
                {
                    editText.setText("");
                }
            }

            mRequestFocus = editText.hasFocus();
        }
    }

    private void onTagRemoved(View view)
    {
        int position = mParentTagLayout.indexOfChild(view);

        if(mTagActionListener != null && position >= 0)
            mTagActionListener.onTagRemoved(position);
    }

    void onAttachedToTagLayout(TagLayout tagLayout)
    {
        this.mParentTagLayout = tagLayout;
    }

    /**
     * Not recommended, use {@link #notifyItemInserted(int)} and {@link #notifyItemRemoved(int)} for
     * better performance and animation purposes
     */
    public void notifyDataSetChanged()
    {
        if(mOnDataChangedListener != null)
            mOnDataChangedListener.onDataSetChanged();
    }

    public void notifyItemInserted(int position)
    {
        if(mOnDataChangedListener != null)
        {
            View view = getView(mParentTagLayout, position);
            mOnDataChangedListener.onItemInserted(position, view);
        }
    }

    public void notifyItemRemoved(int position)
    {
        if(mOnDataChangedListener != null)
            mOnDataChangedListener.onItemRemoved(position);
        mExpandedPosition = -1;
    }

    public void setTagNameMinLength(int tagNameMinLength)
    {
        this.mTagNameMinLength = tagNameMinLength;
    }

    public void setTagNameMaxLength(int tagNameMaxLength)
    {
        this.mTagNameMaxLength = tagNameMaxLength;
    }

    public void setTagNameMaxEms(int tagNameMaxEms)
    {
        this.mTagNameMaxEms = tagNameMaxEms;
    }

    void setOnDataChangedListener(OnDataChangedListener onDataChangedListener)
    {
        this.mOnDataChangedListener = onDataChangedListener;
    }

    public void setTagActionListener(TagActionListener mTagActionListener)
    {
        this.mTagActionListener = mTagActionListener;
    }

    interface OnDataChangedListener
    {
        void onDataSetChanged();

        void onItemInserted(int position, View view);

        void onItemRemoved(int position);
    }

    public interface TagActionListener
    {
        /**
         * @param name of the tag that was added
         *
         * @return true if should clear the {@link EditText} (usually when calling {@link
         * #notifyItemInserted(int)} to animate the addition)
         */
        boolean onTagAdded(String name);

        void onTagRemoved(int position);
    }

    private class OnTagAddRequested implements View.OnClickListener, View.OnKeyListener
    {
        @Override
        public void onClick(View v)
        {
            onTagAdded((View) v.getParent());
        }

        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event)
        {
            if(keyCode == KeyEvent.KEYCODE_ENTER)
            {
                onTagAdded((View) v.getParent());
                return true;
            }
            return false;
        }
    }

    private class OnTagRemoveRequested implements View.OnClickListener
    {
        @Override
        public void onClick(View v)
        {
            final View parent = (View) v.getParent();
            onTagRemoved(parent);
        }
    }
}
