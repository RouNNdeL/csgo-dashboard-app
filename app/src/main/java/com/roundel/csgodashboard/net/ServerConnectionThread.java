package com.roundel.csgodashboard.net;

import android.os.Build;

import com.roundel.csgodashboard.entities.GameServer;
import com.roundel.csgodashboard.util.LogHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Objects;

/**
 * Created by Krzysiek on 2017-01-31.
 */

public class ServerConnectionThread extends ServerCommunicationThreadBase
{
    private static final String TAG = ServerConnectionThread.class.getSimpleName();
    public static final int MODE_CONNECT = 0;
    public static final int MODE_PING = 1;
    private static final String CONNECTION_DEVICE_NAME = "CSGO_DASHBOARD_CONNECTION_DEVICE_NAME";                                   //JSON params: "device_name"
    private static final String CONNECTION_GAME_INFO_PORT = "CSGO_DASHBOARD_CONNECTION_GAME_INFO_PORT";                             //JSON params: "game_info_port"
    private static final String CONNECTION_GAME_INFO_PORT_RESPONSE = "CSGO_DASHBOARD_CONNECTION_GAME_INFO_PORT_RESPONSE";           //JSON params: none
    private static final String CONNECTION_REQUEST = "CSGO_DASHBOARD_CONNECTION_REQUEST";                                           //json params: none
    private static final String CONNECTION_RESPONSE = "CSGO_DASHBOARD_CONNECTION_RESPONSE";                                         //JSON params: none
    private static final String CONNECTION_USER_AGREEMENT = "CSGO_DASHBOARD_CONNECTION_USER_AGREEMENT";                             //JSON params: "user_allowed"
    //<editor-fold desc="private variables">
    private GameServer gameServer;
    private Socket gameServerSocket;

    private int userResponseTimeout = 90000;

    private ServerConnectionListener connectionListener;
    //</editor-fold>


    /**
     * @param gameServer A game server to communicate with
     */
    public ServerConnectionThread(GameServer gameServer)
    {
        this.gameServer = gameServer;
    }

    @Override
    public void run()
    {
        try
        {
            gameServerSocket = new Socket();
            gameServerSocket.connect(new InetSocketAddress(gameServer.getHost(), gameServer.getPort()), connectionTimeout);
            gameServerSocket.setSoTimeout(receiveTimeout);
            JSONObject json = new JSONObject();
            json.put("code", CONNECTION_REQUEST);
            sendJSON(gameServerSocket, json);

            JSONObject response = receiveJSON(gameServerSocket);

            if(Objects.equals(response.getString("code"), CONNECTION_RESPONSE))
            {
                gameServerSocket.setSoTimeout(userResponseTimeout);

                json = new JSONObject();
                json.put("code", CONNECTION_DEVICE_NAME);
                json.put("device_name", Build.MANUFACTURER + " " + Build.MODEL);

                sendJSON(gameServerSocket, json);

                if(connectionListener != null)
                    connectionListener.onAllowConnection();

                response = receiveJSON(gameServerSocket);

                if(Objects.equals(response.getString("code"), CONNECTION_USER_AGREEMENT))
                {
                    if(response.getBoolean("user_allowed"))
                    {
                        if(connectionListener != null)
                            connectionListener.onAccessGranted(gameServer);
                        LogHelper.i(TAG, "Access granted");
                        gameServerSocket.close();
                    }
                    else if(!response.getBoolean("user_allowed"))
                    {
                        if(connectionListener != null)
                            connectionListener.onAccessDenied(gameServer);
                        LogHelper.i(TAG, "Access denied");
                        gameServerSocket.close();
                    }
                }
                else
                {
                    if(connectionListener != null)
                        connectionListener.onServerNotResponded("Didn't send CSGO_DASHBOARD_CONNECTION_USER_AGREEMENT");
                    LogHelper.e(TAG, "Didn't send CSGO_DASHBOARD_CONNECTION_USER_AGREEMENT");
                    gameServerSocket.close();
                }
            }
            else
            {
                if(connectionListener != null)
                    connectionListener.onServerNotResponded("Didn't send CSGO_DASHBOARD_CONNECTION_RESPONSE");
                LogHelper.e(TAG, "Didn't send CSGO_DASHBOARD_CONNECTION_RESPONSE");
                gameServerSocket.close();
            }
        }
        catch(SocketTimeoutException e)
        {
            if(connectionListener != null)
                connectionListener.onServerTimedOut();
            LogHelper.e(TAG, e.toString());
        }
        catch(IOException | JSONException e)
        {
            if(connectionListener != null)
                connectionListener.onServerNotResponded("Didn't send a proper JSON");
            LogHelper.e(TAG, e.toString());
            e.printStackTrace();

            try
            {
                gameServerSocket.close();
            }
            catch(IOException e1)
            {
                e1.printStackTrace();
            }
        }
    }


    public void setConnectionListener(ServerConnectionListener listener)
    {
        this.connectionListener = listener;
    }

    public interface OnStartGameInfoServerListener
    {
        /**
         * @return a port that the server has started on
         */
        int onStartGameInfoServer();
    }

    public interface ServerConnectionListener
    {
        /**
         * Called when user allows for the connection
         */
        void onAccessGranted(GameServer server);

        /**
         * Called when user denies the connection
         */
        void onAccessDenied(GameServer server);

        /**
         * Called when server response was not understood
         *
         * @param error cause of this call
         */
        void onServerNotResponded(String error);

        /**
         * Called when the socket times-out
         */
        void onServerTimedOut();

        /**
         * Called when the socket is awaiting for user action on the PC
         */
        void onAllowConnection();
    }
}
