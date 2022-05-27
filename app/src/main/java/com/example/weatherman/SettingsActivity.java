package com.example.weatherman;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.widget.Toast;

public class SettingsActivity extends PreferenceActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
        load_settings();
    }

    private void load_settings(){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        //boolean is_metric = sp.getBoolean("sp_metric", true);

        SwitchPreference sp_metric = (SwitchPreference)findPreference("sp_metric");
        sp_metric.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference pref, Object obj) {
                boolean checked = (boolean)obj;
                if(checked)
                    Toast.makeText(SettingsActivity.this, "Metric values enabled", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(SettingsActivity.this, "Metric values disabled", Toast.LENGTH_SHORT).show();

                return true;
            }
        });
    }

    @Override
    protected void onResume() {
        load_settings();
        super.onResume();
    }
}
