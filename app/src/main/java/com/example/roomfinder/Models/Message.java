package com.example.roomfinder.Models;


import java.util.HashMap;

public class Message {
    public String message_id;
    public String user_id;
    public String text;
    public String msg_time;
    public Message(HashMap<String, String> result){
        this.message_id = result.get("message_id");
        this.user_id = result.get("user_id");
        this.text = result.get("text");
        this.msg_time = result.get("time");
    }
}