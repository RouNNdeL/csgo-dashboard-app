package com.roundel.csgodashboard.ui.activity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.transition.AutoTransition;
import android.support.transition.Transition;
import android.support.transition.TransitionManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.Toast;

import com.roundel.csgodashboard.R;
import com.roundel.csgodashboard.adapter.recyclerview.UtilityImagesAdapter;
import com.roundel.csgodashboard.adapter.spinner.GrenadeAdapter;
import com.roundel.csgodashboard.adapter.spinner.MapAdapter;
import com.roundel.csgodashboard.adapter.spinner.StanceAdapter;
import com.roundel.csgodashboard.db.DbHelper;
import com.roundel.csgodashboard.db.DbUtils;
import com.roundel.csgodashboard.entities.Map;
import com.roundel.csgodashboard.entities.utility.Grenade;
import com.roundel.csgodashboard.entities.utility.Stance;
import com.roundel.csgodashboard.entities.utility.Tags;
import com.roundel.csgodashboard.entities.utility.Utilities;
import com.roundel.csgodashboard.entities.utility.UtilityGrenade;
import com.roundel.csgodashboard.util.FileGenerator;
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
import java.util.Objects;

import butterknife.BindInt;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddEditNadeActivity extends AppCompatActivity implements TagAdapter.TagActionListener
{
    private static final String TAG = AddEditNadeActivity.class.getSimpleName();

    public static final String EXTRA_GRENADE_ID = "com.roundel.csgodashboard.extra.GRENADE_ID";

    private static final int IMAGE_REQUEST_CODE = 1237;
    private static final int MAX_IMAGE_COUNT = 500;

    //<editor-fold desc="private variables">
    @BindView(R.id.add_nade_toolbar) Toolbar mToolbar;
    @BindView(R.id.add_nade_spinner_grenade) Spinner mGrenadeSpinner;
    @BindView(R.id.add_nade_spinner_stance) Spinner mStanceSpinner;
    @BindView(R.id.add_nade_spinner_map) Spinner mMapSpinner;
    @BindView(R.id.add_nade_image_recyclerview) RecyclerView mImageRecyclerView;
    @BindView(R.id.add_nade_tag_container) TagLayout mTagLayout;
    @BindView(R.id.add_nade_title) TextInputEditText mTitleEditText;
    @BindView(R.id.add_nade_title_container) TextInputLayout mTitleContainer;
    @BindView(R.id.add_nade_description) TextInputEditText mDescriptionEditText;
    @BindView(R.id.add_nade_description_container) TextInputLayout mDescriptionContainer;
    @BindView(R.id.add_nade_checkbox_jumpthrow) CheckBox mJumpthrowCheckbox;

    @BindInt(R.integer.tag_add_transition_duration) int mTagAddTransitionDuration;
    @BindInt(R.integer.tag_remove_transition_duration) int mTagRemoveTransitionDuration;

    private StanceAdapter mStanceAdapter;
    private GrenadeAdapter mGrenadeAdapter;
    private UtilityImagesAdapter mImageAdapter;
    private TagAdapter mTagAdapter;

    private LinearLayoutManager mImageLayoutManager;

    private List<Stance> mStanceList = new ArrayList<>();
    private List<Grenade> mGrenadeList = new ArrayList<>();
    private List<Uri> mImageList = new ArrayList<>();
    private List<Uri> mNewImageList = new ArrayList<>();
    private Tags mTags = new Tags();

    private int mMapCount;

    private MapAdapter mMapAdapter;

    private boolean mIsInEditMode;
    private UtilityGrenade mUtilityData;
    private int mUtilityId = -1;

    private DbHelper mDbHelper;
    private SQLiteDatabase mReadableDatabase;
    private SQLiteDatabase mWritableDatabase;
    //</editor-fold>

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_nade);

        ButterKnife.bind(this);

        mIsInEditMode = Objects.equals(Intent.ACTION_EDIT, getIntent().getAction());

        setSupportActionBar(mToolbar);

        if(getSupportActionBar() != null)
        {
            getSupportActionBar().setTitle(mIsInEditMode ? "Edit grenade" : "Add grenade");
        }

        mDbHelper = new DbHelper(this);
        mReadableDatabase = mDbHelper.getReadableDatabase();
        mWritableDatabase = mDbHelper.getWritableDatabase();

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

        final Cursor queryMaps = DbUtils.queryMaps(
                this.mReadableDatabase,
                new String[]{Map._ID, Map.COLUMN_NAME_NAME}
        );
        mMapCount = queryMaps.getCount();
        mMapAdapter = new MapAdapter(this, queryMaps);
        mMapSpinner.setAdapter(mMapAdapter);
        mMapSpinner.setOnItemSelectedListener(new OnMapSelectedListener());

        mImageAdapter = new UtilityImagesAdapter(mImageList, this);
        mImageAdapter.setOnAddPhotoListener(new OnAddPhotoListener());
        mImageLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mImageRecyclerView.setAdapter(mImageAdapter);
        mImageRecyclerView.setLayoutManager(mImageLayoutManager);

        mTagAdapter = new TagAdapter(mTags, this);
        mTagAdapter.setTagActionListener(this);
        mTagLayout.setAdapter(mTagAdapter);

        if(mIsInEditMode)
        {
            mUtilityId = getIntent().getIntExtra(EXTRA_GRENADE_ID, -1);
            mUtilityData = DbUtils.queryGrenadeById(mReadableDatabase, mUtilityId);
            if(mUtilityData == null)
                throw new RuntimeException("You need to provide a valid utility_grenade_id in integer extra " + EXTRA_GRENADE_ID);
            fillForEdit(mUtilityData);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_add_nade, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch(item.getItemId())
        {
            case R.id.menu_add_nade_done:
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
            mNewImageList.add(uri);
            mImageAdapter.notifyDataSetChanged();
            mImageLayoutManager.scrollToPosition(mImageList.size());
        }
    }

    @Override
    public boolean onTagAdded(String name)
    {
        Transition transition = new AutoTransition();
        transition.setDuration(mTagAddTransitionDuration);
        TransitionManager.beginDelayedTransition(mTagLayout, transition);

        if(!mTags.contains(name))
        {
            mTags.add(new Tags.Tag(name));
            mTagAdapter.notifyItemInserted(mTags.size() - 1);
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

        mTags.remove(position);
        mTagAdapter.notifyItemRemoved(position);
    }

    @Override
    public void onBackPressed()
    {
        if(anyData() && (anyChanges() || !mIsInEditMode))
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            final String message = mIsInEditMode ?
                    "Are you sure you want to discard this grenade?" :
                    "Are you sure you want to discard your changes?";
            builder.setMessage(message)
                    .setPositiveButton("Keep editing", (dialog, which) -> dialog.dismiss())
                    .setNegativeButton("Discard", ((dialog, which) -> super.onBackPressed()));
            builder.show();
        }
        else
        {
            super.onBackPressed();
        }
    }

    private void fillForEdit(UtilityGrenade utilityGrenade)
    {
        mTitleEditText.setText(utilityGrenade.getTitle());
        mDescriptionEditText.setText(utilityGrenade.getDescription());
        mMapSpinner.setSelection(mMapAdapter.getItemPosition(utilityGrenade.getMap().getId()));
        mGrenadeSpinner.setSelection(mGrenadeAdapter.getItemPosition(utilityGrenade.getGrenadeId()));
        mStanceSpinner.setSelection(mStanceAdapter.getItemPosition(utilityGrenade.getStance()));
        mJumpthrowCheckbox.setChecked(utilityGrenade.isJumpThrow());
        mImageList.addAll(utilityGrenade.getImgUris(this));
        mTags.addAll(utilityGrenade.getTags());

        mTagAdapter.notifyDataSetChanged();
        mImageAdapter.notifyDataSetChanged();
    }

    private void submit()
    {
        final UtilityGrenade utilityGrenade = validate();
        if(utilityGrenade != null)
        {
            ArrayList<String> copiedIds = mIsInEditMode ?
                    new ArrayList<>(mUtilityData.getImageIds()) :
                    new ArrayList<>();

            for(Uri uri : mNewImageList)
            {
                try
                {
                    copiedIds.add(copyFromUri(uri));
                }
                catch(IOException e)
                {
                    e.printStackTrace();
                }
            }

            //Update the Ids to point to the new location in the App's memory
            utilityGrenade.setImageIds(copiedIds);

            if(mIsInEditMode)
            {
                DbUtils.updateGrenade(mWritableDatabase, utilityGrenade, mUtilityId);
            }
            else
            {
                DbUtils.insertGrenade(mWritableDatabase, utilityGrenade);
            }
            finish();

        }
    }

    /**
     * @return {@code null} if the form is improperly filled, {@link UtilityGrenade} object
     * containing the form's data if it is properly filled
     */
    private UtilityGrenade validate()
    {
        boolean valid = true;

        int mapId = (int) mMapSpinner.getSelectedItemId();
        int stanceId = (int) mStanceSpinner.getSelectedItemId();
        int grenadeId = (int) mGrenadeSpinner.getSelectedItemId();

        boolean isJumpthrow = mJumpthrowCheckbox.isChecked();

        String title = mTitleEditText.getText().toString().replaceAll("^\\s+|\\s+$", "");
        String description = mDescriptionEditText.getText().toString().replaceAll("^\\s+|\\s+$", "");

        if(title.length() == 0)
        {
            mTitleContainer.setErrorEnabled(true);
            mTitleContainer.setError("Title is required");
            valid = false;
        }
        else
        {
            mTitleContainer.setErrorEnabled(false);
        }

        if(mImageList.size() == 0)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Required fields")
                    .setMessage("You need to chose at least one image to add your grenade")
                    .setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
            builder.show();
            valid = false;
        }

        if(valid)
        {
            //We pass null as the imageIds param because it is going to be updated
            // when we copy the Uris into the App's memory
            return new UtilityGrenade(null, mTags, Map.referenceOnly(mapId), title, description, grenadeId, stanceId, isJumpthrow);
        }
        else
        {
            return null;
        }
    }

    private boolean anyData()
    {
        return mTitleEditText.length() > 0 ||
                mDescriptionEditText.length() > 0 ||
                mImageList.size() > 0 ||
                mTags.size() > 0;
    }

    private boolean anyChanges()
    {
        if(mIsInEditMode)
        {
            final UtilityGrenade validate = validate();
            return !Objects.equals(mUtilityData, validate);
        }
        return false;
    }

    /**
     * @param uri data received from {@link #onActivityResult}
     *
     * @return id of the copied image
     * @throws IOException
     */
    private String copyFromUri(Uri uri) throws IOException
    {
        OutputStream out;

        InputStream inputStream = getContentResolver().openInputStream(uri);
        final byte[] data = getBytes(inputStream);
        if(inputStream != null)
        {
            inputStream.close();
        }

        File file = FileGenerator.createRandomFile("", "", new File(Utilities.getImgPath(this)));
        out = new FileOutputStream(file);

        out.write(data);
        out.close();

        return file.getName();
    }

    private byte[] getBytes(InputStream inputStream) throws IOException
    {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len;
        while((len = inputStream.read(buffer)) != -1)
        {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }

    @OnClick(R.id.add_nade_hitbox_jumpthrow)
    void onClick(View v)
    {
        mJumpthrowCheckbox.setChecked(!mJumpthrowCheckbox.isChecked());
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
                Toast.makeText(AddEditNadeActivity.this, String.format(Locale.getDefault(), "You can have at most %d images", MAX_IMAGE_COUNT), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class OnMapSelectedListener implements AdapterView.OnItemSelectedListener
    {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
        {
            if(position >= mMapCount)
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
