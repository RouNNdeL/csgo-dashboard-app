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

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Krzysiek on 2017-03-04.
 */
public class UtilityGrenadeFragment extends Fragment
{
    private static final String TAG = UtilityGrenadeFragment.class.getSimpleName();

    //<editor-fold desc="private variables">
    @BindView(R.id.fragment_utility_grenade_recycler_view) RecyclerView mRecyclerView;

    private UtilityGrenadeAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;

    private DbHelper mDbHelper;
    private SQLiteDatabase mReadableDataBase;
    //</editor-fold>

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

        mAdapter = new UtilityGrenadeAdapter(getContext(), DbUtils.queryGrenades(mReadableDataBase));
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(mLayoutManager);
        return root;
    }

    private void viewNade(int position)
    {

    }
}
