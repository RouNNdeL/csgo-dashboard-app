package com.roundel.csgodashboard;

/**
 * Created by Krzysiek on 2017-01-20.
 */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Iterator;
import java.util.Objects;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

public class Server extends Activity {

    private ServerSocket serverSocket;
    Handler updateConversationHandler;
    Thread serverThread = null;
    private TextView text;
    private JSONObject gameState = new JSONObject();

    public static final int SERVER_PORT = 6000;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        text = (TextView) findViewById(R.id.text2);

        updateConversationHandler = new Handler();

        this.serverThread = new Thread(new ServerThread());
        this.serverThread.start();

    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    class ServerThread implements Runnable {

        public void run() {
            Socket socket = null;
            try {
                serverSocket = new ServerSocket(SERVER_PORT);
                Log.d("Server", "Started socket on port: "+SERVER_PORT);
            } catch (IOException e) {
                e.printStackTrace();
            }
            while (!Thread.currentThread().isInterrupted()) {

                try {

                    socket = serverSocket.accept();

                    CommunicationThread commThread = new CommunicationThread(socket);
                    new Thread(commThread).start();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    class CommunicationThread implements Runnable {

        private Socket clientSocket;

        private BufferedReader input;

        public CommunicationThread(Socket clientSocket) {

            this.clientSocket = clientSocket;

            try {

                this.input = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void run() {

            while (!Thread.currentThread().isInterrupted()) {

                try {

                    String read = input.readLine();
                    if(read == null)
                        break;
                    JSONObject response = new JSONObject(read);
                    gameState = merge(gameState, response);
                    Log.d("GameState", gameState.toString());
                    updateConversationHandler.post(new updateUIThread(gameState));

                } catch (IOException e) {
                    e.printStackTrace();
                }
                catch(JSONException e)
                {
                    e.printStackTrace();
                }
            }
        }

    }

    class updateUIThread implements Runnable {
        private JSONObject state;

        public updateUIThread(JSONObject state) {
            this.state = state;
        }

        @Override
        public void run() {
            String weapon_name = "none";
            boolean reloading = false;
            int weapon_ammo_clip = -1;
            int weapon_ammo_reserve = -1;
            try
            {
                final JSONObject weapons = state.getJSONObject("player").getJSONObject("weapons");
                Iterator it = weapons.keys();
                {
                    String key = (String) it.next();
                    final JSONObject weapon = weapons.getJSONObject(key);
                    if(Objects.equals(weapon.getString("state"), "active"))
                    {
                        weapon_name = weapon.getString("name");
                        weapon_ammo_clip = weapon.getInt("ammo_clip");
                        weapon_ammo_reserve = weapon.getInt("ammo_reserve");
                    }
                    else if(Objects.equals(weapon.getString("state"), "reloading"))
                    {
                        weapon_name = weapon.getString("name");
                        weapon_ammo_clip = weapon.getInt("ammo_clip");
                        weapon_ammo_reserve = weapon.getInt("ammo_reserve");
                        reloading = true;
                    }
                }
            }
            catch(JSONException e)
            {
                e.printStackTrace();
            }
            text.setText(weapon_name+" "+weapon_ammo_clip+"/"+weapon_ammo_reserve+(reloading?" (reloading)aa":""));
        }
    }

    public static boolean isInteger(String s, int radix) {
        if(s.isEmpty()) return false;
        for(int i = 0; i < s.length(); i++) {
            if(i == 0 && s.charAt(i) == '-') {
                if(s.length() == 1) return false;
                else continue;
            }
            if(Character.digit(s.charAt(i),radix) < 0) return false;
        }
        return true;
    }

    public static JSONObject merge(JSONObject... params) throws JSONException
    {
        JSONObject merged = new JSONObject();
        for (JSONObject obj : params) {
            Iterator it = obj.keys();
            while (it.hasNext()) {
                String key = (String)it.next();
                merged.put(key, obj.get(key));
            }
        }
        return merged;
    }
}
