package com.roundel.csgodashboard.entities;

import android.content.Context;
import android.support.annotation.DrawableRes;

import com.roundel.csgodashboard.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Krzysiek on 2017-02-10.
 */
public class Grenade
{
    private static final String TAG = Grenade.class.getSimpleName();

    private String name;
    private @DrawableRes int icon;

    public Grenade(String name, int icon)
    {
        this.name = name;
        this.icon = icon;
    }

    public static List<Grenade> getDefaultGrenadeList(Context context)
    {
        List<Grenade> list = new ArrayList<>();
        String[] namesList = context.getResources().getStringArray(R.array.add_nade_grenade_names);

        list.add(new Grenade(namesList[0], R.drawable.ic_csgo_smoke_grenade));
        list.add(new Grenade(namesList[1], R.drawable.ic_csgo_flashbang));
        list.add(new Grenade(namesList[2], R.drawable.ic_csgo_molotov));
        list.add(new Grenade(namesList[3], R.drawable.ic_csgo_he_grenade));

        return list;
    }

    public String getName()
    {
        return name;
    }

    public int getIcon()
    {
        return icon;
    }
}
