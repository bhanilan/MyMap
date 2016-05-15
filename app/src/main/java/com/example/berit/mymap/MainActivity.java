package com.example.berit.mymap;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.w3c.dom.Text;

import java.util.List;


public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, LocationListener{

    private final static String TAG = "MainActivity";

    //saving map as variable
    private GoogleMap mGoogleMap;
    private Menu mOptionsMenu;

    private LocationManager locationManager;
    private String provider;

    private int markerCount = 0;
    private Location locationCurrent;
    private Location locationPrevious;
    private Location locationStart;
    private Location lastWP;
    private Location locationCreset;

    private double totalDistance;
    private double distanceFromWP;
    private boolean wpSet;
    private double totalLine;
    private double lineWP;
    private double distanceCreset;
    private double lineCreset;

    private Polyline mPolyline;
    private PolylineOptions mPolylineOptions;

    private TextView textViewCresetDistance;
    private TextView textViewWpDistance;
    private TextView textViewTotalDistance;

    private TextView textViewCresetLine;
    private TextView textViewWPLine;
    private TextView textViewTotalLine;

    private TextView textViewWPCount;
    private TextView textViewSpeed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mymap);
        mapFragment.getMapAsync(this);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        Criteria criteria = new Criteria();

        //get location provider such as GPS/ cell tower or wifi
        provider = locationManager.getBestProvider(criteria, false);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "No COARSE location permission");
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "No FINE location permission");
        }
        //get last location
        locationPrevious = locationManager.getLastKnownLocation(provider);

        //
        if(locationStart == null){
            locationStart = locationPrevious;
        }

        if(locationCreset == null){
            locationCreset = locationPrevious;
        }

        //location change changes the variables on display
        if (locationCurrent != null){
            locationPrevious = locationCurrent;
            onLocationChanged(locationCurrent);
        }

        textViewCresetDistance = (TextView) findViewById(R.id.textview_creset_distance);
        textViewWpDistance = (TextView) findViewById(R.id.textview_wp_distance);
        textViewTotalDistance = (TextView) findViewById(R.id.textview_total_distance);

        textViewCresetLine = (TextView) findViewById(R.id.textview_creset_line);
        textViewWPLine = (TextView) findViewById(R.id.textview_wp_line);
        textViewTotalLine = (TextView) findViewById(R.id.textview_total_line);

        textViewWPCount = (TextView) findViewById(R.id.textview_wpcount);
        textViewSpeed = (TextView) findViewById(R.id.textview_speed);
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
            case R.id.menu_mylocation:
                item.setChecked(!item.isChecked());
                updateMyLocation();
                return true;
            case R.id.menu_trackposition:
                item.setChecked(!item.isChecked());
                updateTrackPosition();
                return true;
            case R.id.menu_keepmapcentered:
                item.setChecked(!item.isChecked());
                return true;

            case R.id.menu_maptype_normal:
            case R.id.menu_maptype_hybrid:
            case R.id.menu_maptype_satellite:
            case R.id.menu_maptype_terrain:
            case R.id.menu_maptype_none:
                item.setChecked(true);
                changeMapType();
                return true;

//            case R.id.menu_map_zoom_10:
//            case R.id.menu_map_zoom_15:
//            case R.id.menu_map_zoom_20:
//            case R.id.menu_map_zoom_in:
//            case R.id.menu_map_zoom_out:
//            case R.id.menu_map_zoom_fittrack:
//                updateMapZoomLevel(item.getItemId());
//                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

//    private void updateMapZoomLevel(int itemId){
//        if (!checkReady()) {
//            return;
//        }
//
//        switch (itemId) {
//            case R.id.menu_map_zoom_10:
//                mGoogleMap.moveCamera(CameraUpdateFactory.zoomTo(10));
//                break;
//            case R.id.menu_map_zoom_15:
//                mGoogleMap.moveCamera(CameraUpdateFactory.zoomTo(15));
//                break;
//            case R.id.menu_map_zoom_20:
//                mGoogleMap.moveCamera(CameraUpdateFactory.zoomTo(20));
//                break;
//            case R.id.menu_map_zoom_in:
//                mGoogleMap.moveCamera(CameraUpdateFactory.zoomIn());
//                break;
//            case R.id.menu_map_zoom_out:
//                mGoogleMap.moveCamera(CameraUpdateFactory.zoomOut());
//                break;
//            case R.id.menu_map_zoom_fittrack:
//                updateMapZoomFitTrack();
//                break;
//        }
//    }

//    private void updateMapZoomFitTrack(){
//        if (mPolyline==null){
//            return;
//        }
//
//        List<LatLng> points = mPolyline.getPoints();
//
//        if (points.size()<=1){
//            return;
//        }
//        LatLngBounds.Builder builder = new LatLngBounds.Builder();
//        for (LatLng point : points) {
//            builder.include(point);
//        }
//        LatLngBounds bounds = builder.build();
//        int padding = 0; // offset from edges of the map in pixels
//        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, padding));
//    }

    private void updateTrackPosition(){
        if (!checkReady()) {
            return;
        }

        if (mOptionsMenu.findItem(R.id.menu_trackposition).isChecked()) {
            mPolylineOptions = new PolylineOptions().width(5).color(Color.RED);
            mPolyline = mGoogleMap.addPolyline(mPolylineOptions);
        }
    }

    private boolean checkReady() {
        if (mGoogleMap == null) {
            Toast.makeText(this, R.string.map_not_ready, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
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

    private void updateMyLocation() {
        if (mOptionsMenu.findItem(R.id.menu_mylocation).isChecked()) {
            mGoogleMap.setMyLocationEnabled(true);
            return;
        }

        mGoogleMap.setMyLocationEnabled(false);
    }


    public void buttonAddWayPointClicked(View view){
        if (locationPrevious==null){
            return;
        }

        markerCount++;

        mGoogleMap.addMarker(new MarkerOptions().position(new LatLng(
                locationPrevious.getLatitude(), locationPrevious.getLongitude()))
                .title(Integer.toString(markerCount)));
        textViewWPCount.setText(Integer.toString(markerCount));

        wpSet = true;
        distanceFromWP = 0;

        lastWP = locationPrevious;
    }

    public void buttonCResetClicked(View view){
        distanceCreset = 0;
        lineCreset = 0;
        locationCreset = locationPrevious;
    }

    @Override
    //after my key has been checked
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;

        // set zoom level to 15 - street
        mGoogleMap.moveCamera(CameraUpdateFactory.zoomTo(17));

        // if there was initial location received, move map to it
        if (locationPrevious != null) {
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng
                    (locationPrevious.getLatitude(), locationPrevious.getLongitude())));
        }

    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
    }

    @Override
    public void onLocationChanged(Location location) {
        LatLng newPoint = new LatLng(location.getLatitude(), location.getLongitude());

        if (mGoogleMap==null) return;

        if (mOptionsMenu.findItem(R.id.menu_keepmapcentered).isChecked() || locationPrevious == null) {
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(newPoint));
        }

        if (mOptionsMenu.findItem(R.id.menu_trackposition).isChecked()) {
            List<LatLng> points = mPolyline.getPoints();
            points.add(newPoint);
            mPolyline.setPoints(points);
        }

        double distance = location.distanceTo(locationPrevious);
        totalDistance = totalDistance + distance;
        totalLine = locationStart.distanceTo(location);
        distanceCreset = distanceCreset + distance;
        lineCreset = locationCreset.distanceTo(location);

        if(wpSet){
            distanceFromWP = distanceFromWP + distance;
            lineWP = lastWP.distanceTo(location);
        }

        //speed in m/sec
//        if(location.hasSpeed()){
//
//        }

        locationPrevious = location;

        textViewWpDistance.setText(String.valueOf(Math.round(distanceFromWP)));
        textViewTotalDistance.setText(String.valueOf(Math.round(totalDistance)));
        textViewWPLine.setText(String.valueOf(Math.round(lineWP)));
        textViewTotalLine.setText(String.valueOf(Math.round(totalLine)));
        textViewCresetDistance.setText(String.valueOf(Math.round(distanceCreset)));
        textViewCresetLine.setText(String.valueOf(Math.round(lineCreset)));
        //textViewSpeed.setText();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    //method for asking updates form GPS
    protected void onResume(){
        super.onResume();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "No COARSE location permission");
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "No FINE location permission");
        }

        if(locationManager != null){
            locationManager.requestLocationUpdates(provider, 500, 1, this);
        }
    }

    @Override
    //method to do smth while being on background
    protected void onPause(){
        super.onPause();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "No COARSE location permission");
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "No FINE location permission");
        }

        if(locationManager != null){
            //remove updates
            locationManager.removeUpdates(this);
        }
    }
}
