package com.example.schen162.vmonopoly;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by SCHEN162 on 5/9/2018.
 */

public class MonoUser {

    private String mLogin;
    private String mPwd;
    private String mNick;
    private int mCoin;

    public MonoUser(){
        mLogin="游客";
        mPwd="";
        mNick="";
        mCoin=100;
    }

    public MonoUser(String uid){
        String respObj = UserManage.getInstance().getUserByID(uid);
        try {
            JSONObject usrObj = new JSONObject(respObj);
            mLogin=usrObj.getString("login");
            mCoin=usrObj.getInt("coin");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getLogin(){
        return mLogin;
    }
}
