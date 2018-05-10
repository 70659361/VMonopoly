package com.example.schen162.vmonopoly;

import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by SCHEN162 on 5/9/2018.
 */

public class MonoPoiItem {
    private int mPrice;
    private String mDesc;
    private PoiItem mPoiItem;
    private MonoUser mOwner;

    public MonoPoiItem(PoiItem poi) {
        mPoiItem=poi;
        try {
            String respObj = PoiManage.getInstance().getPOI(poi.getPoiId());
            JSONObject poiObj = new JSONObject(respObj);
            mPrice=poiObj.getInt("poiprice");
            mDesc=poiObj.getString("poidesc");
            mOwner=new MonoUser(poiObj.getString("poiowner"));

        } catch (JSONException e) {
            e.printStackTrace();
            mPrice=100;
            mDesc="";
        } catch (IOException e) {
            mPrice=100;
            mDesc="";
            e.printStackTrace();
        }
    }

    public int getPrice(){
        return mPrice;
    }
    public String getDesc(){
        return mDesc;
    }
    public PoiItem getPoi(){
        return mPoiItem;
    }
    public MonoUser getOwner(){
        return mOwner;
    }

    public void setPrice(int price){
        mPrice=price;
    }
    public void setDesc(String desc){
        mDesc=desc;
    }
}
