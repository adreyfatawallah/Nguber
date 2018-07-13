package com.adrey.ojekonline;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SearchView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Muh Adrey Fatawallah on 11/9/2016.
 */

public class SearchAddress extends AppCompatActivity {

    private LinearLayout layout_progress;
    private LinearLayout layout_nothing;
    private RecyclerView list;

    private RequestQueue requestQueue;
    private String lokasi;
    private String url = "https://maps.googleapis.com/maps/api/place";
    private String output = "json";
    private String lang = "&language=id";
    private String key = "&key=AIzaSyDVhSn9HcopTvpEelnLtxj3t3tsWU_EltE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_address);

        requestQueue = Volley.newRequestQueue(this);

        try {
            lokasi = getIntent().getStringExtra("lokasi").replace("lat/lng", "").
                    replace(" ", "").replace("(", "").replace(")", "").
                    replace(":", "");
        } catch (Exception e) {
            lokasi = "";
        }

        getNearby();

        ImageButton back = (ImageButton) findViewById(R.id.back);
        final SearchView search = (SearchView) findViewById(R.id.seacrh);
        list = (RecyclerView) findViewById(R.id.list);
        layout_progress = (LinearLayout) findViewById(R.id.layout_progress);
        layout_nothing = (LinearLayout) findViewById(R.id.layout_nothing);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search.setIconified(false);
            }
        });
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                layout_nothing.setVisibility(View.GONE);
                layout_progress.setVisibility(View.VISIBLE);
                getSearch(s);

                return false;
            }

            @Override
            public boolean onQueryTextChange(final String s) {
//                layout_nothing.setVisibility(View.GONE);
//                layout_progress.setVisibility(View.VISIBLE);
//                if (s.equals("")) {
//                    getNearby();
//                } else {
//                    new Handler().postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            getSearch(s);
//                        }
//                    }, 3000);
//                }

                return false;
            }
        });

        setStatusBarColor();
    }

    private void getNearby() {
        String nearby = "/nearbysearch/";
        String location = "?location=";
        String radius = "&radius=50";

        String params = url+nearby+output+location+lokasi+radius+lang+key;
        cetakLog("Search Nearby : " + params);
        StringRequest nearbyRequest = new StringRequest(Request.Method.GET, params, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray result = jsonObject.getJSONArray("results");

                    final List<Nearby> listNearby = new ArrayList<>();
                    for (int x = 0; x < result.length(); x++) {
                        JSONObject object = result.getJSONObject(x);

                        Nearby nearby = new Nearby();
                        nearby.setLat(object.getJSONObject("geometry").getJSONObject("location").getString("lat"));
                        nearby.setLog(object.getJSONObject("geometry").getJSONObject("location").getString("lng"));
                        nearby.setIcon(object.getString("icon"));
                        nearby.setName(object.getString("name"));
                        try {
                            nearby.setVicinity(object.getString("vicinity"));
                        } catch (Exception e) {
                            nearby.setVicinity("");
                        }

                        listNearby.add(nearby);
                    }

                    NearbyAdapter nearbyAdapter = new NearbyAdapter(listNearby, requestQueue, SearchAddress.this);
                    LinearLayoutManager layoutManager = new LinearLayoutManager(SearchAddress.this);
                    list.setLayoutManager(layoutManager);
                    list.setAdapter(nearbyAdapter);

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (listNearby.size() > 0) {
                                layout_progress.setVisibility(View.GONE);
                            } else {
                                layout_progress.setVisibility(View.GONE);
                                layout_nothing.setVisibility(View.VISIBLE);
                            }
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
        requestQueue.add(nearbyRequest);
    }

    private void getSearch(String s) {
        String autocomplete = "/autocomplete/";
        String input = "?input=";

        StringBuilder searchbuild = new StringBuilder();
        String params = "";

        String country = "&components=country:id";
        try {
            String[] search = s.split(" ");
            for (int x = 0; x < search.length; x++) {
                searchbuild.append(search[x]);
                searchbuild.append("%20");
            }
            params = url+autocomplete+output+input+searchbuild.toString()+ country +lang+key;
        } catch (Exception e) {
            params = url+autocomplete+output+input+s+ country +lang+key;
        }

        cetakLog("Search AutoComplete : " + params);
        StringRequest searchRequest = new StringRequest(Request.Method.GET, params, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray predictions = jsonObject.getJSONArray("predictions");

                    final List<Search> listSearch = new ArrayList<>();
                    for (int x = 0; x < predictions.length(); x++) {
                        JSONObject object = predictions.getJSONObject(x);

                        Search search = new Search();
                        search.setReference(object.getString("reference"));
                        search.setMain_text(object.getJSONObject("structured_formatting").getString("main_text"));
                        search.setSecondary_text(object.getJSONObject("structured_formatting").getString("secondary_text"));

                        listSearch.add(search);
                    }

                    SearchAdapter searchAdapter = new SearchAdapter(listSearch, requestQueue, SearchAddress.this);
                    LinearLayoutManager layoutManager = new LinearLayoutManager(SearchAddress.this);
                    list.setLayoutManager(layoutManager);
                    list.setAdapter(searchAdapter);

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (listSearch.size() > 0) {
                                layout_progress.setVisibility(View.GONE);
                            } else {
                                layout_progress.setVisibility(View.GONE);
                                layout_nothing.setVisibility(View.VISIBLE);
                            }
                        }
                    }, 1000);
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
        requestQueue.add(searchRequest);
    }

    private void cetakLog(String response) {
        Log.e("Data", response);
    }

    private void setStatusBarColor() {
        // set status bar color
        Window window = this.getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(this.getResources().getColor(R.color.colorPrimaryDark));
        }
    }
}
