package jp.ac.titech.itpro.sdl.runmusic.activities;

import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

import jp.ac.titech.itpro.sdl.runmusic.R;
import jp.ac.titech.itpro.sdl.runmusic.RunSensor;
import jp.ac.titech.itpro.sdl.runmusic.music.MusicFinderService;
import jp.ac.titech.itpro.sdl.runmusic.view.GraphView;

public class MainActivity extends AppCompatActivity {

    private final static String TAG = "MainActivity";
    private final static int MYREQCODE = 1234;
    private final ArrayList<String> feeling_list = new ArrayList<String>();

    private String feel;

    private MediaPlayer mp;
    private String path;

    private GraphView gView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate");
        setContentView(R.layout.content_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        gView = (GraphView) findViewById(R.id.graph_view);

//        MusicFinderService.findMusicList(this, "熱い", 165);

        showInit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume");
//        RunSensor.getInstance().onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "onPause");
        RunSensor.getInstance().onPause();
    }

    public void showInit(){
        Intent intent = new Intent(this, InitActivity.class);
//        intent.putExtra("request", request);
        startActivityForResult(intent, MYREQCODE);
    }

    public void onClickChangeFeelButton(View v){
        showInit();
    }

    public void onClickStartButton2(View v){
        Log.d(TAG, Environment.getExternalStorageDirectory().getPath());
        path = Environment.getExternalStorageDirectory().getPath();
        File dir = new File(path+"/Music/");
        File file = new File(dir.getAbsolutePath()+"/phatmans/過去現在未来進行形/02 過去現在未来進行形.mp3");

        String m_path = dir.getAbsolutePath()+"/phatmans/過去現在未来進行形/02 過去現在未来進行形.mp3";
        //リソースファイルから再生
        mp = new MediaPlayer();
        try {
            mp.setDataSource(m_path);
            mp.prepare();
            Log.d(TAG, "music: "+mp.getDuration()+", "+mp.getTrackInfo()[0]+", ");
//            mp.start();

        } catch (Exception e){
            e.printStackTrace();
        }

        Intent intent = new Intent(this, RunningActivity.class);
        startActivity(intent);

    }

    public void onClickStartButton3(View v){
        Intent intent = new Intent(this, RunningActivity.class);
        startActivity(intent);
    }

    public void onClickStartButton(View v){
        if(feel == null) {
            alert("今日の気分を入力してください。");
            return;
        }
        RunSensor.getInstance().onCreate(this);

        View startBtn= findViewById(R.id.start_button);
        startBtn.setVisibility(View.GONE);

        View graphView = findViewById(R.id.graph_view);
        graphView.setVisibility(View.VISIBLE);

        TextView bpmView = (TextView) findViewById(R.id.bpm);
        bpmView.setText("BPM: 測定中");
    }

    public void onClickPauseBtn(View v){
        RunSensor.getInstance().onPause();

        View graphView = findViewById(R.id.graph_view);
        graphView.setVisibility(View.GONE);

        View pauseBtn= findViewById(R.id.pause_button);
        pauseBtn.setVisibility(View.VISIBLE);
    }
    public void onClickResumeBtn(View v){
        RunSensor.getInstance().onResume();

        View pauseBtn= findViewById(R.id.pause_button);
        pauseBtn.setVisibility(View.GONE);

        View graphView = findViewById(R.id.graph_view);
        graphView.setVisibility(View.VISIBLE);
    }

    public void onSensorChanged(String txt){
//        TextView tv = (TextView) findViewById(R.id.param);
//        tv.setText(txt);
    }

    public void onUpdateBPM(int bpm){
        TextView tv2 = (TextView) findViewById(R.id.bpm);
        String msg = "BPM: ";
        if(bpm == 0) msg += "測定中";
        else msg += bpm;
        tv2.setText(msg);
    }

    public void onDecideBPM(int bpm){
        Log.d(TAG, "onDecideBPM!!!");
        Log.d(TAG, "BPM: "+bpm);
        Log.d(TAG, "feel: "+feel);

        onClickPauseBtn(null);
        RunSensor.getInstance().onPause();

        Intent intent = new Intent(this, RunningActivity.class);
        intent.putExtra("bpm", bpm);
        intent.putExtra("feel", feel);
        startActivity(intent);
    }

    public void onGraphUpdate(double vx){
//        gView.addData((float)vx, true);
        gView.updateData((float)vx, true);
    }

    @Override
    protected void onActivityResult(int reqCode, int resCode, Intent data) {
        super.onActivityResult(reqCode, resCode, data);
        switch (reqCode) {
            case MYREQCODE:
                if (resCode == RESULT_OK) {
                    feel = data.getStringExtra("feel");
                    TextView text = (Button) findViewById(R.id.change_feel_button);
                    text.setText("今日の気分: " + feel);
                }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void alert(String msg){
        // 確認ダイアログの生成
        AlertDialog.Builder alertDlg = new AlertDialog.Builder(this);
        alertDlg.setMessage(msg);
        alertDlg.setPositiveButton(
            "OK",
            new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // OK ボタンクリック処理
                }
            });
        // 表示
        alertDlg.create().show();
    }

}