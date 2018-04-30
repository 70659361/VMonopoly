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
    private String HttpResponse;
    ByteArrayOutputStream infoStream = new ByteArrayOutputStream();

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
        infoStream.reset();
        UserName="";

        Thread loginThread = new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection=null;
                try {
                    URL url = new URL("http://78ab8146.ngrok.io/login/" + username);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setConnectTimeout(2000);
                    connection.setRequestMethod("GET");
                    InputStream in = connection.getInputStream();
                    byte[] data = new byte[1000];
                    int readSize = 0;

                    while( ( readSize = in.read(data)) != -1 ){
                        infoStream.write(data,0,readSize);
                    }
                    HttpResponse = infoStream.toString();
                    JSONObject usr = new JSONObject(HttpResponse);
                    if(1 == usr.getInt("isUser")){
                        UserName=username;
                    }else{
                        UserName="";
                    }
                }catch (Exception e){
                    Log.d("", e.toString());
                }
            }
        });

        loginThread.start();
        try{loginThread.join();}catch (Exception e){};

        if(UserName == ""){
            return false;
        }else{
            return true;
        }
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

    public void setUser(String user){
        UserName=user;
    }
    public String getUser(){
        return UserName;
    }

}
