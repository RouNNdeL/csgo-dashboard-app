package com.roundel.csgodashboard;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.roundel.csgodashboard.entities.GameServer;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.Objects;

/**
 * Created by Krzysiek on 2017-01-29.
 */
public class ServerDiscoveryThread extends Thread implements Runnable
{
    private static final String TAG = ServerDiscoveryThread.class.getSimpleName();
    private static final String DISCOVERY_MESSAGE = "CSGO_DASHBOARD_DISCOVERY_REQUEST";
    private static final String DISCOVERY_RESPONSE = "CSGO_DASHBOARD_DISCOVERY_RESPONSE";

    private DatagramSocket socket;
    private ServerDiscoveryListener listener;
    private int discoveryTimeout = 500;

    public ServerDiscoveryThread(ServerDiscoveryListener listener)
    {
        this.listener = listener;
    }

    public void setDiscoveryTimeout(int discoveryTimeout)
    {
        this.discoveryTimeout = discoveryTimeout;
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

            byte[] sendData = DISCOVERY_MESSAGE.getBytes();

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
                    Log.d(TAG, "Closing the socket");
                }
            }, discoveryTimeout);

            //Wait for a response
            while(true)
            {
                byte[] recvBuf = new byte[15000];
                DatagramPacket receivePacket = new DatagramPacket(recvBuf, recvBuf.length);
                socket.receive(receivePacket);

                //We have a response
                Log.d(TAG, "Broadcast response from "+receivePacket.getAddress().getHostName()+": " + receivePacket.getAddress().getHostAddress()+":"+receivePacket.getPort());

                //Check if the message is correct
                String message = new String(receivePacket.getData()).trim();
                try
                {
                    JSONObject response = new JSONObject(message);
                    if(Objects.equals(response.getString("code"), DISCOVERY_RESPONSE))
                    {
                        listener.onServerFound(new GameServer(receivePacket.getAddress().getHostName(), receivePacket.getAddress().getHostAddress(), response.getInt("receiving_port")));
                    }
                }
                catch(JSONException e)
                {
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
        }
    }

    public interface ServerDiscoveryListener
    {
        void onServerFound(GameServer server);

        void onSocketClosed();

        void onSocketOpened();
    }
}
