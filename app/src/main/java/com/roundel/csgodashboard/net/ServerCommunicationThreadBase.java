package com.roundel.csgodashboard.net;

import com.roundel.csgodashboard.util.LogHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.Charset;

/**
 * Created by Krzysiek on 2017-02-12.
 */
class ServerCommunicationThreadBase extends Thread
{
    private static final String TAG = ServerCommunicationThreadBase.class.getSimpleName();

    //<editor-fold desc="private variables">
    int connectionTimeout = 5000;
    int receiveTimeout = 5000;
    Socket gameServerSocket;
    //</editor-fold>

    void sendJSON(Socket socket, JSONObject json) throws IOException
    {
        final String string = json.toString();
        LogHelper.i(TAG, "Sending: " + string);
        sendBytes(socket, string.getBytes(Charset.defaultCharset()));
    }

    /**
     * @param socket socket to send the bytes on
     * @param bytes  bytes to send
     *
     * @throws IOException
     */
    void sendBytes(Socket socket, byte[] bytes) throws IOException
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
    void sendBytes(Socket socket, byte[] myByteArray, int start, int len) throws IOException
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

    JSONObject receiveJSON(Socket socket) throws IOException, JSONException
    {
        final String s = new String(receiveBytes(socket)).trim();
        LogHelper.i(TAG, "Received: " + s);
        return new JSONObject(s);
    }

    byte[] receiveBytes(Socket socket) throws IOException
    {
        InputStream inputStream = socket.getInputStream();
        DataInputStream dataInputStream = new DataInputStream(inputStream);
        final byte[] b = new byte[1024];
        dataInputStream.read(b);
        return b;
    }

    byte[] receiveBytes(Socket socket, int bufferSize) throws IOException
    {
        InputStream inputStream = socket.getInputStream();
        DataInputStream dataInputStream = new DataInputStream(inputStream);
        final byte[] b = new byte[bufferSize];
        dataInputStream.read(b);
        return b;
    }

    /**
     * @param array array of bytes to be parsed
     *
     * @return parsed {@link JSONObject}
     * @throws JSONException
     */
    JSONObject jsonFromByteArr(byte[] array) throws JSONException
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

    public void stopThread()
    {
        try
        {
            gameServerSocket.close();
        }
        catch(IOException ignored)
        {

        }
    }
}