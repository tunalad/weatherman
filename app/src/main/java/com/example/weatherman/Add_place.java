package com.example.weatherman;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;

import java.sql.SQLDataException;

public class Add_place extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_place);

        String api_key = "tGfoexE52HG7mcnyCrV697NeITDjvCGA";

        DatabaseManager dbManager = new DatabaseManager(this);
        Button btn_add_place = findViewById(R.id.btn_add_place);
        EditText et_placename = findViewById(R.id.et_placename);

        try {dbManager.open();} catch (SQLDataException e) {e.printStackTrace();}

        btn_add_place.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String place_name = et_placename.getText().toString();
                if(dbManager.fetch_place_key(place_name) == null){
                    Python py = Python.getInstance();
                    PyObject wttr_api = py.getModule("wttr_api");

                    // GET PLACE KEY
                    PyObject place_key = wttr_api.callAttr("get_key", api_key, place_name);
                    dbManager.insert(place_name, place_key.toString());
                    Toast.makeText(Add_place.this, "Place added", Toast.LENGTH_SHORT).show();
                }
                else
                    Toast.makeText(Add_place.this, "Place already exists", Toast.LENGTH_SHORT).show();
            }
        });
    }
}