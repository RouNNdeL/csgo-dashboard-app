package com.roundel.csgodashboard.entities;

import android.content.Context;

import com.roundel.csgodashboard.R;

/**
 * Created by Krzysiek on 2017-01-21.
 */
public class Map
{
    private static final String TAG = Map.class.getSimpleName();
    private static final String MAP_DE_CACHE = "de_cache";
    private static final String MAP_DE_CBBLE = "de_cbble";
    private static final String MAP_DE_DUST = "de_dust";
    private static final String MAP_DE_DUST2 = "de_dust2";
    private static final String MAP_DE_INFERNO = "de_inferno";
    private static final String MAP_DE_MIRAGE = "de_mirage";
    private static final String MAP_DE_NUKE = "de_nuke";
    private static final String MAP_DE_OVERPASS = "de_overpass";
    private static final String MAP_DE_TRAIN = "de_train";
    private String codeName;
    private String name;

    public Map(String codeName, Context context)
    {
        this.codeName = codeName;
        this.name = nameFromCodeName(codeName, context);
    }

    public Map(String codeName, String name)
    {
        this.codeName = codeName;
        this.name = name;
    }

    private String nameFromCodeName(String codeName, Context context)
    {
        switch(codeName)
        {
            case MAP_DE_DUST:
                return context.getString(R.string.map_name_dust);
            case MAP_DE_DUST2:
                return context.getString(R.string.map_name_dust2);
            case MAP_DE_INFERNO:
                return context.getString(R.string.map_name_inferno);
            case MAP_DE_NUKE:
                return context.getString(R.string.map_name_nuke);
            case MAP_DE_MIRAGE:
                return context.getString(R.string.map_name_mirage);
            case MAP_DE_CACHE:
                return context.getString(R.string.map_name_cache);
            case MAP_DE_OVERPASS:
                return context.getString(R.string.map_name_overpass);
            case MAP_DE_TRAIN:
                return context.getString(R.string.map_name_train);
            case MAP_DE_CBBLE:
                return context.getString(R.string.map_name_cbble);
            default:
                return codeName;
        }
    }

}
