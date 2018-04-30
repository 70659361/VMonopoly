package com.example.schen162.vmonopoly;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.Console;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Logger;

/**
 * Created by SCHEN162 on 4/28/2018.
 */

public class UserManage {

    public static final int HTTP_RESPONSE = 0;

    private static UserManage _inst;
    private String UserName;
    private int UserConins;
    private String HttpResponse;
    private JSONObject jsResponse;

    protected UserManage (){
        UserName="";
    }

    public static UserManage getInstance(){
        if(_inst==null){
            _inst=new UserManage();
        }
        return _inst;
    }

    public boolean login(final String username){
        UserName="";
        httpAPICall("/login/" + username);
        try {
            if(null != jsResponse){
                UserName=username;
                UserConins=jsResponse.getInt("coins");
                return true;
            }else{
                return false;
            }
        }catch (Exception e){
            return false;
        }
    }

    public int getUserCoins(){
        try {
            httpAPICall("/coins/" + UserName);
            if (null != jsResponse) {
                UserConins = jsResponse.getInt("coins");
            }
        }catch(Exception e){}

        return UserConins;
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case HTTP_RESPONSE:
                    HttpResponse = msg.obj.toString();
                    break;
                default:
                    break;
            }
        }
    };

    private void httpAPICall(String url){

        Thread loginThread = new Thread(new Runnable() {
            private String APIUrl;
            @Override
            public void run() {
                HttpURLConnection connection=null;
                ByteArrayOutputStream infoStream = new ByteArrayOutputStream();
                try {
                    URL url = new URL("http://7a4b5b14.ngrok.io"+APIUrl);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setConnectTimeout(2000);
                    connection.setRequestMethod("GET");
                    InputStream in = connection.getInputStream();
                    byte[] data = new byte[1000];
                    int readSize = 0;

                    while( ( readSize = in.read(data)) != -1 ){
                        infoStream.write(data,0,readSize);
                    }
                    jsResponse = new JSONObject(infoStream.toString());
                }catch (Exception e){
                    Log.d("", e.toString());
                    jsResponse = null;
                }
            }
            public Runnable setURL(String url){
                APIUrl=url;
                return this;
            }
        }.setURL(url));

        loginThread.start();
        try{loginThread.join();}catch (Exception e){};
    }

    public void setUser(String user){
        UserName=user;
    }
    public String getUser(){
        return UserName;
    }

    public void setCoins(int coins){
        UserConins=coins;
    }
    public int getCoins(){
        return UserConins;
    }


}
