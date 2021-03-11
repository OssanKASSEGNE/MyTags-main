package com.example.mytags;

import android.net.Uri;

public class Media {
    private int id;
    private String uri;
    private String mediaType;
    private String tag;

    public Media(int id ,String uri, String tag){
        this.id = id;
        this.uri = uri;
        this.mediaType = mediaType(uri);
        this.tag = tag;
    }
    public Media(int id ,String uri,String mediaType, String tag){
        this.id = id;
        this.uri = uri;
        this.mediaType = mediaType;
        this.mediaType = mediaType(uri);
        this.tag = tag;
    }

    //Get the media type
    private String mediaType (String uri){
        String type = uri.toString().substring(uri.toString().length() - 3);
        return type;
    }

    public String getMediaType() {
        return mediaType;
    }

    public String getTag() {
        return tag;
    }

    public String getUri() {
        return uri;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public void setUri(String uri) {
        this.uri = uri;
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
                "uri='" + uri + '\'' +
                ", mediaType='" + mediaType + '\'' +
                ", tag='" + tag + '\'' +
                '}';
    }
}
