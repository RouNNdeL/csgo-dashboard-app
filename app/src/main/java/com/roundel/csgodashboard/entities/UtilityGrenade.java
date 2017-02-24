package com.roundel.csgodashboard.entities;

import android.net.Uri;

import java.util.List;

/**
 * Created by Krzysiek on 2017-02-10.
 */
public class UtilityGrenade extends UtilityBase
{
    private static final String TAG = UtilityGrenade.class.getSimpleName();

    //<editor-fold desc="private variables">
    private int type;
    private int stance;
    private boolean isJumpThrow;
    //</editor-fold>

    public UtilityGrenade(List<Uri> imageUris, List<String> tags, int mapId, String title, String description, int type, int stance, boolean isJumpThrow)
    {
        super(imageUris, tags, mapId, title, description);
        this.type = type;
        this.stance = stance;
        this.isJumpThrow = isJumpThrow;
    }

    public int getType()
    {
        return type;
    }

    public void setType(int type)
    {
        this.type = type;
    }

    public boolean isJumpThrow()
    {
        return isJumpThrow;
    }

    public void setJumpThrow(boolean jumpThrow)
    {
        isJumpThrow = jumpThrow;
    }
}
