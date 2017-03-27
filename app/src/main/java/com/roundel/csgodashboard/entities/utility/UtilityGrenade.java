package com.roundel.csgodashboard.entities.utility;

import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;

import com.roundel.csgodashboard.db.DbUtils;
import com.roundel.csgodashboard.entities.Map;

import java.util.Arrays;
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
    public static final String COLUMN_NAME_TYPE = "grenade_id";
    public static final String COLUMN_NAME_STANCE = "stance";
    public static final String COLUMN_NAME_JUMP_THROW = "jumpthrow";
    public static final String COLUMN_NAME_TAG_IDS = "tag_ids";
    public static final String COLUMN_NAME_IMG_IDS = "img_ids";

    public static final String[] PROJECTION_ALL = {
            TABLE_NAME + "." + _ID,
            TABLE_NAME + "." + COLUMN_NAME_TITLE,
            TABLE_NAME + "." + COLUMN_NAME_DESCRIPTION,
            TABLE_NAME + "." + COLUMN_NAME_MAP_ID,
            TABLE_NAME + "." + COLUMN_NAME_TYPE,
            TABLE_NAME + "." + COLUMN_NAME_STANCE,
            TABLE_NAME + "." + COLUMN_NAME_JUMP_THROW,
            TABLE_NAME + "." + COLUMN_NAME_TAG_IDS,
            TABLE_NAME + "." + COLUMN_NAME_IMG_IDS
    };

    public static final String[] PROJECTION_DATA = {
            TABLE_NAME + "." + COLUMN_NAME_TITLE,
            TABLE_NAME + "." + COLUMN_NAME_DESCRIPTION,
            TABLE_NAME + "." + COLUMN_NAME_MAP_ID,
            TABLE_NAME + "." + COLUMN_NAME_TYPE,
            TABLE_NAME + "." + COLUMN_NAME_STANCE,
            TABLE_NAME + "." + COLUMN_NAME_JUMP_THROW,
            TABLE_NAME + "." + COLUMN_NAME_TAG_IDS,
            TABLE_NAME + "." + COLUMN_NAME_IMG_IDS
    };

    //<editor-fold desc="private variables">
    private int grenadeId;
    private int stance;
    private boolean isJumpThrow;
    //</editor-fold>

    public UtilityGrenade(List<String> imageIds, Tags tags, Map map, String title, String description, int grenadeId, int stance, boolean isJumpThrow)
    {
        super(imageIds, tags, map, title, description);
        this.grenadeId = grenadeId;
        this.stance = stance;
        this.isJumpThrow = isJumpThrow;
    }

    public static UtilityGrenade fromCursor(Cursor cursor)
    {

        final List<String> imgIds = Arrays.asList(DbUtils.splitImgIds(
                cursor.getString(cursor.getColumnIndex(UtilityGrenade.COLUMN_NAME_IMG_IDS))
        ));
        final Map map = new Map(
                (int) cursor.getLong(cursor.getColumnIndex(Map.TABLE_NAME + "." + Map._ID)),
                cursor.getString(cursor.getColumnIndex(Map.TABLE_NAME + "." + Map.COLUMN_NAME_CODE_NAME)),
                cursor.getString(cursor.getColumnIndex(Map.TABLE_NAME + "." + Map.COLUMN_NAME_NAME)),
                Uri.parse(cursor.getString(cursor.getColumnIndex(Map.TABLE_NAME + "." + Map.COLUMN_NAME_IMG_URI)))
        );
        return new UtilityGrenade(
                imgIds,
                null,
                map,
                cursor.getString(cursor.getColumnIndex(UtilityGrenade.TABLE_NAME + "." + UtilityGrenade.COLUMN_NAME_TITLE)),
                cursor.getString(cursor.getColumnIndex(UtilityGrenade.TABLE_NAME + "." + UtilityGrenade.COLUMN_NAME_DESCRIPTION)),
                cursor.getInt(cursor.getColumnIndex(UtilityGrenade.TABLE_NAME + "." + UtilityGrenade.COLUMN_NAME_TYPE)),
                cursor.getInt(cursor.getColumnIndex(UtilityGrenade.TABLE_NAME + "." + UtilityGrenade.COLUMN_NAME_STANCE)),
                cursor.getInt(cursor.getColumnIndex(UtilityGrenade.TABLE_NAME + "." + UtilityGrenade.COLUMN_NAME_JUMP_THROW)) == 1
        );
    }

    @Override
    public boolean equals(Object o)
    {
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        if(!super.equals(o)) return false;

        UtilityGrenade that = (UtilityGrenade) o;

        if(grenadeId != that.grenadeId) return false;
        if(stance != that.stance) return false;
        return isJumpThrow == that.isJumpThrow;

    }

    @Override
    public int hashCode()
    {
        int result = super.hashCode();
        result = 31 * result + grenadeId;
        result = 31 * result + stance;
        result = 31 * result + (isJumpThrow ? 1 : 0);
        return result;
    }

    public int getGrenadeId()
    {
        return grenadeId;
    }

    public void setGrenadeId(int grenadeId)
    {
        this.grenadeId = grenadeId;
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
