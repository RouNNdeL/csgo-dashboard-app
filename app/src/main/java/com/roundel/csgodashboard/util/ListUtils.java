package com.roundel.csgodashboard.util;

import android.net.Uri;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Krzysiek on 2017-03-26.
 */

public class ListUtils
{
    public static final String DEFAULT_DELIMITER = ";";

    public static <T> String join(Collection<T> collection)
    {
        return join(collection, DEFAULT_DELIMITER);
    }

    public static <T> String join(Collection<T> collection, CharSequence delimiter)
    {
        StringBuilder sb = new StringBuilder();
        Iterator<T> it = collection.iterator();
        if(it.hasNext())
        {
            sb.append(it.next().toString());
            while(it.hasNext())
            {
                sb.append(delimiter).append(it.next().toString());
            }
        }
        return sb.toString();
    }

    public static List<Uri> splitUris(String string)
    {
        return splitUris(string, DEFAULT_DELIMITER);
    }

    public static List<Uri> splitUris(String string, String delimiter)
    {
        if(string == null)
            return null;
        if(delimiter == null)
            delimiter = DEFAULT_DELIMITER;
        List<Uri> list = new ArrayList<>();
        String[] strings = string.split(delimiter);
        for(String s : strings)
        {
            list.add(Uri.parse(s));
        }
        return list;
    }
}
