package com.adrey.ojekonline;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;

/**
 * Created by Muh Adrey Fatawallah on 11/12/2016.
 */

public class SplashOjek extends AppCompatActivity {

    private static final int PERMISSION_FINE_LOCATION = 11;
    private static final int PERMISSION_COARSE_LOCATION = 22;

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;

    private boolean active = true;
    private int splash_time = 3000;

    private CardView cardView;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_ojek);

        buildGoogleApiClient();

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        cardView = (CardView) findViewById(R.id.cardView);
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent setting = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(setting);
            }
        });

        setStatusBarColor();

        startAnimation();

        Thread thread = new Thread() {
            public void run() {
                try {
                    int waited = 0;
                    while (active && (waited < splash_time)) {
                        sleep(100);
                        if (active) {
                            waited += 100;
                        }
                    }
                } catch (InterruptedException e) {
                    // TODO: handle exception
                } finally {
                    active = false;
                    if (!(active)) {
                        cekPermission();
                    }
                }
            }
        };
        thread.start();
    }

    private void cekPermission() {
        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSION_FINE_LOCATION);
        } else if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION},
                    PERMISSION_COARSE_LOCATION);
        } else {
            getStatusGPS();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_FINE_LOCATION) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION},
                        PERMISSION_COARSE_LOCATION);
            }
        } else if (requestCode == PERMISSION_COARSE_LOCATION) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getStatusGPS();
            }
        }
    }
    private void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .build();

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        getStatusGPS();
    }

    private void getStatusGPS() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient,
                        builder.build());

        // Cek status code from result location service
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {
                final Status status = locationSettingsResult.getStatus();
                final LocationSettingsStates locationSettingsStates = locationSettingsResult.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        if (!(active)) {
                            cardView.setVisibility(View.GONE);
                            progressBar.setVisibility(View.VISIBLE);
                            showActivityNguber();
                        }
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        if (!(active)) {
                            progressBar.setVisibility(View.GONE);
                            cardView.setVisibility(View.VISIBLE);
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        if (!(active)) {
                            progressBar.setVisibility(View.GONE);
                            cardView.setVisibility(View.VISIBLE);
                        }
                        break;
                }
            }
        });
    }

    private void showActivityNguber() {
        finish();
        Intent login = new Intent(getApplicationContext(), MainLogin.class);
        startActivity(login);
    }

    private void startAnimation() {
        Animation animFl = AnimationUtils.loadAnimation(this, R.anim.alpha);
        animFl.reset();
        FrameLayout fl = (FrameLayout)findViewById(R.id.frameLayout);
        fl.clearAnimation();
        fl.startAnimation(animFl);

        Animation animPb = AnimationUtils.loadAnimation(this, R.anim.alpha);
        animPb.reset();
        ProgressBar pb = (ProgressBar)findViewById(R.id.progressBar);
        pb.clearAnimation();
        pb.startAnimation(animPb);

        Animation animTv = AnimationUtils.loadAnimation(this, R.anim.translate);
        animTv.reset();
        TextView tv = (TextView) findViewById(R.id.textView);
        tv.clearAnimation();
        tv.startAnimation(animTv);
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

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }
    @Override
    protected void onResume() {
        super.onResume();
        getStatusGPS();
    }
    @Override
    protected void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }
}
