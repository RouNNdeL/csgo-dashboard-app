package com.roundel.csgodashboard.ui;

import android.content.Context;
import android.graphics.Typeface;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.transition.AutoTransition;
import android.support.transition.Transition;
import android.support.transition.TransitionManager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.paolorotolo.appintro.ISlidePolicy;
import com.roundel.csgodashboard.R;
import com.roundel.csgodashboard.SlideAction;
import com.roundel.csgodashboard.entities.GameServer;
import com.roundel.csgodashboard.recyclerview.GameServerAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


import static android.view.View.OVER_SCROLL_ALWAYS;
import static android.view.View.OVER_SCROLL_NEVER;

/**
 * Created by Krzysiek on 2017-01-23.
 */
public class ServerSearchSlide extends SlideBase implements View.OnClickListener, ISlidePolicy
{
    public static final String TAG = "ServerSearchSlide";

    private static final String ARG_LAYOUT_RES_ID = "layoutResId";
    private int layoutResId;
    private GameServerAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private TextView mTitle;
    private CardView mCardView;
    //private LinearLayout mConnectingStatus;

    private boolean canContinue = false;
    private boolean connectingToServer;

    private List<GameServer> gameServers = new ArrayList<>();
    private SlideAction mSlideActionInterface;
    private ServerConnectionInfo mServerConnectionInfoInterface;
    private ViewGroup root;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);
        root = getRoot();

        mTitle = (TextView) root.findViewById(R.id.setup_connecting_title);

        setTitleSearchingWifi();

        mCardView = (CardView) root.findViewById(R.id.setup_server_search_cardview);

        //mConnectingStatus = (LinearLayout) root.findViewById(R.id.setup_server_search_connecting);

        gameServers.add(new GameServer("TZC", "192.168.1.123", 3000));
        gameServers.add(new GameServer("PC", "192.168.1.76", 3000));

        mAdapter = new GameServerAdapter(gameServers, this);
        mAdapter.setRefreshing(true);

        mLayoutManager = new LinearLayoutManager(getContext());

        mRecyclerView = (RecyclerView) root.findViewById(R.id.setup_server_search_recyclerview);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(mLayoutManager);
        //mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));

        return root;
    }

    @Override
    public void onClick(View v)
    {
        if(v.getId() == R.id.game_server_connect)
        {
            View root = (View) v.getParent();
            mAdapter.setRefreshing(!mAdapter.isRefreshing());

            canContinue = true;
            connectingToServer = true;

            animateConnecting(mRecyclerView.getChildLayoutPosition(root));

            if(mServerConnectionInfoInterface != null)
                mServerConnectionInfoInterface.onConnecting();

            if(mSlideActionInterface != null)
            {
                new Handler().postDelayed(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        if(mSlideActionInterface != null)
                            mSlideActionInterface.onNextPageRequested(getParentFragment());
                    }
                }, 500);
            }
        }
    }

    @Override
    public boolean isPolicyRespected()
    {
        return canContinue;
    }

    @Override
    public void onUserIllegallyRequestedNextPage()
    {

    }

    public static ServerSearchSlide newInstance(int layoutResId)
    {
        ServerSearchSlide sampleSlide = new ServerSearchSlide();

        Bundle args = new Bundle();
        args.putInt(ARG_LAYOUT_RES_ID, layoutResId);
        sampleSlide.setArguments(args);

        return sampleSlide;
    }

    public void attachServerConnectionInfoInterface(ServerConnectionInfo serverConnectionInfo)
    {
        this.mServerConnectionInfoInterface = serverConnectionInfo;
    }

    public void addServer(GameServer server)
    {
        gameServers.add(server);
        mAdapter.notifyItemInserted(gameServers.size() - 1);
    }

    private void animateConnecting(int position)
    {
        final int cardMargin = (int) getResources().getDimension(R.dimen.setup_search_server_cardview_margin);
        final int recyclerViewHeight = (int) getResources().getDimension(R.dimen.setup_search_server_recyclerview_height);

        final LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        final FrameLayout.LayoutParams recyclerViewParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);

        cardParams.setMargins(0, cardMargin, 0, cardMargin);

        mCardView.setLayoutParams(cardParams);
        mRecyclerView.setLayoutParams(recyclerViewParams);
        mRecyclerView.setOverScrollMode(OVER_SCROLL_NEVER);

        setTitleConnecting(position);

        mAdapter.expandWhenConnecting(position);

        Transition transition = new AutoTransition();
        transition.setDuration(200);
        TransitionManager.beginDelayedTransition(root);
    }

    private void reverseAnimateConnecting()
    {
        final int cardMargin = (int) getResources().getDimension(R.dimen.setup_search_server_cardview_margin);
        final int recyclerViewHeight = (int) getResources().getDimension(R.dimen.setup_search_server_recyclerview_height);

        final LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        final FrameLayout.LayoutParams recyclerViewParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, recyclerViewHeight);

        cardParams.setMargins(cardMargin, cardMargin, cardMargin, cardMargin);

        mCardView.setLayoutParams(cardParams);
        mRecyclerView.setLayoutParams(recyclerViewParams);
        mRecyclerView.setOverScrollMode(OVER_SCROLL_ALWAYS);

        setTitleSearchingWifi();

        mAdapter.collapseWhenConnecting();

        Transition transition = new AutoTransition();
        transition.setDuration(200);
        TransitionManager.beginDelayedTransition(root, transition);
    }

    private void setTitleSearchingWifi()
    {
        WifiManager wifiManager = (WifiManager) getContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifiManager.getConnectionInfo();
        final String ssid = info.getSSID();
        /*if(ssid.charAt(0) == '"' && ssid.charAt(ssid.length()-1) == '"')
            ssid = ssid.substring(1,ssid.length()-1);*/

        final String title = String.format(Locale.getDefault(), getString(R.string.setup_server_searching), ssid);

        int start;
        int end;
        if(ssid.charAt(0) == '"' && ssid.charAt(ssid.length() - 1) == '"')
        {
            start = title.indexOf(ssid) + 1;
            end = title.indexOf(ssid) + ssid.length() - 1;
        }
        else
        {
            start = title.indexOf(ssid);
            end = title.indexOf(ssid) + ssid.length();
        }
        Spannable spannableTitle = new SpannableString(title);
        spannableTitle.setSpan(
                new StyleSpan(Typeface.BOLD),
                start,                        //Start
                end,         //End
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        );
        mTitle.setText(spannableTitle);
    }

    public void cancelConnectingProcess()
    {
        connectingToServer = false;
        //TODO: Kill a Process handling connection;
    }

    public boolean isConnectingToServer()
    {
        return connectingToServer;
    }

    private void setTitleConnecting(int gameServerPosition)
    {
        final String name = gameServers.get(gameServerPosition).getName();
        final String format = String.format(Locale.getDefault(), "Connecting to \"%s\"...", name);
        int start = format.indexOf("\"" + name + "\"") + 1;
        int end = start + name.length();

        Spannable spannableTitle = new SpannableString(format);
        spannableTitle.setSpan(
                new StyleSpan(Typeface.BOLD),
                start,
                end,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        );

        mTitle.setText(spannableTitle);
    }

    public interface ServerConnectionInfo
    {
        void onConnected();

        void onConnecting();

        void onFailed();
    }
}
