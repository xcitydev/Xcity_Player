package com.xcityprime.myaudioplayer;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import java.util.ArrayList;

import static com.xcityprime.myaudioplayer.ApplicationClass.CLOSE_N;
import static com.xcityprime.myaudioplayer.ApplicationClass.ID2;
import static com.xcityprime.myaudioplayer.ApplicationClass.Next;
import static com.xcityprime.myaudioplayer.ApplicationClass.PREV;
import static com.xcityprime.myaudioplayer.ApplicationClass.Play;
import static com.xcityprime.myaudioplayer.PlayerActivity.listSongs;

public class MusicService extends Service implements MediaPlayer.OnCompletionListener, AudioManager.OnAudioFocusChangeListener {
    IBinder myBinder = new MyBinder();
    Uri uri;
    ArrayList<MusicFiles> lists = new ArrayList<>();
    int position = -1;
    ActionPlaying actionPlaying;
    MediaSessionCompat mediaSessionCompat;
    MediaPlayer mediaPlayer;
    //Audio Focus
    AudioManager mAudioManager;
    public static final String MUSIC_LAST_PLAYED = "LAST_PLAYED";
    public static final String MUSIC_FILE = "STORED_MUSIC";
    public static final String ARTIST_NAME = "ARTIST_NAME";
    public static final String SONG_NAME = "SONG_NAME";
    @Override
    public void onCreate() {
        super.onCreate();
        lists = listSongs;
        mediaSessionCompat = new MediaSessionCompat(getBaseContext(), "My audio");
        mAudioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);


    }

// AudioFocus

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.e("Bind", "Method");
        lists = listSongs;
        return myBinder;
    }

    @Override
    public void onAudioFocusChange(int focusChange) {
        switch (focusChange){
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                mediaPlayer.pause();
                break;
            case AudioManager.AUDIOFOCUS_GAIN:
                mediaPlayer.start();
                break;
            case AudioManager.AUDIOFOCUS_LOSS:
                mediaPlayer.pause();
                break;
        }
    }


    public class MyBinder extends Binder {
        MusicService getService(){
            return MusicService.this;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int myPosition = intent.getIntExtra("servicePosition", -1);
        String actionName = intent.getStringExtra("ActionName");
        if (myPosition != -1){
            playMedia(myPosition);
        }
        if (actionName != null){
            switch (actionName){
                case "play":
                    Toast.makeText(this, "Playing", Toast.LENGTH_SHORT).show();
                    playBtnClicked();
                    break;
                case "previous":
                    //Toast.makeText(this, "previous", Toast.LENGTH_SHORT).show();
                    prevBtnClicked();
                    break;
                case "Next":
                    //Toast.makeText(this, "next", Toast.LENGTH_SHORT).show();
                    nextBtnClicked();
                    break;
                case "close_n":
                    Toast.makeText(this, "closing X-city player", Toast.LENGTH_SHORT).show();
                    if (actionPlaying != null){
                        stopForeground(true);
                       actionPlaying.closeBtn();
                        onDestroy();
                    }
                    onDestroy();
            }
        }
        return START_STICKY;
    }

    private void playMedia(int startPosition) {
        lists = listSongs;
        position = startPosition;
        if (mediaPlayer != null){
            mediaPlayer.stop();
            mediaPlayer.release();
            if (lists != null){
                createMediaplayer(position);
                mediaPlayer.start();
            }
        }else{
            createMediaplayer(position);
            mediaPlayer.start();
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        stopForeground(true);
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    void start(){
        //Request AudioFocus
        int requestAudioFocusResult = mAudioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        if (requestAudioFocusResult == AudioManager.AUDIOFOCUS_REQUEST_GRANTED){
            mediaPlayer.start();
        }
    }

    boolean isPlaying(){
        return mediaPlayer.isPlaying();
    }
    void stop(){
        mediaPlayer.stop();
    }
    void release(){
        mediaPlayer.release();
    }
    int getDuration(){
        return mediaPlayer.getDuration();
    }
    void seekTo(int position){
        mediaPlayer.seekTo(position);
    }
    int getCurrentPosition(){
        return  mediaPlayer.getCurrentPosition();
    }
    void pause(){
        mediaPlayer.pause();
        mAudioManager.abandonAudioFocus(this);
    }
    void createMediaplayer(int positionInner){
        lists = listSongs;
        position = positionInner;
        uri = Uri.parse(lists.get(position).getPath());
        //SharedPreferences are used to store paths here its a string path
        SharedPreferences.Editor editor = getSharedPreferences(MUSIC_LAST_PLAYED, MODE_PRIVATE)
                .edit();
        editor.putString(MUSIC_FILE, uri.toString());
        editor.putString(ARTIST_NAME, lists.get(position).getArtist());
        editor.putString(SONG_NAME, lists.get(position).getTitle());
        editor.apply();
        mediaPlayer = MediaPlayer.create(getBaseContext(), uri);
    }
    void OnCompleted(){
        showNotificationButton(R.drawable.ic_pause);
        mediaPlayer.setOnCompletionListener(this);
    }
    @Override
    public void onCompletion(MediaPlayer mp) {
        if (actionPlaying != null){
            actionPlaying.nextBtnClicked();
            if(mediaPlayer != null){
                SharedPreferences preferences = getSharedPreferences("NewData", MODE_PRIVATE);
                int posi = preferences.getInt("newD",0);
                if (posi != 0){
                    createMediaplayer(posi);
                }else
                {
                    createMediaplayer(position);
                }
                mediaPlayer.start();
                OnCompleted();
            }
        }
    }
    void setCallBack(ActionPlaying actionPlaying){
        this.actionPlaying = actionPlaying;
    }

    void showNotificationButton(int playPauseBtn){
        //go through the notification series its consist of five videos
        Intent intent = new Intent(this, PlayerActivity.class);
        PendingIntent contentIntent = PendingIntent.getBroadcast(this, 0, intent,0);

        Intent prevIntent = new Intent(this, NotificationReceiver.class).setAction(PREV);
        PendingIntent prevPendingIntent = PendingIntent.getBroadcast(this, 0, prevIntent,PendingIntent.FLAG_UPDATE_CURRENT);

        Intent pauseIntent = new Intent(this, NotificationReceiver.class).setAction(Play);
        PendingIntent pausePendingIntent = PendingIntent.getBroadcast(this, 0, pauseIntent,PendingIntent.FLAG_UPDATE_CURRENT);

        Intent nextIntent = new Intent(this, NotificationReceiver.class).setAction(Next);
        PendingIntent nextPendingIntent = PendingIntent.getBroadcast(this, 0, nextIntent,PendingIntent.FLAG_UPDATE_CURRENT);

        Intent closeIntent = new Intent(this, NotificationReceiver.class).setAction(CLOSE_N);
        PendingIntent closePendingIntent = PendingIntent.getBroadcast(this, 0, closeIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        //Remove this later
        Intent resumeIntent = new Intent(this, NotificationTest.class);
        PendingIntent pendingIntentResume = PendingIntent.getActivity(this, 0, resumeIntent, PendingIntent.FLAG_UPDATE_CURRENT);


        byte[] picture = null;
        picture = getAlbumArt(lists.get(position).getPath());
        Bitmap thumb = null;
        if (picture != null){
            thumb = BitmapFactory.decodeByteArray(picture, 0, picture.length);

        }else{
            thumb = BitmapFactory.decodeResource(getResources(), R.drawable.picsart2);
        }
        int progress = 10;
        Notification notification = new NotificationCompat.Builder(this, ID2)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setSmallIcon(playPauseBtn)
                .setLargeIcon(thumb)
                .setContentTitle(lists.get(position).getTitle())
                .setContentText(lists.get(position).getArtist())
                .addAction(R.drawable.ic_skip_previous, "Previous", prevPendingIntent)
                .addAction(playPauseBtn, "Pause", pausePendingIntent)
                .addAction(R.drawable.ic_skip_next, "Next", nextPendingIntent)
                .addAction(R.drawable.ic_close, "Close", closePendingIntent)
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                        .setMediaSession(mediaSessionCompat.getSessionToken()))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setOnlyAlertOnce(true)
                .setContentIntent(pendingIntentResume)//remove
                .setColor(Color.GREEN)
                .setOngoing(true)
                .build();
        startForeground(2, notification);

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

    void nextBtnClicked(){
        if (actionPlaying != null){
            actionPlaying.nextBtnClicked();
        }
    }

    void prevBtnClicked(){
        if (actionPlaying != null){
            actionPlaying.prevBtnClicked();
        }
    }

    void playBtnClicked(){
        if (actionPlaying != null){
            actionPlaying.playPauseBtnClicked();
        }
    }
}
