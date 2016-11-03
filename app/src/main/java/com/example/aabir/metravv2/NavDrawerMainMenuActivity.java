package com.example.aabir.metravv2;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.*;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;
import java.util.Locale;

public class NavDrawerMainMenuActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,GoogleApiClient.OnConnectionFailedListener,OnMapReadyCallback {


    protected GoogleApiClient mGoogleApiClient;
    private GoogleMap mMap;
    static ProgressDialog progressDialog;

    private PlaceAutocompleteAdapter mAdapter;
    private PlaceAutocompleteAdapter mAdapterDest;

    private AutoCompleteTextView mAutocompleteViewSource;
    private AutoCompleteTextView mAutocompleteViewDest;
    private Button getDirecttions;
    public LatLng s1, s2;
    private static final LatLngBounds BOUNDS_BANGLADESH = new LatLngBounds(
            new LatLng(23.549686, 90.056174 ), new LatLng(24.018418, 90.514853));
    public String source, dest;
    Context context = this;
    FloatingActionButton getCurrentLocationButton;
//    Button findPlacesButton;



    TextView headerText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav_drawer_main_menu);

        progressDialog = new ProgressDialog(NavDrawerMainMenuActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Getting your directions....");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setProgress(0);


//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);


//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
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

        // Retrieve the AutoCompleteTextView that will display Place suggestions.
        mAutocompleteViewSource = (AutoCompleteTextView)
                findViewById(R.id.autocomplete_places_source);
        mAutocompleteViewDest = (AutoCompleteTextView) findViewById(R.id.autocomplete_places_dest);
        getDirecttions = (Button) findViewById(R.id.getDirectionButton1);
        getCurrentLocationButton = (FloatingActionButton) findViewById(R.id.current_location_search_button);

            progressDialog.dismiss();

//        Bundle extra = getIntent().getExtras();
//        if(extra != null) {
//            String value = extra.getString("name");
//            TextView headerText=(TextView)findViewById(R.id.navHeaderText);
//            headerText.setText(value);
//        }
//        Bundle bundle = null;
//        bundle = this.getIntent().getExtras();
//        String myString = bundle.getString("Name");//this is for String
//headerText=(TextView)findViewById(R.id.textView);
//            headerText.setText("Aabir");

//        mAutocompleteViewDest.setText(myString);
//        Log.d("Name is:", myString);


        //findplacesbutton
//        findPlacesButton=(Button)findViewById(R.id.findPlacesButton);
//        findPlacesButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent=new Intent(NavDrawerMainMenuActivity.this,NavDrawerPlaceSearchActivity.class);
//                startActivity(intent);
//            }
//        });



        // Register a listener that receives callbacks when a suggestion has been selected

        mAutocompleteViewSource.setOnItemClickListener(mAutocompleteClickListener);
        mAutocompleteViewDest.setOnItemClickListener(mAutocompleteClickListener);

        // Retrieve the TextViews that will display details and attributions of the selected place.
//        mPlaceDetailsText = (TextView) findViewById(R.id.place_details);
//        mPlaceDetailsAttribution = (TextView) findViewById(R.id.place_attribution);

        // Set up the adapter that will retrieve suggestions from the Places Geo Data API that cover
        // the entire world.
        mAdapter = new PlaceAutocompleteAdapter(this, mGoogleApiClient, BOUNDS_BANGLADESH,
                null);
        mAdapterDest = new PlaceAutocompleteAdapter(this, mGoogleApiClient, BOUNDS_BANGLADESH,
                null);
        mAutocompleteViewSource.setAdapter(mAdapter);
        mAutocompleteViewDest.setAdapter(mAdapter);

        getCurrentLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // instantiate the location manager, note you will need to request permissions in your manifest
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
                        s1=latLng;
                        String ad=addresses.get(0).getAddressLine(0)+", "+addresses.get(0).getAddressLine(1)+", "+addresses.get(0).getAdminArea()+", "+addresses.get(0).getAddressLine(2);
                        Toast.makeText(getApplicationContext(),"Current place selected",Toast.LENGTH_LONG);
                        MarkerOptions markerMe = new MarkerOptions().position(
                                latLng).title("You are here").snippet(addresses.get(0).getAddressLine(0));

                        markerMe.icon(BitmapDescriptorFactory.fromResource(R.drawable.locationicon2)
                        );
//
                        CameraPosition cameraPosition2 = new CameraPosition.Builder()
                                .target(latLng).zoom(16).build();

                        mMap.animateCamera(CameraUpdateFactory
                                .newCameraPosition(cameraPosition2));
//
                        mMap.addMarker(markerMe);


                        mAutocompleteViewSource.setText(ad);
//                        mAutocompleteViewSource.setEnabled(false);

                    } else {
                        ActivityCompat.requestPermissions(getParent(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 200);
                    }
                }catch(Exception e)
                {
                    mAutocompleteViewSource.setText(e.toString());
                }




//                nowDoSomethingWith(location.getLatitude(), location.getLongitude());
            }
        }
        );


        getDirecttions = (Button) findViewById(R.id.getDirectionButton1);
        getDirecttions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               NavDrawerMapWithDirectionActivity.source = source;
               NavDrawerMapWithDirectionActivity.source2 = dest;
               NavDrawerMapWithDirectionActivity.s1 = s1;
               NavDrawerMapWithDirectionActivity.s2 = s2;
                MapsActivity2.origin = s1;
                MapsActivity2.destination = s2;
                progressDialog.show();

                Intent intent = new Intent(NavDrawerMainMenuActivity.this,NavDrawerMapWithDirectionActivity.class);

                startActivity(intent);
            }
        });

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
            onDestroy();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.nav_drawer_main_menu, menu);
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

        } else if (id == R.id.find_place_screen) {

            Intent intent=new Intent(NavDrawerMainMenuActivity.this,NavDrawerPlaceSearchActivity.class);
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




    /**
     * Listener that handles selections from suggestions from the AutoCompleteTextView that
     * displays Place suggestions.
     * Gets the place id of the selected item and issues a request to the Places Geo Data API
     * to retrieve more details about the place.
     *
     * @see com.google.android.gms.location.places.GeoDataApi#getPlaceById(com.google.android.gms.common.api.GoogleApiClient,
     * String...)
     */
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
            if (mAutocompleteViewSource.getText().toString().matches("(.*)" + fullName + "(.*)"))
//            Toast.makeText(getApplicationContext(),latLng.toString(),Toast.LENGTH_LONG);
            {
                s1 = place.getLatLng();
                source = "" + fullName;
            } else if (mAutocompleteViewDest.getText().toString().matches("(.*)" + fullName + "(.*)"))
                ;
            {
                s2 = place.getLatLng();
                dest = "" + fullName;
            }
//            sample2.setText(fullName);
            places.release();
        }
    };

    /**
     * Called when the Activity could not connect to Google Play services and the auto manager
     * could resolve the error automatically.
     * In this case the API is not available and notify the user.
     *
     * @param connectionResult can be inspected to determine the cause of the failure
     */
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

//        Log.e(TAG, "onConnectionFailed: ConnectionResult.getErrorCode() = "
//                + connectionResult.getErrorCode());

        // TODO(Developer): Check error code and notify the user of error state and resolution.
        Toast.makeText(this,
                "Could not connect to Google API Client: Error " + connectionResult.getErrorCode(),
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap=googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        mMap.getUiSettings().setRotateGesturesEnabled(true);
        mMap.setTrafficEnabled(true);
        mMap.setPadding(0,0,20,250);




        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean permissionGranted = ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;

        try {
            if (permissionGranted) {
                // {Some Code}
                Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                // now get the lat/lon from the location and do something with it.
                Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

                List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                LatLng latLng=new LatLng(location.getLatitude(),location.getLongitude());

                String ad=addresses.get(0).getAddressLine(0)+", "+addresses.get(0).getAddressLine(1)+", "+addresses.get(0).getAdminArea()+", "+addresses.get(0).getAddressLine(2);

                MarkerOptions markerMe = new MarkerOptions().position(
                        latLng).title("You are here").snippet(addresses.get(0).getAddressLine(0));

                markerMe.icon(BitmapDescriptorFactory.fromResource(R.drawable.locationicon2)
                );
//
                CameraPosition cameraPosition2 = new CameraPosition.Builder()
                        .target(latLng).zoom(14).build();

                mMap.animateCamera(CameraUpdateFactory
                        .newCameraPosition(cameraPosition2));
//
                mMap.addMarker(markerMe);

            } else {
                ActivityCompat.requestPermissions(getParent(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 200);
            }
        }catch(Exception e)
        {
//            mAutocompleteViewSource.setText(e.toString());
        }



    }
}
