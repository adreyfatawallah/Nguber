package com.adrey.ojekonline;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Muh Adrey Fatawallah on 3/20/2017.
 */

public class MainHistoryAdapter extends RecyclerView.Adapter<MainHistoryAdapter.Holder> {

    public MainHistoryAdapter() {
    }

    @Override
    public MainHistoryAdapter.Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_main_history, parent, false);

        return new Holder(itemView);
    }

    @Override
    public void onBindViewHolder(final MainHistoryAdapter.Holder holder, int position) {
        holder.item_history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainDriveTrace = new Intent(holder.context.getApplicationContext(), MainTraceDrive.class);
                mainDriveTrace.putExtra("from", "history");
                holder.context.startActivity(mainDriveTrace);
            }
        });
    }

    @Override
    public int getItemCount() {
        return 4;
    }

    public class Holder extends RecyclerView.ViewHolder {

        private CardView item_history;
        private Context context;

        public Holder(View itemView) {
            super(itemView);
            context = itemView.getContext();
            item_history = (CardView) itemView.findViewById(R.id.item_history);
        }
    }
}
