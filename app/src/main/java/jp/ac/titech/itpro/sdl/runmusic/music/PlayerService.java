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

package jp.ac.titech.itpro.sdl.runmusic.music;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.provider.MediaStore;
import android.util.Log;

import java.io.IOException;

public class PlayerService extends Service {

    private static final String TAG = PlayerService.class.getSimpleName();
    private static int DURATION = 335;

    // Binder given to clients
    private final IBinder mBinder = new LocalBinder();
    private Worker mWorker;

    private MediaPlayer mediaPlayer;

    public PlayerService() {
    }

    public void setDuration(int duration){
        DURATION = duration;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        if(mediaPlayer != null) mediaPlayer.stop();
        if (mWorker != null) {
            mWorker.interrupt();
        }
        return super.onUnbind(intent);
    }

    public void play(String mid) {
        Log.d(TAG, "play: "+mid);
        if (mWorker == null) {
            mediaPlayer = new MediaPlayer();
            try {
                if(mid != null){
                    mediaPlayer.setDataSource(getApplicationContext(), Uri.withAppendedPath(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,mid));
                    mediaPlayer.prepare();
                }
                mediaPlayer.start();
            }catch (IOException e){
                e.printStackTrace();
            }
            if(mediaPlayer.isPlaying()) {
                mWorker = new Worker();
                mWorker.start();
            }
        } else {
            mediaPlayer.start();
            mWorker.doResume();
        }
    }

    public void play_new(String mid){
        if (mWorker != null) {
            mWorker.interrupt();
        }
        mWorker = null;
        play(mid);
    }

    public boolean isPlaying() {
        return mWorker != null && mWorker.isPlaying() && mediaPlayer.isPlaying();
    }

    public void pause() {
        if (mWorker != null) {
            mediaPlayer.pause();
            mWorker.doPause();
        }
    }

    public int getPosition() {
        if (mWorker != null) {
            return mWorker.getPosition();
        }
        return 0;
    }

    public int getDuration() {
        return DURATION;
    }

    private static class Worker extends Thread {

        boolean paused = false;
        int position = 0;

        @Override
        public void run() {
            try {
                while (position < DURATION) {
                    sleep(1000);
                    if (!paused) {
                        position++;
                    }
                }
            } catch (InterruptedException e) {
                Log.d(TAG, "Player unbounded");
            }
        }

        void doResume() {
            paused = false;
        }

        void doPause() {
            Log.d(TAG, "Player Paused"+paused);
            paused = true;
        }

        boolean isPlaying() {
            return !paused;
        }

        int getPosition() {
            return position;
        }
    }

    /**
     * Class used for the client Binder. Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class LocalBinder extends Binder {

        public PlayerService getService() {
            // Return this instance of PlayerService so clients can call public methods
            return PlayerService.this;
        }
    }
}
