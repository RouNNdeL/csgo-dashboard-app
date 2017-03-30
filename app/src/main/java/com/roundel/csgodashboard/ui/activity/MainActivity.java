package com.roundel.csgodashboard.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.roundel.csgodashboard.R;

public class MainActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PrimaryDrawerItem item = new PrimaryDrawerItem()
                .withName("Hello")
                .withTextColorRes(R.color.colorMaterialDark87)
                .withIcon(R.drawable.ic_add_a_photo_white_24dp)
                .withIconColorRes(R.color.colorMaterialDark50)
                .withSelectedBackgroundAnimated(true);

        new DrawerBuilder().withActivity(this)
                .addDrawerItems(
                        item
                )
                .withOnDrawerItemClickListener((view, position, drawerItem) ->
                {
                    startActivity(new Intent(MainActivity.this, GameInfoActivity.class));
                    return false;
                }).build();
    }
}
