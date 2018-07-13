package com.adrey.ojekonline;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class MainOjek extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    // Request code address
    private static final int REQUEST_CODE_START = 1;
    private static final int REQUEST_CODE_END = 2;

    private RequestQueue requestQueue;

    private GoogleApiClient mGoogleApiClient;
    private List<Address> addresses;

    private GoogleMap mMap;
    private Polyline line;
    private LatLng start, end, latLng;
    private Marker mStart, mEnd, marker;

    private boolean location = false;

    private String nm_asal, lat_asal, long_asal,
            nm_tujuan, lat_tujuan, long_tujuan;
    private StringBuilder roadMap = new StringBuilder();

    private CardView layout_asal;
    private ImageView img_asal;
    private TextView tx_asal;
    private ImageView search_asal;

    private CardView layout_tujuan;
    private ImageView img_tujuan;
    private TextView tx_tujuan;
    private ImageView search_tujuan;

    private ImageView ojek, box;

    private LinearLayout linearLayout, form_order;
    private Button pesan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_ojek);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        requestQueue = Volley.newRequestQueue(this);

        // cek put extra from last activity to exit app
        try {
            if (getIntent().getBooleanExtra("EXIT", false)) {
                finish();
            }
        } catch (Exception ignored) {

        }

        buildGoogleApiClient();

        layout_asal = (CardView) findViewById(R.id.layout_asal);
        img_asal = (ImageView) findViewById(R.id.img_asal);
        tx_asal = (TextView) findViewById(R.id.tx_asal);
        search_asal = (ImageView) findViewById(R.id.search_asal);
        layout_asal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent search = new Intent(getApplicationContext(), SearchAddress.class);
                if (start != null) {
                    search.putExtra("lokasi", start.toString());
                }
                startActivityForResult(search, REQUEST_CODE_START);
            }
        });

        layout_tujuan = (CardView) findViewById(R.id.layout_tujuan);
        img_tujuan = (ImageView) findViewById(R.id.img_tujuan);
        tx_tujuan = (TextView) findViewById(R.id.tx_tujuan);
        search_tujuan = (ImageView) findViewById(R.id.search_tujuan);
        layout_tujuan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent search = new Intent(getApplicationContext(), SearchAddress.class);
                search.putExtra("lokasi", start.toString());
                startActivityForResult(search, REQUEST_CODE_END);
            }
        });

        linearLayout = (LinearLayout) findViewById(R.id.layout_progress);
        form_order = (LinearLayout) findViewById(R.id.form_order);

        ojek = (ImageView) findViewById(R.id.ojek);
        box = (ImageView) findViewById(R.id.box);
        ojek.setColorFilter(getResources().getColor(R.color.colorPrimaryDark));
        ojek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                box.setColorFilter(null);
                ojek.setColorFilter(getResources().getColor(R.color.colorPrimaryDark));
            }
        });
        box.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                box.setColorFilter(getResources().getColor(R.color.colorPrimaryDark));
                ojek.setColorFilter(null);
            }
        });

        pesan = (Button) findViewById(R.id.pesan);
        pesan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mainOrder = new Intent(getApplicationContext(), MainOrder.class);
                Bundle b = new Bundle();
                b.putString("nm_asal", nm_asal);
                b.putString("lat_asal", lat_asal);
                b.putString("long_asal", long_asal);
                b.putString("nm_tujuan", nm_tujuan);
                b.putString("lat_tujuan", lat_tujuan);
                b.putString("long_tujuan", long_tujuan);
                mainOrder.putExtras(b);
                startActivity(mainOrder);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        setupToolbar();
    }

    /**
     * Method create google API client and location request
     **/
    private void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        createLocationRequest();
    }

    private void createLocationRequest() {
        // Create and set location request
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_START) {
            if (resultCode == RESULT_OK) {
                nm_asal = data.getStringExtra("name");
                lat_asal = data.getStringExtra("lat");
                long_asal = data.getStringExtra("long");

                tx_asal.setText(nm_asal);
                img_asal.setVisibility(View.VISIBLE);
                search_asal.setVisibility(View.GONE);
                layout_tujuan.setVisibility(View.VISIBLE);

                start = new LatLng(Double.parseDouble(lat_asal), Double.parseDouble(long_asal));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(start, 17));
                mMap.clear();
                if (mStart != null) {
                    mStart.remove();
                }
                if (mEnd != null) {
                    mEnd.remove();
                    tx_tujuan.setText(getString(R.string.tujuan));
                    img_tujuan.setVisibility(View.GONE);
                    search_tujuan.setVisibility(View.VISIBLE);
                }
                mStart = mMap.addMarker(new MarkerOptions()
                        .position(start)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_personal))
                        .title(nm_asal));
            }
        } else if (requestCode == REQUEST_CODE_END) {
            if (resultCode == RESULT_OK) {
                linearLayout.setVisibility(View.VISIBLE);

                nm_tujuan = data.getStringExtra("name");
                lat_tujuan = data.getStringExtra("lat");
                long_tujuan = data.getStringExtra("long");

                tx_tujuan.setText(nm_tujuan);
                img_tujuan.setVisibility(View.VISIBLE);
                search_tujuan.setVisibility(View.GONE);

                end = new LatLng(Double.parseDouble(lat_tujuan), Double.parseDouble(long_tujuan));
                if (mEnd != null) {
                    mEnd.remove();
                }
                mEnd = mMap.addMarker(new MarkerOptions()
                        .position(end)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pos))
                        .title(nm_tujuan));
                getTrack();
            }
        }
    }

    /**
     * Method get google API
     **/
    private void getTrack() {
        String params = "http://maps.googleapis.com/maps/api/directions/json" +
                "?origin=" + lat_asal + "," + long_asal +
                "&destination=" + lat_tujuan + "," + long_tujuan +
                "&sensor=false" +
                "&units=metric" +
                "&mode=driving";
        cetakLog("Get Track : " + params);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, params, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray routes = jsonObject.getJSONArray("routes");
                    JSONObject object_routes = routes.getJSONObject(0);
                    JSONArray legs = object_routes.getJSONArray("legs");

                    JSONObject object_legs = legs.getJSONObject(0);

                    JSONArray steps = object_legs.getJSONArray("steps");
                    roadMap.append("https://roads.googleapis.com/v1/snapToRoads?path=");
                    for (int x = 0; x < steps.length(); x++) {
                        JSONObject object_steps = steps.getJSONObject(x);

                        // LongLat start
                        JSONObject start_location = object_steps.getJSONObject("start_location");
                        LatLng src = new LatLng(start_location.getDouble("lat"), start_location.getDouble("lng"));
                        // LongLat end
                        JSONObject end_location = object_steps.getJSONObject("end_location");
                        LatLng dest = new LatLng(end_location.getDouble("lat"), end_location.getDouble("lng"));
                        // Create link api
                        roadMap.append(src.latitude).append(",").append(src.longitude).append("|").append(dest.latitude).append(",").append(dest.longitude);
                        if (x < steps.length() - 1) {
                            roadMap.append("|");
                        }
                    }
                    roadMap.append("&interpolate=true&key=AIzaSyDVhSn9HcopTvpEelnLtxj3t3tsWU_EltE");

                    getRoadMap();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        requestQueue.add(stringRequest);
    }

    private void getRoadMap() {
        cetakLog("On Road : " + roadMap.toString());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, roadMap.toString(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray snappedPoints = jsonObject.getJSONArray("snappedPoints");

                    PolylineOptions options = new PolylineOptions().width(15).color(getResources().getColor(R.color.colorPrimaryDark)).geodesic(true);
                    for (int x = 0; x < snappedPoints.length(); x++) {
                        JSONObject object = snappedPoints.getJSONObject(x);
                        JSONObject location = object.getJSONObject("location");
                        LatLng latLng = new LatLng(location.getDouble("latitude"), location.getDouble("longitude"));
                        options.add(latLng);
                    }

//                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(start, 13));
                    LatLngBounds.Builder latlngBuilder = new LatLngBounds.Builder();
                    latlngBuilder.include(start);
                    latlngBuilder.include(end);
                    int width = getResources().getDisplayMetrics().widthPixels;
                    int height = (int) (getResources().getDisplayMetrics().density * 150);
                    int padding = (int) (width * 0.1);
                    LatLngBounds bounds = latlngBuilder.build();
                    mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding));

                    line = mMap.addPolyline(options);
                    form_order.setVisibility(View.VISIBLE);

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            linearLayout.setVisibility(View.GONE);
                        }
                    }, 2000);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        requestQueue.add(stringRequest);
    }

    /**
     * Event status connect google API client
     **/
    @Override
    public void onConnected(Bundle bundle) {
        getLocation();
    }

    @Override
    public void onConnectionSuspended(int i) {
        cetakLog("Connection Suspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        cetakLog("Connection Failed");
    }

    /**
     * Method get location
     **/
    private void getLocation() {
        if (!location) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }

            Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);

            if (mLastLocation != null) {
                Geocoder geocoder = new Geocoder(this, new Locale("id"));

                try {
                    addresses = geocoder.getFromLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude(), 1);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                for (int x = 0; x < addresses.size(); x++) {
                    Log.e("Data", addresses.get(x).toString());
                }

                nm_asal = addresses.get(0).getAddressLine(0);
                lat_asal = String.valueOf(mLastLocation.getLatitude());
                long_asal = String.valueOf(mLastLocation.getLongitude());
                start = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                mStart = mMap.addMarker(new MarkerOptions()
                        .position(start)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_personal))
                        .title(nm_asal));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(start, 17));

                tx_asal.setText(nm_asal);
                img_asal.setVisibility(View.VISIBLE);
                search_asal.setVisibility(View.GONE);
                layout_tujuan.setVisibility(View.VISIBLE);

                viewCar();
            }
        }
    }

    private void viewCar() {
        String strX = String.valueOf(start.latitude);
        String strY = String.valueOf(start.longitude);
        for (int a = 0; a < 20; a++) {
            Random random = new Random();
            StringBuilder stbX = new StringBuilder(strX);
            StringBuilder stbY = new StringBuilder(strY);

            int x, y;
            if (a < 2) {
                for (x = 6; x < strX.length(); x++) {
                    int rX = random.nextInt(9);
                    stbX.setCharAt(x, Character.forDigit(rX, 10));
                }
                for (y = 7; y < strY.length(); y++) {
                    int rY = random.nextInt(9);
                    stbY.setCharAt(y, Character.forDigit(rY, 10));
                }
            } else if (a > 5) {
                for (x = 5; x < strX.length(); x++) {
                    int rX = random.nextInt(9);
                    stbX.setCharAt(x, Character.forDigit(rX, 10));
                }
                for (y = 6; y < strY.length(); y++) {
                    int rY = random.nextInt(9);
                    stbY.setCharAt(y, Character.forDigit(rY, 10));
                }
            } else {
                for (x = 4; x < strX.length(); x++) {
                    int rX = random.nextInt(9);
                    stbX.setCharAt(x, Character.forDigit(rX, 10));
                }
                for (y = 5; y < strY.length(); y++) {
                    int rY = random.nextInt(9);
                    stbY.setCharAt(y, Character.forDigit(rY, 10));
                }
            }

            Double dbX = Double.parseDouble(stbX.toString());
            Double dbY = Double.parseDouble(stbY.toString());
            latLng = new LatLng(dbX, dbY);
            marker = mMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_motorcycle)));
        }
        location = true;

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                linearLayout.setVisibility(View.GONE);
            }
        }, 2000);
    }

    /**
     * Event on Ready Map
     **/
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setMapToolbarEnabled(false);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);
    }

    /**
     * Event status activity
     **/
    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onLocationChanged(Location location) {
        getLocation();
    }

    /**
     * Method cetak text
     **/
    private void cetakLog(String response) {
        Log.e("Data", response);
    }

    /**
     * Setup Toolbar
     */
    private void setupToolbar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(false);
            actionBar.setTitle(null);
        }
    }

    /**
     * Event navigation drawer
     **/
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.profil) {
            startActivity(new Intent(getApplicationContext(), MainProfile.class));
        } else if (id == R.id.history) {
            startActivity(new Intent(getApplicationContext(), MainHistory.class));
        } else if (id == R.id.pengaturan) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
