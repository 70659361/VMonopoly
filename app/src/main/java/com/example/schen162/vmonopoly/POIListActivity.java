package com.example.schen162.vmonopoly;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewDebug;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.amap.api.services.core.PoiItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class POIListActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    protected static ArrayList<PoiItem> pois = null;
    private GridView grid_POIs;
    private List<Map<String, Object>> data_list;
    private SimpleAdapter sim_adapter;
    private int[] icons;
    private String[] prices;
    private String[] iconName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poilist);
        grid_POIs = (GridView) findViewById(R.id.grid_pois);

        init();
    }

    private void init(){
        data_list = new ArrayList<Map<String, Object>>();
        String [] from ={"title", "image","price"};
        int [] to = {R.id.title, R.id.image, R.id.price};

        if(null != pois) {
            int sz = pois.size();
            iconName = new String[sz];
            icons = new int[sz];
            prices = new String[sz];

            String[] ids = new String[sz];
            for(int i=0; i<sz; i++){
                ids[i] = pois.get(i).getPoiId();
            }
            JSONArray respJsonArr=null;
            int respSz = 0;

            try {
                String respJson = PoiManage.getInstance().getPOIs(ids);
                respJsonArr = new JSONArray(respJson);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if(null != respJsonArr){
                respSz = respJsonArr.length();
            }

            for(int i=0; i<sz; i++){
                for(int j=0; j<respSz; j++ ){
                    try {
                        if(pois.get(i).getPoiId() == respJsonArr.getJSONObject(j).getString("poiid")){
                            icons[i] = R.drawable.onsale;
                            prices[i] = "100福币";
                        }else{
                            icons[i] = R.drawable.sold;
                            prices[i] = respJsonArr.getJSONObject(j).getString("poiprice");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                iconName[i] = pois.get(i).getTitle();
            }
            getData();
            sim_adapter = new SimpleAdapter(this, data_list, R.layout.grid_poiitem, from, to);
            grid_POIs.setAdapter(sim_adapter);
            grid_POIs.setOnItemClickListener(this);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapter, View view, int position, long rowid) {

        HashMap<String, Object> item = (HashMap<String, Object>) adapter.getItemAtPosition(position);
        String itemText=(String)item.get("title");
        Toast.makeText(this.getApplicationContext(), "You Select "+itemText, Toast.LENGTH_SHORT).show();

    }

    private List<Map<String, Object>> getData(){
        //cion和iconName的长度是相同的，这里任选其一都可以
        for(int i=0;i<icons.length;i++){
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("title", iconName[i]);
            map.put("image", icons[i]);
            map.put("price", prices[i]);
            data_list.add(map);
        }
        return data_list;
    }
}
