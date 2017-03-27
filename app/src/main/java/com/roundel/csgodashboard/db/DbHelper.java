package com.roundel.csgodashboard.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.roundel.csgodashboard.entities.Map;
import com.roundel.csgodashboard.entities.utility.Tags;
import com.roundel.csgodashboard.entities.utility.UtilityBoost;
import com.roundel.csgodashboard.entities.utility.UtilityGrenade;

/**
 * Created by Krzysiek on 2017-02-22.
 */
public class DbHelper extends SQLiteOpenHelper
{
    private static final String TAG = DbHelper.class.getSimpleName();

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "entities.db";

    private static final String SQL_CREATE_MAPS =
            "CREATE TABLE " + Map.TABLE_NAME + " (" +
                    Map._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    Map.COLUMN_NAME_CODE_NAME + " TEXT NOT NULL UNIQUE," +
                    Map.COLUMN_NAME_NAME + " TEXT," +
                    Map.COLUMN_NAME_IMG_URI + " TEXT)";

    private static final String SQL_CREATE_TAGS =
            "CREATE TABLE " + Tags.TABLE_NAME + " (" +
                    Tags._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    Tags.COLUMN_NAME_NAME + " TEXT UNIQUE)";

    private static final String SQL_CREATE_GRENADES =
            "CREATE TABLE " + UtilityGrenade.TABLE_NAME + " (" +
                    UtilityGrenade._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    UtilityGrenade.COLUMN_NAME_TITLE + " TEXT NOT NULL," +
                    UtilityGrenade.COLUMN_NAME_DESCRIPTION + " TEXT," +
                    UtilityGrenade.COLUMN_NAME_JUMP_THROW + " INTEGER NOT NULL," +
                    UtilityGrenade.COLUMN_NAME_STANCE + " INTEGER NOT NULL," +
                    UtilityGrenade.COLUMN_NAME_MAP_ID + " INTEGER NOT NULL," +
                    UtilityGrenade.COLUMN_NAME_TYPE + " INTEGER NOT NULL," +
                    UtilityGrenade.COLUMN_NAME_TAG_IDS + " TEXT," +
                    UtilityGrenade.COLUMN_NAME_IMG_IDS + " TEXT" + ")";

    private static final String SQL_CREATE_BOOSTS =
            "CREATE TABLE " + UtilityBoost.TABLE_NAME + " (" +
                    UtilityBoost._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    UtilityBoost.COLUMN_NAME_TITLE + " TEXT NOT NULL," +
                    UtilityBoost.COLUMN_NAME_DESCRIPTION + " TEXT," +
                    UtilityBoost.COLUMN_NAME_RUNBOOST + " INTEGER NOT NULL," +
                    UtilityBoost.COLUMN_NAME_TEAMMATES + " INTEGER NOT NULL," +
                    UtilityBoost.COLUMN_NAME_MAP_ID + " INTEGER NOT NULL," +
                    UtilityBoost.COLUMN_NAME_TAGS + " TEXT," +
                    UtilityBoost.COLUMN_NAME_IMG_URIS + " TEXT" + ")";

    private static final String SQL_DELETE_MAPS =
            "DROP TABLE IF EXISTS " + Map.TABLE_NAME;
    private static final String SQL_DELETE_TAGS =
            "DROP TABLE IF EXISTS " + Tags.TABLE_NAME;
    private static final String SQL_DELETE_GRENADES =
            "DROP TABLE IF EXISTS " + UtilityGrenade.TABLE_NAME;
    private static final String SQL_DELETE_BOOSTS =
            "DROP TABLE IF EXISTS " + UtilityBoost.TABLE_NAME;

    //<editor-fold desc="private variables">
    private Context context;
    //</editor-fold>

    public DbHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL(SQL_CREATE_MAPS);
        db.execSQL(SQL_CREATE_TAGS);
        db.execSQL(SQL_CREATE_GRENADES);
        db.execSQL(SQL_CREATE_BOOSTS);

        DbUtils.insertDefaultMaps(db, context);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL(SQL_DELETE_MAPS);
        db.execSQL(SQL_DELETE_TAGS);
        db.execSQL(SQL_DELETE_GRENADES);
        db.execSQL(SQL_DELETE_BOOSTS);

        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        onUpgrade(db, oldVersion, newVersion);
    }
}
