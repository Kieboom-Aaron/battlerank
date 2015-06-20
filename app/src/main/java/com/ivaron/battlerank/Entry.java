package com.ivaron.battlerank;

import android.graphics.Bitmap;

/**
 * Created by Aaron on 19-6-2015.
 */
public class Entry {
    public String title;
    public String path;
    public Bitmap image;
    public int lossCount;
    public int id;
    public Entry(){
        lossCount = 0;
    }
}
