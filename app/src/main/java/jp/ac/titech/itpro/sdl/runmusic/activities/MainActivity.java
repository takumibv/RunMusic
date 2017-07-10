package jp.ac.titech.itpro.sdl.runmusic.activities;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

import jp.ac.titech.itpro.sdl.runmusic.R;
import jp.ac.titech.itpro.sdl.runmusic.RunSensor;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private final static String TAG = "MainActivity";
    private final static int MYREQCODE = 1234;
    private final ArrayList<String> feeling_list = new ArrayList<String>();

    private String feel;

    private MediaPlayer mp;
    private String path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate");
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

//        RunSensor.getInstance().onCreate(this);

//        if(feel == null) showInit();

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

    public void onClickStartButton(View v){
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
            mp.start();

        } catch (Exception e){
            e.printStackTrace();
        }
//        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://techbooster.org/"));

    }

    public void onSensorChanged(String txt){
        TextView tv = (TextView) findViewById(R.id.param);
        tv.setText(txt);
    }

    public void onDecideBPM(int bpm){

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
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}