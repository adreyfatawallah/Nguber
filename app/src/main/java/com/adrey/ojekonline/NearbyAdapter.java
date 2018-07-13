package com.adrey.ojekonline;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;

import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Muh Adrey Fatawallah on 11/9/2016.
 */

class NearbyAdapter extends RecyclerView.Adapter<NearbyAdapter.Holder> {

    private List<Nearby> listNearby = new ArrayList<>();
    private RequestQueue requestQueue;
    private Context context;

    NearbyAdapter(List<Nearby> listNearby, RequestQueue requestQueue, Context context) {
        this.listNearby = listNearby;
        this.requestQueue = requestQueue;
        this.context = context;
    }

    @Override
    public NearbyAdapter.Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_search, parent, false);

        return new Holder(itemView);
    }

    @Override
    public void onBindViewHolder(final NearbyAdapter.Holder holder, final int position) {
        ImageRequest imgRequest = new ImageRequest(listNearby.get(position).getIcon(),
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap bitmap) {
                        holder.progress.setVisibility(View.GONE);
                        holder.image.setVisibility(View.VISIBLE);
                        holder.image.setImageBitmap(bitmap);
                    }
                }, 0, 0, null,
                new Response.ErrorListener() {
                    public void onErrorResponse(VolleyError error) {
                        holder.progress.setVisibility(View.INVISIBLE);
                    }
                });
        requestQueue.add(imgRequest);

        holder.name.setText(listNearby.get(position).getName());
        holder.vicinity.setText(listNearby.get(position).getVicinity());

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra("name", listNearby.get(position).getName());
                intent.putExtra("lat", listNearby.get(position).getLat());
                intent.putExtra("long", listNearby.get(position).getLog());
                ((Activity)context).setResult(RESULT_OK, intent);
                ((Activity)context).finish();
            }
        });
    }

    @Override
    public int getItemCount() {
        return listNearby.size();
    }

    class Holder extends RecyclerView.ViewHolder {

        private CardView layout;
        private ProgressBar progress;
        private ImageView image;
        private TextView name;
        private TextView vicinity;

        Holder(View itemView) {
            super(itemView);

            layout = (CardView) itemView.findViewById(R.id.layout_item);
            progress = (ProgressBar) itemView.findViewById(R.id.progress_item);
            image = (ImageView) itemView.findViewById(R.id.icon);
            name = (TextView) itemView.findViewById(R.id.name);
            vicinity = (TextView) itemView.findViewById(R.id.vicinity);
        }
    }
}
