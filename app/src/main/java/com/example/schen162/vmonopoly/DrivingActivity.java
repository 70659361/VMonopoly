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

public class DrivingActivity extends AppCompatActivity {

    private TextView txMileage;
    private EditText txUsername;
    private TextView txHttp;
    private int mileage;
    private boolean isDrive;
    private Button btnDrive;
    private Button btnLogin;
    private Timer timer;
    private String USER_FILE="user.ini";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        txMileage = (TextView) findViewById(R.id.txt_m);
        txHttp = (TextView) findViewById(R.id.txt_http);
        txUsername = (EditText) findViewById(R.id.txt_username);
        btnDrive = (Button) findViewById(R.id.btn_drive);
        btnLogin = (Button) findViewById(R.id.btn_login);

        btnLogin.setHint("输入用户名");
        btnDrive.setText("开车挖矿");
        isDrive = false;

        try {
            FileInputStream uf = openFileInput(USER_FILE);
            byte[] buffer = new byte[100];
            for(int i=0;i<buffer.length;i++){buffer[i]=0;}
            uf.read(buffer);
            if(0 != buffer[0]) {
                String exUser = new String(buffer).trim();
                login(exUser);
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

    public boolean onDrivePressed(View view) {
        if(UserManage.getInstance().isLogin()) {
            if (isDrive) {
                timer.cancel();
                txMileage.setBackgroundColor(Color.RED);
                btnDrive.setText("开车挖矿");
                UserManage.getInstance().updateCoins(mileage);
            } else {
                txMileage.setBackgroundColor(Color.GREEN);
                btnDrive.setText("停止驾驶");
                timer = new Timer();
                TimerTask task = new MyTimerTask();
                timer.schedule(task, 10000, 10000);
            }
            isDrive = !isDrive;

        }else{
            Toast.makeText(this.getApplicationContext(), "请登录", Toast.LENGTH_SHORT).show();
        }

        return true;
    }

    public boolean onMapPressed(View view) {
        Intent intent = new Intent(this, MapActivity.class);
        startActivity(intent);
        return true;
    }

    public boolean onPOIPressed(View view) {
        Intent intent = new Intent(this, POIListActivity.class);
        startActivity(intent);
        return true;
    }

    public boolean onLoginPressed(View view){
        String us =  txUsername.getText().toString().trim();
        login(us);
        if(UserManage.getInstance().isLogin()) {
            if (isDrive) {
                timer.cancel();
                txMileage.setBackgroundColor(Color.RED);
                btnDrive.setText("开车挖矿");
                UserManage.getInstance().updateCoins(mileage);
            } else {
                txMileage.setBackgroundColor(Color.GREEN);
                btnDrive.setText("停止驾驶");
                timer = new Timer();
                TimerTask task = new MyTimerTask();
                timer.schedule(task, 10000, 10000);
            }
        }else{
            timer.cancel();
            txMileage.setBackgroundColor(Color.RED);
            btnDrive.setText("开车挖矿");
            UserManage.getInstance().updateCoins(mileage);
            isDrive=false;
            txMileage.setText("");
        }
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

    private void login(String us){
        AlertDialog.Builder alertDialogBuilder=new AlertDialog.Builder(this);
        AlertDialog alertDialog = alertDialogBuilder.create();

        if(UserManage.getInstance ().login(us)){
            alertDialog.setMessage("登陆成功, 请开始挖矿！");
            mileage=UserManage.getInstance().getCoins();
            txMileage.setText(Integer.toString(mileage));
            txMileage.setBackgroundColor(Color.RED);
            //btnLogin.hide

            try {
                FileOutputStream fos = this.openFileOutput(USER_FILE, MODE_PRIVATE);
                fos.write(us.getBytes());
                fos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }else {
            alertDialog.setMessage("此用户不存在");
        }
        alertDialog.show();
    }
}
