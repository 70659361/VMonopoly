package com.example.schen162.vmonopoly;

import android.content.Context;
import android.widget.Toast;

import com.amap.api.services.core.PoiItem;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;

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
    private HttpURLConnection urlConnection;

    InputStream inputStream = null;


    protected PoiManage (){
        httpResponse="";
    }

    public static PoiManage getInstance(){
        if(_inst==null){
            _inst=new PoiManage();
        }
        return _inst;
    }

    public String getPOI(String pid) throws IOException{

        String url = "/poi/" + pid;
        httpGETAPICall(url);

        return httpResponse;
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

    public boolean buyPOI(String user, String poiid, int price){

        try {
            String api="/buy/"+user+"/"+poiid+"/"+new Integer(price).toString();
            httpPOSTAPICall(api, "{}");
        } catch (IOException e) {
            e.printStackTrace();
        }

        return true;
    }

    public boolean updatePOIDescription(String poiid, String poidesc){
        String api="/desc/"+poiid+"/"+poidesc;
        try {
            httpPOSTAPICall(api, "");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    public String getPOIbyUser(int userid){
        httpResponse="";
        try {
            String api="/pois/"+ new Integer(userid).toString();
            httpGETAPICall(api);

        }catch (IOException e) {
            e.printStackTrace();
        }
        return httpResponse;
    }

    private void httpGETAPICall(String api) throws IOException{
        Thread _httpThread = new PoiManage.httpThread(api);
        _httpThread.start();
        try{
            _httpThread.join();
        }catch (Exception e){};
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
        private String _method;


        public httpThread(String api, String body){
            _api=api;
            _body=body;
            _method="POST";
        }

        public httpThread(String api){
            _api=api;
            _body="";
            _method="GET";
        }

        public void run(){
            try {
                URL url = null;
                try {
                    url = new URL(AppConfig.HTTP_HOST + _api);
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod(_method);

                    if(_method == "GET"){

                    }else{
                        urlConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                        urlConnection.setRequestProperty("Accept", "application/json");
                        urlConnection.setDoOutput(true);

                        DataOutputStream wr = new DataOutputStream(urlConnection.getOutputStream());
                        wr.writeBytes(_body);
                        wr.close();
                    }

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
                    }else{
                        httpResponse="";
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
