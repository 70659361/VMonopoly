package com.example.schen162.vmonopoly;

import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;

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
        mPrice=100;
        mDesc="快来买我！";


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
