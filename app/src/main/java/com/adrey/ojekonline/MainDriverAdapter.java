package com.adrey.ojekonline;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
 * Created by Muh Adrey Fatawallah on 3/13/2017.
 */

public class MainDriverAdapter extends RecyclerView.Adapter<MainDriverAdapter.Holder> {

    private ArrayList<LatLng> listlonglat = new ArrayList<>();
    private String long_asal, lat_asal, nm_asal, long_tujuan, lat_tujuan, nm_tujuan;

    public MainDriverAdapter(ArrayList<LatLng> listlonglat, String long_asal, String lat_asal, String nm_asal, String long_tujuan, String lat_tujuan, String nm_tujuan) {
        this.listlonglat = listlonglat;
        this.long_asal = long_asal;
        this.lat_asal = lat_asal;
        this.nm_asal = nm_asal;
        this.long_tujuan = long_tujuan;
        this.lat_tujuan = lat_tujuan;
        this.nm_tujuan = nm_tujuan;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_main_driver, parent, false);

        return new Holder(itemView);
    }

    @Override
    public void onBindViewHolder(final Holder holder, final int position) {
        holder.select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((Activity)holder.context).finish();
            }
        });
    }

    @Override
    public int getItemCount() {
        return listlonglat.size();
    }

    public class Holder extends RecyclerView.ViewHolder {

        private Button select;

        private Context context;

        public Holder(View itemView) {
            super(itemView);
            context = itemView.getContext();

            select = (Button) itemView.findViewById(R.id.select);
        }
    }
}
