package com.example.roomfinder.Models;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;

import java.util.HashMap;

public class Dialog {
    public String dialog_id;
    public String chat_user_id;
    public String chat_user_name;
    public String chat_user_surname;
    public String address;
    public String last_msg;
    public Dialog(final HashMap<String,String> dialog){
        this.dialog_id = dialog.get("dialog_id");
        this.chat_user_id = dialog.get("chat_user_id");
        this.chat_user_name = dialog.get("chat_user_name");
        this.chat_user_surname = dialog.get("chat_user_surname");
        this.address = dialog.get("dialog");
        this.last_msg = dialog.get("last_message");

    }
}
