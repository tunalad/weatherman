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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.google.android.material.navigation.NavigationView;

import java.sql.SQLDataException;
import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    SwipeRefreshLayout swipeRefreshLayout;

    ActionBarDrawerToggle drawerToggle;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Menu menu_drawer;

    RecyclerView rcv_hourly;
    RecyclerView rcv_daily;

    //<editor-fold desc="WEATHER DATA ARRAYS">
    ArrayList<String> wttr_hourly_temp = new ArrayList<>();
    ArrayList<String> wttr_hourly_time = new ArrayList<>();
    ArrayList<String> wttr_daily_temp = new ArrayList<>();
    ArrayList<String> wttr_daily_time = new ArrayList<>();
    //</editor-fold>

    //<editor-fold desc="TEXTVIEW DECLARATIONS">
    TextView txt_updated ;
    TextView txt_current;
    TextView txt_maxmin;
    TextView txt_status;
    TextView txt_pressure;
    TextView txt_humidity;
    TextView txt_clouds;
    TextView txt_visibility;
    TextView txt_wind_direction;
    TextView txt_wind_speed;
    //</editor-fold>

    DatabaseManager dbManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        load_settings();

        dbManager = new DatabaseManager(this);

        try {dbManager.open();}
        catch (SQLDataException e) {e.printStackTrace();}

        nav_drawer();
        //get_weather("Belgrade", true);

        //<editor-fold desc="HOURLY & DAILY CARDS">
        rcv_hourly = findViewById(R.id.rcv_hourly);
        WeatherItem wttr_hourly = new WeatherItem(this, wttr_hourly_time, wttr_hourly_temp);
        rcv_hourly.setAdapter(wttr_hourly);
        rcv_hourly.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        rcv_daily= findViewById(R.id.rcv_daily);
        WeatherItem wttr_daily= new WeatherItem(this, wttr_daily_time, wttr_daily_temp);
        rcv_daily.setAdapter(wttr_daily);
        rcv_daily.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        //</editor-fold>

        //<editor-fold desc="SWIPE REFRESH">
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                get_weather("Belgrade", true);
                Toast.makeText(MainActivity.this, "Weather updated", Toast.LENGTH_SHORT).show();
                // update weather on refresh
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        //</editor-fold>
    }
    /* GET API DATA */
    private void get_weather(String place_name, boolean metric){
        //<editor-fold desc="txt IDs">
        txt_updated = findViewById(R.id.txt_updated);
        txt_current = findViewById(R.id.txt_temperature);
        txt_maxmin = findViewById(R.id.txt_minmax);
        txt_status = findViewById(R.id.txt_status);
        txt_pressure = findViewById(R.id.txt_pressure);
        txt_humidity = findViewById(R.id.txt_humidity);
        txt_clouds = findViewById(R.id.txt_clouds);
        txt_visibility = findViewById(R.id.txt_visibility);
        txt_wind_direction = findViewById(R.id.txt_direction);
        txt_wind_speed = findViewById(R.id.txt_speed);
        //</editor-fold>

        String api_key = "Ou4LwGBcgCrqqUdKABOTULJRA2tIcmtC";

        Python py = Python.getInstance();
        PyObject wttr_api = py.getModule("wttr_api");

        // GET PLACE KEY
        String place_key = dbManager.fetch_place_key(place_name);

        //<editor-fold desc="CURRENT WEATHER">
        PyObject current_data = (wttr_api.callAttr("current", api_key, place_key, metric)).callAttr("response");
        PyObject crt_temp = wttr_api.callAttr("crt_temp", current_data);
        PyObject crt_max = wttr_api.callAttr("crt_max", current_data);
        PyObject crt_min = wttr_api.callAttr("crt_min", current_data);
        PyObject crt_text = wttr_api.callAttr("crt_text", current_data);

        txt_current.setText(crt_temp.toString() + "°");
        txt_maxmin.setText(crt_max.toString() + "/" + crt_min.toString());
        txt_status.setText(crt_text.toString());
        //</editor-fold>

        //<editor-fold desc="HOURLY WEATHER">
        PyObject hourly_data = (wttr_api.callAttr("hourly", api_key, place_key, metric)).callAttr("response");
        PyObject hrs_temp, hrs_time;
        wttr_hourly_temp.clear();
        wttr_hourly_time.clear();

        for(int i = 0; i < 12; i++){
            hrs_temp = wttr_api.callAttr("hrs_temp", hourly_data, i);
            hrs_time = wttr_api.callAttr("hrs_time", hourly_data, i);
            wttr_hourly_temp.add(hrs_temp.toString() + "°");
            wttr_hourly_time.add(hrs_time.toString());
        }
        //</editor-fold>

        //<editor-fold desc="DAILY WEATHER">
        PyObject daily_data = (wttr_api.callAttr("daily", api_key, place_key, metric)).callAttr("response");
        PyObject day_max, day_min, day_date;
        wttr_daily_time.clear();
        wttr_daily_temp.clear();
        for(int i = 0; i < 5; i++){
            day_max = wttr_api.callAttr("day_max", daily_data, i);
            day_min = wttr_api.callAttr("day_min", daily_data, i);
            day_date = wttr_api.callAttr("day_date", daily_data, i);
            wttr_daily_temp.add(day_max.toString() + "°/" + day_min.toString() + "°");
            wttr_daily_time.add(day_date.toString());
        }
        //</editor-fold>

        //<editor-fold desc="CURRENT WEATHER DETAILS">
        PyObject det_pressure = wttr_api.callAttr("det_pressure", current_data);
        PyObject det_humidity = wttr_api.callAttr("det_humidity", current_data);
        PyObject det_clouds = wttr_api.callAttr("det_clouds", current_data);
        PyObject det_visibility = wttr_api.callAttr("det_visibility", current_data);

        txt_pressure.setText(det_pressure.toString()+"mb");
        txt_humidity.setText(det_humidity.toString()+"%");
        txt_clouds.setText(det_clouds.toString()+"%");
        txt_visibility.setText(det_visibility+"km");

        PyObject wind_speed= wttr_api.callAttr("wind_speed", current_data);
        PyObject wind_dir = wttr_api.callAttr("wind_dir", current_data);
        txt_wind_direction.setText(wind_dir.toString());
        txt_wind_speed.setText(wind_speed.toString());
        //</editor-fold>

        // UPDATE TIME
        Calendar time = Calendar.getInstance();
        String time_hrs = time.get(Calendar.HOUR_OF_DAY) + ":" + time.get(Calendar.MINUTE);
        txt_updated.setText("Last updated at: " + time_hrs);
    }

    /* NAVIGATION DRAWER MENU */
    private void nav_drawer(){
        navigationView = findViewById(R.id.navigationView);
        drawerLayout = findViewById(R.id.drawerLayout);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);

        menu_drawer = navigationView.getMenu();
        for(int i = 1; i <= dbManager.count_place(); i++)
            menu_drawer.add(0, 70 + i, 0, dbManager.fetch_place_name(i));
        //menu.add(0, itemId, 0, title);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.btn_open_add)
                    startActivity(new Intent(MainActivity.this, Add_place.class));
                else
                    Toast.makeText(MainActivity.this, "Item " + item.getItemId() + " moment?", Toast.LENGTH_SHORT).show();
                return false;
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
        //getMenuInflater().inflate(R.menu.menu_nav_drawer, menu);
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