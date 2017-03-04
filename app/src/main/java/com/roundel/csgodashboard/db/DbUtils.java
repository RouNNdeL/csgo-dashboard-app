package com.roundel.csgodashboard.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import com.roundel.csgodashboard.entities.Map;
import com.roundel.csgodashboard.entities.Maps;
import com.roundel.csgodashboard.entities.utility.Tags;
import com.roundel.csgodashboard.entities.utility.UtilityGrenade;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
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

    //<editor-fold desc="Inserts">
    public static long insertMap(SQLiteDatabase db, Map map)
    {
        ContentValues values = new ContentValues(3);

        values.put(Map.COLUMN_NAME_CODE_NAME, map.getCodeName());
        values.put(Map.COLUMN_NAME_NAME, map.getName());
        values.put(Map.COLUMN_NAME_IMG_URI, map.getImageUri().toString());

        return db.insert(Map.TABLE_NAME, null, values);
    }

    public static long insertTag(SQLiteDatabase db, String tag)
    {
        ContentValues values = new ContentValues(1);
        values.put(Tags.COLUMN_NAME_NAME, tag);

        return db.insert(Tags.TABLE_NAME, null, values);
    }

    public static long insertOrThrowTag(SQLiteDatabase db, String tag)
    {
        ContentValues values = new ContentValues(1);
        values.put(Tags.COLUMN_NAME_NAME, tag);

        return db.insertOrThrow(Tags.TABLE_NAME, null, values);
    }

    public static long insertGrenade(SQLiteDatabase db, UtilityGrenade utilityGrenade)
    {
        ContentValues values = new ContentValues(8);

        HashSet<Long> tagIds = insertTags(db, utilityGrenade.getTags());

        values.put(UtilityGrenade.COLUMN_NAME_TITLE, utilityGrenade.getTitle());
        values.put(UtilityGrenade.COLUMN_NAME_DESCRIPTION, utilityGrenade.getDescription());
        values.put(UtilityGrenade.COLUMN_NAME_MAP_ID, utilityGrenade.getMap().getId());
        values.put(UtilityGrenade.COLUMN_NAME_IMG_IDS, joinImgIds(utilityGrenade.getImageIds()));
        values.put(UtilityGrenade.COLUMN_NAME_JUMP_THROW, utilityGrenade.isJumpThrow() ? 1 : 0);
        values.put(UtilityGrenade.COLUMN_NAME_STANCE, utilityGrenade.getStance());
        values.put(UtilityGrenade.COLUMN_NAME_TYPE, utilityGrenade.getType());
        values.put(UtilityGrenade.COLUMN_NAME_TAG_IDS, joinTagIds(tagIds));

        return db.insert(UtilityGrenade.TABLE_NAME, null, values);
    }
    //</editor-fold>

    //<editor-fold desc="Maps query">
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

    public static Map queryMapById(SQLiteDatabase db, int id)
    {
        Cursor cursor = queryMaps(db, Map.PROJECTION_DATA, Map._ID + " = ?", new String[]{String.valueOf(id)}, Map._ID, ORDER_ASCENDING);
        if(cursor.moveToFirst())
        {
            return new Map(
                    id,
                    cursor.getString(cursor.getColumnIndex(Map.COLUMN_NAME_CODE_NAME)),
                    cursor.getString(cursor.getColumnIndex(Map.COLUMN_NAME_NAME)),
                    Uri.parse(cursor.getString(cursor.getColumnIndex(Map.COLUMN_NAME_IMG_URI)))
            );
        }
        return null;
    }
    //</editor-fold>

    //<editor-fold desc="Grenades query">
    public static Cursor queryGrenades(SQLiteDatabase db, String[] projection, String selection, String[] selectionArgs, String orderColumn, String order)
    {
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        builder.setTables(UtilityGrenade.TABLE_NAME +
                " LEFT JOIN " + Map.TABLE_NAME +
                " ON " +
                UtilityGrenade.TABLE_NAME + "." + UtilityGrenade.COLUMN_NAME_MAP_ID +
                " = " +
                Map.TABLE_NAME + "." + Map._ID
        );
        Cursor primaryCursor = db.rawQuery(builder.buildQuery(
                projection,
                selection,
                null,
                null,
                orderColumn + " COLLATE LOCALIZED " + order,
                null
        ), selectionArgs);
        return primaryCursor;
    }

    public static Cursor queryGrenades(SQLiteDatabase db, String[] projection)
    {
        return queryGrenades(
                db,
                projection,
                null,
                null,
                UtilityGrenade.TABLE_NAME + "." + UtilityGrenade._ID,
                ORDER_ASCENDING
        );
    }

    public static Cursor queryGrenades(SQLiteDatabase db)
    {
        return queryGrenades(
                db,
                concatenateArrays(UtilityGrenade.PROJECTION_ALL, Map.PROJECTION_DATA)
        );
    }

    public static UtilityGrenade queryGrenadeById(SQLiteDatabase db, int id)
    {
        Cursor cursor = queryGrenades(
                db,
                concatenateArrays(UtilityGrenade.PROJECTION_DATA, Map.PROJECTION_ALL),
                UtilityGrenade.TABLE_NAME + "." + UtilityGrenade._ID + " = ?",
                new String[]{String.valueOf(id)},
                UtilityGrenade.TABLE_NAME + "." + UtilityGrenade._ID,
                ORDER_ASCENDING
        );
        if(cursor.moveToFirst())
        {
            //TODO: Finish the method

            final Tags tags = DbUtils.queryTags(
                    db,
                    DbUtils.splitTagIds(
                            cursor.getString(cursor.getColumnIndex(UtilityGrenade.COLUMN_NAME_TAG_IDS))
                    )
            );
            final UtilityGrenade utilityGrenade = UtilityGrenade.fromCursor(cursor);
            utilityGrenade.setTags(tags);
            return utilityGrenade;
        }
        return null;
    }
    //</editor-fold>

    //<editor-fold desc="Tags query">
    private static Tags queryTags(SQLiteDatabase db, List<Long> ids)
    {
        Tags tags = new Tags();
        Cursor cursor = queryTags(
                db,
                new String[]{Tags.COLUMN_NAME_NAME},
                buildTagQuery(ids.size()),
                tagSelectionArgsFromIdList(ids),
                Tags._ID,
                ORDER_ASCENDING
        );
        while(cursor.moveToNext())
        {
            tags.add(cursor.getString(cursor.getColumnIndex(Tags.COLUMN_NAME_NAME)));
        }
        return tags;
    }

    private static List<Long> queryTagIds(SQLiteDatabase db, List<String> names)
    {
        List<Long> ids = new ArrayList<>();
        Cursor cursor = queryTags(
                db,
                new String[]{Tags._ID},
                buildTagIdQuery(names.size()),
                names.toArray(new String[names.size()]),
                Tags._ID,
                ORDER_ASCENDING
        );
        while(cursor.moveToNext())
        {
            ids.add(cursor.getLong(cursor.getColumnIndex(Tags._ID)));
        }
        return ids;
    }

    public static Cursor queryTags(SQLiteDatabase db, String[] projection, String selection, String[] selectionArgs, String orderColumn, String order)
    {
        return db.query(
                Tags.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                orderColumn + " COLLATE LOCALIZED " + order
        );
    }

    public static Cursor queryTags(SQLiteDatabase db, String[] projection)
    {
        return queryTags(
                db,
                projection,
                null,
                null,
                Tags._ID,
                ORDER_ASCENDING
        );
    }

    public static Cursor queryTags(SQLiteDatabase db)
    {
        return queryTags(
                db,
                new String[]{Tags._ID, Tags.COLUMN_NAME_NAME},
                null,
                null,
                Tags._ID,
                ORDER_ASCENDING
        );
    }

    public static String queryTagById(SQLiteDatabase db, int id)
    {
        Cursor cursor = queryTags(
                db,
                new String[]{Tags.COLUMN_NAME_NAME},
                Tags._ID + " = ?",
                new String[]{String.valueOf(id)},
                Tags._ID,
                ORDER_ASCENDING
        );
        if(cursor.moveToFirst())
        {
            return cursor.getString(cursor.getColumnIndex(Tags.COLUMN_NAME_NAME));
        }
        return null;
    }
    //</editor-fold>

    //<editor-fold desc="Support functions">
    public static String[] splitImgIds(String string)
    {
        return string.split(ARRAY_DIVIDER);
    }

    public static String joinImgIds(List<String> list)
    {
        return TextUtils.join(ARRAY_DIVIDER, list);
    }

    public static List<Long> splitTagIds(String string)
    {
        String[] split = string.split(ARRAY_DIVIDER);
        List<Long> list = new ArrayList<>();

        for(String s : split)
        {
            list.add(Long.valueOf(s));
        }

        return list;
    }

    public static String joinTagIds(HashSet<Long> list)
    {
        return TextUtils.join(ARRAY_DIVIDER, list);
    }

    private static String buildTagQuery(int size)
    {
        StringBuilder builder = new StringBuilder();
        for(int i = 0; i < size; i++)
        {
            builder.append("?");
            if(i <= size - 2)
                builder.append(",");
        }
        return String.format(
                Tags._ID + " IN(%s)",
                builder.toString()
        );
    }

    private static String buildTagIdQuery(int size)
    {
        StringBuilder builder = new StringBuilder();
        for(int i = 0; i < size; i++)
        {
            builder.append("?");
            if(i <= size - 2)
                builder.append(",");
        }
        return String.format(
                Tags.COLUMN_NAME_NAME + " IN(%s)",
                builder.toString()
        );
    }

    private static String[] tagSelectionArgsFromIdList(List<Long> ids)
    {
        List<String> tmp = new ArrayList<>();
        for(long id : ids)
        {
            tmp.add(String.valueOf(id));
        }
        return tmp.toArray(new String[tmp.size()]);
    }

    private static HashSet<Long> insertTags(SQLiteDatabase db, Tags tags)
    {
        HashSet<Long> ids = new HashSet<>();
        List<String> notUniqueTags = new ArrayList<>();
        for(String tag : tags)
        {
            try
            {
                ids.add(insertOrThrowTag(db, tag));
            }
            catch(SQLiteConstraintException e)
            {
                notUniqueTags.add(tag);
            }
        }

        ids.addAll(queryTagIds(db, notUniqueTags));

        return ids;
    }

    private static <T> T[] concatenateArrays(T[] a, T[] b)
    {
        int aLen = a.length;
        int bLen = b.length;

        @SuppressWarnings("unchecked")
        T[] c = (T[]) Array.newInstance(a.getClass().getComponentType(), aLen + bLen);
        System.arraycopy(a, 0, c, 0, aLen);
        System.arraycopy(b, 0, c, aLen, bLen);

        return c;
    }

    private static String joinWithQuotes(String delimiter, String quotes, Iterable tokens)
    {
        StringBuilder builder = new StringBuilder();
        Iterator<?> it = tokens.iterator();
        if(it.hasNext())
        {
            builder.append(quotes).append(it.next()).append(quotes);
            while(it.hasNext())
            {
                builder.append(delimiter);
                builder.append(quotes).append(it.next()).append(quotes);
            }
        }
        return builder.toString();
    }
    //</editor-fold>
}