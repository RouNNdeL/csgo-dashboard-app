package com.roundel.csgodashboard.entities;

import android.content.Context;

import com.google.gson.Gson;

import java.io.BufferedReader;
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
                return new Gson().fromJson(builder.toString(), Utilities.class);
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

    public void saveToFile(Context context) throws IOException
    {
        FileOutputStream fos = context.openFileOutput(FILE_NAME, Context.MODE_PRIVATE);
        fos.write(new Gson().toJson(this).getBytes());
        fos.close();
    }
}
