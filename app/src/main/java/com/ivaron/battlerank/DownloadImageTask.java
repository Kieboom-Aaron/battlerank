package com.ivaron.battlerank;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by Aaron on 20-6-2015.
 */
public class DownloadImageTask extends AsyncTask<Object, Void, Bitmap> {
    ImageView iv;
    Entry e;
    @Override
    protected Bitmap doInBackground(Object... objects) {
        iv = (ImageView)objects[1];
        e = (Entry)objects[0];
        return download_Image(e.path);
    }

    @Override
    protected void onPostExecute(Bitmap result) {
        if(result != null){
            e.image = result;
            iv.setImageBitmap(result);
        }
    }


    private Bitmap download_Image(String url) {
        //---------------------------------------------------
        Bitmap bm = null;
        if(url == null || url.equals(null)){
            return null;
        }else {
            try {
                URL aURL = new URL(url);
                URLConnection conn = aURL.openConnection();
                conn.connect();
                InputStream is = conn.getInputStream();
                BufferedInputStream bis = new BufferedInputStream(is);
                bm = BitmapFactory.decodeStream(bis);
                bis.close();
                is.close();
            } catch (IOException e) {
                Log.e("Hub", "Error getting the image from server : " + e.getMessage().toString());
                return null;
            }
            return bm;
        }
    }
}
