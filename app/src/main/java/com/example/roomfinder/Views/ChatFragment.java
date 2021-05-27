package com.example.roomfinder.Views;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.roomfinder.Presenters.chatRecyclerAdapter;
import com.example.roomfinder.R;
import com.example.roomfinder.Models.serverActions;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class ChatFragment extends Fragment {
    SharedPreferences mSettings = MainActivity.mSettings;
    private String dialog_id;

    public ChatFragment(String id){
        this.dialog_id = id;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_chat, container, false);
        ((RecyclerView) root.findViewById(R.id.chat_recycler)).setLayoutManager(new LinearLayoutManager(this.getContext()));
        root.findViewById(R.id.send_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMsg(root);
            }
        });
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                final String[] dialog_data = new String(serverActions.ServerRequest("Get_Dialog_info"+dialog_id + "&" + mSettings.getString("user_id",""))).split("&");
                final String answ = new String(serverActions.ServerRequest("GetChat"+dialog_id));
                final Bitmap chat_user_img = serverActions.GetImage("GetUserImage" + dialog_data[1]);
                final Bitmap chat_ad_img = serverActions.GetImage("GetAdImage" + dialog_data[4]);
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void run() {
                        ((ImageView) root.findViewById(R.id.chat_img)).setImageBitmap(chat_user_img);
                        ((ImageView) root.findViewById(R.id.ad_img)).setImageBitmap(chat_ad_img);
                        ((TextView) root.findViewById(R.id.name)).setText(dialog_data[2] + " " + dialog_data[3]);
                        ((TextView) root.findViewById(R.id.ad_info)).setText(dialog_data[5] + ", " +dialog_data[6] + " \t₽");
                        ((TextView) root.findViewById(R.id.dialog_id)).setText(dialog_id);
                        if (!answ.equals("False")) {
                            try {
                                ((RecyclerView) root.findViewById(R.id.chat_recycler)).setAdapter(new chatRecyclerAdapter(new ArrayList<>(serverActions.getMessages(answ).values())));
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }

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
    public void sendMsg(final View v){
        final String msg = ((EditText) v.findViewById(R.id.msg_inpt)).getText().toString();
        if(msg.equals("")){ Toast.makeText(getContext(), "Введите сообщение", Toast.LENGTH_SHORT).show(); return; }
        Date currentDate = new Date();
        DateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        final String timeText = timeFormat.format(currentDate);
        new Thread(new Runnable() {
            @Override
            public void run() {
                serverActions.ServerRequest("SendMsg" + ((TextView) v.findViewById(R.id.dialog_id)).getText().toString()+"&"+mSettings.getString("user_id","") + "&" + msg + "&" + timeText);
                final String answ = new String(serverActions.ServerRequest("GetChat" + ((TextView) v.findViewById(R.id.dialog_id)).getText().toString()));
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        ((EditText) v.findViewById(R.id.msg_inpt)).setText("");
                        try {
                            ((RecyclerView) v.findViewById(R.id.chat_recycler)).setAdapter(new chatRecyclerAdapter(new ArrayList<>(serverActions.getMessages(answ).values())));
                            ((RecyclerView) v.findViewById(R.id.chat_recycler)).scrollToPosition(((RecyclerView) v.findViewById(R.id.chat_recycler)).getAdapter().getItemCount()-1);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }).start();
    }

}
