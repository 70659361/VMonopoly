package com.example.schen162.vmonopoly;

import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

public class DrivingActivity extends AppCompatActivity {

    private TextView txMileage;
    private int mileage;
    private boolean isDrive;
    private Button btnDrive;
    private Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driving);

        txMileage = (TextView) findViewById(R.id.txt_m);
        btnDrive = (Button) findViewById(R.id.btn_drive);
        isDrive = false;
    }

    public boolean onDrivePressed(View view) {
        if (isDrive) {
            timer.cancel();
            btnDrive.setBackgroundColor(Color.GREEN);
            btnDrive.setText("Drive");
            isDrive = false;
        } else {
            btnDrive.setBackgroundColor(Color.RED);
            btnDrive.setText("Stop");
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
