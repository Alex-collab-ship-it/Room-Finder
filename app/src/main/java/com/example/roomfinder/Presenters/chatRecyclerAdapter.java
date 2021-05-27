package com.example.roomfinder.Presenters;

import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.roomfinder.Models.Message;
import com.example.roomfinder.R;
import com.example.roomfinder.Views.MainActivity;

import java.util.ArrayList;


public class chatRecyclerAdapter extends RecyclerView.Adapter<chatRecyclerAdapter.ViewHolder> {
    SharedPreferences mSettings = MainActivity.mSettings;
    private ArrayList<Message> localDataChat;
    //ViewHolder creates View and holds it
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView text;
        private TextView time;
        private ConstraintLayout layout;
        public ViewHolder(View view){
            super(view);
            text = view.findViewById(R.id.message);
            time = view.findViewById(R.id.msg_time);
            layout = view.findViewById(R.id.mylayout);
        }
    }

    public chatRecyclerAdapter(ArrayList<Message> dataAd){localDataChat = dataAd;}

    @NonNull
    @Override
    public chatRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message,parent,false);
        return new chatRecyclerAdapter.ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull chatRecyclerAdapter.ViewHolder holder, int position) {
        if(localDataChat.get(position).user_id.equals(mSettings.getString("user_id",""))) {
            holder.layout.setBackgroundResource(R.drawable.my_message_rounded);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) holder.layout.getLayoutParams();
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            holder.layout.setLayoutParams(params);
        }
        holder.text.setText(localDataChat.get(position).text);
        String time = localDataChat.get(position).msg_time.split(" ")[1].substring(4,5).equals(":") ? localDataChat.get(position).msg_time.split(" ")[1].substring(0,4)
                : localDataChat.get(position).msg_time.split(" ")[1].substring(0,5);
        holder.time.setText(time);
    }

    @Override
    public int getItemCount() { return localDataChat.size(); }
}
