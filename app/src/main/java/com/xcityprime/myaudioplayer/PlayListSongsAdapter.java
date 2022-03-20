package com.xcityprime.myaudioplayer;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaMetadataRetriever;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import java.util.ArrayList;

public class PlayListSongsAdapter extends RecyclerView.Adapter<PlayListSongsAdapter.ViewHolder> {
    static ArrayList<MusicFiles> mFiles12 = new ArrayList<>();
    int a =1;
    public ArrayList<MusicFiles>  playListMain23 = new ArrayList<>();
    private Context mContext;
    public static final String SHAREDPref12 = "SharedPref12";
    public static final String SharedValue12 = "LIST12";

    public PlayListSongsAdapter(ArrayList<MusicFiles> list , Context mContext) {
        this.mFiles12 = list;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public PlayListSongsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.select_songs_for_playlist, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final PlayListSongsAdapter.ViewHolder holder, final int position) {
        holder.songName.setText(mFiles12.get(position).getTitle());
        holder.artistName.setText(mFiles12.get(position).getArtist());
        byte[] image = getAlbumArt(mFiles12.get(position).getPath());
        if(image != null){
            Glide.with(mContext)
                    .load(image)
                    .into(holder.imgSong);
        }else{
            Glide.with(mContext)
                    .load(R.drawable.picsart1)
                    .into(holder.imgSong);
        }
        holder.menu_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                String path = mFiles12.get(position).getPath();
                String title = mFiles12.get(position).getTitle();
                String artist = mFiles12.get(position).getArtist();
                String album = mFiles12.get(position).getAlbum();
                String duration = mFiles12.get(position).getDuration();
                String id  = mFiles12.get(position).getId();
                /*
                Intent intent = new Intent(mContext, PickFilesActivity.class);
                intent.putExtra("path", path);
                intent.putExtra("title", title);
                intent.putExtra("artist", artist);
                intent.putExtra("dateAdded", dateAdded);
                intent.putExtra("duration", duration);
                intent.putExtra("id", id);
                mContext.startActivity(intent);*/
                MusicFiles musicFilesPlaList = new MusicFiles(path, title, artist, album, duration, id);
                playListMain23.add(musicFilesPlaList);
                if (playListMain23.contains(musicFilesPlaList)){
                    Log.e("MusicFiles", title);
                    Toast.makeText(mContext,"File added",Toast.LENGTH_SHORT).show();
                    notifyDataSetChanged();
                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext.getApplicationContext());
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.clear();
                    Gson gson = new Gson();
                    String json = gson.toJson(playListMain23);
                    editor.putString("editorSa", json);
                    editor.commit();
                    editor.apply();
                    if (playListMain23.size() == 15){
                        /*SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext.getApplicationContext());
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.clear();
                        Gson gson = new Gson();
                        String json = gson.toJson(playListMain);
                        editor.putString("editorSa", json);
                        editor.commit();
                        editor.apply();*/
                        Intent intent = new Intent(mContext, MainActivity.class);
                        intent.putExtra("cool", json);
                        mContext.startActivity(intent);
                    }
                }else{
                    Toast.makeText(mContext,"Not Added",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mFiles12.size();
    }

    private byte[] getAlbumArt(String uri)
    {
        try {
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(uri);
            byte[] art = retriever.getEmbeddedPicture();
            retriever.release();
            return art;
        }catch (RuntimeException ex){
            ex.printStackTrace();
        }
        return new byte[0];
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imgSong, menu_more;
        private TextView artistName, songName;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgSong = itemView.findViewById(R.id.imgSong);
            artistName = itemView.findViewById(R.id.artistName);
            songName = itemView.findViewById(R.id.songName);
            menu_more = itemView.findViewById(R.id.menu_more);
        }
    }

}
