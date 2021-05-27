package com.example.roomfinder.Presenters;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.roomfinder.Models.Advertisement;
import com.example.roomfinder.Models.serverActions;
import com.example.roomfinder.R;

import java.util.ArrayList;


public class adRecyclerAdapter extends RecyclerView.Adapter<adRecyclerAdapter.ViewHolder> {
    private ArrayList<Advertisement> localDataAd;
    //ViewHolder creates View and holds it
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView address;
        private TextView price;
        private TextView distance;
        private TextView name;
        private TextView id;
        private ImageView img;
        public ViewHolder(View view){
            super(view);
            address = view.findViewById(R.id.recyclerAddress);
            price = view.findViewById(R.id.recyclerPrice);
            distance = view.findViewById(R.id.recyclerDistance);
            name = view.findViewById(R.id.recyclerAuthor_name);
            id = view.findViewById(R.id.recyclerAd_id);
            img = view.findViewById(R.id.recyclerImg);
        }
    }

    public adRecyclerAdapter(ArrayList<Advertisement> dataAd){localDataAd = dataAd; notifyDataSetChanged();}

    @NonNull
    @Override
    public adRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ad_item,parent,false);
        return new ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull final adRecyclerAdapter.ViewHolder holder, final int position) {
        holder.address.setText(localDataAd.get(position).address);
        holder.price.setText(localDataAd.get(position).price+"\t₽");
        holder.distance.setText(localDataAd.get(position).distance);
        holder.name.setText(localDataAd.get(position).name+",");
        holder.id.setText(localDataAd.get(position).id);
        new Thread(new Runnable() {
            @Override
            public void run() {
                final Bitmap image = serverActions.GetImage("GetAdImage" + localDataAd.get(position).id);
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        holder.img.setImageBitmap(image);
                    }
                });
            }
        }).start();
    }

    @Override
    public int getItemCount() { return localDataAd.size(); }
}
