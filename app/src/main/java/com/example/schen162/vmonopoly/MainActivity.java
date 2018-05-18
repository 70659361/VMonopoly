package com.example.schen162.vmonopoly;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.BitmapDescriptor;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.Circle;
import com.amap.api.maps2d.model.CircleOptions;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.LatLngBounds;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.maps2d.model.MyLocationStyle;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.amap.api.services.poisearch.PoiSearch.OnPoiSearchListener;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RideRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.WalkRouteResult;
import com.amap.api.services.routepoisearch.RoutePOISearch;
import com.amap.api.services.routepoisearch.RoutePOISearchQuery;
import com.amap.api.services.routepoisearch.RoutePOISearchResult;
//import com.example.schen162.vmonopoly.util.AMapUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static com.amap.api.services.routepoisearch.RoutePOISearch.RoutePOISearchType.TypeGasStation;

public class MainActivity extends AppCompatActivity implements
        OnPoiSearchListener, RouteSearch.OnRouteSearchListener,
        RoutePOISearch.OnRoutePOISearchListener,AMapLocationListener {


    private PoiSearch poiSearch;// POI搜索
    private PoiSearch.Query query;// Poi查询条件类
    private TextView txCurLoc;
    private TextView txCurUser;
    private TextView txCurCoins;
    private TextView txCurMileage;
    private ImageButton btnWalk;
    private AlertDialog mDialog;

    private com.amap.api.maps2d.MapView mapView;
    private com.amap.api.maps2d.AMap aMap;
    private MyLocationStyle myLocationStyle;
    private AMapLocationClient mlocationClient;
    private AMapLocationClientOption mLocationOption;
    private AMapLocation mCurLocation;
    private AMapLocation mStartLocation;
    private AMapLocation mEndLocation;
    private ProgressDialog progDialog;

    private static final int STROKE_COLOR = Color.argb(10,15,15,15);
    private static final int FILL_COLOR = Color.argb(10,15,15,15);

    private Circle circle;
    private myPoiOverlay poiOverlay;
    private int[] markers = {
            R.drawable.poi_marker_1, R.drawable.poi_marker_2,R.drawable.poi_marker_3,
            R.drawable.poi_marker_4, R.drawable.poi_marker_5,R.drawable.poi_marker_6};
    private ArrayList<PoiItem> mPoiItems;
    private ArrayList<MonoPoiItem> mMyPoiItems;
    private int mCurIndex;

    private boolean mIsWalking=false;
    private RouteSearch mRouteSearch;
    private int mileage;
    private TimerTask task;
    private Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mapView = (com.amap.api.maps2d.MapView) findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);// 此方法必须重写

        txCurLoc = (TextView) findViewById(R.id.txt_curLocation);
        txCurCoins = (TextView) findViewById(R.id.txt_curCoins);
        txCurUser = (TextView) findViewById(R.id.txt_curUser);
        txCurMileage = (TextView) findViewById(R.id.txt_mileage);
        btnWalk = (ImageButton) findViewById(R.id.btn_walk);

        init();
    }

    protected void init() {
        if (aMap == null) {
            aMap = mapView.getMap();
        }
        mDialog = null;

        txCurCoins.setText("当前福币: " + new Integer(UserManage.getInstance().getCoins()).toString());
        txCurUser.setText("欢迎: "+ UserManage.getInstance().getUser());
        txCurLoc.setHint("正在获取当前位置...");
        txCurMileage.setText("0");

        myLocationStyle = new MyLocationStyle();
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_FOLLOW);
        myLocationStyle.showMyLocation(true);

        aMap.setMyLocationStyle(myLocationStyle);
        aMap.setMyLocationEnabled(true);
        aMap.moveCamera(CameraUpdateFactory.zoomTo(15));
        aMap.getUiSettings().setMyLocationButtonEnabled(true);
        aMap.setMyLocationEnabled(true);

        mLocationOption = new AMapLocationClientOption();
        mLocationOption.setInterval(2000);
        mlocationClient = new AMapLocationClient(this);
        mlocationClient.setLocationListener(this);
        mlocationClient.setLocationOption(mLocationOption);
        mlocationClient.startLocation();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
        mapView.onDestroy();
    }
    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView.onResume ()，重新绘制加载地图
        mapView.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView.onPause ()，暂停地图的绘制
        mapView.onPause();
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，保存地图当前的状态
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onPoiItemSearched(PoiItem item, int rCode) {
        if(null != item) {
            double lat = item.getLatLonPoint().getLatitude();
            double lon = item.getLatLonPoint().getLongitude();
            MonoPoiItem it = mMyPoiItems.get(mCurIndex);
            String content= "["+it.getOwner().getLogin()+"]"
                    +item.getTitle()+" \n--"+it.getPrice()+ "福币   \n--" + it.getDesc();
            MarkerOptions markerOption = new MarkerOptions()
                    .position(new LatLng(lat, lon)).title(content)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.mypoi));
            aMap.addMarker(markerOption);
        }
    }

    @Override
    public void onPoiSearched(PoiResult result, int rCode) {
        if(result != null) {
            mPoiItems = result.getPois();
            POIListActivity.mPOIs = mPoiItems;

            poiOverlay = new myPoiOverlay(aMap, mPoiItems);
            poiOverlay.addToMap();

            mDialog.cancel();

            AlertDialog.Builder adInfo = new AlertDialog.Builder(this);
            int numPois = mPoiItems.size();
            adInfo.setMessage("发现周边" + new Integer(numPois).toString() + "处地产");
            adInfo.setPositiveButton("DICE", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(getApplicationContext(),POIListActivity.class);
                    startActivity(intent);
                }
            });
            adInfo.setNegativeButton("放弃", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if(null != poiOverlay) {
                        poiOverlay.removeFromMap();
                    }
                }
            });
            mDialog=adInfo.create();
            mDialog.show();
        }else {
            Toast.makeText(getApplicationContext(), "周围没有兴趣点", Toast.LENGTH_SHORT).show();
        }
    }

    private void doKeywordSearchPOI(){
        query = new PoiSearch.Query(AppConfig.SEARCH_KEYWORDS, "", AppConfig.SEARCH_CITY);
        query.setPageSize(AppConfig.SEARCH_POI_NUM);// 设置每页最多返回多少条poiitem
        query.setPageNum(AppConfig.SEARCH_POI_PAGE);// 设置查第一页
        query.setCityLimit(true);

        poiSearch = new PoiSearch(this, query);
        poiSearch.setOnPoiSearchListener(this);

        if(null != mCurLocation) {
            poiSearch.setBound(new PoiSearch.SearchBound(new LatLonPoint(mCurLocation.getLatitude(),
                    mCurLocation.getLongitude()), AppConfig.SEARCH_RADIUS));
        }
        poiSearch.searchPOIAsyn();
    }

    private void doRouteSearchPOI(){
        if(null != mStartLocation && null != mEndLocation){
            mRouteSearch = new RouteSearch(this);
            mRouteSearch.setRouteSearchListener(this);
            int mode=0;
            LatLonPoint mStartPoint = new LatLonPoint(mStartLocation.getLatitude(), mStartLocation.getLongitude());
            LatLonPoint mEndPoint = new LatLonPoint(mEndLocation.getLatitude(), mEndLocation.getLongitude());
            RoutePOISearchQuery query = new RoutePOISearchQuery(mStartPoint ,mEndPoint, mode, TypeGasStation, 250);
            final RoutePOISearch search = new RoutePOISearch(this, query);

            search.setPoiSearchListener(this);
            search.searchRoutePOIAsyn();

            /*
            showProgressDialog();

            final RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(
                    mStartPoint, mEndPoint);
            RouteSearch.WalkRouteQuery query = new RouteSearch.WalkRouteQuery(fromAndTo);
            try {
                mRouteSearch.calculateWalkRoute(query);
            } catch (AMapException e) {
                e.printStackTrace();
            }
            */
        }
    }

    public void onMypoiPressed(View view) {
        int userid = UserManage.getInstance().getUserID();
        String jsObj = PoiManage.getInstance().getPOIbyUser(userid);
        //Toast.makeText(getApplicationContext(), jsObj, Toast.LENGTH_LONG).show();

        try {
            JSONArray jsResp = new JSONArray(jsObj);
            mMyPoiItems=new ArrayList<MonoPoiItem>();
            for(int i=0; i<jsResp.length();i++) {
                JSONObject poiObj = jsResp.getJSONObject(i);
                MonoPoiItem myPoi = new MonoPoiItem(poiObj);
                mMyPoiItems.add(i, myPoi);
                String poiid=poiObj.getString("poiid");
                PoiSearch poiSearch = new PoiSearch(this, null);
                poiSearch.setOnPoiSearchListener(this);
                mCurIndex=i;
                poiSearch.searchPOIIdAsyn(poiid);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if(null != circle){ circle.remove();}
        mCurLocation=aMapLocation;
        updateLocation();
    }

    private void updateLocation() {
        if(null != mCurLocation){
            txCurLoc.setText("当前位置："+mCurLocation.getPoiName());
            LatLng latlong = new LatLng(mCurLocation.getLatitude(), mCurLocation.getLongitude());
            circle = aMap.addCircle(new CircleOptions().center(latlong).radius(AppConfig.SEARCH_RADIUS).strokeColor(STROKE_COLOR)
                    .fillColor(FILL_COLOR).strokeWidth(25));
        }else {
            txCurLoc.setHint("获取当前位置...");
        }
    }

    @Override
    public void onBusRouteSearched(BusRouteResult busRouteResult, int i) {

    }

    @Override
    public void onDriveRouteSearched(DriveRouteResult driveRouteResult, int i) {

    }

    @Override
    public void onWalkRouteSearched(WalkRouteResult walkRouteResult, int i) {

    }

    @Override
    public void onRideRouteSearched(RideRouteResult rideRouteResult, int i) {

    }

    @Override
    public void onRoutePoiSearched(RoutePOISearchResult routePOISearchResult, int i) {

    }

    private class myPoiOverlay {
        private AMap mamap;
        private List<PoiItem> mPois;
        private ArrayList<Marker> mPoiMarks = new ArrayList<Marker>();

        public myPoiOverlay(AMap amap, List<PoiItem> pois) {
            mamap = amap;
            mPois = pois;
        }

        public void addToMap() {
            if(mPois != null) {
                int size = mPois.size();
                for (int i = 0; i < size; i++) {
                    Marker marker = mamap.addMarker(getMarkerOptions(i));
                    PoiItem item = mPois.get(i);
                    marker.setObject(item);
                    mPoiMarks.add(marker);
                }
            }
        }

        public void removeFromMap() {
            for (Marker mark : mPoiMarks) {
                mark.remove();
            }
        }

        public void zoomToSpan() {
            if (mPois != null && mPois.size() > 0) {
                if (mamap == null)
                    return;
                LatLngBounds bounds = getLatLngBounds();
                mamap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
            }
        }

        private LatLngBounds getLatLngBounds() {
            LatLngBounds.Builder b = LatLngBounds.builder();
            if(mPois != null) {
                int size = mPois.size();
                for (int i = 0; i < size; i++) {
                    b.include(new LatLng(mPois.get(i).getLatLonPoint().getLatitude(),
                            mPois.get(i).getLatLonPoint().getLongitude()));
                }
            }
            return b.build();
        }

        private MarkerOptions getMarkerOptions(int index) {
            return new MarkerOptions()
                    .position(
                            new LatLng(mPois.get(index).getLatLonPoint()
                                    .getLatitude(), mPois.get(index)
                                    .getLatLonPoint().getLongitude()))
                    .title(getTitle(index)).snippet(getSnippet(index))
                    .icon(getBitmapDescriptor(index));
        }

        protected String getTitle(int index) {
            return mPois.get(index).getTitle();
        }

        protected String getSnippet(int index) {
            return mPois.get(index).getSnippet();
        }

        public int getPoiIndex(Marker marker) {
            for (int i = 0; i < mPoiMarks.size(); i++) {
                if (mPoiMarks.get(i).equals(marker)) {
                    return i;
                }
            }
            return -1;
        }

        public PoiItem getPoiItem(int index) {
            if (index < 0 || index >= mPois.size()) {
                return null;
            }
            return mPois.get(index);
        }

        protected BitmapDescriptor getBitmapDescriptor(int arg0) {
            if (arg0 < 6) {
                BitmapDescriptor icon = BitmapDescriptorFactory.fromBitmap(
                        BitmapFactory.decodeResource(getResources(), markers[arg0]));
                return icon;
            }else {
                BitmapDescriptor icon = BitmapDescriptorFactory.fromBitmap(
                        BitmapFactory.decodeResource(getResources(), R.drawable.poi_marker_pressed));
                return icon;
            }
        }
    }

    public boolean onWalkPressed(View view) {
        toggleWalk();
        return true;
    }

    private void toggleWalk(){
        if (mIsWalking){
            btnWalk.setBackgroundColor(Color.RED);
            mEndLocation = mCurLocation;

            double lat = mEndLocation.getLatitude();
            double lon = mEndLocation.getLongitude();

            MarkerOptions markerOption = new MarkerOptions()
                    .position(new LatLng(lat, lon)).title(mEndLocation.getPoiName())
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.amap_end));
            aMap.addMarker(markerOption);

            stopEarnCoin();
        }else {
            btnWalk.setBackgroundColor(Color.GREEN);
            mStartLocation = mCurLocation;
            aMap.clear();

            double lat = mStartLocation.getLatitude();
            double lon = mStartLocation.getLongitude();

            MarkerOptions markerOption = new MarkerOptions()
                    .position(new LatLng(lat, lon)).title(mStartLocation.getPoiName())
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.amap_start));
            aMap.addMarker(markerOption);

            startEarnCoin();
        }
        mIsWalking = !mIsWalking;
    }

    private void showProgressDialog() {
        if (progDialog == null)
            progDialog = new ProgressDialog(this);
        progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progDialog.setIndeterminate(false);
        progDialog.setCancelable(true);
        progDialog.setMessage("正在搜索");
        progDialog.show();
    }

    private void dissmissProgressDialog() {
        if (progDialog != null) {
            progDialog.dismiss();
        }
    }

    private void startEarnCoin(){
        timer = new Timer();
        mileage=0;
        task = new TimerTask() {
            public void run() {
                mileage++;
                Message msg = new Message();
                msg.what=1;
                mainHandler.sendMessage(msg);
            }
        };
        timer.schedule(task, 1000, 1000);

        Toast.makeText(getApplicationContext(),"本次行程开始", Toast.LENGTH_LONG).show();
    }

    private void stopEarnCoin(){
        timer.cancel();
        int newCoins=UserManage.getInstance().getCoins()+mileage;
        UserManage.getInstance().updateCoins(newCoins);

        Toast.makeText(this, "恭喜获得"+new Integer(mileage).toString()+"福币", Toast.LENGTH_SHORT).show();
        txCurCoins.setText(new Integer(newCoins).toString()+"福币");

        doKeywordSearchPOI();

        AlertDialog.Builder adInfo=new AlertDialog.Builder(this);
        //adInfo.setTitle("简单对话框");

        adInfo.setMessage("本次行程结束，获取周边地产...");
        mDialog = adInfo.create();
        mDialog.show();
    }

    private Handler mainHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    txCurMileage.setText(new Integer(mileage).toString());
                    break;
            }
        }
    };
}
