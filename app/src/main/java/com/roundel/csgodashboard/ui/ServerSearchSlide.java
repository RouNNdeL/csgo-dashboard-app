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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.paolorotolo.appintro.ISlidePolicy;
import com.roundel.csgodashboard.R;
import com.roundel.csgodashboard.ServerCommunicationThread;
import com.roundel.csgodashboard.ServerDiscoveryThread;
import com.roundel.csgodashboard.SlideAction;
import com.roundel.csgodashboard.entities.GameServer;
import com.roundel.csgodashboard.recyclerview.GameServerAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;


import static android.view.View.OVER_SCROLL_ALWAYS;
import static android.view.View.OVER_SCROLL_NEVER;

/**
 * Created by Krzysiek on 2017-01-23.
 */
public class ServerSearchSlide extends SlideBase implements View.OnClickListener, ISlidePolicy, ServerDiscoveryThread.ServerDiscoveryListener
{
    public static final String TAG = "ServerSearchSlide";

    private static final String ARG_LAYOUT_RES_ID = "layoutResId";
    private int layoutResId;
    private GameServerAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private TextView mTitle;
    private CardView mCardView;
    private Button mRefreshButton;

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

        mRefreshButton = (Button) root.findViewById(R.id.setup_server_search_refresh);
        mRefreshButton.setOnClickListener(this);

        mCardView = (CardView) root.findViewById(R.id.setup_server_search_cardview);

        mAdapter = new GameServerAdapter(gameServers, this);

        mLayoutManager = new LinearLayoutManager(getContext());

        mRecyclerView = (RecyclerView) root.findViewById(R.id.setup_server_search_recyclerview);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(mLayoutManager);

        return root;
    }

    @Override
    public void onClick(View v)
    {
        if(v.getId() == R.id.game_server_connect)
        {
            View root = (View) v.getParent();
            final int position = mRecyclerView.getChildLayoutPosition(root);

            mAdapter.setRefreshing(!mAdapter.isRefreshing());

            connectingToServer = true;


            animateConnecting(position);

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

            final GameServer selectedGameServer = gameServers.get(position);
            attemptConnection(selectedGameServer);
        }
        else if(v.getId() == R.id.setup_server_search_refresh)
        {
            startDiscovery();
        }
    }

    @Override
    public void onServerFound(final GameServer server)
    {
        Log.d("ServerDiscoveryThread", server.getHost() + ":" + server.getPort());
        try
        {
            getActivity().runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    addServer(server);
                }
            });
        }
        catch(NullPointerException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onSocketOpened()
    {
        try
        {
            getActivity().runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    mAdapter.setRefreshing(true);
                }
            });
        }
        catch(NullPointerException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onSocketClosed()
    {
        try
        {
            getActivity().runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    mAdapter.setRefreshing(false);
                }
            });
        }
        catch(NullPointerException e)
        {
            e.printStackTrace();
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
        for(GameServer gameServer: gameServers)
        {
            if(Objects.equals(gameServer.getHost(), server.getHost()))
                return;
        }
        gameServers.add(server);
        mAdapter.notifyItemInserted(gameServers.size() - 1);
    }

    public void startDiscovery()
    {
        ServerDiscoveryThread serverDiscoveryThread = new ServerDiscoveryThread(this);
        serverDiscoveryThread.setDiscoveryTimeout(2500);
        serverDiscoveryThread.start();
    }

    private void attemptConnection(GameServer server)
    {
        ServerCommunicationThread sendingThread = new ServerCommunicationThread(server, ServerCommunicationThread.MODE_CONNECT, "6000");
        sendingThread.setConnectionListener(new ServerCommunicationThread.ServerConnectionListener()
        {
            @Override
            public void onAccessGranted()
            {
                onConnectionSuccessful();
            }

            @Override
            public void onAccessDenied()
            {
                onConnectionRefused();
            }
        });
        sendingThread.start();
    }

    private void onConnectionSuccessful()
    {
        //Remember to run UI operations with Activity.runOnUiThread();
        getActivity().runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                Toast.makeText(getContext(), "Access granted", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void onConnectionRefused()
    {
        //Remember to run UI operations with Activity.runOnUiThread();
        getActivity().runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                Toast.makeText(getContext(), "Access denied", Toast.LENGTH_SHORT).show();
                reverseAnimateConnecting();
            }
        });
    }

    public void updateTitleWifi()
    {
        setTitleSearchingWifi();
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
        reverseAnimateConnecting();
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
