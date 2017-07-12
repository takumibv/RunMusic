package jp.ac.titech.itpro.sdl.runmusic.activities;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.ac.titech.itpro.sdl.runmusic.R;
import jp.ac.titech.itpro.sdl.runmusic.music.MusicContent;
import jp.ac.titech.itpro.sdl.runmusic.music.MusicFinderService;
import jp.ac.titech.itpro.sdl.runmusic.view.ProgressView;
import jp.ac.titech.itpro.sdl.runmusic.view.RecyclerViewAdapter;

/**
 * Created by couchpotatobv on 2017/07/10.
 */

public class RunningActivity extends PlayerActivity {
    private static final int ACTIVITY_CODE = 1111;
    private final static String TAG = "RunningActivity";

    private View mCoverView;
    private View mTitleView;
    private View mTimeView;
    private View mDurationView;
    private View mProgressView;
    private View mFabView;

    private String now_id;
    private String now_title;
    private String now_artist;
    private String now_cover_path;
    private int now_duration;
    private int now_position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_list);

        mCoverView = findViewById(R.id.cover);
        mTitleView = findViewById(R.id.title);
        mTimeView = findViewById(R.id.time);
        mDurationView = findViewById(R.id.duration);
        mProgressView = findViewById(R.id.progress);
        mFabView = findViewById(R.id.fab);

        // Set the recycler adapter
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.tracks);
        assert recyclerView != null;
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

//        List<MusicContent.MusicItem> musics = new ArrayList<>();
//        ContentResolver resolver = getContentResolver();
//        Cursor cursor = resolver.query(
//                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI ,  //データの種類
//                null , //取得する項目 nullは全部
//                null , //フィルター条件 nullはフィルタリング無し
//                null , //フィルター用のパラメータ
//                null   //並べ替え
//        );
//        Log.d( "TEST" , Arrays.toString( cursor.getColumnNames() ) );  //項目名の一覧を出力
//        Log.d( "TEST" , cursor.getCount() + "" ); //取得できた件数を表示
//        Log.d( "TEST" , MediaStore.Audio.Media.EXTERNAL_CONTENT_URI + "" );
//
//        while( cursor.moveToNext() ){
//            String album = cursor.getString( cursor.getColumnIndex( MediaStore.Audio.Media.ALBUM ) );
//            String artist = cursor.getString( cursor.getColumnIndex( MediaStore.Audio.Media.ARTIST ) );
//            String title = cursor.getString( cursor.getColumnIndex( MediaStore.Audio.Media.TITLE ) );
//            String album_id = cursor.getString( cursor.getColumnIndex( MediaStore.Audio.Media.ALBUM_ID ) );
//            int duartion = Integer.parseInt(cursor.getString( cursor.getColumnIndex( MediaStore.Audio.Media.DURATION ) ));
//            Log.d("TEST" , "====================================");
//            Log.d("TEST" , album ); //アルバム名の取得
//            Log.d("TEST" , artist ); //アーティスト名の取得
//            Log.d("TEST" , title ); //タイトルの取得
//            Cursor albumCursor = getContentResolver().query(
//                    MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
//                    null,
//                    MediaStore.Audio.Albums._ID + "=?",
//                    new String[]{ album_id },
//                    null);
//            String album_art = R.drawable.album_cover_two_door+"";
//            if (albumCursor.moveToFirst()){
//                album_art = albumCursor.getString( albumCursor.getColumnIndexOrThrow( MediaStore.Audio.Albums.ALBUM_ART ) );
//                Log.d("TEST" ,  album_art); //タイトルの取得
//            }
//            musics.add(new MusicContent.MusicItem(R.drawable.album_cover_death_cab, title+" / "+artist, album, duartion/1000, album_art));
//        }

        List<MusicContent.MusicItem> musics = MusicFinderService.findMusicList(this, "good", 120);

        RecyclerViewAdapter music_list = new RecyclerViewAdapter(musics);
        recyclerView.setAdapter(music_list);
        TextView mCounter = (TextView) findViewById(R.id.counter);
        mCounter.setText(musics.size() + " songs");

        MusicContent.MusicItem m = musics.get(0);
        now_id = m.getmId();
        now_title = m.getTitle();
        now_artist = m.getArtist();
        now_cover_path = m.getmAlbumArtPath();
        now_duration = (int)m.getDuration();
        now_position = 0;
        setNowView();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case ACTIVITY_CODE:
                if (resultCode == RESULT_OK) {
                    now_id = data.getStringExtra("id");
                    now_title = data.getStringExtra("title");
                    now_artist = data.getStringExtra("artist");
                    now_cover_path = data.getStringExtra("cover_path");
                    now_duration = Integer.parseInt(data.getStringExtra("duration"));
                    now_position = Integer.parseInt(data.getStringExtra("position"));
                    setNowView();
                }
        }
    }

    public void setNowView(){
        ((TextView)((LinearLayout)mTitleView).getChildAt(0)).setText(now_title);
        ((TextView)((LinearLayout)mTitleView).getChildAt(2)).setText(now_artist);
        if(now_cover_path != "" && now_cover_path != null){
            File file = new File(now_cover_path);
            if(file.exists()){
                Bitmap bm = BitmapFactory.decodeFile(file.getPath());
                ((ImageView)mCoverView).setImageBitmap(bm);
            }else{
                ((ImageView)mCoverView).setImageResource(R.drawable.album_cover_default);
            }
        } else {
            ((ImageView)mCoverView).setImageResource(R.drawable.album_cover_default);
        }
        ((TextView)mTimeView).setText(DateUtils.formatElapsedTime(now_position));
        ((TextView)mDurationView).setText(DateUtils.formatElapsedTime(now_duration));
        ((TextView)findViewById(R.id.duration_value)).setText(now_duration+"");
        ((ProgressView)mProgressView).setProgress(now_position);
    }

    public void onFabClick(View view) {
        Log.d(TAG, "onFabClick");
        //noinspection unchecked
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(this,
                new Pair<>(mCoverView, ViewCompat.getTransitionName(mCoverView)),
                new Pair<>(mTitleView, ViewCompat.getTransitionName(mTitleView)),
                new Pair<>(mTimeView, ViewCompat.getTransitionName(mTimeView)),
                new Pair<>(mDurationView, ViewCompat.getTransitionName(mDurationView)),
                new Pair<>(mProgressView, ViewCompat.getTransitionName(mProgressView)),
                new Pair<>(mFabView, ViewCompat.getTransitionName(mFabView)));
        Intent d_activity = new Intent(this, DetailActivity.class);
        d_activity.putExtra("title", now_title);
        d_activity.putExtra("artist", now_artist);
        d_activity.putExtra("id", now_id);
        d_activity.putExtra("cover_path", now_cover_path);
        d_activity.putExtra("duration", now_duration+"");
        ActivityCompat.startActivity(this, d_activity, options.toBundle());
    }

    public void onClickListItem(View v, HashMap<String, String> ops){
//        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(this,
//                new Pair<>(v.findViewById(R.id.cover), v.findViewById(R.id.cover).getTransitionName()),
//                new Pair<>(v.findViewById(R.id.title), ViewCompat.getTransitionName(mTitleView)),
//                new Pair<>(mTimeView, ViewCompat.getTransitionName(mTimeView)),
//                new Pair<>(v.findViewById(R.id.duration), ViewCompat.getTransitionName(mDurationView)),
//                new Pair<>(mProgressView, ViewCompat.getTransitionName(mProgressView)),
//                new Pair<>(mFabView, ViewCompat.getTransitionName(mFabView)));
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(this,
                new Pair<>(mCoverView, ViewCompat.getTransitionName(mCoverView)),
                new Pair<>(mTitleView, ViewCompat.getTransitionName(mTitleView)),
                new Pair<>(mTimeView, ViewCompat.getTransitionName(mTimeView)),
                new Pair<>(mDurationView, ViewCompat.getTransitionName(mDurationView)),
                new Pair<>(mProgressView, ViewCompat.getTransitionName(mProgressView)),
                new Pair<>(mFabView, ViewCompat.getTransitionName(mFabView)));
        Intent d_activity = new Intent(this, DetailActivity.class);
        d_activity.putExtra("play_new", "true");
        for (Map.Entry<String, String> entry : ops.entrySet()) {
            d_activity.putExtra(entry.getKey(), entry.getValue());
        }
        ActivityCompat.startActivityForResult(this, d_activity, ACTIVITY_CODE,options.toBundle());
    }

}
