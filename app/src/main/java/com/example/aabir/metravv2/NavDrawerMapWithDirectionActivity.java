package com.example.aabir.metravv2;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.constant.TransitMode;
import com.akexorcist.googledirection.constant.TransportMode;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.model.Info;
import com.akexorcist.googledirection.model.Route;
import com.akexorcist.googledirection.model.Step;
import com.akexorcist.googledirection.util.DirectionConverter;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class NavDrawerMapWithDirectionActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,OnMapReadyCallback {

    static String source;
    static String source2;
    private GoogleMap mMap;
    static LatLng s1,s2;
    Context context=this;
    static int REQUEST_PLACE_PICKER=1;
    int pos=0;
    int posi=1,posj=2;
    ProgressDialog progressDialog;
    Dialog dlg;
    AppBarLayout appBarLayout;

    int routenumber=0,routesize;
    StringBuilder directionInfo=new StringBuilder("");
    MarkerOptions myPositionMarker;



    LatLng latLngSourceBus;

    Button walkDirectionButton,driveDirectionButton,busDirectionButton;
    LinearLayout barPointDirectionType;





    List<Address> addresses;

    Geocoder geocoder;
    List<Step> step;
    ArrayList<LatLng> sectionList;


    TextView resultText;
    FloatingActionButton floatingActionButton,nextDirectionButton,prevDirectionButton,alternateRouteButton,currentLocationButton;


    private String[] colors = {"#7fff7272", "#7f31c7c5", "#7fff8a00"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav_drawer_map_with_direction);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        if(NavDrawerMainMenuActivity.progressDialog.isShowing())
        NavDrawerMainMenuActivity.progressDialog.dismiss();

//        fab.setVisibility(View.INVISIBLE);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
//                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
//        drawer.setDrawerListener(toggle);
//        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        resultText=(TextView)findViewById(R.id.resultText);


        walkDirectionButton=(Button)findViewById(R.id.walkDirectionButton);
        driveDirectionButton=(Button)findViewById(R.id.driveDirectionButton);
        busDirectionButton=(Button)findViewById(R.id.busDirectionButton);

        barPointDirectionType=(LinearLayout)findViewById(R.id.barPointDirectionType);


//        resultText.setText(source+"="+s1.toString()+"\n"+source2+"="+s2.toString());

        appBarLayout=(AppBarLayout)findViewById(R.id.app_bar_layout);

        progressDialog = new ProgressDialog(NavDrawerMapWithDirectionActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setProgress(0);

        final String serverKey = "AIzaSyAn4t6lMgzYWOHDOEkSGPy8ARfkByiIybk";
        final LatLng origin = s1;
        final LatLng destination = s2;

        floatingActionButton=(FloatingActionButton)findViewById(R.id.fab);
        nextDirectionButton=(FloatingActionButton)findViewById(R.id.nextDirection);
        prevDirectionButton=(FloatingActionButton)findViewById(R.id.prevDirection);
        alternateRouteButton=(FloatingActionButton)findViewById(R.id.alternateRoute);
        currentLocationButton=(FloatingActionButton)findViewById(R.id.currentLocationButton);


//        View view=new View(getApplicationContext());


        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPickButtonClick(v);

            }
        });

        busDirectionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RelativeLayout.LayoutParams layoutParams=new RelativeLayout.LayoutParams(270,20);
                layoutParams.setMargins(375,240,0,0);
                barPointDirectionType.setLayoutParams(layoutParams);

                NetworkTest task=new NetworkTest(resultText,progressDialog);
                task.execute(""+origin.latitude,""+origin.longitude,"bus_station");
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
                latLngSourceBus=new LatLng(latitude,longitude);


                NetworkTest task2=new NetworkTest(resultText,progressDialog);
                task2.execute(""+destination.latitude,""+destination.longitude,"bus_station");
                String result2= null;
                try {
                    result2 = task2.get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
//        textView.setText("");
                String[] latlong2 =  result2.split(",");
                double latitude2 = Double.parseDouble(latlong2[0]);
                double longitude2 = Double.parseDouble(latlong2[1]);
                final LatLng latLngDestBus=new LatLng(latitude2,longitude2);

                Location frst=new Location("");
                frst.setLatitude(origin.latitude);
                frst.setLongitude(origin.longitude);

                Location last=new Location("");
                last.setLatitude(destination.latitude);
                last.setLongitude(destination.longitude);

                if(frst.distanceTo(last)<=1000)
                    GoogleDirection.withServerKey(serverKey)
                            .from(origin)
                            .to(destination)
                            .transportMode(TransportMode.WALKING)
                            .alternativeRoute(true)
                            .execute(new DirectionCallback() {
                                @Override
                                public void onDirectionSuccess(final Direction direction, String rawBody) {
                                    // Do something here
//                        Snackbar.make(mMap, "Success with status : " + direction.getStatus(), Snackbar.LENGTH_SHORT).show();
                                    if (direction.isOK()) {
//                                routenumber=1;
                                        Log.d("Code Is here:","Only Walking");

                                        geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                                        mMap.clear();
                                        resultText.setText("");
                                        Route route = direction.getRouteList().get(routenumber);
                                        String color = colors[routenumber % colors.length];
                                        ArrayList<LatLng> directionPositionList = route.getLegList().get(0).getDirectionPoint();
                                        mMap.addPolyline(DirectionConverter.createPolyline(getApplicationContext(), directionPositionList, 10, Color.BLUE));

                                        Info distanceinfo=direction.getRouteList().get(0).getLegList().get(0).getDistance();
                                        Info durationinfo=direction.getRouteList().get(0).getLegList().get(0).getDuration();

                                        directionInfo.append("Walking or taking a rickshaw is recommended. \nDistance: "+distanceinfo.getText()+"\nDuration: "+durationinfo.getText());

                                    }
                                }

                                @Override
                                public void onDirectionFailure(Throwable t) {
                                    // Do something here
                                }
                            });

                else
                {
                    GoogleDirection.withServerKey(serverKey)
                            .from(origin)
                            .to(latLngSourceBus)
                            .transportMode(TransportMode.WALKING)
                            .alternativeRoute(true)
                            .execute(new DirectionCallback() {
                                @Override
                                public void onDirectionSuccess(final Direction direction, String rawBody) {
                                    // Do something here
//                        Snackbar.make(mMap, "Success with status : " + direction.getStatus(), Snackbar.LENGTH_SHORT).show();
                                    if (direction.isOK()) {
//                                routenumber=1;
                                        mMap.clear();
                                        geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                                        Log.d("Code Is here:","Origin to source bus(prev cleared)");

                                        resultText.setText("");
                                        Route route = direction.getRouteList().get(routenumber);
                                        String color = colors[routenumber % colors.length];
                                        ArrayList<LatLng> directionPositionList = route.getLegList().get(0).getDirectionPoint();
                                        mMap.addPolyline(DirectionConverter.createPolyline(getApplicationContext(), directionPositionList, 10, Color.BLUE));

                                        Info distanceinfo=direction.getRouteList().get(0).getLegList().get(0).getDistance();
                                        Info durationinfo=direction.getRouteList().get(0).getLegList().get(0).getDuration();

                                        directionInfo.append("\nMy Position to nearest Bus Station: \nDistance: "+distanceinfo.getText()+"\nDuration: "+durationinfo.getText());

                                    }
                                }

                                @Override
                                public void onDirectionFailure(Throwable t) {
                                    // Do something here
                                }
                            });
                    Location B1=new Location("");
                    B1.setLatitude(latLngSourceBus.latitude);
                    B1.setLongitude(latLngSourceBus.longitude);

                    Location B2=new Location("");
                    B2.setLatitude(latLngDestBus.latitude);
                    B2.setLongitude(latLngDestBus.longitude);


                    if(B1.distanceTo(B2)>=300)
                        GoogleDirection.withServerKey(serverKey)
                                .from(latLngSourceBus)
                                .to(latLngDestBus)
                                .transportMode(TransportMode.DRIVING)
                                .alternativeRoute(false)
                                .execute(new DirectionCallback() {
                                    @Override
                                    public void onDirectionSuccess(final Direction direction, String rawBody) {
                                        // Do something here
//                        Snackbar.make(mMap, "Success with status : " + direction.getStatus(), Snackbar.LENGTH_SHORT).show();
                                        if (direction.isOK()) {
//                                routenumber=1;

                                            Log.d("Code Is here:","Source bus to dest bus");

                                            geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                                            resultText.setText("");
                                            Route route = direction.getRouteList().get(routenumber);
                                            String color = colors[routenumber % colors.length];
                                            ArrayList<LatLng> directionPositionList = route.getLegList().get(0).getDirectionPoint();
                                            mMap.addPolyline(DirectionConverter.createPolyline(getApplicationContext(), directionPositionList, 10, Color.parseColor(color)));




                                            Info distanceinfo=direction.getRouteList().get(routenumber).getLegList().get(0).getDistance();
                                            Info durationinfo=direction.getRouteList().get(routenumber).getLegList().get(0).getDuration();
//                            resultText.setText("Distance: "+distanceinfo.getText()+"\nDuration: "+durationinfo.getText());

                                            directionInfo.append("\n\nThe Bus Journey: \nDistance: "+distanceinfo.getText()+"\nDuration: "+durationinfo.getText());


                                            //This should be used together
                                            sectionList = direction.getRouteList().get(routenumber).getLegList().get(0).getSectionPoint();
                                            for (LatLng position : sectionList) {

                                                MarkerOptions marker = new MarkerOptions().position(
                                                        position);

                                                marker.icon(BitmapDescriptorFactory
                                                        .defaultMarker(BitmapDescriptorFactory.HUE_CYAN));

                                                mMap.addMarker(marker);

                                            }
                                            step = direction.getRouteList().get(routenumber).getLegList().get(0).getStepList();





                                            ArrayList<PolylineOptions> polylineOptionList = DirectionConverter.createTransitPolyline(getApplicationContext(), step, 5, Color.RED, 5, Color.BLUE);
                                            for (PolylineOptions polylineOption : polylineOptionList) {
                                                mMap.addPolyline(polylineOption);
                                            }


                                            MarkerOptions markerBus = new MarkerOptions().position(
                                                    latLngSourceBus).title("BusStation").snippet("Bus Station");

                                            markerBus.icon(BitmapDescriptorFactory.fromResource(R.drawable.busicon)
                                            );

                                            CameraPosition cameraPosition = new CameraPosition.Builder()
                                                    .target(latLngSourceBus).zoom(16).build();

                                            mMap.animateCamera(CameraUpdateFactory
                                                    .newCameraPosition(cameraPosition));

                                            mMap.addMarker(markerBus);


                                            MarkerOptions markerBus2 = new MarkerOptions().position(
                                                    latLngDestBus).title("BusStation").snippet("Bus Station");

                                            markerBus2.icon(BitmapDescriptorFactory.fromResource(R.drawable.busicon)
                                            );

                                            CameraPosition cameraPosition2 = new CameraPosition.Builder()
                                                    .target(latLngDestBus).zoom(16).build();

                                            mMap.animateCamera(CameraUpdateFactory
                                                    .newCameraPosition(cameraPosition));

                                            mMap.addMarker(markerBus2);






                                            alternateRouteButton.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    if(routenumber<direction.getRouteList().size()-1)
                                                        routenumber++;
                                                    else
                                                        routenumber=0;

                                                    pos=0;
                                                    posi=1;
                                                    posj=2;

                                                    mMap.clear();
                                                    CameraPosition cameraPosition = new CameraPosition.Builder()
                                                            .target(s1).zoom(12).build();

                                                    mMap.animateCamera(CameraUpdateFactory
                                                            .newCameraPosition(cameraPosition));


                                                    Route route = direction.getRouteList().get(routenumber);
                                                    String color = colors[routenumber % colors.length];
                                                    ArrayList<LatLng> directionPositionList = route.getLegList().get(0).getDirectionPoint();
                                                    mMap.addPolyline(DirectionConverter.createPolyline(getApplicationContext(), directionPositionList, 10, Color.parseColor(color)));

                                                    Info distanceinfo=direction.getRouteList().get(routenumber).getLegList().get(0).getDistance();
                                                    Info durationinfo=direction.getRouteList().get(routenumber).getLegList().get(0).getDuration();
                                                    resultText.setText("Distance: "+distanceinfo.getText()+"\nDuration: "+durationinfo.getText());


                                                    //This should be used together
                                                    sectionList = direction.getRouteList().get(routenumber).getLegList().get(0).getSectionPoint();
                                                    for (LatLng position : sectionList) {
                                                        MarkerOptions marker = new MarkerOptions().position(
                                                                position);

                                                        marker.icon(BitmapDescriptorFactory
                                                                .defaultMarker(BitmapDescriptorFactory.HUE_CYAN));

                                                        mMap.addMarker(marker);

                                                    }
                                                    step = direction.getRouteList().get(routenumber).getLegList().get(0).getStepList();
                                                    ArrayList<PolylineOptions> polylineOptionList = DirectionConverter.createTransitPolyline(getApplicationContext(), step, 5, Color.RED, 5, Color.BLUE);
                                                    for (PolylineOptions polylineOption : polylineOptionList) {
                                                        mMap.addPolyline(polylineOption);
                                                    }
                                                }
                                            });



                                            nextDirectionButton.setOnClickListener(new View.OnClickListener() {

                                                @Override
                                                public void onClick(View v) {

                                                    try {

                                                        if(pos==0)
                                                        {
                                                            resultText.setText("You are here");
                                                            CameraPosition cameraPosition = new CameraPosition.Builder()
                                                                    .target(s1).zoom(20).build();

                                                            mMap.animateCamera(CameraUpdateFactory
                                                                    .newCameraPosition(cameraPosition));
                                                            pos++;
                                                        }


                                                        else if (pos < sectionList.size()-1) {
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





                                            //PreviousButtonClick
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
                                                }
                                            });

                                            currentLocationButton.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {






                                                    LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                                                    // get the last know location from your location manager.
                                                    boolean permissionGranted = ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;

                                                    try {
                                                        if (permissionGranted) {
                                                            // {Some Code}


                                                            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                                                            LatLng latLng=new LatLng(location.getLatitude(),location.getLongitude());
                                                            CameraPosition cameraPosition = new CameraPosition.Builder()
                                                                    .target(latLng).zoom(20).build();

                                                            myPositionMarker = new MarkerOptions().position(
                                                                    latLng).title("You");

                                                            myPositionMarker.icon(BitmapDescriptorFactory
                                                                    .defaultMarker(BitmapDescriptorFactory.HUE_AZURE));

                                                            mMap.addMarker(myPositionMarker);
                                                            mMap.animateCamera(CameraUpdateFactory
                                                                    .newCameraPosition(cameraPosition));


                                                        } else {
                                                            ActivityCompat.requestPermissions(getParent(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 200);
                                                        }
                                                    }catch(Exception e)
                                                    {

                                                    }

                                                }
                                            });





                                        }

                                    }

                                    @Override
                                    public void onDirectionFailure(Throwable t) {
                                        // Do something here
                                        driveDirectionButton.performClick();
                                    }
                                });
                    GoogleDirection.withServerKey(serverKey)
                            .from(latLngDestBus)
                            .to(destination)
                            .transportMode(TransportMode.WALKING)
                            .alternativeRoute(true)
                            .execute(new DirectionCallback() {
                                @Override
                                public void onDirectionSuccess(final Direction direction, String rawBody) {
                                    // Do something here
//                        Snackbar.make(mMap, "Success with status : " + direction.getStatus(), Snackbar.LENGTH_SHORT).show();
                                    if (direction.isOK()) {
//                                routenumber=1;
                                        geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                                        Log.d("Code Is here:","dest bus to dest");

                                        resultText.setText("");
                                        Route route = direction.getRouteList().get(routenumber);
                                        String color = colors[routenumber % colors.length];
                                        ArrayList<LatLng> directionPositionList = route.getLegList().get(0).getDirectionPoint();
                                        mMap.addPolyline(DirectionConverter.createPolyline(getApplicationContext(), directionPositionList, 10, Color.BLUE));

                                        Info distanceinfo=direction.getRouteList().get(routenumber).getLegList().get(0).getDistance();
                                        Info durationinfo=direction.getRouteList().get(routenumber).getLegList().get(0).getDuration();

                                        directionInfo.append("\n\nBusStation to My Destination: \nDistance: "+distanceinfo.getText()+"\nDuration: "+durationinfo.getText());

                                    }
                                }

                                @Override
                                public void onDirectionFailure(Throwable t) {
                                    // Do something here
                                }
                            });

                }
            }
        });




        driveDirectionButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                GoogleDirection.withServerKey(serverKey)
                        .from(origin)
                        .to(destination)
                        .transportMode(TransportMode.DRIVING)
                        .alternativeRoute(true)
                        .execute(new DirectionCallback() {
                            @Override
                            public void onDirectionSuccess(final Direction direction, String rawBody) {
                                // Do something here
//                        Snackbar.make(mMap, "Success with status : " + direction.getStatus(), Snackbar.LENGTH_SHORT).show();
                                if (direction.isOK()) {
                                    RelativeLayout.LayoutParams layoutParams=new RelativeLayout.LayoutParams(270,20);
                                    layoutParams.setMargins(720,240,0,0);
                                    barPointDirectionType.setLayoutParams(layoutParams);


                                    mMap.clear();
                                    LatLng latLngSource = s1;
                                    MarkerOptions marker = new MarkerOptions().position(
                                            latLngSource).title(source).snippet("Source");
                                    marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.locationicon2));
                                    CameraPosition cameraPosition = new CameraPosition.Builder()
                                            .target(latLngSource).zoom(12).build();
                                    mMap.animateCamera(CameraUpdateFactory
                                            .newCameraPosition(cameraPosition));
                                    mMap.addMarker(marker);

                                    LatLng latLngDest = s2;
                                    MarkerOptions marker2 = new MarkerOptions().position(
                                            latLngDest).title(source2).snippet("Destination");
                                    marker2.icon(BitmapDescriptorFactory.fromResource(R.drawable.locationicon2));
                                    mMap.addMarker(marker2);

                                    geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                                    resultText.setText("");
                                    Route route = direction.getRouteList().get(routenumber);
                                    String color = colors[routenumber % colors.length];
                                    ArrayList<LatLng> directionPositionList = route.getLegList().get(0).getDirectionPoint();
                                    mMap.addPolyline(DirectionConverter.createPolyline(getApplicationContext(), directionPositionList, 10, Color.parseColor(color)));

                                    Info distanceinfo=direction.getRouteList().get(routenumber).getLegList().get(0).getDistance();
                                    Info durationinfo=direction.getRouteList().get(routenumber).getLegList().get(0).getDuration();

                                    sectionList = direction.getRouteList().get(routenumber).getLegList().get(0).getSectionPoint();
                                    for (LatLng position : sectionList) {

                                        MarkerOptions marker5 = new MarkerOptions().position(
                                                position);

                                        marker5.icon(BitmapDescriptorFactory
                                                .defaultMarker(BitmapDescriptorFactory.HUE_CYAN));

                                        mMap.addMarker(marker5);

                                    }
                                    step = direction.getRouteList().get(routenumber).getLegList().get(0).getStepList();

                                    ArrayList<PolylineOptions> polylineOptionList = DirectionConverter.createTransitPolyline(getApplicationContext(), step, 5, Color.RED, 5, Color.BLUE);
                                    for (PolylineOptions polylineOption : polylineOptionList) {
                                        mMap.addPolyline(polylineOption);
                                    }
                                    directionInfo.delete(0,directionInfo.length());
                                    directionInfo.append("\nMy Position to Destination: \nDistance: "+distanceinfo.getText()+"\nDuration: "+durationinfo.getText());

                                }
                            }

                            @Override
                            public void onDirectionFailure(Throwable t) {
                                // Do something here
                            }
                        });

            }
        });
        walkDirectionButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                GoogleDirection.withServerKey(serverKey)
                        .from(origin)
                        .to(destination)
                        .transportMode(TransportMode.WALKING)
                        .alternativeRoute(true)
                        .execute(new DirectionCallback() {
                            @Override
                            public void onDirectionSuccess(final Direction direction, String rawBody) {
                                // Do something here
//                        Snackbar.make(mMap, "Success with status : " + direction.getStatus(), Snackbar.LENGTH_SHORT).show();
                                if (direction.isOK()) {
                                    RelativeLayout.LayoutParams layoutParams=new RelativeLayout.LayoutParams(270,20);
                                    layoutParams.setMargins(40,240,0,0);
                                    barPointDirectionType.setLayoutParams(layoutParams);


                                    mMap.clear();
                                    LatLng latLngSource = s1;
                                    MarkerOptions marker = new MarkerOptions().position(
                                            latLngSource).title(source).snippet("Source");
                                    marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.locationicon2));
                                    CameraPosition cameraPosition = new CameraPosition.Builder()
                                            .target(latLngSource).zoom(12).build();
                                    mMap.animateCamera(CameraUpdateFactory
                                            .newCameraPosition(cameraPosition));
                                    mMap.addMarker(marker);

                                    LatLng latLngDest = s2;
                                    MarkerOptions marker2 = new MarkerOptions().position(
                                            latLngDest).title(source2).snippet("Destination");
                                    marker2.icon(BitmapDescriptorFactory.fromResource(R.drawable.locationicon2));
                                    mMap.addMarker(marker2);

                                    geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                                    resultText.setText("");
                                    Route route = direction.getRouteList().get(routenumber);
                                    String color = colors[routenumber % colors.length];
                                    ArrayList<LatLng> directionPositionList = route.getLegList().get(0).getDirectionPoint();
                                    mMap.addPolyline(DirectionConverter.createPolyline(getApplicationContext(), directionPositionList, 10, Color.parseColor(color)));

                                    Info distanceinfo=direction.getRouteList().get(routenumber).getLegList().get(0).getDistance();
                                    Info durationinfo=direction.getRouteList().get(routenumber).getLegList().get(0).getDuration();

                                    sectionList = direction.getRouteList().get(routenumber).getLegList().get(0).getSectionPoint();
                                    for (LatLng position : sectionList) {

                                        MarkerOptions marker5 = new MarkerOptions().position(
                                                position);

                                        marker5.icon(BitmapDescriptorFactory
                                                .defaultMarker(BitmapDescriptorFactory.HUE_CYAN));

                                        mMap.addMarker(marker5);

                                    }
                                    step = direction.getRouteList().get(routenumber).getLegList().get(0).getStepList();

                                    ArrayList<PolylineOptions> polylineOptionList = DirectionConverter.createTransitPolyline(getApplicationContext(), step, 5, Color.RED, 5, Color.BLUE);
                                    for (PolylineOptions polylineOption : polylineOptionList) {
                                        mMap.addPolyline(polylineOption);
                                    }
                                    directionInfo.delete(0,directionInfo.length());
                                    directionInfo.append("\nMy Position to Destination: \nDistance: "+distanceinfo.getText()+"\nDuration: "+durationinfo.getText());

                                }
                            }

                            @Override
                            public void onDirectionFailure(Throwable t) {
                                // Do something here
                            }
                        });

            }
        });


        busDirectionButton.performClick();

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            Intent intent=new Intent(NavDrawerMapWithDirectionActivity.this,NavDrawerMainMenuActivity.class);
            startActivity(intent);
//            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.nav_drawer_map_with_direction, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.get_direction_screen) {
            // Handle the camera action
            Intent intent=new Intent(NavDrawerMapWithDirectionActivity.this,NavDrawerMainMenuActivity.class);
            startActivity(intent);
        } else if (id == R.id.find_place_screen) {
            Intent intent=new Intent(NavDrawerMapWithDirectionActivity.this,NavDrawerPlaceSearchActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // Add a marker in Sydney and move the camera
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//             TODO: Consider calling
            return;
        }
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
//        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        mMap.getUiSettings().setRotateGesturesEnabled(true);
        mMap.setTrafficEnabled(true);
        mMap.getUiSettings().setAllGesturesEnabled(true);
        mMap.getUiSettings().setTiltGesturesEnabled(true);

        mMap.setPadding(0,0,50,0);

        LatLng latLngSource = s1;
//                    resultText.setText(latLngSource.toString());

        MarkerOptions marker = new MarkerOptions().position(
                latLngSource).title(source).snippet("Source");

        marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.locationicon2));

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(latLngSource).zoom(12).build();

        mMap.animateCamera(CameraUpdateFactory
                .newCameraPosition(cameraPosition));

        mMap.addMarker(marker);




        LatLng latLngDest = s2;
//                    resultText.setText(latLngDest.toString());

        MarkerOptions marker2 = new MarkerOptions().position(
                latLngDest).title(source2).snippet("Destination");

        marker2.icon(BitmapDescriptorFactory.fromResource(R.drawable.locationicon2));
// Moving Camera to a Location with animation


        mMap.addMarker(marker2);














    }

    public void setSource(String source)
    {
        this.source=source;
    }

    public void onPickButtonClick(View v) {
        // Construct an intent for the place picker
        resultText.setText(directionInfo);
        resultText.setVisibility(View.VISIBLE);

        resultText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                directionInfo.delete(0,directionInfo.length());
//                appBarLayout.setVisibility(View.INVISIBLE);
                    resultText.setVisibility(View.INVISIBLE);
                resultText.setGravity(View.SCROLL_INDICATOR_BOTTOM);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode,
                                    int resultCode, Intent data) {

        if (requestCode == REQUEST_PLACE_PICKER
                && resultCode == Activity.RESULT_OK) {

            // The user has selected a place. Extract the name and address.
            final com.google.android.gms.location.places.Place place = PlacePicker.getPlace(data, this);

            final CharSequence name = place.getName();
            final CharSequence address = place.getAddress();
            String attributions = PlacePicker.getAttributions(data);
            if (attributions == null) {
                attributions = "";
            }

            resultText.setText(name);
            resultText.setText(resultText.getText().toString().concat(address.toString()));
            resultText.setText(resultText.getText().toString().concat(Html.fromHtml(attributions).toString()));

        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

}
