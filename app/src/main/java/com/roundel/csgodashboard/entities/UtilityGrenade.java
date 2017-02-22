package com.roundel.csgodashboard.entities;

import android.net.Uri;

import java.util.List;

/**
 * Created by Krzysiek on 2017-02-10.
 */
public class UtilityGrenade extends UtilityBase
{
    private static final String TAG = UtilityGrenade.class.getSimpleName();

    public static final int GRENADE_FLASHBANG = 200;
    public static final int GRENADE_HE = 201;
    public static final int GRENADE_MOLOTOV = 202;
    public static final int GRENADE_SMOKE = 203;

    //<editor-fold desc="private variables">
    private int type;
    private boolean isJumpThrow;
    //</editor-fold>

    public UtilityGrenade(List<Uri> imageUris, List<String> tags, Map map, String title, String description, int type, boolean isJumpThrow)
    {
        super(imageUris, tags, map, title, description);
        this.type = type;
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
