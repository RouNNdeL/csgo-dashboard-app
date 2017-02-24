package com.roundel.csgodashboard.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.transition.AutoTransition;
import android.support.transition.Transition;
import android.support.transition.TransitionManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.roundel.csgodashboard.R;
import com.roundel.csgodashboard.adapter.GrenadeAdapter;
import com.roundel.csgodashboard.adapter.GridImageAdapter;
import com.roundel.csgodashboard.adapter.StanceAdapter;
import com.roundel.csgodashboard.db.DbHelper;
import com.roundel.csgodashboard.db.DbUtils;
import com.roundel.csgodashboard.entities.Map;
import com.roundel.csgodashboard.entities.Maps;
import com.roundel.csgodashboard.entities.UserData;
import com.roundel.csgodashboard.entities.utility.Grenade;
import com.roundel.csgodashboard.entities.utility.Stance;
import com.roundel.csgodashboard.entities.utility.Utilities;
import com.roundel.csgodashboard.util.FileGenerator;
import com.roundel.csgodashboard.util.LogHelper;
import com.roundel.csgodashboard.view.ExpandableHeightGridView;
import com.roundel.csgodashboard.view.taglayout.TagAdapter;
import com.roundel.csgodashboard.view.taglayout.TagLayout;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindInt;
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
    @BindView(R.id.add_nade_spinner_map) Spinner mMapSpinner;
    @BindView(R.id.add_nade_image_grid) ExpandableHeightGridView mImageGrid;
    @BindView(R.id.add_nade_tag_container) TagLayout mTagLayout;

    @BindInt(R.integer.tag_add_transition_duration) int mTagAddTransitionDuration;
    @BindInt(R.integer.tag_remove_transition_duration) int mTagRemoveTransitionDuration;

    private StanceAdapter mStanceAdapter;
    private GrenadeAdapter mGrenadeAdapter;
    private GridImageAdapter mImageAdapter;
    private TagAdapter mTagAdapter;

    private List<Stance> mStanceList = new ArrayList<>();
    private List<Grenade> mGrenadeList = new ArrayList<>();
    private List<Uri> mImageList = new ArrayList<>();
    private List<String> mTagList = new ArrayList<>();

    private UserData mUserData;
    private Maps mUserDataMaps;
    private SimpleCursorAdapter mMapAdapter;

    private DbHelper mDbHelper;
    //</editor-fold>

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_nade);

        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);

        getSupportActionBar().setTitle("Add nade");

        mUserData = UserData.fromContext(this);
        mUserDataMaps = mUserData.getMaps();

        mDbHelper = new DbHelper(this);

        mStanceList = Stance.getDefaultStanceList(this);
        mStanceAdapter = new StanceAdapter(
                this,
                R.layout.list_icon_two_line_no_ripple,
                R.id.list_text_primary, mStanceList
        );
        mStanceSpinner.setAdapter(mStanceAdapter);

        mGrenadeList = Grenade.getDefaultGrenadeList(this);
        mGrenadeAdapter = new GrenadeAdapter(
                this,
                R.layout.list_icon_one_line_no_ripple,
                R.id.list_text_primary, mGrenadeList
        );
        mGrenadeSpinner.setAdapter(mGrenadeAdapter);

        mMapAdapter = new SimpleCursorAdapter(
                this,
                R.layout.list_simple_one_line_no_ripple,
                DbUtils.queryMaps(
                        mDbHelper.getReadableDatabase(),
                        new String[]{Map._ID, Map.COLUMN_NAME_NAME}
                ),
                new String[]{Map.COLUMN_NAME_NAME},
                new int[]{R.id.list_text_primary},
                CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER
        );
        mMapSpinner.setAdapter(mMapAdapter);
        mMapSpinner.setOnItemSelectedListener(new OnMapSelectedListener());

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
                submit();
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
        Transition transition = new AutoTransition();
        transition.setDuration(mTagAddTransitionDuration);
        TransitionManager.beginDelayedTransition(mTagLayout, transition);

        if(!mTagList.contains(name))
        {
            mTagList.add(name);
            mTagAdapter.notifyItemInserted(mTagList.size() - 1);
            return true;
        }
        else
        {
            Toast.makeText(this, "Tags have to be unique", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    @Override
    public void onTagRemoved(int position)
    {
        Transition transition = new AutoTransition();
        transition.setDuration(mTagRemoveTransitionDuration);
        TransitionManager.beginDelayedTransition(mTagLayout, transition);

        mTagList.remove(position);
        mTagAdapter.notifyItemRemoved(position);
    }

    private void submit()
    {
        if(verify())
        {
            List<Uri> copiedPaths = new ArrayList<>();
            for(Uri uri : mImageList)
            {
                try
                {
                    copiedPaths.add(copyFromUri(uri));
                }
                catch(IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * @return true if the form is properly filled
     */
    private boolean verify()
    {

        return true;
    }

    /**
     * @param uri data received from {@link #onActivityResult}
     *
     * @return {@link Uri} of the new file created in the external storage
     * @throws IOException
     */
    private Uri copyFromUri(Uri uri) throws IOException
    {
        OutputStream out;
        String root = getExternalFilesDir(null).getAbsolutePath() + File.separator;

        LogHelper.d(TAG, root);
        InputStream inputStream = getContentResolver().openInputStream(uri);
        final byte[] data = getBytes(inputStream);

        File file = FileGenerator.createRandomFile("", "", new File(root + Utilities.IMAGE_FOLDER_NAME));
        out = new FileOutputStream(file);

        out.write(data);
        out.close();

        return Uri.parse(file.getAbsolutePath());
    }

    public byte[] getBytes(InputStream inputStream) throws IOException
    {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while((len = inputStream.read(buffer)) != -1)
        {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
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

    private class OnMapSelectedListener implements AdapterView.OnItemSelectedListener
    {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
        {
            if(position >= mUserDataMaps.size())
            {
                //TODO: Start a AddMapActivity
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent)
        {

        }
    }
}
