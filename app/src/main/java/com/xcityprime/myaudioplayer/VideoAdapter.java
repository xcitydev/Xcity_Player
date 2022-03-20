package com.xcityprime.myaudioplayer;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.util.ArrayList;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.MyViewHolder> {
    private Context mContext;
    static ArrayList<VideoFiles> videoFiles;
    View view;
    public VideoAdapter(Context mContext, ArrayList<VideoFiles> videoFiles) {
        this.mContext = mContext;
        this.videoFiles = videoFiles;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(mContext).inflate(R.layout.video_items, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.fileName.setText(videoFiles.get(position).getTitle());
        double a = Double.parseDouble(videoFiles.get(position).getDuration())/100000;
        double roundOff = (double) Math.round(a*100.0)/100.0;
        String videoTimeCode = String.valueOf(roundOff);
        holder.videoDuration.setText(videoTimeCode);
        Glide.with(mContext).load(new File(videoFiles.get(position).getPath())).into(holder.thumbnail);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, Video_player_activity.class);
                intent.putExtra("position", position);
                intent.putExtra("sender", "FilesIsSending");
                mContext.startActivity(intent);
            }
        });
        holder.menuMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                PopupMenu popupMenu = new PopupMenu(mContext, v);
                popupMenu.getMenuInflater().inflate(R.menu.popup, popupMenu.getMenu());
                popupMenu.show();
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.delete:
                                Toast.makeText(mContext,"Delete Clicked!",Toast.LENGTH_SHORT).show();
                                deleteFile(position,v);
                                break;
                        }
                        return false;
                    }
                });
            }
        });
    }

    private void deleteFile(int position, View v){
        Uri contentUri = ContentUris.withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, Long.parseLong(videoFiles.get(position).getId()));
        //for deleting

        File file = new File(videoFiles.get(position).getPath());
        boolean deleted = file.delete(); //delete your file
        if (deleted) {
            mContext.getContentResolver().delete(contentUri,null,null);
            videoFiles.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, videoFiles.size());
            Snackbar.make(v, "File Deleted!", Snackbar.LENGTH_LONG).show();
        }else {
            //cant delete if in sdcard or api 19 and above
            Snackbar.make(v, "File Can't be Deleted!", Snackbar.LENGTH_LONG).show();
        }
    }

    @Override
    public int getItemCount() {
        return videoFiles.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        ImageView thumbnail, menuMore;
        TextView fileName, videoDuration;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            thumbnail = itemView.findViewById(R.id.thumbnail);
            menuMore = itemView.findViewById(R.id.menu_more);
            fileName = itemView.findViewById(R.id.video_file_name);
            videoDuration = itemView.findViewById(R.id.video_duration);
        }
    }
}
