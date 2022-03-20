package com.xcityprime.myaudioplayer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

import static com.xcityprime.myaudioplayer.PickFilesActivity.playListTitleFrmEditText;

public class Playlist1 extends Fragment {
    View view;
    MusicAdapter playlistAdapter;
    RecyclerView recyclerView;
    public ArrayList<MusicFiles> playMa = new ArrayList<>();
    private FloatingActionButton createNew;
    TextView textTitle;
    int pos = 0;
    private ArrayList<MusicFiles> playI = new ArrayList<>();

    public Playlist1() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_playlist1, container, false);
        textTitle = view.findViewById(R.id.titleText);
        if (playListTitleFrmEditText != null){

            textTitle.setText(playListTitleFrmEditText);
        }
        recyclerView = view.findViewById(R.id.recycleViewPlayList12);
        createNew = view.findViewById(R.id.createNewPlayList12);
        createNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), PickFilesActivity.class);
                intent.putExtra("playList2", 1);
                startActivity(intent);
            }
        });
        recyclerView.setHasFixedSize(true);
        //Shared Pref
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        String listStringShared = preferences.getString("editorSa", "");
        Gson gson = new Gson();
        //String listString = getActivity().getIntent().getStringExtra("cool");
        Type type = new TypeToken<ArrayList<MusicFiles>>(){}.getType();
        playI = gson.fromJson(listStringShared, type);
        //Gson gsonn = new Gson();
        //Type type1 = new TypeToken<ArrayList<MusicFiles>>(){}.getType();
        //playMa = gsonn.fromJson(listString, type1);
        if (playI != null){
            if (!(playI.size() < 1)){
                playlistAdapter = new MusicAdapter(getContext(), playI);
                recyclerView.setAdapter(playlistAdapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
            }
        }

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}