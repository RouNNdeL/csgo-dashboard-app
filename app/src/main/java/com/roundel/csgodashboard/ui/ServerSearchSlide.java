package com.roundel.csgodashboard.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.transition.AutoTransition;
import android.support.transition.Transition;
import android.support.transition.TransitionManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ContextThemeWrapper;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;


import static android.view.View.OVER_SCROLL_ALWAYS;
import static android.view.View.OVER_SCROLL_NEVER;

/**
 * Created by Krzysiek on 2017-01-23.
 */
public class ServerSearchSlide extends SlideBase implements View.OnClickListener, ISlidePolicy, ServerDiscoveryThread.ServerDiscoveryListener
{
    public static final String TAG = "ServerSearchSlide";

    private static final String ARG_LAYOUT_RES_ID = "layoutResId";
    private static final Pattern HOST_PATTERN = Pattern.compile("([\\d]){1,3}\\.([\\d]){1,3}\\.([\\d]){1,3}\\.([\\d]){1,3}");
    private static final Pattern LOCAL_HOST_PATTERN = Pattern.compile("(^127\\..*)|(^10\\..*)|(^172\\.1[6-9]\\..*)|(^172\\.2[0-9]\\..*)|(^172\\.3[0-1]\\..*)|(^192\\.168\\..*)");
    private static final int PORT_MAX = (1 << 16) - 1;
    private static final int PORT_MIN = 0;
    //<editor-fold desc="private variables">
    @BindView(R.id.setup_server_search_manual_connect) LinearLayout mManualConnectButton;
    @BindView(R.id.setup_server_connection_auto) LinearLayout mAutoConnectionContainer;
    @BindView(R.id.setup_server_connection_manual) LinearLayout mManualConnectionContainer;
    @BindView(R.id.setup_server_connection_progress) LinearLayout mConnectionProgressContainer;

    /**
     * Button o the bottom of the screen, either used to refresh (auto mode) or connect (manual
     * mode)
     */
    @BindView(R.id.setup_server_back_to_auto) ImageView mBackToAutoButton;
    @BindView(R.id.setup_server_manual_host) TextInputEditText mManualHost;
    @BindView(R.id.setup_server_manual_host_wrapper) TextInputLayout mManualHostWrapper;
    @BindView(R.id.setup_server_manual_port) TextInputEditText mManualPort;
    @BindView(R.id.setup_server_manual_port_wrapper) TextInputLayout mManualPortWrapper;
    @BindView(R.id.setup_server_connection_status) TextView mConnectionStatus;
    private int layoutResId;
    private GameServerAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private TextView mTitle;
    private CardView mCardView;
    private Button mActionButton;
    private boolean canContinue = false;
    private boolean connectingToServer = false;
    private boolean isInManualMode = false;

    private List<GameServer> gameServers = new ArrayList<>();
    private SlideAction mSlideActionInterface;
    private ServerConnectionInfo mServerConnectionInfoInterface;
    private ViewGroup root;
    private GameServer currentGameServer;
    //</editor-fold>

    public static ServerSearchSlide newInstance(int layoutResId)
    {
        ServerSearchSlide sampleSlide = new ServerSearchSlide();

        Bundle args = new Bundle();
        args.putInt(ARG_LAYOUT_RES_ID, layoutResId);
        sampleSlide.setArguments(args);

        return sampleSlide;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);
        root = getRoot();

        ButterKnife.bind(this, root);

        mManualConnectButton.setOnClickListener(this);

        mBackToAutoButton.setOnClickListener(this);

        mTitle = (TextView) root.findViewById(R.id.setup_connecting_title);
        setTitleSearchingWifi();

        mActionButton = (Button) root.findViewById(R.id.setup_server_search_action_btn);
        mActionButton.setOnClickListener(this);

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
        switch(v.getId())
        {
            case R.id.game_server_connect:
            {
                View root = (View) v.getParent();
                final int position = mRecyclerView.getChildLayoutPosition(root);

                mAdapter.setRefreshing(!mAdapter.isRefreshing());

                connectingToServer = true;

                currentGameServer = gameServers.get(position);

                animateAutoConnecting();

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

                attemptConnection(currentGameServer);
                break;
            }
            case R.id.setup_server_search_action_btn:
            {
                if(isInManualMode && validateManualForm())
                {
                    currentGameServer = getManualGameServer();
                    attemptConnection(currentGameServer);
                    animateManualConnecting();
                    connectingToServer = true;
                }
                else
                    startDiscovery();
                break;
            }
            case R.id.setup_server_search_manual_connect:
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(getContext(), R.style.LightDialog));
                builder.setTitle("Can't connect?");
                builder.setMessage("On some networks the app might not be able to automatically detect a PC. " +
                        "You can however attempt a manual connection by supplying an IP and a port shown by the server running on you PC." +
                        "\nDo you want to attempt the manual connection?");
                builder.setPositiveButton("Manual", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        dialog.dismiss();
                        animateToManualConnection();
                    }
                });
                builder.setNegativeButton("cancel", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        dialog.dismiss();
                    }
                });
                builder.create().show();

                break;
            }
            case R.id.setup_server_back_to_auto:
                View view = getActivity().getCurrentFocus();
                if(view != null)
                {
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
                animateToAutoConnection();
                break;
        }
    }

    //<editor-fold desc="Discovery interface">
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
    //</editor-fold>

    @Override
    public boolean isPolicyRespected()
    {
        return canContinue;
    }

    @Override
    public void onUserIllegallyRequestedNextPage()
    {

    }

    @Override
    public boolean onBackPressed()
    {
        if(connectingToServer && isInManualMode)
        {
            reverseAnimateManualConnecting();
            connectingToServer = false;
            return false;
        }
        else if(!isInManualMode && connectingToServer)
        {
            reverseAnimateAutoConnecting();
            connectingToServer = false;
            return false;
        }
        else if(isInManualMode && !connectingToServer)
        {
            animateToAutoConnection();
            isInManualMode = false;
            return false;
        }
        return true;
    }

    public void attachServerConnectionInfoInterface(ServerConnectionInfo serverConnectionInfo)
    {
        this.mServerConnectionInfoInterface = serverConnectionInfo;
    }

    public void addServer(GameServer server)
    {
        for(GameServer gameServer : gameServers)
        {
            if(Objects.equals(gameServer.getHost(), server.getHost()))
                return;
        }
        gameServers.add(server);
        mAdapter.notifyItemInserted(gameServers.size() - 1);
    }

    public void startDiscovery()
    {
        ServerDiscoveryThread serverDiscoveryThread = new ServerDiscoveryThread();
        serverDiscoveryThread.setServerDiscoveryListener(this);
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

            @Override
            public void onServerNotResponded(final String error)
            {
                onConnectionFailed();
            }

            @Override
            public void onServerTimedOut()
            {
                onConnectionFailed();
            }

            @Override
            public void onAllowConnection()
            {
                onConnectionAllow();
            }
        });
        sendingThread.start();
    }

    //<editor-fold desc="Connection interface">
    private void onConnectionSuccessful()
    {
        //Remember to run UI operations with Activity.runOnUiThread();
        getActivity().runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                Toast.makeText(getContext(), "Access granted", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void onConnectionRefused()
    {
        connectingToServer = false;
        //Remember to run UI operations with Activity.runOnUiThread();
        getActivity().runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                Toast.makeText(getContext(), "Access denied", Toast.LENGTH_LONG).show();
                if(isInManualMode)
                    reverseAnimateManualConnecting();
                else
                    reverseAnimateAutoConnecting();
            }
        });
    }

    private void onConnectionFailed()
    {
        connectingToServer = false;
        getActivity().runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                Toast.makeText(getContext(), "Connection failed", Toast.LENGTH_LONG).show();
                if(isInManualMode)
                    reverseAnimateManualConnecting();
                else
                    reverseAnimateAutoConnecting();
            }
        });
    }

    private void onConnectionAllow()
    {
        getActivity().runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                setStatusAllow();
            }
        });
    }
    //</editor-fold>

    //<editor-fold desc="Connecting animation">
    private void animateAutoConnecting()
    {
        final int cardMargin = (int) getResources().getDimension(R.dimen.setup_search_server_cardview_margin);
        final int recyclerViewHeight = (int) getResources().getDimension(R.dimen.setup_search_server_recyclerview_height);

        final LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        final FrameLayout.LayoutParams containerParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);

        cardParams.setMargins(0, cardMargin, 0, cardMargin);

        mCardView.setLayoutParams(cardParams);
        mAutoConnectionContainer.setLayoutParams(containerParams);
        mRecyclerView.setOverScrollMode(OVER_SCROLL_NEVER);

        mManualConnectButton.setVisibility(View.GONE);

        setTitleConnecting();

        mAdapter.expandWhenConnecting(0);

        Transition transition = new AutoTransition();
        transition.setDuration(200);
        TransitionManager.beginDelayedTransition(root);
    }

    private void reverseAnimateAutoConnecting()
    {
        final int cardMargin = (int) getResources().getDimension(R.dimen.setup_search_server_cardview_margin);
        final int recyclerViewHeight = (int) getResources().getDimension(R.dimen.setup_search_server_recyclerview_height);

        final LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        final FrameLayout.LayoutParams containerParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, recyclerViewHeight);

        cardParams.setMargins(cardMargin, cardMargin, cardMargin, cardMargin);

        mCardView.setLayoutParams(cardParams);
        mAutoConnectionContainer.setLayoutParams(containerParams);
        mRecyclerView.setOverScrollMode(OVER_SCROLL_ALWAYS);

        mManualConnectButton.setVisibility(View.VISIBLE);

        setTitleSearchingWifi();

        mAdapter.collapseWhenConnecting();

        Transition transition = new AutoTransition();
        transition.setDuration(200);
        TransitionManager.beginDelayedTransition(root, transition);
    }

    private void animateManualConnecting()
    {
        mAutoConnectionContainer.setVisibility(View.GONE);

        mConnectionProgressContainer.setVisibility(View.VISIBLE);

        TransitionManager.beginDelayedTransition(root);
    }

    private void reverseAnimateManualConnecting()
    {
        mAutoConnectionContainer.setVisibility(View.VISIBLE);

        mConnectionProgressContainer.setVisibility(View.GONE);

        TransitionManager.beginDelayedTransition(root);
    }
    //</editor-fold>

    //<editor-fold desc="Change connection mode animation">
    private void animateToManualConnection()
    {
        isInManualMode = true;
        mManualConnectionContainer.setVisibility(View.VISIBLE);
        mAutoConnectionContainer.setVisibility(View.GONE);
        mActionButton.setText("Connect");
        TransitionManager.beginDelayedTransition(root);
        setTitleInvisible();
    }

    private void animateToAutoConnection()
    {
        isInManualMode = false;
        mManualConnectionContainer.setVisibility(View.GONE);
        mAutoConnectionContainer.setVisibility(View.VISIBLE);
        mActionButton.setText("Refresh");
        TransitionManager.beginDelayedTransition(root);
        setTitleSearchingWifi();
    }

    //</editor-fold>

    //<editor-fold desc="Title modifications">
    private void setTitleInvisible()
    {
        mTitle.setVisibility(View.GONE);
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

        mTitle.setVisibility(View.VISIBLE);
        mTitle.setText(spannableTitle);
    }

    private void setTitleConnecting()
    {
        final String name = currentGameServer.getName();
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

        mTitle.setVisibility(View.VISIBLE);
        mTitle.setText(spannableTitle);
    }
    //</editor-fold>

    //<editor-fold desc="Connection status modifications">
    private void setStatusConnecting()
    {
        mConnectionStatus.setText("Establishing connection...");
    }

    private void setStatusAllow()
    {
        final String text = String.format(
                Locale.getDefault(),
                getString(R.string.setup_server_search_allow_connection),
                Build.MANUFACTURER + " " + Build.MODEL
        );
        if(isInManualMode)
        {
            mConnectionStatus.setText(text);
        }
        else
        {
            mAdapter.setConnectingStatusText(text);
            mAdapter.notifyDataSetChanged();
        }
    }
    //</editor-fold>

    private boolean validateManualForm()
    {
        final Matcher hostMatcher = HOST_PATTERN.matcher(mManualHost.getText());
        final Matcher localHostMatcher = LOCAL_HOST_PATTERN.matcher(mManualHost.getText());

        boolean passes = true;

        if(hostMatcher.matches())
        {
            if(localHostMatcher.matches())
            {
                mManualHostWrapper.setErrorEnabled(false);
            }
            else
            {
                mManualHostWrapper.setErrorEnabled(true);
                mManualHostWrapper.setError("Not a valid local ip address");
                passes = false;
            }
        }
        else
        {
            mManualHostWrapper.setErrorEnabled(true);
            mManualHostWrapper.setError("Not a valid ip address");
            passes = false;
        }
        if(Integer.parseInt(mManualPort.getText().toString()) < PORT_MIN || Integer.parseInt(mManualPort.getText().toString()) > PORT_MAX)
        {
            mManualPortWrapper.setErrorEnabled(true);
            mManualPortWrapper.setError("Port hast to be in range 1-65535");
            passes = false;
        }
        else
        {
            mManualPortWrapper.setErrorEnabled(false);
        }
        TransitionManager.beginDelayedTransition(root);
        return passes;
    }

    private GameServer getManualGameServer()
    {
        String host = mManualHost.getText().toString();
        int port = Integer.parseInt(mManualPort.getText().toString());
        return new GameServer(host, host, port);
    }

    public boolean isConnectingToServer()
    {
        return connectingToServer;
    }

    public void updateTitleWifi()
    {
        setTitleSearchingWifi();
    }

    public void cancelConnectingProcess()
    {
        connectingToServer = false;
        reverseAnimateAutoConnecting();
        //TODO: Kill a Process handling connection;
    }

    public interface ServerConnectionInfo
    {
        void onConnected();

        void onConnecting();

        void onFailed();
    }
}
