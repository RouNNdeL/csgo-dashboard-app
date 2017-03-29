package com.roundel.csgodashboard.entities.utility;

import android.provider.BaseColumns;

import com.roundel.csgodashboard.entities.Map;

import java.util.ArrayList;

/**
 * Created by Krzysiek on 2017-02-10.
 */
public class UtilityBoost extends UtilityBase implements BaseColumns
{
    private static final String TAG = UtilityBoost.class.getSimpleName();

    public static final String TABLE_NAME = "utilities_boosts";

    public static final String COLUMN_NAME_TITLE = "title";
    public static final String COLUMN_NAME_DESCRIPTION = "description";
    public static final String COLUMN_NAME_MAP_ID = "map";
    public static final String COLUMN_NAME_TEAMMATES = "teammates";
    public static final String COLUMN_NAME_RUNBOOST = "runboost";
    public static final String COLUMN_NAME_TAGS = "tags";
    public static final String COLUMN_NAME_IMG_URIS = "img_uris";

    //<editor-fold desc="private variables">
    private int numberOfTeamMates;
    private boolean isRunBoost;
    //</editor-fold>

    public UtilityBoost(ArrayList<String> imgIds, Tags tags, Map map, String title, String description, int numberOfTeamMates, boolean isRunBoost)
    {
        super(imgIds, tags, map, title, description);
        this.numberOfTeamMates = numberOfTeamMates;
        this.isRunBoost = isRunBoost;
    }

    @Override
    public boolean equals(Object o)
    {
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        if(!super.equals(o)) return false;

        UtilityBoost that = (UtilityBoost) o;

        if(numberOfTeamMates != that.numberOfTeamMates) return false;
        return isRunBoost == that.isRunBoost;

    }

    @Override
    public int hashCode()
    {
        int result = super.hashCode();
        result = 31 * result + numberOfTeamMates;
        result = 31 * result + (isRunBoost ? 1 : 0);
        return result;
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
