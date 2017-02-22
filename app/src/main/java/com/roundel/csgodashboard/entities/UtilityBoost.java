package com.roundel.csgodashboard.entities;

import android.net.Uri;

import java.util.List;

/**
 * Created by Krzysiek on 2017-02-10.
 */
public class UtilityBoost extends UtilityBase
{
    private static final String TAG = UtilityBoost.class.getSimpleName();

    //<editor-fold desc="private variables">
    private int numberOfTeamMates;
    private boolean isRunBoost;
//</editor-fold>

    public UtilityBoost(List<Uri> imageUris, List<String> tags, Map map, String title, String description, int numberOfTeamMates, boolean isRunBoost)
    {
        super(imageUris, tags, map, title, description);
        this.numberOfTeamMates = numberOfTeamMates;
        this.isRunBoost = isRunBoost;
    }

    public int getNumberOfTeamMates()
    {
        return numberOfTeamMates;
    }

    public void setNumberOfTeamMates(int numberOfTeamMates)
    {
        this.numberOfTeamMates = numberOfTeamMates;
    }

    public boolean isRunBoost()
    {
        return isRunBoost;
    }

    public void setRunBoost(boolean runBoost)
    {
        isRunBoost = runBoost;
    }
}
