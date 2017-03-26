package com.roundel.csgodashboard.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.roundel.csgodashboard.R;
import com.roundel.csgodashboard.util.LogHelper;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LogActivity extends AppCompatActivity implements LogHelper.LogListener
{
    //<editor-fold desc="private variables">
    @BindView(R.id.activity_log_content) TextView content;
    @BindView(R.id.log_toolbar) Toolbar toolbar;
    //</editor-fold>

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null)
        {
            getSupportActionBar().setTitle("Logs");
        }

        LogHelper.setLogListener(this);

        updateLogs();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_log, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch(item.getItemId())
        {
            case R.id.menu_log_share:
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(android.content.Intent.EXTRA_TEXT, LogHelper.getLogs());
                startActivity(Intent.createChooser(intent, "Share logs"));
                return true;
            case R.id.menu_log_clear:
                LogHelper.clearLogs();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onLogAdded(LogHelper.Log log)
    {
        updateLogs();
    }

    @Override
    public void onLogsCleared()
    {
        updateLogs();
    }

    private void updateLogs()
    {
        content.setText(LogHelper.getLogs());
    }
}
