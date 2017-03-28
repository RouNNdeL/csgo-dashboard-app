package com.roundel.csgodashboard.ui.activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.roundel.csgodashboard.R;
import com.roundel.csgodashboard.util.LogHelper;

public class SnackbarTestActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_snackbar_test);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(v ->
        {
            Snackbar snackbar = Snackbar.make(v, "Test", Snackbar.LENGTH_LONG)
                    .setAction("Action", v1 ->
                    {});

            View view = snackbar.getView();
            LogHelper.d("SnackbarTest", view.toString());
            if(view instanceof LinearLayout)
                LogHelper.d("SnackbarTest", view.toString() + " is an instance of LinearLayout");
            if(view instanceof FrameLayout)
                LogHelper.d("SnackbarTest", view.toString() + " is an instance of FrameLayout");
            TextView text = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
            Button action = (Button) view.findViewById(android.support.design.R.id.snackbar_action);

            if(text.getParent() instanceof FrameLayout)
                LogHelper.d("SnackbarTest", text.getParent().toString() + " is an instance of FrameLayout");
            if(text.getParent() instanceof LinearLayout)
                LogHelper.d("SnackbarTest", text.getParent().toString() + " is an instance of LinearLayout");
            action.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT));
            text.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1f));

            snackbar.show();
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

}
