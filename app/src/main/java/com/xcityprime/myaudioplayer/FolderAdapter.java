package com.xcityprime.myaudioplayer;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class FolderAdapter extends RecyclerView.Adapter<FolderAdapter.MyHolder> {
    private ArrayList<String> folderName;
    static ArrayList<VideoFiles> videoFiles;
    private Context mContext;

    public FolderAdapter(ArrayList<String> folderName, ArrayList<VideoFiles> videoFiles, Context mContext) {
        this.folderName = folderName;
        this.videoFiles = videoFiles;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.folder_items, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        int index = folderName.get(position).lastIndexOf("/");
        String folder = folderName.get(position).substring(index + 1);
        holder.folderName.setText(folder);
        holder.counterFiles.setText(String.valueOf(NumberOfFiles(folderName.get(position))));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, Folder_player_Activity.class);
                intent.putExtra("folderName", folderName.get(position));
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return folderName.size();
    }

    static class MyHolder extends RecyclerView.ViewHolder {
        TextView folderName, counterFiles;
        public MyHolder(@NonNull View itemView) {
            super(itemView);
            folderName = itemView.findViewById(R.id.folderName);
            counterFiles = itemView.findViewById(R.id.count_files);
        }
    }
    int NumberOfFiles(String folderName){
        int countFiles = 0;
        for (VideoFiles videofiles : videoFiles){
            if (videofiles.getPath().substring(0, videofiles.getPath().lastIndexOf("/"))
            .endsWith(folderName)){
                countFiles++;
            }
        }
        return countFiles;
    }
}
