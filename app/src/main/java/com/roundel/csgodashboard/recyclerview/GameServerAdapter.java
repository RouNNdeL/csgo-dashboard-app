package com.roundel.csgodashboard.recyclerview;

import android.support.transition.AutoTransition;
import android.support.transition.Transition;
import android.support.transition.TransitionManager;
import android.support.v4.view.animation.FastOutLinearInInterpolator;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.roundel.csgodashboard.R;
import com.roundel.csgodashboard.entities.GameServer;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by Krzysiek on 2017-01-23.
 */
public class GameServerAdapter extends RecyclerView.Adapter<GameServerAdapter.ViewHolder>
{
    private static final String TAG = GameServerAdapter.class.getSimpleName();

    private static final int TYPE_ITEM = 1;
    private static final int TYPE_REFRESH = 2;
    private List<GameServer> mDataSet = new ArrayList<>();
    private boolean refreshing;
    private View.OnClickListener mListener;
    private int mExpandedPosition = -1;
    private int mConnectingPosition = -1;
    private RecyclerView mRecyclerView;
    private String connectingStatusText = "";

    public GameServerAdapter(List<GameServer> data, View.OnClickListener listener)
    {
        mDataSet = data;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view;
        switch(viewType)
        {
            case TYPE_ITEM:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.game_server_row, parent, false);
                view.findViewById(R.id.game_server_connect).setOnClickListener(mListener);
                break;
            case TYPE_REFRESH:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.progress_48dp, parent, false);
                break;
            default:
                throw new IllegalArgumentException("viewType has to be either TYPE_REFRESH or TYPE_ITEM");
        }

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position)
    {
        if(holder.getItemViewType() == TYPE_ITEM)
        {
            View content = holder.getContent();
            TextView name = (TextView) content.findViewById(R.id.game_server_name);
            TextView host = (TextView) content.findViewById(R.id.game_server_host);

            final GameServer gameServer = mDataSet.get(position);
            name.setText(gameServer.getName());
            host.setText(String.format(Locale.getDefault(), "%s:%d", gameServer.getHost(), gameServer.getPort()));

            final boolean isExpanded = position == mExpandedPosition;

            content.findViewById(R.id.game_server_connect).setVisibility(isExpanded ? View.VISIBLE : View.GONE);
            content.setActivated(isExpanded);
            content.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    mExpandedPosition = isExpanded ? -1 : position;
                    Transition transition = new AutoTransition();
                    transition.setDuration(150);
                    transition.setInterpolator(new FastOutLinearInInterpolator());
                    TransitionManager.beginDelayedTransition(mRecyclerView, transition);
                    notifyDataSetChanged();
                }
            });
        }
    }

    @Override
    public int getItemCount()
    {
        if(mConnectingPosition == -1)
            return mDataSet.size() + (refreshing ? 1 : 0);
        else
            return 1;
    }

    @Override
    public int getItemViewType(int position)
    {
        if(position > mDataSet.size() - 1)
        {
            return TYPE_REFRESH;
        }
        else
        {
            return TYPE_ITEM;
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView)
    {
        super.onAttachedToRecyclerView(recyclerView);

        mRecyclerView = recyclerView;
    }

    public void setRefreshing(boolean refreshing)
    {
        this.refreshing = refreshing;
        if(refreshing)
            notifyItemInserted(getItemCount());
        else
            notifyItemRemoved(getItemCount());
    }

    public void setConnectingStatusText(String connectingStatusText)
    {
        this.connectingStatusText = connectingStatusText;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        private View content;

        public ViewHolder(View content)
        {
            super(content);
            this.content = content;
        }

        public View getContent()
        {
            return content;
        }
    }
}
