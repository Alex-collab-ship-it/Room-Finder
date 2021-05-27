package com.example.roomfinder.Views;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.roomfinder.Models.Advertisement;
import com.example.roomfinder.R;
import com.example.roomfinder.Models.serverActions;

import java.util.HashMap;

public class AddFragment extends Fragment {
    SharedPreferences mSettings = MainActivity.mSettings;
    private String ad_id;

    public AddFragment(String id){
        this.ad_id = id;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_add, container, false);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                final String answ = new String(serverActions.ServerRequest("GetAd"+ad_id+"&" + mSettings.getString("my_location","")));
                final String[] a = answ.split("&");
                final Bitmap img = serverActions.GetImage("GetAdImage" + ad_id);
                final Bitmap author_img = serverActions.GetImage("GetUserImage" + a[1]);
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        ((TextView) root.findViewById(R.id.ad_id)).setText(a[0]);
                        ((ImageView)root.findViewById(R.id.img)).setImageBitmap(img);
                        ((ImageView)root.findViewById(R.id.author_img)).setImageBitmap(author_img);
                        ((TextView) root.findViewById(R.id.address)).setText(a[2]);
                        ((TextView) root.findViewById(R.id.description)).setText(a[3]);
                        Spannable wordtoSpan = new SpannableString(" " + a[4]);
                        ((TextView) root.findViewById(R.id.price)).setText(a[5] + "\t₽");
                        wordtoSpan.setSpan(new ForegroundColorSpan(Color.parseColor("#17a2b8")), 0, wordtoSpan.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        wordtoSpan.setSpan(new StyleSpan(Typeface.BOLD), 0, wordtoSpan.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        ((TextView) root.findViewById(R.id.days)).setText("Максимальное количество суток проживания  -");
                        ((TextView) root.findViewById(R.id.days)).append(wordtoSpan);
                        wordtoSpan = new SpannableString(a[6] + " от вас.");
                        wordtoSpan.setSpan(new StyleSpan(Typeface.BOLD), 0, wordtoSpan.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        ((TextView) root.findViewById(R.id.distance)).setText(wordtoSpan);
                        ((TextView) root.findViewById(R.id.showOnMap)).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                startActivity(new Intent(getContext(),MapActivity.class).putExtra("show_ad_geo",true).putExtra("ad_geo",a[7])
                                        .putExtra("lat",mSettings.getString("my_location","").split(",")[0]).putExtra("lon",mSettings.getString("my_location","").split(",")[1]));
                            }
                        });
                        ((TextView) root.findViewById(R.id.name)).setText(a[8] + " "+ a[9]);
                    }
                });


            }
        });
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return root;
    }

}
