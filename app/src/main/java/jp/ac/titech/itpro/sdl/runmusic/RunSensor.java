package jp.ac.titech.itpro.sdl.runmusic;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import java.util.ArrayList;

import jp.ac.titech.itpro.sdl.runmusic.activities.MainActivity;

/**
 * Created by couchpotatobv on 2017/07/09.
 */

public class RunSensor implements SensorEventListener {
    private final static String TAG = "RunSensor";

    private static RunSensor rSensor = new RunSensor();

    private SensorManager sensorMgr;
    private Sensor runCounter;
    private Context context;

    double vx=0, vy=0, vz=0;
    ArrayList<Double> data = new ArrayList<Double>();
    private final static int DATA_SIZE = 500;
    double mean = 0;

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
        if(sensorMgr == null){
            return;
        }
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
        double vx_df = vx - event.values[0];
        double vy_df = vy - event.values[1];
        double vz_df = vz - event.values[2];

        vx = event.values[0];
        vy = event.values[1];
        vz = event.values[2];

        double para = Math.sqrt(Math.pow(vx_df, 2)+Math.pow(vy_df, 2)+Math.pow(vz_df, 2));
        data.add(para);
        if(data.size() > DATA_SIZE){
            data.remove(0);
            double sum = 0;
            for(int i = 1; i < data.size(); i++) sum += data.get(i);
            mean = sum / data.size();
        }
        Log.d(TAG, "onSensorChanged:"+(int)(para*100));
//        TextView tv = (TextView) context.findViewById(R.id.vx_content);
//        Log.d(TAG, tv.toString());
//        tv.setText(vx);
        ((MainActivity)context).onSensorChanged(""+para+" : "+mean+" : "+data.size());
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}