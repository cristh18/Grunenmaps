package com.leinaro.grunenthal;

import android.Manifest;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity
        implements AdapterView.OnItemClickListener, OnMapReadyCallback, View.OnClickListener {

    private GoogleMap mMap;
    //    private Location location;
    private LocationManager locationManager;
    private static final int INITIAL_REQUEST = 1337;

    private boolean isPalexis;
    private boolean moreZoom;
    private String filter = "";


    private ImageButton palexis;
    private ImageButton transtec;

    private static final String[] INITIAL_PERMS = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.READ_CONTACTS
    };

    private GoogleApiClient client;
    private GPSTracker gps;
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (!canAccessLocation() || !canAccessContacts()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(INITIAL_PERMS, INITIAL_REQUEST);
            }
        }

        sharedPref = getPreferences(Context.MODE_PRIVATE);
        editor = sharedPref.edit();

        palexis = (ImageButton) findViewById(R.id.palexis);
        transtec = (ImageButton) findViewById(R.id.transtec);
        moreZoom = false;
        transtec.setOnClickListener(this);
        palexis.setOnClickListener(this);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        mTracker = getDefaultTracker();


        AdapterDrawerMenu customAdapter = new AdapterDrawerMenu(GrnenthalApplication.franquicias, this);

        ListView listDrawer = (ListView) findViewById(R.id.lst_menu_items);
        listDrawer.setAdapter(customAdapter);
        listDrawer.setSelector(R.drawable.list_selector);

        listDrawer.setOnItemClickListener(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Acquire a reference to the system Location Manager
        locationManager = (LocationManager) this.getSystemService(this.LOCATION_SERVICE);

        isPalexis = false;

        gps = new GPSTracker(MainActivity.this);

        // check if GPS enabled
        if (gps.canGetLocation()) {

            double latitude = gps.getLatitude();
            double longitude = gps.getLongitude();

            Geocoder geocoder = new Geocoder(
                    this, Locale
                    .getDefault());
            List<Address> addresses;
            try {
                Log.v("log_tag", "latitude" + latitude);
                Log.v("log_tag", "longitude" + longitude);
                addresses = geocoder.getFromLocation(latitude,
                        longitude, 1);
                if (addresses.size()>=1) {
                    Log.v("log_tag", "addresses+)_+++" + addresses);
                    String cityName = addresses.get(0).getAddressLine(1);
                    if (cityName == null){
                     cityName = addresses.get(0).getFeatureName();
                    }
                    if (cityName == null){
                        cityName = "lat: "+latitude +" - lon: "+ longitude;
                    }
                    Log.v("log_tag", "CityName " + cityName);

                    if (sharedPref.getBoolean("first_install",false)){
                        mTracker.send(new HitBuilders.EventBuilder()
                                .setCategory("install")
                                .setAction("install")
                                .setLabel(cityName)
                                .build());

                        editor.putBoolean("first_install", true);
                        editor.commit();
                    }

                    mTracker.send(new HitBuilders.EventBuilder()
                            .setCategory("city")
                            .setAction("open")
                            .setLabel(cityName)
                            .build());
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }


            colombiaDefault = new LatLng(latitude, longitude);
            if (mMap != null) {
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(colombiaDefault, 12));

                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            }
            moreZoom = true;

        } else {
            colombiaDefault = new LatLng(4.689019, -74.090721);
            gps.showSettingsAlert();
        }
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

        // Obtain the shared Tracker instance.
    }

    private Tracker mTracker;

    /**
     * Gets the default {@link Tracker} for this {@link Application}.
     * @return tracker
     */
    synchronized public Tracker getDefaultTracker() {
        if (mTracker == null) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            // To enable debug logging use: adb shell setprop log.tag.GAv4 DEBUG
            mTracker = analytics.newTracker(R.xml.global_tracker);
        }
        return mTracker;
    }

    private boolean canAccessLocation() {
        return (hasPermission(Manifest.permission.ACCESS_FINE_LOCATION));
    }

    private boolean canAccessContacts() {
        return (hasPermission(Manifest.permission.READ_CONTACTS));
    }

    private boolean hasPermission(String perm) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return (PackageManager.PERMISSION_GRANTED == checkSelfPermission(perm));
        }else
            return true;

    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED) {


            if (moreZoom) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);

                if (gps.canGetLocation()) {

                    double latitude = gps.getLatitude();
                    double longitude = gps.getLongitude();

                    Geocoder geocoder = new Geocoder(
                            this, Locale
                            .getDefault());
                    List<Address> addresses;
                    try {
                        Log.v("log_tag", "latitude" + latitude);
                        Log.v("log_tag", "longitude" + longitude);
                        addresses = geocoder.getFromLocation(latitude,
                                longitude, 1);
                        if (addresses.size()>=1) {
                            Log.v("log_tag", "addresses+)_+++" + addresses);
                            String cityName = addresses.get(0).getAddressLine(1);
                            Log.v("log_tag", "CityName " + cityName);
                            if (cityName == null){
                                cityName = addresses.get(0).getFeatureName();
                            }
                            if (cityName == null){
                                cityName = "lat: "+latitude +" - lon: "+ longitude;
                            }
                            if (sharedPref.getBoolean("first_install",false)){
                                mTracker.send(new HitBuilders.EventBuilder()
                                        .setCategory("install")
                                        .setAction("install")
                                        .setLabel(cityName)
                                        .build());

                                editor.putBoolean("first_install", true);
                                editor.commit();
                            }

                            mTracker.send(new HitBuilders.EventBuilder()
                                    .setCategory("city")
                                    .setAction("open")
                                    .setLabel(cityName)
                                    .build());
                        }
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    colombiaDefault = new LatLng(latitude, longitude);
                    if (mMap != null) {
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(colombiaDefault, 12));

                        mMap.addMarker(new MarkerOptions()
                                .position(colombiaDefault)
//            .icon(getMarkerIcon(pharmacies.get(i).getColor()))
                                .title("Estas aqui")
//            .snippet(pharmacies.get(i).getAddress())
                );
                        mMap.setMyLocationEnabled(true);
                        mMap.getUiSettings().setMyLocationButtonEnabled(true);
                    }
                    moreZoom = true;

                }
            } else {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
                mMap.getUiSettings().setCompassEnabled(true);
            }

        } else {
            Toast.makeText(this, "No tienes permisos", Toast.LENGTH_LONG).show();
        }

    }

    public BitmapDescriptor getMarkerIcon(String color) {
        float[] hsv = new float[3];
        Color.colorToHSV(Color.parseColor(color), hsv);
        return BitmapDescriptorFactory.defaultMarker(hsv[0]);
    }

    private LatLng colombiaDefault;

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        addMarkers("", GrnenthalApplication.pharmacies2, "");

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED) {

                    if (moreZoom) {
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
        } else {
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
            mMap.getUiSettings().setCompassEnabled(true);
        }

        } else {
            Toast.makeText(this, "No tienes permisos", Toast.LENGTH_LONG).show();
        }

    }

    private void addMarkerFinal(Pharmacies pharmacies) {
        mMap.addMarker(new MarkerOptions()
                .position(
                        new LatLng(
                                pharmacies.getLat(),
                                pharmacies.getLon()))
                .icon(getMarkerIcon(pharmacies.getColor()))
                .title(pharmacies.getName())
                .snippet(pharmacies.getAddress()));

    }

    private void filterOnlyProduct(List<Pharmacies> pharmacies) {
        for (int i = 0; i < pharmacies.size(); i++) {
            addMarkerFinal(pharmacies.get(i));
        }
    }

    private void addMarkers(String filterf, List<Pharmacies> pharmacies, String filterAll) {
        mMap.clear();

        if (filterf.isEmpty() && filterAll.isEmpty()) {
            filterOnlyProduct(pharmacies);
        } else if (!filterf.isEmpty() && filterAll.isEmpty()) {
            for (int i = 0; i < pharmacies.size(); i++) {
                if (filterf.compareToIgnoreCase(pharmacies.get(i).getFranchise()) == 0) {
                    addMarkerFinal(pharmacies.get(i));
                }
            }
        } else if (filterf.isEmpty() && !filterAll.isEmpty()) {
            for (int i = 0; i < pharmacies.size(); i++) {
                if (pharmacies.get(i).getName().toUpperCase().contains(filterAll.toUpperCase())
                        || pharmacies.get(i).getFranchise().toUpperCase().contains(filterAll.toUpperCase())
                        || pharmacies.get(i).getAddress().toUpperCase().contains(filterAll.toUpperCase())) {
                    addMarkerFinal(pharmacies.get(i));
                }
            }
        } else if (!filterf.isEmpty() && !filterAll.isEmpty()) {
            for (int i = 0; i < pharmacies.size(); i++) {
                if (filterf.compareToIgnoreCase(pharmacies.get(i).getFranchise()) == 0) {
                    if (pharmacies.get(i).getName().toUpperCase().contains(filterAll.toUpperCase())
                            || pharmacies.get(i).getFranchise().toUpperCase().contains(filterAll.toUpperCase())
                            || pharmacies.get(i).getAddress().toUpperCase().contains(filterAll.toUpperCase())) {
                        addMarkerFinal(pharmacies.get(i));
                    }
                }
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {

            SearchManager manager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
            SearchView search = (SearchView) menu.findItem(R.id.action_search).getActionView();
            search.setSearchableInfo(manager.getSearchableInfo(getComponentName()));
            search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

                @Override
                public boolean onQueryTextSubmit(String query) {

                    if (isPalexis) {
                        addMarkers(filter, GrnenthalApplication.pharmacies1, query);
                    } else {
                        addMarkers(filter, GrnenthalApplication.pharmacies2, query);
                    }

                    return false;
                }

                @Override
                public boolean onQueryTextChange(String query) {
                    return true;
                }

            });

        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        view.setSelected(true);


        filter = (position == 0) ? "" : parent.getAdapter().getItem(position).toString();


        if (isPalexis) {
            addMarkers(filter, GrnenthalApplication.pharmacies1, "");
        } else {
            addMarkers(filter, GrnenthalApplication.pharmacies2, "");
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.palexis:
                showAllPalexis();
                break;
            case R.id.transtec:
                showAllTranstec();
                break;
            default:
                break;
        }
    }

    private void showAllTranstec() {
        transtec.setImageResource(R.mipmap.transtec_button_on);
        palexis.setImageResource(R.mipmap.palexis_button_off);
        addMarkers("", GrnenthalApplication.pharmacies2, "");
        isPalexis = false;
    }

    private void showAllPalexis() {
        transtec.setImageResource(R.mipmap.transtec_button_off);
        palexis.setImageResource(R.mipmap.palexis_button_on);
        addMarkers("", GrnenthalApplication.pharmacies1, "");
        isPalexis = true;
    }

    @Override
    public void onStart() {
        super.onStart();

        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW,
                "Main Page",
                Uri.parse("http://host/path"),
                Uri.parse("android-app://com.leinaro.grunenthal/http/host/path")
        );
//        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
//        Action viewAction = Action.newAction(
//                Action.TYPE_VIEW, // TODO: choose an action type.
//                "Main Page", // TODO: Define a title for the content shown.
//                // TODO: If you have web page content that matches this app activity's content,
//                // make sure this auto-generated web page URL is correct.
//                // Otherwise, set the URL to null.
//                Uri.parse("http://host/path"),
//                // TODO: Make sure this auto-generated app URL is correct.
//                Uri.parse("android-app://com.leinaro.grunenthal/http/host/path")
//        );
//        AppIndex.AppIndexApi.end(client, viewAction);
//        client.disconnect();
    }
}
