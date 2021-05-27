package com.example.roomfinder.Views;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;

import com.example.roomfinder.R;
import com.example.roomfinder.Models.Validations;
import com.example.roomfinder.Models.serverActions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;


import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    static final int GALLERY_REQUEST_PROFILE = 1;
    static final int GALLERY_REQUEST_AD= 2;
    static final int MAP_REQUEST= 23;
    AlertDialog dialog;
    private boolean isAdPhotoDownloaded = false;
    private boolean is1stUser = true;
    private String coord = "";
    private LocationManager lm;
    public static SharedPreferences mSettings;
    public static SharedPreferences.Editor editor;


    private final LocationListener locationListener = new LocationListener() {

        @Override
        public void onLocationChanged(final Location location) {
            coord = new LatLng(location.getLatitude(), location.getLongitude()).toString().replace("lat/lng: (", "").replace(")", "");
            editor = mSettings.edit();
            editor.putString("my_location",coord);
            editor.commit();

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSettings = getSharedPreferences("mysettings", Context.MODE_PRIVATE);
        editor = mSettings.edit();
        // Toast.makeText(MainActivity.this, mSettings.getString("my_location", ""), Toast.LENGTH_SHORT).show();
        if (!isLocationEnabled(this)){ Toast.makeText(this, "Включите геопозицию", Toast.LENGTH_SHORT).show(); }
        if (mSettings.getString("user_id", "").equals("")) {
            startActivityForResult(new Intent(MainActivity.this,
                    Authorisation.class),200);
        }
        setContentView(R.layout.activity_main);
        NavigationUI.setupWithNavController((BottomNavigationView) findViewById(R.id.bottomNavigationView), Navigation.findNavController(this, R.id.fragment));
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment, new AddsFragment()).commit();
        askForLocationPermission();
        requestLocation();
    }

    //package com.example.roomfinder;


    public void showMap(View view){
        askForLocationPermission();
        if (!isLocationEnabled(this)){ Toast.makeText(this, "Включите геопозицию", Toast.LENGTH_SHORT).show(); return; }
        requestLocation();
        if(mSettings.getString("my_location","").equals("")) { Toast.makeText(this, "Подключение геолокации... ", Toast.LENGTH_SHORT).show(); return; }
        startActivity( new Intent(MainActivity.this,
                    MapActivity.class).putExtra("set_location",false)
                .putExtra("lat",mSettings.getString("my_location","").split(",")[0]).putExtra("lon",mSettings.getString("my_location","").split(",")[1]));
    }

    public void showOnMap(View view){
        askForLocationPermission();
        if (!isLocationEnabled(this)){ Toast.makeText(this, "Включите геопозицию", Toast.LENGTH_SHORT).show(); return; }
        requestLocation();
        if(mSettings.getString("my_location","").equals("")){ Toast.makeText(this, "Подключение геолокации... ", Toast.LENGTH_SHORT).show(); return;}
        startActivityForResult(new Intent(MainActivity.this,
                MapActivity.class).putExtra("set_location",true).putExtra("show_ad_geo",false)
            .putExtra("lat",mSettings.getString("my_location","").split(",")[0]).putExtra("lon",mSettings.getString("my_location","").split(",")[1]),23);
    }



    public void showAd(View view){ getSupportFragmentManager().beginTransaction().replace(R.id.fragment, new AddFragment(((TextView) view.findViewById(R.id.recyclerAd_id)).getText().toString())).commit(); }
    public void closeAd(View view){ getSupportFragmentManager().beginTransaction().replace(R.id.fragment, new AddsFragment()).commit(); }
    public void showChat(View view){ getSupportFragmentManager().beginTransaction().replace(R.id.fragment, new ChatFragment(((TextView) view.findViewById(R.id.dialog_id)).getText().toString())).commit(); }
    public void closeChat(View view) { getSupportFragmentManager().beginTransaction().replace(R.id.fragment, new MessagesFragment()).commit(); }
    public void addChat(View view){
        new Thread(new Runnable() {
            @Override
            public void run() {
                serverActions.ServerRequest("AddDialog" + ((TextView)findViewById(R.id.ad_id)).getText().toString() + "&" + mSettings.getString("user_id", ""));
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment, new MessagesFragment()).commit();
                    }
                });
            }
        }).start();
    }


    //    ================================== Изменение пароля ==============================
    public void show_change_pass(View view){
        if(findViewById(R.id.change_pass_tab).getLayoutParams().height==0){
            findViewById(R.id.change_pass_tab).setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        }
        else {
            findViewById(R.id.change_pass_tab).setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0));
        }
    }
    public void changePass(View view){
        String changePassValidation_result = Validations.changePassValidation(((EditText)findViewById(R.id.old_pas_inpt)).getText().toString(),
                ((EditText)findViewById(R.id.pas1_inpt)).getText().toString(),
                ((EditText)findViewById(R.id.pas2_inpt)).getText().toString());
        final String req = "ChngPass"+((EditText)findViewById(R.id.old_pas_inpt)).getText().toString() +
                "&" + ((EditText)findViewById(R.id.pas1_inpt)).getText().toString() + "&" + mSettings.getString("user_id","");
        if (changePassValidation_result.equals("")){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    final String answ = new String(serverActions.ServerRequest(req));
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (answ.equals("True")) {
                                    findViewById(R.id.change_pass_tab).setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0));
                                    Toast.makeText(getApplicationContext(), "Пароль успешно изменён", Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    Toast.makeText(getApplicationContext(), "Неверный пароль", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                }
            }).start();
        }
        else{
            Toast.makeText(this, changePassValidation_result, Toast.LENGTH_SHORT).show();
        }
    }

    public void addNewAd(View view){
        //if(!isAdPhotoDownloaded){ Toast.makeText(this, "Загрузите фотографию", Toast.LENGTH_SHORT).show(); return; }
        if (((TextView) findViewById(R.id.location)).getText().toString().equals("")){ Toast.makeText(this, "Откройте карту и выберите место", Toast.LENGTH_SHORT).show(); return; }
        String validation_result = Validations.adValidation(((EditText) findViewById(R.id.address)).getText().toString(),
                ((EditText) findViewById(R.id.description)).getText().toString(), ((EditText) findViewById(R.id.price)).getText().toString());
        if (validation_result.equals("")){
            //Bitmap bitmap_ad = ((BitmapDrawable) ((ImageView)findViewById(R.id.add_ad_img)).getDrawable()).getBitmap();
            //final String img = serverActions.BitmapToString(bitmap_ad);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    serverActions.ServerRequest("AddItem"+mSettings.getString("user_id","")+"&"+((EditText) findViewById(R.id.address)).getText().toString()+"&"+
                            ((EditText) findViewById(R.id.description)).getText().toString()+"&"+((EditText) findViewById(R.id.days)).getText().toString()+"&"+((EditText) findViewById(R.id.price)).getText().toString()+
                            "&"+((TextView) findViewById(R.id.location)).getText().toString() + "&" + "pic3");
                    //serverActions.ServerRequest("SetAdImage" + img);
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment, new ProfileFragment()).commit();
                }
            }).start();
            isAdPhotoDownloaded = false;
        }
        else{
            Toast.makeText(this, validation_result, Toast.LENGTH_SHORT).show();
        }

    }

    public void closeConfirmModal (View view){
        dialog.dismiss();
    }

    public void deleteAdModal(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View modal = this.getLayoutInflater().inflate(R.layout.modal_confirm, null);
        ((TextView) modal.findViewById(R.id.recyclerAd_id)).setText(((TextView) view.findViewById(R.id.recyclerAd_id)).getText().toString());
        builder.setView(modal);
        dialog = builder.create();
        dialog.show();
    }
    public void deleteAd(View view){
        final String ad_id = ((TextView) view.findViewById(R.id.recyclerAd_id)).getText().toString();
        dialog.dismiss();
        new Thread(new Runnable() {
            @Override
            public void run() {
                serverActions.ServerRequest("DeleteItem"+ad_id);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment, new ProfileFragment()).commit();
            }
        }).start();
    }


//    ================================= Изменение фото профиля ==================================================

    public void changeProfilePhoto(View view){
        //Вызываем стандартную галерею для выбора изображения с помощью Intent.ACTION_PICK:
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, GALLERY_REQUEST_PROFILE);

    }
    //Обрабатываем результат выбора в галерее:
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent i) {
        super.onActivityResult(requestCode, resultCode, i);
        if (requestCode == GALLERY_REQUEST_PROFILE){
            if(resultCode == RESULT_OK){
                Uri selectedImage = i.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
                    SharedPreferences.Editor editor = mSettings.edit();
                    editor.putString("profile_img",bitmap.toString());
                    ((ImageView) findViewById(R.id.profile_photo)).setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else if (requestCode == GALLERY_REQUEST_AD) {
            if(resultCode == RESULT_OK){
                Uri selectedImage = i.getData();
                try {
                    findViewById(R.id.add_ad_img_ic).setVisibility(View.INVISIBLE);
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
                    ((ImageView) findViewById(R.id.add_ad_img)).setImageBitmap(bitmap);
                    isAdPhotoDownloaded = true;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else if(requestCode == MAP_REQUEST) {
            if (i.getExtras().getString("location").equals("")) {
                Toast.makeText(this, "Координаты не добавлены", Toast.LENGTH_SHORT).show();
            } else {
                ((TextView)findViewById(R.id.location)).setText(i.getExtras().getString("location"));
            }
        } else if (requestCode == 200) {
            NavigationUI.setupWithNavController((BottomNavigationView) findViewById(R.id.bottomNavigationView), Navigation.findNavController(this, R.id.fragment));
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment, new AddsFragment()).commit();
        }
    }

    public void addPhoto(View view){
        //Вызываем стандартную галерею для выбора изображения с помощью Intent.ACTION_PICK:
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, GALLERY_REQUEST_AD);
    }
    public void logout(View view){
        editor = mSettings.edit();
        editor.putString("user_id","");
        editor.putString("name","");
        editor.putString("surname","");
        editor.putString("phone","");
        editor.putString("login","");
        editor.putString("my_location","");
        editor.commit();
        startActivityForResult(new Intent(MainActivity.this,
                Authorisation.class),200);
        requestLocation();
    }
    public static boolean isLocationEnabled(Context context) {
        int locationMode = 0;
        String locationProviders;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            try {
                locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);

            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
            }

            return locationMode != Settings.Secure.LOCATION_MODE_OFF;

        }else{
            locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            return !TextUtils.isEmpty(locationProviders);
        }
    }
    public void requestLocation(){
        lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000,
                0, locationListener);
    }

    public void askForLocationPermission(){
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
            } else {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
            if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
            } else {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            }
        }
    }


}
