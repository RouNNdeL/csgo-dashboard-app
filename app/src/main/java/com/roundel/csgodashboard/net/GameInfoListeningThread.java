package com.roundel.csgodashboard.net;

import com.roundel.csgodashboard.util.LogHelper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Krzysiek on 2017-02-08.
 */
public class GameInfoListeningThread extends Thread implements Runnable
{
    private static final String TAG = GameInfoListeningThread.class.getSimpleName();

    //<editor-fold desc="private variables">
    private int port;
    private ServerSocket serverSocket;
    private boolean listen = true;

    private OnServerStartedListener onServerStartedListener;
    private OnDataListener onDataListener;
    //</editor-fold>

    public GameInfoListeningThread()
    {
    }

    @Override
    public void run()
    {
        try
        {
            serverSocket = new ServerSocket(0);
            port = serverSocket.getLocalPort();

            if(onServerStartedListener != null)
                onServerStartedListener.onServerStarted(port);

            LogHelper.i(TAG, "Started ServerSocket on port: " + port);
            while(listen)
            {
                Socket socket = serverSocket.accept();

                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                StringBuilder stringBuilder = new StringBuilder();
                String line;

                while((line = reader.readLine()) != null)
                {
                    stringBuilder.append(line);
                }

                String data = stringBuilder.toString();
                LogHelper.i(TAG, "Received data: " + data);
                if(onDataListener != null)
                    onDataListener.onDataReceived(data);
            }
        }
        catch(IOException e)
        {
            LogHelper.e(TAG, e.toString());
            e.printStackTrace();
        }
    }

    public void stopListening()
    {
        this.listen = false;
        try
        {
            if(serverSocket != null)
                serverSocket.close();
        }
        catch(IOException ignored)
        {

        }
    }

    public void setOnServerStartedListener(OnServerStartedListener onServerStartedListener)
    {
        this.onServerStartedListener = onServerStartedListener;
    }

    public void setOnDataListener(OnDataListener onDataListener)
    {
        this.onDataListener = onDataListener;
    }

    public interface OnServerStartedListener
    {
        void onServerStarted(int port);
    }

    public interface OnDataListener
    {
        void onDataReceived(String data);
    }
}
