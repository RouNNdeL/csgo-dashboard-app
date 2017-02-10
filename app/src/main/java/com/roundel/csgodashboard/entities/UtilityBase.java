package com.roundel.csgodashboard.entities;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Krzysiek on 2017-02-10.
 */
public class UtilityBase
{
    private static final String TAG = UtilityBase.class.getSimpleName();

    public static final String ASSETS_BASE = "file:///android_asset/";

    private List<URI> imageUris = new ArrayList<>();
    private List<String> tags = new ArrayList<>();

    private Map map;
    private String title;
    private String description;

    public UtilityBase(List<URI> imageUris, List<String> tags, Map map, String title, String description)
    {
        this.imageUris = imageUris;
        this.tags = tags;
        this.map = map;
        this.title = title;
        this.description = description;
    }

    public List<URI> getImageUris()
    {
        return imageUris;
    }

    public void setImageUris(List<URI> imageUris)
    {
        this.imageUris = imageUris;
    }

    public List<String> getTags()
    {
        return tags;
    }

    public void setTags(List<String> tags)
    {
        this.tags = tags;
    }

    public Map getMap()
    {
        return map;
    }

    public void setMap(Map map)
    {
        this.map = map;
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
