package com.roundel.csgodashboard.ui.activity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.roundel.csgodashboard.R;
import com.roundel.csgodashboard.adapter.spinner.AnyGrenadeAdapter;
import com.roundel.csgodashboard.adapter.spinner.AnyMapAdapter;
import com.roundel.csgodashboard.db.DbHelper;
import com.roundel.csgodashboard.db.DbUtils;
import com.roundel.csgodashboard.entities.Map;
import com.roundel.csgodashboard.entities.utility.FilterGrenade;
import com.roundel.csgodashboard.entities.utility.Grenade;
import com.roundel.csgodashboard.entities.utility.Tags;
import com.roundel.csgodashboard.ui.fragment.UtilityGrenadeFragment;
import com.roundel.csgodashboard.view.OverlayView;
import com.roundel.csgodashboard.view.taglayout.TagAdapter;
import com.roundel.csgodashboard.view.taglayout.TagLayout;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UtilityActivity extends AppCompatActivity implements SearchView.OnQueryTextListener
{
    private static final String TAG = UtilityActivity.class.getSimpleName();

    //<editor-fold desc="private variables">
    @BindView(R.id.utility_toolbar) Toolbar mToolbar;
    @BindView(R.id.utility_tablayout) TabLayout mTabLayout;
    @BindView(R.id.utility_appbar) AppBarLayout mAppbar;
    @BindView(R.id.utility_viewpager) ViewPager mViewPager;
    @BindView(R.id.utility_fab_menu) FloatingActionMenu mFabMenu;
    @BindView(R.id.utility_fab_grenade) FloatingActionButton mFabGrenade;
    @BindView(R.id.utility_fab_boost) FloatingActionButton mFabBoost;
    @BindView(R.id.utility_coordinator) CoordinatorLayout mCoordinatorLayout;
    @BindView(R.id.utility_overlay) OverlayView mOverlay;

    //Filter dialog Views and Adpaters
    private Spinner mMapSpinner;
    private Spinner mGrenadeSpinner;
    private Spinner mJumpthrowSpinner;
    private TagLayout mTagLayout;

    private TagAdapter mTagAdapter;
    /**
     * The {@link PagerAdapter} that will provide fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every loaded fragment in memory. If
     * this becomes too memory intensive, it may be best to switch to a {@link
     * FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private SQLiteDatabase mReadableDatabase;

    private Cursor mMapCursor;
    private Tags mTags;

    private FilterGrenade mGrenadeFilter = new FilterGrenade();
    //</editor-fold>

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_utility);

        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);

        mReadableDatabase = new DbHelper(this).getReadableDatabase();

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mSectionsPagerAdapter);

        mFabMenu.setOnMenuToggleListener((boolean opened) -> mOverlay.setInterceptEvents(opened));

        mOverlay.setOnClickListener((View v) -> mFabMenu.close(true));

        mFabGrenade.setOnClickListener(v ->
        {
            startActivity(new Intent(UtilityActivity.this, AddEditNadeActivity.class));
            mFabMenu.close(true);
        });

        TabLayout tabLayout = (TabLayout) findViewById(R.id.utility_tablayout);
        tabLayout.setupWithViewPager(mViewPager);
    }

    @Override
    protected void onResume()
    {
        mMapCursor = DbUtils.queryMaps(
                this.mReadableDatabase,
                new String[]{Map._ID, Map.COLUMN_NAME_NAME}
        );
        mTags = DbUtils.queryAllTags(mReadableDatabase);
        if(mSectionsPagerAdapter.mUtilityGrenadeFragment != null)
            mSectionsPagerAdapter.mUtilityGrenadeFragment.updateData(mGrenadeFilter);
        super.onResume();
    }

    @Override
    public void onBackPressed()
    {
        if(mFabMenu.isOpened())
            mFabMenu.close(true);
        else
            super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_utility, menu);

        MenuItem searchItem = menu.findItem(R.id.menu_utility_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(this);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch(item.getItemId())
        {
            case R.id.menu_utility_search:
                return true;
            case R.id.menu_utility_filter:
                showGrenadeFilterDialog();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onQueryTextSubmit(String query)
    {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText)
    {
        mGrenadeFilter.setSearchQuery(newText);
        mSectionsPagerAdapter.mUtilityGrenadeFragment.updateData(mGrenadeFilter);
        return false;
    }

    private void showGrenadeFilterDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(R.layout.dialog_filter_grenade);
        builder.setPositiveButton("Ok", (dialog, which) -> filterGrenade())
                .setNegativeButton("Cancel", (dialog, which) ->
                {})
                .setTitle("Search filter");
        AlertDialog dialog = builder.create();
        dialog.show();

        setupGrenadeDialog(dialog);
    }

    private void setupGrenadeDialog(AlertDialog dialog)
    {
        mMapSpinner = (Spinner) dialog.findViewById(R.id.utility_grenade_filter_map);
        mGrenadeSpinner = (Spinner) dialog.findViewById(R.id.utility_grenade_filter_grenade);
        mJumpthrowSpinner = (Spinner) dialog.findViewById(R.id.utility_grenade_filter_jumpthrow);
        mTagLayout = (TagLayout) dialog.findViewById(R.id.utility_grenade_filter_taglayout);

        AnyMapAdapter mapAdapter = new AnyMapAdapter(this, mMapCursor);
        AnyGrenadeAdapter grenadeAdapter = new AnyGrenadeAdapter(this, Grenade.getDefaultGrenadeList(this));
        ArrayAdapter<String> jumpthrowAdapter = new ArrayAdapter<>(
                this, R.layout.list_simple_one_line_no_ripple,
                R.id.list_text_primary,
                new String[]{"Any", "Yes", "No"}
        );
        mTagAdapter = new TagAdapter(mTags, this);

        mMapSpinner.setAdapter(mapAdapter);
        mGrenadeSpinner.setAdapter(grenadeAdapter);
        mJumpthrowSpinner.setAdapter(jumpthrowAdapter);
        mTagLayout.setAdapter(mTagAdapter);

        //Setup default values, when have been set in the FilterGrenade
        mGrenadeSpinner.setSelection(grenadeAdapter.getItemPosition(
                mGrenadeFilter.getType() == null ? -1 : mGrenadeFilter.getType()
        ));
        mMapSpinner.setSelection(mapAdapter.getItemPosition(
                mGrenadeFilter.getMapId() == null ? -1 : mGrenadeFilter.getMapId()
        ));
        mJumpthrowSpinner.setSelection(
                mGrenadeFilter.getJumpThrow() == null ? 0 : mGrenadeFilter.getJumpThrow() ? 1 : 2
        );
        mTagAdapter.setSelectedItemIds(mGrenadeFilter.getTagIds());
    }

    private void filterGrenade()
    {
        Boolean jumpthrow;
        if(mJumpthrowSpinner.getSelectedItemId() == 1)
            jumpthrow = true;
        else if(mJumpthrowSpinner.getSelectedItemId() == 2)
            jumpthrow = false;
        else
            jumpthrow = null;

        Long mapId = null;
        if(mMapSpinner.getSelectedItemId() >= 0)
            mapId = mMapSpinner.getSelectedItemId();

        Integer type = null;
        if(mGrenadeSpinner.getSelectedItemId() >= 0)
        {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            {
                type = Math.toIntExact(mGrenadeSpinner.getSelectedItemId());
            }
            else
            {
                type = (int) mGrenadeSpinner.getSelectedItemId();
            }
        }

        mGrenadeFilter.setMapId(mapId);
        mGrenadeFilter.setType(type);
        mGrenadeFilter.setTagIds(mTagAdapter.getSelectedItemIds());
        mGrenadeFilter.setJumpThrow(jumpthrow);

        mSectionsPagerAdapter.mUtilityGrenadeFragment.updateData(mGrenadeFilter);
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to one of the
     * sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter
    {
        //<editor-fold desc="private variables">
        private UtilityGrenadeFragment mUtilityGrenadeFragment;
        //</editor-fold>

        public SectionsPagerAdapter(FragmentManager fm)
        {
            super(fm);
        }

        @Override
        public Fragment getItem(int position)
        {
            if(position == 0)
                return mUtilityGrenadeFragment = UtilityGrenadeFragment.newInstance();
            else
                return null;
        }

        @Override
        public int getCount()
        {
            return 1;
        }

        @Override
        public CharSequence getPageTitle(int position)
        {
            switch(position)
            {
                case 0:
                    return "Grenades";
                case 1:
                    return "Boosts";
            }
            return null;
        }
    }
}
