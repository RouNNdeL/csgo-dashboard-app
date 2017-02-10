package com.roundel.csgodashboard.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Spinner;
import android.widget.Toast;

import com.roundel.csgodashboard.R;
import com.roundel.csgodashboard.entities.Grenade;
import com.roundel.csgodashboard.entities.Stance;
import com.roundel.csgodashboard.spinner.GrenadeAdapter;
import com.roundel.csgodashboard.spinner.StanceAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AddNadeActivity extends AppCompatActivity
{
    private static final String TAG = AddNadeActivity.class.getSimpleName();

    @BindView(R.id.add_nade_spinner_grenade) Spinner mGrenadeSpinner;
    @BindView(R.id.add_nade_spinner_stance) Spinner mStanceSpinner;

    private StanceAdapter mStanceAdapter;
    private GrenadeAdapter mGrenadeAdapter;

    private List<Stance> mStanceList = new ArrayList<>();
    private List<Grenade> mGrenadeList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_nade);

        ButterKnife.bind(this);

        mStanceList = Stance.getDefaultStanceList(this);
        mStanceAdapter = new StanceAdapter(this, R.layout.list_icon_two_line_no_ripple, R.id.list_text_primary, mStanceList);
        mStanceSpinner.setAdapter(mStanceAdapter);

        mGrenadeList = Grenade.getDefaultGrenadeList(this);
        mGrenadeAdapter = new GrenadeAdapter(this, R.layout.list_icon_one_line_no_ripple, R.id.list_text_primary, mGrenadeList);
        mGrenadeSpinner.setAdapter(mGrenadeAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_add_nade, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch(item.getItemId())
        {
            case R.id.menu_add_nade_done:
                //TODO: Validate the form, then save it's state and finish the Activity
                Toast.makeText(this, "TODO: Save and finish the activity", Toast.LENGTH_SHORT).show();
                return true;
        }
        return false;
    }
}
