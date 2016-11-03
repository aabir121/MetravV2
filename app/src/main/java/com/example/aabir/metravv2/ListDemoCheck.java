package com.example.aabir.metravv2;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by aabir on 10/7/2016.
 */
public class ListDemoCheck extends AppCompatActivity {

    ListView list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listdemocheck);

        list=(ListView)findViewById(R.id.listviewdemo);

        String[] values = new String[] { "Android", "iPhone", "WindowsMobile",
                "Blackberry", "WebOS", "Ubuntu", "Windows7", "Max OS X",
                "Linux", "OS/2", "Ubuntu", "Windows7", "Max OS X", "Linux",
                "OS/2", "Ubuntu", "Windows7", "Max OS X", "Linux", "OS/2",
                "Android", "iPhone", "WindowsMobile" };

        final ArrayList<String> listarr = new ArrayList<String>();
        for (int i = 0; i < values.length; ++i) {
            listarr.add(values[i]);
        }
        final ArrayAdapter adapter = new ArrayAdapter(this,
                android.R.layout.simple_list_item_1, listarr);
        list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                final String item = (String) parent.getItemAtPosition(position);
                Toast.makeText(getBaseContext(),item,Toast.LENGTH_LONG).show();
//                view.animate().setDuration(2000).alpha(0).withEndAction(new Runnable() {
//                            @Override
//                            public void run() {
//                                listarr.remove(item);
//                                adapter.notifyDataSetChanged();
//                                view.setAlpha(1);
//                            }
//                        });
            }

        });

    }
}
