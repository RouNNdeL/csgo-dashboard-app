package com.roundel.csgodashboard.entities;

import android.content.Context;

import java.io.IOException;

/**
 * Created by Krzysiek on 2017-02-20.
 */
public class UserData
{
    private static final String TAG = UserData.class.getSimpleName();

    public static final String ASSETS_BASE = "file:///android_asset";

    //<editor-fold desc="private variables">
    private Maps maps;
    private Utilities utilities;
    private Context context;
    //</editor-fold>


    private UserData(Maps maps, Utilities utilities, Context context)
    {
        this.maps = maps;
        this.utilities = utilities;
        this.context = context;
    }

    private UserData(Maps maps, Context context)
    {
        this.maps = maps;
        this.context = context;
    }

    private UserData(Utilities utilities, Context context)
    {
        this.utilities = utilities;
        this.context = context;
    }

    public static UserData fromContext(Context context)
    {
        Maps maps;
        Utilities utilities;
        try
        {
            utilities = Utilities.fromFile(context);
            maps = Maps.fromFile(context);
        }
        catch(IOException e)
        {
            throw new RuntimeException(e);
        }
        return new UserData(maps, utilities, context);
    }

    public static UserData mapsOnly(Context context)
    {
        Maps maps;
        try
        {
            maps = Maps.fromFile(context);
        }
        catch(IOException e)
        {
            throw new RuntimeException(e);
        }
        return new UserData(maps, context);
    }

    public static UserData utilitiesOnly(Context context)
    {
        Utilities utilities;
        try
        {
            utilities = Utilities.fromFile(context);
        }
        catch(IOException e)
        {
            throw new RuntimeException(e);
        }
        return new UserData(utilities, context);
    }

    /**
     * @param map     object to be saved to the {@value Maps#FILE_NAME} file
     * @param context required to access private storage
     *
     * @return true if saved successfully
     */
    public static boolean addMap(Map map, Context context)
    {
        try
        {
            Maps maps = Maps.fromFile(context);
            maps.add(map);
            maps.saveToFile(context);
            return true;
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * @param utility object to be saved to the {@value Utilities#FILE_NAME} file
     * @param context required to access private storage
     *
     * @return true if saved successfully
     */
    public static boolean addUtility(UtilityBase utility, Context context)
    {
        try
        {
            Utilities utilities = Utilities.fromFile(context);
            utilities.add(utility);
            utilities.saveToFile(context);
            return true;
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
        return false;
    }

    public void save()
    {
        try
        {
            if(this.utilities != null)
            {
                this.utilities.saveToFile(context);
            }
            if(this.maps != null)
            {
                this.maps.saveToFile(context);
            }
        }
        catch(IOException e)
        {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public Utilities getUtilities()
    {
        return utilities;
    }

    public Maps getMaps()
    {
        return maps;
    }
}
