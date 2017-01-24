package com.roundel.csgodashboard.ui;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.transition.AutoTransition;
import android.support.transition.Fade;
import android.support.transition.Transition;
import android.support.transition.TransitionManager;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.paolorotolo.appintro.ISlidePolicy;
import com.roundel.csgodashboard.entities.GameServer;
import com.roundel.csgodashboard.R;
import com.roundel.csgodashboard.SlideAction;
import com.roundel.csgodashboard.recyclerview.GameServerAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


import static android.view.View.GONE;
import static android.view.View.OVER_SCROLL_ALWAYS;
import static android.view.View.OVER_SCROLL_NEVER;
import static android.view.View.TRANSLATION_X;

/**
 * Created by Krzysiek on 2017-01-23.
 */
public class ServerSearchSlide extends Fragment implements View.OnClickListener, ISlidePolicy
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
    private ViewGroup root;

    private boolean canContinue = false;

    private List<GameServer> gameServers = new ArrayList<>();
    private SlideAction mSlideActionInterface;
    private ServerConnectionInfo mServerConnectionInfoInterface;

    public static ServerSearchSlide newInstance(int layoutResId)
    {
        ServerSearchSlide sampleSlide = new ServerSearchSlide();

        Bundle args = new Bundle();
        args.putInt(ARG_LAYOUT_RES_ID, layoutResId);
        sampleSlide.setArguments(args);

        return sampleSlide;
    }

    public void attachSlideActionInterface(SlideAction slideAction)
    {
        this.mSlideActionInterface = slideAction;
    }

    public void attachServerConnectionInfoInterface(ServerConnectionInfo serverConnectionInfo)
    {
        this.mServerConnectionInfoInterface = serverConnectionInfo;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        if(getArguments() != null && getArguments().containsKey(ARG_LAYOUT_RES_ID))
        {
            layoutResId = getArguments().getInt(ARG_LAYOUT_RES_ID);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        root = (ViewGroup) inflater.inflate(layoutResId, container, false);

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

    public void addServer(GameServer server)
    {
        gameServers.add(server);
        mAdapter.notifyItemInserted(gameServers.size() - 1);
    }

    @Override
    public void onClick(View v)
    {
        if(v.getId() == R.id.game_server_connect)
        {
            View root = (View) v.getParent();
            mAdapter.setRefreshing(!mAdapter.isRefreshing());
            canContinue = true;
            animateConnecting(mRecyclerView.getChildLayoutPosition(root));
            if(mServerConnectionInfoInterface != null)
                mServerConnectionInfoInterface.onConnecting();
            /*if(mSlideActionInterface != null)
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
            }*/
        }
    }

    public void animateConnecting(int position)
    {
        final int cardMargin = (int) getResources().getDimension(R.dimen.setup_search_server_cardview_margin);
        final int recyclerViewHeight = (int) getResources().getDimension(R.dimen.setup_search_server_recyclerview_height);

        final LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        final FrameLayout.LayoutParams recyclerViewParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);

        cardParams.setMargins(0, cardMargin, 0, cardMargin);

        mCardView.setLayoutParams(cardParams);
        mRecyclerView.setLayoutParams(recyclerViewParams);
        mRecyclerView.setOverScrollMode(OVER_SCROLL_NEVER);
        //mConnectingStatus.setVisibility(View.VISIBLE);
        /*
        GameServer gameServer = gameServers.get(position);

        TextView name = (TextView) mConnectingStatus.findViewById(R.id.connecting_game_server_name);
        TextView host = (TextView) mConnectingStatus.findViewById(R.id.connecting_game_server_host);
        TextView port = (TextView) mConnectingStatus.findViewById(R.id.connecting_game_server_port);

        name.setText(gameServer.getName());
        host.setText(gameServer.getHost());
        port.setText(String.valueOf(gameServer.getPort()));*/

        //mTitle.setVisibility(GONE);
        final String format = String.format(Locale.getDefault(), "Connecting to \"%s\"...", gameServers.get(position).getName());
        int start = format.indexOf("\""+gameServers.get(position).getName()+"\"");
        int end = start + gameServers.get(position).getName().length()+1;

        Spannable spannableTitle = new SpannableString(format);
        spannableTitle.setSpan(
                new StyleSpan(android.graphics.Typeface.BOLD),
                start,                        //Start
                end,         //End
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        );

        mTitle.setText(spannableTitle);

        mAdapter.expandWhenConnecting(position);

        Transition transition = new AutoTransition();
        transition.setDuration(200);
        TransitionManager.beginDelayedTransition(root);
    }

    public void reverseAnimateConnecting()
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

    public void setTitleSearchingWifi()
    {
        WifiManager wifiManager = (WifiManager) getContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifiManager.getConnectionInfo();
        final String ssid = info.getSSID();
        /*if(ssid.charAt(0) == '"' && ssid.charAt(ssid.length()-1) == '"')
            ssid = ssid.substring(1,ssid.length()-1);*/

        final String title = String.format(Locale.getDefault(), getString(R.string.setup_server_searching), ssid);

        int start;
        int end;
        if(ssid.charAt(0) == '"' && ssid.charAt(ssid.length()-1) == '"')
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
                new StyleSpan(android.graphics.Typeface.BOLD),
                start,                        //Start
                end,         //End
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        );
        mTitle.setText(spannableTitle);
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

    public interface ServerConnectionInfo
    {
        void onConnected();

        void onConnecting();

        void onFailed();
    }
}
