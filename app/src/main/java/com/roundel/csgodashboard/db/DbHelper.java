package com.roundel.csgodashboard.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.roundel.csgodashboard.entities.Map;

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

    private static final String SQL_DELETE_MAPS =
            "DROP TABLE IF EXISTS " + Map.TABLE_NAME;

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
        DbUtils.insertDefaultMaps(db, context);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL(SQL_DELETE_MAPS);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        onUpgrade(db, oldVersion, newVersion);
    }
}
