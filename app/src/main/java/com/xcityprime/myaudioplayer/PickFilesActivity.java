package com.xcityprime.myaudioplayer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.xcityprime.myaudioplayer.MainActivity.musicFiles;

public class PickFilesActivity extends AppCompatActivity {


    PlayListSongsAdapter playListSongsAdapter;
    RecyclerView recyclerView;
    EditText playListTitle;
    private Button doneBtn;
    static String playListTitleFrmEditText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_files);
        playListTitle = findViewById(R.id.playListTitle);
        doneBtn = findViewById(R.id.doneBtn);
        recyclerView = findViewById(R.id.recycleViewPlayList);
        recyclerView.setHasFixedSize(true);
        if (!(musicFiles.size() < 1)){
            playListSongsAdapter = new PlayListSongsAdapter(musicFiles, this);
            recyclerView.setAdapter(playListSongsAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        }

        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playListTitleFrmEditText = playListTitle.getText().toString();
                Toast.makeText(PickFilesActivity.this, "New Favourite Created", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(PickFilesActivity.this, MainActivity.class);
                startActivity(intent);


            }
        });
    }
}