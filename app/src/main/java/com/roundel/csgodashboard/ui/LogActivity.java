package com.roundel.csgodashboard.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.roundel.csgodashboard.R;
import com.roundel.csgodashboard.util.LogHelper;

public class LogActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);

        TextView content = (TextView) findViewById(R.id.activity_log_content);
        content.setText(LogHelper.getLogs());
    }
}
