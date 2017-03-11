package com.roundel.csgodashboard.ui.activity;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.roundel.csgodashboard.R;
import com.roundel.csgodashboard.db.DbHelper;
import com.roundel.csgodashboard.db.DbUtils;
import com.roundel.csgodashboard.entities.Map;
import com.roundel.csgodashboard.entities.utility.Grenade;
import com.roundel.csgodashboard.entities.utility.Stance;
import com.roundel.csgodashboard.entities.utility.Tags;
import com.roundel.csgodashboard.entities.utility.UtilityGrenade;
import com.roundel.csgodashboard.view.CircleRectView;
import com.roundel.csgodashboard.view.taglayout.TagAdapter;
import com.roundel.csgodashboard.view.taglayout.TagLayout;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ViewNadeActivity extends AppCompatActivity
{
    public static final String EXTRA_UTILITY_ID = "com.roundel.csgodashboard.extra.GRENADE_ID";

    //<editor-fold desc="private variables">
    @BindView(R.id.view_nade_backdrop) CircleRectView mBackdrop;
    @BindView(R.id.view_nade_appbar) AppBarLayout mAppbar;
    @BindView(R.id.view_nade_map) TextView mMap;
    @BindView(R.id.view_nade_grenade_icon) ImageView mGrenadeIcon;
    @BindView(R.id.view_nade_grenade) TextView mGrenade;
    @BindView(R.id.view_nade_stance_icon) ImageView mStanceIcon;
    @BindView(R.id.view_nade_stance) TextView mStance;
    @BindView(R.id.view_nade_tag_container) TagLayout mTagContainer;
    @BindView(R.id.view_nade_description) TextView mDescription;
    @BindView(R.id.view_nade_root) NestedScrollView mCoordinatorLayout;

    private TagAdapter mTagAdapter;

    private DbHelper mDbHelper;
    private SQLiteDatabase mReadableDatabase;
    private UtilityGrenade mUtilityData;
    private Tags mTags;
    //</editor-fold>

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        /*if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            CircleToRectTransition transition = new CircleToRectTransition();
            transition.setDuration(500);
            transition.addTarget(R.id.utility_grenade_map_img);
            transition.addTarget(R.id.view_nade_backdrop);
            getWindow().setSharedElementEnterTransition(transition);
            getWindow().setSharedElementExitTransition(transition);
        }*/
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_nade);

        ButterKnife.bind(this);

        Intent intent = getIntent();
        int utilityId = intent.getIntExtra(EXTRA_UTILITY_ID, -1);
        if(utilityId < 0)
            throw new RuntimeException("You need to provide a valid utility_grenade_id in integer extra " + EXTRA_UTILITY_ID);

        mDbHelper = new DbHelper(this);
        mReadableDatabase = mDbHelper.getReadableDatabase();

        mUtilityData = DbUtils.queryGrenadeById(mReadableDatabase, utilityId);
        if(mUtilityData == null)
            throw new RuntimeException("You need to provide a valid utility_grenade_id in integer extra " + EXTRA_UTILITY_ID);

        fillActivity();
    }

    /**
     * This method should only be called in the {@link #onCreate(Bundle)} method of the activity
     */
    private void fillActivity()
    {
        final Map map = mUtilityData.getMap();
        final Stance stance = Stance.fromType(mUtilityData.getStance(), this);
        final Grenade grenade = Grenade.fromType(mUtilityData.getType(), this);

        if(getSupportActionBar() != null)
        {
            getSupportActionBar().setTitle(mUtilityData.getTitle());
        }

        mMap.setText(map.getName());
        mStance.setText(stance.getTitle());
        mStanceIcon.setImageResource(stance.getIcon());
        mGrenade.setText(grenade.getName());
        mGrenadeIcon.setImageResource(grenade.getIcon());
        mDescription.setText(mUtilityData.getDescription());

        mTagAdapter = new TagAdapter(mUtilityData.getTags(), this);
        mTagContainer.setAdapter(mTagAdapter);

        final List<Uri> imgUris = mUtilityData.getImgUris(this);
        if(imgUris.size() > 0)
            Glide.with(this).load(imgUris.get(0)).into(mBackdrop);
    }
}
