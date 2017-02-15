package com.roundel.csgodashboard.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.transition.TransitionManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.roundel.csgodashboard.R;
import com.roundel.csgodashboard.entities.Grenade;
import com.roundel.csgodashboard.entities.Stance;
import com.roundel.csgodashboard.recyclerview.UtilityImagesAdapter;
import com.roundel.csgodashboard.spinner.GrenadeAdapter;
import com.roundel.csgodashboard.spinner.StanceAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AddNadeActivity extends AppCompatActivity
{
    private static final String TAG = AddNadeActivity.class.getSimpleName();

    private static int IMAGE_REQUEST_CODE = 1237;

    //<editor-fold desc="private variables">
    @BindView(R.id.add_nade_toolbar) Toolbar mToolbar;

    @BindView(R.id.add_nade_spinner_grenade) Spinner mGrenadeSpinner;
    @BindView(R.id.add_nade_spinner_stance) Spinner mStanceSpinner;
    @BindView(R.id.add_nade_recyclerview_image) RecyclerView mImageRecyclerView;

    private StanceAdapter mStanceAdapter;
    private GrenadeAdapter mGrenadeAdapter;
    private UtilityImagesAdapter mImageAdapter;

    private List<Stance> mStanceList = new ArrayList<>();
    private List<Grenade> mGrenadeList = new ArrayList<>();
    private List<Uri> mImageList = new ArrayList<>();

    private GridLayoutManager mImageLayoutManager;
    //</editor-fold>

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_nade);

        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);

        getSupportActionBar().setTitle("Add nade");

        mStanceList = Stance.getDefaultStanceList(this);
        mStanceAdapter = new StanceAdapter(this, R.layout.list_icon_two_line_no_ripple, R.id.list_text_primary, mStanceList);
        mStanceSpinner.setAdapter(mStanceAdapter);

        mGrenadeList = Grenade.getDefaultGrenadeList(this);
        mGrenadeAdapter = new GrenadeAdapter(this, R.layout.list_icon_one_line_no_ripple, R.id.list_text_primary, mGrenadeList);
        mGrenadeSpinner.setAdapter(mGrenadeAdapter);

        mImageList.add(Uri.parse("file:///android_asset/maps/de_mirage.jpg"));
        mImageList.add(Uri.parse("file:///android_asset/maps/de_mirage.jpg"));
        mImageList.add(Uri.parse("file:///android_asset/maps/de_mirage.jpg"));
        mImageList.add(Uri.parse("file:///android_asset/maps/de_mirage.jpg"));
        mImageList.add(Uri.parse("file:///android_asset/maps/de_mirage.jpg"));
        mImageList.add(Uri.parse("file:///android_asset/maps/de_mirage.jpg"));
        mImageList.add(Uri.parse("file:///android_asset/maps/de_mirage.jpg"));
        //TODO: Find a way to fix the 'GridLayoutManager' and possibly animate height changes
        mImageLayoutManager = new GridLayoutManager(this, 3);
        mImageAdapter = new UtilityImagesAdapter(mImageList, this);
        mImageAdapter.setOnAddPhotoListener(new OnAddPhotoListener());
        mImageRecyclerView.setAdapter(mImageAdapter);
        mImageRecyclerView.setLayoutManager(mImageLayoutManager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_add_nade, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch(item.getItemId())
        {
            case R.id.menu_add_nade_done:
                //TODO: Validate the form, then save it's state and finish the Activity
                Toast.makeText(this, "TODO: Save and finish the activity", Toast.LENGTH_SHORT).show();
                return true;
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent)
    {
        super.onActivityResult(requestCode, resultCode, intent);

        if(requestCode == IMAGE_REQUEST_CODE && resultCode == RESULT_OK && intent != null && intent.getData() != null)
        {

            Uri uri = intent.getData();

            mImageList.add(uri);
            mImageAdapter.notifyDataSetChanged();
            TransitionManager.beginDelayedTransition((ViewGroup) mImageRecyclerView.getParent());
        }
    }

    private class OnAddPhotoListener implements View.OnClickListener
    {
        @Override
        public void onClick(View v)
        {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), IMAGE_REQUEST_CODE);
        }
    }
}
