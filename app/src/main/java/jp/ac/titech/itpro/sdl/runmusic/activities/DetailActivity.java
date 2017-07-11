/*
 * Copyright (c) 2016. André Mion
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package jp.ac.titech.itpro.sdl.runmusic.activities;

import com.andremion.music.MusicCoverView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.transition.Transition;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;

import jp.ac.titech.itpro.sdl.runmusic.R;
import jp.ac.titech.itpro.sdl.runmusic.view.TransitionAdapter;

public class DetailActivity extends PlayerActivity {

    private MusicCoverView mCoverView;
    private TextView mCoverPathView;
    private LinearLayout mTitle;

    private Intent response_data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_detail);

        mCoverView = (MusicCoverView) findViewById(R.id.cover);
        mCoverPathView = (TextView) findViewById(R.id.album_cover_path);
        mTitle = (LinearLayout) findViewById(R.id.title);

        Intent i = getIntent();
        String title = i.getStringExtra("title");
        String artist = i.getStringExtra("artist");
        String cover_path = i.getStringExtra("cover_path");

        if(title != null) ((TextView)mTitle.getChildAt(0)).setText(title);
        if(artist != null) ((TextView)mTitle.getChildAt(2)).setText(artist);
        if(i.getStringExtra("duration") != null){
            int duration = Integer.parseInt(i.getStringExtra("duration"));
            setDuration(duration);
        }
        if(cover_path != "" && cover_path != null){
            File file = new File(cover_path);
            if(file.exists()){
                Bitmap bm = BitmapFactory.decodeFile(file.getPath());
                mCoverView.setImageBitmap(bm);
            }
        }

        response_data = new Intent();
        response_data.putExtra("title", title);
        response_data.putExtra("artist", artist);
        response_data.putExtra("cover_path", cover_path);
        setResult(RESULT_OK, response_data);

        mCoverView.setCallbacks(new MusicCoverView.Callbacks() {
            @Override
            public void onMorphEnd(MusicCoverView coverView) {
                // Nothing to do
                Log.d("coverView", coverView+"") ;
            }

            @Override
            public void onRotateEnd(MusicCoverView coverView) {
                supportFinishAfterTransition();
            }
        });

        getWindow().getSharedElementEnterTransition().addListener(new TransitionAdapter() {
            @Override
            public void onTransitionEnd(Transition transition) {
                play();
                mCoverView.start();
            }
        });
    }

    @Override
    public void onBackPressed() {
        onFabClick(null);
    }

    public void onFabClick(View view) {
        pause();
        response_data.putExtra("position", getPosition());
        response_data.putExtra("duration", getDuration());
        mCoverView.stop();
    }

}
