package com.example.weatherman;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;

import java.sql.SQLDataException;
import java.util.ArrayList;

public class Add_place extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_place);

        //<editor-fold>
        String api_key = Python.getInstance().getModule("wttr_api").get("API_KEY").toString();

        DatabaseManager dbManager = new DatabaseManager(this);
        Button btn_add_place = findViewById(R.id.btn_add_place);
        EditText et_placename = findViewById(R.id.et_placename);
        RecyclerView rcv_places = findViewById(R.id.rcv_places);
        ArrayList<String> places = new ArrayList<>();

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor esp = sp.edit();
        //</editor-fold>

        try {dbManager.open();} catch (SQLDataException e) {e.printStackTrace();}

        for(int i = 1; i <= dbManager.count_place(); i++) places.add(dbManager.fetch_place_name(i));

        PlaceItem placeItem = new PlaceItem(this, places);
        rcv_places.setAdapter(placeItem);
        rcv_places.setLayoutManager(new LinearLayoutManager(this));

        btn_add_place.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String place_name = et_placename.getText().toString();
                if(dbManager.fetch_place_key(place_name) == "PLACE DOESN'T EXIST"){
                    Python py = Python.getInstance();
                    PyObject wttr_api = py.getModule("wttr_api");
                    PyObject place_key = wttr_api.callAttr("get_key", api_key, place_name);

                    if(place_key.toString().length() != 6 )
                        Toast.makeText(Add_place.this, "Place doesn't exists", Toast.LENGTH_SHORT).show();
                    else {
                        dbManager.insert(place_name, place_key.toString());

                        esp.putString(getString(R.string.main_place), dbManager.fetch_place_name(place_key.toString()));
                        esp.commit();

                        Add_place.super.onBackPressed();
                        Toast.makeText(Add_place.this, "Place added", Toast.LENGTH_SHORT).show();
                    }
                }
                else Toast.makeText(Add_place.this, "Place already added", Toast.LENGTH_SHORT).show();
            }
        });

    }
}