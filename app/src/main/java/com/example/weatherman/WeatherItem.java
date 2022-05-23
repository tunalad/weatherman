package com.example.weatherman;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class WeatherItem extends RecyclerView.Adapter<WeatherItem.ViewHolder> {

    Context c;
    ArrayList<String> data_time;
    ArrayList<String> data_temp;

    public WeatherItem(Context context, ArrayList<String> wttr_time, ArrayList<String> wttr_temp){
        c = context;
        data_time = wttr_time;
        data_temp = wttr_temp;
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
    }

    @Override
    public int getItemCount() {
        return data_temp.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView txt_wttr_time, txt_wttr_temp;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_wttr_temp = itemView.findViewById(R.id.txt_wttr_temp);
            txt_wttr_time = itemView.findViewById(R.id.txt_wttr_time);
        }
    }
}
