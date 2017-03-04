package com.roundel.csgodashboard.entities.utility;

import android.provider.BaseColumns;

import com.roundel.csgodashboard.entities.Map;

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
    public static final String COLUMN_NAME_TAG_IDS = "tag_ids";
    public static final String COLUMN_NAME_IMG_IDS = "img_ids";

    public static final String[] PROJECTION_ALL = {
            _ID,
            COLUMN_NAME_TITLE,
            COLUMN_NAME_DESCRIPTION,
            COLUMN_NAME_MAP_ID,
            COLUMN_NAME_TYPE,
            COLUMN_NAME_STANCE,
            COLUMN_NAME_JUMP_THROW,
            COLUMN_NAME_TAG_IDS,
            COLUMN_NAME_IMG_IDS
    };

    public static final String[] PROJECTION_DATA = {
            COLUMN_NAME_TITLE,
            COLUMN_NAME_DESCRIPTION,
            COLUMN_NAME_MAP_ID,
            COLUMN_NAME_TYPE,
            COLUMN_NAME_STANCE,
            COLUMN_NAME_JUMP_THROW,
            COLUMN_NAME_TAG_IDS,
            COLUMN_NAME_IMG_IDS
    };

    //<editor-fold desc="private variables">
    private int type;
    private int stance;
    private boolean isJumpThrow;
    //</editor-fold>

    public UtilityGrenade(List<String> imageIds, Tags tags, Map map, String title, String description, int type, int stance, boolean isJumpThrow)
    {
        super(imageIds, tags, map, title, description);
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

    public int getStance()
    {
        return stance;
    }

    public void setStance(int stance)
    {
        this.stance = stance;
    }
}
