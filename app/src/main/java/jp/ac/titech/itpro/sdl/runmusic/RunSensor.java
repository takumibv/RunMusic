package jp.ac.titech.itpro.sdl.runmusic;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;

import jp.ac.titech.itpro.sdl.runmusic.activities.MainActivity;
import jp.ac.titech.itpro.sdl.runmusic.view.GraphView;

/**
 * Created by couchpotatobv on 2017/07/09.
 */

public class RunSensor implements SensorEventListener {
    private final static String TAG = "RunSensor";

    private static RunSensor rSensor = new RunSensor();

    private SensorManager sensorMgr;
    private Sensor runCounter;
    private Context context;

    private double vx=0, vy=0, vz=0;
    private ArrayList<Double> data = new ArrayList<Double>();
    private int plus_minus = 0; // -1:負, 1:正
    private int times = 0;
    private Boolean is_valid = false;
    private final static int MAX_TRY_TIME = 3;
    ArrayList<Long> point_time = new ArrayList<Long>();
    private final static int POINT_VALID_SIZE = 5;
    private final static int DATA_SIZE = 200;
    double mean = 0;

    private final static int BPM_DECIDE_COUNT = 3;
    private final static int VALID_BPM_BAND = 10;
    private int prev_bpm = 0;
    private int bpm_d_count = 0;

    private final static long GRAPH_REFRESH_WAIT_MS = 10;
    private GraphRefreshThread th = null;
    private Handler handler;

    private RunSensor(){}

    public static RunSensor getInstance(){
        return rSensor;
    }

    public void onCreate(Context context){
        this.context = context;
        sensorMgr = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        this.onResume();

        handler = new Handler();
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

        th = new GraphRefreshThread();
        th.start();
    }

    public void onPause(){
        if(sensorMgr == null){
            return;
        }
        this.removeRunCounterListener();

        th = null;
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
        int bpm = calcBpm();
//        Log.d(TAG, "onSensorChanged:"+(int)(para*100));
//        TextView tv = (TextView) context.findViewById(R.id.vx_content);
//        Log.d(TAG, tv.toString());
//        tv.setText(vx);
        ((MainActivity)context).onSensorChanged(""+para+" : "+mean+" : "+data.size());
        if(bpm > 0 && bpm_d_count > BPM_DECIDE_COUNT ) ((MainActivity)context).onDecideBPM(bpm);
    }

    public int calcBpm(){
        if(data.size() != DATA_SIZE) return 0;

        int bpm = 0;

        if(data.get(DATA_SIZE-1) - mean > 0){
            if(plus_minus == 1){
                times++;
                if(times > MAX_TRY_TIME) is_valid = true;
            } else if(plus_minus == -1 && is_valid){
                bpm = updateBPM(new Date().getTime());
                plus_minus = 1;
                times = 0;
                is_valid = false;
            } else {
                plus_minus = 1;
                times = 0;
            }
        }else{
            if(plus_minus == -1){
                times++;
                if(times > MAX_TRY_TIME) is_valid = true;
            } else if(plus_minus == 1 && is_valid){
                bpm = updateBPM(new Date().getTime());
                plus_minus = -1;
                times = 0;
                is_valid = false;
            } else {
                plus_minus = -1;
                times = 0;
            }
        }

        return bpm;
    }

    public int updateBPM(Long time){
        point_time.add(time);
        if(point_time.size() > POINT_VALID_SIZE) point_time.remove(0);
        int bpm = 0;
        int mean = 0;
        for(int i=0; i<point_time.size()-1; i++){
            long a = point_time.get(i+1) - point_time.get(i);
            mean += a;
        }
        if(point_time.size() > 1) mean /= (point_time.size()-1);
        if(mean != 0) bpm = 30000 / mean ;
        if(bpm < prev_bpm + VALID_BPM_BAND/2 && bpm > prev_bpm -VALID_BPM_BAND/2 ){
            bpm_d_count++;
        }else{
            bpm_d_count = 0;
        }
        prev_bpm = bpm;

        ((MainActivity)context).onUpdateBPM(bpm);

        return bpm;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private class GraphRefreshThread extends Thread {
        public void run() {
            try {
                while (th != null) {
                    handler.post(new Runnable() {
                        public void run() {
//                            rateView.setText(String.format(Locale.getDefault(), "%f", rate));
//                            accuracyView.setText(String.format(Locale.getDefault(), "%d", accuracy));
                            if(data.size() > 0) ((MainActivity)context).onGraphUpdate(data.get(data.size() - 1));
                        }
                    });
                    Thread.sleep(GRAPH_REFRESH_WAIT_MS);
                }
            }
            catch (InterruptedException e) {
                Log.e(TAG, e.toString());
                th = null;
            }
        }
    }
}