package com.example.weatherman;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.sql.SQLDataException;
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
                dbManager = new DatabaseManager(c);
                try {dbManager.open();} catch (SQLDataException e) {e.printStackTrace();}
                Log.d("onClick delete: ", place_name.get(position));
                dbManager.delete(place_name.get(position));
                Toast.makeText(c, place_name.get(position) + " deleted", Toast.LENGTH_SHORT).show();
            }
        });
        /*holder.btn_favourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.esp.putString(c.getString(R.string.main_place),place_name.get(position));
                holder.esp.commit();
                Toast.makeText(c, place_name.get(position) + " pinned", Toast.LENGTH_SHORT).show();
            }
        });*/
    }

    @Override
    public int getItemCount() {
        return place_name.size();
    }

    public class PlaceItemHolder extends RecyclerView.ViewHolder{
        //SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(c);
        //SharedPreferences.Editor esp = sp.edit();


        TextView txt_place_name, txt_place_index;
        ImageView btn_delete, btn_favourite;
        public PlaceItemHolder(@NonNull View itemView) {
            super(itemView);
            txt_place_index = itemView.findViewById(R.id.txt_place_index);
            txt_place_name = itemView.findViewById(R.id.txt_place_name);
            btn_delete = itemView.findViewById(R.id.icon_delete);
            //btn_favourite = itemView.findViewById(R.id.icon_favourite);
        }
    }
}
