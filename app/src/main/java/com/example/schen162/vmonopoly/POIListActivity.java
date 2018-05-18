package com.example.schen162.vmonopoly;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
    private ListView listPOIs;
    private TextView txtCurCoins;
    private ProgressDialog progDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poilist);

        txtCurCoins = (TextView) findViewById(R.id.txt_poilist_coins);
        txtCurCoins.setText("当前福币： "+UserManage.getInstance().getCoins());

        showProgressDialog();
        showPOIs();
        dissmissProgressDialog();
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
            final Button poiBtn3 = (Button) view.findViewById(R.id.btn_poi3);

            poiBtn1.setTag(R.id.btn_poi1, position);
            poiBtn2.setTag(R.id.btn_poi2, position);
            poiBtn3.setTag(R.id.btn_poi3, position);
            poiBtn1.setOnClickListener(this);
            poiBtn2.setOnClickListener(this);
            poiBtn3.setOnClickListener(this);
            //poiDesc.

            poiTitle.setText(poi.getPoi().getTitle());
            poiPrice.setText(new Integer(poi.getPrice()).toString()+"福币");
            try {
                //sold POI
                poiOwner.setText("属于 ["+poi.getOwner().getLogin()+"]");
                poiImage.setImageResource(R.drawable.sold);
                if("null" != poi.getDesc()) {
                    poiDesc.setText(poi.getDesc());
                }
                String ownerUser = poi.getOwner().getLogin();
                String curUser= UserManage.getInstance().getUser();
                //own POI
                if(ownerUser.equals(curUser)){
                    poiBtn1.setVisibility(View.INVISIBLE);
                    poiBtn2.setVisibility(View.VISIBLE);
                    poiBtn3.setVisibility(View.VISIBLE);
                    poiDesc.setFocusable(true);
                    poiDesc.setFocusableInTouchMode(true);
                    poiDesc.setHint("可为您的地产添加信息");
                    poiDesc.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                            //TODO:
                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            //TODO:
                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                            poiBtn3.setTag(R.id.btn_poi3+1, s.toString());
                        }
                    });
                }else {  //Other's POI
                    poiBtn1.setVisibility(View.INVISIBLE);
                    poiBtn2.setVisibility(View.INVISIBLE);
                    poiBtn3.setVisibility(View.INVISIBLE);
                    poiDesc.setFocusable(false);
                    poiDesc.setFocusableInTouchMode(false);
                }
            }catch (Exception e){
                //on sale poi
                poiImage.setImageResource(R.drawable.onsale);
                poiBtn1.setVisibility(View.VISIBLE);
                poiBtn2.setVisibility(View.INVISIBLE);
                poiBtn3.setVisibility(View.INVISIBLE);
                poiOwner.setText("待售");
                poiDesc.setFocusable(false);
                poiDesc.setFocusableInTouchMode(false);
            }
            return view;
        }

        @Override
        public void onClick(View view){
            switch (view.getId()){
                case R.id.btn_poi1:
                    final int pos1=(int)view.getTag(R.id.btn_poi1);
                    //Toast.makeText(getApplicationContext(), "btn1: " + new Integer(pos1).toString(), Toast.LENGTH_SHORT).show();
                    openDialog(pos1);
                    break;

                case R.id.btn_poi2:
                    int pos2=(int)view.getTag(R.id.btn_poi2);
                    Toast.makeText(getApplicationContext(), "btn2: " + new Integer(pos2).toString(), Toast.LENGTH_SHORT).show();
                    break;

                case R.id.btn_poi3:
                    int pos3=(int)view.getTag(R.id.btn_poi3);
                    String desc=(String)view.getTag(R.id.btn_poi3+1);
                    updateDescption(pos3, desc);
                    break;
                default:
                    break;
            }
        }
    }

    private void openDialog(final int pos){
        AlertDialog.Builder adInfo=new AlertDialog.Builder(this);
        //adInfo.setTitle("简单对话框");
        adInfo.setMessage("您想购买此地吗");
        adInfo.setIcon(R.drawable.onsale);
        adInfo.setPositiveButton("是", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int inPrice=mMonoPOIs.get(pos).getPrice();
                if(inPrice > UserManage.getInstance().getCoins()){
                    Toast.makeText(getApplicationContext(), "对不起，您的福币不够。", Toast.LENGTH_SHORT).show();
                }else{
                    if(true == PoiManage.getInstance().buyPOI(
                            UserManage.getInstance().getUser(), mMonoPOIs.get(pos).getPoi().getPoiId(), inPrice)){
                        Toast.makeText(getApplicationContext(), "购买成功！", Toast.LENGTH_SHORT).show();
                        showPOIs();
                    }
                }                // 根据实际情况编写相应代码。
            }
        });
        //为“取消”按钮注册监听事件
        adInfo.setNegativeButton("否", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 根据实际情况编写相应代码。
            }
        });
        adInfo.create();
        adInfo.show();
    }

    private void updateDescption(int pos, String desc){
       String pid = mPOIs.get(pos).getPoiId();
       if(PoiManage.getInstance().updatePOIDescription(pid, desc)){
           Toast.makeText(getApplicationContext(),"更新成功", Toast.LENGTH_SHORT).show();
       }else{
           Toast.makeText(getApplicationContext(),"更新失败", Toast.LENGTH_SHORT).show();
       }
    }

    private void showProgressDialog() {
        if (progDialog == null)
            progDialog = new ProgressDialog(this);
        progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progDialog.setIndeterminate(false);
        progDialog.setCancelable(true);
        progDialog.setMessage("加载中...");
        progDialog.show();
    }

    private void dissmissProgressDialog() {
        if (progDialog != null) {
            progDialog.dismiss();
        }
    }

    private Handler poiHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    showPOIs();
                    break;
            }
        }
    };

    private void showPOIs(){
        if(mPOIs != null) {
            listPOIs = (ListView) findViewById(R.id.list_pois);
            mMonoPOIs = new ArrayList<MonoPoiItem>();
            int j = Math.min(mPOIs.size(), 3);
            for(int i=0; i<j;i++){
                mMonoPOIs.add(new MonoPoiItem(mPOIs.get(i)));
            }
            POIAdapter adapter = new POIAdapter(POIListActivity.this, R.layout.list_poiitem, mMonoPOIs);
            listPOIs.setAdapter(adapter);
            listPOIs.setOnItemClickListener(new ListView.OnItemClickListener(){
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    //Toast.makeText(getApplicationContext(),"我是item点击事件 i = " + i + "l = " + l,Toast.LENGTH_SHORT).show();
                }
            });
        }else{
            Toast.makeText(getApplicationContext(), "周围没有兴趣点", Toast.LENGTH_SHORT).show();
        }
    }
}
