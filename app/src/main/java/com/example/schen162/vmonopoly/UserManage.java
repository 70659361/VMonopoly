package com.example.schen162.vmonopoly;

/**
 * Created by SCHEN162 on 4/28/2018.
 */

public class UserManage {
    private static UserManage _inst;
    private String username;

    protected UserManage (){
        username="";
    }

    public static UserManage getInstance(){
        if(_inst==null){
            _inst=new UserManage();
        }
        return _inst;
    }

    public void setUser(String user){
        username=user;
    }
    public String getUser(){
        return username;
    }

}
