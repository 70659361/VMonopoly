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

import java.util.Timer;
import java.util.TimerTask;

public class DrivingActivity extends AppCompatActivity {

    private TextView txMileage;
    private EditText txUsername;
    private TextView txHttp;
    private int mileage;
    private boolean isDrive;
    private Button btnDrive;
    private Button btnLogin;
    private Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driving);

        txMileage = (TextView) findViewById(R.id.txt_m);
        txHttp = (TextView) findViewById(R.id.txt_http);
        txUsername = (EditText) findViewById(R.id.txt_username);
        btnDrive = (Button) findViewById(R.id.btn_drive);
        btnLogin = (Button) findViewById(R.id.btn_login);

        btnLogin.setHint("输入用户名");
        btnDrive.setText("开车挖矿");
        isDrive = false;
    }

    public boolean onDrivePressed(View view) {
        if (isDrive) {
            timer.cancel();
            txMileage.setBackgroundColor(Color.RED);
            //btnDrive.setBackgroundColor(Color.RED);
            btnDrive.setText("开车挖矿");
            isDrive = false;
        } else {
            txMileage.setBackgroundColor(Color.GREEN);
            btnDrive.setText("停止驾驶");
            timer=new Timer();
            TimerTask task = new MyTimerTask();
            timer.schedule(task, 1000, 1000);
            isDrive = true;
        }
        return true;
    }

    public boolean onMapPressed(View view) {
        Intent intent = new Intent(this, MonoMainActivity.class);
        startActivity(intent);
        return true;
    }

    public boolean onLoginPressed(View view){
        String us=txUsername.getText().toString();
        AlertDialog.Builder alertDialogBuilder=new AlertDialog.Builder(this);
        AlertDialog alertDialog = alertDialogBuilder.create();

        if(UserManage.getInstance ().login(us)){
            alertDialog.setMessage("登陆成功, 请开始挖矿！");
        }else {
            alertDialog.setMessage("此用户不存在");
        }
        alertDialog.show();
        return true;
    }

    final Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    mileage++;
                    txMileage.setText(Integer.toString(mileage));
                    break;
            }
            super.handleMessage(msg);
        }
    };

    private class MyTimerTask extends TimerTask {
        public void run() {
            Message message = new Message();
            message.what = 1;
            handler.sendMessage(message);
        }
    }
}
