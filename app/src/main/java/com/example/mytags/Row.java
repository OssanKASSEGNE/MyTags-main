package com.example.mytags;

import android.net.Uri;

public class Row {

    private Uri img;

    public Row(Uri img) {
        this.img = img;
    }

    public Uri getImg() {
        return img;
    }

    public void setImg(Uri img) {
        this.img = img;
    }
}
