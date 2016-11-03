package com.example.aabir.metravv2;

/**
 * Created by aabir on 6/17/2016.
 */

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;


import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.List;
import java.util.Locale;

import common.activities.SampleActivityBase;

public class TestAcitivity extends SampleActivityBase implements GoogleApiClient.OnConnectionFailedListener {

    /**
     * GoogleApiClient wraps our service connection to Google Play Services and provides access
     * to the user's sign in state as well as the Google's APIs.
     */
    protected GoogleApiClient mGoogleApiClient;

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
    ImageButton getCurrentLocationButton;
    Button findPlacesButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Construct a GoogleApiClient for the {@link Places#GEO_DATA_API} using AutoManage
        // functionality, which automatically sets up the API client to handle Activity lifecycle
        // events. If your activity does not extend FragmentActivity, make sure to call connect()
        // and disconnect() explicitly.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, 0 /* clientId */, this)
                .addApi(Places.GEO_DATA_API)
                .build();

        setContentView(R.layout.place_autocomplete_test);

        // Retrieve the AutoCompleteTextView that will display Place suggestions.
        mAutocompleteViewSource = (AutoCompleteTextView)
                findViewById(R.id.autocomplete_places_source);
        mAutocompleteViewDest = (AutoCompleteTextView) findViewById(R.id.autocomplete_places_dest);
        getDirecttions = (Button) findViewById(R.id.getDirectionButton1);
        getCurrentLocationButton = (ImageButton) findViewById(R.id.current_location_search_button);


        //findplacesbutton
        findPlacesButton=(Button)findViewById(R.id.findPlacesButton);
        findPlacesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(TestAcitivity.this,PlaceSearchActivity.class);
                startActivity(intent);
            }
        });



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
                        Geocoder  geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

                        List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                        LatLng latLng=new LatLng(location.getLatitude(),location.getLongitude());
                        s1=latLng;
                                String ad=addresses.get(0).getAddressLine(0)+", "+addresses.get(0).getAddressLine(1)+", "+addresses.get(0).getAdminArea()+", "+addresses.get(0).getAddressLine(2);
                        Toast.makeText(getApplicationContext(),"Current place selected",Toast.LENGTH_LONG);

                        mAutocompleteViewSource.setText(ad);
                        mAutocompleteViewSource.setEnabled(false);

                    } else {
                        ActivityCompat.requestPermissions(getParent(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 200);
                    }
                }catch(Exception e)
                {
                    mAutocompleteViewSource.setText(e.toString());
                }




//                nowDoSomethingWith(location.getLatitude(), location.getLongitude());
            }
        });


        getDirecttions = (Button) findViewById(R.id.getDirectionButton1);
        getDirecttions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MapsActivity.source = source;
                MapsActivity.source2 = dest;
                MapsActivity.s1 = s1;
                MapsActivity.s2 = s2;
                MapsActivity2.origin = s1;
                MapsActivity2.destination = s2;
                Intent intent = new Intent(TestAcitivity.this, MapsActivity.class);
                startActivity(intent);
            }
        });


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
            final Place place = places.get(0);

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


}
