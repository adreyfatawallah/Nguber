package com.adrey.ojekonline;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;

/**
 * Created by Muh Adrey Fatawallah on 3/20/2017.
 */

public class MainHistory extends AppCompatActivity {

    private RecyclerView history;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_history);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        MainHistoryAdapter mainHistoryAdapter = new MainHistoryAdapter();
        history = (RecyclerView) findViewById(R.id.history);
        history.setHasFixedSize(true);
        history.setLayoutManager(linearLayoutManager);
        history.setAdapter(mainHistoryAdapter);

        setupToolbar();
    }

    private void setupToolbar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("History");
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
}
