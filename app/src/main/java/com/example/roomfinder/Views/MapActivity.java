package com.example.roomfinder.Views;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.roomfinder.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    public LocationManager locationManager;
    private LatLng coord;
    private Marker marker;
    private String marker_coord;



    private final LocationListener locationListener = new LocationListener() {
        private boolean isFirstLocation = true;

        @Override
        public void onLocationChanged(final Location location) {
            coord = new LatLng(location.getLatitude(), location.getLongitude());
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ((TextView) (findViewById(R.id.tvLocation))).setText("Ваши координаты: "+coord.toString().replace("lat/lng: (","").replace(")",""));
                    //Toast.makeText(getApplicationContext(), coord.toString().replace("lat/lng: (","").replace(")",""), Toast.LENGTH_SHORT).show();

                }
            });
            if (isFirstLocation && getIntent().getExtras().getBoolean("set_location")) {
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(coord, 17));
                isFirstLocation = false;
            }

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {
            Toast.makeText(getApplicationContext(), "Включите геопозицию", Toast.LENGTH_SHORT).show();
            finish();
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        //presenter = new MainPresenter(this, new MainModel());
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
            } else {
                ActivityCompat.requestPermissions(MapActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
            if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
            } else {
                ActivityCompat.requestPermissions(MapActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            }
        }
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000,
                0, locationListener);
    }




    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if(!MainActivity.isLocationEnabled(this)){ return; }
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
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
            } else {
                ActivityCompat.requestPermissions(MapActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
            if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
            } else {
                ActivityCompat.requestPermissions(MapActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            }
        }
        coord = new LatLng(Double.parseDouble(getIntent().getExtras().getString("lat")), Double.parseDouble(getIntent().getExtras().getString("lon")));
        marker_coord = coord.toString().replace("lat/lng: (","").replace(")","");
        ((TextView) findViewById(R.id.map_title)).setText("");
        Intent i = new Intent();
        i.putExtra("location",marker_coord);
        setResult(200,i);
        ((TextView) (findViewById(R.id.tvLocation))).setText("Ваши координаты: "+coord.toString().replace("lat/lng: (","").replace(")",""));
        if(getIntent().getExtras().getBoolean("set_location")){
            ((TextView) findViewById(R.id.map_title)).setText("Перетащите маркер в соответствии вашему объявлению и нажмите на него");
            marker = mMap.addMarker(
                    new MarkerOptions()
                            .position(coord)
                            .draggable(true));
            mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
                @Override
                public void onMarkerDragStart(Marker marker) {

                }

                @Override
                public void onMarkerDrag(Marker marker) {

                }

                @Override
                public void onMarkerDragEnd(Marker marker) {
                    marker_coord = marker.getPosition().toString().replace("lat/lng: (","").replace(")","");
                }
            });
            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker m) {
                    Intent i = new Intent();
                    i.putExtra("location",marker_coord);
                    setResult(23,i);
                    finish();
                    return false;
                }
            });
        }
        if(getIntent().getExtras().getBoolean("show_ad_geo")){
            String[] a = getIntent().getExtras().getString("ad_geo").split(",");
            marker = mMap.addMarker(
                    new MarkerOptions()
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA))
                            .position(new LatLng(Double.parseDouble(a[0]),Double.parseDouble(a[1]))));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(a[0]),Double.parseDouble(a[1])), 17));
        }
        mMap.setMyLocationEnabled(true);
        mMap.setBuildingsEnabled(true);
    }



    public void closeMap(View view){
        Intent i = new Intent();
        i.putExtra("location","");
        setResult(23,i);
        finish();
    }



}

