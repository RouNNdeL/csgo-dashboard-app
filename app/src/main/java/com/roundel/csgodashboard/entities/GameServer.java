package com.roundel.csgodashboard.entities;

/**
 * Created by Krzysiek on 2017-01-23.
 */
public class GameServer
{
    public static final String TAG = GameServer.class.getSimpleName();

    //<editor-fold desc="private variables">
    private String name;
    private String host;
    private int port;
//</editor-fold>

    public GameServer(String name, String host, int port)
    {
        this.name = name;
        this.host = host;
        this.port = port;
    }

    @Override
    public String toString()
    {
        return "GameServer{" +
                "name='" + name + '\'' +
                ", host='" + host + '\'' +
                ", port=" + port +
                '}';
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
}
