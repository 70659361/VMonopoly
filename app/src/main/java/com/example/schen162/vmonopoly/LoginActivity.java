package com.example.schen162.vmonopoly;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Timer;
import java.util.TimerTask;

public class LoginActivity extends AppCompatActivity {

    private EditText txUsername;
    private EditText txPwd;
    private EditText txHost;
    private Button btnLogin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        txUsername = (EditText) findViewById(R.id.txt_username);
        txPwd = (EditText) findViewById(R.id.txt_pwd);
        txHost = (EditText) findViewById(R.id.txt_host);
        btnLogin = (Button) findViewById(R.id.btn_login);

        txUsername.setHint("输入用户名");
        txPwd.setHint("输入密码");
        txHost.setText(AppConfig.HTTP_HOST);

        try {
            FileInputStream uf = openFileInput(AppConfig.USER_FILE);
            byte[] buffer = new byte[100];
            for(int i=0;i<buffer.length;i++){buffer[i]=0;}
            uf.read(buffer);
            if(0 != buffer[0]) {
                String exUser = new String(buffer).trim();
                txUsername.setText(exUser);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStart(){
        super.onStart();
    }

    public boolean onLoginPressed(View view){
        String us =  txUsername.getText().toString().trim();
        if(login(us)) {
            Toast.makeText(this.getApplicationContext(), "登陆成功", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this,MainActivity.class);
            startActivity(intent);
        }else{
            Toast.makeText(this.getApplicationContext(), "登陆失败", Toast.LENGTH_SHORT).show();
        }
        return true;
    }

    private boolean login(String us){
        AlertDialog.Builder alertDialogBuilder=new AlertDialog.Builder(this);
        AlertDialog alertDialog = alertDialogBuilder.create();
        boolean ret=UserManage.getInstance ().login(us);
        if(ret){
            try {
                FileOutputStream fos = this.openFileOutput(AppConfig.HTTP_HOST, MODE_PRIVATE);
                fos.write(us.getBytes());
                fos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else {
            alertDialog.setMessage("此用户不存在");
        }
        return ret;
    }
}
