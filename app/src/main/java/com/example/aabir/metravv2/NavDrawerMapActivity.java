package com.example.aabir.metravv2;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class NavDrawerMapActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,OnMapReadyCallback,GoogleMap.OnMarkerClickListener,GoogleMap.OnMapLongClickListener {


    private GoogleMap mMap;
    String placeType="bus_station";
//    TextView txt;
    ArrayList<Place> arrayList;
    ArrayList<ArrayList<String>> finallist=new ArrayList<>();
    LatLng myPlaceLatLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav_drawer_map);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
//                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
//        drawer.setDrawerListener(toggle);
//        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        String newString;
        String myPlacelatlngString=null;

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                newString= null;
            } else {
                newString= extras.getString("placelist");
                placeType=extras.getString("placetype");
                myPlacelatlngString=extras.getString("myplace");
            }
        } else {
            newString= (String) savedInstanceState.getSerializable("placelist");
            placeType= (String) savedInstanceState.getSerializable("placetype");
            myPlacelatlngString=(String)savedInstanceState.getSerializable("myplace");
        }
//        arrayList =  (ArrayList<Place>)getIntent().getSerializableExtra("placelist");

//        txt=(TextView)findViewById(R.id.arrayListTextActivityMaps);
//        txt.setText(newString);

        String[] myCorordinates=myPlacelatlngString.split(",");
        Double myLat=Double.parseDouble(myCorordinates[0]);
        Double myLon=Double.parseDouble(myCorordinates[1]);

        myPlaceLatLng=new LatLng(myLat,myLon);


        String[] places=newString.split(";");
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



        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            Intent intent=new Intent(NavDrawerMapActivity.this,NavDrawerMainMenuActivity.class);
            startActivity(intent);
//            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.nav_drawer_map, menu);
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
            Intent intent=new Intent(NavDrawerMapActivity.this,NavDrawerMainMenuActivity.class);
            startActivity(intent);
            // Handle the camera action
        } else if (id == R.id.find_place_screen) {
            Intent intent=new Intent(NavDrawerMapActivity.this,NavDrawerPlaceSearchActivity.class);
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
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        mMap.getUiSettings().setRotateGesturesEnabled(true);
        mMap.setTrafficEnabled(true);

//        String typeIcon=placeType;
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







        Resources res = getResources();
        int resID = res.getIdentifier(typeIcon , "drawable", getPackageName());
        Drawable resDraw = res.getDrawable(resID );
        for(int i=0;i<finallist.size();i++)
        {
            Double lat=Double.parseDouble(finallist.get(i).get(1));
            Double lon=Double.parseDouble(finallist.get(i).get(2));
            LatLng latLng=new LatLng(lat,lon);
//            txt.setText(txt.getText().toString().concat(latLng.toString()+"\n"));
            MarkerOptions markerBus2 = new MarkerOptions().position(
                    latLng).title(finallist.get(i).get(0));

            markerBus2.icon(BitmapDescriptorFactory.fromResource(resID)
            );
//
            CameraPosition cameraPosition2 = new CameraPosition.Builder()
                    .target(latLng).zoom(16).build();

            mMap.animateCamera(CameraUpdateFactory
                    .newCameraPosition(cameraPosition2));
//
            mMap.addMarker(markerBus2);
        }

        MarkerOptions markerMe = new MarkerOptions().position(
                myPlaceLatLng).title("You are here!");

        markerMe.icon(BitmapDescriptorFactory.fromResource(R.drawable.locationicon2)
        );
//
        CameraPosition cameraPosition2 = new CameraPosition.Builder()
                .target(myPlaceLatLng).zoom(16).build();

        mMap.animateCamera(CameraUpdateFactory
                .newCameraPosition(cameraPosition2));
//
        mMap.addMarker(markerMe);



        mMap.setOnMarkerClickListener(this);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {


        Place place=new Place();
        place.setId(marker.getId());
        String placeIcon=place.getIcon();
        final Dialog dlg;
        dlg=new Dialog(NavDrawerMapActivity.this,android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);


        LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
        View v = inflater.inflate(R.layout.popupinfo, null);

        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dlg.setContentView(v);
        ImageView placeImage=(ImageView)v.findViewById(R.id.placeImage);



        TextView txtViewInfo=(TextView)v.findViewById(R.id.txtViewInfo);
        txtViewInfo.setText(marker.getTitle().toString());
        txtViewInfo.setText(txtViewInfo.getText().toString().concat(placeIcon));
        Button closeBtn=(Button)findViewById(R.id.close_popup);
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dlg.dismiss();
            }
        });
        dlg.show();

        return false;
    }

    @Override
    public void onMapLongClick(LatLng latLng) {

    }
}
