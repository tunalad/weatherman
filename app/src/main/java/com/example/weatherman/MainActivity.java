package com.example.weatherman;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.github.pwittchen.weathericonview.WeatherIconView;
import com.google.android.material.navigation.NavigationView;

import java.sql.SQLDataException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    SwipeRefreshLayout swipeRefreshLayout;

    //<editor-fold desc="UI ELEMENTS DECLARATION">
    ActionBarDrawerToggle drawerToggle;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Menu menu_drawer;

    RecyclerView rcv_hourly;
    RecyclerView rcv_daily;
    //</editor-fold>

    //<editor-fold desc="WEATHER DATA ARRAYS">
    ArrayList<String> wttr_hourly_temp = new ArrayList<>();
    ArrayList<String> wttr_hourly_time = new ArrayList<>();
    ArrayList<Integer> wttr_hourly_icons = new ArrayList<>();
    ArrayList<String> wttr_daily_temp = new ArrayList<>();
    ArrayList<String> wttr_daily_time = new ArrayList<>();
    ArrayList<Integer> wttr_daily_icons = new ArrayList<>();
    //</editor-fold>

    //<editor-fold desc="WEATHER THINGS DECLARATIONS">
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
    TextView txt_placename;
    WeatherIconView icon_wind;
    //</editor-fold>

    DatabaseManager dbManager;

    SharedPreferences sp;
    SharedPreferences.Editor esp;

    boolean metric;
    String main_place;
    ArrayList<Integer> places_list_ids;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //<editor-fold desc="IDs">
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
        txt_placename = findViewById(R.id.txt_placename);
        icon_wind = findViewById(R.id.icon_wind);
        //</editor-fold>

        rcv_daily= findViewById(R.id.rcv_daily);
        rcv_hourly = findViewById(R.id.rcv_hourly);

        dbManager = new DatabaseManager(this);

        try {dbManager.open();}
        catch (SQLDataException e) {e.printStackTrace();}

        dbManager.fetch();

        load_settings();


        //<editor-fold desc="SWIPE REFRESH">
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                load_settings();
                Toast.makeText(MainActivity.this, "Weather updated", Toast.LENGTH_SHORT).show();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        //</editor-fold>
    }
    /* GET API DATA */
    private void get_weather(String place_name){

        txt_placename.setText(place_name);

        Python py = Python.getInstance();
        PyObject wttr_api = py.getModule("wttr_api");
        String api_key = wttr_api.get("API_KEY").toString();

        // GET PLACE KEY
        Log.d("get_weather:", "'" + place_name + "'");
        String place_key = dbManager.fetch_place_key(place_name);

        //<editor-fold desc="CURRENT WEATHER">
        PyObject current_data = (wttr_api.callAttr("current", api_key, place_key, metric)).callAttr("response");
        PyObject crt_temp = wttr_api.callAttr("crt_temp", current_data);
        PyObject crt_max = wttr_api.callAttr("crt_max", current_data);
        PyObject crt_min = wttr_api.callAttr("crt_min", current_data);
        PyObject crt_text = wttr_api.callAttr("crt_text", current_data);

        txt_current.setText(crt_temp.toString() + "°");
        txt_maxmin.setText(crt_max.toString() + "°/" + crt_min.toString() + "°");
        txt_status.setText(crt_text.toString());

        if(!metric){
            txt_current.setText(c_to_f(crt_temp.toFloat()) + "°");
            txt_maxmin.setText(c_to_f(crt_max.toFloat()) + "/" +
                    c_to_f(crt_min.toFloat()));
        }
        //</editor-fold>

        //<editor-fold desc="HOURLY WEATHER">
        PyObject hourly_data = (wttr_api.callAttr("hourly", api_key, place_key, metric)).callAttr("response");
        PyObject hrs_temp, hrs_time, hrs_icon;
        wttr_hourly_temp.clear();
        wttr_hourly_time.clear();

        for(int i = 0; i < 12; i++){
            hrs_temp = wttr_api.callAttr("hrs_temp", hourly_data, i);
            hrs_time = wttr_api.callAttr("hrs_time", hourly_data, i);
            hrs_icon = wttr_api.callAttr("hrs_icon", hourly_data, i);
            if(!metric)
                wttr_hourly_temp.add(c_to_f(hrs_temp.toFloat()) + "°");
            else wttr_hourly_temp.add(hrs_temp.toString() + "°");
            wttr_hourly_time.add(hrs_time.toString());
            wttr_hourly_icons.add(hrs_icon.toInt());
        }

        WeatherItem wttr_hourly = new WeatherItem(this, wttr_hourly_time, wttr_hourly_temp, wttr_hourly_icons);
        rcv_hourly.setAdapter(wttr_hourly);
        rcv_hourly.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        //</editor-fold>

        //<editor-fold desc="DAILY WEATHER">
        PyObject daily_data = (wttr_api.callAttr("daily", api_key, place_key, metric)).callAttr("response");
        PyObject day_max, day_min, day_date, day_icon;
        wttr_daily_time.clear();
        wttr_daily_temp.clear();
        for(int i = 0; i < 5; i++){
            day_max = wttr_api.callAttr("day_max", daily_data, i);
            day_min = wttr_api.callAttr("day_min", daily_data, i);
            day_date = wttr_api.callAttr("day_date", daily_data, i);
            day_icon = wttr_api.callAttr("day_icon", daily_data, i);
            if(!metric)
                wttr_daily_temp.add(c_to_f(day_max.toFloat()) + "°\n" +
                                    c_to_f(day_min.toFloat()) + "°");
            else
                wttr_daily_temp.add(" " + day_max.toString() + "°\n " + day_min.toString() + "°");

            // From date to day
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            try {
                Date d = dateFormat.parse(day_date.toString());
                String final_date = new SimpleDateFormat("EEE").format(d);
                wttr_daily_time.add(final_date);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            //wttr_daily_time.add(day_date.toString());
            wttr_daily_icons.add(day_icon.toInt());
        }

        WeatherItem wttr_daily= new WeatherItem(this, wttr_daily_time, wttr_daily_temp, wttr_daily_icons);
        rcv_daily.setAdapter(wttr_daily);
        rcv_daily.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        //</editor-fold>

        //<editor-fold desc="CURRENT WEATHER DETAILS">
        PyObject det_pressure = wttr_api.callAttr("det_pressure", current_data);
        PyObject det_humidity = wttr_api.callAttr("det_humidity", current_data);
        PyObject det_clouds = wttr_api.callAttr("det_clouds", current_data);
        PyObject det_visibility = wttr_api.callAttr("det_visibility", current_data);

        txt_pressure.setText("Pressure: \t" + det_pressure.toString()+"mb");
        txt_humidity.setText("Humidity: \t" + det_humidity.toString()+"%");
        txt_clouds.setText("Clouds: \t" + det_clouds.toString()+"%");
        txt_visibility.setText("Visibility: \t" + det_visibility+"km");

        PyObject wind_speed= wttr_api.callAttr("wind_speed", current_data);
        PyObject wind_dir = wttr_api.callAttr("wind_dir", current_data);
        txt_wind_direction.setText(wind_dir.toString());
        txt_wind_speed.setText(wind_speed.toString());
        //icon_wind.setIconResource(getString(R.string.)); // Todo: make compass icon spin
        //</editor-fold>

        // UPDATE TIME
        Calendar time = Calendar.getInstance();
        String time_hrs = time.get(Calendar.HOUR_OF_DAY) + ":" + time.get(Calendar.MINUTE);
        txt_updated.setText("Updated at: " + time_hrs);
    }

    String c_to_f(float value){
        return String.format("%.1f", ((value * 1.8) + 32));
    }

    /* NAVIGATION DRAWER MENU */
    @SuppressLint("LongLogTag")
    private void nav_drawer(){
        navigationView = findViewById(R.id.navigationView);
        drawerLayout = findViewById(R.id.drawerLayout);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);

        places_list_ids = new ArrayList<>();
        menu_drawer = navigationView.getMenu();

        /////////////////////////////////////////////////

        places_list_ids.removeAll(places_list_ids);

        Cursor crs = dbManager.fetch();
        crs.moveToFirst();

        for(int i= 1; i<= crs.getCount(); i++){
            places_list_ids.add(crs.getInt(0));
            crs.moveToNext();
        }

        for(int i= 0; i < places_list_ids.size(); i++){
            if(menu_drawer.findItem(i+70) == null)
                menu_drawer.add(0, i + 70, 0, dbManager.fetch_place_name(places_list_ids.get(i)));
            else
                Log.d("MENU_DRAWER ITEM ID: " + i+70 , menu_drawer.findItem(i+70).toString());
        }

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                sp = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                esp = sp.edit();

                main_place = sp.getString(getString(R.string.main_place), "noplaceselected");

                if (item.getItemId() == R.id.btn_open_add)
                    startActivity(new Intent(MainActivity.this, Add_place.class));
                else{
                    // key changed for 2 because of the above for loops I assume?? Todo: do menu keys properly
                    esp.putString(getString(R.string.main_place), dbManager.fetch_place_name( (item.getItemId() - 68) ));
                    esp.commit();
                    get_weather(dbManager.fetch_place_name(item.getItemId() - 68));
                    drawerLayout.closeDrawers();
                }
                return false;
            }
        });
    }

    /* LOAD SETTINGS */
    private void load_settings(){
        sp = PreferenceManager.getDefaultSharedPreferences(this);
        esp = sp.edit();

        boolean is_metric = sp.getBoolean("sp_metric", true);
        main_place = sp.getString(getString(R.string.main_place), "noplaceselected");

        metric = is_metric;

        if(main_place == "noplaceselected")
            Toast.makeText(this, "No place selected", Toast.LENGTH_SHORT).show();
        else get_weather(main_place);

        nav_drawer();
    }

    @Override
    protected void onResume() {
        load_settings();
        super.onResume();
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

}