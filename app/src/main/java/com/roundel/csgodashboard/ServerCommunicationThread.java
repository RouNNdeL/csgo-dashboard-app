package com.roundel.csgodashboard;

import android.os.Build;

import com.roundel.csgodashboard.entities.GameServer;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Objects;

/**
 * Created by Krzysiek on 2017-01-31.
 */
public class ServerCommunicationThread extends Thread implements Runnable
{
    public static final int MODE_CONNECT = 0;

    private static final String TAG = ServerCommunicationThread.class.getSimpleName();

    private static final String CONNECTION_REQUEST = "CSGO_DASHBOARD_CONNECTION_REQUEST";
    private static final String CONNECTION_RESPONSE = "CSGO_DASHBOARD_CONNECTION_RESPONSE";
    private static final String CONNECTION_GRANTED = "CSGO_DASHBOARD_ACCESS_GRANTED";
    private static final String CONNECTION_DENIED = "CSGO_DASHBOARD_ACCESS_DENIED";

    private GameServer gameServer;
    private Socket gameServerSocket;
    private int communicationMode;

    private ServerConnectionListener connectionListener;

    private int gameListeningPort = -1;

    public ServerCommunicationThread(GameServer gameServer, int communicationMode, int... args)
    {
        this.gameServer = gameServer;
        this.communicationMode = communicationMode;
        switch(communicationMode)
        {
            case MODE_CONNECT:
                if(args.length < 1)
                    throw new IllegalArgumentException("You have to supply a port that the app is going to send game info to, when using MODE_CONNECT.");
                gameListeningPort = args[0];
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
                    gameServerSocket = new Socket(gameServer.getHost(), gameServer.getPort());
                    sendBytes(gameServerSocket, CONNECTION_REQUEST.getBytes());
                    byte[] response = receiveBytes();
                    if(Objects.equals(new String(response).trim(), CONNECTION_RESPONSE))
                    {
                        JSONObject json = new JSONObject();
                        json.put("device_name", Build.MANUFACTURER + " " + Build.MODEL);
                        json.put("game_port", gameListeningPort);
                        sendBytes(gameServerSocket, json.toString().getBytes());
                        response = receiveBytes();
                        if(Objects.equals(new String(response).trim(), CONNECTION_GRANTED) && connectionListener != null)
                        {
                            connectionListener.onAccessGranted();
                        }
                        else if(Objects.equals(new String(response).trim(), CONNECTION_DENIED) && connectionListener != null)
                        {
                            connectionListener.onAccessDenied();
                        }
                    }
                }
                catch(IOException | JSONException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    private void sendBytes(Socket socket, byte[] myByteArray) throws IOException
    {
        sendBytes(socket, myByteArray, 0, myByteArray.length);
    }

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

    private byte[] receiveBytes() throws IOException
    {
        InputStream inputStream = gameServerSocket.getInputStream();
        DataInputStream dataInputStream = new DataInputStream(inputStream);
        final byte[] b = new byte[1024];
        dataInputStream.read(b);
        return b;
    }

    public void setConnectionListener(ServerConnectionListener listener)
    {
        this.connectionListener = listener;
    }

    public interface ServerConnectionListener
    {
        void onAccessGranted();

        void onAccessDenied();
    }
}
