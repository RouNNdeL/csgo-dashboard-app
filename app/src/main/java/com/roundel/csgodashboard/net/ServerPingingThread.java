package com.roundel.csgodashboard.net;

import com.roundel.csgodashboard.entities.GameServer;
import com.roundel.csgodashboard.util.LogHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;

/**
 * Created by Krzysiek on 2017-02-08.
 */
public class ServerPingingThread extends ServerCommunicationThreadBase
{
    private static final String TAG = ServerPingingThread.class.getSimpleName();
    private static final String PING_REQUEST = "CSGO_DASHBOARD_PING_REQUEST";       //JSON params: none
    private static final String PING_RESPONSE = "CSGO_DASHBOARD_PING_RESPONSE";     //JSON params: none
    //<editor-fold desc="private variables">
    private GameServer gameServer;
    private ServerStatusListener listener;

    private Socket gameServerSocket;
    //</editor-fold>

    public ServerPingingThread(GameServer gameServer)
    {
        this.gameServer = gameServer;
    }

    @Override
    public void run()
    {
        LogHelper.d(TAG, "Starting the thread");
        try
        {
            gameServerSocket = new Socket();
            gameServerSocket.connect(new InetSocketAddress(gameServer.getHost(), gameServer.getPort()), connectionTimeout);
            gameServerSocket.setSoTimeout(receiveTimeout);

            JSONObject json = new JSONObject();
            json.put("code", PING_REQUEST);

            LogHelper.i(TAG, "Sending ping request: " + json.toString());
            sendJSON(gameServerSocket, json);

            JSONObject response = receiveJSON(gameServerSocket);

            LogHelper.i(TAG, "Got response: " + response.toString());

            if(listener != null)
            {
                if(response.get("code") == PING_RESPONSE)
                    listener.onConnected();
                else
                    listener.onDisconnected();
            }
            gameServerSocket.close();
        }
        catch(SocketTimeoutException e)
        {
            if(listener != null)
                listener.onDisconnected();
            LogHelper.e(TAG, e.toString());
            try
            {
                gameServerSocket.close();
            }
            catch(IOException ignored)
            {

            }
        }
        catch(IOException | JSONException e)
        {
            if(listener != null)
                listener.onDisconnected();
            LogHelper.e(TAG, e.toString());
            e.printStackTrace();
            try
            {
                gameServerSocket.close();
            }
            catch(IOException ignored)
            {

            }
        }
    }

    public interface ServerStatusListener
    {
        void onDisconnected();

        void onConnected();
    }
}
