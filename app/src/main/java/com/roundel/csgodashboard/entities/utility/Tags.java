package com.roundel.csgodashboard.entities.utility;

import android.provider.BaseColumns;

import java.util.ArrayList;

/**
 * Created by Krzysiek on 2017-03-01.
 */
public class Tags extends ArrayList<String> implements BaseColumns
{
    private static final String TAG = Tags.class.getSimpleName();

    public static final String TABLE_NAME = "tags";

    public static final String COLUMN_NAME_NAME = "name";
}
