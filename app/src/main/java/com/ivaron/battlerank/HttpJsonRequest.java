package com.ivaron.battlerank;

import android.os.AsyncTask;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

/**
 * Created by Aaron on 25-5-2015.
 */
public abstract class HttpJsonRequest extends AsyncTask<String, Void, String> {

    private HttpGet httpGet;
    protected int responseCode;
    @Override
    protected String doInBackground(String[] params) {
        StringBuilder builder = new StringBuilder();
        responseCode = -1;
        JSONObject jsonResponse = null;
        HttpClient client = new DefaultHttpClient();
        httpGet = new HttpGet("https://battlerank.herokuapp.com"+params[0]);
        try {
            HttpResponse response = client.execute(httpGet);
            StatusLine statusLine = response.getStatusLine();
            responseCode = statusLine.getStatusCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                HttpEntity entity = response.getEntity();
                InputStream content = entity.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(content));
                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }
                String done = builder.toString();
                return done;
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onCancelled(){
        if(httpGet != null){
            httpGet.abort();
        }
    }

    protected abstract void onPostExecute(String result);


}
