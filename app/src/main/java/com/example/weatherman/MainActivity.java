package com.example.weatherman;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    SwipeRefreshLayout swipeRefreshLayout;
    ActionBarDrawerToggle drawerToggle;
    DrawerLayout drawerLayout;

    String wttr_hourly_temp[], wttr_hourly_time[];

    RecyclerView rcv_hourly;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        load_settings();

        rcv_hourly = findViewById(R.id.rcv_hourly);

        wttr_hourly_temp = getResources().getStringArray(R.array.wttr_hourly_temp);
        wttr_hourly_time = getResources().getStringArray(R.array.wttr_hourly_time);

        WeatherItem wttr_hourly = new WeatherItem(this, wttr_hourly_time, wttr_hourly_temp);

        rcv_hourly.setAdapter(wttr_hourly);
        rcv_hourly.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        /* HAMBURGRR MENU */
        drawerLayout = findViewById(R.id.drawerLayout);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);

        /* SWIPE REFRESH */
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Toast.makeText(MainActivity.this, "Weather updated", Toast.LENGTH_SHORT).show();
                // update weather on refresh
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    /* LOAD SETTINGS */
    private void load_settings(){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        boolean is_metric = sp.getBoolean("sp_metric", true);
        if (is_metric)
            Toast.makeText(this, "Metric values enabled", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(this, "Metric values disabled", Toast.LENGTH_SHORT).show();
    }

    /* MENU ITEMS */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_items, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.m_settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        load_settings();
        super.onResume();
    }
}