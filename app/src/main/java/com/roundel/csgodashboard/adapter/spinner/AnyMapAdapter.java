package com.roundel.csgodashboard.adapter.spinner;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.roundel.csgodashboard.R;
import com.roundel.csgodashboard.entities.Map;

/**
 * Created by Krzysiek on 2017-03-23.
 */

public class AnyMapAdapter extends MapAdapter
{
    public AnyMapAdapter(Context context, Cursor cursor)
    {
        super(
                context,
                R.layout.list_simple_one_line_no_ripple,
                cursor,
                new String[]{Map.COLUMN_NAME_NAME},
                new int[]{R.id.list_text_primary},
                CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER
        );
    }

    @Override
    public long getItemId(int position)
    {
        if(position == 0)
            return -1;
        return super.getItemId(position - 1);
    }

    @Nullable
    @Override
    public Object getItem(int position)
    {
        if(position == 0)
            return "Any";
        if(mDataValid && mCursor != null)
        {
            mCursor.moveToPosition(position - 1);
            return mCursor;
        }
        else
        {
            return null;
        }
    }

    @Override
    public int getCount()
    {
        return super.getCount() + 1;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_simple_one_line_no_ripple, parent, false);

        if(position == 0)
        {
            TextView name = (TextView) view.findViewById(R.id.list_text_primary);
            name.setText((String) getItem(0));
        }
        else
        {
            bindView(view, mContext, (Cursor) getItem(position));
        }

        return view;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent)
    {
        return getView(position, convertView, parent);
    }
}
