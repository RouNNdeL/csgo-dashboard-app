package com.roundel.csgodashboard.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.roundel.csgodashboard.R;
import com.roundel.csgodashboard.entities.Map;
import com.roundel.csgodashboard.entities.Maps;

/**
 * Created by Krzysiek on 2017-02-22.
 */
public class MapSpinnerAdapter extends BaseAdapter
{
    private static final String TAG = MapSpinnerAdapter.class.getSimpleName();

    private static final int TYPE_MAP = 0;
    private static final int TYPE_ADD_MAP = 1;

    //<editor-fold desc="private variables">
    private Maps mDataSet;
    private LayoutInflater mLayoutInflater;
//</editor-fold>

    public MapSpinnerAdapter(Maps mDataSet, Context context)
    {
        this.mDataSet = mDataSet;
        this.mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount()
    {
        return mDataSet.size() + 1;
    }

    @Override
    public Map getItem(int position)
    {
        if(position >= mDataSet.size())
        {
            return null;
        }
        return mDataSet.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        return getDropDownView(position, convertView, parent);
    }

    @Override
    public int getItemViewType(int position)
    {
        if(position >= mDataSet.size())
        {
            return TYPE_ADD_MAP;
        }
        else
        {
            return TYPE_MAP;
        }
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent)
    {
        View view;
        final int viewType = getItemViewType(position);
        if(viewType == TYPE_MAP)
        {
            view = mLayoutInflater.inflate(R.layout.list_simple_one_line_no_ripple, parent, false);
            TextView mapName = (TextView) view.findViewById(R.id.list_text_primary);
            mapName.setText(getItem(position).getName());
        }
        else if(viewType == TYPE_ADD_MAP)
        {
            view = mLayoutInflater.inflate(R.layout.list_simple_one_line_no_ripple, parent, false);
            TextView textPrimary = (TextView) view.findViewById(R.id.list_text_primary);
            textPrimary.setText("Add a new map");
        }
        else
        {
            throw new IllegalArgumentException("Unknown view type: " + viewType);
        }
        return view;
    }
}
