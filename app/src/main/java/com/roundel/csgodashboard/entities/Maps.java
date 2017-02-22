package com.roundel.csgodashboard.entities;

import android.content.Context;
import android.net.Uri;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.roundel.csgodashboard.util.UriAdapter;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

/**
 * Created by Krzysiek on 2017-02-20.
 */
public class Maps extends ArrayList<Map>
{
    private static final String TAG = Maps.class.getSimpleName();
    private static final String FILE_NAME = "maps.dat";

    public static Maps fromFile(Context context) throws IOException
    {
        try
        {
            FileInputStream fileInputStream = context.openFileInput(FILE_NAME);
            if(fileInputStream != null)
            {
                StringBuilder builder = new StringBuilder();
                BufferedReader reader = new BufferedReader(new InputStreamReader(fileInputStream));
                String line;
                while((line = reader.readLine()) != null)
                {
                    builder.append(line);
                }
                final GsonBuilder gsonBuilder = new GsonBuilder();
                gsonBuilder.registerTypeAdapter(Uri.class, new UriAdapter());

                final Gson gson = gsonBuilder.create();
                return gson.fromJson(builder.toString(), Maps.class);
            }
            else
                return Maps.getDefaultMaps(context);
        }
        catch(FileNotFoundException e)
        {
            e.printStackTrace();
            return Maps.getDefaultMaps(context);
        }

    }

    public static Maps getDefaultMaps(Context context)
    {
        Maps maps = new Maps();

        maps.add(new Map(Map.MAP_DE_CACHE, Map.MAP_URI_DE_CACHE, context));
        maps.add(new Map(Map.MAP_DE_CBBLE, Map.MAP_URI_DE_CBBLE, context));
        maps.add(new Map(Map.MAP_DE_DUST, Map.MAP_URI_DE_DUST, context));
        maps.add(new Map(Map.MAP_DE_DUST2, Map.MAP_URI_DE_DUST2, context));
        maps.add(new Map(Map.MAP_DE_INFERNO, Map.MAP_URI_DE_INFERNO, context));
        maps.add(new Map(Map.MAP_DE_MIRAGE, Map.MAP_URI_DE_MIRAGE, context));
        maps.add(new Map(Map.MAP_DE_NUKE, Map.MAP_URI_DE_NUKE, context));
        maps.add(new Map(Map.MAP_DE_OVERPASS, Map.MAP_URI_DE_OVERPASS, context));
        maps.add(new Map(Map.MAP_DE_TRAIN, Map.MAP_URI_DE_TRAIN, context));

        return maps;
    }

    public void saveToFile(Context context) throws IOException
    {
        final GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Uri.class, new UriAdapter());
        final Gson gson = gsonBuilder.create();

        FileOutputStream fos = context.openFileOutput(FILE_NAME, Context.MODE_PRIVATE);
        fos.write(gson.toJson(this).getBytes());
        fos.close();
    }

    public Map mapFromCodeName(String codeName)
    {
        for(Map map : this)
        {
            if(Objects.equals(map.getCodeName(), codeName))
            {
                return map;
            }
        }
        return null;
    }

    public HashSet<String> getMapNames()
    {
        HashSet<String> set = new HashSet<>();

        for(Map map : this)
        {
            set.add(map.getName());
        }

        return set;
    }

    public List<String> getOrderedMaps()
    {
        List<String> list = new ArrayList<>();

        for(Map map : this)
        {
            if(!list.contains(map.getName()))
            {
                list.add(map.getName());
            }
        }

        return list;
    }
}
