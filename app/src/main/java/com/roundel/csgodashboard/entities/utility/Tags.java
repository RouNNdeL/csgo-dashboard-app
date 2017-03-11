package com.roundel.csgodashboard.entities.utility;

import android.os.Build;
import android.provider.BaseColumns;
import android.util.LongSparseArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Krzysiek on 2017-03-01.
 */
public class Tags extends ArrayList<Tags.Tag> implements BaseColumns
{
    private static final String TAG = Tags.class.getSimpleName();

    public static final String TABLE_NAME = "tags";

    public static final String COLUMN_NAME_NAME = "tag_name";

    public List<String> getNames()
    {
        List<String> names;
        if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N)
        {
            names = this.stream().map(Tag::getName).collect(Collectors.toList());
        }
        else
        {
            names = new ArrayList<>();
            //noinspection Convert2streamapi
            for(Tag tag : this)
            {
                names.add(tag.getName());
            }
        }
        return names;
    }

    public String[] getNamesArray()
    {
        final List<String> names = getNames();
        return names.toArray(new String[names.size()]);
    }

    public HashSet<Long> getIds()
    {
        HashSet<Long> ids;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
        {
            ids = this.stream().map(Tag::getId).collect(Collectors.toCollection(HashSet::new));
        }
        else
        {
            ids = new HashSet<>();
            //noinspection Convert2streamapi
            for(Tag tag : this)
            {
                ids.add(tag.getId());
            }
        }
        return ids;
    }

    public String[] getIdsArray()
    {
        HashSet<String> ids;

        ids = new HashSet<>();
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
        {
            ids.addAll(this.stream().map(tag -> String.valueOf(tag.getId())).collect(Collectors.toList()));
        }
        else
        {
            //noinspection Convert2streamapi
            for(Tag tag : this)
            {
                ids.add(String.valueOf(tag.getId()));
            }
        }

        return ids.toArray(new String[ids.size()]);
    }

    public LongSparseArray<String> asSparseArray()
    {
        LongSparseArray<String> array = new LongSparseArray<>();
        for(Tag tag : this)
        {
            array.append(tag.getId(), tag.getName());
        }
        return array;
    }

    public HashMap<Long, String> asHashMap()
    {
        HashMap<Long, String> map = new HashMap<>();
        for(Tag tag : this)
        {
            map.put(tag.getId(), tag.getName());
        }
        return map;
    }

    public static class Tag
    {
        //<editor-fold desc="private variables">
        private long id = -1;
        private String name;
        //</editor-fold>

        public Tag(long id, String name)
        {
            this.name = name;
            this.id = id;
        }

        public Tag(String name)
        {
            this.name = name;
        }

        public long getId()
        {
            return id;
        }

        public void setId(int id)
        {
            this.id = id;
        }

        public String getName()
        {
            return name;
        }

        public void setName(String name)
        {
            this.name = name;
        }
    }
}
