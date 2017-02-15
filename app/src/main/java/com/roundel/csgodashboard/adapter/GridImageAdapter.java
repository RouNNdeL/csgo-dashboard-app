package com.roundel.csgodashboard.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.roundel.csgodashboard.R;

import java.util.List;

/**
 * Created by Krzysiek on 2017-02-15.
 */
public class GridImageAdapter extends BaseAdapter
{
    private static final String TAG = GridImageAdapter.class.getSimpleName();

    //<editor-fold desc="private variables">
    private List<Uri> imageURIs;

    private Context mContext;
    private View.OnClickListener onAddPhotoListener;
    private View.OnClickListener onPhotoSelectedListener;

    private LayoutInflater inflater;
    private int max_size = 50;
    //</editor-fold>

    public GridImageAdapter(List<Uri> imageURIs, Context context)
    {
        this.imageURIs = imageURIs;
        this.mContext = context;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount()
    {
        return Math.min(imageURIs.size() + 1, max_size);
    }

    @Override
    public Object getItem(int position)
    {
        return imageURIs.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        ImageView imageView = (ImageView) inflater.inflate(R.layout.utility_imageview, parent, false);
        if(position < imageURIs.size())
        {
            imageView.setOnClickListener(onPhotoSelectedListener);
            Glide.with(mContext).load(imageURIs.get(position)).placeholder(R.drawable.ic_photo_white_24dp).into(imageView);
            imageView.setPadding(0, 0, 0, 0);
        }
        else
        {
            imageView.setOnClickListener(onAddPhotoListener);
            float density = mContext.getResources().getDisplayMetrics().density;
            int padding = (int) (density * 32);
            imageView.setPadding(padding, padding, padding, padding);
        }
        return imageView;
    }

    public void setOnAddPhotoListener(View.OnClickListener onAddPhotoListener)
    {
        this.onAddPhotoListener = onAddPhotoListener;
    }

    public void setOnPhotoSelectedListener(View.OnClickListener onPhotoSelectedListener)
    {
        this.onPhotoSelectedListener = onPhotoSelectedListener;
    }
}
