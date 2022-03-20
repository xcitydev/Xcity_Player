package com.xcityprime.myaudioplayer;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.audio.AudioAttributes;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.util.ArrayList;

import static com.xcityprime.myaudioplayer.VideoAdapter.videoFiles;
import static com.xcityprime.myaudioplayer.VideoFolderAdapter.folderVideoFiles;

public class Video_player_activity extends AppCompatActivity  {

    PlayerView playerView;
    SimpleExoPlayer simpleExoPlayer;
    int position = -1;
    ArrayList<VideoFiles> myFiles = new ArrayList<>();
    MusicService musicService;
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setFullScreenMethod();
        setContentView(R.layout.activity_video_player_activity);
        initializeViews();
        position = getIntent().getIntExtra("position", -1);
    }

    private void setFullScreenMethod() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }


    private void initializeViews(){
        playerView = findViewById(R.id.exoplayer_movie);
    }

    private void initializePlayer(){
        String sender = getIntent().getStringExtra("sender");
        if (sender.equals("FolderIsSending")){
            myFiles = folderVideoFiles;
        }else{
            myFiles = videoFiles;
        }

        String path = myFiles.get(position).getPath();
        if (path != null){
            Uri uri = Uri.parse(path);
            simpleExoPlayer = new SimpleExoPlayer.Builder(this)
                    .build();
            AudioAttributes audioAttributes = new AudioAttributes.Builder().setUsage(C.USAGE_MEDIA).setContentType(C.CONTENT_TYPE_MOVIE).build();
            simpleExoPlayer.setAudioAttributes(audioAttributes);
            DataSource.Factory factory = new DefaultDataSourceFactory(this,
                    Util.getUserAgent(this,  "X-city Player"));
            ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
            MediaSource mediaSource = new ProgressiveMediaSource.Factory(factory, extractorsFactory).createMediaSource(uri);
            playerView.setPlayer(simpleExoPlayer);
            playerView.setKeepScreenOn(true);
            simpleExoPlayer.prepare(mediaSource);
            simpleExoPlayer.setPlayWhenReady(true);
        }
    }

    private void releasePlayer(){
        if (simpleExoPlayer != null){
            simpleExoPlayer.release();
            simpleExoPlayer = null;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (Util.SDK_INT>=24){
            initializePlayer();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Util.SDK_INT<24 || simpleExoPlayer == null){
            initializePlayer();
        }
    }

    @Override
    protected void onPause() {
        if (Util.SDK_INT<24){
            releasePlayer();
        }
        super.onPause();
    }

    @Override
    protected void onStop() {
        if (Util.SDK_INT>=24){
            releasePlayer();
        }
        super.onStop();
    }
}