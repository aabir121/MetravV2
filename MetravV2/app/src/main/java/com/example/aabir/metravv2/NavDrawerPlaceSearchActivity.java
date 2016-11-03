package com.example.aabir.metravv2;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.LayoutInflaterCompat;

import android.support.v7.app.AlertDialog;
import android.support.v7.view.ContextThemeWrapper;
import android.text.Layout;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.akexorcist.googledirection.model.Info;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class NavDrawerPlaceSearchActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,AdapterView.OnItemSelectedListener,GoogleApiClient.OnConnectionFailedListener,OnMapReadyCallback,GoogleMap.OnMapLongClickListener,GoogleMap.OnMarkerClickListener,GoogleMap.OnMarkerDragListener {

    //location

    private GoogleMap mMap;
    MarkerOptions myPositionMarker;
    MarkerOptions markerHere;





    private AutoCompleteTextView mAutocompleteViewSource;
    FloatingActionButton getCurrentLocationButton;
    protected GoogleApiClient mGoogleApiClient;
    private PlaceAutocompleteAdapter mAdapter;
    private static final LatLngBounds BOUNDS_BANGLADESH = new LatLngBounds(
            new LatLng(23.549686, 90.056174 ), new LatLng(24.018418, 90.514853));


    SeekBar seekBar;
    EditText distanceText;
    //    NumberPicker numberPicker;
    List<Map<String, String>> items;
    Button testButtonPlace;
    String placeType=null;

    //for async task
    private static final String APP_ID = "AIzaSyCJIPYN7Zb1AOk3Kar9OAiSQKvNoJIXXpI";
    int pos=0;
    ProgressDialog progressDialog;
    Double lat,lon;
    LatLng positionLatlng;
    ArrayList<Place> arrayList= new ArrayList<Place>();
    StringBuilder placeList=new StringBuilder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav_drawer_place_search);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
//                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
//        drawer.setDrawerListener(toggle);
//        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);



        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, 0 /* clientId */, this)
                .addApi(Places.GEO_DATA_API)
                .build();

        mAutocompleteViewSource = (AutoCompleteTextView)
                findViewById(R.id.autocomplete_places_source_place_search_activity);
        getCurrentLocationButton = (FloatingActionButton) findViewById(R.id.current_location_search_button_place_search_activity);

        mAutocompleteViewSource.setOnItemClickListener(mAutocompleteClickListener);
        mAdapter = new PlaceAutocompleteAdapter(this, mGoogleApiClient, BOUNDS_BANGLADESH,
                null);
        mAutocompleteViewSource.setAdapter(mAdapter);






        Spinner dropdown = (Spinner)findViewById(R.id.placeTypeSpinner);
//            txt=(TextView)findViewById(R.id.demoTextView);
        seekBar=(SeekBar)findViewById(R.id.seekBarDemo);
        distanceText=(EditText)findViewById(R.id.distanceTextDemo);
//            numberPicker=(NumberPicker)findViewById(R.id.numberPicker);
        testButtonPlace=(Button)findViewById(R.id.testButtonPlace);

//            numberPicker.setMaxValue(2000);
//            numberPicker.setMinValue(100);

        progressDialog = new ProgressDialog(NavDrawerPlaceSearchActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setProgress(0);

//            numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
//                @Override
//                public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
////                    txt.setText(""+newVal);
//                    distanceText.setText(""+newVal);
//                }
//            });

        distanceText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(Double.parseDouble(distanceText.getText().toString())>2000)
                    Toast.makeText(getApplicationContext(),"Radius Cannot be more than 2000 meters.",Toast.LENGTH_LONG);

                return false;
            }
        });
        seekBar.setMax(2000);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                distanceText.setText(""+progress);
//                    numberPicker.setValue(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        getCurrentLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                // get the last know location from your location manager.
                boolean permissionGranted = ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;

                try {
                    if (permissionGranted) {
                        // {Some Code}
                        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        // now get the lat/lon from the location and do something with it.
                        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

                        List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                        LatLng latLng=new LatLng(location.getLatitude(),location.getLongitude());
                        positionLatlng=latLng;
                        String ad=addresses.get(0).getAddressLine(0)+", "+addresses.get(0).getAddressLine(1)+", "+addresses.get(0).getAdminArea()+", "+addresses.get(0).getAddressLine(2);
                        Toast.makeText(getApplicationContext(),"Current place selected",Toast.LENGTH_LONG);
                        lat=positionLatlng.latitude;
                        lon=positionLatlng.longitude;

                        myPositionMarker = new MarkerOptions().position(
                                latLng).title(addresses.get(0).getAddressLine(0));

                        myPositionMarker.icon(BitmapDescriptorFactory
                                .defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
//
                        CameraPosition cameraPosition2 = new CameraPosition.Builder()
                                .target(latLng).zoom(16).build();

                        mMap.animateCamera(CameraUpdateFactory
                                .newCameraPosition(cameraPosition2));
//
                        mMap.addMarker(myPositionMarker);

                        mAutocompleteViewSource.setText(ad);
//                        mAutocompleteViewSource.setEnabled(false);

                    } else {
                        ActivityCompat.requestPermissions(getParent(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 200);
                    }
                }catch(Exception e)
                {
                    mAutocompleteViewSource.setText(e.toString());
                }

            }
        });




        items = new ArrayList<Map<String, String>>();

        Map<String, String> item0 = new HashMap<String, String>(2);
        item0.put("text", "Bus Stations");
        item0.put("subText", "bus_station");
        items.add(item0);

        Map<String, String> item1 = new HashMap<String, String>(2);
        item1.put("text", "ATM Booths");
        item1.put("subText", "atm");
        items.add(item1);

        Map<String, String> item2 = new HashMap<String, String>(2);
        item2.put("text", "Restaurant");
        item2.put("subText", "restaurant");
        items.add(item2);

        Map<String, String> item3 = new HashMap<String, String>(2);
        item3.put("text", "Hospital");
        item3.put("subText", "hospital");
        items.add(item3);

        Map<String, String> item4 = new HashMap<String, String>(2);
        item4.put("text", "Airport");
        item4.put("subText", "airport");
        items.add(item4);

        Map<String, String> item5 = new HashMap<String, String>(2);
        item5.put("text", "Bank");
        item5.put("subText", "bank");
        items.add(item5);

        Map<String, String> item6 = new HashMap<String, String>(2);
        item6.put("text", "School");
        item6.put("subText", "school");
        items.add(item6);

        Map<String, String> item7 = new HashMap<String, String>(2);
        item7.put("text", "Police Stations");
        item7.put("subText", "police");
        items.add(item7);

        Map<String, String> item8 = new HashMap<String, String>(2);
        item8.put("text", "University");
        item8.put("subText", "university");
        items.add(item8);

        Map<String, String> item9 = new HashMap<String, String>(2);
        item9.put("text", "Train Stations");
        item9.put("subText", "train_station");
        items.add(item9);


//        for(int i=0;i<items.size();i++)
//        {
//            txt.setText(txt.getText().toString().concat(items.get(i).get("subText").toString()+"\n"));
//        }


        SimpleAdapter adapter = new SimpleAdapter(NavDrawerPlaceSearchActivity.this, items,
                android.R.layout.simple_spinner_item, // This is the layout that will be used for the standard/static part of the spinner. (You can use android.R.layout.simple_list_item_2 if you want the subText to also be shown here.)
                new String[] {"text"},
                new int[] {android.R.id.text1}
        );

// This sets the layout that will be used when the dropdown views are shown. I'm using android.R.layout.simple_list_item_2 so the subtext will also be shown.
        adapter.setDropDownViewResource(android.R.layout.simple_list_item_2);

//        spinner.setAdapter(adapter);
//
//        String[] items = new String[]{};
//            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_activated_1, items);
        dropdown.setAdapter(adapter);
        dropdown.setOnItemSelectedListener(this);
        testButtonPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                new SelectedPlaceFindBackgroundInner(txt,progressDialog).execute(""+lat,""+lon,txt.getText().toString(),distanceText.getText().toString());
//                txt.setText(arrayList.get(0).toString());
                SelectedPlaceFindBackgroundInner task=new SelectedPlaceFindBackgroundInner(progressDialog);
                task.execute(""+lat,""+lon,placeType,distanceText.getText().toString());











//                for(int i=0;i<arrayList.size();i++)
//                txt.setText(txt.getText().toString().concat(arrayList.get(i).getName()));
//                PlaceMaps.arrayList=arrayList;
                String placeListString="";
                try {
                    placeListString=task.get().toString();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }


                if(placeListString==null)
                {
                    Toast.makeText(getApplicationContext(),"Sorry! No such place found in the given region.",Toast.LENGTH_LONG);
                }

                else{

                    ArrayList<ArrayList<String>> finallist=new ArrayList<>();


                    String[] places=placeListString.split(";");
//        txt.setText(places[4]);
                    String[] separatePlaces=null;
                    for(int i=0;i<places.length;i++)
                    {
                        ArrayList<String> l=new ArrayList<String>();

                        separatePlaces=places[i].split(",");
                        l.add(separatePlaces[0]);
                        l.add(separatePlaces[1]);
                        l.add(separatePlaces[2]);
//            txt.setText(separatePlaces[2]);
                        finallist.add(l);
                    }


                    String typeIcon="pin_bus";
                    if(placeType.equals("bus_station"))
                        typeIcon="pin_bus";
                    else if(placeType.equals("atm"))
                        typeIcon="pin_atm";
                    else if(placeType.equals("restaurant"))
                        typeIcon="pin_restaurant";
                    else if(placeType.equals("hospital"))
                        typeIcon="pin_hospital";
                    else if(placeType.equals("airport"))
                        typeIcon="pin_airport";
                    else if(placeType.equals("bank"))
                        typeIcon="pin_bank";
                    else if(placeType.equals("school"))
                        typeIcon="pin_school";
                    else if(placeType.equals("police"))
                        typeIcon="pin_police_station";
                    else if(placeType.equals("university"))
                        typeIcon="pin_school";
                    else if(placeType.equals("train_station"))
                        typeIcon="pin_train";


                    Double disVal=Double.parseDouble(distanceText.getText().toString());

                    mMap.clear();
                    mMap.addCircle(new CircleOptions()
                            .center(new LatLng(lat, lon))
                            .radius(disVal)
                            .strokeColor(0x80D2D6FF)
                            .fillColor(0x60D2D6FF));
//                mMap.addMarker(markerHere);
                    mMap.addMarker(myPositionMarker);


                    Resources res = getResources();
                    int resID = res.getIdentifier(typeIcon , "drawable", getPackageName());
                    Drawable resDraw = res.getDrawable(resID );
                    for(int i=0;i<arrayList.size();i++)
                    {
                        Double lat=Double.parseDouble(finallist.get(i).get(1));
                        Double lon=Double.parseDouble(finallist.get(i).get(2));
                        LatLng latLng=new LatLng(lat,lon);
                        MarkerOptions markerBus2 = new MarkerOptions().position(
                                latLng).title(finallist.get(i).get(0));

                        markerBus2.icon(BitmapDescriptorFactory.fromResource(resID)
                        );
                        CameraPosition cameraPosition2 = new CameraPosition.Builder()
                                .target(latLng).zoom(16).build();

                        mMap.animateCamera(CameraUpdateFactory
                                .newCameraPosition(cameraPosition2));

                        mMap.addMarker(markerBus2);
                    }

                    CameraPosition cameraPosition2 = new CameraPosition.Builder()
                            .target(new LatLng(lat,lon)).zoom(14).build();

                    mMap.animateCamera(CameraUpdateFactory
                            .newCameraPosition(cameraPosition2));
                }





//                Intent intent=new Intent(NavDrawerPlaceSearchActivity.this,NavDrawerMapActivity.class);
//                intent.putExtra("placetype",placeType);
//                intent.putExtra("myplace",""+lat+","+lon);
//                intent.putExtra("placelist",placeListString);
//                startActivity(intent);
            }
        });



    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.nav_drawer_place_search, menu);
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

        if (id == R.id.nav_camera) {
            Intent intent=new Intent(NavDrawerPlaceSearchActivity.this,NavDrawerMainMenuActivity.class);
            startActivity(intent);
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

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
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//        txt.setText(items.get(position).get("subText").toString());
        placeType=items.get(position).get("subText").toString();
//        txt.setText("");
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        mMap.getUiSettings().setRotateGesturesEnabled(true);
        mMap.setTrafficEnabled(true);
        mMap.setPadding(0,0,20,240);



        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        // get the last know location from your location manager.
        boolean permissionGranted = ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;

        try {
            if (permissionGranted) {
                // {Some Code}
                Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                // now get the lat/lon from the location and do something with it.
                Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

                List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                LatLng latLng=new LatLng(location.getLatitude(),location.getLongitude());
                positionLatlng=latLng;
                lat=location.getLatitude();
                lon=location.getLongitude();
                String ad=addresses.get(0).getAddressLine(0)+", "+addresses.get(0).getAddressLine(1)+", "+addresses.get(0).getAdminArea()+", "+addresses.get(0).getAddressLine(2);
                Toast.makeText(getApplicationContext(),"Current place selected",Toast.LENGTH_LONG);

                myPositionMarker = new MarkerOptions().position(
                        latLng).title(addresses.get(0).getAddressLine(0));

                myPositionMarker.icon(BitmapDescriptorFactory
                        .defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
//
                CameraPosition cameraPosition2 = new CameraPosition.Builder()
                        .target(latLng).zoom(16).build();

                mMap.animateCamera(CameraUpdateFactory
                        .newCameraPosition(cameraPosition2));
//
                mMap.addMarker(myPositionMarker);
                mAutocompleteViewSource.setText(ad);

            } else {
                ActivityCompat.requestPermissions(getParent(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 200);
            }
        }catch(Exception e)
        {
            Toast.makeText(getApplicationContext(),e.toString(),Toast.LENGTH_SHORT);
        }


        mMap.setOnMapLongClickListener(this);
        mMap.setOnMarkerClickListener(this);
    }

    @Override
    public void onMapLongClick(LatLng latLng) {

        mMap.clear();
        mMap.addMarker(myPositionMarker);
        markerHere = new MarkerOptions().position(
                latLng).title("Source");

        myPositionMarker.icon(BitmapDescriptorFactory
                .defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        markerHere.draggable(true);
        mMap.addMarker(markerHere);

        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

        try {
            List<Address> addresses = geocoder.getFromLocation(markerHere.getPosition().latitude,markerHere.getPosition().longitude, 1);
            String ad=addresses.get(0).getAddressLine(0)+", "+addresses.get(0).getAddressLine(1)+", "+addresses.get(0).getAdminArea()+", "+addresses.get(0).getAddressLine(2);
            mAutocompleteViewSource.setText(ad);
            lat=markerHere.getPosition().latitude;
            lon=markerHere.getPosition().longitude;
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    @Override
    public boolean onMarkerClick(Marker marker) {

        final LatLng markerLatLng=marker.getPosition();


        List<Address> addressesDest = null;
        List<Address> addressesSource = null;
        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

        try {
            addressesDest = geocoder.getFromLocation(markerLatLng.latitude, markerLatLng.longitude, 1);
            addressesSource = geocoder.getFromLocation(lat, lon, 1);

        }catch (IOException e) {
            e.printStackTrace();
        }


            final AlertDialog.Builder alertDialog = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.myDialog));
        alertDialog.setTitle(marker.getTitle());
        alertDialog.setMessage(marker.getId()+"\n\nDo you want directions to this place?");

        final List<Address> finalAddressesSource = addressesSource;
        final List<Address> finalAddressesDest = addressesDest;
        alertDialog.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        NavDrawerMapWithDirectionActivity.source = finalAddressesSource.get(0).getAddressLine(0);
                        NavDrawerMapWithDirectionActivity.source2 = finalAddressesDest.get(0).getAddressLine(0);
                        NavDrawerMapWithDirectionActivity.s1 = new LatLng(lat,lon);
                        NavDrawerMapWithDirectionActivity.s2 = markerLatLng;
                        mAutocompleteViewSource.setText("");
                        distanceText.setText("0");
                        seekBar.setProgress(0);
                        Intent intent = new Intent(NavDrawerPlaceSearchActivity.this,NavDrawerMapWithDirectionActivity.class);
                        startActivity(intent);


                        // continue with delete
                    }
                });
                alertDialog.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                // do nothing
                            }
                        });

        alertDialog.show();
//        new AlertDialog.Builder(getApplicationContext())
//                .setTitle("Marker Clicked")
//                .setMessage("Do you want directions to this place?")
//                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                        // continue with delete
//                    }
//                })
//                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                        // do nothing
//                    }
//                })
//                .setIcon(android.R.drawable.ic_dialog_alert)
//                .show();



//        Toast.makeText(getApplicationContext(),"Clicked",Toast.LENGTH_LONG);

        return false;
    }

    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {


        LatLng latLng=marker.getPosition();
        markerHere = new MarkerOptions().position(
                latLng).title("Source");

        myPositionMarker.icon(BitmapDescriptorFactory
                .defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        markerHere.draggable(true);

        mMap.addMarker(markerHere);

        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

        try {
            List<Address> addresses = geocoder.getFromLocation(markerHere.getPosition().latitude,markerHere.getPosition().longitude, 1);
            String ad=addresses.get(0).getAddressLine(0)+", "+addresses.get(0).getAddressLine(1)+", "+addresses.get(0).getAdminArea()+", "+addresses.get(0).getAddressLine(2);
            mAutocompleteViewSource.setText(ad);
            lat=markerHere.getPosition().latitude;
            lon=markerHere.getPosition().longitude;
        } catch (IOException e) {
            e.printStackTrace();
        }



    }


    class SelectedPlaceFindBackgroundInner extends AsyncTask<String, Void, String> {
        //        private TextView textView;
        private LatLng latLng;
        int pos=0;
        Geocoder geocoder;
        Context context;
        private static final String APP_ID = "AIzaSyCJIPYN7Zb1AOk3Kar9OAiSQKvNoJIXXpI";
        Info durationinfo,distanceinfo;

        String jsonData=null;
        public SelectedPlaceFindBackgroundInner(ProgressDialog progressDialog) {
//            this.textView = textView;
//        this.latLng=latLng;
        }

        @Override
        protected String doInBackground(String... strings) {
//        jsonData=getUrlContents(strings[0]);
            Double lat=Double.parseDouble(strings[0]);
            Double lon=Double.parseDouble(strings[1]);
            Double distance=Double.parseDouble(strings[3]);
            return findPlaces(lat,lon,strings[2],distance);

        }
        protected String getJSON(String url) {
            return getUrlContents(url);
        }

        public String findPlaces(double latitude, double longitude, String placeSpacification,Double distance2)
        {
            StringBuilder content = new StringBuilder();
            String urlString = makeUrl(latitude, longitude,placeSpacification,distance2);

            String json = getJSON(urlString);


            JSONObject object = null;
            try {
                object = new JSONObject(json);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            JSONArray array = null;
            try {
                array = object.getJSONArray("results");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            LatLng origin=new LatLng(latitude,longitude);

            arrayList.clear();
            for (int i = 0; i < array.length(); i++) {
                try {
                    Place place = Place.jsonToPontoReferencia((JSONObject) array.get(i));
                    String placeName=place.getName();
                    Log.v("Places Services ", ""+place);

                    content.append(""+placeName+"\n");
                    arrayList.add(place);
                } catch (Exception e) {
                }
            }

            double min=1000;

            for(int i=0;i<arrayList.size();i++)
            {
                LatLng destination=new LatLng(arrayList.get(i).getLatitude(),arrayList.get(i).getLongitude());

                Location frst=new Location("");
                frst.setLatitude(latitude);
                frst.setLongitude(longitude);

                Location last=new Location("");
                last.setLatitude(arrayList.get(i).getLatitude());
                last.setLongitude(arrayList.get(i).getLongitude());

                double distance=frst.distanceTo(last);
                if(distance<min)
                {min=distance;
                    pos=i;}
            }

            placeList.delete(0,placeList.length());
//            textView.setText("");
            for(int i=0;i<arrayList.size();i++)
            {
                placeList.append(arrayList.get(i).getName()+",");
                placeList.append(arrayList.get(i).getLatitude()+",");
                placeList.append(arrayList.get(i).getLongitude()+";");
//                textView.setText(txt.getText().toString().concat(arrayList.get(i).getName()+","+arrayList.get(i).getLatitude()+","+arrayList.get(i).getLongitude()+"\n"));
            }



//        content.append("Distance: "+min+"\n\n");
//            String resultCoordinates=arrayList.get(pos).getLatitude()+","+arrayList.get(pos).getLongitude();
            return placeList.toString();
        }





        private String getUrlContents(String theUrl)
        {
            String weather = theUrl;
            try {
                URL url = new URL(theUrl);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                InputStream stream = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream));
                StringBuilder builder = new StringBuilder();
                StringBuilder content = new StringBuilder();

                String inputString;
                String line;
                while ((line = bufferedReader.readLine()) != null)
                {
                    content.append(line + "\n");
                }


//            while ((inputString = bufferedReader.readLine()) != null) {
//                builder.append(inputString);
//            }
                weather=content.toString();

                urlConnection.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return weather;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(String temp) {

//        latLng=new LatLng(arrayList.get(pos).getLatitude(),arrayList.get(pos).getLongitude());
//            StringBuilder placeList=new StringBuilder();

            progressDialog.dismiss();
        }



        private String makeUrl(double latitude, double longitude,String place,Double distance) {
            StringBuilder urlString = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
//        StringBuilder urlString = new StringBuilder("https://www.google.com");

            if (place.equals("")) {
                urlString.append("location=");
                urlString.append(Double.toString(latitude));
                urlString.append(",");
                urlString.append(Double.toString(longitude));
                urlString.append("&radius="+distance);
                //   urlString.append("&types="+place);
                urlString.append("&key=" + APP_ID+"&sensor=true");
            } else {
                urlString.append("location=");
                urlString.append(Double.toString(latitude));
                urlString.append(",");
                urlString.append(Double.toString(longitude));
                urlString.append("&radius="+distance);
                urlString.append("&types="+place);
                urlString.append("&key=" + APP_ID+"&sensor=true");
            }

            return urlString.toString();
        }
    }





    //Expand Layout


    public class DropDownAnim extends Animation {
        private final int targetHeight;
        private final View view;
        private final boolean down;

        public DropDownAnim(View view, int targetHeight, boolean down) {
            this.view = view;
            this.targetHeight = targetHeight;
            this.down = down;
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            int newHeight;
            if (down) {
                newHeight = (int) (targetHeight * interpolatedTime);
            } else {
                newHeight = (int) (targetHeight * (1 - interpolatedTime));
            }
            view.getLayoutParams().height = newHeight;
            view.requestLayout();
        }

        @Override
        public void initialize(int width, int height, int parentWidth,
                               int parentHeight) {
            super.initialize(width, height, parentWidth, parentHeight);
        }

        @Override
        public boolean willChangeBounds() {
            return true;
        }
    }






    //PlaceSearchSuggestion
    private AdapterView.OnItemClickListener mAutocompleteClickListener
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            /*
             Retrieve the place ID of the selected item from the Adapter.
             The adapter stores each Place suggestion in a AutocompletePrediction from which we
             read the place ID and title.
              */
//            mAutocompleteViewSource.setEnabled(true);
            final AutocompletePrediction item = mAdapter.getItem(position);
            final String placeId = item.getPlaceId();
            final CharSequence primaryText = item.getPrimaryText(null);
//            mAutocompleteViewDest.setText(parent.toString());
//            Log.i(TAG, "Autocomplete item selected: " + primaryText);

            /*
             Issue a request to the Places Geo Data API to retrieve a Place object with additional
             details about the place.
              */
            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                    .getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);


//                Toast.makeText(getApplicationContext(), "Clicked: " + primaryText,
//                        Toast.LENGTH_SHORT).show();


        }

    };

    /**
     * Callback for results from a Places Geo Data API query that shows the first place result in
     * the details view on screen.
     */
    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback
            = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                // Request did not complete successfully
//                Log.e(TAG, "Place query did not complete. Error: " + places.getStatus().toString());
                places.release();
                return;
            }
            // Get the Place object from the buffer.
            final com.google.android.gms.location.places.Place place = places.get(0);

//          android.location.Address address= (Address) place.getAddress();
            String fullName = place.getName() + "";
            LatLng latLng = place.getLatLng();
            positionLatlng=place.getLatLng();
            lat=positionLatlng.latitude;
            lon=positionLatlng.longitude;

//            sample2.setText(fullName);
            places.release();
        }
    };

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

        // TODO(Developer): Check error code and notify the user of error state and resolution.
        Toast.makeText(this,
                "Could not connect to Google API Client: Error " + connectionResult.getErrorCode(),
                Toast.LENGTH_SHORT).show();
    }














}
