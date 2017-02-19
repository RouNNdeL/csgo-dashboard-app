package com.roundel.csgodashboard.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.transition.TransitionManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Spinner;
import android.widget.Toast;

import com.roundel.csgodashboard.R;
import com.roundel.csgodashboard.adapter.GrenadeAdapter;
import com.roundel.csgodashboard.adapter.GridImageAdapter;
import com.roundel.csgodashboard.adapter.StanceAdapter;
import com.roundel.csgodashboard.entities.Grenade;
import com.roundel.csgodashboard.entities.Stance;
import com.roundel.csgodashboard.view.ExpandableHeightGridView;
import com.roundel.csgodashboard.view.taglayout.TagAdapter;
import com.roundel.csgodashboard.view.taglayout.TagLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AddNadeActivity extends AppCompatActivity implements TagAdapter.TagActionListener
{
    private static final String TAG = AddNadeActivity.class.getSimpleName();

    private static final int IMAGE_REQUEST_CODE = 1237;
    private static final int MAX_IMAGE_COUNT = 50;

    //<editor-fold desc="private variables">
    @BindView(R.id.add_nade_toolbar) Toolbar mToolbar;

    @BindView(R.id.add_nade_spinner_grenade) Spinner mGrenadeSpinner;
    @BindView(R.id.add_nade_spinner_stance) Spinner mStanceSpinner;
    @BindView(R.id.add_nade_image_grid) ExpandableHeightGridView mImageGrid;

    @BindView(R.id.add_nade_tag_container) TagLayout mTagLayout;
    List<String> mTagList = new ArrayList<>();
    private StanceAdapter mStanceAdapter;
    private GrenadeAdapter mGrenadeAdapter;
    private GridImageAdapter mImageAdapter;
    private TagAdapter mTagAdapter;
    private List<Stance> mStanceList = new ArrayList<>();
    private List<Grenade> mGrenadeList = new ArrayList<>();
    private List<Uri> mImageList = new ArrayList<>();
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

        mImageAdapter = new GridImageAdapter(mImageList, this);
        mImageAdapter.setOnAddPhotoListener(new OnAddPhotoListener());
        mImageGrid.setAdapter(mImageAdapter);
        mImageGrid.setExpanded(true);

        mTagList.add("one-way");
        mTagList.add("mid");
        mTagList.add("b-site");

        mTagAdapter = new TagAdapter(mTagList, this);
        mTagAdapter.setTagActionListener(this);
        mTagLayout.setAdapter(mTagAdapter);
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
        }
    }

    @Override
    public boolean onTagAdded(String name)
    {
        TransitionManager.beginDelayedTransition(mTagLayout);
        mTagList.add(name);
        mTagAdapter.notifyItemInserted(mTagList.size() - 1);

        return true;
    }

    @Override
    public void onTagRemoved(int position)
    {
        TransitionManager.beginDelayedTransition(mTagLayout);
        mTagList.remove(position);
        mTagAdapter.notifyItemRemoved(position);
    }

    private class OnAddPhotoListener implements View.OnClickListener
    {
        @Override
        public void onClick(View v)
        {
            if(mImageList.size() < MAX_IMAGE_COUNT)
            {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), IMAGE_REQUEST_CODE);
            }
            else
            {
                Toast.makeText(AddNadeActivity.this, String.format(Locale.getDefault(), "You can have at most %d images", MAX_IMAGE_COUNT), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
