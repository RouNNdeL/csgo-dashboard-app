package com.roundel.csgodashboard.ui;

/**
 * Created by Krzysiek on 2017-01-20.
 */

import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.design.widget.AppBarLayout;
import android.support.transition.TransitionManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.FrameLayout;
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
import com.roundel.csgodashboard.view.FillingIcon;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;

public class GameInfoActivity extends AppCompatActivity implements View.OnClickListener, GameInfoListeningThread.OnDataListener, GameState.RoundEvents
{
    private static final String TAG = GameInfoActivity.class.getSimpleName();
    public static final String EXTRA_GAME_SERVER_HOST = "EXTRA_GAME_SERVER_HOST";
    public static final String EXTRA_GAME_SERVER_NAME = "EXTRA_GAME_SERVER_NAME";
    public static final String EXTRA_GAME_SERVER_PORT = "EXTRA_GAME_SERVER_PORT";

    private final ScheduledExecutorService pingingScheduler = Executors.newScheduledThreadPool(1);
    //<editor-fold desc="private variables">

    @BindView(R.id.game_info_round_time) TextView mRoundTime;
    @BindView(R.id.game_info_round_time_text) TextView mRoundTimeText;
    @BindView(R.id.game_info_round_no) TextView mRoundNumber;
    @BindView(R.id.game_info_bomb_container) FrameLayout mBombFrame;
    @BindView(R.id.game_info_bomb) ImageView mBombView;
    @BindView(R.id.game_info_bomb_ticks) ImageView mBombTicksView;
    @BindView(R.id.game_info_section_round) LinearLayout mSectionRoundInfo;
    @BindView(R.id.game_info_score_home) TextView mScoreHome;
    @BindView(R.id.game_info_score_away) TextView mScoreAway;
    @BindView(R.id.game_info_side_home) TextView mSideHome;
    @BindView(R.id.game_info_side_away) TextView mSideAway;
    @BindView(R.id.game_info_health_icon) FillingIcon mHealthIcon;
    @BindView(R.id.game_info_armor_icon) FillingIcon mArmorIcon;
    @BindView(R.id.game_info_stats_kills) TextView mStatsKills;
    @BindView(R.id.game_info_stats_assists) TextView mStatsAssists;
    @BindView(R.id.game_info_stats_deaths) TextView mStatsDeaths;
    @BindView(R.id.game_info_stats_kdr) TextView mStatsKDR;
    @BindView(R.id.game_info_health_stats) TextView mHealthStats;
    @BindView(R.id.game_info_armor_stats) TextView mArmorStats;

    @BindColor(R.color.yellowT) int mColorYellowT;
    @BindColor(R.color.blueCT) int mColorBlueCT;

    private TextView text;
    private ImageView backdrop;
    private Toolbar toolbar;
    private AppBarLayout appBarLayout;
    private GameState gameState;
    private GameServer gameServer;
    private ScheduledFuture<?> pingingHandler;
    private GameInfoListeningThread mGameInfoServerThread;

    private int maxHealthValue = 100;
    private int maxArmorValue = 100;

    private ValueAnimator mBombTickingAnimator;
    private ValueAnimator mBombTickScaleAnimator;
    private CountDownTimer mTimer;

    private int mRoundTimeMillis = 116000;
    private int mBombTimeMillis = 40000;
    private int mFreezeTimeMillis = 16000;
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
        findViewById(R.id.testBombD).setOnClickListener(this);
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
                plantBomb();
                break;
            }
            case R.id.testBombD:
            {
                defuseBomb();
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
            {
                gameState = GameState.fromJSON(new JSONObject(data));
                gameState.setRoundEventsListener(this);
            }
            else
                gameState.update(new JSONObject(data));
            LogHelper.d(TAG, "Updated gameState: " + gameState.toString());
            update();
        }
        catch(JSONException e)
        {
            LogHelper.e(TAG, e.toString());
            e.printStackTrace();
        }
    }

    @Override
    public void onBombPlanted()
    {
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                plantBomb();
            }
        });
        LogHelper.d("RoundEvents", "onBombPlanted: " + gameState.toString());
    }

    @Override
    public void onBombDefused()
    {
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                defuseBomb();
            }
        });
        LogHelper.d("RoundEvents", "onBombDefused: " + gameState.toString());
    }

    @Override
    public void onRoundStart()
    {
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                startRound();
            }
        });
        LogHelper.d("RoundEvents", "onRoundStart: " + gameState.toString());
    }

    @Override
    public void onRoundEnd()
    {
        LogHelper.d("RoundEvents", "onRoundEnd: " + gameState.toString());
    }

    @Override
    public void onFreezeTimeStart()
    {
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                startFreezeTime();
            }
        });
        LogHelper.d("RoundEvents", "onFreezeTimeStart: " + gameState.toString());
    }

    @Override
    public void onMatchStart()
    {
        LogHelper.d("RoundEvents", "onMatchStart: " + gameState.toString());
    }

    @Override
    public void onMatchEnd()
    {
        LogHelper.d("RoundEvents", "onMatchEnd: " + gameState.toString());
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

    private void updateTeam()
    {
        if(gameState != null && gameState.getPlayer() != null && gameState.getPlayer().getTeam() != null)
        {
            if(gameState.getPlayer().getTeam() == GameState.Team.T)
            {
                mSideHome.setText(getString(R.string.game_info_side_t));
                mSideHome.setTextColor(mColorYellowT);

                mSideAway.setText(getString(R.string.game_info_side_ct));
                mSideAway.setTextColor(mColorBlueCT);
            }
            else
            {
                mSideHome.setText(getString(R.string.game_info_side_ct));
                mSideHome.setTextColor(mColorBlueCT);

                mSideAway.setText(getString(R.string.game_info_side_t));
                mSideAway.setTextColor(mColorYellowT);

            }
        }
    }

    private void updateStats()
    {
        if(gameState != null && gameState.getPlayer() != null)
        {
            final GameState.Player player = gameState.getPlayer();

            mStatsKills.setText(String.format(Locale.getDefault(), "%d", player.getKills()));
            mStatsAssists.setText(String.format(Locale.getDefault(), "%d", player.getAssists()));
            mStatsDeaths.setText(String.format(Locale.getDefault(), "%d", player.getDeaths()));
            mStatsKDR.setText(player.getKDRString());
        }
    }

    private void updateScores()
    {
        mScoreHome.setText(String.format(Locale.getDefault(), "%d", gameState.getScoreHome()));
        mScoreAway.setText(String.format(Locale.getDefault(), "%d", gameState.getScoreAway()));
    }

    private void updateHealthArmor()
    {
        if(gameState != null && gameState.getPlayer() != null)
        {
            final DecimalFormat format = new DecimalFormat("0");

            final float health = gameState.getPlayer().getHealth();
            mHealthIcon.setFillValue(Math.min(health / maxHealthValue, 1));
            mHealthStats.setText(format.format(health));

            final float armor = gameState.getPlayer().getArmor();
            mArmorIcon.setFillValue(Math.min(armor / maxArmorValue, 1));
            mArmorStats.setText(format.format(armor));
        }
    }

    private void startRound()
    {
        startTimer(mRoundTimeMillis);
    }

    private void plantBomb()
    {
        animateBombPlant();
        startTimer(mBombTimeMillis);
        mRoundTimeText.setText("Bomb Planted");
    }

    private void defuseBomb()
    {
        mTimer.cancel();
        animateBombDefuse();
        mRoundTime.setText("");
        mRoundTimeText.setText("Bomb Defused");
    }

    private void startFreezeTime()
    {
        animateHideBomb();
        startTimer(mFreezeTimeMillis);
        mRoundTimeText.setText("Freeze Time");
    }

    private void update()
    {
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                if(gameState == null)
                    return;
                mRoundNumber.setText(String.format(Locale.getDefault(), "Round %d", gameState.getRound()));

                updateScores();
                updateTeam();
                updateStats();
                updateHealthArmor();
                LogHelper.d(TAG, "Map phase: " + gameState.getMapPhase());
            }
        });
    }

    private void animateBombPlant()
    {
        if(mBombTickingAnimator != null)
            mBombTickingAnimator.cancel();
        if(mBombTickScaleAnimator != null)
            mBombTickScaleAnimator.cancel();

        TransitionManager.beginDelayedTransition(mSectionRoundInfo);
        mRoundNumber.setVisibility(View.GONE);
        mBombFrame.setVisibility(View.VISIBLE);

        mBombTickScaleAnimator = ValueAnimator.ofFloat(1.0f, 1.15f);
        mBombTickingAnimator = ValueAnimator.ofArgb(getColor(R.color.bombPlantedInactive), getColor(R.color.bombPlantedActive));

        mBombTickingAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
        {
            @Override
            public void onAnimationUpdate(ValueAnimator animation)
            {
                Integer color = (Integer) animation.getAnimatedValue();
                if(color != null)
                {
                    mBombView.setImageTintList(ColorStateList.valueOf(color));
                    mBombTicksView.setImageTintList(ColorStateList.valueOf(color));
                }
            }
        });

        mBombTickScaleAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
        {
            @Override
            public void onAnimationUpdate(ValueAnimator animation)
            {
                mBombTicksView.setScaleX((float) animation.getAnimatedValue());
                mBombTicksView.setScaleY((float) animation.getAnimatedValue());
            }
        });

        mBombTickingAnimator.setRepeatMode(ValueAnimator.REVERSE);
        mBombTickingAnimator.setDuration(1000);
        mBombTickingAnimator.setRepeatCount(ValueAnimator.INFINITE);
        mBombTickingAnimator.start();

        mBombTickScaleAnimator.setRepeatMode(ValueAnimator.REVERSE);
        mBombTickScaleAnimator.setDuration(1000);
        mBombTickScaleAnimator.setRepeatCount(ValueAnimator.INFINITE);
        mBombTickScaleAnimator.start();
    }

    private void animateBombDefuse()
    {
        ValueAnimator bombColor = ValueAnimator.ofArgb(getColor(R.color.bombPlantedInactive), getColor(R.color.bombDefused));
        ValueAnimator tickAlpha = ValueAnimator.ofFloat(1.0f, 0.0f);

        bombColor.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
        {
            @Override
            public void onAnimationUpdate(ValueAnimator animation)
            {
                if(animation.getAnimatedValue() != null)
                {
                    mBombView.setImageTintList(ColorStateList.valueOf((int) animation.getAnimatedValue()));
                    mBombTicksView.setImageTintList(ColorStateList.valueOf((int) animation.getAnimatedValue()));
                }
            }
        });

        tickAlpha.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
        {
            @Override
            public void onAnimationUpdate(ValueAnimator animation)
            {
                if(animation.getAnimatedValue() != null)
                {
                    mBombTicksView.setAlpha((float) animation.getAnimatedValue());
                }
            }
        });

        AnimatorSet set = new AnimatorSet();
        set.setDuration(500);
        set.playTogether(tickAlpha, bombColor);
        set.start();

        if(mBombTickingAnimator != null)
            mBombTickingAnimator.cancel();
        if(mBombTickScaleAnimator != null)
            mBombTickScaleAnimator.cancel();
    }

    private void animateHideBomb()
    {
        TransitionManager.beginDelayedTransition(mSectionRoundInfo);
        mRoundNumber.setVisibility(View.VISIBLE);
        mBombFrame.setVisibility(View.GONE);

        new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                resetBomb();
            }
        }, 1000);
    }

    private void resetBomb()
    {
        mBombTicksView.setAlpha(1.0f);
        mBombTicksView.setScaleX(1.0f);
        mBombTicksView.setScaleY(1.0f);
        mBombTicksView.setImageTintList(ColorStateList.valueOf(getColor(R.color.bombPlantedInactive)));
        mBombView.setImageTintList(ColorStateList.valueOf(getColor(R.color.bombPlantedInactive)));
    }

    private void startTimer(int millis)
    {
        if(mTimer != null)
            mTimer.cancel();
        mTimer = new CountDownTimer(millis, 1000)
        {

            public void onTick(long millisUntilFinished)
            {
                mRoundTime.setText(String.format(Locale.getDefault(), "%01d:%02d", (int) Math.floor(millisUntilFinished / 60 / 1000), (millisUntilFinished / 1000) % 60));
            }

            public void onFinish()
            {
                mRoundTime.setText(String.format(Locale.getDefault(), "%01d:%02d", 0, 0));
            }
        }.start();
    }
}
