package com.example.roomfinder.Presenters;

import android.content.SharedPreferences;
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

import com.example.roomfinder.Models.Dialog;
import com.example.roomfinder.Models.serverActions;
import com.example.roomfinder.R;
import com.example.roomfinder.Views.MainActivity;

import java.util.ArrayList;

public class msgRecyclerAdapter extends RecyclerView.Adapter<msgRecyclerAdapter.ViewHolder> {
    SharedPreferences mSettings = MainActivity.mSettings;
    private ArrayList<Dialog> localDataMsg;
    //ViewHolder creates View and holds it
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView name;
        private TextView surname;
        private TextView address;
        private TextView last_msg;
        private TextView dialog_id;
        private ImageView img;
        public ViewHolder(View view){
            super(view);
            dialog_id = view.findViewById(R.id.dialog_id);
            name = view.findViewById(R.id.name);
            surname = view.findViewById(R.id.surname);
            address = view.findViewById(R.id.address);
            last_msg = view.findViewById(R.id.last_msg);
            img = view.findViewById(R.id.recyclerImg);
        }
    }

    public msgRecyclerAdapter(ArrayList<Dialog> dataMsg){localDataMsg = dataMsg; notifyDataSetChanged();}

    @NonNull
    @Override
    public msgRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.msg_item,parent,false);
        return new msgRecyclerAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final msgRecyclerAdapter.ViewHolder holder, final int position) {
        holder.dialog_id.setText(localDataMsg.get(position).dialog_id);
        holder.name.setText(localDataMsg.get(position).chat_user_name);
        holder.surname.setText(localDataMsg.get(position).chat_user_surname);
        holder.address.setText(localDataMsg.get(position).address);
        holder.last_msg.setText(localDataMsg.get(position).last_msg);
        new Thread(new Runnable() {
            @Override
            public void run() {
                final Bitmap image = serverActions.GetImage("GetUserImage" + localDataMsg.get(position).chat_user_id);
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
    public int getItemCount() {
        return localDataMsg.size();
    }
}
