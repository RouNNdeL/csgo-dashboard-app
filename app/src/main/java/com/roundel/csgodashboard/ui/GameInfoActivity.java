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
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.roundel.csgodashboard.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GameInfoActivity extends AppCompatActivity implements View.OnClickListener
{
    private static final String TAG = GameInfoActivity.class.getSimpleName();

    //<editor-fold desc="private variables">
    //@BindView(R.id.chart) LineChart mLineChart;
    @BindView(R.id.game_info_round_time) TextView mRoundTime;
    @BindView(R.id.game_info_round_no) TextView mRoundNumber;
    @BindView(R.id.game_info_bomb) ImageView mBombView;
    @BindView(R.id.game_info_section_round) LinearLayout mSectionRoundInfo;
    private TextView text;
    private ImageView backdrop;
    private Toolbar toolbar;
    private AppBarLayout appBarLayout;
//</editor-fold>

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
        /*mLineChart.setData(lineData);
        mLineChart.setDrawGridBackground(false);
        mLineChart.invalidate(); // refresh*/

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
}
