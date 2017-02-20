package com.roundel.csgodashboard.entities;

import android.content.Context;
import android.net.Uri;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.roundel.csgodashboard.R;
import com.roundel.csgodashboard.util.UriAdapter;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Objects;

/**
 * Created by Krzysiek on 2017-02-20.
 */
public class Maps extends ArrayList<Maps.Map>
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
            if(Objects.equals(map.codeName, codeName))
            {
                return map;
            }
        }
        return null;
    }

    public static class Map
    {
        /**
         * Created by Krzysiek on 2017-01-21.
         */
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

        private static final Uri MAP_URI_DE_CACHE = Uri.parse(UserData.ASSETS_BASE + "/maps/de_cache.jpg");
        private static final Uri MAP_URI_DE_CBBLE = Uri.parse(UserData.ASSETS_BASE + "/maps/de_cbble.jpg");
        private static final Uri MAP_URI_DE_DUST = Uri.parse(UserData.ASSETS_BASE + "/maps/de_dust.jpg");
        private static final Uri MAP_URI_DE_DUST2 = Uri.parse(UserData.ASSETS_BASE + "/maps/de_dust2.jpg");
        private static final Uri MAP_URI_DE_INFERNO = Uri.parse(UserData.ASSETS_BASE + "/maps/de_inferno.jpg");
        private static final Uri MAP_URI_DE_MIRAGE = Uri.parse(UserData.ASSETS_BASE + "/maps/de_mirage.jpg");
        private static final Uri MAP_URI_DE_NUKE = Uri.parse(UserData.ASSETS_BASE + "/maps/de_nuke.jpg");
        private static final Uri MAP_URI_DE_OVERPASS = Uri.parse(UserData.ASSETS_BASE + "/maps/de_overpass.jpg");
        private static final Uri MAP_URI_DE_TRAIN = Uri.parse(UserData.ASSETS_BASE + "/maps/de_train.jpg");

        //<editor-fold desc="private variables">
        private String codeName;
        private String name;
        private Uri imageUri;
        //</editor-fold>

        public Map(String codeName, Uri imageUri, Context context)
        {
            this.codeName = codeName;
            this.name = nameFromCodeName(codeName, context);
            this.imageUri = imageUri;
        }

        public Map(String codeName, String name, Uri imageUri)
        {
            this.codeName = codeName;
            this.name = name;
            this.imageUri = imageUri;
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
    }
}
