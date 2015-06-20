package com.ivaron.battlerank.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ivaron.battlerank.R;
import com.ivaron.battlerank.models.Battle;

import java.util.ArrayList;

/**
 * Created by Aaron on 19-6-2015.
 */
public class BattleListAdapter extends BaseAdapter {
    private ArrayList<Battle> items;
    private LayoutInflater inflater;
    public BattleListAdapter(Context context, ArrayList<Battle>data){
        items = data;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return items.get(position).id;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.listitem_battlelist, null);
            holder = new ViewHolder();
            holder.title = (TextView) convertView.findViewById(R.id.title);
            holder.subTitle = (TextView) convertView.findViewById(R.id.subtitle);
            holder.holder = (RelativeLayout) convertView.findViewById(R.id.holder);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.title.setText(items.get(position).name);
        holder.subTitle.setText(parent.getResources().getString(R.string.battlelist_subtitle_prefix)+ items.get(position).amountOfImages);
        if(items.get(position).completed){
            holder.holder.setBackgroundColor(parent.getResources().getColor(R.color.green));
        }else{
            holder.holder.setBackgroundColor(parent.getResources().getColor(R.color.white));
        }
        return convertView;
    }

    private class ViewHolder{
        TextView title;
        TextView subTitle;
        RelativeLayout holder;
    }
}
