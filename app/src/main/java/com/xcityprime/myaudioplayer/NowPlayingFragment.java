package com.xcityprime.myaudioplayer;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import static android.content.Context.MODE_PRIVATE;
import static com.xcityprime.myaudioplayer.MainActivity.ARTIST_TO_FRAGMENT;
import static com.xcityprime.myaudioplayer.MainActivity.PATH_TO_FRAGMENT;
import static com.xcityprime.myaudioplayer.MainActivity.SHOW_MINI_PLAYER;
import static com.xcityprime.myaudioplayer.MainActivity.SONG_NAME_TO_FRAGMENT;

public class NowPlayingFragment extends Fragment implements ServiceConnection {


    ImageView albumArt, bottom_prev, bottom_next;
    TextView artistName, songName;
    FloatingActionButton bottom_floatingActionButton;
    View view;
    MusicService musicService;

    public static final String MUSIC_LAST_PLAYED = "LAST_PLAYED";
    public static final String MUSIC_FILE = "STORED_MUSIC";
    public static final String ARTIST_NAME = "ARTIST_NAME";
    public static final String SONG_NAME = "SONG_NAME";
    public NowPlayingFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       view = inflater.inflate(R.layout.fragment_now_playing, container, false);
       songName = view.findViewById(R.id.textViewBottomView);
       artistName = view.findViewById(R.id.artistBottomView);
       bottom_prev = view.findViewById(R.id.bottom_skip_prev);
       bottom_next = view.findViewById(R.id.bottom_skip_next);
       bottom_floatingActionButton = view.findViewById(R.id.bottom_play);
       albumArt = view.findViewById(R.id.bottom_album_art);
       bottom_floatingActionButton = view.findViewById(R.id.bottom_play);
        bottom_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    try {
                        if (musicService != null){
                            musicService.nextBtnClicked();
                            if (getActivity() != null) {
                                SharedPreferences.Editor editor = getActivity().getSharedPreferences(MUSIC_LAST_PLAYED, MODE_PRIVATE)
                                        .edit();
                                editor.putString(MUSIC_FILE, musicService.lists.get(musicService.position).getPath());
                                editor.putString(ARTIST_NAME, musicService.lists.get(musicService.position).getArtist());
                                editor.putString(SONG_NAME, musicService.lists.get(musicService.position).getTitle());
                                editor.apply();
                                SharedPreferences preferences = getActivity().getSharedPreferences(MUSIC_LAST_PLAYED, MODE_PRIVATE);
                                //contains the music path
                                String path = preferences.getString(MUSIC_FILE, null);
                                String song = preferences.getString(SONG_NAME, null);
                                String artist = preferences.getString(ARTIST_NAME, null);
                                if (path != null){
                                    SHOW_MINI_PLAYER = true;
                                    PATH_TO_FRAGMENT = path;
                                    SONG_NAME_TO_FRAGMENT = song;
                                    ARTIST_TO_FRAGMENT = artist;
                                }else{
                                    SHOW_MINI_PLAYER = false;
                                    PATH_TO_FRAGMENT = null;
                                    SONG_NAME_TO_FRAGMENT = null;
                                    ARTIST_TO_FRAGMENT = null;
                                }
                                if (SHOW_MINI_PLAYER){
                                    if (PATH_TO_FRAGMENT != null) {
                                        byte[] art = getAlbumArt(PATH_TO_FRAGMENT);
                                        // show embedded picture
                                        if (art != null){
                                            Glide.with(getContext()).load(art).into(albumArt);
                                        }else{
                                            Glide.with(getContext()).load(R.drawable.picsart1).into(albumArt);
                                        }
                                        songName.setText(SONG_NAME_TO_FRAGMENT);
                                        artistName.setText(ARTIST_TO_FRAGMENT);
                                    }
                                }
                            }
                        }
                    }catch (Exception e){
                        if (musicService != null){
                            musicService.nextBtnClicked();
                            if (getActivity() != null) {
                                SharedPreferences.Editor editor = getActivity().getSharedPreferences(MUSIC_LAST_PLAYED, MODE_PRIVATE)
                                        .edit();
                                editor.putString(MUSIC_FILE, musicService.lists.get(musicService.position).getPath());
                                editor.putString(ARTIST_NAME, musicService.lists.get(musicService.position).getArtist());
                                editor.putString(SONG_NAME, musicService.lists.get(musicService.position).getTitle());
                                editor.apply();
                                SharedPreferences preferences = getActivity().getSharedPreferences(MUSIC_LAST_PLAYED, MODE_PRIVATE);
                                //contains the music path
                                String path = preferences.getString(MUSIC_FILE, null);
                                String song = preferences.getString(SONG_NAME, null);
                                String artist = preferences.getString(ARTIST_NAME, null);
                                if (path != null){
                                    SHOW_MINI_PLAYER = true;
                                    PATH_TO_FRAGMENT = path;
                                    SONG_NAME_TO_FRAGMENT = song;
                                    ARTIST_TO_FRAGMENT = artist;
                                }else{
                                    SHOW_MINI_PLAYER = false;
                                    PATH_TO_FRAGMENT = null;
                                    SONG_NAME_TO_FRAGMENT = null;
                                    ARTIST_TO_FRAGMENT = null;
                                }
                                if (SHOW_MINI_PLAYER){
                                    if (PATH_TO_FRAGMENT != null) {
                                        byte[] art = getAlbumArt(PATH_TO_FRAGMENT);
                                        // show embedded picture
                                        if (art != null){
                                            Glide.with(getContext()).load(art).into(albumArt);
                                        }else{
                                            Glide.with(getContext()).load(R.drawable.picsart1).into(albumArt);
                                        }
                                        songName.setText(SONG_NAME_TO_FRAGMENT);
                                        artistName.setText(ARTIST_TO_FRAGMENT);
                                    }
                                }
                            }
                        }
                    }
                }catch (Exception e){
                    Toast.makeText(getContext(), "Pick a song from the list!", Toast.LENGTH_SHORT).show();
                }


            }
        });
        bottom_prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (musicService != null){
                        musicService.prevBtnClicked();
                        if (getActivity() != null) {
                            SharedPreferences.Editor editor = getActivity().getSharedPreferences(MUSIC_LAST_PLAYED, MODE_PRIVATE)
                                    .edit();
                            editor.putString(MUSIC_FILE, musicService.lists.get(musicService.position).getPath());
                            editor.putString(ARTIST_NAME, musicService.lists.get(musicService.position).getArtist());
                            editor.putString(SONG_NAME, musicService.lists.get(musicService.position).getTitle());
                            editor.apply();
                            SharedPreferences preferences = getActivity().getSharedPreferences(MUSIC_LAST_PLAYED, MODE_PRIVATE);
                            //contains the music path
                            String path = preferences.getString(MUSIC_FILE, null);
                            String song = preferences.getString(SONG_NAME, null);
                            String artist = preferences.getString(ARTIST_NAME, null);
                            if (path != null){
                                SHOW_MINI_PLAYER = true;
                                PATH_TO_FRAGMENT = path;
                                SONG_NAME_TO_FRAGMENT = song;
                                ARTIST_TO_FRAGMENT = artist;
                            }else{
                                SHOW_MINI_PLAYER = false;
                                PATH_TO_FRAGMENT = null;
                                SONG_NAME_TO_FRAGMENT = null;
                                ARTIST_TO_FRAGMENT = null;
                            }
                            if (SHOW_MINI_PLAYER){
                                if (PATH_TO_FRAGMENT != null) {
                                    byte[] art = getAlbumArt(PATH_TO_FRAGMENT);
                                    // show embedded picture
                                    if (art != null){
                                        Glide.with(getContext()).load(art).into(albumArt);
                                    }else{
                                        Glide.with(getContext()).load(R.drawable.picsart1).into(albumArt);
                                    }
                                    songName.setText(SONG_NAME_TO_FRAGMENT);
                                    artistName.setText(ARTIST_TO_FRAGMENT);
                                }
                            }
                        }
                    }
                }catch (Exception e){
                    Toast.makeText(getContext(), "Music player just restarted, pick a song from your list", Toast.LENGTH_SHORT).show();
                }

            }
        });
        bottom_floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (musicService != null){
                        musicService.playBtnClicked();
                        if (musicService.isPlaying()){
                            bottom_floatingActionButton.setImageResource(R.drawable.ic_pause);
                        }else {
                            bottom_floatingActionButton.setImageResource(R.drawable.ic_play);
                        }
                    }
                }catch (Exception e){
                    Toast.makeText(getContext(), "Music player just restarted, pick a song from your list", Toast.LENGTH_SHORT).show();
                }

            }
        });
       return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (SHOW_MINI_PLAYER){
            if (PATH_TO_FRAGMENT != null) {
                byte[] art = getAlbumArt(PATH_TO_FRAGMENT);
                // show embedded picture
                if (art != null){
                    Glide.with(getContext()).load(art).into(albumArt);
                }else{
                    Glide.with(getContext()).load(R.drawable.picsart1).into(albumArt);
                }
                songName.setText(SONG_NAME_TO_FRAGMENT);
                artistName.setText(ARTIST_TO_FRAGMENT);
               Intent intent = new Intent(getContext(), MusicService.class);
                if (getContext() != null){
                    getContext().bindService(intent, this, Context.BIND_AUTO_CREATE);
                }
            }
        }
        try {
            if (musicService != null){
                if (musicService.isPlaying()){
                    bottom_floatingActionButton.setImageResource(R.drawable.ic_pause);
                }else if (!musicService.isPlaying()){
                    bottom_floatingActionButton.setImageResource(R.drawable.ic_play);
                }
            }
        }catch (Exception e){
            Toast.makeText(getContext(), "Please play a Song from the List", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onPause() {
        super.onPause();

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

        }
        return new byte[0];
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        MusicService.MyBinder binder = (MusicService.MyBinder) service;
       musicService = binder.getService();
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        musicService = null;
    }
}