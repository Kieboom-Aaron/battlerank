package com.ivaron.battlerank;

import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class BattleGame {

    public String name;
    public int id;
    public ArrayList<Entry> entries;
    private TextView title1;
    private TextView title2;
    private ImageView image1;
    private ImageView image2;

    private int zeroLost, oneLost, twoLost;
    private Entry[] activeEntries;
    private Drawable finalImage;
    public BattleGame(String json, Drawable image){
        finalImage = image;
        entries = new ArrayList<Entry>();
        activeEntries = new Entry[2];
        oneLost = 0;
        twoLost = 0;
        try{
            JSONObject battle = new JSONObject(json);
            id = battle.getInt("id");
            name = battle.getString("name");
            JSONArray jsonEntries = battle.getJSONArray("enteries");
            for(int c = 0; c < jsonEntries.length(); c++){
                JSONObject jsonEntry = jsonEntries.getJSONObject(c);
                Entry e = new Entry();
                e.id = jsonEntry.getInt("id");
                e.title = jsonEntry.getString("title");
                e.path = jsonEntry.getString("image");
                entries.add(e);
            }
            zeroLost = entries.size();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void startBattle(LinearLayout set1, LinearLayout set2){
        title1 = (TextView)set1.findViewById(R.id.title1);
        title2 = (TextView)set2.findViewById(R.id.title2);
        image1 = (ImageView)set1.findViewById(R.id.image1);
        image2 = (ImageView)set2.findViewById(R.id.image2);
        View.OnClickListener listener1 = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickEntry(0);
            }
        };
        View.OnClickListener listener2 = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickEntry(1);
            }
        };
        set1.setOnClickListener(listener1);
        set2.setOnClickListener(listener2);
        nextEntries();
    }
    //start game logic
    private void loadImage(Entry entry, ImageView imageView){
        if(entry.image == null){
            DownloadImageTask task = new DownloadImageTask();
            task.execute(entry, imageView);
        } else{
            imageView.setImageBitmap(entry.image);
        }
    }

    private void pickEntry(int pair){
        if(pair == 0) {
            addLoss(activeEntries[1]);
        }else{
            addLoss(activeEntries[0]);
        }
        nextEntries();
    }

    private void addLoss(Entry e){
        switch (e.lossCount){
            case 0:
                zeroLost--;
                oneLost++;
                break;
            case 1:
                oneLost--;
                twoLost++;
                break;
            case 2:
                twoLost--;
                break;
        }
        e.lossCount++;
    }

    private void nextEntries(){
        activeEntries[0] = null;
        if(zeroLost > 1){
            activeEntries = getEntries(0);
        }else if(oneLost > 1){
            activeEntries = getEntries(1);
        }else if(twoLost > 1){
            activeEntries = getEntries(2);
        }else{
            Collections.sort(entries, new Comparator<Entry>(){
                @Override
                public int compare(Entry o1, Entry o2) {
                    return o1.lossCount - o2.lossCount;
                }
            });
            PostResultTask prt = new PostResultTask();
            prt.execute();
            title1.setText("Resultaten zijn opgeslagen");
            title2.setText("Resultaten zijn opgeslagen");
            image1.setImageDrawable(finalImage);
            image2.setImageDrawable(finalImage);
        }
        if(activeEntries[0] != null && !activeEntries[0].equals(null)) {
            title1.setText(activeEntries[0].title);
            title2.setText(activeEntries[1].title);
            loadImage(activeEntries[0], image1);
            loadImage(activeEntries[1], image2);
        }
    }

    private Entry[] getEntries(int lossCount){
        Entry[] temp = new Entry[2];
        int index = 0;

        for(int i = (entries.size()-1) ; i>= 0 ; i--){
            if(lossCount == entries.get(i).lossCount){
                temp[index] = entries.get(i);
                index++;
            }
            if(index == 2){
                return temp;
            }
        }
        return temp;
    }

    private class PostResultTask extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... params) {
            int responseCode = -1;
            JSONObject jsonResponse = null;
            StringBuilder builder = new StringBuilder();
            HttpClient client = new DefaultHttpClient();
            HttpPost postRequest = new HttpPost("http://battlerank.herokuapp.com/rankings/"+id);
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
            nameValuePairs.add(new BasicNameValuePair("first", entries.get(0).id+""));
            nameValuePairs.add(new BasicNameValuePair("second", entries.get(1).id+""));
            if(entries.get(2) != null && !entries.get(2).equals(null)){
                nameValuePairs.add(new BasicNameValuePair("third", entries.get(2).id+""));
            }
            try {
                postRequest.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                postRequest.addHeader("x-token", "androidapp");
                HttpResponse response = client.execute(postRequest);
                StatusLine statusLine = response.getStatusLine();
                responseCode = statusLine.getStatusCode();
                if(responseCode == HttpURLConnection.HTTP_OK){
                    HttpEntity entity = response.getEntity();
                    InputStream content = entity.getContent();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(content));
                    String line;
                    while((line = reader.readLine()) != null){
                        builder.append(line);
                    }
                    String done = builder.toString();
                    jsonResponse = new JSONObject(done);
                    CompletedBattles.getInstance().addCompletedBattle(id);
                    return null;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
