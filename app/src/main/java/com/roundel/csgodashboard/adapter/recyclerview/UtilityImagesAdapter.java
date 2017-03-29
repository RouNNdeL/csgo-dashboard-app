package com.roundel.csgodashboard.adapter.recyclerview;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.roundel.csgodashboard.R;
import com.transitionseverywhere.Fade;
import com.transitionseverywhere.Transition;
import com.transitionseverywhere.TransitionManager;
import com.transitionseverywhere.TransitionSet;
import com.transitionseverywhere.extra.Scale;

import java.util.List;

/**
 * Created by Krzysiek on 2017-02-10.
 */
public class UtilityImagesAdapter extends RecyclerView.Adapter<UtilityImagesAdapter.ViewHolder>
{
    private static final String TAG = UtilityImagesAdapter.class.getSimpleName();

    private static final int TYPE_ITEM = 400;
    private static final int TYPE_ADD = 401;

    private List<Uri> imageURIs;

    private Context mContext;
    private PhotoActions mPhotoActionListener;
    private RecyclerView mRecyclerView;

    private int mSelectedPosition = -1;

    public UtilityImagesAdapter(List<Uri> imageURIs, Context mContext)
    {
        this.imageURIs = imageURIs;
        this.mContext = mContext;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.utility_imageview, parent, false);
        return new ViewHolder(view, viewType);
    }

    @Override
    public int getItemViewType(int position)
    {
        return position < imageURIs.size() ? TYPE_ITEM : TYPE_ADD;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position)
    {
        final View itemView = holder.itemView;

        final ImageView imageView = (ImageView) itemView.findViewById(R.id.add_nade_imageview);
        final ImageView deleteIcon = (ImageView) itemView.findViewById(R.id.add_nade_image_delete);
        final View overlay = itemView.findViewById(R.id.add_nade_image_overlay);

        overlay.setVisibility(holder.getLayoutPosition() == mSelectedPosition ? View.VISIBLE : View.GONE);
        deleteIcon.setVisibility(holder.getLayoutPosition() == mSelectedPosition ? View.VISIBLE : View.GONE);

        if(holder.viewType == TYPE_ITEM)
        {
            imageView.setBackgroundColor(Color.TRANSPARENT);
            Glide.with(mContext)
                    .load(imageURIs.get(position))
                    .placeholder(R.drawable.ic_photo_white_24dp).listener(new RequestListener<Uri, GlideDrawable>()
            {
                @Override
                public boolean onException(Exception e, Uri model, Target<GlideDrawable> target, boolean isFirstResource)
                {
                    return false;
                }

                @Override
                public boolean onResourceReady(GlideDrawable resource, Uri model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource)
                {
                    imageView.setBackgroundColor(Color.WHITE);
                    return false;
                }
            })
                    .into(imageView);
            imageView.setPadding(0, 0, 0, 0);
        }
        else if(holder.viewType == TYPE_ADD)
        {
            imageView.setImageResource(R.drawable.ic_add_a_photo_white_24dp);
            int padding = mContext.getResources().getDimensionPixelSize(R.dimen.utility_imageview_add_photo_padding);
            imageView.setPadding(padding, padding, padding, padding);
            imageView.setBackgroundColor(Color.TRANSPARENT);
        }
        else
        {
            throw new IllegalArgumentException("holder.viewType cannot be: " + holder.viewType);
        }

    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView)
    {
        super.onAttachedToRecyclerView(recyclerView);
        mRecyclerView = recyclerView;
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView)
    {
        super.onDetachedFromRecyclerView(recyclerView);
        mRecyclerView = null;
    }

    @Override
    public int getItemCount()
    {
        return imageURIs.size() + 1;
    }

    private void clearPreviousSelection()
    {
        if(mSelectedPosition == -1)
            return;
        final RecyclerView.ViewHolder holder = mRecyclerView
                .findViewHolderForLayoutPosition(mSelectedPosition);
        if(holder == null)
        {
            notifyItemChanged(mSelectedPosition);
            return;
        }
        View previousView = holder.itemView;
        if(previousView == null)
            return;

        ImageView previousDeleteIcon = (ImageView) previousView.findViewById(R.id.add_nade_image_delete);
        View previousOverlay = previousView.findViewById(R.id.add_nade_image_overlay);

        previousOverlay.setVisibility(View.GONE);
        previousDeleteIcon.setVisibility(View.GONE);

        mSelectedPosition = -1;
    }

    public void setPhotoActionListener(PhotoActions photoActionListener)
    {
        mPhotoActionListener = photoActionListener;
    }

    private Transition getImageSelectionTransition()
    {
        final TransitionSet set = new TransitionSet();
        final Scale scale = new Scale(0.75f);
        scale.excludeTarget(R.id.add_nade_image_overlay, true);

        set.addTransition(scale);
        set.addTransition(new Fade());

        return set;
    }

    public interface PhotoActions
    {
        void onAddPhoto();

        void onRemovePhoto(int position);
    }

    public class ViewHolder extends GameServerAdapter.ViewHolder
    {
        int viewType;

        public ViewHolder(View content, int viewType)
        {
            super(content);
            this.viewType = viewType;
            if(viewType == TYPE_ITEM)
            {
                itemView.setOnClickListener(v ->
                {
                    ImageView deleteIcon = (ImageView) v.findViewById(R.id.add_nade_image_delete);
                    View overlay = v.findViewById(R.id.add_nade_image_overlay);

                    TransitionManager.beginDelayedTransition(mRecyclerView, getImageSelectionTransition());

                    final int position = getLayoutPosition();
                    if(mSelectedPosition != -1 && mSelectedPosition != position)
                    {
                        clearPreviousSelection();

                        overlay.setVisibility(View.VISIBLE);
                        deleteIcon.setVisibility(View.VISIBLE);

                        mSelectedPosition = position;
                    }
                    else if(mSelectedPosition == position)
                    {
                        overlay.setVisibility(View.GONE);
                        deleteIcon.setVisibility(View.GONE);

                        mSelectedPosition = -1;
                    }
                    else
                    {
                        overlay.setVisibility(View.VISIBLE);
                        deleteIcon.setVisibility(View.VISIBLE);

                        mSelectedPosition = position;
                    }

                });
                final ImageView deleteIcon = (ImageView) itemView.findViewById(R.id.add_nade_image_delete);
                deleteIcon.setOnClickListener(v ->
                {
                    clearPreviousSelection();
                    mPhotoActionListener.onRemovePhoto(getLayoutPosition());
                });
            }
            else if(viewType == TYPE_ADD)
            {
                content.setOnClickListener(v ->
                {
                    TransitionManager.beginDelayedTransition(mRecyclerView, getImageSelectionTransition());

                    clearPreviousSelection();
                    mPhotoActionListener.onAddPhoto();
                });
            }
            else
            {
                throw new IllegalArgumentException("holder.viewType cannot be: " + viewType);
            }
        }
    }
}
