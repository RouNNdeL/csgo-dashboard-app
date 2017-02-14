package com.roundel.csgodashboard.ui;

/**
 * Created by Krzysiek on 2017-01-20.
 */

import android.animation.Animator;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.transition.TransitionManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.roundel.csgodashboard.R;
import com.roundel.csgodashboard.entities.GameServer;
import com.roundel.csgodashboard.entities.GameState;
import com.roundel.csgodashboard.net.GameInfoListeningThread;
import com.roundel.csgodashboard.net.ServerGameInfoPortThread;
import com.roundel.csgodashboard.net.ServerPingingThread;
import com.roundel.csgodashboard.util.LogHelper;
import com.roundel.csgodashboard.view.ValueFillingIcon;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GameInfoActivity extends AppCompatActivity implements View.OnClickListener, GameInfoListeningThread.OnDataListener
{
    private static final String TAG = GameInfoActivity.class.getSimpleName();
    public static final String EXTRA_GAME_SERVER_HOST = "EXTRA_GAME_SERVER_HOST";
    public static final String EXTRA_GAME_SERVER_NAME = "EXTRA_GAME_SERVER_NAME";
    public static final String EXTRA_GAME_SERVER_PORT = "EXTRA_GAME_SERVER_PORT";
    private final ScheduledExecutorService pingingScheduler = Executors.newScheduledThreadPool(1);
    //@BindView(R.id.chart) LineChart mLineChart;
    //<editor-fold desc="private variables">
    @BindView(R.id.game_info_round_time) TextView mRoundTime;
    @BindView(R.id.game_info_round_no) TextView mRoundNumber;
    @BindView(R.id.game_info_bomb) ImageView mBombView;
    @BindView(R.id.game_info_section_round) LinearLayout mSectionRoundInfo;
    @BindView(R.id.game_info_score_home) TextView mScoreHome;
    @BindView(R.id.game_info_score_away) TextView mScoreAway;
    @BindView(R.id.game_info_health_icon) ValueFillingIcon mHealthIcon;
    @BindView(R.id.game_info_armor_icon) ValueFillingIcon mArmorIcon;
    private TextView text;
    private ImageView backdrop;
    private Toolbar toolbar;
    private AppBarLayout appBarLayout;
    private GameState gameState;
    private GameServer gameServer;
    private ScheduledFuture<?> pingingHandler;
    private GameInfoListeningThread mGameInfoServerThread;

    //</editor-fold>

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

        Glide.with(this).load(Uri.parse("file:///android_asset/maps/de_mirage.jpg")).into(backdrop);

        /*this.serverThread = new Thread(new ServerThread());
        this.serverThread.start();*/

        findViewById(R.id.testButton).setOnClickListener(this);
        findViewById(R.id.testBomb).setOnClickListener(this);
        findViewById(R.id.viewLogs).setOnClickListener(this);
        findViewById(R.id.testAddNade).setOnClickListener(this);

        mHealthIcon.setFillValue(1.0f);
        mArmorIcon.setFillValue(0.87f);

    }

    @Override
    protected void onResume()
    {
        super.onResume();
        final Intent intent = getIntent();
        if(intent.hasExtra(EXTRA_GAME_SERVER_NAME) &&
                intent.hasExtra(EXTRA_GAME_SERVER_HOST) &&
                intent.hasExtra(EXTRA_GAME_SERVER_PORT))
        {
            gameServer = new GameServer(
                    intent.getStringExtra(EXTRA_GAME_SERVER_NAME),
                    intent.getStringExtra(EXTRA_GAME_SERVER_HOST),
                    intent.getIntExtra(EXTRA_GAME_SERVER_PORT, -1)
            );
            startGameInfoListener();
            startPinging();

        }
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        if(pingingHandler != null)
        {
            pingingHandler.cancel(true);
            LogHelper.i(TAG, "Stopping the pingingHandler");
        }
        if(mGameInfoServerThread != null)
        {
            mGameInfoServerThread.stopListening();
            LogHelper.i(TAG, "Stopping the GameInfoListeningThread");
        }
    }

    @Override
    public void onClick(View v)
    {
        switch(v.getId())
        {
            case R.id.testButton:
            {
                Intent intent = new Intent(GameInfoActivity.this, ServerSetupActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.testBomb:
            {
                mRoundNumber.setVisibility(View.GONE);
                mBombView.setVisibility(View.VISIBLE);
                TransitionManager.beginDelayedTransition(mSectionRoundInfo);

                ValueAnimator animator = ValueAnimator.ofObject(new ArgbEvaluator(), getColor(R.color.bombPlantedInactive), getColor(R.color.bombPlantedActive));

                animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
                {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation)
                    {
                        Integer color = (Integer) animation.getAnimatedValue();
                        if(color != null)
                        {
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
                break;
            }
            case R.id.viewLogs:
            {
                Intent intent = new Intent(GameInfoActivity.this, LogActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.testAddNade:
            {
                Intent intent = new Intent(GameInfoActivity.this, AddNadeActivity.class);
                startActivity(intent);
                break;
            }
        }
    }

    @Override
    public void onDataReceived(String data)
    {
        try
        {
            if(gameState == null)
                gameState = GameState.fromJSON(new JSONObject(data));
            else
                gameState.update(new JSONObject(data));
            LogHelper.d(TAG, "Updated gameState: " + gameState.toString());
            updateViews();
        }
        catch(JSONException e)
        {
            LogHelper.e(TAG, e.toString());
            e.printStackTrace();
        }
    }

    private void startGameInfoListener()
    {
        mGameInfoServerThread = new GameInfoListeningThread();
        mGameInfoServerThread.setOnServerStartedListener(new GameInfoListeningThread.OnServerStartedListener()
        {
            @Override
            public void onServerStarted(int port)
            {
                sendPortToGameServer(port);
            }
        });
        mGameInfoServerThread.setOnDataListener(this);
        mGameInfoServerThread.start();
        LogHelper.i(TAG, "Starting the GameInfoListeningThread");
    }

    private void startPinging()
    {
        Runnable threadRunnable = new Runnable()
        {
            @Override
            public void run()
            {
                ServerPingingThread serverPingingThread = new ServerPingingThread(gameServer);
                serverPingingThread.start();
            }
        };
        if(pingingHandler != null)
            pingingHandler.cancel(false);
        pingingHandler = pingingScheduler.scheduleAtFixedRate(threadRunnable, 5, 5, TimeUnit.SECONDS);
        LogHelper.i(TAG, "Scheduled the pingingHandler for 5s");
    }

    private void sendPortToGameServer(int port)
    {
        ServerGameInfoPortThread portThread = new ServerGameInfoPortThread(gameServer, port);
        portThread.start();
    }

    private void updateViews()
    {
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                if(gameState == null)
                    return;
                mRoundNumber.setText(String.format(Locale.getDefault(), "Round %d", gameState.getRound()));
                mScoreHome.setText(Integer.toString(gameState.getScoreHome()));
                mScoreAway.setText(Integer.toString(gameState.getScoreAway()));
            }
        });
    }


}
