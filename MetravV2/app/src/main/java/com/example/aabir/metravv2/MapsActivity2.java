package com.example.aabir.metravv2;

import android.*;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.view.LayoutInflaterCompat;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.constant.TransitMode;
import com.akexorcist.googledirection.constant.TransportMode;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.model.Info;
import com.akexorcist.googledirection.model.Leg;
import com.akexorcist.googledirection.model.Route;
import com.akexorcist.googledirection.model.Step;
import com.akexorcist.googledirection.util.DirectionConverter;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.plus.model.people.Person;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MapsActivity2 extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    ProgressDialog progDailog;
    String serverKey = "AIzaSyAn4t6lMgzYWOHDOEkSGPy8ARfkByiIybk";
    static LatLng origin;
    static LatLng destination;
    TextView resultText;
    Context context=this;

        List<Address> addresses;

    Geocoder geocoder;
    List<Step> step;

    FloatingActionButton floatingActionButton,nextDirectionButton,prevDirectionButton;
    int pos=1;
    int posi=1,posj=2;

    AppBarLayout appBarLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_maps2);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        resultText=(TextView)findViewById(R.id.resultText2);
        floatingActionButton=(FloatingActionButton)findViewById(R.id.fab2);
        nextDirectionButton=(FloatingActionButton)findViewById(R.id.nextDirection2);
        prevDirectionButton=(FloatingActionButton)findViewById(R.id.prevDirection2);
        appBarLayout=(AppBarLayout)findViewById(R.id.app_bar_layout);
        new Load().execute();
        resultText.setVisibility(View.INVISIBLE);
        appBarLayout.setVisibility(View.INVISIBLE);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog=new Dialog(context);
                dialog.setContentView(R.layout.popupinfo);
//                Button button=(Button)findViewById(R.id.close_popup);
//                button.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        dialog.dismiss();
//                    }
//                });
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                Window window = dialog.getWindow();
                WindowManager.LayoutParams wlp = window.getAttributes();

                wlp.gravity = Gravity.TOP;
                wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
                window.setAttributes(wlp);
                dialog.show();
            }
        });
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//             TODO: Consider calling
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        mMap.getUiSettings().setRotateGesturesEnabled(true);
        mMap.setTrafficEnabled(true);
        mMap.setPadding(0,350,0,0);

        LatLng latLngSource = origin;
//                    resultText.setText(latLngSource.toString());

        MarkerOptions marker = new MarkerOptions().position(
                latLngSource).snippet("Source");

        marker.icon(BitmapDescriptorFactory
                .defaultMarker(BitmapDescriptorFactory.HUE_GREEN));

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(latLngSource).zoom(12).build();

        mMap.animateCamera(CameraUpdateFactory
                .newCameraPosition(cameraPosition));

        mMap.addMarker(marker);




        LatLng latLngDest = destination;
//                    resultText.setText(latLngDest.toString());

        MarkerOptions marker2 = new MarkerOptions().position(
                latLngDest).snippet("Destination");

        marker2.icon(BitmapDescriptorFactory
                .defaultMarker(BitmapDescriptorFactory.HUE_RED));

// Moving Camera to a Location with animation


        mMap.addMarker(marker2);

    }
    class Load extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progDailog = new ProgressDialog(MapsActivity2.this);
            progDailog.setMessage("Loading...");
            progDailog.setIndeterminate(false);
            progDailog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progDailog.setCancelable(false);
            progDailog.show();
        }
        @Override
        protected String doInBackground(String... aurl) {
            //do something while spinning circling show

            GoogleDirection.withServerKey(serverKey)
                    .from(origin)
                    .to(destination)
                    .transportMode(TransportMode.WALKING)
                    .transitMode(TransitMode.BUS)
                    .alternativeRoute(true)
                    .execute(new DirectionCallback() {
                        @Override
                        public void onDirectionSuccess(Direction direction, String rawBody) {
                            // Do something here
//                        Snackbar.make(mMap, "Success with status : " + direction.getStatus(), Snackbar.LENGTH_SHORT).show();
                            if (direction.isOK()) {
                                resultText.setText("");

                                Route route = direction.getRouteList().get(0);
                                Leg leg = route.getLegList().get(0);
                                step= leg.getStepList();

                                ArrayList<LatLng> pointList = leg.getDirectionPoint();

                                final ArrayList<LatLng> sectionList = leg.getSectionPoint();

                                geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

                                for (LatLng position : sectionList) {
                                    try {
                                        addresses = geocoder.getFromLocation(position.latitude, position.longitude, 1);
                                        String ad=addresses.get(0).getAddressLine(0);

                                        MarkerOptions marker = new MarkerOptions().position(
                                                position).title(ad);

                                        marker.icon(BitmapDescriptorFactory
                                                .defaultMarker(BitmapDescriptorFactory.HUE_CYAN));

                                        mMap.addMarker(marker);

                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }

                                CameraPosition cameraPosition = new CameraPosition.Builder()
                                        .target(origin).zoom(20).build();

                                mMap.animateCamera(CameraUpdateFactory
                                        .newCameraPosition(cameraPosition));

/*

                                int i=1;
                                for(int j=2;j<sectionList.size();j++)
                                {
                                    String info=sectionList.get(j).toString();
                                    try {

                                        String maneuver = step.get(i).getManeuver();
                                        if(maneuver==null)
                                            maneuver="head straight";
                                        addresses = geocoder.getFromLocation(sectionList.get(j).latitude, sectionList.get(j).longitude, 1);
                                        String ad=addresses.get(0).getAddressLine(0);
                                        i++;
                                        String info2=resultText.getText().toString().concat("On "+ad+", "+maneuver);
                                        resultText.setText(info2);

                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }

                                }
                                i=2;

*/
                                ArrayList<PolylineOptions> polylineOptionList = DirectionConverter.createTransitPolyline(getApplicationContext(), step, 5, Color.RED, 5, Color.BLUE);
                                for (PolylineOptions polylineOption : polylineOptionList) {
                                    mMap.addPolyline(polylineOption);
                                }
                                resultText.setText("");
                                resultText.setText("You are here");
                                appBarLayout.setVisibility(View.VISIBLE);
                                resultText.setVisibility(View.VISIBLE);
                                nextDirectionButton.setOnClickListener(new View.OnClickListener() {

                                    @Override
                                    public void onClick(View v) {

//                                    sectionList.get(pos);

                                        try {
                                            if (pos < sectionList.size()-1) {
                                                String maneuver = step.get(posi).getManeuver();
                                                if (maneuver == null)
                                                    maneuver = "head straight";

                                                addresses = geocoder.getFromLocation(sectionList.get(pos).latitude, sectionList.get(pos).longitude, 1);


                                                String ad = addresses.get(0).getAddressLine(0);


                                                Info distanceInfo = step.get(posi).getDistance();
                                                Info durationInfo = step.get(posi).getDuration();
                                                String distance = distanceInfo.getText();
                                                String duration = durationInfo.getText();
                                                if (posi < step.size())
                                                    posi++;


                                                String info2 = "On " + ad + ", " + maneuver+"\nDuration: "+duration+"\nDistance: "+distance;
                                                resultText.setText(info2);


                                                CameraPosition cameraPosition = new CameraPosition.Builder()
                                                        .target(sectionList.get(pos)).zoom(20).build();

                                                mMap.animateCamera(CameraUpdateFactory
                                                        .newCameraPosition(cameraPosition));
                                                pos++;

                                            } else
                                            {
                                                CameraPosition cameraPosition = new CameraPosition.Builder()
                                                        .target(sectionList.get(pos)).zoom(20).build();

                                                mMap.animateCamera(CameraUpdateFactory
                                                        .newCameraPosition(cameraPosition));
                                                resultText.setText("You have successfully reached your destination.");
                                                Toast.makeText(getApplicationContext(),"Destination",Toast.LENGTH_LONG);
                                            }
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }


                                    }
                                });


                                prevDirectionButton.setOnClickListener(new View.OnClickListener() {

                                    @Override
                                    public void onClick(View v) {

//                                    sectionList.get(pos);
                                        if (posi >0) {
                                            posi--;
                                            pos--;
                                        }
                                        try {
                                            if (pos > 1) {
                                                String maneuver = step.get(posi).getManeuver();
                                                if (maneuver == null)
                                                    maneuver = "head straight";

                                                addresses = geocoder.getFromLocation(sectionList.get(pos).latitude, sectionList.get(pos).longitude, 1);


                                                String ad = addresses.get(0).getAddressLine(0);


                                                Info distanceInfo = step.get(posi).getDistance();
                                                Info durationInfo = step.get(posi).getDuration();
                                                String distance = distanceInfo.getText();
                                                String duration = durationInfo.getText();


                                                String info2 = "On " + ad + ", " + maneuver+"\nDuration: "+duration+"\nDistance: "+distance;
                                                resultText.setText(info2);


                                                CameraPosition cameraPosition = new CameraPosition.Builder()
                                                        .target(sectionList.get(pos)).zoom(20).build();

                                                mMap.animateCamera(CameraUpdateFactory
                                                        .newCameraPosition(cameraPosition));

                                            } else
                                            {
                                                CameraPosition cameraPosition = new CameraPosition.Builder()
                                                        .target(sectionList.get(pos)).zoom(20).build();

                                                mMap.animateCamera(CameraUpdateFactory
                                                        .newCameraPosition(cameraPosition));
                                                resultText.setText("\tThis is the source");
                                                Toast.makeText(getApplicationContext(),"Destination",Toast.LENGTH_LONG);
                                            }
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }


//                                    Snackbar snackbar= Snackbar.make(getCurrentFocus(),"",Snackbar.LENGTH_LONG);
                                    }
                                });




                            }

                        }

                        @Override
                        public void onDirectionFailure(Throwable t) {
                            // Do something here
                        }
                    });


            return null;

        }
        @Override
        protected void onPostExecute(String unused) {
            super.onPostExecute(unused);
            progDailog.dismiss();
        }
    }
}
