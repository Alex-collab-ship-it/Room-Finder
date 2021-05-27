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
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.roomfinder.R;
import com.example.roomfinder.Presenters.adRecyclerAdapter;
import com.example.roomfinder.Models.serverActions;

import java.util.ArrayList;


public class AddsFragment extends Fragment {
    SharedPreferences mSettings = MainActivity.mSettings;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View root = inflater.inflate(R.layout.fragment_adds, container, false);
        ((RecyclerView) root.findViewById(R.id.ads_list)).setLayoutManager(new LinearLayoutManager(this.getContext()));
        ((SeekBar) root.findViewById(R.id.distanceSettings)).setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                String myprogress = seekBar.getProgress() + "";
                if (seekBar.getProgress()>30 && seekBar.getProgress()<=37){
                    myprogress = seekBar.getProgress()*10 + "";
                }
                else if (seekBar.getProgress()>37){
                    myprogress = seekBar.getProgress()*1000 +"";
                }
                ((TextView) root.findViewById(R.id.progress)).setText("Показывать объявления в радиусе: " + myprogress + " км");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                String myprogress = seekBar.getProgress() + "";
                if (seekBar.getProgress()>30 && seekBar.getProgress()<=37){
                    myprogress = seekBar.getProgress()*10 + "";
                }
                else if (seekBar.getProgress()>37){
                    myprogress = seekBar.getProgress()*1000 +"";
                }
                ((TextView) root.findViewById(R.id.progress)).setText("Показывать объявления в радиусе: " + myprogress + " км");
                final String finalMyprogress = myprogress;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while(mSettings.getString("my_location","").equals(""));
                        final String answ = new String(serverActions.ServerRequest("GetAds%"+mSettings.getString("my_location","")+"&"+ finalMyprogress + "&" + mSettings.getString("user_id","")));
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                if (answ.equals("False")){
                                    ((RecyclerView) root.findViewById(R.id.ads_list)).removeAllViewsInLayout();
                                    Toast.makeText(getContext(), "Объявлений не найдено",Toast.LENGTH_SHORT).show();
                                }
                                else{
                                    try {
                                        ((RecyclerView) root.findViewById(R.id.ads_list)).setAdapter(new adRecyclerAdapter(new ArrayList<>(serverActions.getAds(answ).values())));
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        });
                    }
                }).start();
            }
        });
        if (!MainActivity.isLocationEnabled(getContext())){
            ((TextView) root.findViewById(R.id.alert)).setText("Включите геолокацию");
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(mSettings.getString("my_location","").equals(""));
                final String answ = new String(serverActions.ServerRequest("GetAds%"+mSettings.getString("my_location","") +
                        "&" + ((SeekBar) root.findViewById(R.id.distanceSettings)).getProgress() + "&" + mSettings.getString("user_id","")));
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        ((TextView) root.findViewById(R.id.alert)).setText("Объявления");
                        if (answ.equals("False")){
                            Toast.makeText(getContext(), "Объявлений не найдено",Toast.LENGTH_SHORT).show();
                        }
                        else{
                            try {
                                ((RecyclerView) root.findViewById(R.id.ads_list)).setAdapter(new adRecyclerAdapter(new ArrayList<>(serverActions.getAds(answ).values())));
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            };

                        }

                    }
                });
            }
        }).start();

        return root;
    }
}