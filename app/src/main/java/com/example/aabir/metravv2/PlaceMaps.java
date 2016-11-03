package com.example.aabir.metravv2;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class PlaceMaps extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    TextView txt;
     ArrayList<Place> arrayList;
    ArrayList<ArrayList<String>> finallist=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_maps);
//        arrayList=new ArrayList<Place>();
        String newString;
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                newString= null;
            } else {
                newString= extras.getString("placelist");
            }
        } else {
            newString= (String) savedInstanceState.getSerializable("placelist");
        }
//        arrayList =  (ArrayList<Place>)getIntent().getSerializableExtra("placelist");

        txt=(TextView)findViewById(R.id.arrayListTextActivityMaps);
//        txt.setText(newString);

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

//        txt.setText(finallist.get(18).get(2));

//        MarkerOptions[] marker=new MarkerOptions[100];

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        mMap.getUiSettings().setRotateGesturesEnabled(true);
        mMap.setTrafficEnabled(true);

        for(int i=0;i<finallist.size();i++)
        {
            Double lat=Double.parseDouble(finallist.get(i).get(1));
            Double lon=Double.parseDouble(finallist.get(i).get(2));
            LatLng latLng=new LatLng(lat,lon);
            txt.setText(txt.getText().toString().concat(latLng.toString()+"\n"));
            MarkerOptions markerBus2 = new MarkerOptions().position(
                    latLng).title(finallist.get(i).get(0));

            markerBus2.icon(BitmapDescriptorFactory.fromResource(R.drawable.busicon)
            );
//
            CameraPosition cameraPosition2 = new CameraPosition.Builder()
                    .target(latLng).zoom(16).build();

            mMap.animateCamera(CameraUpdateFactory
                    .newCameraPosition(cameraPosition2));
//
            mMap.addMarker(markerBus2);
        }

    }
}
