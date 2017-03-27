package com.roundel.csgodashboard.adapter.spinner;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.SimpleCursorAdapter;
import android.widget.CursorAdapter;

import com.roundel.csgodashboard.R;
import com.roundel.csgodashboard.entities.Map;

/**
 * Created by Krzysiek on 2017-03-23.
 */

public class MapAdapter extends SimpleCursorAdapter
{
    public MapAdapter(Context context, Cursor cursor)
    {
        super(
                context,
                R.layout.list_simple_one_line_no_ripple,
                cursor,
                new String[]{Map.COLUMN_NAME_NAME},
                new int[]{R.id.list_text_primary},
                CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER
        );
    }

    public MapAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags)
    {
        super(context, layout, c, from, to, flags);
    }

    public int getItemPosition(long id)
    {
        for(int i = 0; i < getCount(); i++)
        {
            final Object item = getItem(i);
            if(item instanceof Cursor && ((Cursor) item).getLong(mRowIDColumn) == id)
                return i;
        }
        return -1;
    }
}
