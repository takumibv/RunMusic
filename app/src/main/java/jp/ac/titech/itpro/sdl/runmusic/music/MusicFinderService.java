package jp.ac.titech.itpro.sdl.runmusic.music;

import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Binder;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.annotation.StringDef;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jp.ac.titech.itpro.sdl.runmusic.R;

/**
 * Created by couchpotatobv on 2017/07/11.
 */

public class MusicFinderService {

    public static List<MusicContent.MusicItem> findMusicList(Context c, String feel, int Bpm){

        List<MusicContent.MusicItem> musics = new ArrayList<>();
        ContentResolver resolver = c.getContentResolver();
        Cursor cursor = resolver.query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI ,  //データの種類
                null , //取得する項目 nullは全部
                null , //フィルター条件 nullはフィルタリング無し
                null , //フィルター用のパラメータ
                null   //並べ替え
        );
        Log.d( "TEST" , Arrays.toString( cursor.getColumnNames() ) );  //項目名の一覧を出力
        Log.d( "TEST" , cursor.getCount() + "" ); //取得できた件数を表示
        Log.d( "TEST" , MediaStore.Audio.Media.EXTERNAL_CONTENT_URI + "" );

        while( cursor.moveToNext() ){
            String album = cursor.getString( cursor.getColumnIndex( MediaStore.Audio.Media.ALBUM ) );
            String artist = cursor.getString( cursor.getColumnIndex( MediaStore.Audio.Media.ARTIST ) );
            String title = cursor.getString( cursor.getColumnIndex( MediaStore.Audio.Media.TITLE ) );
            String album_id = cursor.getString( cursor.getColumnIndex( MediaStore.Audio.Media.ALBUM_ID ) );
            int duartion = Integer.parseInt(cursor.getString( cursor.getColumnIndex( MediaStore.Audio.Media.DURATION ) ));
            Log.d("TEST" , "====================================");
            Log.d("TEST" , album ); //アルバム名の取得
            Log.d("TEST" , artist ); //アーティスト名の取得
            Log.d("TEST" , title ); //タイトルの取得
            Cursor albumCursor = c.getContentResolver().query(
                    MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                    null,
                    MediaStore.Audio.Albums._ID + "=?",
                    new String[]{ album_id },
                    null);
            String album_art = R.drawable.album_cover_two_door+"";
            if (albumCursor.moveToFirst()){
                album_art = albumCursor.getString( albumCursor.getColumnIndexOrThrow( MediaStore.Audio.Albums.ALBUM_ART ) );
                Log.d("TEST" ,  album_art);
            }
            musics.add(new MusicContent.MusicItem(R.drawable.album_cover_death_cab, title+" / "+artist, album, duartion/1000, album_art));
        }

        return musics;
    }
}
