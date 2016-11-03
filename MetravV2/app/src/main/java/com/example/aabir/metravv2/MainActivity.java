package com.example.aabir.metravv2;

import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.MapsInitializer;

public class MainActivity extends AppCompatActivity {
Button getDirectionButton;
String source,source2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
      //  source=(EditText)findViewById(R.id.sourceText);
        getDirectionButton=(Button)findViewById(R.id.getDirectionButton);
        getDirectionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MapsActivity.source=source;
                MapsActivity.source2=source2;
                Intent intent=new Intent(MainActivity.this,MapsActivity.class);
                startActivity(intent);
            }
        });
        final PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                autocompleteFragment.getView().setBackground(getDrawable(R.drawable.graybox));
//                autocompleteFragment.setText("Source");
                autocompleteFragment.setHint("Source");
            }
        }
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                // Log.i(TAG, "Place: " + place.getName());
                autocompleteFragment.setText(place.getName());
                source=""+place.getName();
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                //   Log.i(TAG, "An error occurred: " + status);
            }
        });
        final PlaceAutocompleteFragment autocompleteFragment2 = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment2);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                autocompleteFragment2.getView().setBackground(getDrawable(R.drawable.graybox));
//                autocompleteFragment.setText("Source");
                autocompleteFragment2.setHint("Destination");
            }
        }
        autocompleteFragment2.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                // Log.i(TAG, "Place: " + place.getName());
                autocompleteFragment2.setText(place.getName());
                source2=""+place.getName();
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                //   Log.i(TAG, "An error occurred: " + status);
            }
        });
    }


}
