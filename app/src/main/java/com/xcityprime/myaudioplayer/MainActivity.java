package com.xcityprime.myaudioplayer;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Random;

import cn.hugeterry.coordinatortablayout.CoordinatorTabLayout;
import smartdevelop.ir.eram.showcaseviewlib.GuideView;
import smartdevelop.ir.eram.showcaseviewlib.config.DismissType;
import smartdevelop.ir.eram.showcaseviewlib.listener.GuideListener;

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {
    public static final int REQUEST_CODE = 1;
    // For the video aspect
    static ArrayList<VideoFiles> videoFiles = new ArrayList<>();
    static ArrayList<String> folderList = new ArrayList<>();
    // not among
    ImageButton minimize;
    FrameLayout frameLayout;
    static float points = (float) 0.2;
    static ArrayList<MusicFiles> musicFiles;
    static boolean shuffleBoolean = false, repeatBoolean = false;
    static ArrayList<MusicFiles> albums = new ArrayList<>();
    private CoordinatorTabLayout coordinatorTabLayout;
    private ViewPager viewPager;
    private int[] imageArray = new int[]{R.drawable.music, R.drawable.album1, R.drawable.album3, R.drawable.youneverjustknow, R.drawable.folder};
    private int[] colorArray = new int[]{R.color.color1, R.color.color2, R.color.color3, R.color.color4, R.color.colorPrimary};
    private String MY_SORT_PREF = "SortOrder";
    public static final String MUSIC_LAST_PLAYED = "LAST_PLAYED";
    public static final String MUSIC_FILE = "STORED_MUSIC";
    public static boolean SHOW_MINI_PLAYER = false;
    public static String PATH_TO_FRAGMENT= null;
    public static String ARTIST_TO_FRAGMENT= null;
    private FirebaseUser user;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    public static String SONG_NAME_TO_FRAGMENT= null;
    public static final String ARTIST_NAME = "ARTIST_NAME";
    public static final String SONG_NAME = "SONG_NAME";
    public final String Pref = "PREF_NAME";

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        minimize = findViewById(R.id.minize);
        frameLayout = findViewById(R.id.frameLay);
        permission();
        user = FirebaseAuth.getInstance().getCurrentUser();
        minimize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                frameLayout.setVisibility(View.GONE);
            }
        });


    }
    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void permission(){
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},REQUEST_CODE);
            }
        } else{
            musicFiles = getAllAudio(this);
            videoFiles = getAllVideos(this);
            initViewPager();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                musicFiles = getAllAudio(this);
                videoFiles = getAllVideos(this);
                initViewPager();
                SharedPreferences settings = getSharedPreferences(Pref, 0);
                if(settings.getBoolean("my_first_time", true)){
                    Random random = new Random();
                    int uniqueNumber = random.nextInt(999999);
                    new GuideView.Builder(MainActivity.this)
                            .setTitle("Welcome to X-city Player")
                            .setContentText("Click on the menu button at the Top Right to send a message to the Developer.\n  Write down your Unique Number: " +
                                    ""+uniqueNumber+" to be able to withdraw rewards")
                            .setTargetView(frameLayout)
                            .setDismissType(DismissType.targetView)
                            .setGuideListener(new GuideListener() {
                                @Override
                                public void onDismiss(View view) {
                                    new GuideView.Builder(MainActivity.this)
                                            .setTitle("Close Mini Controls")
                                            .setContentText("Click here to close mini controls")
                                            .setTargetView(minimize)
                                            .setDismissType(DismissType.targetView)
                                            .build().show();
                                }
                            })
                            .build()
                            .show();
                    settings.edit().putBoolean("my_first_time", false).putInt("un",uniqueNumber).apply();
                }
                //Do whatever you want permission granted to do
            }else{ Toast.makeText(MainActivity.this,"Permission Denied", Toast.LENGTH_SHORT).show();
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},REQUEST_CODE);}
        }
    }

    private void initViewPager(){
        viewPager = findViewById(R.id.viewPager);
        coordinatorTabLayout = findViewById(R.id.coordinatorLay);
        viewPagerAdapter viewPagerAdapter = new viewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addFragmet(new Songs(),"Songs");
        viewPagerAdapter.addFragmet(new Album(),"Albums");
        viewPagerAdapter.addFragmet(new PlayListFragment(), "PlayList");
        viewPagerAdapter.addFragmet(new Videos(), "Videos");
        viewPagerAdapter.addFragmet(new Folder(), "Folder");
        viewPager.setAdapter(viewPagerAdapter);
        coordinatorTabLayout.setTranslucentStatusBar(this)
                .setTitle("X-city Player")
                .setImageArray(imageArray, colorArray)
                .setBackEnable(true)
                .setupWithViewPager(viewPager);
    }
    public static class viewPagerAdapter extends FragmentPagerAdapter{
        private ArrayList<Fragment> fragments;
        private ArrayList<String> titles;

        public viewPagerAdapter(@NonNull FragmentManager fm) {
            super(fm);
            this.fragments = new ArrayList<>();
            this.titles = new ArrayList<>();
        }

        void addFragmet(Fragment fragment, String title){
            fragments.add(fragment);
            titles.add(title);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return titles.get(position);
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.Q)
    public ArrayList<MusicFiles> getAllAudio(Context context){
        SharedPreferences preferences = getSharedPreferences(MY_SORT_PREF, MODE_PRIVATE);
        String sortOrder = preferences.getString("sorting","sortByName");
        ArrayList<String> duplicate = new ArrayList<>();
        albums.clear();
        ArrayList<MusicFiles> tempAudioList = new ArrayList<>();
        String order = null;
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        switch (sortOrder){
            case "sortByName":
                order = MediaStore.MediaColumns.DISPLAY_NAME + " ASC";
                break;
            case "sortByDate":
                order = MediaStore.MediaColumns.DATE_ADDED + " ASC";
                break;
            case "sortBySize":
                order = MediaStore.MediaColumns.SIZE + " DESC";
                break;
        }
        String[] projection ={
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media._ID

        };
        Cursor cursor = context.getContentResolver().query(uri,projection,null,null, order);
        if (cursor != null){
            while (cursor.moveToNext()){
                String album = cursor.getString(0);
                String title = cursor.getString(1);
                String duration = cursor.getString(2);
                String path = cursor.getString(3);
                String artist = cursor.getString(4);
                String id = cursor.getString(5);

                MusicFiles musicFiles = new MusicFiles(path,title,artist,album,duration,id);
                //take Log.e for check
                Log.e("Path: "+ path ,"Album: "+album);
                tempAudioList.add(musicFiles);
                if (!duplicate.contains(album)){
                    albums.add(musicFiles);
                    duplicate.add(album);
                }
            }
            cursor.close();
        }
        return tempAudioList;
    }

    // Video aspect continued
    public ArrayList<VideoFiles> getAllVideos(Context context){
        SharedPreferences preferences = getSharedPreferences(MY_SORT_PREF, MODE_PRIVATE);
        String sortOrder = preferences.getString("sorting","sortByName");
        ArrayList<VideoFiles> tempVideoFiles = new ArrayList<>();
        String order2 = null;
        Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;

        switch (sortOrder){
            case "sortByName":
                order2 = MediaStore.MediaColumns.DISPLAY_NAME + " ASC";
                break;
            case "sortByDate":
                order2 = MediaStore.MediaColumns.DATE_ADDED + " ASC";
                break;
            case "sortBySize":
                order2 = MediaStore.MediaColumns.SIZE + " DESC";
                break;
        }

        String[] projection1 = {
                MediaStore.Video.Media._ID,
                MediaStore.Video.Media.DATA,
                MediaStore.Video.Media.TITLE,
                MediaStore.Video.Media.SIZE,
                MediaStore.Video.Media.DATE_ADDED,
                MediaStore.Video.Media.DURATION,
                MediaStore.Video.Media.DISPLAY_NAME
        };
        Cursor cursor = context.getContentResolver().query(uri, projection1, null, null, order2);
        if (cursor != null){
            while (cursor.moveToNext()){
                String id = cursor.getString(0);
                String path = cursor.getString(1);
                String title = cursor.getString(2);
                String size = cursor.getString(3);
                String dateAdded = cursor.getString(4);
                String duration = cursor.getString(5);
                String fileName = cursor.getString(6);
                VideoFiles videoFiles = new VideoFiles(id, path, title, fileName, size, dateAdded, duration);
                Log.e("Path", path);
                // go to the path that looks like /storage/sd_card/VideoDir/Abc/myVideoFile.mp4
                int slashFirstIndex = path.lastIndexOf("/");
                String subString = path.substring(0, slashFirstIndex);
                // go to the path that looks like /storage/sd_card/VideoDir/Abc because last index would be excluded so slash

                // now we get folder names like the abc

               //int index = subString.lastIndexOf("/");
               //String folderName = subString.substring(index + 1, slashFirstIndex);
                if (!folderList.contains(subString)){
                    folderList.add(subString);
                }
                tempVideoFiles.add(videoFiles);
            }
            cursor.close();
        }
        return tempVideoFiles;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search, menu);
        MenuItem menuItem = menu.findItem(R.id.search_option);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setOnQueryTextListener(this);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        String userInput = newText.toLowerCase();
        ArrayList<MusicFiles> myFiles = new ArrayList<>();
        for (MusicFiles song : musicFiles){
            if (song.getTitle().toLowerCase().contains(userInput)){
                myFiles.add(song);
            }
        }
        Songs.musicAdapter.updateList(myFiles);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        SharedPreferences.Editor editor = getSharedPreferences(MY_SORT_PREF, MODE_PRIVATE).edit();
        switch (item.getItemId()){
            case R.id.by_name:
                editor.putString("sorting", "sortByName");
                editor.apply();
                this.recreate();
                break;
            case R.id.by_date:
                editor.putString("sorting", "sortByDate");
                editor.apply();
                this.recreate();
                break;
            case R.id.by_size:
                editor.putString("sorting", "sortBySize");
                editor.apply();
                this.recreate();
                break;
            case R.id.about_view:
                Intent intent = new Intent(MainActivity.this, About_Creator.class);
                startActivity(intent);
                break;
            case R.id.turnOff:
                SharedPreferences uniqueNum = getSharedPreferences(Pref, MODE_PRIVATE);
                int nu = uniqueNum.getInt("un", 0);
                Toast.makeText(this, "Your Unique Number is: Xcity"+nu+"Player", Toast.LENGTH_SHORT).show();
                SharedPreferences preferences12 = getSharedPreferences("lolt", Context.MODE_PRIVATE);
                float fl = preferences12.getFloat("floatt", 0);
                String po = String.valueOf(fl);
                if(user != null){
                    db.collection("users").document(user.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot documentSnapshot = task.getResult();
                                String datas = documentSnapshot.getString("bttEarned");
                                Toast.makeText(MainActivity.this, "Total BTT earned: "+datas+" Withdrawal starts from 500Btt", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }else{
                    Toast.makeText(MainActivity.this, "Kingly login to your X-city Player acct", Toast.LENGTH_LONG).show();
                }
                if (points > 500){
                    Toast.makeText(this, "You can withdraw your BTT Now!. Message the developer from About Creator to withdraw now!", Toast.LENGTH_SHORT).show();
                }
                break;
            case android.R.id.home:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (frameLayout!=null){
            frameLayout.setVisibility(View.VISIBLE);
        }
        SharedPreferences preferences = getSharedPreferences(MUSIC_LAST_PLAYED, MODE_PRIVATE);
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
            ARTIST_TO_FRAGMENT = null;
            SONG_NAME_TO_FRAGMENT = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SharedPreferences preferences1 = getSharedPreferences("lolt", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences1.edit();
        editor.clear();
        editor.putFloat("floatt", points);
        editor.apply();
        editor.commit();
    }
}
