package jp.ac.titech.itpro.sdl.runmusic;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

/**
 * Created by couchpotatobv on 2017/07/09.
 */

public class RunSensor implements SensorEventListener {
    private final static String TAG = "RunSensor";

    private static RunSensor rSensor = new RunSensor();

    private SensorManager sensorMgr;
    private Sensor runCounter;
    private Context context;

    private RunSensor(){}

    public static RunSensor getInstance(){
        return rSensor;
    }

    public void onCreate(Context context){
        this.context = context;
        sensorMgr = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        this.onResume();
    }

    public void onResume(){
        runCounter = sensorMgr.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
//        runCounter = sensorMgr.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
//        if (accelerometer == null) {
//            Toast.makeText(this, "no accelmeter",
//                    Toast.LENGTH_SHORT).show();
//            finish();
//            return;
//        }
        if (runCounter == null) {
            return;
        }
        this.setRunCounterListener();
    }

    public void onPause(){
        if(sensorMgr == null){
            return;
        }
        this.removeRunCounterListener();
    }

    public void setRunCounterListener(){
        if(runCounter != null) sensorMgr.registerListener(this, runCounter, SensorManager.SENSOR_DELAY_FASTEST);
    }

    public void removeRunCounterListener(){
        sensorMgr.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float vx = event.values[0];
        float vy = event.values[1];
        float vz = event.values[2];
        float para = Math.abs(vx*vy*vz);
        Log.d(TAG, "onSensorChanged:"+(int)para);
//        TextView tv = (TextView) context.findViewById(R.id.vx_content);
//        Log.d(TAG, tv.toString());
//        tv.setText(vx);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}