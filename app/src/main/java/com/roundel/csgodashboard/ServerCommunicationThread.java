package com.roundel.csgodashboard;

import com.roundel.csgodashboard.entities.GameServer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Created by Krzysiek on 2017-01-31.
 */
public class ServerCommunicationThread extends Thread implements Runnable
{
    private static final String TAG = ServerCommunicationThread.class.getSimpleName();

    private GameServer gameServer;
    private Socket gameServerSocket;
    private String data;
    private ServerCommunication listener;

    public ServerCommunicationThread(GameServer gameServer, String data)
    {
        this.gameServer = gameServer;
        this.data = data;
    }

    public void setServerCommunication(ServerCommunication listener)
    {
        this.listener = listener;
    }

    @Override
    public void run()
    {
        try
        {
            gameServerSocket = new Socket(gameServer.getHost(), gameServer.getPort());
            sendBytes(data.getBytes());
            byte[] response = receiveBytes();
            if(listener != null)
                listener.onReceive(response);
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }

    private void sendBytes(byte[] myByteArray) throws IOException
    {
        sendBytes(myByteArray, 0, myByteArray.length);
    }

    private void sendBytes(byte[] myByteArray, int start, int len) throws IOException
    {
        if(len < 0)
            throw new IllegalArgumentException("Negative length not allowed");
        if(start < 0 || start >= myByteArray.length)
            throw new IndexOutOfBoundsException("Out of bounds: " + start);
        // Other checks if needed.

        // May be better to save the streams in the support class;
        // just like the socket variable.
        OutputStream out = gameServerSocket.getOutputStream();
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

    public interface ServerCommunication
    {
        void onReceive(byte[] response);
    }
}
