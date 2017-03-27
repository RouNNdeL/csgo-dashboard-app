package com.roundel.csgodashboard.ui.activity;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.github.clans.fab.FloatingActionButton;
import com.roundel.csgodashboard.R;
import com.roundel.csgodashboard.db.DbHelper;
import com.roundel.csgodashboard.db.DbUtils;
import com.roundel.csgodashboard.entities.Map;
import com.roundel.csgodashboard.entities.utility.Grenade;
import com.roundel.csgodashboard.entities.utility.Stance;
import com.roundel.csgodashboard.entities.utility.Tags;
import com.roundel.csgodashboard.entities.utility.UtilityGrenade;
import com.roundel.csgodashboard.util.ListUtils;
import com.roundel.csgodashboard.view.taglayout.TagAdapter;
import com.roundel.csgodashboard.view.taglayout.TagLayout;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ViewNadeActivity extends AppCompatActivity
{
    private static final String TAG = ViewNadeActivity.class.getSimpleName();

    public static final String EXTRA_GRENADE_ID = "com.roundel.csgodashboard.extra.GRENADE_ID";

    //<editor-fold desc="private variables">
    @BindView(R.id.view_nade_backdrop) ImageView mBackdrop;
    @BindView(R.id.view_nade_appbar) AppBarLayout mAppbar;
    @BindView(R.id.view_nade_map) TextView mMap;
    @BindView(R.id.view_nade_grenade_icon) ImageView mGrenadeIcon;
    @BindView(R.id.view_nade_grenade) TextView mGrenade;
    @BindView(R.id.view_nade_stance_icon) ImageView mStanceIcon;
    @BindView(R.id.view_nade_stance) TextView mStance;
    @BindView(R.id.view_nade_tag_container) TagLayout mTagContainer;
    @BindView(R.id.view_nade_description) TextView mDescription;
    @BindView(R.id.view_nade_jumpthrow) RelativeLayout mJumpthrow;
    @BindView(R.id.view_nade_root) CoordinatorLayout mCoordinatorLayout;
    @BindView(R.id.view_nade_tag_header) TextView mTagHeader;
    @BindView(R.id.view_nade_collapsing_toolbar) CollapsingToolbarLayout mCollapsingToolbar;
    @BindView(R.id.view_nade_toolbar) Toolbar mToolbar;
    @BindView(R.id.view_nade_fab) FloatingActionButton mFab;

    private TagAdapter mTagAdapter;

    private DbHelper mDbHelper;
    private SQLiteDatabase mReadableDatabase;
    private UtilityGrenade mUtilityData;
    //TODO: Find a View that this listener should be attached to
    private final View.OnClickListener mOnBackdropClickListener = (v) ->
    {
        startNadeActivity();
    };
    private int mUtilityId;
    private final View.OnClickListener mOnFabClickListener = v ->
    {
        startEditNadeActivity();
    };
    //</editor-fold>

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_nade);

        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);

        mBackdrop.setOnClickListener(mOnBackdropClickListener);
        mFab.setOnClickListener(mOnFabClickListener);

        Intent intent = getIntent();
        mUtilityId = intent.getIntExtra(EXTRA_GRENADE_ID, -1);

        mDbHelper = new DbHelper(this);
        mReadableDatabase = mDbHelper.getReadableDatabase();
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        mUtilityData = DbUtils.queryGrenadeById(mReadableDatabase, mUtilityId);
        if(mUtilityData == null)
            throw new RuntimeException("You need to provide a valid utility_grenade_id in integer extra " + EXTRA_GRENADE_ID);

        fillActivity();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = new MenuInflater(this);
        inflater.inflate(R.menu.menu_view_nade, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch(item.getItemId())
        {
            case R.id.menu_view_nade_delete:
            {
                //TODO: Add deletion
            }
            case R.id.menu_view_nade_share:
            {
                //TODO: Add sharing
            }
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * This method should only be called in the {@link #onCreate(Bundle)} method of the activity
     */
    private void fillActivity()
    {
        final Map map = mUtilityData.getMap();
        final Stance stance = Stance.fromType(mUtilityData.getStance(), this);
        final Grenade grenade = Grenade.fromType(mUtilityData.getGrenadeId(), this);

        mCollapsingToolbar.setTitle(mUtilityData.getTitle());

        mMap.setText(map.getName());
        mStance.setText(stance.getTitle());
        mStanceIcon.setImageResource(stance.getIcon());
        mGrenade.setText(grenade.getName());
        mGrenadeIcon.setImageResource(grenade.getIcon());
        mDescription.setText(mUtilityData.getDescription());
        mJumpthrow.setVisibility(mUtilityData.isJumpThrow() ? View.VISIBLE : View.GONE);

        final Tags tags = mUtilityData.getTags();
        if(tags.size() > 0)
        {
            mTagContainer.setVisibility(View.VISIBLE);
            mTagHeader.setVisibility(View.VISIBLE);
            mTagAdapter = new TagAdapter(tags, this);
            mTagContainer.setAdapter(mTagAdapter);
        }
        else
        {
            mTagContainer.setVisibility(View.GONE);
            mTagHeader.setVisibility(View.GONE);
        }

        final List<Uri> imgUris = mUtilityData.getImgUris(this);
        if(imgUris.size() > 0)
            Glide.with(this).load(imgUris.get(0)).into(mBackdrop);
    }

    private void startEditNadeActivity()
    {
        Intent intent = new Intent(ViewNadeActivity.this, AddEditNadeActivity.class);
        intent.setAction(Intent.ACTION_EDIT);
        intent.putExtra(AddEditNadeActivity.EXTRA_GRENADE_ID, mUtilityId);
        startActivity(intent);
    }

    private void startNadeActivity()
    {
        final Intent intent = new Intent(ViewNadeActivity.this, GalleryActivity.class);
        intent.putExtra(GalleryActivity.EXTRA_PHOTO_URIS, ListUtils.join(mUtilityData.getImgUris(this)));
        startActivity(intent);
    }
}
