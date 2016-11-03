package com.example.aabir.metravv2;

/**
 * Created by aabir on 8/10/2016.
 */
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;


public class PlacesService extends Activity{
    private static final String TAG = PlacesService.class.getSimpleName();
    TextView nearbyText;
    private String API_KEY="AIzaSyAyHMjav5_HgnJD3LGanbLo65XT5C256I8";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.nearby_place);
        nearbyText=(TextView)findViewById(R.id.nearbyText);
        nearbyText.setText("Something");
        new GetPlaces().execute();
    }

    public void setApiKey(String apikey) {
        this.API_KEY = apikey;
    }

    public List<Place> findPlaces(double latitude, double longitude,String placeSpacification)
    {

        String urlString = makeUrl(latitude, longitude,placeSpacification);
        nearbyText.setText(urlString);

        String json = getJSON(urlString);
//        else
        nearbyText.setText(json);
//
//        System.out.println(json);

//            JSONObject object = new JSONObject(json);
//            JSONArray array = object.getJSONArray("results");
//
//
//            ArrayList<Place> arrayList = new ArrayList<Place>();
//            for (int i = 0; i < array.length(); i++) {
//                try {
//                    Place place = Place.jsonToPontoReferencia((JSONObject) array.get(i));
//
//                    Log.v("Places Services ", ""+place);
//
//
//                    arrayList.add(place);
//                } catch (Exception e) {
//                }
//            }
        return null;
    }
    //location=28.632808,77.218276&radius=5000&key=AIzaSyCJIPYN7Zb1AOk3Kar9OAiSQKvNoJIXXpI&sensor=true
    //https://maps.googleapis.com/maps/api/place/search/json?location=28.632808,77.218276&radius=500&types=atm&sensor=false&key=<key>
    private String makeUrl(double latitude, double longitude,String place) {
        StringBuilder urlString = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
//        StringBuilder urlString = new StringBuilder("https://www.google.com");

        if (place.equals("")) {
            urlString.append("location=");
            urlString.append(Double.toString(latitude));
            urlString.append(",");
            urlString.append(Double.toString(longitude));
            urlString.append("&radius=1000");
            //   urlString.append("&types="+place);
            urlString.append("&key=" + API_KEY+"&sensor=true");
        } else {
            urlString.append("location=");
            urlString.append(Double.toString(latitude));
            urlString.append(",");
            urlString.append(Double.toString(longitude));
            urlString.append("&radius=1000");
            urlString.append("&types="+place);
            urlString.append("&key=" + API_KEY+"&sensor=true");
        }

        return urlString.toString();
    }

    protected String getJSON(String url) {
        return getUrlContents(url);
    }

    private String getUrlContents(String theUrl)
    {
        StringBuilder content = new StringBuilder();
        nearbyText.setText("code is here");
        try {
            URL url = new URL(theUrl);
//nearbyText.setText(url.toString());

//            URLConnection urlConnection = url.openConnection();
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.connect();
            InputStream is = conn.getInputStream();
            BufferedReader reader =new BufferedReader(new InputStreamReader(is, "UTF-8"));
//            nearbyText.setText(theUrl);
//            Log.d(TAG, "onCreate() Restoring previous state");

//            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(),"iso-8859-1"));
//            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(url.openStream()));
//            nearbyText.setText("code is here");
            nearbyText.setText("dsgdfg");
            String line;
            while ((line = reader.readLine()) != null)
            {
                content.append(line + "\n");
            }
            nearbyText.setText(content);
            reader.close();
        }

        catch (Exception e)
        {
            nearbyText.setText(nearbyText.getText().toString().concat("\n\nError: "+e.toString()));
        }

        return content.toString();
    }

    class GetPlaces extends AsyncTask<Void, Void, Void> {
        Context context;
        private ListView listView;
        public GetPlaces() {
            // TODO Auto-generated constructor stub
        }

        @Override
        protected void onPostExecute(Void result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
//            this.listView.setAdapter(new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, placeName));

        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();


        }

        @Override
        protected Void doInBackground(Void... arg0) {
            // TODO Auto-generated method stub
//            findPlaces(28.632808,77.218276,"");
            getUrlContents("https://www.google.com");

            return null;
        }

    }








}
