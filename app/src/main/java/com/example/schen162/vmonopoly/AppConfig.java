package com.example.schen162.vmonopoly;

/**
 * Created by SCHEN162 on 5/3/2018.
 */

public class AppConfig {
    public static String HTTP_HOST = "http://d20f0f1d.ngrok.io";
    public static final String USER_FILE = "user.ini";
    public static final int SEARCH_RADIUS = 300;
    public static final String SEARCH_KEYWORDS = "美食,酒店";
    public static final String SEARCH_CITY = "上海";
    public static final int SEARCH_POI_NUM = 6;
    public static final int SEARCH_POI_PAGE = 1;

    public static void setHttpHost(String host){
        HTTP_HOST = host;
    }
}
