package com.roundel.csgodashboard.net;

import android.os.Build;

import com.roundel.csgodashboard.entities.GameServer;
import com.roundel.csgodashboard.util.LogHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.charset.Charset;
import java.util.Objects;

/**
 * Created by Krzysiek on 2017-01-31.
 */

public class ServerConnectionThread extends Thread implements Runnable
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

    private int connectionTimeout = 5000;
    private int receiveTimeout = 5000;
    private int userResponseTimeout = 90000;

    private ServerConnectionListener connectionListener;
    private OnStartGameInfoServerListener startServerListener;

    private int gameListeningPort = -1;
    //</editor-fold>


    /**
     * @param gameServer A game server to communicate with
     */
    public ServerConnectionThread(GameServer gameServer, OnStartGameInfoServerListener startServerListener)
    {
        this.gameServer = gameServer;
        this.startServerListener = startServerListener;
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
            sendBytes(gameServerSocket, json.toString().getBytes(Charset.defaultCharset()));
            LogHelper.i(TAG, "Sending " + json.toString());

            JSONObject response = jsonFromByteArr(receiveBytes(gameServerSocket));
            LogHelper.i(TAG, "Received: " + response.toString());

            if(Objects.equals(response.getString("code"), CONNECTION_RESPONSE))
            {
                gameServerSocket.setSoTimeout(userResponseTimeout);

                json = new JSONObject();
                json.put("code", CONNECTION_DEVICE_NAME);
                json.put("device_name", Build.MANUFACTURER + " " + Build.MODEL);

                sendBytes(gameServerSocket, json.toString().getBytes(Charset.defaultCharset()));
                LogHelper.i(TAG, "Sending " + json.toString());

                if(connectionListener != null)
                    connectionListener.onAllowConnection();

                response = jsonFromByteArr(receiveBytes(gameServerSocket));
                LogHelper.i(TAG, "Received: " + response.toString());

                if(Objects.equals(response.getString("code"), CONNECTION_USER_AGREEMENT))
                {
                    if(response.getBoolean("user_allowed"))
                    {
                        gameListeningPort = startServerListener.onStartGameInfoServer();

                        gameServerSocket.setSoTimeout(receiveTimeout);

                        json = new JSONObject();
                        json.put("code", CONNECTION_GAME_INFO_PORT);
                        json.put("game_info_port", gameListeningPort);

                        sendBytes(gameServerSocket, json.toString().getBytes());
                        LogHelper.i(TAG, "Sending " + json.toString());

                        response = jsonFromByteArr(receiveBytes(gameServerSocket));
                        LogHelper.i(TAG, "Received: " + response.toString());

                        if(Objects.equals(response.getString("code"), CONNECTION_GAME_INFO_PORT_RESPONSE))
                        {
                            if(connectionListener != null)
                                connectionListener.onAccessGranted(gameServer);
                            LogHelper.i(TAG, "Access granted");
                            gameServerSocket.close();
                        }
                        else
                        {
                            if(connectionListener != null)
                                connectionListener.onServerNotResponded("Didn't send CSGO_DASHBOARD_CONNECTION_GAME_INFO_PORT_RESPONSE");
                            gameServerSocket.close();
                        }
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

    /**
     * @param socket socket to send the bytes on
     * @param bytes  bytes to send
     *
     * @throws IOException
     */
    private void sendBytes(Socket socket, byte[] bytes) throws IOException
    {
        sendBytes(socket, bytes, 0, bytes.length);
    }


    /**
     * @param socket      socket to send the bytes to
     * @param myByteArray bytes to send
     * @param start       offset for the bytes
     * @param len         length of the message
     *
     * @throws IOException
     * @throws IllegalArgumentException  when length is negative
     * @throws IndexOutOfBoundsException when start is negative or exceeds the array
     */
    private void sendBytes(Socket socket, byte[] myByteArray, int start, int len) throws IOException
    {
        if(len < 0)
            throw new IllegalArgumentException("Negative length not allowed");
        if(start < 0 || start >= myByteArray.length)
            throw new IndexOutOfBoundsException("Out of bounds: " + start);
        // Other checks if needed.

        // May be better to save the streams in the support class;
        // just like the socket variable.
        OutputStream out = socket.getOutputStream();
        DataOutputStream dos = new DataOutputStream(out);

        //dos.writeInt(len);
        if(len > 0)
        {
            dos.write(myByteArray);
        }
        dos.flush();
    }

    private byte[] receiveBytes(Socket socket) throws IOException
    {
        InputStream inputStream = socket.getInputStream();
        DataInputStream dataInputStream = new DataInputStream(inputStream);
        final byte[] b = new byte[1024];
        dataInputStream.read(b);
        return b;
    }

    /**
     * @param array array of bytes to be parsed
     *
     * @return parsed {@link JSONObject}
     * @throws JSONException
     */
    private JSONObject jsonFromByteArr(byte[] array) throws JSONException
    {
        return new JSONObject(new String(array, Charset.defaultCharset()).trim());
    }

    public void setConnectionListener(ServerConnectionListener listener)
    {
        this.connectionListener = listener;
    }

    public void setConnectionTimeout(int connectionTimeout)
    {
        this.connectionTimeout = connectionTimeout;
    }

    public void setReceiveTimeout(int receiveTimeout)
    {
        this.receiveTimeout = receiveTimeout;
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
