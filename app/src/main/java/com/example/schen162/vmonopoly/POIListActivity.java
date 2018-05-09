package com.example.schen162.vmonopoly;

import android.content.Context;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.services.core.PoiItem;

import java.util.ArrayList;
import java.util.List;

public class POIListActivity extends AppCompatActivity {

    protected static ArrayList<PoiItem> mPOIs = null;
    private  ListView listPOIs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poilist);

        if(mPOIs != null) {
            listPOIs = (ListView) findViewById(R.id.list_pois);
            POIAdapter adapter = new POIAdapter(POIListActivity.this, R.layout.list_poiitem, mPOIs);
            listPOIs.setAdapter(adapter);
        }else{
            Toast.makeText(getApplicationContext(), "周围没有POI", Toast.LENGTH_SHORT).show();
        }
    }

    public class POIAdapter extends ArrayAdapter {
        private final int resourceId;

        public POIAdapter(Context context, int textViewResourceId, List<PoiItem> objects) {
            super(context, textViewResourceId, objects);
            resourceId = textViewResourceId;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            PoiItem poi = (PoiItem) getItem(position); // 获取当前项的Fruit实例
            View view = LayoutInflater.from(getContext()).inflate(resourceId, null);
            ImageView poiImage = (ImageView) view.findViewById(R.id.img_poi);
            TextView poiName = (TextView) view.findViewById(R.id.txt_PoiTitle);
            TextView poiPrice = (TextView) view.findViewById(R.id.txt_PoiPrice);
            TextView poiDesc = (TextView) view.findViewById(R.id.txt_PoiDesc);
            TextView poiOwner = (TextView) view.findViewById(R.id.txt_PoiOwner);

            poiImage.setImageResource(R.drawable.onsale);
            poiName.setText(poi.getTitle());//为文本视图设置文本内容
            return view;
        }
    }
}
