package com.example.aabir.metravv2;

import android.Manifest;
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
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
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
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
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
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        resultText=(TextView)findViewById(R.id.resultText);
        resultText.setText(source+"="+s1.toString()+"\n"+source2+"="+s2.toString());

        appBarLayout=(AppBarLayout)findViewById(R.id.app_bar_layout);

        progressDialog = new ProgressDialog(MapsActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setProgress(0);

        String serverKey = "AIzaSyAn4t6lMgzYWOHDOEkSGPy8ARfkByiIybk";
        final LatLng origin = s1;
        LatLng destination = s2;

        floatingActionButton=(FloatingActionButton)findViewById(R.id.fab);
        nextDirectionButton=(FloatingActionButton)findViewById(R.id.nextDirection);
        prevDirectionButton=(FloatingActionButton)findViewById(R.id.prevDirection);
        alternateRouteButton=(FloatingActionButton)findViewById(R.id.alternateRoute);
        currentLocationButton=(FloatingActionButton)findViewById(R.id.currentLocationButton);

floatingActionButton.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        onPickButtonClick(v);
    }
});

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
        final LatLng latLngSourceBus=new LatLng(latitude,longitude);


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
                            geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

                            resultText.setText("");
                            Route route = direction.getRouteList().get(routenumber);
                            String color = colors[routenumber % colors.length];
                            ArrayList<LatLng> directionPositionList = route.getLegList().get(0).getDirectionPoint();
                            mMap.addPolyline(DirectionConverter.createPolyline(getApplicationContext(), directionPositionList, 5, Color.BLUE));

                            Info distanceinfo=direction.getRouteList().get(0).getLegList().get(0).getDistance();
                            Info durationinfo=direction.getRouteList().get(0).getLegList().get(0).getDuration();

                            directionInfo.append("My Position to nearest Bus Station: \nDistance: "+distanceinfo.getText()+"\nDuration: "+durationinfo.getText());

                        }
                    }

                        @Override
                        public void onDirectionFailure(Throwable t) {
                            // Do something here
                        }
                    });


        GoogleDirection.withServerKey(serverKey)
                .from(latLngSourceBus)
                .to(latLngDestBus)
                .transportMode(TransportMode.WALKING)
                .transitMode(TransitMode.RAIL)
                .alternativeRoute(false)
                .execute(new DirectionCallback() {
                    @Override
                    public void onDirectionSuccess(final Direction direction, String rawBody) {
                        // Do something here
//                        Snackbar.make(mMap, "Success with status : " + direction.getStatus(), Snackbar.LENGTH_SHORT).show();
                        if (direction.isOK()) {
//                                routenumber=1;
                            geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

                            resultText.setText("");
                            Route route = direction.getRouteList().get(routenumber);
                            String color = colors[routenumber % colors.length];
                            ArrayList<LatLng> directionPositionList = route.getLegList().get(0).getDirectionPoint();
                            mMap.addPolyline(DirectionConverter.createPolyline(getApplicationContext(), directionPositionList, 5, Color.parseColor(color)));




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
                                    mMap.addPolyline(DirectionConverter.createPolyline(getApplicationContext(), directionPositionList, 5, Color.parseColor(color)));

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

                            resultText.setText("");
                            Route route = direction.getRouteList().get(routenumber);
                            String color = colors[routenumber % colors.length];
                            ArrayList<LatLng> directionPositionList = route.getLegList().get(0).getDirectionPoint();
                            mMap.addPolyline(DirectionConverter.createPolyline(getApplicationContext(), directionPositionList, 5, Color.BLUE));

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



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // Add a marker in Sydney and move the camera
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//             TODO: Consider calling
            return;
        }
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        mMap.getUiSettings().setRotateGesturesEnabled(true);
        mMap.setTrafficEnabled(true);
        mMap.setPadding(0,220,200,0);

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
        appBarLayout.setVisibility(View.VISIBLE);

        resultText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                directionInfo.delete(0,directionInfo.length());
                appBarLayout.setVisibility(View.INVISIBLE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode,
                                    int resultCode, Intent data) {

        if (requestCode == REQUEST_PLACE_PICKER
                && resultCode == Activity.RESULT_OK) {

            // The user has selected a place. Extract the name and address.
            final Place place = PlacePicker.getPlace(data, this);

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
