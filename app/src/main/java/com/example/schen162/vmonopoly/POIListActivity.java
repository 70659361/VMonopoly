package com.example.schen162.vmonopoly;

import android.app.AlertDialog;
import android.app.Application;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.UserManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewDebug;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
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
    private TextView txCoins;
    private List<Map<String, Object>> data_list;
    private SimpleAdapter sim_adapter;

    private int[] icons;
    private String[] prices;
    private String[] iconName;
    private String[] ids;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poilist);
        grid_POIs = (GridView) findViewById(R.id.grid_pois);
        txCoins = (TextView) findViewById(R.id.txt_coins);

        init();
    }

    private void init(){
        data_list = new ArrayList<Map<String, Object>>();
        String [] from ={"title", "image","price"};
        int [] to = {R.id.title, R.id.image, R.id.price};

        if(0 < pois.size()) {
            int sz = pois.size();
            iconName = new String[sz];
            icons = new int[sz];
            prices = new String[sz];

            ids = new String[sz];
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
                        String jid = respJsonArr.getJSONObject(j).getString("poiid");
                        iconName[i]=pois.get(i).getTitle();
                        if(ids[i].equals(jid)){
                            icons[i] = R.drawable.sold;
                            prices[i] = respJsonArr.getJSONObject(j).getString("poiprice")+"福币";
                            continue;
                        }else{
                            icons[i] = R.drawable.onsale;
                            prices[i] = "100福币";
                            continue;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                iconName[i]=pois.get(i).getTitle();
                icons[i] = R.drawable.onsale;
                prices[i] = "100福币";
                continue;
            }
            getData();
            sim_adapter = new SimpleAdapter(this, data_list, R.layout.grid_poiitem, from, to);
            grid_POIs.setAdapter(sim_adapter);
            grid_POIs.setOnItemClickListener(this);
        }else{
            Toast.makeText(getApplicationContext(), "周围没有POI", Toast.LENGTH_SHORT).show();
        }

        txCoins.setText("当前福币："+UserManage.getInstance().getCoins());
    }

    @Override
    public void onItemClick(AdapterView<?> adapter, View view, final int position, long rowid) {

        HashMap<String, Object> item = (HashMap<String, Object>) adapter.getItemAtPosition(position);
        int icon=(int)item.get("image");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        if(icon == R.drawable.onsale){
            builder.setMessage("您想购买此地吗？").setPositiveButton("是", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    String stPrice=prices[position].substring(0, prices[position].lastIndexOf("福"));
                    int inPrice=Integer.parseInt(stPrice);
                    if(inPrice > UserManage.getInstance().getCoins()){
                        Toast.makeText(getApplicationContext(), "对不起，您的福币不够。", Toast.LENGTH_SHORT).show();
                    }else{
                        if(true == PoiManage.getInstance().buyPOI(UserManage.getInstance().getUser(), ids[position], stPrice)){
                            Toast.makeText(getApplicationContext(), "购买成功！", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }).setNegativeButton("否", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                }
            });
        }else{
            builder.setMessage("这块地已经被买走了，看看其他的吧。");
        }
        AlertDialog alert = builder.create();
        alert.show();
    }

    private List<Map<String, Object>> getData(){
        for(int i=0;i<icons.length;i++){
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("title", iconName[i]);
            map.put("image", icons[i]);
            map.put("price", prices[i]);
            map.put("id", ids[i]);
            data_list.add(map);
        }
        return data_list;
    }
}
