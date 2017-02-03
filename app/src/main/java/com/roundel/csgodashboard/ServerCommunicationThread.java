package com.roundel.csgodashboard;

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
import java.util.Objects;

/**
 * Created by Krzysiek on 2017-01-31.
 */
public class ServerCommunicationThread extends Thread implements Runnable
{
    public static final int MODE_CONNECT = 0;

    private static final String CONNECTION_DEVICE_NAME = "CSGO_DASHBOARD_CONNECTION_DEVICE_NAME";                                   //JSON params: "device_name"
    private static final String CONNECTION_GAME_INFO_PORT = "CSGO_DASHBOARD_CONNECTION_GAME_INFO_PORT";                             //JSON params: "game_info_port"
    private static final String CONNECTION_GAME_INFO_PORT_RESPONSE = "CSGO_DASHBOARD_CONNECTION_GAME_INFO_PORT_RESPONSE";           //JSON params: none
    private static final String CONNECTION_REQUEST = "CSGO_DASHBOARD_CONNECTION_REQUEST";                                           //json params: none
    private static final String CONNECTION_RESPONSE = "CSGO_DASHBOARD_CONNECTION_RESPONSE";                                         //JSON params: none
    private static final String CONNECTION_USER_AGREEMENT = "CSGO_DASHBOARD_CONNECTION_USER_AGREEMENT";                             //JSON params: "user_allowed"

    private static final String TAG = ServerCommunicationThread.class.getSimpleName();

    private GameServer gameServer;
    private Socket gameServerSocket;
    private int communicationMode;
    private int connectionTimeout = 5000;
    private int receiveTimeout = 15000;

    private ServerConnectionListener connectionListener;

    private int gameListeningPort = -1;


    /**
     * @param gameServer        A game server to communicate with
     * @param communicationMode A communication protocol Has to be {@link #MODE_CONNECT}
     * @param args              Arguments depend on the {@param communicationMode}
     *
     * @throws IllegalArgumentException if the {@param communicationMode} is invalid or arguments
     *                                  don't match the mode
     */
    public ServerCommunicationThread(GameServer gameServer, int communicationMode, String... args)
    {
        this.gameServer = gameServer;
        this.communicationMode = communicationMode;
        switch(communicationMode)
        {
            case MODE_CONNECT:
                if(args.length < 1)
                    throw new IllegalArgumentException("You have to supply a port that the app is going to send game info to, when using MODE_CONNECT.");
                gameListeningPort = Integer.valueOf(args[0]);
                break;
            default:
                throw new IllegalArgumentException("Mode has to be one of [MODE_CONNECT]");
        }
    }

    @Override
    public void run()
    {
        switch(communicationMode)
        {
            case MODE_CONNECT:
            {
                try
                {
                    gameServerSocket = new Socket();
                    gameServerSocket.connect(new InetSocketAddress(gameServer.getHost(), gameServer.getPort()), connectionTimeout);
                    gameServerSocket.setSoTimeout(receiveTimeout);
                    JSONObject json = new JSONObject();
                    json.put("code", CONNECTION_REQUEST);
                    sendBytes(gameServerSocket, json.toString().getBytes());
                    LogHelper.i(TAG, "Sending " + json.toString());

                    JSONObject response = jsonFromByteArr(receiveBytes(gameServerSocket));
                    LogHelper.i(TAG, "Received: " + response.toString());

                    if(Objects.equals(response.getString("code"), CONNECTION_RESPONSE))
                    {
                        json = new JSONObject();
                        json.put("code", CONNECTION_DEVICE_NAME);
                        json.put("device_name", Build.MANUFACTURER + " " + Build.MODEL);

                        sendBytes(gameServerSocket, json.toString().getBytes());
                        LogHelper.i(TAG, "Sending " + json.toString());

                        if(connectionListener != null)
                            connectionListener.onAllowConnection();

                        response = jsonFromByteArr(receiveBytes(gameServerSocket));
                        LogHelper.i(TAG, "Received: " + response.toString());

                        if(Objects.equals(response.getString("code"), CONNECTION_USER_AGREEMENT))
                        {
                            if(response.getBoolean("user_allowed"))
                            {
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
                                        connectionListener.onAccessGranted();
                                    LogHelper.i(TAG, "Access granted");
                                }
                                else
                                {
                                    if(connectionListener != null)
                                        connectionListener.onServerNotResponded("Didn't send CSGO_DASHBOARD_CONNECTION_GAME_INFO_PORT_RESPONSE");
                                }
                            }
                            else if(!response.getBoolean("user_allowed"))
                            {
                                if(connectionListener != null)
                                    connectionListener.onAccessDenied();
                                LogHelper.i(TAG, "Access denied");
                            }
                        }
                        else
                        {
                            if(connectionListener != null)
                                connectionListener.onServerNotResponded("Didn't send CSGO_DASHBOARD_CONNECTION_USER_AGREEMENT");
                            LogHelper.e(TAG, "Didn't send CSGO_DASHBOARD_CONNECTION_USER_AGREEMENT");
                        }
                    }
                    else
                    {
                        if(connectionListener != null)
                            connectionListener.onServerNotResponded("Didn't send CSGO_DASHBOARD_CONNECTION_RESPONSE");
                        LogHelper.e(TAG, "Didn't send CSGO_DASHBOARD_CONNECTION_RESPONSE");
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
                }
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
        return new JSONObject(new String(array).trim());
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

    public interface ServerConnectionListener
    {
        /**
         * Called when user allows for the connection
         */
        void onAccessGranted();

        /**
         * Called when user denies the connection
         */
        void onAccessDenied();

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
