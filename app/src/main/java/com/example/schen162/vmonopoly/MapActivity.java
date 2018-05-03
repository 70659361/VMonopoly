package com.example.schen162.vmonopoly;

import android.content.Intent;
import android.graphics.Color;
import android.provider.SyncStateContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.model.Circle;
import com.amap.api.maps2d.model.CircleOptions;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.MyLocationStyle;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.amap.api.services.poisearch.PoiSearch.OnPoiSearchListener;
import com.amap.api.services.routepoisearch.RoutePOISearchQuery;

import static android.R.attr.mode;

public class MapActivity extends AppCompatActivity implements OnPoiSearchListener, AMapLocationListener {

    private com.amap.api.maps2d.MapView mapView;
    private com.amap.api.maps2d.AMap aMap;
    private PoiSearch poiSearch;// POI搜索
    private PoiSearch.Query query;// Poi查询条件类
    private TextView txCur;
    private TextView txCurCoins;
    private MyLocationStyle myLocationStyle;
    private AMapLocationClient mlocationClient;
    private AMapLocationClientOption mLocationOption;
    private AMapLocation mCurLocation;
    private static final int STROKE_COLOR = Color.argb(180, 3, 145, 255);
    private static final int FILL_COLOR = Color.argb(10, 0, 0, 180);
    private final int SEARCH_RADIUS = 300;
    private Circle circle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        mapView = (com.amap.api.maps2d.MapView) findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);// 此方法必须重写

        txCurCoins = (TextView) findViewById(R.id.txt_CurCoins);

        init();
    }

    protected void init() {
        if (aMap == null) {
            aMap = mapView.getMap();
        }
        txCur= (TextView) findViewById(R.id.txt_curLocation);
        txCurCoins.setText("当前福币：" + new Integer(UserManage.getInstance().getCoins()).toString());

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
        Intent intent = new Intent(this,POIListActivity.class);
        POIListActivity.pois = result.getPois();
        startActivity(intent);
    }

    public boolean onSearchPressed(View view) {
        String keyWord="美食";
        query = new PoiSearch.Query(keyWord, "", "上海");
        query.setPageSize(9);// 设置每页最多返回多少条poiitem
        query.setPageNum(1);// 设置查第一页
        query.setCityLimit(true);

        poiSearch = new PoiSearch(this, query);
        poiSearch.setOnPoiSearchListener(this);

        if(null != mCurLocation) {
            poiSearch.setBound(new PoiSearch.SearchBound(new LatLonPoint(mCurLocation.getLatitude(),
                    mCurLocation.getLongitude()), SEARCH_RADIUS));
        }
        poiSearch.searchPOIAsyn();

        return true;
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if(null != circle){ circle.remove();}
        mCurLocation=aMapLocation;

        updateLocation();
    }

    private void updateLocation() {
        if(null != mCurLocation){
            txCur.setText("当前位置："+mCurLocation.getPoiName());

            LatLng latlong = new LatLng(mCurLocation.getLatitude(), mCurLocation.getLongitude());
            circle = aMap.addCircle(new CircleOptions().center(latlong).radius(SEARCH_RADIUS).strokeColor(Color.GREEN)
                    .fillColor(Color.GREEN).strokeWidth(25));
        }else {
            txCur.setHint("获取当前位置...");
        }
    }
}
