package com.leinaro.grunenthal.ui.activities;

import android.Manifest;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.leinaro.grunenthal.GPSTracker;
import com.leinaro.grunenthal.GrnenthalApplication;
import com.leinaro.grunenthal.R;
import com.leinaro.grunenthal.api.models.Pharmacy;
import com.leinaro.grunenthal.models.SearchParameters;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class MainActivity extends AppCompatActivity
        implements OnMapReadyCallback, View.OnClickListener {

    public static final String KEY_PRODUCT_SELECTED = "PRODUCT_SELECTED";

    private ImageButton palexis;
    private ImageButton transtec;
    private ImageButton norspan;
    private GoogleMap mMap;
    private int productSelected = TRANSTEC;
    private String filter = "";
    private int channel = 0;
    private List<Pharmacy> currentPharmacies = new ArrayList();

    private GPSTracker gps;

    private LatLng colombiaDefault;

    protected AlertDialog dialog;

    private static final int TRANSTEC = 1;
    private static final int PALEXIS = 2;
    private static final int NORSPAN = 3;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        initializeView();

        currentPharmacies = GrnenthalApplication.pharmacies2;

        successLocationAndPhoneStatePermission(); // TODO CHANGE METHOD NAME
    }

    protected void successLocationAndPhoneStatePermission() {
        gps = new GPSTracker(MainActivity.this);
        // check if GPS enabled
        if (gps.canGetLocation()) {
            double latitude = gps.getLatitude();
            double longitude = gps.getLongitude();
            colombiaDefault = new LatLng(latitude, longitude);
        } else {
            colombiaDefault = new LatLng(4.689019, -74.090721);
            gps.showSettingsAlert();
        }
    }

    private void initializeView() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        palexis = findViewById(R.id.palexis);
        transtec = findViewById(R.id.transtec);
        norspan = findViewById(R.id.norspan);
        transtec.setOnClickListener(this);
        palexis.setOnClickListener(this);
        norspan.setOnClickListener(this);
        initNavigationDrawer(toolbar);
    }

    private void initNavigationDrawer(Toolbar toolbar) {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigator = findViewById(R.id.nav_view);
        navigator.setNavigationItemSelectedListener(menuItem -> {
            drawer.closeDrawer(GravityCompat.START);
            int id = menuItem.getItemId();
            switch (id) {
                case R.id.custom_search:
                    startActivityForResult(new Intent(getBaseContext(), CustomSearchActivity.class), 1111);
                    return true;
                case R.id.terms:
                    startActivity(new Intent(getBaseContext(), TermsActivity.class));
                    return true;
                default:
                    return true;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(this.getClass().getName(), "onResume");
        setUpMapIfNeeded();
    }

    private void setUpMapIfNeeded() {
        if (mMap == null) {
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        MainActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    public BitmapDescriptor getMarkerIcon(String color) {
        float[] hsv = new float[3];
        Color.colorToHSV(Color.parseColor(color), hsv);
        return BitmapDescriptorFactory.defaultMarker(hsv[0]);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        addMarkers(filter, currentPharmacies, "", channel);
        MainActivityPermissionsDispatcher.zoomLocationWithPermissionCheck(this);

        Log.e(this.getClass().getName(), "onMapReady");
    }

    @NeedsPermission({Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION})
    void zoomLocation() {

        if (mMap != null) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(colombiaDefault, 12));
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);

            if (gps.canGetLocation()) {
                double latitude = gps.getLatitude() == 0.0 ? 4.689019 : gps.getLatitude();
                double longitude = gps.getLongitude() == 0.0 ? -74.090721 : gps.getLongitude();
                colombiaDefault = new LatLng(latitude, longitude);

                if (mMap != null) {
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(colombiaDefault, 12));
                    mMap.addMarker(new MarkerOptions().position(colombiaDefault));
                    mMap.setMyLocationEnabled(true);
                    mMap.getUiSettings().setMyLocationButtonEnabled(true);
                }
            }
        }
    }


    private void addMarkerFinal(Pharmacy pharmacy) {
        if (pharmacy.getLat() != null
                && pharmacy.getLon() != null
                && !TextUtils.isEmpty(pharmacy.getLat())
                && !TextUtils.isEmpty(pharmacy.getLon())) {
            Marker marker = mMap.addMarker(getMarkerOptions(pharmacy));
            mMap.setInfoWindowAdapter(getInfoWindowAdapter());
            marker.showInfoWindow();
        }
    }

    @NotNull
    private MarkerOptions getMarkerOptions(Pharmacy pharmacy) {
        return new MarkerOptions()
                .position(new LatLng(Double.parseDouble(pharmacy.getLat()), Double.parseDouble(pharmacy.getLon())))
                .icon(getMarkerIcon(pharmacy.getColor()))
                .title(pharmacy.getName())
                .snippet(getString(R.string.marker_format, pharmacy.getAddress(), pharmacy.getPhone(), pharmacy.getPresentations()));
    }

    @NotNull
    private GoogleMap.InfoWindowAdapter getInfoWindowAdapter() {
        return new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                View v = getLayoutInflater().inflate(R.layout.custom_marker_info_window, null);
                TextView title = v.findViewById(R.id.textView_title);
                title.setText(marker.getTitle());
                TextView subtitle = v.findViewById(R.id.textView_subtitle);
                subtitle.setText(marker.getSnippet());
                return v;
            }
        };
    }

    private void filterOnlyProduct(List<Pharmacy> pharmacies) {
        for (int i = 0; i < pharmacies.size(); i++) {
            addMarkerFinal(pharmacies.get(i));
        }
    }

    private void addMarkers(String filterf, List<Pharmacy> pharmacies, String filterAll, int channel) {
        mMap.clear();

        if (filterf.isEmpty() && filterAll.isEmpty()) {
            filterOnlyProduct(pharmacies);
        } else if (!filterf.isEmpty() && filterAll.isEmpty() && channel == 0) {
            for (int i = 0; i < pharmacies.size(); i++) {
                if (filterf.compareToIgnoreCase(pharmacies.get(i).getFranchise()) == 0) {
                    addMarkerFinal(pharmacies.get(i));
                } else if (filterf.equalsIgnoreCase("Todos")) {
                    addMarkerFinal(pharmacies.get(i));
                }
            }
        } else if (filterf.isEmpty() && !filterAll.isEmpty() && channel == 0) {
            for (int i = 0; i < pharmacies.size(); i++) {
                if (pharmacies.get(i).getName().toUpperCase().contains(filterAll.toUpperCase())
                        || pharmacies.get(i).getFranchise().toUpperCase().contains(filterAll.toUpperCase())
                        || pharmacies.get(i).getAddress().toUpperCase().contains(filterAll.toUpperCase())) {
                    addMarkerFinal(pharmacies.get(i));
                }
            }
        } else if (!filterf.isEmpty() && !filterAll.isEmpty() && channel == 0) {
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
                    ArrayList<Pharmacy> pharmacies = new ArrayList();
                    switch (productSelected) {
                        case TRANSTEC:
                            pharmacies = (ArrayList<Pharmacy>) GrnenthalApplication.pharmacies2;
                            break;
                        case PALEXIS:
                            pharmacies = (ArrayList<Pharmacy>) GrnenthalApplication.pharmacies1;
                            break;
                        case NORSPAN:
                            pharmacies = (ArrayList<Pharmacy>) GrnenthalApplication.pharmacies3;
                            break;
                    }

                    addMarkers("", pharmacies, query, 0);

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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.palexis:
                showAllPalexis();
                break;
            case R.id.transtec:
                showAllTranstec();
                break;
            case R.id.norspan:
                showAllNorspan();
                break;
            default:
                break;
        }
    }

    private void showAllTranstec() {
        transtec.setImageResource(R.mipmap.transtec_button_on);
        palexis.setImageResource(R.mipmap.palexis_button_off);
        norspan.setImageResource(R.mipmap.norspan_button_off);
        productSelected = TRANSTEC;
        addMarkers("", GrnenthalApplication.pharmacies2, "", 0);

    }

    private void showAllPalexis() {
        transtec.setImageResource(R.mipmap.transtec_button_off);
        palexis.setImageResource(R.mipmap.palexis_button_on);
        norspan.setImageResource(R.mipmap.norspan_button_off);
        productSelected = PALEXIS;
        addMarkers("", GrnenthalApplication.pharmacies1, "", 0);
    }

    private void showAllNorspan() {
        norspan.setImageResource(R.mipmap.norspan_button_on);
        palexis.setImageResource(R.mipmap.palexis_button_off);
        transtec.setImageResource(R.mipmap.transtec_button_off);
        productSelected = NORSPAN;
        addMarkers("", GrnenthalApplication.pharmacies3, "", 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Log.e(this.getClass().getName(), "onActivityResult");
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1111 && resultCode == 8888) {
            if (data != null && data.getExtras() != null) {
                SearchParameters searchParameters = data.getParcelableExtra("SEARCH_PARAMETERS");
                Log.e(this.getClass().getName(), "Search parameters: " + searchParameters.toString());
                filter = searchParameters.getFranchise();
                channel = searchParameters.getChannel();

                if (mMap != null) {
                    addMarkers(filter, currentPharmacies, "", channel);
                }

                switch (productSelected) {
                    case TRANSTEC:
                        currentPharmacies = GrnenthalApplication.pharmacies2;
                        transtec.setImageResource(R.mipmap.transtec_button_on);
                        palexis.setImageResource(R.mipmap.palexis_button_off);
                        norspan.setImageResource(R.mipmap.norspan_button_off);
                        break;
                    case PALEXIS:
                        currentPharmacies = GrnenthalApplication.pharmacies1;
                        transtec.setImageResource(R.mipmap.transtec_button_off);
                        palexis.setImageResource(R.mipmap.palexis_button_on);
                        norspan.setImageResource(R.mipmap.norspan_button_off);
                        break;
                    case NORSPAN:
                        currentPharmacies = GrnenthalApplication.pharmacies3;
                        norspan.setImageResource(R.mipmap.norspan_button_on);
                        palexis.setImageResource(R.mipmap.palexis_button_off);
                        transtec.setImageResource(R.mipmap.transtec_button_off);
                        break;
                }
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(KEY_PRODUCT_SELECTED, productSelected);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            productSelected = savedInstanceState.getInt(KEY_PRODUCT_SELECTED);
        }
        super.onRestoreInstanceState(savedInstanceState);
    }

    /**
     * Permissions
     */
    @OnShowRationale({Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION})
    void showRationaleForCamera(PermissionRequest request) {
        showRationaleDialog(R.string.copy_permission_location_rationale, request);
    }

    @OnPermissionDenied({Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.READ_EXTERNAL_STORAGE})
    protected void LocationAndPhoneStatePermissionDenied() {
        Toast.makeText(this, "Permissions denied", Toast.LENGTH_SHORT).show();
        MainActivityPermissionsDispatcher.zoomLocationWithPermissionCheck(this);
    }

    @OnNeverAskAgain({Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION})
    public void locationPermissionOnNeverAskAgain() {
        showDialogDeniedPermission("ubicación");
    }

    private void showRationaleDialog(@StringRes int messageResId, final PermissionRequest request) {
        new AlertDialog.Builder(this)
                .setPositiveButton("Permitir", (dialog, which) -> request.proceed())
                .setNegativeButton("Denegar", (dialog, which) -> request.cancel())
                .setCancelable(false)
                .setMessage(messageResId)
                .show();
    }

    private void showDialogDeniedPermission(String permissionDenied) {
        AlertDialog.Builder builder = getDialogDeniedPermission("Has denegado el permiso de ".concat(permissionDenied).concat("tienes que habilitarlo de manera manual para poder continuar."))
                .setPositiveButton("Habilitar", (dialog, which) -> handleDialogPermission(dialog));
        dialog = builder.create();
        dialog.show();
    }

    private AlertDialog.Builder getDialogDeniedPermission(String message) {
        return new AlertDialog.Builder(this)
                .setTitle("Permiso denegado")
                .setMessage(message)
                .setCancelable(false);
    }

    private void handleDialogPermission(DialogInterface dialog) {
        dialog.dismiss();
        startSettings();
    }

    protected void startSettings() {
        startActivity(new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.parse("package:".concat(GrnenthalApplication.get().getPackageName()))));
    }
}
