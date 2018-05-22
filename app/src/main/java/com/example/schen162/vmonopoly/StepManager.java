package com.example.schen162.vmonopoly;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Handler;
import android.os.Messenger;
import android.widget.Toast;

import static android.content.Context.SENSOR_SERVICE;

/**
 * Created by SCHEN162 on 5/21/2018.
 */

public class StepManager{

    private SensorManager sensorManager;
    private Sensor stepCounter;//步伐总数传感器
    private Sensor stepDetector;//单次步伐传感器
    private SensorEventListener stepCounterListener;//步伐总数传感器事件监听器
    private SensorEventListener stepDetectorListener;//单次步伐传感器事件监听器
    private double totalSteps;
    private double currentSteps;
    private Context contx;

    public StepManager(Context context){
        contx = context;

        sensorManager= (SensorManager) context.getSystemService(SENSOR_SERVICE);//获取传感器系统服务
        stepCounter=sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);//获取计步总数传感器
        stepDetector=sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);//获取单次计步传感器
        totalSteps=0;
        currentSteps=0;

        initListener();
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static boolean isSupportStepCountSensor(Context context) {
        // 获取传感器管理器的实例
        SensorManager sensorManager = (SensorManager) context
                .getSystemService(SENSOR_SERVICE);
        Sensor countSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        Sensor detectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        return countSensor != null || detectorSensor != null;
    }

    protected void initListener() {
        stepCounterListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                totalSteps = event.values[0];
                Toast.makeText(contx, new Double(totalSteps).toString(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };

        stepDetectorListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                currentSteps = event.values[0];
                Toast.makeText(contx, new Double(currentSteps).toString(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
            }
        };
    }

    private void registerSensor(){
        //注册传感器事件监听器
        sensorManager.registerListener(stepDetectorListener,stepDetector,SensorManager.SENSOR_DELAY_FASTEST);
        sensorManager.registerListener(stepCounterListener,stepCounter,SensorManager.SENSOR_DELAY_FASTEST);
    }

    private void unregisterSensor(){
        //解注册传感器事件监听器
        sensorManager.unregisterListener(stepCounterListener);
        sensorManager.unregisterListener(stepDetectorListener);
    }

    public void onPause(){
        unregisterSensor();
    }

    public void onResume(){
        registerSensor();
    }

    public double getTotalSteps(){
        return totalSteps;
    }


}
