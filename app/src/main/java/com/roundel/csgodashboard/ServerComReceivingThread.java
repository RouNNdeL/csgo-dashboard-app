package com.roundel.csgodashboard;

import com.roundel.csgodashboard.entities.GameServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Krzysiek on 2017-01-31.
 */
public class ServerComReceivingThread extends Thread implements Runnable
{
    private static final String TAG = ServerComReceivingThread.class.getSimpleName();

    private Socket gameServerSocket;
    private ServerSocket receivingSocket;
    private GameServer gameServer;
    private ServerCommunicationListener listener;

    public ServerComReceivingThread(ServerCommunicationListener listener)
    {
        this.listener = listener;
    }

    @Override
    public void run()
    {
        try
        {
            receivingSocket = new ServerSocket();

            listener.onBind(receivingSocket.getLocalPort());

            while(!Thread.currentThread().isInterrupted())
            {
                gameServerSocket = receivingSocket.accept();
                listener.onReceive(new BufferedReader(new InputStreamReader(this.gameServerSocket.getInputStream())));
            }
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }

    public interface ServerCommunicationListener
    {
        void onBind(int port);

        void onReceive(BufferedReader reader);
    }
}
