package com.example.schen162.vmonopoly;

import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by SCHEN162 on 5/3/2018.
 */

public class PoiManage {
    private static PoiManage _inst;
    private String httpResponse;

    InputStream inputStream = null;
    HttpURLConnection urlConnection = null;

    protected PoiManage (){
        httpResponse="";
    }

    public static PoiManage getInstance(){
        if(_inst==null){
            _inst=new PoiManage();
        }
        return _inst;
    }

    public String getPOIs(String[] ids) throws IOException {

        String jsObj = "{\"id\":[";
        for(int i=0;i<ids.length-1;i++){
            jsObj+="\""+ids[i]+"\",";
        }
        jsObj+="\""+ids[ids.length-1]+"\"]}";

        httpPOSTAPICall("/pois", jsObj);

        return httpResponse;
    }

    private void httpPOSTAPICall(String api, String body) throws IOException {
        Thread _httpThread = new PoiManage.httpThread(api, body);
        _httpThread.start();
        try{
            _httpThread.join();
        }catch (Exception e){};
    }

    class httpThread extends Thread {
        private String _api;
        private String _body;

        public httpThread(String api, String body){
            _api=api;
            _body=body;
        }
        public void run(){
            try {
                URL url = new URL(AppConfig.HTTP_HOST + _api);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                urlConnection.setRequestProperty("Accept", "application/json");
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoOutput(true);
                DataOutputStream wr = new DataOutputStream(urlConnection.getOutputStream());
                wr.writeBytes(_body);
                wr.close();
                int statusCode = urlConnection.getResponseCode();
                if (statusCode == 200) {
                    ByteArrayOutputStream infoStream = new ByteArrayOutputStream();
                    inputStream = new BufferedInputStream(urlConnection.getInputStream());
                    byte[] data = new byte[1000];
                    int readSize = 0;
                    while( ( readSize = inputStream.read(data)) != -1 ){
                        infoStream.write(data,0,readSize);
                    }
                    httpResponse=infoStream.toString();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
