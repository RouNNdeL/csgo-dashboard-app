package com.roundel.csgodashboard.entities;

import android.content.Context;
import android.support.annotation.DrawableRes;

import com.roundel.csgodashboard.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Krzysiek on 2017-02-08.
 */
public class Stance
{
    private static final String TAG = Stance.class.getSimpleName();

    private String title;
    private String description;
    private @DrawableRes int icon;

    public Stance(String title, String description, @DrawableRes int icon)
    {
        this.title = title;
        this.description = description;
        this.icon = icon;
    }

    public static List<Stance> getDefaultStanceList(Context context) throws IndexOutOfBoundsException
    {
        List<Stance> stanceList = new ArrayList<>();
        String[] stanceTitleArray = context.getResources().getStringArray(R.array.add_nade_stance_options);
        String[] stanceDescriptionArray = context.getResources().getStringArray(R.array.add_nade_stance_descriptions);

        //TODO: Change the icons
        stanceList.add(new Stance(stanceTitleArray[0], stanceDescriptionArray[0], R.drawable.ic_stance_running));
        stanceList.add(new Stance(stanceTitleArray[1], stanceDescriptionArray[1], R.drawable.ic_stance_running));
        stanceList.add(new Stance(stanceTitleArray[2], stanceDescriptionArray[2], R.drawable.ic_stance_running));
        stanceList.add(new Stance(stanceTitleArray[3], stanceDescriptionArray[3], R.drawable.ic_stance_running));
        stanceList.add(new Stance(stanceTitleArray[4], stanceDescriptionArray[4], R.drawable.ic_stance_walking));

        return stanceList;
    }

    public String getTitle()
    {
        return title;
    }

    public String getDescription()
    {
        return description;
    }

    public
    @DrawableRes
    int getIcon()
    {
        return icon;
    }
}
