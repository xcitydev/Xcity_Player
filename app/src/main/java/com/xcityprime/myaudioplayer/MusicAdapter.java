package com.xcityprime.myaudioplayer;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaMetadataRetriever;
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
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.util.ArrayList;

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.MyVieHolder> {
    private Context mContext;
    static ArrayList<MusicFiles> mFiles;
    private MainActivity mow = new MainActivity();

    MusicAdapter(Context mContext, ArrayList<MusicFiles> mFiles){
        this.mFiles = mFiles;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public MyVieHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.music_items, parent, false);
        return new MyVieHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyVieHolder holder, final int position) {
        holder.file_name.setText(mFiles.get(position).getTitle());
        byte[] image = getAlbumArt(mFiles.get(position).getPath());
        if(image != null){
            Glide.with(mContext).asBitmap()
                    .load(image)
                    .into(holder.album_art);
        }else{
            Glide.with(mContext)
                    .load(R.drawable.picsart1)
                    .into(holder.album_art);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, PlayerActivity.class);
                intent.putExtra("Position", position);
                mContext.startActivity(intent);
            }
        });
        holder.menu_more.setOnClickListener(new View.OnClickListener() {
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
                            case R.id.share:
                                String fileSend = mFiles.get(position).getPath();
                                Uri uriPath = FileProvider.getUriForFile(mContext, "com.xcityprime.myaudioplayer", new File(fileSend));
                                Intent sendIntent = new Intent(Intent.ACTION_SEND);
                                sendIntent.setType("audio/*");
                                sendIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                sendIntent.putExtra(Intent.EXTRA_STREAM, uriPath);
                                mContext.startActivity(Intent.createChooser(sendIntent, "Share Music"));
                                break;
                            case R.id.playNext:
                                String nextPlay = mFiles.get(position).getTitle();
                                Toast.makeText(mContext, "Playing "+ nextPlay+ "next!", Toast.LENGTH_SHORT).show();
                                int newData = position;
                                SharedPreferences.Editor editor = mContext.getSharedPreferences("NewData", Context.MODE_PRIVATE).edit();
                                editor.putInt("newD", newData);
                                editor.apply();
                                break;
                        }
                        return false;
                    }
                });
            }
        });
    }
    private void deleteFile(int position, View v){
        Uri contentUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, Long.parseLong(mFiles.get(position).getId()));
        //for deleting

        File file = new File(mFiles.get(position).getPath());
        boolean deleted = file.delete(); //delete your file
        if (deleted) {
            mContext.getContentResolver().delete(contentUri,null,null);
            mFiles.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, mFiles.size());
            Snackbar.make(v, "File Deleted!", Snackbar.LENGTH_LONG).show();
        }else {
            //cant delete if in sdcard or api 19 and above
            Snackbar.make(v, "File Can't be Deleted!", Snackbar.LENGTH_LONG).show();
        }
    }

    @Override
    public int getItemCount() {
        return mFiles.size();
    }

    public class MyVieHolder extends RecyclerView.ViewHolder{
        TextView file_name;
        ImageView album_art, menu_more;

        public MyVieHolder(@NonNull View itemView) {
            super(itemView);
            file_name = itemView.findViewById(R.id.music_file_name);
            album_art = itemView.findViewById(R.id.music_img);
            menu_more = itemView.findViewById(R.id.menu_more);
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
    void updateList(ArrayList<MusicFiles> musicFilesArrayList){
        mFiles = new ArrayList<>();
        mFiles.addAll(musicFilesArrayList);
        notifyDataSetChanged();
    }
}
