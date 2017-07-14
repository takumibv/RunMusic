package jp.ac.titech.itpro.sdl.runmusic.music;

import android.app.Activity;
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
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import jp.ac.titech.itpro.sdl.runmusic.R;

/**
 * Created by couchpotatobv on 2017/07/11.
 */

public class MusicFinderService {
    private final static String TAG = "MusicFinderService";

    private static InputStream inputStream = null;
    private static BufferedReader bufferedReader = null;
    private static String text = "";
    private static Context context;

    private static ArrayList<HashMap<String, String>> music_list = new ArrayList<>();

    public static List<MusicContent.MusicItem> findMusicList(Context c, String feel, int bpm){
        context = c;
        int feel_type = 0;
        switch (feel){
            case "熱い":
                feel_type = 1;
                break;
            case "爽やか":
                feel_type= 2;
                break;
            default:
                break;
        }
        if(feel_type == 0 || bpm == 0) return null;

        inputFile(c);
        ArrayList<HashMap<String, String>> list = trimMusicList(feel_type, bpm);

        List<MusicContent.MusicItem> musics = new ArrayList<>();
        for(int i=0; i<list.size(); i++){
            HashMap<String, String> m = list.get(i);
            String selection = MediaStore.Audio.Media.ARTIST + "=?";
            String[] selection_vals = new String[]{ m.get("artist") };

            Log.d(TAG, selection);
            Log.d(TAG, m.get("title")+"");

            ContentResolver resolver = c.getContentResolver();
            Cursor cursor = resolver.query(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI ,  //データの種類
                    null , //取得する項目 nullは全部
                    selection , //フィルター条件 nullはフィルタリング無し
                    selection_vals , //フィルター用のパラメータ
                    null   //並べ替え
            );
            cursor.moveToNext();

            for (String columnName : cursor.getColumnNames()) {
                Log.d(TAG, columnName);
                Log.d(TAG, cursor.getColumnIndex(columnName)+"");
            }

            Log.d(TAG, "count:: "+cursor.getCount());
            if(cursor.getCount() == 0) continue;
            Log.d(TAG, cursor.getString(cursor.getColumnIndex( MediaStore.Audio.Media.TITLE ))+"");

            String id = cursor.getString( cursor.getColumnIndex( MediaStore.Audio.Media._ID ) );
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
                Log.d("TEST" ,  album_art+"");
            }
            musics.add(new MusicContent.MusicItem(id, R.drawable.album_cover_default, title+" / "+artist, album, duartion/1000, album_art));

        }

//        List<MusicContent.MusicItem> musics = new ArrayList<>();
//        ContentResolver resolver = c.getContentResolver();
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
//            String id = cursor.getString( cursor.getColumnIndex( MediaStore.Audio.Media._ID ) );
//            String album = cursor.getString( cursor.getColumnIndex( MediaStore.Audio.Media.ALBUM ) );
//            String artist = cursor.getString( cursor.getColumnIndex( MediaStore.Audio.Media.ARTIST ) );
//            String title = cursor.getString( cursor.getColumnIndex( MediaStore.Audio.Media.TITLE ) );
//            String album_id = cursor.getString( cursor.getColumnIndex( MediaStore.Audio.Media.ALBUM_ID ) );
//            int duartion = Integer.parseInt(cursor.getString( cursor.getColumnIndex( MediaStore.Audio.Media.DURATION ) ));
//            Log.d("TEST" , "====================================");
//            Log.d("TEST" , album ); //アルバム名の取得
//            Log.d("TEST" , artist ); //アーティスト名の取得
//            Log.d("TEST" , title ); //タイトルの取得
//            Cursor albumCursor = c.getContentResolver().query(
//                    MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
//                    null,
//                    MediaStore.Audio.Albums._ID + "=?",
//                    new String[]{ album_id },
//                    null);
//            String album_art = R.drawable.album_cover_two_door+"";
//            if (albumCursor.moveToFirst()){
//                album_art = albumCursor.getString( albumCursor.getColumnIndexOrThrow( MediaStore.Audio.Albums.ALBUM_ART ) );
//                Log.d("TEST" ,  album_art+"");
//            }
//            musics.add(new MusicContent.MusicItem(id, R.drawable.album_cover_default, title+" / "+artist, album, duartion/1000, album_art));
//        }

        return musics;
    }

    public static void inputFile(Context c){
        context = c;
        try {
            try {
                // assetsフォルダ内の sample.txt をオープンする
                inputStream = context.getResources().getAssets().open("music_list.txt");
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

                String str;
                while ((str = bufferedReader.readLine()) != null) {
                    music_list.add(getMusicFromText(str));
                }
            } finally {
                if (inputStream != null) inputStream.close();
                if (bufferedReader != null) bufferedReader.close();
            }
        } catch (Exception e){
            // エラー発生時の処理
            e.printStackTrace();
        }
    }

    public static HashMap<String, String> getMusicFromText(String str){
        char[] charArray = str.toCharArray();
        HashMap<String, String> music = new HashMap<String, String>();
        int i=0;
        str = "";
        for (char ch : charArray) {
            if(i==0 && ch == '\t'){
                music.put("feel", str);
                str = "";
                i++;
            }else if(i==1 && ch == '\t'){
                music.put("bpm", str);
                str = "";
                i++;
            }else if(i==2 && ch == '\t'){
                music.put("title", str);
                str = "";
                i++;
            }else{
                str += ch;
            }
        }
        music.put("artist", str);
//        Log.d(TAG, music+"");
        return music;
    }

    public static ArrayList<HashMap<String, String>> trimMusicList(int feel, int bpm){
        if(bpm < 160) bpm=160;
        if(bpm >= 190) bpm=180;
        ArrayList<HashMap<String, String>> list = new ArrayList<>();
        for(int i=0; i<music_list.size(); i++){
            HashMap<String, String> m = music_list.get(i);
            if(Integer.parseInt(m.get("feel"))==feel && Integer.parseInt(m.get("bpm"))/10==(bpm+5)/10){
                list.add(m);
                Log.d(TAG, "::"+m);
            }
        }
        Collections.shuffle(list);
        return list;
    }
}
