package com.example.mytags;

import android.net.Uri;

public class Media {
    private int id;
    private String imageUri;
    private String fileUri;
    private String mediaType;
    private String tag;


    public Media(int id ,String imageUri,String fileUri,String mediaType, String tag){
        this.id = id;
        this.fileUri = fileUri;
        this.imageUri = imageUri;
        this.mediaType = mediaType;
        this.tag = tag;
    }

    public String getMediaType() {
        return mediaType;
    }

    public String getTag() {
        return tag;
    }

    public String getImageUri() {
        return imageUri;
    }

    public String getFileUri() {
        return fileUri;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public void setFileUri(String fileUri) {
        this.fileUri = fileUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public int getId() {
        return id;
    }


    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Media{" +
                "id=" + id +
                ", imageUri='" + imageUri + '\'' +
                ", fileUri='" + fileUri + '\'' +
                ", mediaType='" + mediaType + '\'' +
                ", tag='" + tag + '\'' +
                '}';
    }
}
