package com.example.schen162.vmonopoly;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ViewDebug;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.amap.api.services.core.PoiItem;

import java.util.ArrayList;

public class POIListActivity extends AppCompatActivity {

    protected static ArrayList<PoiItem> pois;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poilist);

        init();
    }

    private void init(){
        Intent intent = getIntent();

        int n = pois.size();
        String[] data = new String[n];

        if(null != pois){
            for(int i=0; i<n; i++) {
                 data[i] = Integer.toString(i+1) + ".  " + pois.get(i).getTitle();
            }
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                POIListActivity.this, android.R.layout.simple_list_item_1, data);
        ListView listView = (ListView) findViewById(R.id.list_pois);
        listView.setAdapter(adapter);
    }
}
