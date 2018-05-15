package com.example.schen162.vmonopoly;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.services.core.PoiItem;

import java.util.ArrayList;
import java.util.List;

public class POIListActivity extends AppCompatActivity {

    protected static ArrayList<PoiItem> mPOIs = null;
    protected static ArrayList<MonoPoiItem> mMonoPOIs = null;
    private  ListView listPOIs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poilist);

        if(mPOIs != null) {
            listPOIs = (ListView) findViewById(R.id.list_pois);
            mMonoPOIs = new ArrayList<MonoPoiItem>();
            //mMonoPOIs.add(new MonoPoiItem(mPOIs.get(0)));
            int j = Math.min(mPOIs.size(), 3);
            for(int i=0; i<j;i++){
                mMonoPOIs.add(new MonoPoiItem(mPOIs.get(i)));
            }
            POIAdapter adapter = new POIAdapter(POIListActivity.this, R.layout.list_poiitem, mMonoPOIs);
            listPOIs.setAdapter(adapter);
            listPOIs.setOnItemClickListener(new ListView.OnItemClickListener(){
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Toast.makeText(getApplicationContext(),"我是item点击事件 i = " + i + "l = " + l,Toast.LENGTH_SHORT).show();
                }
            });
        }else{
            Toast.makeText(getApplicationContext(), "周围没有兴趣点", Toast.LENGTH_SHORT).show();
        }
    }

    public class POIAdapter extends ArrayAdapter implements View.OnClickListener  {
        private final int resourceId;

        public POIAdapter(Context context, int textViewResourceId, List<MonoPoiItem> objects) {
            super(context, textViewResourceId, objects);
            resourceId = textViewResourceId;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            MonoPoiItem poi = (MonoPoiItem) getItem(position); // 获取当前项的Fruit实例
            View view = LayoutInflater.from(getContext()).inflate(resourceId, null);
            ImageView poiImage = (ImageView) view.findViewById(R.id.img_poi);
            TextView poiTitle = (TextView) view.findViewById(R.id.txt_PoiTitle);
            TextView poiPrice = (TextView) view.findViewById(R.id.txt_PoiPrice);
            EditText poiDesc = (EditText) view.findViewById(R.id.txt_PoiDesc);
            TextView poiOwner = (TextView) view.findViewById(R.id.txt_PoiOwner);
            Button poiBtn1 = (Button) view.findViewById(R.id.btn_poi1);
            Button poiBtn2 = (Button) view.findViewById(R.id.btn_poi2);

            poiBtn1.setTag(R.id.btn_poi1, position);
            poiBtn2.setTag(R.id.btn_poi2, position);
            poiBtn1.setOnClickListener(this);
            poiBtn2.setOnClickListener(this);
            poiImage.setImageResource(R.drawable.onsale);
            poiTitle.setText(poi.getPoi().getTitle());
            poiPrice.setText(new Integer(poi.getPrice()).toString()+"福币");
            try {
                poiDesc.setText(poi.getDesc());
                poiOwner.setText("属于 ["+poi.getOwner().getLogin()+"]");
                poiImage.setImageResource(R.drawable.sold);
            }catch (Exception e){
                poiOwner.setText("待售");
            }
            return view;
        }

        @Override
        public void onClick(View view){
            switch (view.getId()){
                case R.id.btn_poi1:
                    final int pos1=(int)view.getTag(R.id.btn_poi1);
                    //Toast.makeText(getApplicationContext(), "btn1: " + new Integer(pos1).toString(), Toast.LENGTH_SHORT).show();
                    AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
                    builder.setMessage("您想购买此地吗？");
                    AlertDialog alert = builder.create();
                    alert.show();
                    break;

                case R.id.btn_poi2:
                    int pos2=(int)view.getTag(R.id.btn_poi2);
                    Toast.makeText(getApplicationContext(), "btn2: " + new Integer(pos2).toString(), Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    }
}
