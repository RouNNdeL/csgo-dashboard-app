package com.roundel.csgodashboard.entities;

import android.content.Context;
import android.provider.BaseColumns;
import android.support.annotation.DrawableRes;

import com.roundel.csgodashboard.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Krzysiek on 2017-02-10.
 */
public class Grenade implements BaseColumns
{
    private static final String TAG = Grenade.class.getSimpleName();
    public static final int TYPE_FLASHBANG = 200;
    public static final int TYPE_HE = 201;
    public static final int TYPE_MOLOTOV = 202;
    public static final int TYPE_SMOKE = 203;
    public static String TABLE_NAME = "grenades";
    //<editor-fold desc="private variables">
    private int id;
    private String name;
    @DrawableRes private int icon;
    //</editor-fold>

    public Grenade(int id, String name, int icon)
    {
        this.id = id;
        this.name = name;
        this.icon = icon;
    }

    public static List<Grenade> getDefaultGrenadeList(Context context)
    {
        List<Grenade> list = new ArrayList<>();

        list.add(Grenade.fromType(TYPE_FLASHBANG, context));
        list.add(Grenade.fromType(TYPE_SMOKE, context));
        list.add(Grenade.fromType(TYPE_MOLOTOV, context));
        list.add(Grenade.fromType(TYPE_HE, context));

        return list;
    }

    public static Grenade fromType(int type, Context context)
    {
        String[] namesList = context.getResources().getStringArray(R.array.add_nade_grenade_names);
        switch(type)
        {
            case TYPE_SMOKE:
                return new Grenade(type, namesList[0], R.drawable.ic_csgo_smoke_grenade);
            case TYPE_FLASHBANG:
                return new Grenade(type, namesList[1], R.drawable.ic_csgo_flashbang);
            case TYPE_MOLOTOV:
                return new Grenade(type, namesList[2], R.drawable.ic_csgo_molotov);
            case TYPE_HE:
                return new Grenade(type, namesList[3], R.drawable.ic_csgo_he_grenade);
            default:
                throw new IllegalArgumentException("Has to be one of {TYPE_SMOKE, TYPE_FLASHBANG, TYPE_MOLOTOV, TYPE_HE}");
        }
    }

    public int getId()
    {
        return id;
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
