package com.roundel.csgodashboard.net;

import com.roundel.csgodashboard.entities.GameServer;
import com.roundel.csgodashboard.entities.RoundEvents;
import com.roundel.csgodashboard.util.LogHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Objects;

/**
 * Created by Krzysiek on 2017-02-12.
 */
public class ServerUpdateThread extends ServerCommunicationThreadBase
{
    private static final String TAG = ServerUpdateThread.class.getSimpleName();

    private static final String BEGIN_REQUEST = "CSGO_DASHBOARD_UPDATE_BEGIN_REQUEST";
    private static final String BEGIN_RESPONSE = "CSGO_DASHBOARD_UPDATE_BEGIN_RESPONSE";
    private static final String PORT_REQUEST = "CSGO_DASHBOARD_UPDATE_PORT_REQUEST";
    private static final String PORT_RESPONSE = "CSGO_DASHBOARD_UPDATE_PORT_RESPONSE";
    private static final String TIME_REQUEST = "CSGO_DASHBOARD_UPDATE_TIME_REQUEST";
    private static final String EVENT_REQUEST = "CSGO_DASHBOARD_UPDATE_EVENT_REQUEST";
    private static final String EVENT_RESPONSE = "CSGO_DASHBOARD_UPDATE_EVENT_RESPONSE";

    private static final String EVENT_BOMB_PLANTED = "BOMB_PLANT";
    private static final String EVENT_BOMB_DEFUSED = "BOMB_DEFUSe";
    private static final String EVENT_ROUND_STARTED = "ROUND_START";
    private static final String EVENT_ROUND_ENDED = "ROUND_END";
    private static final String EVENT_FREEZE_TIME_STARTED = "FREEZE_TIME_START";
    //<editor-fold desc="private variables">
    private GameServer mGameServer;
    private int mPort;

    private RoundEvents mRoundEventsListener;
    private OnOffsetDetermined mOffsetListener;
    //</editor-fold>

    public ServerUpdateThread(GameServer gameServer, int port)
    {
        this.mGameServer = gameServer;
        this.mPort = port;
    }

    @Override
    public void run()
    {
        try
        {
            gameServerSocket = new Socket();
            gameServerSocket.connect(new InetSocketAddress(mGameServer.getHost(), mGameServer.getPort()), connectionTimeout);
            gameServerSocket.setSoTimeout(receiveTimeout);

            JSONObject request = new JSONObject();
            request.put("code", BEGIN_REQUEST);

            sendJSON(gameServerSocket, request);

            JSONObject response = jsonFromByteArr(receiveBytes(gameServerSocket));
            if(Objects.equals(response.getString("code"), BEGIN_RESPONSE))
            {
                request = new JSONObject();
                request.put("code", PORT_REQUEST);
                request.put("game_info_port", mPort);

                sendJSON(gameServerSocket, request);

                response = receiveJSON(gameServerSocket);
                if(Objects.equals(response.getString("code"), PORT_RESPONSE))
                {
                    request = new JSONObject();
                    request.put("code", TIME_REQUEST);

                    sendJSON(gameServerSocket, request);

                    byte[] time = receiveBytes(gameServerSocket, 8);

                    ByteBuffer buffer = ByteBuffer.wrap(time).order(ByteOrder.LITTLE_ENDIAN);
                    long timeMillis = buffer.getLong();

                    long offset = System.currentTimeMillis() - timeMillis;

                    LogHelper.d(TAG, "Server offset: " + offset);

                    if(offset > System.currentTimeMillis())
                    {
                        LogHelper.d(TAG, "Offset is too large: " + offset);
                        throw new IllegalArgumentException("Offset must not be bigger than System.currentTimeMillis(): " + offset);
                    }
                    else
                    {
                        if(mOffsetListener != null)
                            mOffsetListener.onOffsetDetermined(offset);

                        request = new JSONObject();

                        request.put("code", EVENT_REQUEST);

                        sendJSON(gameServerSocket, request);

                        response = receiveJSON(gameServerSocket);

                        if(Objects.equals(response.getString("code"), EVENT_RESPONSE))
                        {
                            final long serverTimestamp = response.getLong("timestamp");
                            switch(response.getString("event"))
                            {
                                case EVENT_BOMB_PLANTED:
                                    if(mRoundEventsListener != null)
                                        mRoundEventsListener.onBombPlanted(serverTimestamp);
                                    break;

                                case EVENT_BOMB_DEFUSED:
                                    if(mRoundEventsListener != null)
                                        mRoundEventsListener.onBombDefused(serverTimestamp);
                                    break;

                                case EVENT_ROUND_STARTED:
                                    if(mRoundEventsListener != null)
                                        mRoundEventsListener.onRoundStart(serverTimestamp);
                                    break;

                                case EVENT_ROUND_ENDED:
                                    if(mRoundEventsListener != null)
                                        mRoundEventsListener.onRoundEnd(serverTimestamp);
                                    break;
                                case EVENT_FREEZE_TIME_STARTED:
                                    if(mRoundEventsListener != null)
                                        mRoundEventsListener.onFreezeTimeStart(serverTimestamp);
                                    break;
                            }
                        }
                    }
                }
            }
        }
        catch(IOException | JSONException e)
        {
            e.printStackTrace();
        }
    }

    public void setRoundEventListener(RoundEvents roundEventsListener)
    {
        this.mRoundEventsListener = roundEventsListener;
    }

    public void setOffsetListener(OnOffsetDetermined mOffsetListener)
    {
        this.mOffsetListener = mOffsetListener;
    }

    public interface OnOffsetDetermined
    {
        void onOffsetDetermined(long offset);
    }
}
