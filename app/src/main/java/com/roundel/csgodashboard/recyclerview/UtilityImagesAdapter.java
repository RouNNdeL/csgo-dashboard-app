package com.roundel.csgodashboard.recyclerview;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.roundel.csgodashboard.R;

import java.util.List;

/**
 * Created by Krzysiek on 2017-02-10.
 */
public class UtilityImagesAdapter extends RecyclerView.Adapter<UtilityImagesAdapter.ViewHolder>
{
    private static final String TAG = UtilityImagesAdapter.class.getSimpleName();

    private List<Uri> imageURIs;

    private Context mContext;
    private View.OnClickListener onAddPhotoListener;
    private View.OnClickListener onPhotoSelectedListener;

    public UtilityImagesAdapter(List<Uri> imageURIs, Context mContext)
    {
        this.imageURIs = imageURIs;
        this.mContext = mContext;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.utility_imageview, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position)
    {
        if(position < imageURIs.size())
        {
            final ImageView imageView = (ImageView) holder.itemView;
            imageView.setOnClickListener(onPhotoSelectedListener);
            Glide.with(mContext).load(imageURIs.get(position)).placeholder(R.drawable.ic_photo_white_24dp).into(imageView);
            imageView.setPadding(0, 0, 0, 0);
        }
        else
        {
            final ImageView imageView = (ImageView) holder.itemView;
            imageView.setOnClickListener(onAddPhotoListener);
            float density = mContext.getResources().getDisplayMetrics().density;
            int padding = (int) (density * 32);
            imageView.setPadding(padding, padding, padding, padding);
        }
    }

    @Override
    public int getItemCount()
    {
        return imageURIs.size() + 1;
    }

    public void setOnAddPhotoListener(View.OnClickListener onAddPhotoListener)
    {
        this.onAddPhotoListener = onAddPhotoListener;
    }

    public void setOnPhotoSelectedListener(View.OnClickListener onPhotoSelectedListener)
    {
        this.onPhotoSelectedListener = onPhotoSelectedListener;
    }

    public static class ViewHolder extends GameServerAdapter.ViewHolder
    {

        public ViewHolder(View content)
        {
            super(content);
        }
    }
}
