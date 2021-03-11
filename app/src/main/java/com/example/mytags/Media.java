package com.example.mytags;

import android.net.Uri;

public class Media {
    private String uri;
    private String mediaType;
    private String tag;

    public Media(String uri, String tag){
        this.uri = uri;
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

    @Override
    public String toString() {
        return "Media{" +
                "uri='" + uri + '\'' +
                ", mediaType='" + mediaType + '\'' +
                ", tag='" + tag + '\'' +
                '}';
    }
}
