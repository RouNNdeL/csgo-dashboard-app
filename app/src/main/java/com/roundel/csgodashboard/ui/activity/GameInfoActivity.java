package com.roundel.csgodashboard.ui.activity;

/**
 * Created by Krzysiek on 2017-01-20.
 */

import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.design.widget.AppBarLayout;
import android.support.transition.AutoTransition;
import android.support.transition.Transition;
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
import com.roundel.csgodashboard.entities.Map;
import com.roundel.csgodashboard.entities.RoundEvents;
import com.roundel.csgodashboard.entities.UserData;
import com.roundel.csgodashboard.net.GameInfoListeningThread;
import com.roundel.csgodashboard.net.ServerPingingThread;
import com.roundel.csgodashboard.net.ServerUpdateThread;
import com.roundel.csgodashboard.util.LogHelper;
import com.roundel.csgodashboard.view.FillingIcon;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import butterknife.BindColor;
import butterknife.BindInt;
import butterknife.BindView;
import butterknife.ButterKnife;

public class GameInfoActivity extends AppCompatActivity implements View.OnClickListener, GameInfoListeningThread.OnDataListener, RoundEvents, ServerUpdateThread.OnOffsetDetermined
{
    private static final String TAG = GameInfoActivity.class.getSimpleName();

    public static final String EXTRA_GAME_SERVER_HOST = "com.roundel.csgodashboard.extra.GAME_SERVER_HOST";
    public static final String EXTRA_GAME_SERVER_NAME = "com.roundel.csgodashboard.extra.GAME_SERVER_NAME";
    public static final String EXTRA_GAME_SERVER_PORT = "com.roundel.csgodashboard.extra.GAME_SERVER_PORT";

    private final ScheduledExecutorService mPingingScheduler = Executors.newScheduledThreadPool(1);
    private final int mBombResetDelay = 1000;
    private final float mBombTickingMaxScale = 1.15f;
    private final int mPingingPeriod = 5000;
    private final int mPingingDelay = 5000;

    //<editor-fold desc="private variables">
    @BindView(R.id.game_info_round_time) TextView mRoundTime;
    @BindView(R.id.game_info_round_time_text) TextView mRoundState;
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
    @BindView(R.id.game_info_name_home) TextView mNameHome;
    @BindView(R.id.game_info_name_away) TextView mNameAway;

    @BindColor(R.color.yellowT) int mColorYellowT;
    @BindColor(R.color.blueCT) int mColorBlueCT;

    @BindInt(R.integer.bomb_show_transition_duration) int mBombShowTransitionDuration;
    @BindInt(R.integer.bomb_hide_transition_duration) int mBombHideTransitionDuration;
    @BindInt(R.integer.bomb_ticking_anim_duration) int mBombTickingAnimDuration;
    @BindInt(R.integer.bomb_defuse_transition_duration) int mBombDefuseTransitionDuration;

    private TextView text;
    private ImageView mMapImage;
    private Toolbar mToolbar;
    private AppBarLayout mAppBarLayout;
    private GameState mGameState;
    private GameServer mGameServer;
    private UserData mUserData;
    private ScheduledFuture<?> mPingingHandler;
    private GameInfoListeningThread mGameInfoServerThread;

    private int maxHealthValue = 100;
    private int maxArmorValue = 100;

    private ValueAnimator mBombTickingAnimator;
    private ValueAnimator mBombTickScaleAnimator;
    private boolean mIsBombVisible;

    private CountDownTimer mTimer;

    private int mRoundTimeMillis = 116000;
    private int mBombTimeMillis = 40000;
    private int mFreezeTimeMillis = 16000;

    private long mServerTimeOffset;
    //</editor-fold>

    @Override
    public void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        text = (TextView) findViewById(R.id.text2);
        mMapImage = (ImageView) findViewById(R.id.main_backdrop);
        //title = (TextView) findViewById(R.id.main_toolbar_title);
        mToolbar = (Toolbar) findViewById(R.id.main_toolbar);
        mAppBarLayout = (AppBarLayout) findViewById(R.id.main_appbar);

        setSupportActionBar(mToolbar);

        /*this.serverThread = new Thread(new ServerThread());
        this.serverThread.start();*/

        findViewById(R.id.testButton).setOnClickListener(this);
        findViewById(R.id.testBomb).setOnClickListener(this);
        findViewById(R.id.testBombD).setOnClickListener(this);
        findViewById(R.id.viewLogs).setOnClickListener(this);
        findViewById(R.id.testAddNade).setOnClickListener(this);

        mUserData = UserData.mapsOnly(this);
        mUserData.save();
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
            mGameServer = new GameServer(
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
        if(mPingingHandler != null)
        {
            mPingingHandler.cancel(true);
            LogHelper.i(TAG, "Stopping the mPingingHandler");
        }
        if(mGameInfoServerThread != null)
        {
            mGameInfoServerThread.stopListening();
            LogHelper.i(TAG, "Stopping the GameInfoListeningThread");
        }
        mGameState = null;
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
                plantBomb(System.currentTimeMillis());
                break;
            }
            case R.id.testBombD:
            {
                Intent intent = new Intent(GameInfoActivity.this, UtilityActivity.class);
                startActivity(intent);
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
            if(mGameState == null)
            {
                mGameState = GameState.fromJSON(new JSONObject(data));
                mGameState.setRoundEventsListener(this);
            }
            else
                mGameState.update(new JSONObject(data));
            LogHelper.d(TAG, "Updated mGameState: " + mGameState.toString());
            update();
        }
        catch(JSONException e)
        {
            LogHelper.e(TAG, e.toString());
            e.printStackTrace();
        }
    }

    @Override
    public void onBombPlanted(final long serverTimestamp)
    {
        runOnUiThread(() -> plantBomb(serverTimestamp + mServerTimeOffset));
        LogHelper.d(
                "UpdateEvents",
                "onBombPlanted: " +
                        new java.text.SimpleDateFormat("MM-dd HH:mm:ss.SSS", Locale.getDefault()).format(new Date(serverTimestamp + mServerTimeOffset))
        );
        LogHelper.d("UpdateEvents", "Server offset" + mServerTimeOffset);
        LogHelper.d("RoundEvents", "onBombPlanted: " + (mGameState != null ? mGameState.toString() : "GameSate=null"));
    }

    @Override
    public void onBombExploded(long serverTimestamp)
    {
        runOnUiThread(this::explodeBomb);
        LogHelper.d("RoundEvents", "onBombExploded: " + (mGameState != null ? mGameState.toString() : "GameSate=null"));
    }

    @Override
    public void onBombDefused(long serverTimestamp)
    {
        runOnUiThread(this::defuseBomb);
        LogHelper.d("RoundEvents", "onBombDefused: " + (mGameState != null ? mGameState.toString() : "GameSate=null"));
    }

    @Override
    public void onRoundStart(final long serverTimestamp)
    {
        runOnUiThread(() -> startRound(serverTimestamp + mServerTimeOffset));
        LogHelper.d("RoundEvents", "onRoundStart: " + (mGameState != null ? mGameState.toString() : "GameSate=null"));
    }

    @Override
    public void onRoundEnd(long serverTimestamp)
    {
        LogHelper.d("RoundEvents", "onRoundEnd: " + (mGameState != null ? mGameState.toString() : "GameSate=null"));
    }

    @Override
    public void onFreezeTimeStart(final long serverTimestamp)
    {
        runOnUiThread(() -> startFreezeTime(serverTimestamp + mServerTimeOffset));
        LogHelper.d("RoundEvents", "onFreezeTimeStart: " + (mGameState != null ? mGameState.toString() : "GameSate=null"));
    }

    @Override
    public void onMatchStart(long serverTimestamp)
    {
        LogHelper.d("RoundEvents", "onMatchStart: " + (mGameState != null ? mGameState.toString() : "GameSate=null"));
    }

    @Override
    public void onMatchEnd(long serverTimestamp)
    {
        LogHelper.d("RoundEvents", "onMatchEnd: " + (mGameState != null ? mGameState.toString() : "GameSate=null"));
    }

    @Override
    public void onWarmupStart(long serverTimestamp)
    {
        runOnUiThread(this::startWarmup);
        LogHelper.d("RoundEvents", "onWarmupStart: " + (mGameState != null ? mGameState.toString() : "GameSate=null"));
    }

    @Override
    public void onOffsetDetermined(long offset)
    {
        mServerTimeOffset = offset;
    }

    private void startGameInfoListener()
    {
        mGameInfoServerThread = new GameInfoListeningThread();
        mGameInfoServerThread.setOnServerStartedListener(this::updateServer);
        mGameInfoServerThread.setOnDataListener(this);
        mGameInfoServerThread.start();
        LogHelper.i(TAG, "Starting the GameInfoListeningThread");
    }

    private void startPinging()
    {
        Runnable threadRunnable = () -> {
            ServerPingingThread serverPingingThread = new ServerPingingThread(mGameServer);
            serverPingingThread.start();
        };
        if(mPingingHandler != null)
            mPingingHandler.cancel(false);
        mPingingHandler = mPingingScheduler.scheduleAtFixedRate(threadRunnable, mPingingDelay, mPingingPeriod, TimeUnit.MILLISECONDS);
        LogHelper.i(TAG, "Scheduled the mPingingHandler for 5s");
    }

    private void updateServer(int port)
    {
        ServerUpdateThread updateThread = new ServerUpdateThread(mGameServer, port);
        updateThread.setRoundEventListener(this);
        updateThread.setOffsetListener(this);
        updateThread.start();
    }

    private void updateTeam()
    {
        if(mGameState != null && mGameState.getPlayer() != null && mGameState.getPlayer().getTeam() != null)
        {
            if(mGameState.getPlayer().getTeam() == GameState.Team.T)
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
        if(mGameState != null && mGameState.getPlayer() != null)
        {
            final GameState.Player player = mGameState.getPlayer();

            mStatsKills.setText(String.format(Locale.getDefault(), "%d", player.getKills()));
            mStatsAssists.setText(String.format(Locale.getDefault(), "%d", player.getAssists()));
            mStatsDeaths.setText(String.format(Locale.getDefault(), "%d", player.getDeaths()));
            mStatsKDR.setText(player.getKdrString());
        }
    }

    private void updateScores()
    {
        mScoreHome.setText(String.format(Locale.getDefault(), "%d", mGameState.getScoreHome()));
        mScoreAway.setText(String.format(Locale.getDefault(), "%d", mGameState.getScoreAway()));
    }

    private void updateHealthArmor()
    {
        if(mGameState != null && mGameState.getPlayer() != null)
        {
            final DecimalFormat format = new DecimalFormat("0");

            final float health = mGameState.getPlayer().getHealth();
            mHealthIcon.setFillValue(Math.min(health / maxHealthValue, 1));
            mHealthStats.setText(format.format(health));

            final float armor = mGameState.getPlayer().getArmor();
            mArmorIcon.setFillValue(Math.min(armor / maxArmorValue, 1));
            mArmorStats.setText(format.format(armor));
        }
    }

    private void updateTeamNames()
    {
        if(mGameState != null)
        {
            if(mGameState.getNameHome() != null)
            {
                mNameHome.setText(mGameState.getNameHome());
                mNameHome.setAllCaps(false);
            }
            else
            {
                mNameHome.setText(getString(R.string.game_info_home_name));
                mNameHome.setAllCaps(true);
            }

            if(mGameState.getNameAway() != null)
            {
                mNameAway.setText(mGameState.getNameAway());
                mNameAway.setAllCaps(false);
            }
            else
            {
                mNameAway.setText(getString(R.string.game_info_away_name));
                mNameAway.setAllCaps(true);
            }
        }
    }

    private void updateRoundNo()
    {
        if(mGameState.getMapPhase() != GameState.MapPhase.WARMUP)
        {
            mRoundNumber.setText(
                    String.format(
                            Locale.getDefault(),
                            getString(R.string.game_info_round), mGameState.getRound() + 1 //Cs numbers round from 0
                    )
            );
        }
    }

    private void updateMap()
    {
        final Map map = mUserData.getMaps().mapFromCodeName(mGameState.getMapCodeName());
        if(map != null)
        {
            Glide.with(this).load(map.getImageUri()).into(mMapImage);
            if(getSupportActionBar() != null)
            {
                //TODO: Find a way to fix the not updating toolbar
                getSupportActionBar().setTitle(map.getName());
            }
        }
    }

    private void update()
    {
        runOnUiThread(() -> {
            if(mGameState == null)
                return;

            updateRoundNo();
            updateScores();
            updateTeam();
            updateStats();
            updateHealthArmor();
            updateTeamNames();
            updateMap();
        });
    }

    private void startRound(long localTimestamp)
    {
        transitionHideBomb();
        startTimer((localTimestamp - System.currentTimeMillis()) + mRoundTimeMillis);
        mRoundState.setText(R.string.game_info_time_default);
    }

    private void startFreezeTime(long localTimestamp)
    {
        transitionHideBomb();
        startTimer((localTimestamp - System.currentTimeMillis()) + mFreezeTimeMillis);
        mRoundState.setText(R.string.game_info_timer_freeze);
    }

    private void startWarmup()
    {
        transitionHideBomb();
        mRoundTime.setText("");
        mRoundNumber.setText("");
        mRoundState.setText(R.string.game_info_time_warmup);
    }

    private void plantBomb(long localTimestamp)
    {
        transitionShowBomb();
        animateBombPlant();
        startTimer((localTimestamp - System.currentTimeMillis()) + mBombTimeMillis);
        mRoundState.setText(R.string.game_info_timer_planted);
    }

    private void explodeBomb()
    {
        if(mBombTickingAnimator != null)
            mBombTickingAnimator.cancel();
        if(mBombTickScaleAnimator != null)
            mBombTickScaleAnimator.cancel();

        mRoundState.setText(R.string.game_info_timer_exploded);
        mRoundTime.setText(String.format(Locale.getDefault(), "%01d:%02d", 0, 0));
    }

    private void defuseBomb()
    {
        mTimer.cancel();
        animateBombDefuse();
        mRoundTime.setText("");
        mRoundState.setText(R.string.game_info_timer_defused);
    }

    private void animateBombPlant()
    {
        if(mBombTickingAnimator != null)
            mBombTickingAnimator.cancel();
        if(mBombTickScaleAnimator != null)
            mBombTickScaleAnimator.cancel();


        mBombTickScaleAnimator = ValueAnimator.ofFloat(1.0f, mBombTickingMaxScale);
        mBombTickingAnimator = ValueAnimator.ofArgb(getColor(R.color.bombPlantedInactive), getColor(R.color.bombPlantedActive));

        mBombTickingAnimator.addUpdateListener(animation -> {
            Integer color = (Integer) animation.getAnimatedValue();
            if(color != null)
            {
                mBombView.setImageTintList(ColorStateList.valueOf(color));
                mBombTicksView.setImageTintList(ColorStateList.valueOf(color));
            }
        });

        mBombTickScaleAnimator.addUpdateListener(animation -> {
            mBombTicksView.setScaleX((float) animation.getAnimatedValue());
            mBombTicksView.setScaleY((float) animation.getAnimatedValue());
        });

        mBombTickingAnimator.setRepeatMode(ValueAnimator.REVERSE);
        mBombTickingAnimator.setDuration(mBombTickingAnimDuration);
        mBombTickingAnimator.setRepeatCount(ValueAnimator.INFINITE);
        mBombTickingAnimator.start();

        mBombTickScaleAnimator.setRepeatMode(ValueAnimator.REVERSE);
        mBombTickScaleAnimator.setDuration(mBombTickingAnimDuration);
        mBombTickScaleAnimator.setRepeatCount(ValueAnimator.INFINITE);
        mBombTickScaleAnimator.start();
    }

    private void animateBombDefuse()
    {

        if(mBombTickingAnimator != null)
            mBombTickingAnimator.cancel();
        if(mBombTickScaleAnimator != null)
            mBombTickScaleAnimator.cancel();

        ValueAnimator bombColor = ValueAnimator.ofArgb(getColor(R.color.bombPlantedInactive), getColor(R.color.bombDefused));
        ValueAnimator tickAlpha = ValueAnimator.ofFloat(1.0f, 0.0f);

        bombColor.addUpdateListener(animation -> {
            if(animation.getAnimatedValue() != null)
            {
                mBombView.setImageTintList(ColorStateList.valueOf((int) animation.getAnimatedValue()));
                mBombTicksView.setImageTintList(ColorStateList.valueOf((int) animation.getAnimatedValue()));
            }
        });

        tickAlpha.addUpdateListener(animation -> {
            if(animation.getAnimatedValue() != null)
            {
                mBombTicksView.setAlpha((float) animation.getAnimatedValue());
            }
        });

        AnimatorSet set = new AnimatorSet();
        set.setDuration(mBombDefuseTransitionDuration);
        set.playTogether(tickAlpha, bombColor);
        set.start();
    }

    private void transitionHideBomb()
    {
        if(!mIsBombVisible)
            return;

        mIsBombVisible = false;

        Transition transition = new AutoTransition();
        transition.setDuration(mBombHideTransitionDuration);
        TransitionManager.beginDelayedTransition(mSectionRoundInfo, transition);
        mRoundNumber.setVisibility(View.VISIBLE);
        mBombFrame.setVisibility(View.GONE);

        new Handler().postDelayed(this::resetBomb, mBombResetDelay);
    }

    private void transitionShowBomb()
    {
        if(mIsBombVisible)
            return;

        mIsBombVisible = true;

        Transition transition = new AutoTransition();
        transition.setDuration(mBombShowTransitionDuration);
        TransitionManager.beginDelayedTransition(mSectionRoundInfo, transition);
        mRoundNumber.setVisibility(View.GONE);
        mBombFrame.setVisibility(View.VISIBLE);
    }


    private void resetBomb()
    {
        mBombTicksView.setAlpha(1.0f);
        mBombTicksView.setScaleX(1.0f);
        mBombTicksView.setScaleY(1.0f);
        mBombTicksView.setImageTintList(ColorStateList.valueOf(getColor(R.color.bombPlantedInactive)));
        mBombView.setImageTintList(ColorStateList.valueOf(getColor(R.color.bombPlantedInactive)));
    }

    private void startTimer(long millis)
    {
        if(mTimer != null)
            mTimer.cancel();
        mTimer = new CountDownTimer(millis, 1000)
        {

            public void onTick(long millisUntilFinished)
            {
                mRoundTime.setText(String.format(
                        Locale.getDefault(),
                        "%01d:%02d",
                        (int) Math.floor(millisUntilFinished / 60 / 1000),
                        (millisUntilFinished / 1000) % 60
                ));
            }

            public void onFinish()
            {
                mRoundTime.setText(String.format(Locale.getDefault(), "%01d:%02d", 0, 0));
            }
        }.start();
    }
}
