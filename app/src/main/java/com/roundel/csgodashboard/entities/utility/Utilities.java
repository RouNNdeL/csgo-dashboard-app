package com.roundel.csgodashboard.entities.utility;

import android.content.Context;
import android.net.Uri;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.roundel.csgodashboard.util.UriAdapter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by Krzysiek on 2017-02-10.
 */
public class Utilities extends ArrayList<UtilityBase>
{
    private static final String TAG = Utilities.class.getSimpleName();

    public static final String IMAGE_FOLDER_NAME = "utilities";
    private static final int BUFFER_SIZE = 1024;
    private static final String FILE_NAME = "utilities.dat";

    public static Utilities fromFile(Context context) throws IOException
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
                return gson.fromJson(builder.toString(), Utilities.class);
            }
            else
                return new Utilities();
        }
        catch(FileNotFoundException e)
        {
            e.printStackTrace();
            return new Utilities();
        }
    }

    public static String getImgPath(Context context)
    {
        return context.getExternalFilesDir(null).getAbsolutePath() + File.separator + IMAGE_FOLDER_NAME + File.separator;
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

    /*public HashSet<String> getTags()
    {
        HashSet<String> set = new HashSet<>();

        for(UtilityBase utility : this)
        {
            set.addAll(utility.getTags());
        }

        return set;
    }*/

    public Tags getOrderedTags()
    {
        Tags list = new Tags();

        for(UtilityBase utility : this)
        {
            for(Tags.Tag tag : utility.getTags())
            {
                if(!list.contains(tag))
                {
                    list.add(tag);
                }
            }
        }

        return list;
    }
}
