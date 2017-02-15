package com.roundel.csgodashboard.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.roundel.csgodashboard.R;
import com.roundel.csgodashboard.entities.Grenade;

import java.util.List;

/**
 * Created by Krzysiek on 2017-02-10.
 */
public class GrenadeAdapter extends ArrayAdapter<Grenade>
{
    private static final String TAG = GrenadeAdapter.class.getSimpleName();

    //<editor-fold desc="private variables">
    private LayoutInflater inflater;
//</editor-fold>


    public GrenadeAdapter(Context context, int resource, int textViewResourceId, List<Grenade> objects)
    {
        super(context, resource, textViewResourceId, objects);

        inflater = LayoutInflater.from(context);
    }

    @Override
    public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent)
    {
        final View root = inflater.inflate(R.layout.list_icon_one_line_no_ripple, parent, false);
        final Grenade grenade = getItem(position);

        TextView name = (TextView) root.findViewById(R.id.list_text_primary);
        ImageView icon = (ImageView) root.findViewById(R.id.list_icon);

        if(grenade != null)
        {
            name.setText(grenade.getName());
            icon.setImageDrawable(getContext().getDrawable(grenade.getIcon()));
        }

        return root;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent)
    {
        return getDropDownView(position, convertView, parent);
    }
}
