package com.roundel.csgodashboard.entities;

import java.util.Locale;

/**
 * Created by Krzysiek on 2017-01-23.
 */
public class GameServer
{
    public static final String TAG = GameServer.class.getSimpleName();

    private String name;
    private String host;
    private int port;

    public GameServer(String name, String host, int port)
    {
        this.name = name;
        this.host = host;
        this.port = port;
    }

    public String getName()
    {
        return name;
    }

    public String getHost()
    {
        return host;
    }

    public int getPort()
    {
        return port;
    }

    @Override
    public String toString()
    {
        return String.format(Locale.getDefault(), "%s - %s:%d", name, host, port);
    }
}
