package com.roundel.csgodashboard.ui.fragment;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.roundel.csgodashboard.R;
import com.roundel.csgodashboard.adapter.recyclerview.UtilityGrenadeAdapter;
import com.roundel.csgodashboard.db.DbHelper;
import com.roundel.csgodashboard.db.DbUtils;
import com.roundel.csgodashboard.entities.utility.FilterGrenade;
import com.roundel.csgodashboard.ui.OnViewUtilityListener;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Krzysiek on 2017-03-04.
 */
public class UtilityGrenadeFragment extends Fragment implements View.OnClickListener
{
    private static final String TAG = UtilityGrenadeFragment.class.getSimpleName();

    @BindView(R.id.fragment_utility_grenade_recycler_view) RecyclerView mRecyclerView;

    private UtilityGrenadeAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;

    private DbHelper mDbHelper;
    private SQLiteDatabase mReadableDataBase;

    private OnViewUtilityListener mOnViewUtilityListenerListener;

    public UtilityGrenadeFragment()
    {
    }

    public static UtilityGrenadeFragment newInstance()
    {
        UtilityGrenadeFragment fragment = new UtilityGrenadeFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View root = inflater.inflate(R.layout.fragment_utility_grenade_list, container, false);

        ButterKnife.bind(this, root);

        mDbHelper = new DbHelper(getContext());
        mReadableDataBase = mDbHelper.getReadableDatabase();

        mAdapter = new UtilityGrenadeAdapter(
                getContext(),
                DbUtils.queryGrenades(mReadableDataBase),
                DbUtils.queryTagsForGrenades(mReadableDataBase)
        );
        mAdapter.setOnItemClickListener(this);

        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(mLayoutManager);

        return root;
    }

    @Override
    public void onClick(View v)
    {
        final int position = mRecyclerView.getChildAdapterPosition(v);
        if(position <= -1)
            throw new IllegalStateException("Child is not part of the RecyclerView");

        viewNade(position);
    }

    public void attachViewUtilityListener(OnViewUtilityListener listener)
    {
        this.mOnViewUtilityListenerListener = listener;
    }

    public void updateData(FilterGrenade newFilter)
    {
        DbUtils.Query query = DbUtils.buildQueryFromGrenadeFilter(mReadableDataBase, newFilter);
        mAdapter.setHighlight(true);
        mAdapter.setHighlightText(newFilter.getSearchQuery());
        mAdapter.swapData(
                DbUtils.queryGrenades(mReadableDataBase, query.selection, query.selectionArgs),
                DbUtils.queryTagsForGrenades(mReadableDataBase, query.selection, query.selectionArgs)
        );
    }

    private void viewNade(int position)
    {
        if(mOnViewUtilityListenerListener != null)
            mOnViewUtilityListenerListener.onViewGrenade(mAdapter.getItemId(position));
    }
}
