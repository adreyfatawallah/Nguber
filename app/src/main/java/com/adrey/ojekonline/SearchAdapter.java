package com.adrey.ojekonline;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Muh Adrey Fatawallah on 11/9/2016.
 */

class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.Holder> {

    private List<Search> listSearch = new ArrayList<>();
    private com.android.volley.RequestQueue requestQueue;
    private Context context;

    SearchAdapter(List<Search> listSearch, com.android.volley.RequestQueue requestQueue, Context context) {
        this.listSearch = listSearch;
        this.requestQueue = requestQueue;
        this.context = context;
    }

    @Override
    public SearchAdapter.Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_search, parent, false);

        return new Holder(itemView);
    }

    @Override
    public void onBindViewHolder(final SearchAdapter.Holder holder, final int position) {
        holder.progress_img.setVisibility(View.GONE);
        holder.image.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_pos));
        holder.image.setVisibility(View.VISIBLE);
        holder.main_text.setText(listSearch.get(position).getMain_text());
        holder.second_text.setText(listSearch.get(position).getSecondary_text());
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.progress.setVisibility(View.VISIBLE);
                holder.layout.setVisibility(View.GONE);
                getDetail(listSearch.get(position).getMain_text() ,listSearch.get(position).getReference(), holder);
            }
        });
    }

    private void getDetail(final String main_text, String reference, final Holder holder) {
        String params = "https://maps.googleapis.com/maps/api/place/details/json" +
                "?reference=" +reference+
                "&sensor=false" +
                "&language=id" +
                "&key=AIzaSyDVhSn9HcopTvpEelnLtxj3t3tsWU_EltE";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, params, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONObject result = jsonObject.getJSONObject("result");
                    JSONObject geometry = result.getJSONObject("geometry");
                    JSONObject location = geometry.getJSONObject("location");
                    String lat = location.getString("lat");
                    String log = location.getString("lng");

                    holder.progress.setVisibility(View.GONE);
                    holder.layout.setVisibility(View.VISIBLE);

                    Intent intent = new Intent();
                    intent.putExtra("name", main_text);
                    intent.putExtra("lat", lat);
                    intent.putExtra("long", log);
                    ((Activity)context).setResult(RESULT_OK, intent);
                    ((Activity)context).finish();
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

    @Override
    public int getItemCount() {
        return listSearch.size();
    }

    class Holder extends RecyclerView.ViewHolder {
        private CardView layout;
        private ProgressBar progress, progress_img;
        private ImageView image;
        private TextView main_text;
        private TextView second_text;

        Holder(View itemView) {
            super(itemView);

            layout = (CardView) itemView.findViewById(R.id.layout_item);
            progress = (ProgressBar) itemView.findViewById(R.id.progress);
            progress_img = (ProgressBar) itemView.findViewById(R.id.progress_item);
            image = (ImageView) itemView.findViewById(R.id.icon);
            main_text = (TextView) itemView.findViewById(R.id.name);
            second_text = (TextView) itemView.findViewById(R.id.vicinity);
        }
    }
}
