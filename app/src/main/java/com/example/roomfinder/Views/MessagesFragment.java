package com.example.roomfinder.Views;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.roomfinder.R;
import com.example.roomfinder.Presenters.msgRecyclerAdapter;
import com.example.roomfinder.Models.serverActions;

import java.util.ArrayList;

public class MessagesFragment extends Fragment {
    SharedPreferences mSettings = MainActivity.mSettings;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_messages, container, false);
        ((RecyclerView) root.findViewById(R.id.msgs_list)).setLayoutManager(new LinearLayoutManager(this.getContext()));
        new Thread(new Runnable() {
            @Override
            public void run() {
                final String answ = new String(serverActions.ServerRequest("GetDialogs"+mSettings.getString("user_id","")));
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        if (!answ.equals("False")){
                            try {
                                ((RecyclerView) root.findViewById(R.id.msgs_list)).setAdapter(new msgRecyclerAdapter(new ArrayList<>(serverActions.getDialogs(answ).values())));
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
            }
        }).start();
        return root;
    }
}