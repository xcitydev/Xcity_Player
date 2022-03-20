package com.xcityprime.myaudioplayer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.xcityprime.myaudioplayer.MainActivity.albums;


/**
 * A simple {@link Fragment} subclass.
 */
public class Album extends Fragment {

    RecyclerView recyclerView;
    AlbumAdapter albumAdapter;
    public Album() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_album, container, false);
        recyclerView = view.findViewById(R.id.recycleView);
        recyclerView.setHasFixedSize(true);
        if (!(albums.size() < 1)){
            albumAdapter = new AlbumAdapter(getContext(), albums);
            recyclerView.setAdapter(albumAdapter);
            recyclerView.setLayoutManager(new GridLayoutManager(getContext(),2));
        }
        return view;
    }
}
