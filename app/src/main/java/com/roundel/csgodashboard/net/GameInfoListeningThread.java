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
    private ServerSocket gameInfoListeningSocket;
    private boolean listen = true;

    private OnServerStartedListener onServerStartedListener;
    //</editor-fold>

    public GameInfoListeningThread() throws IOException
    {
        gameInfoListeningSocket = new ServerSocket(0);
        port = gameInfoListeningSocket.getLocalPort();
    }

    @Override
    public void run()
    {
        try
        {
            if(onServerStartedListener != null)
                onServerStartedListener.onServerStarted(port);

            LogHelper.i(TAG, "Started ServerSocket on port :" + port);
            while(listen)
            {
                Socket socket = gameInfoListeningSocket.accept();

                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                StringBuilder stringBuilder = new StringBuilder();
                String line;

                while((line = reader.readLine()) != null)
                {
                    stringBuilder.append(line);
                }

                String data = stringBuilder.toString();
                LogHelper.i(TAG, "Received data: " + data);
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
    }

    public void setOnServerStartedListener(OnServerStartedListener onServerStartedListener)
    {
        this.onServerStartedListener = onServerStartedListener;
    }

    public int getPort()
    {
        return port;
    }

    public interface OnServerStartedListener
    {
        void onServerStarted(int port);
    }
}
