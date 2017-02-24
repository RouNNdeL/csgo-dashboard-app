package com.roundel.csgodashboard.entities.utility;

/**
 * Created by Krzysiek on 2017-02-24.
 */
public class FilterBoost extends FilterBase
{
    private static final String TAG = FilterBoost.class.getSimpleName();

    //<editor-fold desc="private variables">
    private Integer numberOfTeamMates;
    private Boolean isRunBoost;
    //</editor-fold>

    public Boolean getRunBoost()
    {
        return isRunBoost;
    }

    public void setRunBoost(Boolean runBoost)
    {
        isRunBoost = runBoost;
    }

    public Integer getNumberOfTeamMates()
    {
        return numberOfTeamMates;
    }

    public void setNumberOfTeamMates(Integer numberOfTeamMates)
    {
        this.numberOfTeamMates = numberOfTeamMates;
    }
}
