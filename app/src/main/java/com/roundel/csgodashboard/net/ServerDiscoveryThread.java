package com.roundel.csgodashboard.net;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.roundel.csgodashboard.entities.GameServer;
import com.roundel.csgodashboard.util.LogHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.nio.charset.Charset;
import java.util.Enumeration;
import java.util.Objects;

/**
 * Created by Krzysiek on 2017-01-29.
 */
public class ServerDiscoveryThread extends Thread
{
    private static final String TAG = ServerDiscoveryThread.class.getSimpleName();
    private static final String DISCOVERY_MESSAGE = "CSGO_DASHBOARD_DISCOVERY_REQUEST";
    private static final String DISCOVERY_RESPONSE = "CSGO_DASHBOARD_DISCOVERY_RESPONSE";
    //<editor-fold desc="private variables">
    private DatagramSocket socket;
    private ServerDiscoveryListener listener;

    private int discoveryTimeout = 500;
//</editor-fold>

    /**
     * This thread is used to send a UDP broadcast and detect servers running on the local network
     */
    public ServerDiscoveryThread()
    {
    }

    @Override
    public void run()
    {
        // Find the server using UDP broadcast
        try
        {
            //Open a random port to send the package
            socket = new DatagramSocket();
            socket.setBroadcast(true);

            listener.onSocketOpened();

            byte[] sendData = DISCOVERY_MESSAGE.getBytes(Charset.defaultCharset());

            //Try the 255.255.255.255 first
            try
            {
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, InetAddress.getByName("255.255.255.255"), 8888);
                socket.send(sendPacket);
            }
            catch(Exception e)
            {
            }

            // Broadcast the message over all the network interfaces
            Enumeration interfaces = NetworkInterface.getNetworkInterfaces();
            while(interfaces.hasMoreElements())
            {
                NetworkInterface networkInterface = (NetworkInterface) interfaces.nextElement();

                if(networkInterface.isLoopback() || !networkInterface.isUp())
                {
                    continue; // Don't want to broadcast to the loopback interface
                }

                for(InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses())
                {
                    InetAddress broadcast = interfaceAddress.getBroadcast();
                    if(broadcast == null)
                    {
                        continue;
                    }

                    // Send the broadcast package!
                    try
                    {
                        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, broadcast, 8888);
                        socket.send(sendPacket);
                    }
                    catch(Exception e)
                    {
                    }
                }
            }

            new Handler(Looper.getMainLooper()).postDelayed(new Runnable()
            {
                @Override
                public void run()
                {
                    socket.close();
                }
            }, discoveryTimeout);

            //Wait for a response
            while(true)
            {
                byte[] recvBuf = new byte[15000];
                DatagramPacket receivePacket = new DatagramPacket(recvBuf, recvBuf.length);
                socket.receive(receivePacket);

                //We have a response
                LogHelper.i(TAG, "Broadcast response from " + receivePacket.getAddress().getHostName() + ": " + receivePacket.getAddress().getHostAddress() + ":" + receivePacket.getPort());

                //Check if the message is correct
                String message = new String(receivePacket.getData(), Charset.defaultCharset()).trim();

                LogHelper.i(TAG, "Message: " + message);
                try
                {
                    JSONObject response = new JSONObject(message);
                    if(Objects.equals(response.getString("code"), DISCOVERY_RESPONSE))
                    {
                        String hostName = response.getString("server_hostname");
                        final String hostAddress = receivePacket.getAddress().getHostAddress();
                        final int communicationPort = response.getInt("communication_port");
                        
                        Log.i(TAG, "New server \"" + hostName + "\"at:" + hostAddress + ":" + communicationPort);

                        listener.onServerFound(
                                new GameServer(
                                        hostName,
                                        hostAddress,
                                        communicationPort
                                ));
                    }
                }
                catch(JSONException e)
                {
                    LogHelper.e(TAG, e.toString());
                    e.printStackTrace();
                }
            }
        }
        catch(SocketException e)
        {
            listener.onSocketClosed();
        }
        catch(IOException e)
        {
            e.printStackTrace();
            LogHelper.e(TAG, e.toString());
        }
    }

    /**
     * @param listener a {@link ServerDiscoveryListener} that notifies about the process
     */
    public void setServerDiscoveryListener(ServerDiscoveryListener listener)
    {
        this.listener = listener;
    }

    public void setDiscoveryTimeout(int discoveryTimeout)
    {
        this.discoveryTimeout = discoveryTimeout;
    }

    public interface ServerDiscoveryListener
    {
        void onServerFound(GameServer server);

        void onSocketClosed();

        void onSocketOpened();
    }
}
