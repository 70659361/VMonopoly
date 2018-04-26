package com.example.schen162.vmonopoly;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.amap.api.maps.AMap;
import com.amap.api.maps.MapView;


public class MonoMainActivity extends AppCompatActivity {

    MapView mMapView = null;
    AMap aMap = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mono_main);

        mMapView = (MapView) findViewById(R.id.map);
        mMapView.onCreate(savedInstanceState);


        if (aMap == null) {
            aMap = mMapView.getMap();
        }
    }
}
