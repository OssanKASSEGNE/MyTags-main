package com.example.mytags;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.android.material.chip.Chip;

import java.util.ArrayList;
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
                        Util.showMessage(mContext,mContext.getString(R.string.no_type));
                    }else{
                        switch(media.getMediaType()){
                            case "image":
                                openImage(Uri.parse(media.getFileUri()),"image/*");
                                break;
                            case "video":
                                openImage(Uri.parse(media.getFileUri()),"video/*");
                                break;
                            default :
                                openFile(Uri.parse(media.getFileUri()));
                                break;
                        }
                    }
            }
        });

        String type = media.getMediaType();

        int color = R.color.beige_pack;
        int icon = R.drawable.ic_search;

        //create media icone
        switch(media.getMediaType()){
            case "image":
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

        holder.chipItem.setChipIconResource(icon);
        holder.chipItem.setChipBackgroundColorResource(color);
        holder.chipItem.setText(media.getTag());

        //Event on Click chipItem
        holder.chipItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Send tag to main activity
                Intent mainActivity = new Intent(mContext, MainActivity.class);
                mainActivity.putExtra("value",media.getTag()+" SEARCH_TAG");
                mContext.startActivity(mainActivity);
            }
        });
        //Event on Click chipdelete
        holder.chipDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //delete this media
                //Send tag to main activity
                Intent mainActivity = new Intent(mContext, MainActivity.class);
                mainActivity.putExtra("value",media.getFileUri()+" DELETE_TAG");
                mContext.startActivity(mainActivity);
            }
        });
        //Event on write ChipEdit
        holder.chipEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog popupDialog = new Dialog(mContext);
                Button okButton;
                Button cancelButton;
                //Dialog to write associate media and Tag

                popupDialog.setContentView(R.layout.add_popup);
                LayoutInflater inflater = LayoutInflater.from(mContext);
                View view = inflater.inflate(R.layout.add_popup, null);
                cancelButton = (Button) popupDialog.findViewById(R.id.cancel_popup_button);
                okButton = (Button) popupDialog.findViewById(R.id.add_image_popup_button);
                EditText editTextTag = (EditText) popupDialog.findViewById(R.id.textTag);

                ImageView image_preview = (ImageView) popupDialog.findViewById(R.id.imagePreview);
                image_preview.setImageURI(Uri.parse(media.getImageUri()));

                //if user clicks on cancel
                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popupDialog.dismiss();
                    }
                });
                //if user click ok => save
                // picture Uri and Tags string
                okButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String currentTag = editTextTag.getText().toString();
                        //Update tag of this media
                        //Send tag to main activity
                        Intent mainActivity = new Intent(mContext, MainActivity.class);
                        mainActivity.putExtra("value",currentTag+" UPDATE_TAG " +media.getFileUri());
                        mContext.startActivity(mainActivity);
                    }
                });
                popupDialog.show();

            }
        });


    }

    @Override
    public int getItemCount() {
        return mdata.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {

        ImageView img;
        Chip chipItem;
        Chip chipDelete;
        Chip chipEdit;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.row_img);
            chipItem = itemView.findViewById(R.id.chip_item);
            chipDelete = itemView.findViewById(R.id.chip_remove);
            chipEdit = itemView.findViewById(R.id.chip_edit);
        }
    }

    //Open Image
    private void openImage(Uri fileUri,String fileType){
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(fileUri,fileType);
        mContext.startActivity(intent);
    }
    //Open AnyFile
    public void openFile(Uri fileUri) {
        String fileType = fileUri.toString();

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        intent.setData(fileUri);
        try {
            mContext.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }


}