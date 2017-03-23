package com.roundel.csgodashboard.view.taglayout;

import android.animation.Animator;
import android.content.Context;
import android.support.transition.AutoTransition;
import android.support.transition.Transition;
import android.support.transition.TransitionManager;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.roundel.csgodashboard.R;
import com.roundel.csgodashboard.entities.utility.Tags;
import com.zhy.view.flowlayout.FlowLayout;

import java.util.HashSet;

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
    private OnTagSelected mOnTagSelected = new OnTagSelected();
    private OnTagExpanded mOnTagExpanded = new OnTagExpanded();
    private boolean mRequestFocus;

    private Tags mDataSet;
    private int mExpandTransitionDuration = 150;

    private int mTagNameMinLength = 2;
    private int mTagNameMaxLength = 15;
    private int mTagNameMaxEms = 8;
    private HashSet<Long> mSelectedItemIds = new HashSet<>();
    //</editor-fold>

    public TagAdapter(Tags data, Context context)
    {
        this.mDataSet = data;
        this.mContext = context;
        mInflater = LayoutInflater.from(context);
        mExpandTransitionDuration = context.getResources().getInteger(R.integer.tag_expand_transition_duration);
    }

    public Tags.Tag getItem(int position)
    {
        return mDataSet.get(position);
    }

    private long getItemId(int position)
    {
        return getItem(position).getId();
    }

    public int getCount()
    {
        if(mParentTagLayout.isEditable())
            return mDataSet.size() + 1;
        else
            return mDataSet.size();
    }

    public HashSet<Long> getSelectedItemIds()
    {
        return mSelectedItemIds;
    }

    public void setSelectedItemIds(HashSet<Long> selectedItemIds)
    {
        if(selectedItemIds == null)
            return;
        for(int i = 0; i < getCount(); i++)
        {
            if(selectedItemIds.contains(getItem(i).getId()))
            {
                View p = mParentTagLayout.getChildAt(i);
                p.findViewById(R.id.utility_tag_layout).setSelected(true);
                p.findViewById(R.id.utility_tag_layout_mock).setSelected(false);
            }
        }
        mSelectedItemIds = selectedItemIds;
    }

    public View getView(FlowLayout parent, int position)
    {
        View view;
        if(position >= mDataSet.size())
        {
            view = mInflater.inflate(R.layout.utility_tag_add, parent, false);
            final ImageView addIcon = (ImageView) view.findViewById(R.id.utility_tag_add);
            final EditText editText = (EditText) view.findViewById(R.id.utility_tag_edittext);

            addIcon.setOnClickListener(mOnTagAddRequestedListener);
            editText.setOnKeyListener(mOnTagAddRequestedListener);
            editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(mTagNameMaxLength)});
            if(mRequestFocus)
                editText.requestFocus();
            view.setSelected(true);
        }
        else
        {
            view = mInflater.inflate(R.layout.utility_tag_layout, parent, false);

            final TextView name = (TextView) view.findViewById(R.id.utility_tag_name);

            name.setText(getItem(position).getName());
            name.setMaxEms(mTagNameMaxEms);

            if(mParentTagLayout.isEditable())
            {
                final ImageView removeIcon = (ImageView) view.findViewById(R.id.utility_tag_remove);

                removeIcon.setOnClickListener(mOnTagRemoveRequested);
                view.setOnClickListener(mOnTagExpanded);
                view.setSelected(true);
            }
            else if(mParentTagLayout.isSelectable())
            {
                final TextView nameMock = (TextView) view.findViewById(R.id.utility_tag_name_mock);
                final LinearLayout mock = (LinearLayout) view.findViewById(R.id.utility_tag_layout_mock);

                view.setOnTouchListener(mOnTagSelected);

                mock.setVisibility(View.VISIBLE);
                nameMock.setText(getItem(position).getName());
                nameMock.setMaxEms(mTagNameMaxEms);
            }
            else
            {
                view.setSelected(true);
            }
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

    private class OnTagSelected implements View.OnTouchListener
    {
        @Override
        public boolean onTouch(View v, MotionEvent event)
        {
            if(event.getAction() == MotionEvent.ACTION_UP)
            {
                final View tag = v.findViewById(R.id.utility_tag_layout);
                final View tagMock = v.findViewById(R.id.utility_tag_layout_mock);

                int x = (int) event.getX();
                int y = (int) event.getY();

                int finalRadius = Math.max(v.getWidth(), v.getHeight());

                Animator animator = ViewAnimationUtils.createCircularReveal(tag, x, y, 0, finalRadius);

                tag.setSelected(!tag.isSelected());
                tagMock.setSelected(!tag.isSelected());

                if(tag.isSelected())
                {
                    mSelectedItemIds.add(getItemId(mParentTagLayout.indexOfChild(v)));
                }
                else
                {
                    mSelectedItemIds.remove(getItemId(mParentTagLayout.indexOfChild(v)));
                }

                animator.setDuration(250);
                animator.start();
                return false;
            }
            return true;
        }
    }

    private class OnTagExpanded implements View.OnClickListener
    {

        @Override
        public void onClick(View v)
        {
            final ImageView removeIcon = (ImageView) v.findViewById(R.id.utility_tag_remove);
            final ImageView removeIconMock = (ImageView) v.findViewById(R.id.utility_tag_remove_mock);
            final int position = mParentTagLayout.indexOfChild(v);
            final boolean isExpanded = position == mExpandedPosition;

            final Transition transition = new AutoTransition();
            transition.setDuration(mExpandTransitionDuration);

            TransitionManager.beginDelayedTransition(mParentTagLayout, transition);
            removeIcon.setVisibility(View.VISIBLE);
            removeIconMock.setVisibility(View.VISIBLE);
            if(mExpandedPosition != -1)
            {
                View child = mParentTagLayout.getChildAt(mExpandedPosition);
                if(child != null)
                {
                    final ImageView i = (ImageView) child.findViewById(R.id.utility_tag_remove);
                    final ImageView iMock = (ImageView) child.findViewById(R.id.utility_tag_remove_mock);
                    if(i != null)
                    {
                        i.setVisibility(View.GONE);
                        iMock.setVisibility(View.GONE);
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
            //Two View#getParent() calls are required, because of the mock view used for the CircularRevelAnimation
            final View parent = (View) v.getParent().getParent();
            onTagRemoved(parent);
        }
    }
}
