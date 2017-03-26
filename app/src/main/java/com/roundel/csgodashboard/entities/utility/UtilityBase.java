package com.roundel.csgodashboard.entities.utility;

import android.content.Context;
import android.net.Uri;
import android.os.Build;

import com.roundel.csgodashboard.entities.Map;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Krzysiek on 2017-02-10.
 */
public class UtilityBase
{
    private static final String TAG = UtilityBase.class.getSimpleName();

    //<editor-fold desc="private variables">
    private List<String> imageIds;
    private Tags tags;

    private Map map;
    private String title;
    private String description;
    //</editor-fold>

    public UtilityBase(List<String> imageIds, Tags tags, Map map, String title, String description)
    {
        this.imageIds = imageIds;
        this.tags = tags;
        this.map = map;
        this.title = title;
        this.description = description;
    }

    public List<String> getImageIds()
    {
        return imageIds;
    }

    public void setImageIds(List<String> imageUris)
    {
        this.imageIds = imageUris;
    }

    public List<Uri> getImgUris(Context context)
    {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
        {
            return imageIds.stream().map(id -> Uri.parse("file://" + Utilities.getImgPath(context) + id)).collect(Collectors.toList());
        }
        else
        {
            List<Uri> list = new ArrayList<>();
            //noinspection Convert2streamapi
            for(String id : imageIds)
            {
                list.add(Uri.parse("file://" + Utilities.getImgPath(context) + id));
            }
            return list;
        }
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
