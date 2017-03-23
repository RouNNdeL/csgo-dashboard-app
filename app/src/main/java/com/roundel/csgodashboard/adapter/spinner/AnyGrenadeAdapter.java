package com.roundel.csgodashboard.adapter.spinner;

import android.content.Context;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;

import com.roundel.csgodashboard.entities.utility.Grenade;

import java.util.List;

/**
 * Created by Krzysiek on 2017-03-23.
 */

public class AnyGrenadeAdapter extends GrenadeAdapter
{
    public AnyGrenadeAdapter(Context context, @LayoutRes int resource, @IdRes int textViewResourceId, List<Grenade> grenades)
    {
        super(context, resource, textViewResourceId, grenades);
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
    public Grenade getItem(int position)
    {
        if(position == 0)
            return new Grenade(-1, "Any", -1);
        return super.getItem(position - 1);
    }

    @Override
    public int getCount()
    {
        return super.getCount() + 1;
    }
}
