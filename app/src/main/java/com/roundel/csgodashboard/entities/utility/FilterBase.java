package com.roundel.csgodashboard.entities.utility;

import java.util.List;

/**
 * Created by Krzysiek on 2017-02-24.
 */
public class FilterBase
{
    private static final String TAG = FilterBase.class.getSimpleName();

    //<editor-fold desc="private variables">
    private List<String> tags;
    private Integer mapId;
    private String title;
    //</editor-fold>


    public List<String> getTags()
    {
        return tags;
    }

    public void setTags(List<String> tags)
    {
        this.tags = tags;
    }

    public Integer getMapId()
    {
        return mapId;
    }

    public void setMapId(Integer mapId)
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
}
