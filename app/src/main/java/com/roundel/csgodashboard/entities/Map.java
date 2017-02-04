package com.roundel.csgodashboard.entities;

import android.content.Context;

/**
 * Created by Krzysiek on 2017-01-21.
 */
public class Map
{
    public static final String MAP_DE_CACHE = "de_cache";
    public static final String MAP_DE_CBBLE = "de_cbble";
    public static final String MAP_DE_DUST = "de_dust";
    public static final String MAP_DE_DUST2 = "de_dust2";
    public static final String MAP_DE_INFERNO = "de_inferno";
    public static final String MAP_DE_MIRAGE = "de_mirage";
    public static final String MAP_DE_NUKE = "de_nuke";
    public static final String MAP_DE_OVERPASS = "de_overpass";
    public static final String MAP_DE_TRAIN = "de_train";
    private static final String TAG = Map.class.getSimpleName();
    private String codeName;
    private String name;

    public Map(String codeName)
    {
        this.codeName = codeName;
    }

    private String nameFromCodeName(Context context)
    {
        switch(this.codeName)
        {
            case MAP_DE_DUST:
                return "Dust";
            case MAP_DE_DUST2:
                return "Dust 2";
            case MAP_DE_INFERNO:
                return "Inferno";
            case MAP_DE_NUKE:
                return "Nuke";
            case MAP_DE_MIRAGE:
                return "Mirage";
            case MAP_DE_CACHE:
                return "Cache";
            case MAP_DE_OVERPASS:
                return "Overpass";
            case MAP_DE_TRAIN:
                return "Train";
            case MAP_DE_CBBLE:
                return "Cobblestone";
            default:
                return this.codeName;
        }
    }

}
