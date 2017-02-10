package com.roundel.csgodashboard.entities;

import java.net.URI;
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

    private int type;
    private boolean isJumpThrow;

    public UtilityGrenade(List<URI> imageUris, List<String> tags, Map map, String title, String description, int type, boolean isJumpThrow)
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
