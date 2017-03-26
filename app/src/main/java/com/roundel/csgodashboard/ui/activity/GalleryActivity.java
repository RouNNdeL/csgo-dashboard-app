package com.roundel.csgodashboard.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.transition.Fade;
import android.support.transition.TransitionManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.alexvasilkov.gestures.GestureController;
import com.alexvasilkov.gestures.views.GestureImageView;
import com.bumptech.glide.Glide;
import com.roundel.csgodashboard.R;
import com.roundel.csgodashboard.util.ListUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class GalleryActivity extends AppCompatActivity
{
    public static final String EXTRA_PHOTO_URIS = "com.roundel.csgodashboard.extra.EXTRA_PHOTO_URIS";

    /**
     * Whether or not the system UI should be auto-hidden after
     * {@value #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    private final Handler mHideHandler = new Handler();
    private final DetailOnPageChangeListener mOnPageChangeListener = new DetailOnPageChangeListener();
    @BindView(R.id.gallery_viewpager) ViewPager mViewPager;
    @BindView(R.id.gallery_content_controls) View mControlsView;
    @BindView(R.id.gallery_root) FrameLayout mRootView;
    @BindView(R.id.gallery_action_bar) AppBarLayout mAppBar;
    @BindView(R.id.gallery_toolbar) Toolbar mToolbar;
    private boolean mVisible;
    private final GestureController.OnGestureListener mPhotoGestureListener = new GestureController.OnGestureListener()
    {
        @Override
        public void onDown(@NonNull MotionEvent event)
        {

        }

        @Override
        public void onUpOrCancel(@NonNull MotionEvent event)
        {

        }

        @Override
        public boolean onSingleTapUp(@NonNull MotionEvent event)
        {
            return false;
        }

        @Override
        public boolean onSingleTapConfirmed(@NonNull MotionEvent event)
        {
            toggle();
            return true;
        }

        @Override
        public void onLongPress(@NonNull MotionEvent event)
        {

        }

        @Override
        public boolean onDoubleTap(@NonNull MotionEvent event)
        {
            return false;
        }
    };
    private final Runnable mHideRunnable = this::hide;
    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private final View.OnClickListener mDelayHideTouchListener = (view) ->
    {
        if(AUTO_HIDE)
        {
            delayedHide(AUTO_HIDE_DELAY_MILLIS);
        }
    };
    private List<Uri> mImgUris;
    private PhotoViewPagerAdapter mViewPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_gallery);

        ButterKnife.bind(this);

        mAppBar.setOutlineProvider(null);
        setSupportActionBar(mToolbar);

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null)
        {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        mVisible = true;

        Intent intent = getIntent();
        mImgUris = ListUtils.splitUris(intent.getStringExtra(EXTRA_PHOTO_URIS));
        // Set up the user interaction to manually show or hide the system UI.
        mViewPagerAdapter = new PhotoViewPagerAdapter(this, mImgUris, mPhotoGestureListener);
        mViewPager.setAdapter(mViewPagerAdapter);
        mViewPager.addOnPageChangeListener(mOnPageChangeListener);

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        //findViewById(R.id.dummy_button).setOnTouchListener(mDelayHideTouchListener);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState)
    {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch(item.getItemId())
        {
            case android.R.id.home:
                //NavUtils.navigateUpFromSameTask(this);
                onBackPressed();
                return true;
            case R.id.menu_gallery_external:
            {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                final int position = mOnPageChangeListener.getCurrentPage();
                intent.setDataAndType(mImgUris.get(position), "image/*");
                startActivity(Intent.createChooser(intent, "Open image"));
                break;
            }
            case R.id.menu_gallery_share:
            {
                Intent intent = new Intent(Intent.ACTION_SEND);
                final int position = mOnPageChangeListener.getCurrentPage();
                intent.setDataAndType(mImgUris.get(position), "image/*");
                startActivity(Intent.createChooser(intent, "Share image"));
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = new MenuInflater(this);
        inflater.inflate(R.menu.menu_gallery, menu);
        return true;
    }

    private void toggle()
    {
        if(mVisible)
        {
            hide();
        }
        else
        {
            show();
        }
    }

    private void hide()
    {
        mViewPager.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

        ActionBar actionBar = getSupportActionBar();
        final Fade transition = new Fade();
        transition.excludeTarget(R.id.gallery_action_bar, true);
        TransitionManager.beginDelayedTransition(mRootView, transition);
        if(actionBar != null)
        {
            actionBar.hide();
        }
        mControlsView.setVisibility(View.GONE);
        mVisible = false;
    }

    private void show()
    {
        TransitionManager.beginDelayedTransition(mRootView, new Fade());
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null)
        {
            actionBar.show();
        }
        mControlsView.setVisibility(View.VISIBLE);

        // Show the system bar
        mViewPager.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;
    }

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis)
    {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

    public class DetailOnPageChangeListener extends ViewPager.SimpleOnPageChangeListener
    {

        private int currentPage;

        @Override
        public void onPageSelected(int position)
        {
            currentPage = position;
        }

        public final int getCurrentPage()
        {
            return currentPage;
        }
    }

    private class PhotoViewPagerAdapter extends PagerAdapter
    {
        private Context mContext;
        private List<Uri> mPhotoUris;
        private GestureController.OnGestureListener mOnGestureListener;
        private LayoutInflater mInflater;

        public PhotoViewPagerAdapter(Context context, List<Uri> mPhotoUris, GestureController.OnGestureListener onPhotoGestureListener)
        {
            this.mContext = context;
            this.mPhotoUris = mPhotoUris;
            this.mOnGestureListener = onPhotoGestureListener;
            this.mInflater = LayoutInflater.from(context);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position)
        {
            View view = mInflater.inflate(R.layout.fragment_photo, container, false);

            GestureImageView photoView = (GestureImageView) view.findViewById(R.id.photo_view);
            photoView.getController().enableScrollInViewPager(mViewPager);
            photoView.getController().setOnGesturesListener(mOnGestureListener);
            photoView.getController().getSettings().setMaxZoom(4f).setFillViewport(true);

            Glide.with(mContext).load(mImgUris.get(position)).into(photoView);
            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object)
        {
            container.removeView((FrameLayout) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object)
        {
            return view == object;
        }

        @Override
        public int getCount()
        {
            return mPhotoUris.size();
        }
    }
}
