package com.roundel.csgodashboard.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.roundel.csgodashboard.entities.Map;
import com.roundel.csgodashboard.entities.Maps;

/**
 * Created by Krzysiek on 2017-02-22.
 */
public class DbUtils
{
    private static final String TAG = DbUtils.class.getSimpleName();

    public static final String ORDER_ASCENDING = "ASC";
    public static final String ORDER_DESCENDING = "DESC";

    public static long insertMap(SQLiteDatabase db, Map map)
    {
        ContentValues values = new ContentValues();
        values.put(Map.COLUMN_NAME_CODE_NAME, map.getCodeName());
        values.put(Map.COLUMN_NAME_NAME, map.getName());
        values.put(Map.COLUMN_NAME_IMG_URI, map.getImageUri().toString());

        return db.insert(Map.TABLE_NAME, null, values);
    }

    public static Cursor queryMaps(SQLiteDatabase db, String[] projection, String selection, String[] selectionArgs, String orderColumn, String order)
    {
        return db.query(
                Map.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                orderColumn + " " + order
        );
    }

    public static Cursor queryMaps(SQLiteDatabase db, String[] projection)
    {
        return queryMaps(
                db,
                projection,
                null,
                null,
                Map._ID,
                ORDER_ASCENDING
        );
    }

    public static Cursor queryMaps(SQLiteDatabase db)
    {
        return queryMaps(
                db,
                new String[]{Map._ID, Map.COLUMN_NAME_CODE_NAME, Map.COLUMN_NAME_NAME, Map.COLUMN_NAME_IMG_URI}
        );
    }

    public static boolean insertDefaultMaps(SQLiteDatabase db, Context context)
    {
        boolean success = true;
        for(Map map : Maps.getDefaultMaps(context))
        {
            if(insertMap(db, map) == -1)
                success = false;
        }
        return success;
    }
}
