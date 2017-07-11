package jp.ac.titech.itpro.sdl.runmusic.activities;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

import jp.ac.titech.itpro.sdl.runmusic.R;
import jp.ac.titech.itpro.sdl.runmusic.RunSensor;
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
        RunSensor.getInstance().onCreate(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume");
        RunSensor.getInstance().onResume();
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

//        ContentResolver resolver = getContentResolver();
//        Cursor cursor = resolver.query(
//            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI ,  //データの種類
//            null , //取得する項目 nullは全部
//            null , //フィルター条件 nullはフィルタリング無し
//            null , //フィルター用のパラメータ
//            null   //並べ替え
//        );
//        Log.d( "TEST" , Arrays.toString( cursor.getColumnNames() ) );  //項目名の一覧を出力
//        Log.d( "TEST" , cursor.getCount() + "" ); //取得できた件数を表示
//        Log.d( "TEST" , MediaStore.Audio.Media.EXTERNAL_CONTENT_URI + "" );
//
//        while( cursor.moveToNext() ){
//            Log.d("TEST" , "====================================");
//            Log.d("TEST" , cursor.getString( cursor.getColumnIndex( MediaStore.Audio.Media.ALBUM ) ) ); //アルバム名の取得
//            Log.d("TEST" , cursor.getString( cursor.getColumnIndex( MediaStore.Audio.Media.ARTIST ) ) ); //アーティスト名の取得
//            Log.d("TEST" , cursor.getString( cursor.getColumnIndex( MediaStore.Audio.Media.TITLE ) ) ); //タイトルの取得
//            Cursor albumCursor = getContentResolver().query(
//                    MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
//                    null,
//                    MediaStore.Audio.Albums._ID + "=?",
//                    new String[]{ cursor.getString( cursor.getColumnIndex( MediaStore.Audio.Media.ALBUM_ID ) ) },
//                    null);
//            if (albumCursor.moveToFirst())
//                Log.d("TEST" , albumCursor.getString( albumCursor.getColumnIndexOrThrow( MediaStore.Audio.Albums.ALBUM_ART ) ) ); //タイトルの取得
//        }

        Intent intent = new Intent(this, RunningActivity.class);
        startActivity(intent);

    }

    public void onClickStartButton(View v){
        Intent intent = new Intent(this, RunningActivity.class);
        startActivity(intent);
    }

    public void onSensorChanged(String txt){
        TextView tv = (TextView) findViewById(R.id.param);
        tv.setText(txt);
    }

    public void onDecideBPM(int bpm){

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
                    TextView text = (TextView) findViewById(R.id.vx_content);
                    text.setText(feel);
                }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}