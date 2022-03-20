package com.xcityprime.myaudioplayer;

import static com.xcityprime.myaudioplayer.AlbumDetailsAdapter.albumFiles;
import static com.xcityprime.myaudioplayer.MainActivity.points;
import static com.xcityprime.myaudioplayer.MainActivity.repeatBoolean;
import static com.xcityprime.myaudioplayer.MainActivity.shuffleBoolean;
import static com.xcityprime.myaudioplayer.MusicAdapter.mFiles;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.palette.graphics.Palette;

import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdCallback;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Random;

import smartdevelop.ir.eram.showcaseviewlib.GuideView;
import smartdevelop.ir.eram.showcaseviewlib.config.DismissType;
import smartdevelop.ir.eram.showcaseviewlib.listener.GuideListener;

public class PlayerActivity extends AppCompatActivity implements ActionPlaying, ServiceConnection {

    TextView song_name, artist_name, duration_played, duration_total;
    ImageView cover_art, nextBtn, prevBtn, backBtn, shuffleBtn, repeatBtn, icMenu;
    FloatingActionButton playPauseBtn;
    ImageButton goldCoin1, goldCoin2;
    SeekBar seekBar;
    int position = -1;
    static ArrayList<MusicFiles> listSongs = new ArrayList<>();
    static Uri uri;
    int reward = 5;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private RewardedAd mRewardedAd;
    //static MediaPlayer mediaPlayer;
    private Handler handler = new Handler();
    private Thread playThread, prevThread, nextThread;
    public AdView adView1;
    AudioManager audioManager;
    private InterstitialAd interstitialAd12, interstitialAd1;
    MusicService musicService;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setFullscreen();
        setContentView(R.layout.activity_player);
        initViews();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        SharedPreferences settings = getSharedPreferences("Pref", 0);
        if(settings.getBoolean("my_first_time", true)){
            new GuideView.Builder(PlayerActivity.this)
                    .setTitle("New Controls")
                    .setContentText("Swipe Left to go to the previous song\n Swipe Right to go to Next song\nSwipe Up to increase volume\n Swipe Down to decrease Volume")
                    .setTargetView(cover_art)
                    .setDismissType(DismissType.targetView)
                    .setGuideListener(new GuideListener() {
                        @Override
                        public void onDismiss(View view) {
                            new GuideView.Builder(PlayerActivity.this)
                                    .setTitle("Earn while playing your favorite songs")
                                    .setContentText("You can now earn while using X-city player.\n Click on this button, wait for 10secs and get free BTT token!")
                                    .setTargetView(goldCoin1)
                                    .setDismissType(DismissType.targetView)
                                    .build().show();
                        }
                    })
                    .build()
                    .show();
            settings.edit().putBoolean("my_first_time", false).commit();
        }
        getIntentMethod();
        audioManager = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        //TODO change the audio name
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {

            }
        });
        adView1 = new AdView(this);
        adView1 = findViewById(R.id.adView1);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView1.loadAd(adRequest);


        //Rewarded Ads
        interstitialAd1 = new InterstitialAd(this);
        interstitialAd1.setAdUnitId("ca-app-pub-9988395020991274/2653107241");
        interstitialAd1.loadAd(new AdRequest.Builder().build());

        interstitialAd12 = new InterstitialAd(this);
        interstitialAd12.setAdUnitId("ca-app-pub-9988395020991274/3774617225");
        interstitialAd12.loadAd(new AdRequest.Builder().build());
        //ADS End
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (musicService != null && fromUser){
                    musicService.seekTo(progress * 1000);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        PlayerActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (musicService != null){
                    int mCurrentPosition = musicService.getCurrentPosition()/1000;
                    seekBar.setProgress(mCurrentPosition);
                    duration_played.setText(formmatedTime(mCurrentPosition));
                }
                handler.postDelayed(this,1000);
            }
        });
        shuffleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (shuffleBoolean){
                    shuffleBoolean = false;
                    shuffleBtn.setImageResource(R.drawable.ic_shuffle);

                }else{
                    shuffleBoolean = true;
                    shuffleBtn.setImageResource(R.drawable.ic_shuffle_on);
                }
            }
        });
        repeatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(firebaseUser != null){
                    db.collection("users").document(firebaseUser.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot documentSnapshot = task.getResult();
                                String datas = documentSnapshot.getString("bttEarned");
                                Toast.makeText(PlayerActivity.this, datas, Toast.LENGTH_SHORT).show();
                                int newValue = Integer.parseInt(datas)+reward;
                                String neShit = String.valueOf(newValue);
                                db.collection("users").document(firebaseUser.getUid()).update(
                                        "bttEarned" , neShit
                                ).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            Toast.makeText(PlayerActivity.this,"Success", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        }
                    });
                }
                if (interstitialAd1 != null){
                    interstitialAd1.show();
                }
                if (repeatBoolean){
                    repeatBoolean = false;
                    repeatBtn.setImageResource(R.drawable.ic_repeat);
                }
                else {
                    repeatBoolean = true;
                    repeatBtn.setImageResource(R.drawable.ic_repeat_on);
                }
            }
        });
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (interstitialAd12 != null){
                    interstitialAd12.show();
                }
                onBackPressed();
            }
        });

        cover_art.setOnTouchListener(new OnSwipeTouchListener(PlayerActivity.this){
            public void onSwipeRight(){
                nextBtnClicked();
            }
            public void onSwipeLeft(){
                prevBtnClicked();
            }
            public void onSwipeTop(){
                audioManager.adjustVolume(AudioManager.ADJUST_RAISE, AudioManager.FLAG_PLAY_SOUND);
            }
            public void onSwipeBottom(){
                audioManager.adjustVolume(AudioManager.ADJUST_LOWER, AudioManager.FLAG_PLAY_SOUND);
            }
        });
        goldCoin2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAds();
            }
        });
        goldCoin1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadAds();
                Toast.makeText(PlayerActivity.this, "Loading in 10secs...", Toast.LENGTH_SHORT).show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        goldCoin2.setVisibility(View.VISIBLE);
                        goldCoin1.setVisibility(View.GONE);
                    }
                },10000);
            }
        });
    }


    private void showAds(){
        if (this.mRewardedAd.isLoaded()){
            goldCoin2.setVisibility(View.GONE);
            goldCoin1.setVisibility(View.VISIBLE);
            RewardedAdCallback callback1 = new RewardedAdCallback() {
                @Override
                public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                    float p = rewardItem.getAmount();
                    float ew = p/5;
                    points = points + p;
                    Toast.makeText(PlayerActivity.this, "Earned "+ew+" Btt token", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onRewardedAdOpened() {
                    super.onRewardedAdOpened();
                    Toast.makeText(PlayerActivity.this, "Opened", Toast.LENGTH_SHORT).show();

                }

                @Override
                public void onRewardedAdClosed() {
                    super.onRewardedAdClosed();
                    Toast.makeText(PlayerActivity.this, "closed", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onRewardedAdFailedToShow(int i) {
                    super.onRewardedAdFailedToShow(i);
                    Toast.makeText(PlayerActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                }
            };
            this.mRewardedAd.show(this, callback1);
        }
        else {
            goldCoin2.setVisibility(View.GONE);
            goldCoin1.setVisibility(View.VISIBLE);
            Toast.makeText(PlayerActivity.this, "Not Loaded", Toast.LENGTH_SHORT).show();
        }
    }
   // private void setFullscreen() {
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
   // }
    private void loadAds(){
        this.mRewardedAd = new RewardedAd(this,"ca-app-pub-9988395020991274/7377630943");
        RewardedAdLoadCallback callback = new RewardedAdLoadCallback(){
            @Override
            public void onRewardedAdFailedToLoad(int i) {
                super.onRewardedAdFailedToLoad(i);
            }

            @Override
            public void onRewardedAdLoaded() {
                super.onRewardedAdLoaded();
            }
        };
        this.mRewardedAd.loadAd(new AdRequest.Builder().build(), callback);

        ///next step

    }

    @Override
    protected void onResume() {
        Intent intent = new Intent(this, MusicService.class);
        bindService(intent, this, BIND_AUTO_CREATE);
        playThreadBtn();
        prevThreadBtn();
        nextThreadBtn();
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unbindService(this);
    }

    private void prevThreadBtn() {
        prevThread = new Thread(){
            @Override
            public void run() {
                super.run();
                prevBtn.setOnClickListener(v -> prevBtnClicked());
            }
        };
        prevThread.start();
    }

    public void prevBtnClicked()
    {
        if (musicService.isPlaying()){
            musicService.stop();
            musicService.release();
            if (shuffleBoolean && !repeatBoolean){
                position = getRandom(listSongs.size() - 1);
            }else if (!shuffleBoolean && !repeatBoolean){
                position = ((position - 1) < 0 ? (listSongs.size() - 1) : (position - 1));
            }
            uri = Uri.parse(listSongs.get(position).getPath());
            musicService.createMediaplayer(position);
            metaData(uri);
            song_name.setText(listSongs.get(position).getTitle());
            artist_name.setText(listSongs.get(position).getArtist());
            seekBar.setMax(musicService.getDuration()/1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (musicService != null){
                        int mCurrentPosition = musicService.getCurrentPosition()/1000;
                        seekBar.setProgress(mCurrentPosition);
                    }
                    handler.postDelayed(this,1000);
                }
            });
            musicService.OnCompleted();
            musicService.showNotificationButton(R.drawable.ic_pause);
            playPauseBtn.setBackgroundResource(R.drawable.ic_pause);
            musicService.start();
        }else{
            musicService.stop();
            musicService.release();
            if (shuffleBoolean && !repeatBoolean){
                position = getRandom(listSongs.size() - 1);
            }else if (!shuffleBoolean && !repeatBoolean){
                position = ((position - 1) < 0 ? (listSongs.size() - 1) : (position - 1));
            }
            uri = Uri.parse(listSongs.get(position).getPath());
            musicService.createMediaplayer(position);
            metaData(uri);
            song_name.setText(listSongs.get(position).getTitle());
            artist_name.setText(listSongs.get(position).getArtist());
            seekBar.setMax(musicService.getDuration()/1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (musicService != null){
                        int mCurrentPosition = musicService.getCurrentPosition()/1000;
                        seekBar.setProgress(mCurrentPosition);
                    }
                    handler.postDelayed(this,1000);
                }
            });
            musicService.OnCompleted();
            musicService.showNotificationButton(R.drawable.ic_play);
            playPauseBtn.setBackgroundResource(R.drawable.ic_play);
        }
    }

    private void nextThreadBtn() {
        nextThread = new Thread(){
            @Override
            public void run() {
                super.run();
                nextBtn.setOnClickListener(v -> nextBtnClicked());
            }
        };
        nextThread.start();
    }

    public void nextBtnClicked() {
        if (musicService.isPlaying()){
            musicService.stop();
            musicService.release();
            if (shuffleBoolean && !repeatBoolean){
                position = getRandom(listSongs.size() - 1);
            }else if (!shuffleBoolean && !repeatBoolean){
                position = ((position+1) % listSongs.size());
            }
            //Note: else if position will be position...
            uri = Uri.parse(listSongs.get(position).getPath());
            musicService.createMediaplayer(position);
            metaData(uri);
            song_name.setText(listSongs.get(position).getTitle());
            artist_name.setText(listSongs.get(position).getArtist());
            seekBar.setMax(musicService.getDuration()/1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (musicService != null){
                        int mCurrentPosition = musicService.getCurrentPosition()/1000;
                        seekBar.setProgress(mCurrentPosition);
                    }
                    handler.postDelayed(this,1000);
                }
            });
            musicService.OnCompleted();
            musicService.showNotificationButton(R.drawable.ic_pause);
            playPauseBtn.setBackgroundResource(R.drawable.ic_pause);
            musicService.start();
        }else{
            musicService.stop();
            musicService.release();
            if (shuffleBoolean && !repeatBoolean){
                position = getRandom(listSongs.size() - 1);
            }else if (!shuffleBoolean && !repeatBoolean){
                position = ((position+1) % listSongs.size());
            }
            uri = Uri.parse(listSongs.get(position).getPath());
            musicService.createMediaplayer(position);
            metaData(uri);
            song_name.setText(listSongs.get(position).getTitle());
            artist_name.setText(listSongs.get(position).getArtist());
            seekBar.setMax(musicService.getDuration()/1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (musicService != null){
                        int mCurrentPosition = musicService.getCurrentPosition()/1000;
                        seekBar.setProgress(mCurrentPosition);
                    }
                    handler.postDelayed(this,1000);
                }
            });
            musicService.OnCompleted();
            musicService.showNotificationButton(R.drawable.ic_play);
            playPauseBtn.setBackgroundResource(R.drawable.ic_play);
        }
    }

    private int getRandom(int i) {
        Random random = new Random();
        return random.nextInt(i + 1);
    }

    private void playThreadBtn() {
        playThread = new Thread(){
            @Override
            public void run() {
                super.run();
                playPauseBtn.setOnClickListener(v -> {
                    //if (interstitialAd.isLoaded()){
                     //   interstitialAd.show();
                   // }
                    playPauseBtnClicked();
                });
            }
        };
        playThread.start();
    }

    public void playPauseBtnClicked() {
        if (musicService.isPlaying()){
            musicService.showNotificationButton(R.drawable.ic_play);
            playPauseBtn.setImageResource(R.drawable.ic_play);
            musicService.pause();
            seekBar.setMax(musicService.getDuration()/1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (musicService != null){
                        int mCurrentPosition = musicService.getCurrentPosition()/1000;
                        seekBar.setProgress(mCurrentPosition);
                    }
                    handler.postDelayed(this,1000);
                }
            });
        }else{
            musicService.showNotificationButton(R.drawable.ic_pause);
            playPauseBtn.setImageResource(R.drawable.ic_pause);
            musicService.start();
            seekBar.setMax(musicService.getDuration()/1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (musicService != null){
                        int mCurrentPosition = musicService.getCurrentPosition()/1000;
                        seekBar.setProgress(mCurrentPosition);
                    }
                    handler.postDelayed(this,1000);
                }
            });
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void closeBtn(){
        musicService.stopForeground(true);
        finishAndRemoveTask();
        //this.finishAffinity();
    }

    private String  formmatedTime(int mCurrentPosition) {
        String totalOut = "";
        String totalNew = "";
        String seconds = String.valueOf(mCurrentPosition % 60);
        String minutes = String.valueOf(mCurrentPosition/60);
        totalOut = minutes + ":"+seconds;
        totalNew = minutes + ":" + "0"+ seconds;
        if (seconds.length() == 1){
            return  totalNew;
        }else{
            return  totalOut;
        }
    }

    private void getIntentMethod(){
        position = getIntent().getIntExtra("Position",-1);
        String sender = getIntent().getStringExtra("sender");
        if (sender != null && sender.equals("albumDetails")){
            listSongs = albumFiles;
        }else {
            listSongs = mFiles;
        }
        if (listSongs != null){
            playPauseBtn.setImageResource(R.drawable.ic_pause);
            uri = Uri.parse(listSongs.get(position).getPath());
        }

        Intent intent = new Intent(this, MusicService.class);
        intent.putExtra("servicePosition", position);
        startService(intent);

    }
    private void initViews(){
        goldCoin1 = findViewById(R.id.goldCoin);
        goldCoin2 = findViewById(R.id.goldCoin2);
        song_name = findViewById(R.id.song_name);
        artist_name = findViewById(R.id.song_artist);
        duration_played = findViewById(R.id.duration_played);
        duration_total = findViewById(R.id.duration_total);
        cover_art = findViewById(R.id.cover_art);
        nextBtn = findViewById(R.id.id_next);
        prevBtn = findViewById(R.id.id_prev);
        backBtn = findViewById(R.id.back_btn);
        shuffleBtn = findViewById(R.id.id_shuffle);
        repeatBtn = findViewById(R.id.id_repeat);
        icMenu = findViewById(R.id.menu_btn);
        playPauseBtn = findViewById(R.id.play_pause);
        seekBar = findViewById(R.id.seek_bar);
    }
    private void metaData(Uri uri){
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(uri.toString());
        int durationTotal = Integer.parseInt(listSongs.get(position).getDuration())/1000;
        duration_total.setText(formmatedTime(durationTotal));
        final byte[] art = retriever.getEmbeddedPicture();
        Bitmap bitmap;
        if (art != null){
            bitmap = BitmapFactory.decodeByteArray(art,0,art.length);
            ImageAnimation(this,cover_art, bitmap);
            Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                @Override
                public void onGenerated(@Nullable Palette palette) {
                    Palette.Swatch swatch = palette.getDominantSwatch();
                    if (swatch != null){
                        ImageView gradient = findViewById(R.id.imageViewGredient);
                        RelativeLayout mContainer = findViewById(R.id.mContainer);
                        gradient.setBackgroundResource(R.drawable.gradient_bg);
                        mContainer.setBackgroundResource(R.drawable.main_bg);
                        GradientDrawable gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP,
                                new int[]{swatch.getRgb(),0x00000000});
                        gradient.setBackground(gradientDrawable);
                        GradientDrawable gradientDrawableBg = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP,
                                new int[]{swatch.getRgb(),swatch.getRgb()});
                        mContainer.setBackground(gradientDrawableBg);
                        song_name.setTextColor(swatch.getTitleTextColor());
                        artist_name.setTextColor(swatch.getBodyTextColor());
                    }else{
                        ImageView gradient = findViewById(R.id.imageViewGredient);
                        RelativeLayout mContainer = findViewById(R.id.mContainer);
                        gradient.setBackgroundResource(R.drawable.gradient_bg);
                        mContainer.setBackgroundResource(R.drawable.main_bg);
                        GradientDrawable gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP,
                                new int[]{0xff000000,0x00000000});
                        gradient.setBackground(gradientDrawable);
                        GradientDrawable gradientDrawableBg = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP,
                                new int[]{0xff000000,0xff000000});
                        mContainer.setBackground(gradientDrawableBg);
                        song_name.setTextColor(Color.WHITE);
                        artist_name.setTextColor(Color.DKGRAY);
                    }
                }
            });
        }else{
            // root error
                Glide.with(getApplicationContext())
                        .asBitmap()
                        .load(R.drawable.picsart1)
                        .into(cover_art);
                ImageView gradient = findViewById(R.id.imageViewGredient);
                RelativeLayout mContainer = findViewById(R.id.mContainer);
                gradient.setBackgroundResource(R.drawable.gradient_bg);
                mContainer.setBackgroundResource(R.drawable.main_bg);
                GradientDrawable gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP,
                        new int[]{0xff000000,0x00000000});
                gradient.setBackground(gradientDrawable);
                GradientDrawable gradientDrawableBg = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP,
                        new int[]{0xff000000,0xff000000});
                mContainer.setBackground(gradientDrawableBg);
                song_name.setTextColor(Color.WHITE);
                artist_name.setTextColor(Color.DKGRAY);

        }
    }
    public void ImageAnimation(final Context context, final ImageView imageView, final Bitmap bitmap){
        Animation animOut = AnimationUtils.loadAnimation(context, android.R.anim.fade_out);
        final Animation animIn = AnimationUtils.loadAnimation(context, android.R.anim.fade_in);
        animOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Glide.with(context).load(bitmap).into(imageView);
                animIn.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                imageView.startAnimation(animIn);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        imageView.startAnimation(animOut);
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        MusicService.MyBinder myBinder = (MusicService.MyBinder) service;
        musicService = myBinder.getService();
        musicService.setCallBack(this);
        Toast.makeText(this, "Playing  "+listSongs.get(position).getTitle()+ " By "+listSongs.get(position).getArtist(), Toast.LENGTH_SHORT).show();
        seekBar.setMax(musicService.getDuration()/1000);
        metaData(uri);
        song_name.setText(listSongs.get(position).getTitle());
        artist_name.setText(listSongs.get(position).getArtist());
        musicService.OnCompleted();
        musicService.showNotificationButton(R.drawable.ic_pause);
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        musicService = null;
    }



}
