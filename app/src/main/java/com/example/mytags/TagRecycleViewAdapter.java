package com.example.mytags;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;

import java.util.List;

public class TagRecycleViewAdapter extends RecyclerView.Adapter<TagRecycleViewAdapter.TagViewHolder> {

    Context mContext;
    List<TagElement> mData;

    public TagRecycleViewAdapter(Context mContext, List<TagElement> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }


    @NonNull
    @Override
    public TagViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.list_layout, parent, false);
        return new TagRecycleViewAdapter.TagViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TagViewHolder holder, int position) {
        holder.tag.setText(mData.get(position).getTag());
        holder.nbPhoto.setText(String.valueOf(mData.get(position).getCountPhoto()));
        holder.nbVideo.setText(String.valueOf(mData.get(position).getCountVideo()));
        holder.nbAudio.setText(String.valueOf(mData.get(position).getCountAudio()));
        holder.nbFile.setText(String.valueOf(mData.get(position).getCountFile()));
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class TagViewHolder extends RecyclerView.ViewHolder {
        Chip tag;
        TextView nbPhoto;
        TextView nbVideo;
        TextView nbAudio;
        TextView nbFile;

        public TagViewHolder(@NonNull View itemView) {
            super(itemView);
            tag = (Chip) itemView.findViewById(R.id.chip_tag);
            nbPhoto = (TextView) itemView.findViewById(R.id.nb_photo);
            nbVideo = (TextView) itemView.findViewById(R.id.nb_video);
            nbAudio = (TextView) itemView.findViewById(R.id.nb_audio);
            nbFile = (TextView) itemView.findViewById(R.id.nb_file);
        }
    }
}
