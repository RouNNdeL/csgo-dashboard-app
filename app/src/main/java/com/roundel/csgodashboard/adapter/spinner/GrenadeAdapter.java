package com.roundel.csgodashboard.adapter.spinner;

import android.content.Context;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.roundel.csgodashboard.R;
import com.roundel.csgodashboard.entities.utility.Grenade;

import java.util.List;

/**
 * Created by Krzysiek on 2017-02-10.
 */
public class GrenadeAdapter extends ArrayAdapter<Grenade>
{
    private static final String TAG = GrenadeAdapter.class.getSimpleName();

    //<editor-fold desc="private variables">
    private LayoutInflater inflater;
    @LayoutRes private int resource;
    @IdRes private int textViewId;
    //</editor-fold>


    public GrenadeAdapter(Context context, @LayoutRes int resource, @IdRes int textViewResourceId, List<Grenade> grenades)
    {
        super(context, resource, textViewResourceId, grenades);

        this.textViewId = textViewResourceId;
        this.resource = resource;
        this.inflater = LayoutInflater.from(context);
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
        final View root = inflater.inflate(resource, parent, false);
        final Grenade grenade = getItem(position);

        TextView name = (TextView) root.findViewById(textViewId);
        ImageView icon = (ImageView) root.findViewById(R.id.list_icon);

        if(grenade != null)
        {
            name.setText(grenade.getName());
            if(icon != null)
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

    public int getItemPosition(long id)
    {
        for(int i = 0; i < getCount(); i++)
        {
            if(getItemId(i) == id)
                return i;
        }
        return -1;
    }
}
