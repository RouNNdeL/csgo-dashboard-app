package com.roundel.csgodashboard.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.LongSparseArray;

import com.roundel.csgodashboard.entities.Map;
import com.roundel.csgodashboard.entities.Maps;
import com.roundel.csgodashboard.entities.utility.FilterGrenade;
import com.roundel.csgodashboard.entities.utility.Tags;
import com.roundel.csgodashboard.entities.utility.UtilityGrenade;
import com.roundel.csgodashboard.util.LogHelper;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collectors;

/**
 * Created by Krzysiek on 2017-02-22.
 */
public class DbUtils
{
    private static final String TAG = DbUtils.class.getSimpleName();

    public static final String ORDER_ASCENDING = "ASC";
    public static final String ORDER_DESCENDING = "DESC";

    private static final String ARRAY_DIVIDER = ";";

    //Used to be able to perform a LIKE (GLOB) query on the tag list
    // ex. _2_;_4_;_8_ can be queried with tag_ids GLOB *_2_* (for tag with id 2)
    private static final String TAG_IDENTIFIER = "_";

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

    public static long insertOrThrowTag(SQLiteDatabase db, Tags.Tag tag)
    {
        ContentValues values = new ContentValues(1);
        values.put(Tags.COLUMN_NAME_NAME, tag.getName());

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
        values.put(UtilityGrenade.COLUMN_NAME_TYPE, utilityGrenade.getGrenadeId());
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
    public static Cursor queryGrenadesWithoutMaps(SQLiteDatabase db, String[] projection, String selection, String[] selectionArgs, String orderColumn, String order)
    {
        return db.query(
                UtilityGrenade.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                orderColumn + " COLLATE LOCALIZED " + order
        );
    }

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
        return db.rawQuery(builder.buildQuery(
                projection,
                selection,
                null,
                null,
                orderColumn + " COLLATE LOCALIZED " + order,
                null
        ), selectionArgs);
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

    public static Cursor queryGrenades(SQLiteDatabase db, String selection, String[] selectionArgs)
    {
        return queryGrenades(
                db,
                concatenateArrays(UtilityGrenade.PROJECTION_ALL, Map.PROJECTION_DATA),
                selection,
                selectionArgs,
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

            final Tags tags = queryTags(
                    db,
                    splitTagIds(
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
    private static Tags queryTags(SQLiteDatabase db, HashSet<Long> ids)
    {
        Tags tags = new Tags();
        Cursor cursor = queryTags(
                db,
                new String[]{Tags._ID, Tags.COLUMN_NAME_NAME},
                buildTagQuery(ids.size()),
                tagSelectionArgsFromIdSet(ids),
                Tags._ID,
                ORDER_ASCENDING
        );
        while(cursor.moveToNext())
        {
            tags.add(new Tags.Tag(
                            cursor.getLong(cursor.getColumnIndex(Tags._ID)),
                            cursor.getString(cursor.getColumnIndex(Tags.COLUMN_NAME_NAME))
                    )
            );
        }
        return tags;
    }

    private static HashSet<Long> queryTagIdsByName(SQLiteDatabase db, String name)
    {
        Cursor cursor = queryTags(
                db,
                new String[]{Tags._ID},
                Tags.COLUMN_NAME_NAME + " GLOB ?",
                new String[]{"*" + name + "*"}
        );

        HashSet<Long> ids = new HashSet<>();
        while(cursor.moveToNext())
        {
            ids.add(cursor.getLong(cursor.getColumnIndex(Tags._ID)));
        }
        return ids;
    }

    public static Tags queryAllTags(SQLiteDatabase db)
    {
        Tags tags = new Tags();
        Cursor cursor = queryTags(
                db,
                new String[]{Tags._ID, Tags.COLUMN_NAME_NAME},
                null,
                null,
                Tags._ID,
                ORDER_ASCENDING
        );
        while(cursor.moveToNext())
        {
            tags.add(new Tags.Tag(
                            cursor.getLong(cursor.getColumnIndex(Tags._ID)),
                            cursor.getString(cursor.getColumnIndex(Tags.COLUMN_NAME_NAME))
                    )
            );
        }
        return tags;
    }

    private static LongSparseArray<String> queryTagsAsSparseArray(SQLiteDatabase db, HashSet<Long> ids)
    {
        LongSparseArray<String> array = new LongSparseArray<>();
        Cursor cursor = queryTags(
                db,
                new String[]{Tags._ID, Tags.COLUMN_NAME_NAME},
                buildTagQuery(ids.size()),
                tagSelectionArgsFromIdSet(ids),
                Tags._ID,
                ORDER_ASCENDING
        );
        while(cursor.moveToNext())
        {
            array.put(
                    cursor.getLong(cursor.getColumnIndex(Tags._ID)),
                    cursor.getString(cursor.getColumnIndex(Tags.COLUMN_NAME_NAME))
            );
        }
        return array;
    }

    public static LongSparseArray<String> queryTagsForGrenades(SQLiteDatabase db)
    {
        return queryTagsForGrenades(db, null, null);
    }

    public static LongSparseArray<String> queryTagsForGrenades(SQLiteDatabase db, String selection, String[] selectionArgs)
    {
        return queryTagsForGrenades(db, selection, selectionArgs, UtilityGrenade._ID, ORDER_ASCENDING);
    }

    public static LongSparseArray<String> queryTagsForGrenades(SQLiteDatabase db, String selection, String[] selectionArgs, String orderColumn, String order)
    {
        Cursor tagIds = queryGrenadesWithoutMaps(db, new String[]{UtilityGrenade.COLUMN_NAME_TAG_IDS}, selection, selectionArgs, orderColumn, order);
        HashSet<Long> ids = new HashSet<>();

        //Fetch all required ids
        while(tagIds.moveToNext())
        {
            ids.addAll(splitTagIds(
                    tagIds.getString(tagIds.getColumnIndex(UtilityGrenade.COLUMN_NAME_TAG_IDS)))
            );
        }

        return queryTagsAsSparseArray(db, ids);
    }

    private static HashSet<Long> queryTagIds(SQLiteDatabase db, Tags tags)
    {
        HashSet<Long> ids = new HashSet<>();
        Cursor cursor = queryTags(
                db,
                new String[]{Tags._ID},
                buildTagIdQuery(tags.size()),
                tags.getNamesArray(),
                Tags._ID,
                ORDER_ASCENDING
        );
        while(cursor.moveToNext())
        {
            ids.add(cursor.getLong(cursor.getColumnIndex(Tags._ID)));
        }
        return ids;
    }

    public static Cursor queryTags(SQLiteDatabase db, String[] projection, String selection, String[] selectionArgs)
    {
        return queryTags(db, projection, selection, selectionArgs, Tags._ID, ORDER_ASCENDING);
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

    public static HashSet<Long> splitTagIds(String string)
    {
        String[] split = string.split(ARRAY_DIVIDER);
        HashSet<Long> list = new HashSet<>();

        for(String s : split)
        {
            if(s.length() == 0)
                continue;
            list.add(Long.valueOf(s.substring(
                    TAG_IDENTIFIER.length(),
                    s.length() - TAG_IDENTIFIER.length()
            )));
        }

        return list;
    }

    public static String joinTagIds(HashSet<Long> list)
    {

        StringBuilder builder = new StringBuilder();
        Iterator<Long> iterator = list.iterator();
        if(iterator.hasNext())
        {
            builder.append(TAG_IDENTIFIER).append(iterator.next()).append(TAG_IDENTIFIER);
            while(iterator.hasNext())
            {
                builder.append(ARRAY_DIVIDER);
                builder.append(TAG_IDENTIFIER).append(iterator.next()).append(TAG_IDENTIFIER);
            }
        }
        return builder.toString();

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
        List<String> tmp;
        if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N)
        {
            tmp = ids.stream().map(String::valueOf).collect(Collectors.toList());
        }
        else
        {
            tmp = new ArrayList<>();
            //noinspection Convert2streamapi
            for(long id : ids)
            {
                tmp.add(String.valueOf(id));
            }
        }
        return tmp.toArray(new String[tmp.size()]);
    }

    private static String[] tagSelectionArgsFromIdSet(HashSet<Long> ids)
    {
        return tagSelectionArgsFromIdList(new ArrayList<>(ids));
    }

    private static HashSet<Long> insertTags(SQLiteDatabase db, Tags tags)
    {
        HashSet<Long> ids = new HashSet<>();
        Tags notUniqueTags = new Tags();
        for(Tags.Tag tag : tags)
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

    /**
     * @param array that the values are going to be fetched from
     * @param ids   of tags to be displayed
     *
     * @return {@link String} of tag names split with a comma (ex. 'tag1, tag2, tag3')
     */
    public static String formatTagNames(LongSparseArray<String> array, HashSet<Long> ids)
    {
        return formatTagNames(array, ids, ", ");
    }

    /**
     * @param array     that the values are going to be fetched from
     * @param ids       of tags to be displayed
     * @param delimiter the sequence of characters to be used between each element added to the
     *                  formatted {@link String} value
     *
     * @return {@link String} of tag names split with a comma (ex. 'tag1, tag2, tag3')
     */
    public static String formatTagNames(LongSparseArray<String> array, HashSet<Long> ids, CharSequence delimiter)
    {
        if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N)
        {
            StringJoiner joiner = new StringJoiner(delimiter);
            for(int i = 0; i < array.size(); i++)
            {
                final long key = array.keyAt(i);
                if(ids.contains(key))
                {
                    joiner.add(array.get(key));
                }
            }

            return joiner.toString();
        }
        else
        {
            StringBuilder builder = new StringBuilder();
            for(int i = 0; i < array.size(); i++)
            {
                final long key = array.keyAt(i);
                if(ids.contains(key))
                {
                    builder.append(array.get(key));
                    builder.append(delimiter);
                }
            }

            builder.setLength(builder.length() - delimiter.length());
            return builder.toString();
        }
    }

    /**
     * @param filter {@link FilterGrenade} object containing filter properties
     *
     * @return a {@link Query} object containing a selection {@link String} and selection arguments
     * {@link String}[]
     */
    public static Query buildQueryFromGrenadeFilter(SQLiteDatabase db, FilterGrenade filter)
    {
        HashSet<Long> tagIds = null;
        if(filter.getSearchQuery() != null)
            tagIds = queryTagIdsByName(db, filter.getSearchQuery());
        return buildQueryFromGrenadeFilter(filter, tagIds);
    }

    /**
     * @param filter         {@link FilterGrenade} object containing filter properties
     * @param matchingTagIds a {@link HashSet} of tag ids that match the free text query
     *
     * @return a {@link Query} object containing a selection {@link String} and selection arguments
     * {@link String}[]
     */
    private static Query buildQueryFromGrenadeFilter(FilterGrenade filter, HashSet<Long> matchingTagIds)
    {
        StringBuilder builder = new StringBuilder();
        List<String> selectionArgs = new ArrayList<>();

        boolean appendJoiner = false;

        if(filter.getSearchQuery() != null)
        {
            builder.append(UtilityGrenade.TABLE_NAME)
                    .append(".")
                    .append(UtilityGrenade.COLUMN_NAME_TITLE)
                    .append(" GLOB ?");
            selectionArgs.add("*" + filter.getSearchQuery() + "*");
            appendJoiner = true;
        }

        if(filter.getJumpThrow() != null)
        {
            if(appendJoiner)
                builder.append(" AND ");
            builder.append(UtilityGrenade.TABLE_NAME)
                    .append(".")
                    .append(UtilityGrenade.COLUMN_NAME_JUMP_THROW)
                    .append(" = ?");
            selectionArgs.add(String.valueOf(filter.getJumpThrow() ? 1 : 0));
            appendJoiner = true;
        }

        if(filter.getType() != null)
        {
            if(appendJoiner)
                builder.append(" AND ");
            builder.append(UtilityGrenade.TABLE_NAME)
                    .append(".")
                    .append(UtilityGrenade.COLUMN_NAME_TYPE)
                    .append(" = ?");
            selectionArgs.add(String.valueOf(filter.getType()));
            appendJoiner = true;
        }

        if(filter.getMapId() != null)
        {
            if(appendJoiner)
                builder.append(" AND ");
            builder.append(UtilityGrenade.TABLE_NAME)
                    .append(".")
                    .append(UtilityGrenade.COLUMN_NAME_MAP_ID)
                    .append(" = ?");
            selectionArgs.add(String.valueOf(filter.getMapId()));
            appendJoiner = true;
        }

        if(filter.getTagIds() != null && filter.getTagIds().size() > 0)
        {
            if(appendJoiner)
                builder.append(" AND ");

            builder.append("(");

            Iterator<Long> iterator = filter.getTagIds().iterator();

            if(iterator.hasNext())
            {
                builder.append(UtilityGrenade.TABLE_NAME)
                        .append(".")
                        .append(UtilityGrenade.COLUMN_NAME_TAG_IDS)
                        .append(" GLOB ?");
                selectionArgs.add("*" + TAG_IDENTIFIER + iterator.next() + TAG_IDENTIFIER + "*");
                while(iterator.hasNext())
                {
                    builder.append(" OR ");
                    builder.append(UtilityGrenade.TABLE_NAME)
                            .append(".")
                            .append(UtilityGrenade.COLUMN_NAME_TAG_IDS)
                            .append(" GLOB ?");
                    selectionArgs.add("*" + TAG_IDENTIFIER + iterator.next() + TAG_IDENTIFIER + "*");
                }
            }

            builder.append(")");
            appendJoiner = true;
        }

        if(matchingTagIds != null && matchingTagIds.size() > 0)
        {
            if(appendJoiner)
                builder.append(" OR ");

            builder.append("(");

            Iterator<Long> iterator = matchingTagIds.iterator();

            if(iterator.hasNext())
            {
                builder.append(UtilityGrenade.TABLE_NAME)
                        .append(".")
                        .append(UtilityGrenade.COLUMN_NAME_TAG_IDS)
                        .append(" GLOB ?");
                selectionArgs.add("*" + TAG_IDENTIFIER + iterator.next() + TAG_IDENTIFIER + "*");
                while(iterator.hasNext())
                {
                    builder.append(" OR ");
                    builder.append(UtilityGrenade.TABLE_NAME)
                            .append(".")
                            .append(UtilityGrenade.COLUMN_NAME_TAG_IDS)
                            .append(" GLOB ?");
                    selectionArgs.add("*" + TAG_IDENTIFIER + iterator.next() + TAG_IDENTIFIER + "*");
                }
            }

            builder.append(")");
            appendJoiner = true;
        }

        final Query query = new Query(builder.toString(), selectionArgs);
        LogHelper.d(TAG, query.toString());
        return query;
    }
    //</editor-fold>

    public static class Query
    {
        public String selection;
        public String[] selectionArgs;
        //<editor-fold desc="private variables">
        //</editor-fold>

        private Query(String selection, String[] selectionArgs)
        {
            this.selection = selection;
            this.selectionArgs = selectionArgs;
        }

        private Query(String selection, List<String> selectionArgs)
        {
            this(selection, selectionArgs.toArray(new String[selectionArgs.size()]));
        }

        @Override
        public String toString()
        {
            return "Query{" +
                    "selection='" + selection + '\'' +
                    ", selectionArgs=" + Arrays.toString(selectionArgs) +
                    '}';
        }
    }
}