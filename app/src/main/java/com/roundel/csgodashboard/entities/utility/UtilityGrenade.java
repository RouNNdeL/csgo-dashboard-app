package com.roundel.csgodashboard.entities.utility;

import android.net.Uri;
import android.provider.BaseColumns;

import java.util.List;

/**
 * Created by Krzysiek on 2017-02-10.
 */
public class UtilityGrenade extends UtilityBase implements BaseColumns
{
    private static final String TAG = UtilityGrenade.class.getSimpleName();

    public static final String TABLE_NAME = "utilities_grenades";

    public static final String COLUMN_NAME_TITLE = "title";
    public static final String COLUMN_NAME_DESCRIPTION = "description";
    public static final String COLUMN_NAME_MAP_ID = "map";
    public static final String COLUMN_NAME_TYPE = "type";
    public static final String COLUMN_NAME_STANCE = "stance";
    public static final String COLUMN_NAME_JUMP_THROW = "jumpthrow";
    public static final String COLUMN_NAME_TAGS = "tags";
    public static final String COLUMN_NAME_IMG_URIS = "img_uris";

    //<editor-fold desc="private variables">
    private int type;
    private int stance;
    private boolean isJumpThrow;
    //</editor-fold>

    public UtilityGrenade(List<Uri> imageUris, Tags tags, int mapId, String title, String description, int type, int stance, boolean isJumpThrow)
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
