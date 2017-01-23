package com.roundel.csgodashboard.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.roundel.csgodashboard.GameServer;
import com.roundel.csgodashboard.R;
import com.roundel.csgodashboard.recyclerview.GameServerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Krzysiek on 2017-01-23.
 */
public class ServerSearchSlide extends Fragment implements View.OnClickListener
{
    public static final String TAG = "ServerSearchSlide";

    private static final String ARG_LAYOUT_RES_ID = "layoutResId";
    private int layoutResId;
    private GameServerAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;

    private List<GameServer> gameServers = new ArrayList<>();

    public static ServerSearchSlide newInstance(int layoutResId) {
        ServerSearchSlide sampleSlide = new ServerSearchSlide();

        Bundle args = new Bundle();
        args.putInt(ARG_LAYOUT_RES_ID, layoutResId);
        sampleSlide.setArguments(args);

        return sampleSlide;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null && getArguments().containsKey(ARG_LAYOUT_RES_ID)) {
            layoutResId = getArguments().getInt(ARG_LAYOUT_RES_ID);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(layoutResId, container, false);

        gameServers.add(new GameServer("TZC", "192.168.1.123", 3000));
        gameServers.add(new GameServer("PC", "192.168.1.76", 3000));

        mAdapter = new GameServerAdapter(gameServers, this);
        mAdapter.setRefreshing(true);

        mLayoutManager = new LinearLayoutManager(getContext());

        mRecyclerView = (RecyclerView) view.findViewById(R.id.setup_connecting_recyclerview);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(mLayoutManager);
        //mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));

        return view;
    }

    public void addServer(GameServer server)
    {
        gameServers.add(server);
        mAdapter.notifyItemInserted(gameServers.size()-1);
    }

    @Override
    public void onClick(View v)
    {
        if(v.getId() == R.id.game_server_row)
        {
            mAdapter.setRefreshing(!mAdapter.isRefreshing());
        }
    }
}
