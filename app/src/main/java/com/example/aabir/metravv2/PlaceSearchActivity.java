package com.example.aabir.metravv2;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.SeekBar;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ZoomControls;

import com.akexorcist.googledirection.model.Info;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * Created by aabir on 8/24/2016.
 */
public class PlaceSearchActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{
//    TextView txt;
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
    ArrayList<Place> arrayList= new ArrayList<Place>();
    StringBuilder placeList=new StringBuilder();



    @Override
        protected void onCreate(Bundle savedInstanceState){
            super.onCreate(savedInstanceState);
            setContentView(R.layout.place_search);
            Spinner dropdown = (Spinner)findViewById(R.id.placeTypeSpinner);
//            txt=(TextView)findViewById(R.id.demoTextView);
            seekBar=(SeekBar)findViewById(R.id.seekBarDemo);
            distanceText=(EditText)findViewById(R.id.distanceTextDemo);
//            numberPicker=(NumberPicker)findViewById(R.id.numberPicker);
            testButtonPlace=(Button)findViewById(R.id.testButtonPlace);

//            numberPicker.setMaxValue(2000);
//            numberPicker.setMinValue(100);

        progressDialog = new ProgressDialog(PlaceSearchActivity.this);
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



//        for(int i=0;i<items.size();i++)
//        {
//            txt.setText(txt.getText().toString().concat(items.get(i).get("subText").toString()+"\n"));
//        }


        SimpleAdapter adapter = new SimpleAdapter(PlaceSearchActivity.this, items,
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
//                txt.setText(placeListString);
//                    txt.setText(txt.getText().toString().concat(arrayList.get(i).getName()));
                Intent intent=new Intent(PlaceSearchActivity.this,NavDrawerMapActivity.class);
                intent.putExtra("placelist",placeListString);
                startActivity(intent);
            }
        });
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











}
