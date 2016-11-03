package com.example.aabir.metravv2;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/**
 * Created by aabir on 8/23/2016.
 */
public class NetworkActivity extends Activity {

    static Double lat,lon;
    static ArrayList<Place> placeList;
    LatLng latLng=null;
    String result;
    ProgressDialog progressDialog;
    private static final String APP_ID = "AIzaSyCJIPYN7Zb1AOk3Kar9OAiSQKvNoJIXXpI";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.network_test_layout);
    Context context=this;
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        // get the last know location from your location manager.
        boolean permissionGranted = ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        try {
            if (permissionGranted) {
                // {Some Code}
                Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                // now get the lat/lon from the location and do something with it.
                lat=location.getLatitude();
                lon=location.getLongitude();
            }
        }catch(Exception e)
        {
            e.printStackTrace();
        }
        progressDialog = new ProgressDialog(NetworkActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setProgress(0);
//        progressDialog.show();
        TextView textView = (TextView) findViewById(R.id.textViewWeather);
        NetworkTest task=new NetworkTest(textView,progressDialog);
        task.execute(""+lat,""+lon,"atm");
        String result= null;
        try {
            result = task.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
//        textView.setText("");
        String[] latlong =  result.split(",");
        double latitude = Double.parseDouble(latlong[0]);
        double longitude = Double.parseDouble(latlong[1]);
        LatLng latLng1=new LatLng(latitude,longitude);
        textView.setText(latLng1.toString());
//        LatLng latLng1=(Lat)textView.getText().toString();
//        textView.setText(placeList.toString());
    }

}
