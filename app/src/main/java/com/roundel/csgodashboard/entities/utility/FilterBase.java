package com.roundel.csgodashboard.entities.utility;

import java.util.HashSet;

/**
 * Created by Krzysiek on 2017-02-24.
 */
public class FilterBase
{
    private static final String TAG = FilterBase.class.getSimpleName();

    //<editor-fold desc="private variables">
    private HashSet<Long> tagIds;
    private Long mapId;
    private String searchQuery;
    //</editor-fold>


    public HashSet<Long> getTagIds()
    {
        return tagIds;
    }

    public void setTagIds(HashSet<Long> tagIds)
    {
        this.tagIds = tagIds;
    }

    public Long getMapId()
    {
        return mapId;
    }

    public void setMapId(Long mapId)
    {
        this.mapId = mapId;
    }

    public String getSearchQuery()
    {
        return searchQuery;
    }

    public void setSearchQuery(String searchQuery)
    {
        this.searchQuery = searchQuery;
    }
}
