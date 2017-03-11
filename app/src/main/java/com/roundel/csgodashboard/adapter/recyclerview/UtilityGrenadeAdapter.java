package com.roundel.csgodashboard.adapter.recyclerview;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.LongSparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.roundel.csgodashboard.R;
import com.roundel.csgodashboard.db.DbUtils;
import com.roundel.csgodashboard.entities.Map;
import com.roundel.csgodashboard.entities.utility.Utilities;
import com.roundel.csgodashboard.entities.utility.UtilityGrenade;
import com.roundel.csgodashboard.view.CircleRectView;

/**
 * Created by Krzysiek on 2017-03-04.
 */
public class UtilityGrenadeAdapter extends CursorRecyclerViewAdapter<UtilityGrenadeAdapter.ViewHolder>
{
    private static final String TAG = UtilityGrenadeAdapter.class.getSimpleName();

    //<editor-fold desc="private variables">
    private Context mContext;
    private LongSparseArray<String> mTagArray;
    private View.OnClickListener mOnItemClickListener;
    //</editor-fold>

    public UtilityGrenadeAdapter(Context context, Cursor cursor, LongSparseArray<String> tagArray)
    {
        super(cursor);
        this.mContext = context;
        this.mTagArray = tagArray;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Cursor cursor)
    {
        CircleRectView mapImageView = (CircleRectView) viewHolder.itemView.findViewById(R.id.utility_grenade_map_img);

        TextView titleTextView = (TextView) viewHolder.itemView.findViewById(R.id.utility_grenade_title);
        TextView mapTextView = (TextView) viewHolder.itemView.findViewById(R.id.utility_grenade_map);
        TextView tagsTextView = (TextView) viewHolder.itemView.findViewById(R.id.utility_grenade_tags);

        final String[] imgIds = DbUtils.splitImgIds(
                cursor.getString(cursor.getColumnIndex(UtilityGrenade.COLUMN_NAME_IMG_IDS))
        );
        Uri mainImgUri = Uri.parse(imgIds.length > 0 ? "file://" + Utilities.getImgPath(mContext) + imgIds[0] : null);

        Glide.with(mContext).load(mainImgUri).into(mapImageView);

        titleTextView.setText(cursor.getString(cursor.getColumnIndex(UtilityGrenade.COLUMN_NAME_TITLE)));
        mapTextView.setText(cursor.getString(cursor.getColumnIndex(Map.COLUMN_NAME_NAME)));
        tagsTextView.setText(DbUtils.formatTagNames(
                mTagArray,
                DbUtils.splitTagIds(
                        cursor.getString(cursor.getColumnIndex(UtilityGrenade.COLUMN_NAME_TAG_IDS))
                )
        ));
    }

    @Override
    public long getItemId(int position)
    {
        final Cursor cursor = getCursor();
        if(cursor.moveToPosition(position))
        {
            return cursor.getLong(
                    cursor.getColumnIndex(UtilityGrenade.TABLE_NAME + "." + UtilityGrenade._ID)
            );
        }
        return -1;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.utility_entry_grenade, parent, false);
        if(mOnItemClickListener != null)
        {
            view.setOnClickListener(mOnItemClickListener);
        }
        return new ViewHolder(view);
    }

    public void setOnItemClickListener(View.OnClickListener onItemClickListener)
    {
        mOnItemClickListener = onItemClickListener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        public ViewHolder(View itemView)
        {
            super(itemView);
        }
    }
}
