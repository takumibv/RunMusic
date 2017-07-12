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

package jp.ac.titech.itpro.sdl.runmusic.view;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import jp.ac.titech.itpro.sdl.runmusic.R;
import jp.ac.titech.itpro.sdl.runmusic.activities.RunningActivity;
import jp.ac.titech.itpro.sdl.runmusic.music.MusicContent.MusicItem;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private final List<MusicItem> mValues;

    public RecyclerViewAdapter(List<MusicItem> items) {
        mValues = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.content_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mCoverView.setImageResource(holder.mItem.getCover());
        holder.mTitleView.setText(holder.mItem.getTitle());
        holder.mArtistView.setText(holder.mItem.getArtist());
        holder.mDurationView.setText(DateUtils.formatElapsedTime(holder.mItem.getDuration()));

        String album_art_path = holder.mItem.getmAlbumArtPath();
        holder.mCoverPathView.setText(holder.mItem.getmAlbumArtPath());
        if(album_art_path != ""){
            File file = new File(album_art_path);
            if(file.exists()){
                Bitmap bm = BitmapFactory.decodeFile(file.getPath());
                holder.mCoverView.setImageBitmap(bm);
            }
        }

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, String> options = new HashMap<String, String>();
                options.put("id", holder.mItem.getmId());
                options.put("cover_path", holder.mItem.getmAlbumArtPath());
                options.put("title", holder.mItem.getTitle());
                options.put("artist", holder.mItem.getArtist());
                options.put("duration", holder.mItem.getDuration()+"");
                ((RunningActivity)v.getContext()).onClickListItem(v, options);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final ImageView mCoverView;
        public final TextView mCoverPathView;
        public final TextView mTitleView;
        public final TextView mArtistView;
        public final TextView mDurationView;
        public MusicItem mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mCoverView = (ImageView) view.findViewById(R.id.cover);
            mTitleView = (TextView) view.findViewById(R.id.title);
            mArtistView = (TextView) view.findViewById(R.id.artist);
            mDurationView = (TextView) view.findViewById(R.id.duration);
            mCoverPathView = (TextView) view.findViewById(R.id.album_cover_path);
        }
    }

}
