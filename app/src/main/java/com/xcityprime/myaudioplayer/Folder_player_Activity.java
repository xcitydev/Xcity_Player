package com.xcityprime.myaudioplayer;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class Folder_player_Activity extends AppCompatActivity {

    RecyclerView recyclerView;
    VideoFolderAdapter videoFolderAdapter;
    String myFolderName;
    ArrayList<VideoFiles> videoFilesArrayList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_folder_player_);

        recyclerView = findViewById(R.id.recycleView_video_folder);
        myFolderName = getIntent().getStringExtra("folderName");
        if (myFolderName != null){
            videoFilesArrayList = getAllVideos(this, myFolderName);
        }
        if (videoFilesArrayList.size() > 0) {
            videoFolderAdapter = new VideoFolderAdapter(this, videoFilesArrayList);
            recyclerView.setAdapter(videoFolderAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        }
    }
    public ArrayList<VideoFiles> getAllVideos(Context context, String folderName){
        ArrayList<VideoFiles> tempVideoFiles = new ArrayList<>();
        Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        String[] projection1 = {
                MediaStore.Video.Media._ID,
                MediaStore.Video.Media.DATA,
                MediaStore.Video.Media.TITLE,
                MediaStore.Video.Media.SIZE,
                MediaStore.Video.Media.DATE_ADDED,
                MediaStore.Video.Media.DURATION,
                MediaStore.Video.Media.DISPLAY_NAME,
                MediaStore.Video.Media.BUCKET_DISPLAY_NAME
        };
        String selection = MediaStore.Video.Media.DATA + " like?";
        String[] selectionArgs = new String[]{"%"+ folderName + "%"};
        Cursor cursor = context.getContentResolver().query(uri, projection1, selection, selectionArgs, null);
        if (cursor != null){
            while (cursor.moveToNext()){
                String id = cursor.getString(0);
                String path = cursor.getString(1);
                String title = cursor.getString(2);
                String size = cursor.getString(3);
                String dateAdded = cursor.getString(4);
                String duration = cursor.getString(5);
                String fileName = cursor.getString(6);
                String bucket_name = cursor.getString(7);
                VideoFiles videoFiles = new VideoFiles(id, path, title, fileName, size, dateAdded, duration);
                Log.e("Path", path);

        //        if (folderName.endsWith(bucket_name)) {
                    tempVideoFiles.add(videoFiles);
          //      }
            }
            cursor.close();
        }
        return tempVideoFiles;
    }
}