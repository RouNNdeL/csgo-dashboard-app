package com.roundel.csgodashboard.net;

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
import java.nio.charset.Charset;

/**
 * Created by Krzysiek on 2017-02-08.
 */
public class ServerPingingThread extends Thread implements Runnable
{
    private static final String PING_REQUEST = "CSGO_DASHBOARD_PING_REQUEST";
    private static final String PING_RESPONSE = "CSGO_DASHBOARD_PING_RESPONSE";
    private static final String TAG = ServerPingingThread.class.getSimpleName();
    private GameServer gameServer;
    private ServerStatusListener listener;

    private Socket gameServerSocket;

    private int connectionTimeout = 5000;
    private int receiveTimeout = 5000;

    public ServerPingingThread(GameServer gameServer)
    {
        this.gameServer = gameServer;
    }

    @Override
    public void run()
    {
        gameServerSocket = new Socket();
        try
        {
            gameServerSocket.connect(new InetSocketAddress(gameServer.getHost(), gameServer.getPort()), connectionTimeout);

            JSONObject json = new JSONObject();
            json.put("code", PING_REQUEST);

            LogHelper.i(TAG, "Sending ping request: " + json.toString());
            sendBytes(gameServerSocket, json.toString().getBytes());

            JSONObject response = jsonFromByteArr(receiveBytes(gameServerSocket));

            LogHelper.i(TAG, "Got response: " + response.toString());

            if(response.get("code") == PING_RESPONSE)
                listener.onConnected();
            else
                listener.onDisconnected();
        }
        catch(IOException | JSONException e)
        {
            listener.onDisconnected();
            LogHelper.e(TAG, e.toString());
            e.printStackTrace();
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

    public void setConnectionTimeout(int connectionTimeout)
    {
        this.connectionTimeout = connectionTimeout;
    }

    public void setReceiveTimeout(int receiveTimeout)
    {
        this.receiveTimeout = receiveTimeout;
    }

    public interface ServerStatusListener
    {
        void onDisconnected();

        void onConnected();
    }
}
