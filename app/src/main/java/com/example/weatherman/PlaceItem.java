package com.example.weatherman;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class PlaceItem extends RecyclerView.Adapter<PlaceItem.PlaceItemHolder> {
    Context c;
    ArrayList<String> place_name = new ArrayList<>();
    DatabaseManager dbManager;

    public PlaceItem(Context context, ArrayList<String> place_name){
        c = context;
        this.place_name = place_name;
    }

    @NonNull
    @Override
    public PlaceItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(c);
        View view = inflater.inflate(R.layout.rcv_place_item, parent, false);
        return new PlaceItemHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaceItemHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.txt_place_name.setText(String.valueOf(place_name.get(position)));
        holder.txt_place_index.setText(String.valueOf(position+1));
        holder.btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(c, "Deleted that b", Toast.LENGTH_SHORT).show();
            }
        });
        holder.btn_favourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(c, "Pinned that b", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return place_name.size();
    }

    public class PlaceItemHolder extends RecyclerView.ViewHolder{
        TextView txt_place_name, txt_place_index;
        ImageView btn_delete, btn_favourite;
        public PlaceItemHolder(@NonNull View itemView) {
            super(itemView);
            txt_place_index = itemView.findViewById(R.id.txt_place_index);
            txt_place_name = itemView.findViewById(R.id.txt_place_name);
            btn_delete = itemView.findViewById(R.id.icon_delete);
            btn_favourite = itemView.findViewById(R.id.icon_favourite);
        }
    }
}
