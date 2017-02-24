package com.roundel.csgodashboard.entities;

import android.content.Context;
import android.provider.BaseColumns;
import android.support.annotation.DrawableRes;

import com.roundel.csgodashboard.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Krzysiek on 2017-02-08.
 */
public class Stance implements BaseColumns
{
    private static final String TAG = Stance.class.getSimpleName();

    public static final String TABLE_NAME = "stances";

    public static final int TYPE_STANDING = 300;
    public static final int TYPE_CROUCHING = 301;
    public static final int TYPE_CRAB_WALKING = 302;
    public static final int TYPE_RUNNING = 303;
    public static final int TYPE_WALKING = 304;

    //<editor-fold desc="private variables">
    private int id;
    private String title;
    private String description;
    @DrawableRes private int icon;
    //</editor-fold>

    public Stance(int id, String title, String description, @DrawableRes int icon)
    {
        this.id = id;
        this.title = title;
        this.description = description;
        this.icon = icon;
    }

    public static List<Stance> getDefaultStanceList(Context context) throws IndexOutOfBoundsException
    {
        List<Stance> stanceList = new ArrayList<>();

        stanceList.add(Stance.fromType(TYPE_STANDING, context));
        stanceList.add(Stance.fromType(TYPE_CROUCHING, context));
        stanceList.add(Stance.fromType(TYPE_CRAB_WALKING, context));
        stanceList.add(Stance.fromType(TYPE_RUNNING, context));
        stanceList.add(Stance.fromType(TYPE_WALKING, context));

        return stanceList;
    }

    public static Stance fromType(int type, Context context)
    {
        String[] stanceTitleArray = context.getResources().getStringArray(R.array.add_nade_stance_options);
        String[] stanceDescriptionArray = context.getResources().getStringArray(R.array.add_nade_stance_descriptions);
        switch(type)
        {
            case TYPE_STANDING:
                return new Stance(type, stanceTitleArray[0], stanceDescriptionArray[0], R.drawable.ic_stance_standing);
            case TYPE_CROUCHING:
                return new Stance(type, stanceTitleArray[1], stanceDescriptionArray[1], R.drawable.ic_stance_crouching);
            case TYPE_CRAB_WALKING:
                return new Stance(type, stanceTitleArray[2], stanceDescriptionArray[2], R.drawable.ic_stance_crouching);
            case TYPE_RUNNING:
                return new Stance(type, stanceTitleArray[3], stanceDescriptionArray[3], R.drawable.ic_stance_running);
            case TYPE_WALKING:
                return new Stance(type, stanceTitleArray[4], stanceDescriptionArray[4], R.drawable.ic_stance_walking);
            default:
                throw new IllegalArgumentException("Type has to be one of {TYPE_STANDING, TYPE_CROUCHING, TYPE_CRAB_WALKING, TYPE_RUNNING, TYPE_WALKING}");

        }
    }

    public String getTitle()
    {
        return title;
    }

    public String getDescription()
    {
        return description;
    }

    public int getId()
    {
        return id;
    }

    @DrawableRes
    public int getIcon()
    {
        return icon;
    }
}
