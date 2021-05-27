package com.example.roomfinder.Views;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.roomfinder.R;
import com.example.roomfinder.Models.serverActions;
import com.example.roomfinder.Presenters.userAdRecyclerAdapter;

import java.util.ArrayList;

public class ProfileFragment extends Fragment {
    SharedPreferences mSettings = MainActivity.mSettings;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_profile, container, false);
        ((TextView) root.findViewById(R.id.user_name)).setText(mSettings.getString("name", ""));
        ((TextView) root.findViewById(R.id.user_surname)).setText(mSettings.getString("surname", ""));
        ((TextView) root.findViewById(R.id.user_phone)).setText("Ваш телефон: " + mSettings.getString("phone", ""));
        ((RecyclerView) root.findViewById(R.id.userAds_list)).setLayoutManager(new LinearLayoutManager(this.getContext()));
        new Thread(new Runnable() {
            @Override
            public void run() {
                final String answ = new String(serverActions.ServerRequest("GetAds"+mSettings.getString("user_id","")));
                final Bitmap image = serverActions.GetImage("GetUserImage" + mSettings.getString("user_id",""));
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        if (!answ.equals("False")){
                            try {
                                ((RecyclerView) root.findViewById(R.id.userAds_list)).setAdapter(new userAdRecyclerAdapter(new ArrayList<>(serverActions.getAds(answ).values())));
                                ((ImageView) root.findViewById(R.id.profile_photo)).setImageBitmap(image);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        ((ImageView) root.findViewById(R.id.profile_photo)).setImageBitmap(image);

                    }
                });
            }
        }).start();

        return root;
    }
}