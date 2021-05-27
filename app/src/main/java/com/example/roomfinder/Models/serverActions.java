package com.example.roomfinder.Models;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.util.Base64;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.example.roomfinder.Models.Advertisement;
import com.example.roomfinder.R;
import com.example.roomfinder.Views.MainActivity;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class serverActions {
    static SharedPreferences mSettings = MainActivity.mSettings;
    // "192.168.1.66"

    public static byte[] ServerRequest(String request) {
        try {
            Socket socket = new Socket(mSettings.getString("server_ip",""), 1234);
            DataOutputStream outStream = new DataOutputStream(socket.getOutputStream());
            outStream.writeUTF(request);
            outStream.flush(); //синхронизация
            byte[] resultBuff = new byte[0];
            byte[] buff = new byte[1024];
            int k = -1;
            while((k = socket.getInputStream().read(buff, 0, buff.length)) > -1) {
                byte[] tbuff = new byte[resultBuff.length + k]; // temp buffer size = bytes already read + bytes last read
                System.arraycopy(resultBuff, 0, tbuff, 0, resultBuff.length); // copy previous bytes
                System.arraycopy(buff, 0, tbuff, resultBuff.length, k);  // copy current lot
                resultBuff = tbuff; // call the temp buffer as your result buff
            }
            socket.close();
            return resultBuff;
        } catch (Exception ex) {
            return null;
        }
    }



    public static HashMap<Integer, Advertisement> getAds(String answ) throws InterruptedException {
        // получение инфы об объявлениях из бд
        // заполнение ифной из бд
        //заполнение
        HashMap<Integer, Advertisement> ad_Data = new HashMap<>();
        ArrayList<ArrayList<String>> res = new ArrayList<>();
        String[] ads = answ.split("%");
        for (int i = 0; i<ads.length; i++){ // заполнение ответом с серевера
            ArrayList<String> ad = new ArrayList<>(Arrays.asList(ads[i].split("&")));
            res.add(ad);
        }
        for (int i = 0; i<res.size(); i++) { // заполнение hashmap
            ArrayList<String> ad = res.get(i);
            HashMap<String,String> localData = new HashMap<>();
            localData.put("ad_id", ad.get(0));
            localData.put("author_id", ad.get(1));
            localData.put("address", ad.get(2));
            localData.put("description",ad.get(3));
            localData.put("days",ad.get(4));
            localData.put("price" , ad.get(5));
            localData.put("distance", ad.get(6));
            localData.put("name" , ad.get(7));
            localData.put("surname" , ad.get(8));
            Advertisement localAd = new Advertisement(localData) ;
            ad_Data.put(Integer.parseInt(ad.get(0)), localAd);
        }
        return ad_Data;
    }

    public static HashMap<Integer, Dialog> getDialogs(String answ) throws InterruptedException {
        // заполнение ифной из бд
        HashMap<Integer, Dialog> dialog_Data = new HashMap<>();
        ArrayList<ArrayList<String>> res = new ArrayList<>();
        String[] dialogs = answ.split("%");
        for (int i = 0; i<dialogs.length; i++){ // заполнение ответом с серевера
            ArrayList<String> dialog = new ArrayList<>(Arrays.asList(dialogs[i].split("&")));
            res.add(dialog);
        }
        for (int i = 0; i<res.size(); i++) { // заполнение hashmap
            ArrayList<String> dialog = res.get(i);
            HashMap<String,String> localData = new HashMap<>();
            localData.put("dialog_id", dialog.get(0));
            localData.put("chat_user_id", dialog.get(1));
            localData.put("chat_user_name", dialog.get(2));
            localData.put("chat_user_surname",dialog.get(3));
            localData.put("last_message",dialog.get(4));
            Dialog localdialog = new Dialog(localData) ;
            dialog_Data.put(Integer.parseInt(dialog.get(0)), localdialog);
        }
        return dialog_Data;
    }

    public static HashMap<Integer, Message> getMessages(String answ) throws InterruptedException {
        HashMap<Integer, Message> chat_Data = new HashMap<>();
        ArrayList<ArrayList<String>> res = new ArrayList<>();
        String[] messages = answ.split("%");
        for (int i = 0; i<messages.length; i++){ // заполнение ответом с серевера
            ArrayList<String> message = new ArrayList<>(Arrays.asList(messages[i].split("&")));
            res.add(message);
        }
        for (int i = 0; i<res.size(); i++) { // заполнение hashmap
            ArrayList<String> message = res.get(i);
            HashMap<String,String> localData = new HashMap<>();
            localData.put("message_id",message.get(0));
            localData.put("user_id", message.get(1));
            localData.put("text", message.get(2));
            localData.put("time", message.get(3));
            Message localMessage = new Message(localData) ;
            chat_Data.put(i, localMessage);
        }
        return chat_Data;
    }


    public static Bitmap GetImage(final String req) {
        final byte[] bytesImage = ServerRequest(req);
        return BitmapFactory.decodeByteArray(bytesImage, 0, bytesImage.length);
    }

    public static String BitmapToString(Bitmap bmp){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream .toByteArray();
        String image_byte = String.valueOf(byteArray);
        return image_byte;
    }

}
