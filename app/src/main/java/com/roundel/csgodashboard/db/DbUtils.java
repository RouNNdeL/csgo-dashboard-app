package com.roundel.csgodashboard.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.text.TextUtils;

import com.roundel.csgodashboard.entities.Map;
import com.roundel.csgodashboard.entities.Maps;
import com.roundel.csgodashboard.entities.utility.Grenade;
import com.roundel.csgodashboard.entities.utility.Stance;
import com.roundel.csgodashboard.entities.utility.UtilityGrenade;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Krzysiek on 2017-02-22.
 */
public class DbUtils
{
    private static final String TAG = DbUtils.class.getSimpleName();

    public static final String ORDER_ASCENDING = "ASC";
    public static final String ORDER_DESCENDING = "DESC";

    private static final String ARRAY_DIVIDER = ";";

    public static long insertMap(SQLiteDatabase db, Map map)
    {
        ContentValues values = new ContentValues(3);
        values.put(Map.COLUMN_NAME_CODE_NAME, map.getCodeName());
        values.put(Map.COLUMN_NAME_NAME, map.getName());
        values.put(Map.COLUMN_NAME_IMG_URI, map.getImageUri().toString());

        return db.insert(Map.TABLE_NAME, null, values);
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
                Map.PROJECTION_ALL
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

    public static Cursor queryGrenades(SQLiteDatabase db, String[] projection, String selection, String[] selectionArgs, String orderColumn, String order)
    {
        return db.query(
                Grenade.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                orderColumn + " COLLATE LOCALIZED " + order
        );
    }

    public static Cursor queryGrenades(SQLiteDatabase db, String[] projection)
    {
        return queryGrenades(
                db,
                projection,
                null,
                null,
                UtilityGrenade._ID,
                ORDER_ASCENDING
        );
    }

    public static Cursor queryGrenades(SQLiteDatabase db)
    {
        return queryGrenades(
                db,
                UtilityGrenade.PROJECTION_ALL
        );
    }

    public static UtilityGrenade queryGrenadeById(SQLiteDatabase db, int id)
    {
        Cursor cursor = queryGrenades(
                db,
                UtilityGrenade.PROJECTION_DATA,
                UtilityGrenade._ID + " = ?",
                new String[]{String.valueOf(id)},
                UtilityGrenade._ID,
                ORDER_ASCENDING
        );
        if(cursor.moveToFirst())
        {
            //TODO: Finish the method
            return new UtilityGrenade(
                    null,
                    null,
                    0,
                    null,
                    null,
                    0,
                    0,
                    false
            );
        }
        return null;
    }

    private static List<Uri> splitUris(String string)
    {
        String[] split = string.split(ARRAY_DIVIDER);
        List<Uri> list = new ArrayList<>();

        for(String s : split)
        {
            list.add(Uri.parse(s));
        }

        return list;
    }

    private static String joinUris(List<Uri> list)
    {
        return TextUtils.join(ARRAY_DIVIDER, list);
    }

    private static List<Integer> splitTagIds(String string)
    {
        String[] split = string.split(ARRAY_DIVIDER);
        List<Integer> list = new ArrayList<>();

        for(String s : split)
        {
            list.add(Integer.valueOf(s));
        }

        return list;
    }

    private static String joinTagIds(List<Integer> list)
    {
        return TextUtils.join(ARRAY_DIVIDER, list);
    }
}
