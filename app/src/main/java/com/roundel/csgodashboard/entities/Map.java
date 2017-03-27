package com.roundel.csgodashboard.entities;

import android.content.Context;
import android.net.Uri;
import android.provider.BaseColumns;

import com.roundel.csgodashboard.R;

/**
 * Created by Krzysiek on 2017-01-21.
 */
public class Map implements BaseColumns
{
    private static final String TAG = Map.class.getSimpleName();

    public static final String TABLE_NAME = "maps";

    public static final String COLUMN_NAME_CODE_NAME = "code_name";
    public static final String COLUMN_NAME_NAME = "name";
    public static final String COLUMN_NAME_IMG_URI = "uri";

    public static final String[] PROJECTION_ALL = new String[]{
            TABLE_NAME + "." + _ID,
            TABLE_NAME + "." + COLUMN_NAME_CODE_NAME,
            TABLE_NAME + "." + COLUMN_NAME_NAME,
            TABLE_NAME + "." + COLUMN_NAME_IMG_URI
    };
    public static final String[] PROJECTION_DATA = new String[]{
            TABLE_NAME + "." + COLUMN_NAME_CODE_NAME,
            TABLE_NAME + "." + COLUMN_NAME_NAME,
            TABLE_NAME + "." + COLUMN_NAME_IMG_URI
    };

    static final String MAP_DE_CBBLE = "de_cbble";
    static final String MAP_DE_CACHE = "de_cache";
    static final String MAP_DE_DUST = "de_dust";
    static final String MAP_DE_DUST2 = "de_dust2";
    static final String MAP_DE_INFERNO = "de_inferno";
    static final String MAP_DE_MIRAGE = "de_mirage";
    static final String MAP_DE_NUKE = "de_nuke";
    static final String MAP_DE_OVERPASS = "de_overpass";
    static final String MAP_DE_TRAIN = "de_train";

    static final Uri MAP_URI_DE_CACHE = Uri.parse(UserData.ASSETS_BASE + "/maps/de_cache.jpg");
    static final Uri MAP_URI_DE_CBBLE = Uri.parse(UserData.ASSETS_BASE + "/maps/de_cbble.jpg");
    static final Uri MAP_URI_DE_DUST = Uri.parse(UserData.ASSETS_BASE + "/maps/de_dust.jpg");
    static final Uri MAP_URI_DE_DUST2 = Uri.parse(UserData.ASSETS_BASE + "/maps/de_dust2.jpg");
    static final Uri MAP_URI_DE_INFERNO = Uri.parse(UserData.ASSETS_BASE + "/maps/de_inferno.jpg");
    static final Uri MAP_URI_DE_MIRAGE = Uri.parse(UserData.ASSETS_BASE + "/maps/de_mirage.jpg");
    static final Uri MAP_URI_DE_NUKE = Uri.parse(UserData.ASSETS_BASE + "/maps/de_nuke.jpg");
    static final Uri MAP_URI_DE_OVERPASS = Uri.parse(UserData.ASSETS_BASE + "/maps/de_overpass.jpg");
    static final Uri MAP_URI_DE_TRAIN = Uri.parse(UserData.ASSETS_BASE + "/maps/de_train.jpg");

    //<editor-fold desc="private variables">
    private String codeName;
    private String name;
    private Uri imageUri;
    private int id = -1;
    //</editor-fold>

    private Map(String codeName, Uri imageUri, Context context)
    {
        this.codeName = codeName;
        this.name = nameFromCodeName(codeName, context);
        this.imageUri = imageUri;
    }

    private Map(int id)
    {
        this.id = id;
    }

    public Map(int id, String codeName, String name, Uri imageUri)
    {
        this.id = id;
        this.codeName = codeName;
        this.name = name;
        this.imageUri = imageUri;
    }

    public static Map withoutId(String codeName, Uri imageUri, Context context)
    {
        return new Map(codeName, imageUri, context);
    }

    public static Map referenceOnly(int id)
    {
        return new Map(id);
    }

    @Override
    public boolean equals(Object o)
    {
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;

        Map map = (Map) o;

        return id == map.id;

    }

    @Override
    public int hashCode()
    {
        return id;
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

    public Uri getImageUri()
    {
        return imageUri;
    }

    public String getName()
    {
        return name;
    }

    public String getCodeName()
    {
        return codeName;
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }
}