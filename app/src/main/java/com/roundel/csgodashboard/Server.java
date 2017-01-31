package com.roundel.csgodashboard;

/**
 * Created by Krzysiek on 2017-01-20.
 */

import android.animation.Animator;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.AppBarLayout;
import android.support.transition.TransitionManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.roundel.csgodashboard.ui.ServerSetupActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Server extends AppCompatActivity implements View.OnClickListener
{

    public static final int SERVER_PORT = 6000;
    Handler updateConversationHandler;
    Thread serverThread = null;
    private ServerSocket serverSocket;
    private TextView text;
    private TextView title;
    @BindView(R.id.chart)
    LineChart mLineChart;
    @BindView(R.id.game_info_round_time) TextView mRoundTime;
    @BindView(R.id.game_info_round_no) TextView mRoundNumber;
    @BindView(R.id.game_info_bomb) ImageView mBombView;
    private ImageView backdrop;
    @BindView(R.id.game_info_section_round) LinearLayout mSectionRoundInfo;
    private Toolbar toolbar;
    private AppBarLayout appBarLayout;
    private JSONObject gameState = new JSONObject();

    @Override
    public void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        text = (TextView) findViewById(R.id.text2);
        backdrop = (ImageView) findViewById(R.id.main_backdrop);
        //title = (TextView) findViewById(R.id.main_toolbar_title);
        toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        appBarLayout = (AppBarLayout) findViewById(R.id.main_appbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Mirage");

        ViewTreeObserver observer = backdrop.getViewTreeObserver();
        observer.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener()
        {
            @Override
            public boolean onPreDraw()
            {
                backdrop.getViewTreeObserver().removeOnPreDrawListener(this);
                int backdropHeight = backdrop.getMeasuredHeight();
                int backdropWidth = backdrop.getMeasuredWidth();

                Bitmap backdropContent = BitmapFactory.decodeResource(getResources(), R.drawable.map_de_mirage);
                int newHeight = (int) (Double.valueOf(backdropContent.getHeight()) * (Double.valueOf(backdropWidth) / Double.valueOf(backdropContent.getWidth())));
                Log.d("BitmapScale", backdropContent.getHeight() + " " + backdropWidth + " " + backdropContent.getWidth() + " " + (Double.valueOf(backdropWidth) / Double.valueOf(backdropContent.getWidth())));
                Bitmap scaled = Bitmap.createScaledBitmap(backdropContent, backdropWidth, newHeight, true);
                backdrop.setImageBitmap(scaled);

                return true;
            }
        });

        updateConversationHandler = new Handler();

        /*this.serverThread = new Thread(new ServerThread());
        this.serverThread.start();*/

        Log.d("Name", Build.MANUFACTURER + " " + Build.MODEL);

        findViewById(R.id.testButton).setOnClickListener(this);
        findViewById(R.id.testBomb).setOnClickListener(this);

        List<Entry> entries = new ArrayList<>();
        entries.add(new Entry(1, 800));
        entries.add(new Entry(2, 3400));
        entries.add(new Entry(3, 2000));

        LineDataSet dataSet = new LineDataSet(entries, "Money"); // add entries to dataset
        dataSet.setColor(getColor(R.color.greenMoney));
        dataSet.setCircleColor(getColor(R.color.greenMoney));
        dataSet.setCircleColorHole(getColor(R.color.grey900));
        dataSet.setLineWidth(2.5f);
        dataSet.setCircleRadius(4);

        LineData lineData = new LineData(dataSet);
        mLineChart.setData(lineData);
        mLineChart.setDrawGridBackground(false);
        mLineChart.invalidate(); // refresh

    }

    @Override
    protected void onStop()
    {
        super.onStop();
        try
        {
            serverSocket.close();
        }
        catch(IOException | NullPointerException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v)
    {
        if(v.getId() == R.id.testButton)
        {
            Intent intent = new Intent(Server.this, ServerSetupActivity.class);
            startActivity(intent);
        }
        else if(v.getId() == R.id.testBomb)
        {
            mRoundNumber.setVisibility(View.GONE);
            mBombView.setVisibility(View.VISIBLE);
            TransitionManager.beginDelayedTransition(mSectionRoundInfo);

            ValueAnimator animator = ValueAnimator.ofObject(new ArgbEvaluator(), getColor(R.color.bombPlantedInactive), getColor(R.color.bombPlantedActive));

            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    Integer color = (Integer) animation.getAnimatedValue();
                    if (color != null) {
                        mBombView.setImageTintList(ColorStateList.valueOf(color));
                    }
                }
            });

            animator.addListener(new Animator.AnimatorListener()
            {
                @Override
                public void onAnimationStart(Animator animation)
                {

                }

                @Override
                public void onAnimationEnd(Animator animation)
                {
                    //TODO: Add a custom transition that will hide the flashing lights
                    mBombView.setImageDrawable(getDrawable(R.drawable.bomb_defused));
                    mBombView.setImageTintList(ColorStateList.valueOf(getColor(R.color.bombDefused)));
                }

                @Override
                public void onAnimationCancel(Animator animation)
                {

                }

                @Override
                public void onAnimationRepeat(Animator animation)
                {

                }
            });

            animator.setRepeatMode(ValueAnimator.REVERSE);
            animator.setDuration(1000);
            animator.setRepeatCount(10);
            animator.start();
        }
    }

    public static boolean isInteger(String s, int radix)
    {
        if(s.isEmpty()) return false;
        for(int i = 0; i < s.length(); i++)
        {
            if(i == 0 && s.charAt(i) == '-')
            {
                if(s.length() == 1) return false;
                else continue;
            }
            if(Character.digit(s.charAt(i), radix) < 0) return false;
        }
        return true;
    }

    public static JSONObject merge(JSONObject... params) throws JSONException
    {
        JSONObject merged = new JSONObject();
        for(JSONObject obj : params)
        {
            Iterator it = obj.keys();
            while(it.hasNext())
            {
                String key = (String) it.next();
                merged.put(key, obj.get(key));
            }
        }
        return merged;
    }

    class ServerThread implements Runnable
    {

        public void run()
        {
            Socket socket = null;
            try
            {
                serverSocket = new ServerSocket(SERVER_PORT);
                Log.d("Server", "Started socket on port: " + SERVER_PORT);
            }
            catch(IOException e)
            {
                e.printStackTrace();
            }
            while(!Thread.currentThread().isInterrupted())
            {

                try
                {
                    socket = serverSocket.accept();

                    CommunicationThread commThread = new CommunicationThread(socket);
                    new Thread(commThread).start();

                }
                catch(IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    class CommunicationThread implements Runnable
    {

        private Socket clientSocket;

        private BufferedReader input;

        public CommunicationThread(Socket clientSocket)
        {

            this.clientSocket = clientSocket;

            try
            {

                this.input = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));

            }
            catch(IOException e)
            {
                e.printStackTrace();
            }
        }

        @Override
        public void run()
        {

            while(!Thread.currentThread().isInterrupted())
            {

                try
                {

                    String read = input.readLine();
                    if(read == null)
                        break;
                    JSONObject response = new JSONObject(read);
                    gameState = merge(gameState, response);
                    Log.d("GameState", gameState.toString());
                    updateConversationHandler.post(new updateUIThread(gameState));

                }
                catch(IOException e)
                {
                    e.printStackTrace();
                }
                catch(JSONException e)
                {
                    e.printStackTrace();
                }
            }
        }

    }

    class updateUIThread implements Runnable
    {
        private JSONObject state;

        public updateUIThread(JSONObject state)
        {
            this.state = state;
        }

        @Override
        public void run()
        {
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
            text.setText(weapon_name + " " + weapon_ammo_clip + "/" + weapon_ammo_reserve + (reloading ? " (reloading)" : ""));
        }
    }
}
