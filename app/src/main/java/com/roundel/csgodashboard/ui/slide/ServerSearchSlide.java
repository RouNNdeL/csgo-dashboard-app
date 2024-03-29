package com.roundel.csgodashboard.ui.slide;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.transition.AutoTransition;
import android.support.transition.ChangeBounds;
import android.support.transition.Transition;
import android.support.transition.TransitionManager;
import android.support.transition.TransitionSet;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
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
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.paolorotolo.appintro.ISlidePolicy;
import com.roundel.csgodashboard.R;
import com.roundel.csgodashboard.adapter.recyclerview.GameServerAdapter;
import com.roundel.csgodashboard.entities.GameServer;
import com.roundel.csgodashboard.net.ServerConnectionThread;
import com.roundel.csgodashboard.net.ServerDiscoveryThread;
import com.roundel.csgodashboard.ui.activity.GameInfoActivity;
import com.roundel.csgodashboard.util.LogHelper;
import com.transitionseverywhere.extra.Scale;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindInt;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Krzysiek on 2017-01-23.
 */
public class ServerSearchSlide extends SlideBase implements View.OnClickListener, ISlidePolicy, ServerDiscoveryThread.ServerDiscoveryListener
{
    private static final String TAG = ServerSearchSlide.class.getSimpleName();

    private static final String ARG_LAYOUT_RES_ID = "layoutResId";
    private static final Pattern HOST_PATTERN = Pattern.compile("([\\d]){1,3}\\.([\\d]){1,3}\\.([\\d]){1,3}\\.([\\d]){1,3}");
    private static final Pattern LOCAL_HOST_PATTERN = Pattern.compile("(^127\\..*)|(^10\\..*)|(^172\\.1[6-9]\\..*)|(^172\\.2[0-9]\\..*)|(^172\\.3[0-1]\\..*)|(^192\\.168\\..*)");
    private static final int PORT_MAX = (1 << 16) - 1;
    private static final int PORT_MIN = 0;

    private final int mStartActivityDelay = 1500;

    private final List<GameServer> mGameServers = new ArrayList<>();

    //<editor-fold desc="private variables">
    @BindView(R.id.setup_server_connection_container) FrameLayout mConnectionContainer;
    @BindView(R.id.setup_server_search_manual_connect) LinearLayout mManualConnectButton;
    @BindView(R.id.setup_server_connection_auto) ConstraintLayout mAutoConnectionContainer;
    @BindView(R.id.setup_server_connection_manual) ConstraintLayout mManualConnectionContainer;
    @BindView(R.id.setup_server_connection_progress) ConstraintLayout mConnectionProgressContainer;
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
    @BindView(R.id.setup_server_connection_bar) ProgressBar mConnectionIconProgress;
    @BindView(R.id.setup_server_connection_success) ImageView mConnectionIconSuccess;
    @BindView(R.id.setup_server_connection_failure) ImageView mConnectionIconFailure;

    @BindView(R.id.setup_server_search_recyclerview) RecyclerView mRecyclerView;
    @BindView(R.id.setup_connecting_title) TextView mTitle;
    @BindView(R.id.setup_server_search_action_btn) Button mActionButton;
    @BindView(R.id.setup_server_search_cardview) CardView mCardView;

    @BindInt(R.integer.default_transition_duration) int defaultTransitionDuration;

    private GameServerAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private boolean canContinue = false;
    private boolean isConnectingToServer = false;
    private boolean isRefreshing = false;
    private boolean isInManualMode = false;
    private SlideAction mSlideActionInterface;

    private ServerConnectionInfo mServerConnectionInfoInterface;
    private ViewGroup mRoot;
    private GameServer currentGameServer;
    //</editor-fold>

    public static ServerSearchSlide newInstance()
    {
        ServerSearchSlide sampleSlide = new ServerSearchSlide();

        Bundle args = new Bundle();
        args.putInt(ARG_LAYOUT_RES_ID, R.layout.setup_server_search);
        sampleSlide.setArguments(args);

        return sampleSlide;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        mRoot = (ViewGroup) super.onCreateView(inflater, container, savedInstanceState);

        ButterKnife.bind(this, mRoot);

        mManualConnectButton.setOnClickListener(this);

        mBackToAutoButton.setOnClickListener(this);

        setTitleSearchingWifi();

        mActionButton.setOnClickListener(this);

        mAdapter = new GameServerAdapter(mGameServers, this);

        mLayoutManager = new LinearLayoutManager(getContext());

        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAutoConnectionContainer.setVisibility(View.VISIBLE);
        mManualConnectionContainer.setVisibility(View.GONE);
        mConnectionProgressContainer.setVisibility(View.GONE);

        return mRoot;
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
                currentGameServer = mGameServers.get(position);

                connect();
                break;
            }
            case R.id.setup_server_search_action_btn:
            {
                if(isInManualMode && validateManualForm())
                {
                    currentGameServer = getManualGameServer();
                    connect();
                }
                else if(!isRefreshing)
                    startDiscovery();
                break;
            }
            case R.id.setup_server_search_manual_connect:
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(getContext(), R.style.LightDialog));
                builder.setTitle("Can't connect?");
                builder.setMessage("On some networks the app might not be able to automatically detect a PC. " +
                        "You can however attempt a manual connection by supplying an IP and a port shown by the server running on you PC." +
                        "\n\nDo you want to attempt the manual connection?");
                builder.setPositiveButton("Manual", (dialog, which) ->
                {
                    dialog.dismiss();
                    animateToManualConnection();
                });
                builder.setNegativeButton("cancel", (dialog, which) -> dialog.dismiss());
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
            getActivity().runOnUiThread(() -> addServer(server));
        }
        catch(NullPointerException e)
        {
            e.printStackTrace();
        }
    }

    //<editor-fold desc="Main connection methods">

    @Override
    public void onSocketOpened()
    {
        try
        {
            getActivity().runOnUiThread(() ->
            {
                isRefreshing = true;
                mAdapter.setRefreshing(true);
            });
        }
        catch(NullPointerException e)
        {
            e.printStackTrace();
        }
    }

   /* private void autoConnect()
    {
        isConnectingToServer = true;

        setStatusConnecting();
        animateAutoConnecting();
      attemptConnection(currentGameServer);
    }*/

    @Override
    public void onSocketClosed()
    {
        try
        {
            getActivity().runOnUiThread(() ->
            {
                isRefreshing = false;
                mAdapter.setRefreshing(false);
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

    @Override
    public boolean onBackPressed()
    {
        if(isConnectingToServer)
        {
            reverseAnimateConnecting();
            isConnectingToServer = false;
            return false;
        }
        else if(isInManualMode && !isConnectingToServer)
        {
            setTitleSearchingWifi();
            animateToAutoConnection();
            return false;
        }
        return true;
    }

    /**
     * Remember to always set the {@link #currentGameServer} before calling this method
     */
    private void connect()
    {
        if(isConnectingToServer)
            return;
        isConnectingToServer = true;

        setStatusConnecting();
        setTitleConnecting();
        setStatusIconProgress();
        animateConnecting();
        attemptConnection(currentGameServer);
    }

    public void attachServerConnectionInfoInterface(ServerConnectionInfo serverConnectionInfo)
    {
        this.mServerConnectionInfoInterface = serverConnectionInfo;
    }

    public void addServer(GameServer server)
    {
        for(int i = 0; i < mGameServers.size(); i++)
        {
            GameServer gameServer = mGameServers.get(i);
            if(Objects.equals(gameServer.getHost(), server.getHost()))
            {
                if(gameServer.getPort() != server.getPort())
                {
                    gameServer.setPort(server.getPort());
                    mAdapter.notifyItemChanged(i);
                }
                return;
            }
        }
        mGameServers.add(server);
        mAdapter.notifyItemInserted(mGameServers.size() - 1);
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
        ServerConnectionThread sendingThread = new ServerConnectionThread(server);
        sendingThread.setConnectionListener(new ServerConnectionThread.ServerConnectionListener()
        {
            @Override
            public void onAccessGranted(GameServer gameServer)
            {
                onConnectionSuccessful(gameServer);
            }

            @Override
            public void onAccessDenied(GameServer gameServer)
            {
                onConnectionRefused(gameServer);
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
    private void onConnectionSuccessful(final GameServer gameServer)
    {
        //Remember to run UI operations with Activity.runOnUiThread();
        getActivity().runOnUiThread(() ->
        {
            setStatusIconSuccess();
            setStatusConnectedSuccessfully();

            new Handler().postDelayed(() ->
            {
                Intent intent = new Intent(getContext(), GameInfoActivity.class);
                intent.putExtra(GameInfoActivity.EXTRA_GAME_SERVER_NAME, gameServer.getName());
                intent.putExtra(GameInfoActivity.EXTRA_GAME_SERVER_HOST, gameServer.getHost());
                intent.putExtra(GameInfoActivity.EXTRA_GAME_SERVER_PORT, gameServer.getPort());
                getContext().startActivity(intent);
            }, mStartActivityDelay);
        });
    }

    private void onConnectionRefused(GameServer gameServer)
    {
        //isConnectingToServer = false;
        //Remember to run UI operations with Activity.runOnUiThread();
        getActivity().runOnUiThread(() ->
        {
            setStatusIconFailure();

            setStatusConnectionDeined();
        });
    }

    private void onConnectionFailed()
    {
        //isConnectingToServer = false;
        if(getActivity() != null)
            getActivity().runOnUiThread(() -> new Handler().postDelayed(() ->
            {
                setStatusIconFailure();

                setStatusConnectionFailure();
            }, defaultTransitionDuration * 2));
    }

    private void onConnectionAllow()
    {
        getActivity().runOnUiThread(this::setStatusAllow);
    }

    //<editor-fold desc="Connecting animation">
    private void animateConnecting()
    {
        Transition autoTransition = new AutoTransition();
        autoTransition.setDuration(defaultTransitionDuration);
        TransitionManager.beginDelayedTransition(mRoot, autoTransition);

        mConnectionContainer.setVisibility(View.GONE);
        mConnectionProgressContainer.setVisibility(View.VISIBLE);
        mActionButton.setVisibility(View.GONE);
    }

    private void reverseAnimateConnecting()
    {
        Transition autoTransition = new AutoTransition();
        autoTransition.setDuration(defaultTransitionDuration);
        TransitionManager.beginDelayedTransition(mRoot, autoTransition);

        mConnectionContainer.setVisibility(View.VISIBLE);
        mConnectionProgressContainer.setVisibility(View.GONE);

        mConnectionIconSuccess.setVisibility(View.GONE);
        mConnectionIconFailure.setVisibility(View.GONE);

        mActionButton.setVisibility(View.VISIBLE);
    }

    //<editor-fold desc="Change connection mode animation">
    private void animateToManualConnection()
    {
        isInManualMode = true;

        Transition autoTransition = new AutoTransition();
        autoTransition.setDuration(defaultTransitionDuration);
        TransitionManager.beginDelayedTransition(mRoot, autoTransition);

        mManualConnectionContainer.setVisibility(View.VISIBLE);
        mAutoConnectionContainer.setVisibility(View.GONE);
        mActionButton.setText("Connect");
        setTitleInvisible();
    }

    private void animateToAutoConnection()
    {
        isInManualMode = false;

        Transition autoTransition = new AutoTransition();
        autoTransition.setDuration((defaultTransitionDuration));
        TransitionManager.beginDelayedTransition(mRoot, autoTransition);

        mManualConnectionContainer.setVisibility(View.GONE);
        mAutoConnectionContainer.setVisibility(View.VISIBLE);
        mActionButton.setText("Refresh");
        setTitleSearchingWifi();
    }

    //<editor-fold desc="Status icon changes">
    private void setStatusIconSuccess()
    {
        com.transitionseverywhere.TransitionManager.beginDelayedTransition(mRoot, new Scale());

        mConnectionIconSuccess.setVisibility(View.VISIBLE);

        mConnectionIconProgress.setVisibility(View.GONE);
        mConnectionIconFailure.setVisibility(View.GONE);
    }

    private void setStatusIconFailure()
    {
        com.transitionseverywhere.TransitionManager.beginDelayedTransition(mRoot, new Scale());

        mConnectionIconFailure.setVisibility(View.VISIBLE);

        mConnectionIconProgress.setVisibility(View.GONE);
        mConnectionIconSuccess.setVisibility(View.GONE);
    }

    private void setStatusIconProgress()
    {
        mConnectionIconProgress.setVisibility(View.VISIBLE);

        mConnectionIconSuccess.setVisibility(View.GONE);
        mConnectionIconFailure.setVisibility(View.GONE);
    }

    //<editor-fold desc="Title modifications">
    private void setTitleInvisible()
    {
        mTitle.setVisibility(View.GONE);
    }

    private void setTitleSearchingWifi()
    {
        WifiManager wifiManager = (WifiManager) getContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifiManager.getConnectionInfo();
        final String ssid = info.getSSID();
        /*if(ssid.charAt(0) == '"' && ssid.charAt(ssid.length()-1) == '"')
            ssid = ssid.substring(1,ssid.length()-1);*/

        final String title = String.format(Locale.getDefault(), getString(R.string.setup_server_searching), ssid);

        if(TextUtils.isEmpty(ssid))
        {
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
                    start,
                    end,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            );
            mTitle.setText(spannableTitle);
        }
        else
        {
            mTitle.setText(R.string.setup_server_no_wifi);
        }

        mTitle.setVisibility(View.VISIBLE);
    }
    //</editor-fold>

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

    //<editor-fold desc="Connection status modifications">
    private void setStatusConnecting()
    {
        mConnectionStatus.setText("Establishing connection...");
    }

    private void setStatusConnectedSuccessfully()
    {
        mConnectionStatus.setText("Connected!");
    }

    private void setStatusConnectionFailure()
    {
        mConnectionStatus.setText("Connection failure");
    }

    private void setStatusConnectionDeined()
    {
        mConnectionStatus.setText("Connection denied by the server");
    }
    //</editor-fold>

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

    private boolean validateManualForm()
    {
        final Matcher hostMatcher = HOST_PATTERN.matcher(mManualHost.getText());
        final Matcher localHostMatcher = LOCAL_HOST_PATTERN.matcher(mManualHost.getText());

        boolean passes = true;

        TransitionSet set = new TransitionSet();
        set.addTransition(new ChangeBounds());
        set.addTransition(new android.support.transition.Fade());

        TransitionManager.beginDelayedTransition(mRoot, set);

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
        Integer port = null;
        try
        {
            port = Integer.parseInt(mManualPort.getText().toString());
        }
        catch(NumberFormatException e)
        {
            LogHelper.i(TAG, e.toString());
        }
        if(port == null)
        {
            mManualPortWrapper.setErrorEnabled(true);
            mManualPortWrapper.setError("This field is required");
            passes = false;
        }
        else
        {
            if(port < PORT_MIN || port > PORT_MAX)
            {
                mManualPortWrapper.setErrorEnabled(true);
                mManualPortWrapper.setError("Port hast to be in range 1-65535");
                passes = false;
            }
            else
            {
                mManualPortWrapper.setErrorEnabled(false);
            }
        }
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
        return isConnectingToServer;
    }

    public void updateTitleWifi()
    {
        setTitleSearchingWifi();
    }

    public void cancelConnectingProcess()
    {
        isConnectingToServer = false;
        reverseAnimateConnecting();
        //TODO: Kill a Process handling connection;
    }

    public interface ServerConnectionInfo
    {
        void onConnected();

        void onConnecting();

        void onFailed();
    }
}
