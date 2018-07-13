package com.adrey.ojekonline;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
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

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;

/**
 * Created by Muh Adrey Fatawallah on 3/19/2017.
 */

public class MainOrder extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Polyline line;
    private LatLng start, end;
    private Marker mStart, mEnd;

    private RequestQueue requestQueue;
    private Bundle b;

    private LinearLayout linearLayout;
    private TextView from, to, txdistance, price, other;
    private Button pesan;

    private String long_asal, lat_asal, nm_asal, long_tujuan, lat_tujuan, nm_tujuan, jarak;
    private StringBuilder roadMap = new StringBuilder();
    private Double harga;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_order);

        requestQueue = Volley.newRequestQueue(this);

        b = getIntent().getExtras();
        long_asal = b.getString("long_asal");
        lat_asal = b.getString("lat_asal");
        nm_asal = b.getString("nm_asal");
        long_tujuan = b.getString("long_tujuan");
        lat_tujuan = b.getString("lat_tujuan");
        nm_tujuan = b.getString("nm_tujuan");

        from = (AutoScaleText) findViewById(R.id.from);
        to = (AutoScaleText) findViewById(R.id.to);
        txdistance = (TextView) findViewById(R.id.distance);
        price = (TextView) findViewById(R.id.price);
        from.setText(nm_asal);
        to.setText(nm_tujuan);

        linearLayout = (LinearLayout) findViewById(R.id.layout_progress);
        other = (TextView) findViewById(R.id.other);
        other.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainDriver = new Intent(getApplicationContext(), MainDriver.class);
                b = new Bundle();
                b.putString("nm_asal", nm_asal);
                b.putString("lat_asal", lat_asal);
                b.putString("long_asal", long_asal);
                b.putString("nm_tujuan", nm_tujuan);
                b.putString("lat_tujuan", lat_tujuan);
                b.putString("long_tujuan", long_tujuan);
                mainDriver.putExtras(b);
                startActivity(mainDriver);
            }
        });
        pesan = (Button) findViewById(R.id.pesan);
        pesan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainDriveTrace = new Intent(getApplicationContext(), MainTraceDrive.class);
                mainDriveTrace.putExtra("from", "order");
                startActivity(mainDriveTrace);
            }
        });

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        setupToolbar();
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

        start = new LatLng(Double.parseDouble(lat_asal), Double.parseDouble(long_asal));
        mStart = mMap.addMarker(new MarkerOptions()
                .position(start)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_personal))
                .title(nm_asal));
        end = new LatLng(Double.parseDouble(lat_tujuan), Double.parseDouble(long_tujuan));
        mEnd = mMap.addMarker(new MarkerOptions()
                .position(end)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pos))
                .title(nm_tujuan));

        getTrack();
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
                    JSONObject distance = object_legs.getJSONObject("distance");

                    String strdist = distance.getString("text");
                    txdistance.setText(strdist);
                    harga = Double.parseDouble(strdist.replace(" km", "").replace(" m", "")
                            .replace(",", "")) * 3000;
                    setTextPrice();

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

    private void setupToolbar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(getString(R.string.pesan));
        }
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

    /**
     * Method cetak text
     **/
    private void cetakLog(String response) {
        Log.e("Data", response);
    }

    private void setTextPrice() {
        NumberFormat numberFormat = NumberFormat.getCurrencyInstance(new java.util.Locale("id"));
        DecimalFormatSymbols decimalFormatSymbols = ((DecimalFormat) numberFormat)
                .getDecimalFormatSymbols();
        decimalFormatSymbols.setCurrencySymbol("IDR ");
        ((DecimalFormat) numberFormat)
                .setDecimalFormatSymbols(decimalFormatSymbols);
        numberFormat.setMaximumFractionDigits(0);

        price.setText(numberFormat.format(harga));
    }
}
