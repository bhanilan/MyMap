package com.example.berit.mymap;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback{

    private final static String TAG = "MainActivity";

    //saving map as variable
    private GoogleMap mGoogleMap;

    private Menu mOptionsMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mymap);
        mapFragment.getMapAsync(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        mOptionsMenu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_maptype_normal:
            case R.id.menu_maptype_hybrid:
            case R.id.menu_maptype_satellite:
            case R.id.menu_maptype_terrain:
            case R.id.menu_maptype_none:
                item.setChecked(true);
                changeMapType();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void changeMapType(){
        if(mGoogleMap == null){
            return;
        }

        if(mOptionsMenu.findItem(R.id.menu_maptype_hybrid).isChecked()){
            mGoogleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        } else if(mOptionsMenu.findItem(R.id.menu_maptype_normal).isChecked()){
            mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        } else if(mOptionsMenu.findItem(R.id.menu_maptype_none).isChecked()){
            mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NONE);
        } else if(mOptionsMenu.findItem(R.id.menu_maptype_satellite).isChecked()){
            mGoogleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        } else if(mOptionsMenu.findItem(R.id.menu_maptype_terrain).isChecked()){
            mGoogleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        }
    }

    @Override
    //my key has been checked
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;

        LatLng latLng = new LatLng(59.3955457, 24.6643051);
        mGoogleMap.addMarker(new MarkerOptions().position(latLng).title("ITK"));

        // set zoom level to 15 - street
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));

        mGoogleMap.setMyLocationEnabled(true);
    }
}
