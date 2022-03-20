package com.xcityprime.myaudioplayer;

import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class AlbumDetailsAdapter extends RecyclerView.Adapter<AlbumDetailsAdapter.MyViewHolder> {
    private Context mContext;
    static ArrayList<MusicFiles> albumFiles;
    View view;
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(mContext).inflate(R.layout.music_items, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        holder.album_name.setText(albumFiles.get(position).getTitle());
        byte[] image = getAlbumArt(albumFiles.get(position).getPath());
        if(image != null){
            Glide.with(mContext).asBitmap()
                    .load(image)
                    .into(holder.album_image);
        }else{
            Glide.with(mContext)
                    .load(R.drawable.picsart1)
                    .into(holder.album_image);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, PlayerActivity.class);
                intent.putExtra("sender","albumDetails");
                intent.putExtra("Position", position);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return albumFiles.size();
    }

    public AlbumDetailsAdapter(Context mcontext, ArrayList<MusicFiles> albumFiles) {
        this.mContext = mcontext;
        this.albumFiles = albumFiles;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView album_image;
        TextView album_name;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            album_image = itemView.findViewById(R.id.music_img);
            album_name = itemView.findViewById(R.id.music_file_name);
        }
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
}
