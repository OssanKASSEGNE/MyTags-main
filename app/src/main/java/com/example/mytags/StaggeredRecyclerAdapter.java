package com.example.mytags;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;

import java.util.List;

public class StaggeredRecyclerAdapter extends RecyclerView.Adapter<StaggeredRecyclerAdapter.ImageViewHolder> {

    Context mContext;
    List<Media> mdata;

    public StaggeredRecyclerAdapter(Context mContext, List<Media> mdata) {
        this.mContext = mContext;
        this.mdata = mdata;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.row_item, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        //Get the media
        Media media = mdata.get(position);
        //Load the media Uri into the ImageView
        holder.img.setImageURI(Uri.parse(media.getImageUri()));
        //Load OnClick event into the ImageView
        holder.img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    //OpenFile onClick
                    if(media.getFileUri().isEmpty()){
                        Util.showMessage(mContext,"Pas de Type");
                    }else{
                        openFile(Uri.parse(media.getFileUri()))   ;
                    }
            }
        });

        String type = media.getMediaType();

        int color = R.color.beige_pack;
        int icon = R.drawable.ic_search;


        switch(media.getMediaType()){
            case "photo":
                color = R.color.green_pack;
                icon = R.drawable.ic_photo;
                break;
            case "video":
                color = R.color.yellow_pack;
                icon = R.drawable.ic_video;
                break;
            case "audio":
                color = R.color.red_pack;
                icon = R.drawable.ic_add_audio;
                break;
            case "file":
                color = R.color.colorPrimaryDark;
                icon = R.drawable.ic_add_file;
                break;
        }

        holder.chip.setChipIconResource(icon);
        holder.chip.setChipBackgroundColorResource(color);
        holder.chip.setText(media.getTag());




    }

    @Override
    public int getItemCount() {
        return mdata.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {

        ImageView img;
        Chip chip;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.row_img);
            chip = itemView.findViewById(R.id.chip_item);
        }
    }


    //Open AnyFile
    public void openFile(Uri fileUri) {
        String fileType = fileUri.toString();

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        intent.setData(fileUri);
        Intent chooser = Intent.createChooser(intent, "Ouvrir le fichier avec ");
        try {
            mContext.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }


}