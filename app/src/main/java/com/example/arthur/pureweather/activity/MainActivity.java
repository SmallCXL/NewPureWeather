package com.example.arthur.pureweather.activity;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.arthur.pureweather.R;
import com.example.arthur.pureweather.db.PureWeatherDB;
import com.example.arthur.pureweather.utils.CheckVersion;


public class MainActivity extends AppCompatActivity {

    private PureWeatherDB pureWeatherDB;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pureWeatherDB = PureWeatherDB.getInstance(this);

        TextView result = ((TextView) findViewById(R.id.check_result));
        Button checkVersion = ((Button) findViewById(R.id.check_version));
        checkVersion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                CheckVersion.checkForUpdate(MainActivity.this,"");
                String versionName = CheckVersion.getVersionName(MainActivity.this);
                result.setText(versionName);

            }
        });
    }

}
