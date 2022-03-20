package com.xcityprime.myaudioplayer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.xcityprime.myaudioplayer.MainActivity.videoFiles;


public class Videos extends Fragment {

    RecyclerView recyclerView;
    VideoAdapter videoAdapter;

    public Videos() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_videos, container, false);
        recyclerView = view.findViewById(R.id.recycleView);
        if (videoFiles != null && videoFiles.size() > 0){
            videoAdapter = new VideoAdapter(getContext(), videoFiles);
            recyclerView.setAdapter(videoAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        }
        return view;
    }
}