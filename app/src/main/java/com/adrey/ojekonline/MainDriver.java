package com.adrey.ojekonline;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Muh Adrey Fatawallah on 3/11/2017.
 */

public class MainDriver extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Marker mStart, marker;
    private LatLng start, latLng;
    private RequestQueue requestQueue;

    private ArrayList<Marker> listMarker = new ArrayList<>();

    private LinearLayoutManager mLayoutManager;
    private MainDriverAdapter mAdapter;
    private SnapHelper snapHelper;

    private ArrayList<LatLng> listlonglat = new ArrayList<>();

    private String nm_asal, lat_asal, long_asal,
            nm_tujuan, lat_tujuan, long_tujuan;

    private RecyclerView list_driver;
    private LinearLayout linearLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_driver);

        requestQueue = Volley.newRequestQueue(this);

        Bundle b = getIntent().getExtras();
        nm_asal = b.getString("nm_asal");
        lat_asal = b.getString("lat_asal");
        long_asal = b.getString("long_asal");
        nm_tujuan = b.getString("nm_tujuan");
        lat_tujuan = b.getString("lat_tujuan");
        long_tujuan = b.getString("long_tujuan");

        list_driver = (RecyclerView) findViewById(R.id.list_driver);
        linearLayout = (LinearLayout) findViewById(R.id.layout_progress);

        list_driver.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        list_driver.setLayoutManager(mLayoutManager);
        snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(list_driver);
        list_driver.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (newState == 0) {
                    LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                    int pos = linearLayoutManager.findFirstVisibleItemPosition();
                    cetakLog(String.valueOf(pos));

                    for (int i = 0; i < listMarker.size(); i++) {
                        if (i != pos)
                            listMarker.get(i).setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_motorcycle));
                        else
                            listMarker.get(i).setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_motorcycle_red));
                    }
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(listlonglat.get(pos), 17));
                }
            }
        });

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        setupToolbar();
    }

    private void setupToolbar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(R.string.pilih_ojek);
        }
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

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                int pos = Integer.parseInt(marker.getTitle());
                list_driver.smoothScrollToPosition(pos+1);
                return true;
            }
        });

        setMarkerMap();
    }

    private void setMarkerMap() {
        start = new LatLng(Double.parseDouble(lat_asal), Double.parseDouble(long_asal));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(start, 17));
        mStart = mMap.addMarker(new MarkerOptions()
                .position(start)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_personal))
                .title(nm_asal));
        viewmotorcycle(start);
    }
    private void viewmotorcycle(LatLng start) {
        String strX = String.valueOf(start.latitude);
        String strY = String.valueOf(start.longitude);
        for (int a = 0; a < 10; a++) {
            Random random = new Random();
            StringBuilder stbX = new StringBuilder(strX);
            StringBuilder stbY = new StringBuilder(strY);

            int x, y;
            if (a < 5) {
                for (x = 6; x < strX.length(); x++) {
                    int rX = random.nextInt(9);
                    stbX.setCharAt(x, Character.forDigit(rX, 10));
                }
                for (y = 7; y < strY.length(); y++) {
                    int rY = random.nextInt(9);
                    stbY.setCharAt(y, Character.forDigit(rY, 10));
                }
            } else {
                for (x = 5; x < strX.length(); x++) {
                    int rX = random.nextInt(9);
                    stbX.setCharAt(x, Character.forDigit(rX, 10));
                }
                for (y = 6; y < strY.length(); y++) {
                    int rY = random.nextInt(9);
                    stbY.setCharAt(y, Character.forDigit(rY, 10));
                }
            }

            Double dbX = Double.parseDouble(stbX.toString());
            Double dbY = Double.parseDouble(stbY.toString());
            latLng = new LatLng(dbX, dbY);
            if (a == 0)
                marker = mMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_motorcycle_red))
                        .title(String.valueOf(a)));
            else
                marker = mMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_motorcycle))
                        .title(String.valueOf(a)));
            listMarker.add(marker);

            listlonglat.add(latLng);
        }

        mAdapter = new MainDriverAdapter(listlonglat, long_asal, lat_asal, nm_asal,
                long_tujuan, lat_tujuan, nm_tujuan);
        list_driver.setAdapter(mAdapter);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                linearLayout.setVisibility(View.GONE);
            }
        }, 2000);
    }

    /**
     * Method cetak text
     **/
    private void cetakLog(String response) {
        Log.e("Data", response);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
