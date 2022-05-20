package com.example.weatherman;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    SwipeRefreshLayout swipeRefreshLayout;

    String wttr_hourly_temp[], wttr_hourly_time[];

    RecyclerView rcv_hourly;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rcv_hourly = findViewById(R.id.rcv_hourly);

        wttr_hourly_temp = getResources().getStringArray(R.array.wttr_hourly_temp);
        wttr_hourly_time = getResources().getStringArray(R.array.wttr_hourly_time);

        WeatherItem wttr_hourly = new WeatherItem(this, wttr_hourly_time, wttr_hourly_temp);

        rcv_hourly.setAdapter(wttr_hourly);
        rcv_hourly.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));


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
}