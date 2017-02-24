package com.roundel.csgodashboard.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.roundel.csgodashboard.entities.Map;
import com.roundel.csgodashboard.entities.Maps;
import com.roundel.csgodashboard.entities.utility.Grenade;
import com.roundel.csgodashboard.entities.utility.Stance;

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
        ContentValues values = new ContentValues(3);
        values.put(Map.COLUMN_NAME_CODE_NAME, map.getCodeName());
        values.put(Map.COLUMN_NAME_NAME, map.getName());
        values.put(Map.COLUMN_NAME_IMG_URI, map.getImageUri().toString());

        return db.insert(Map.TABLE_NAME, null, values);
    }

    public static long insertGrenade(SQLiteDatabase db, Grenade grenade)
    {
        ContentValues values = new ContentValues(1);
        values.put(Grenade._ID, grenade.getId());
        return db.insert(Grenade.TABLE_NAME, null, values);
    }

    public static long insertStance(SQLiteDatabase db, Stance stance)
    {
        ContentValues values = new ContentValues(1);
        values.put(Stance._ID, stance.getId());
        return db.insert(Stance.TABLE_NAME, null, values);
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
                orderColumn + " COLLATE LOCALIZED " + order
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

    public static boolean insertDefaultGrenades(SQLiteDatabase db, Context context)
    {
        boolean success = true;
        for(Grenade grenade : Grenade.getDefaultGrenadeList(context))
        {
            if(insertGrenade(db, grenade) == -1)
                success = false;
        }
        return success;
    }

    public static boolean insertDefaultStances(SQLiteDatabase db, Context context)
    {
        boolean success = true;
        for(Stance stance : Stance.getDefaultStanceList(context))
        {
            if(insertStance(db, stance) == -1)
                success = false;
        }
        return success;
    }

    public static Map queryMapById(SQLiteDatabase db, int id)
    {
        Cursor cursor = queryMaps(db, Map.PROJECTION_DATA, Map._ID + " = ?", new String[]{String.valueOf(id)}, Map._ID, ORDER_ASCENDING);
        if(cursor.moveToFirst())
        {
            return new Map(
                    cursor.getString(cursor.getColumnIndex(Map.COLUMN_NAME_CODE_NAME)),
                    cursor.getString(cursor.getColumnIndex(Map.COLUMN_NAME_NAME)),
                    Uri.parse(cursor.getString(cursor.getColumnIndex(Map.COLUMN_NAME_IMG_URI)))
            );
        }
        return null;
    }
}
