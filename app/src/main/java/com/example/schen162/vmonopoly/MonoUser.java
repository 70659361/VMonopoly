package com.example.schen162.vmonopoly;

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

    public String getLogin(){
        return mLogin;
    }
}
