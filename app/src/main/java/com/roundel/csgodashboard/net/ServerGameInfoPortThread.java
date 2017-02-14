package com.roundel.csgodashboard.net;

import com.roundel.csgodashboard.entities.GameServer;
import com.roundel.csgodashboard.util.LogHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Objects;

/**
 * Created by Krzysiek on 2017-02-12.
 */
public class ServerGameInfoPortThread extends ServerCommunicationThreadBase
{
    private static final String TAG = ServerGameInfoPortThread.class.getSimpleName();

    private static final String BEGIN_REQUEST = "CSGO_DASHBOARD_PORT_BEGIN_REQUEST";
    private static final String BEGIN_RESPONSE = "CSGO_DASHBOARD_PORT_BEGIN_RESPONSE";
    private static final String DATA_REQUEST = "CSGO_DASHBOARD_PORT_DATA_REQUEST";
    private static final String DATA_RESPONSE = "CSGO_DASHBOARD_PORT_DATA_RESPONSE";

    //<editor-fold desc="private variables">
    private GameServer gameServer;
    private int port;
    //</editor-fold>

    public ServerGameInfoPortThread(GameServer gameServer, int port)
    {
        this.gameServer = gameServer;
        this.port = port;
    }

    @Override
    public void run()
    {
        try
        {
            gameServerSocket = new Socket();
            gameServerSocket.connect(new InetSocketAddress(gameServer.getHost(), gameServer.getPort()), connectionTimeout);
            gameServerSocket.setSoTimeout(receiveTimeout);

            JSONObject request = new JSONObject();
            request.put("code", BEGIN_REQUEST);

            sendJSON(gameServerSocket, request);

            JSONObject response = jsonFromByteArr(receiveBytes(gameServerSocket));
            if(Objects.equals(response.getString("code"), BEGIN_RESPONSE))
            {
                request = new JSONObject();
                request.put("code", DATA_REQUEST);
                request.put("game_info_port", port);

                sendJSON(gameServerSocket, request);

                response = jsonFromByteArr(receiveBytes(gameServerSocket));
                if(Objects.equals(response.getString("code"), DATA_RESPONSE))
                {
                    LogHelper.i(TAG, "Successfully received " + DATA_RESPONSE);
                }
            }
        }
        catch(IOException | JSONException e)
        {
            e.printStackTrace();
        }
    }
}
