package com.example.mytags;

public class TagElement {

    private String sTag;
    private int countPhoto;
    private int countVideo;
    private int countAudio;
    private int countFile;

    public TagElement(String sTag, int countPhoto, int countVideo, int countAudio, int countFile) {
        this.sTag = sTag;
        this.countPhoto = countPhoto;
        this.countVideo = countVideo;
        this.countAudio = countAudio;
        this.countFile = countFile;
    }

    public int getCountAudio() {
        return countAudio;
    }

    public int getCountFile() {
        return countFile;
    }

    public int getCountPhoto() {
        return countPhoto;
    }

    public int getCountVideo() {
        return countVideo;
    }

    public String getTag() {
        return sTag;
    }

    public void setCountAudio(int countAudio) {
        this.countAudio = countAudio;
    }

    public void setCountFile(int countFile) {
        this.countFile = countFile;
    }

    public void setCountPhoto(int countPhoto) {
        this.countPhoto = countPhoto;
    }

    public void setCountVideo(int countVideo) {
        this.countVideo = countVideo;
    }

    public void setsTag(String sTag) {
        this.sTag = sTag;
    }
}
