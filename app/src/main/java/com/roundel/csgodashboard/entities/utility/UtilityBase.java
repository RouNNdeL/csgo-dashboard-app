package com.roundel.csgodashboard.entities.utility;

import android.net.Uri;

import com.roundel.csgodashboard.entities.Map;

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

    private Map map;
    private String title;
    private String description;
    //</editor-fold>

    public UtilityBase(List<Uri> imageUris, Tags tags, Map map, String title, String description)
    {
        this.imageUris = imageUris;
        this.tags = tags;
        this.map = map;
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

    public Map getMap()
    {
        return map;
    }

    public void setMap(Map map)
    {
        map = map;
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
