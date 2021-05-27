package com.example.roomfinder.Views;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.roomfinder.R;
import com.example.roomfinder.Models.Validations;
import com.example.roomfinder.Models.serverActions;

public class Authorisation extends Activity {
    public static final String APP_PREFERENCES = "mysettings";
    SharedPreferences mSettings;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSettings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        editor = mSettings.edit();
        setContentView(R.layout.authorization);

    }

    public void authorisation(final View view){
        if (mSettings.getString("server_ip","").equals("")){ Toast.makeText(Authorisation.this, "Введите IP сервера", Toast.LENGTH_SHORT).show(); return; }
        new Thread(new Runnable() {
            @Override
            public void run() {
                final String answ = new String(serverActions.ServerRequest("Autor1sat1on"+((EditText) findViewById(R.id.log_inpt)).getText().toString()+"&"+((EditText) findViewById(R.id.pas_inpt)).getText().toString()));
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (!answ.equals("")) {
                            String[] res = answ.split("&");
                            editor.putString("user_id", res[0]);
                            editor.putString("login",res[1]);
                            editor.putString("name",res[3]);
                            editor.putString("surname",res[4]);
                            editor.putString("phone",res[5]);
                            byte[] bytesImage = res[6].getBytes();
                            BitmapFactory.decodeByteArray(bytesImage, 0, bytesImage.length);
                            editor.putString("profile_img", res[6]);
                            editor.commit();
                            finish();
                        }
                        else{
                            Toast.makeText(Authorisation.this, "Неверный логин или пароль", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }).start();
    }

    // ============================== регистрация ===========================================
    public void registration(View view){
        if (mSettings.getString("server_ip","").equals("")){ Toast.makeText(Authorisation.this, "Введите IP сервера", Toast.LENGTH_SHORT).show(); return; }
        String validation_result = Validations.regValidation(((EditText)findViewById(R.id.login_input)).getText().toString(),
                ((EditText)findViewById(R.id.pas1_inpt)).getText().toString(),((EditText)findViewById(R.id.pas2_inpt)).getText().toString(),((EditText)findViewById(R.id.name_inpt)).getText().toString(),
                ((EditText)findViewById(R.id.surname_inpt)).getText().toString(),((EditText)findViewById(R.id.phone_inpt)).getText().toString());
        if (validation_result.equals("")){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    final String answ = new String(serverActions.ServerRequest("Reg1strat1on"+((EditText)findViewById(R.id.login_input)).getText().toString()+"&"+
                            ((EditText)findViewById(R.id.pas1_inpt)).getText().toString()+"&"+((EditText)findViewById(R.id.name_inpt)).getText().toString()+"&"+
                            ((EditText)findViewById(R.id.surname_inpt)).getText().toString()+"&"+((EditText)findViewById(R.id.phone_inpt)).getText().toString()));
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (!answ.equals("")) {
                                String[] res = answ.split("&");
                                editor.putString("user_id", res[0]);
                                editor.putString("login",res[1]);
                                editor.putString("name",res[3]);
                                editor.putString("surname",res[4]);
                                editor.putString("phone",res[5]);
                                editor.commit();
                                finish();
                            }
                            else{
                                Toast.makeText(Authorisation.this, "Пользователь с таким логином уже зарегистрирован", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }).start();
        }
        else{
            new Toast(this).makeText(this,validation_result, Toast.LENGTH_LONG).show();
        }
    }
    public void toRegPage(View view) { setContentView(R.layout.registration); }
    public void toLogPage(View view) { setContentView(R.layout.authorization); }

    public void saveIP(View view){
        if (((EditText) findViewById(R.id.ip_inpt)).getText().toString().equals("")){ Toast.makeText(Authorisation.this, "Некорректный IP сервера", Toast.LENGTH_SHORT).show(); return; }
        editor.putString("server_ip",((EditText)findViewById(R.id.ip_inpt)).getText().toString());
        editor.commit();
    }
}
