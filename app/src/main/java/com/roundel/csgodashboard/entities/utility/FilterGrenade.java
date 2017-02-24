package com.roundel.csgodashboard.entities.utility;

/**
 * Created by Krzysiek on 2017-02-24.
 */
public class FilterGrenade extends FilterBase
{
    private static final String TAG = FilterGrenade.class.getSimpleName();

    //<editor-fold desc="private variables">
    private Integer type;
    private Integer stance;
    private Boolean isJumpThrow;
    //</editor-fold>

    public Integer getType()
    {
        return type;
    }

    public void setType(Integer type)
    {
        this.type = type;
    }

    public Integer getStance()
    {
        return stance;
    }

    public void setStance(Integer stance)
    {
        this.stance = stance;
    }

    public Boolean getJumpThrow()
    {
        return isJumpThrow;
    }

    public void setJumpThrow(Boolean jumpThrow)
    {
        isJumpThrow = jumpThrow;
    }
}
