package com.roundel.csgodashboard.ui;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;

import com.roundel.csgodashboard.R;
import com.roundel.csgodashboard.db.DbHelper;
import com.roundel.csgodashboard.db.DbUtils;
import com.roundel.csgodashboard.entities.Map;
import com.roundel.csgodashboard.entities.utility.Grenade;
import com.roundel.csgodashboard.entities.utility.Stance;
import com.roundel.csgodashboard.entities.utility.UtilityGrenade;
import com.roundel.csgodashboard.view.taglayout.TagAdapter;
import com.roundel.csgodashboard.view.taglayout.TagLayout;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ViewNadeActivity extends AppCompatActivity
{
    public static final String EXTRA_GRENADE_ID = "com.roundel.csgodashboard.extra.GRENADE_ID";

    //<editor-fold desc="private variables">
    @BindView(R.id.view_nade_toolbar) Toolbar mToolbar;
    @BindView(R.id.view_nade_backdrop) ImageView mBackdrop;
    @BindView(R.id.view_nade_collapsing_toolbar) CollapsingToolbarLayout mCollapsingToolbar;
    @BindView(R.id.view_nade_appbar) AppBarLayout mAppbar;
    @BindView(R.id.view_nade_map) TextView mMap;
    @BindView(R.id.view_nade_grenade_icon) ImageView mGrenadeIcon;
    @BindView(R.id.view_nade_grenade) TextView mGrenade;
    @BindView(R.id.view_nade_stance_icon) ImageView mStanceIcon;
    @BindView(R.id.view_nade_stance) TextView mStance;
    @BindView(R.id.view_nade_tag_container) TagLayout mTagContainer;
    @BindView(R.id.view_nade_description) TextView mDescription;
    @BindView(R.id.view_nade_coordinator_layout) CoordinatorLayout mCoordinatorLayout;

    private TagAdapter mTagAdapter;

    private DbHelper mDbHelper;
    private SQLiteDatabase mReadableDatabase;
    private UtilityGrenade mUtilityData;
    //</editor-fold>

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_nade);

        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);

        Intent intent = getIntent();
        int utilityId = intent.getIntExtra(EXTRA_GRENADE_ID, -1);
        if(utilityId < 0)
            throw new IllegalStateException("You need to provide a valid utility id in int extra" + EXTRA_GRENADE_ID);

        mDbHelper = new DbHelper(this);
        mReadableDatabase = mDbHelper.getReadableDatabase();

        mUtilityData = DbUtils.queryGrenadeById(mReadableDatabase, utilityId);

        fillActivity();
    }

    /**
     * This method should only be called in the {@link #onCreate(Bundle)} method of the activity
     */
    private void fillActivity()
    {
        final Map map = DbUtils.queryMapById(mReadableDatabase, mUtilityData.getMapId());
        final Stance stance = Stance.fromType(mUtilityData.getStance(), this);
        final Grenade grenade = Grenade.fromType(mUtilityData.getStance(), this);

        getSupportActionBar().setTitle(mUtilityData.getTitle());

        mMap.setText(map.getName());
        mStance.setText(stance.getTitle());
        mStanceIcon.setImageResource(stance.getIcon());
        mGrenade.setText(grenade.getName());
        mGrenadeIcon.setImageResource(grenade.getIcon());
        mDescription.setText(mUtilityData.getDescription());

        mTagAdapter = new TagAdapter(mUtilityData.getTags(), this);
        mTagContainer.setAdapter(mTagAdapter);
    }
}
