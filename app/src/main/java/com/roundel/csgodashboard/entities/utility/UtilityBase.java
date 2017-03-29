package com.roundel.csgodashboard.entities.utility;

import android.content.Context;
import android.net.Uri;
import android.os.Build;

import com.roundel.csgodashboard.entities.Map;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Krzysiek on 2017-02-10.
 */
public class UtilityBase
{
    private static final String TAG = UtilityBase.class.getSimpleName();

    //<editor-fold desc="private variables">
    private ArrayList<String> imageIds;
    private Tags tags;

    private Map map;
    private String title;
    private String description;
    //</editor-fold>

    public UtilityBase(ArrayList<String> imageIds, Tags tags, Map map, String title, String description)
    {
        this.imageIds = imageIds;
        this.tags = tags;
        this.map = map;
        this.title = title;
        this.description = description;
    }

    @Override
    public boolean equals(Object o)
    {
        if(this == o) return true;
        if(!(o instanceof UtilityBase)) return false;

        UtilityBase that = (UtilityBase) o;

        if(!tags.equals(that.tags)) return false;
        if(!map.equals(that.map)) return false;
        if(!title.equals(that.title)) return false;
        return description.equals(that.description);

    }

    @Override
    public int hashCode()
    {
        int result = imageIds.hashCode();
        result = 31 * result + tags.hashCode();
        result = 31 * result + map.hashCode();
        result = 31 * result + title.hashCode();
        result = 31 * result + description.hashCode();
        return result;
    }

    public List<String> getImageIds()
    {
        return imageIds;
    }

    public void setImageIds(ArrayList<String> imageIds)
    {
        this.imageIds = imageIds;
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

    private LinkedHashMap<String, String> getImageMap(Context context)
    {
        LinkedHashMap<String, String> hashMap = new LinkedHashMap<>();

        for(String id : imageIds)
        {
            hashMap.put(Uri.parse("file://" + Utilities.getImgPath(context) + id).toString(), id);
        }

        return hashMap;
    }

    public boolean removeIdByUri(Uri uri, Context context)
    {
        return imageIds.remove(getImageMap(context).get(uri.toString()));
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
