package com.roundel.csgodashboard.entities.utility;

import android.net.Uri;

import java.util.List;

/**
 * Created by Krzysiek on 2017-02-10.
 */
public class UtilityBase
{
    private static final String TAG = UtilityBase.class.getSimpleName();

    //<editor-fold desc="private variables">
    private List<Uri> imageUris;
    private Tags tags;

    private int mapId;
    private String title;
    private String description;
    //</editor-fold>

    public UtilityBase(List<Uri> imageUris, Tags tags, int mapId, String title, String description)
    {
        this.imageUris = imageUris;
        this.tags = tags;
        this.mapId = mapId;
        this.title = title;
        this.description = description;
    }

    public List<Uri> getImageUris()
    {
        return imageUris;
    }

    public void setImageUris(List<Uri> imageUris)
    {
        this.imageUris = imageUris;
    }

    public Tags getTags()
    {
        return tags;
    }

    public void setTags(Tags tags)
    {
        this.tags = tags;
    }

    public int getMapId()
    {
        return mapId;
    }

    public void setMapId(int mapId)
    {
        this.mapId = mapId;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }
}
