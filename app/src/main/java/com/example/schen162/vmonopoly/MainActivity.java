package com.example.schen162.vmonopoly;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.model.BitmapDescriptor;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.Circle;
import com.amap.api.maps2d.model.CircleOptions;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.LatLngBounds;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.maps2d.model.MyLocationStyle;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.amap.api.services.poisearch.PoiSearch.OnPoiSearchListener;
//import com.example.schen162.vmonopoly.util.AMapUtil;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnPoiSearchListener, AMapLocationListener {


    private PoiSearch poiSearch;// POI搜索
    private PoiSearch.Query query;// Poi查询条件类
    private TextView txCurLoc;
    private TextView txCurUser;
    private TextView txCurCoins;
    private ImageButton btnWalk;

    private com.amap.api.maps2d.MapView mapView;
    private com.amap.api.maps2d.AMap aMap;
    private MyLocationStyle myLocationStyle;
    private AMapLocationClient mlocationClient;
    private AMapLocationClientOption mLocationOption;
    private AMapLocation mCurLocation;
    private AMapLocation mStartLocation;
    private AMapLocation mEndLocation;

    private static final int STROKE_COLOR = Color.argb(10,15,15,15);
    private static final int FILL_COLOR = Color.argb(10,15,15,15);

    private Circle circle;
    private myPoiOverlay poiOverlay;
    private int[] markers = {
            R.drawable.poi_marker_1, R.drawable.poi_marker_2,R.drawable.poi_marker_3,
            R.drawable.poi_marker_4, R.drawable.poi_marker_5,R.drawable.poi_marker_6,
            R.drawable.poi_marker_7, R.drawable.poi_marker_8,R.drawable.poi_marker_9};

    private boolean mIsWalking=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mapView = (com.amap.api.maps2d.MapView) findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);// 此方法必须重写

        txCurLoc = (TextView) findViewById(R.id.txt_curLocation);
        txCurCoins = (TextView) findViewById(R.id.txt_curCoins);
        txCurUser = (TextView) findViewById(R.id.txt_curUser);
        btnWalk = (ImageButton) findViewById(R.id.btn_walk);

        init();
    }

    protected void init() {
        if (aMap == null) {
            aMap = mapView.getMap();
        }

        txCurCoins.setText("当前福币: " + new Integer(UserManage.getInstance().getCoins()).toString());
        txCurUser.setText("欢迎: "+ UserManage.getInstance().getUser());

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
        // TODO Auto-generated method stub
    }

    @Override
    public void onPoiSearched(PoiResult result, int rCode) {

        ArrayList<PoiItem> poiItems = result.getPois();

        /*
        ArrayList<MonoPoiItem> monoPois = new ArrayList<MonoPoiItem>();
        for(int i=0; i<poiItems.size();i++){
            monoPois.add(new MonoPoiItem(poiItems.get(i)));
        }*/

        POIGridActivity.pois = poiItems;
        POIListActivity.mPOIs = poiItems;

        //Intent intent = new Intent(this,POIGridActivity.class);
        //startActivity(intent);

        poiOverlay = new myPoiOverlay(aMap, poiItems);
        poiOverlay.addToMap();
    }

    public boolean onSearchPressed(View view) {
        doKeywordSearchPOI();
        return true;
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

        }
    }

    public void onDicePressed(View view) {
        Intent intent = new Intent(this,POIGridActivity.class);
        startActivity(intent);
    }

    public void onListPressed(View view) {
        Intent intent = new Intent(this,POIListActivity.class);
        startActivity(intent);
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

        /**
         * 去掉PoiOverlay上所有的Marker。
         *
         * @since V2.1.0
         */
        public void removeFromMap() {
            for (Marker mark : mPoiMarks) {
                mark.remove();
            }
        }

        /**
         * 移动镜头到当前的视角。
         * @since V2.1.0
         */
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

        /**
         * 从marker中得到poi在list的位置。
         *
         * @param marker 一个标记的对象。
         * @return 返回该marker对应的poi在list的位置。
         * @since V2.1.0
         */
        public int getPoiIndex(Marker marker) {
            for (int i = 0; i < mPoiMarks.size(); i++) {
                if (mPoiMarks.get(i).equals(marker)) {
                    return i;
                }
            }
            return -1;
        }

        /**
         * 返回第index的poi的信息。
         * @param index 第几个poi。
         * @return poi的信息。poi对象详见搜索服务模块的基础核心包（com.amap.api.services.core）中的类 <strong><a href="../../../../../../Search/com/amap/api/services/core/PoiItem.html" title="com.amap.api.services.core中的类">PoiItem</a></strong>。
         * @since V2.1.0
         */
        public PoiItem getPoiItem(int index) {
            if (index < 0 || index >= mPois.size()) {
                return null;
            }
            return mPois.get(index);
        }

        protected BitmapDescriptor getBitmapDescriptor(int arg0) {
            if (arg0 < 10) {
                BitmapDescriptor icon = BitmapDescriptorFactory.fromBitmap(
                        BitmapFactory.decodeResource(getResources(), markers[arg0]));
                return icon;
            }else {
                BitmapDescriptor icon = BitmapDescriptorFactory.fromBitmap(
                        BitmapFactory.decodeResource(getResources(), R.drawable.onsale));
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
            doKeywordSearchPOI();
            aMap.addMarker(new MarkerOptions()
                    .position(new LatLng(mEndLocation.getLongitude(), mEndLocation.getLatitude()))
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.end)));
        }else {
            btnWalk.setBackgroundColor(Color.GREEN);
            mStartLocation = mCurLocation;
            aMap.addMarker(new MarkerOptions()
                    .position(new LatLng(mStartLocation.getLongitude(), mStartLocation.getLatitude()))
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.start)));
        }
        mIsWalking = !mIsWalking;
    }
}
