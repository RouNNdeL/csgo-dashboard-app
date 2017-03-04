package com.roundel.csgodashboard.adapter.recyclerview;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.makeramen.roundedimageview.RoundedImageView;
import com.roundel.csgodashboard.R;
import com.roundel.csgodashboard.db.DbUtils;
import com.roundel.csgodashboard.entities.Map;
import com.roundel.csgodashboard.entities.utility.Utilities;
import com.roundel.csgodashboard.entities.utility.UtilityGrenade;

/**
 * Created by Krzysiek on 2017-03-04.
 */
public class UtilityGrenadeAdapter extends CursorRecyclerViewAdapter<UtilityGrenadeAdapter.ViewHolder>
{
    private static final String TAG = UtilityGrenadeAdapter.class.getSimpleName();

    //<editor-fold desc="private variables">
    private Context mContext;
//</editor-fold>

    public UtilityGrenadeAdapter(Context context, Cursor cursor)
    {
        super(cursor);
        this.mContext = context;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Cursor cursor)
    {
        RoundedImageView mapImageView = (RoundedImageView) viewHolder.itemView.findViewById(R.id.utility_grenade_map_img);

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
        tagsTextView.setText("Loading tags...");

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.utility_entry_grenade, parent, false);
        return new ViewHolder(view);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder
    {

        public ViewHolder(View itemView)
        {
            super(itemView);
        }
    }
}
