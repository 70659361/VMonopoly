package com.example.schen162.vmonopoly;

/**
 * Created by SCHEN162 on 5/3/2018.
 */

public class AppConfig {
    public static String HTTP_HOST = "http://7a410bb1.ngrok.io";
    public static final String USER_FILE = "user.ini";

    public static void setHttpHost(String host){
        HTTP_HOST = host;
    }
}
