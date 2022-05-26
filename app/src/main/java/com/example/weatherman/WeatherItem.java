package com.example.weatherman;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class WeatherItem extends RecyclerView.Adapter<WeatherItem.ViewHolder> {

    Context c;
    ArrayList<String> data_time;
    ArrayList<String> data_temp;
    ArrayList<Integer> iconid;

    public WeatherItem(Context context, ArrayList<String> wttr_time, ArrayList<String> wttr_temp, ArrayList<Integer> icn){
        c = context;
        data_time = wttr_time;
        data_temp = wttr_temp;
        iconid = icn;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(c);
        View view = inflater.inflate(R.layout.rcv_wttr_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.txt_wttr_time.setText(data_time.get(position));
        holder.txt_wttr_temp.setText(data_temp.get(position));
        if(iconid.get(position) <= 9)
            Glide.with(c).load("https://developer.accuweather.com/sites/default/files/0"+ iconid.get(position) +"-s.png").into(holder.icon_wttr);
        else
            Glide.with(c).load("https://developer.accuweather.com/sites/default/files/"+ iconid.get(position) +"-s.png").into(holder.icon_wttr);
    }

    @Override
    public int getItemCount() {
        return data_temp.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView txt_wttr_time, txt_wttr_temp;
        ImageView icon_wttr;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_wttr_temp = itemView.findViewById(R.id.txt_wttr_temp);
            txt_wttr_time = itemView.findViewById(R.id.txt_wttr_time);
            icon_wttr = itemView.findViewById(R.id.icon_wttr);
        }
    }
}
