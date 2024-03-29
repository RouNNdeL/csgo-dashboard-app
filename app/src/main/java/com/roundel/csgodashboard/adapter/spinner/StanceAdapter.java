package com.roundel.csgodashboard.adapter.spinner;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.roundel.csgodashboard.R;
import com.roundel.csgodashboard.entities.utility.Stance;

import java.util.List;

/**
 * Created by Krzysiek on 2017-02-10.
 */
public class StanceAdapter extends ArrayAdapter<Stance>
{
    private static final String TAG = StanceAdapter.class.getSimpleName();

    //<editor-fold desc="private variables">
    private LayoutInflater mLayoutInflater;
    //</editor-fold>

    public StanceAdapter(Context context, int resource, int textViewResourceId, List<Stance> objects)
    {
        super(context, resource, textViewResourceId, objects);

        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public long getItemId(int position)
    {
        if(getItem(position) != null)
        {
            return getItem(position).getId();
        }
        return -1;
    }

    @Override
    public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent)
    {
        View root = mLayoutInflater.inflate(R.layout.list_icon_two_line_no_ripple, parent, false);
        final Stance stance = getItem(position);

        TextView title = (TextView) root.findViewById(R.id.list_text_primary);
        TextView desc = (TextView) root.findViewById(R.id.list_text_secondary);
        ImageView icon = (ImageView) root.findViewById(R.id.list_icon);

        if(stance != null)
        {
            title.setText(stance.getTitle());
            desc.setText(stance.getDescription());
            icon.setImageDrawable(getContext().getDrawable(stance.getIcon()));
        }
        return root;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent)
    {
        View root = mLayoutInflater.inflate(R.layout.list_icon_one_line_no_ripple, parent, false);
        final Stance stance = getItem(position);

        TextView title = (TextView) root.findViewById(R.id.list_text_primary);
        ImageView icon = (ImageView) root.findViewById(R.id.list_icon);

        if(stance != null)
        {
            title.setText(stance.getTitle());
            icon.setImageDrawable(getContext().getDrawable(stance.getIcon()));
        }
        return root;
    }

    public int getItemPosition(long id)
    {
        for(int i = 0; i < getCount(); i++)
        {
            final Stance item = getItem(i);
            if(item != null && item.getId() == id)
                return i;
        }
        return -1;
    }
}
