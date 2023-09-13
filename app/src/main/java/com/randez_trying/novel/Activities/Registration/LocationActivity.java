package com.randez_trying.novel.Activities.Registration;

import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import com.randez_trying.novel.R;

import java.io.IOException;
import java.util.Locale;

public class LocationActivity extends AppCompatActivity {

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_location);

        ImageView back = findViewById(R.id.back);
        RelativeLayout cont = findViewById(R.id.btn_cont);
        TextView manual = findViewById(R.id.rookami);

        back.setOnClickListener(v -> {
            finish();
            overridePendingTransition(0, 0);
        });
        cont.setOnClickListener(v -> {
            if (ActivityCompat.checkSelfPermission(this, "android.permission.ACCESS_FINE_LOCATION") == 0 || ActivityCompat.checkSelfPermission(this, "android.permission.ACCESS_COARSE_LOCATION") == 0) {
                double[] location = getLocation();
                try {
                    String locality = new Geocoder(this, new Locale("ru", "RU")).getFromLocation(location[0], location[1], 1).get(0).getLocality();
                    Intent intent = new Intent(this, LocationManualActivity.class);
                    intent.putExtra("city", locality);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else {
                ActivityCompat.requestPermissions(this, new String[]{"android.permission.ACCESS_FINE_LOCATION", "android.permission.ACCESS_COARSE_LOCATION"}, 476);
            }
        });
        manual.setOnClickListener(v -> {
            startActivity(new Intent(this, LocationManualActivity.class));
            overridePendingTransition(0, 0);
        });
    }

    public double[] getLocation() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        String bestProvider = locationManager.getBestProvider(new Criteria(), false);
        if (ActivityCompat.checkSelfPermission(this, "android.permission.ACCESS_FINE_LOCATION") == 0 || ActivityCompat.checkSelfPermission(this, "android.permission.ACCESS_COARSE_LOCATION") == 0) {
            Location lastKnownLocation = locationManager.getLastKnownLocation(bestProvider);
            try {
                return new double[]{lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude()};
            } catch (NullPointerException e) {
                e.printStackTrace();
                return null;
            }
        } else {
            ActivityCompat.requestPermissions(this, new String[]{"android.permission.ACCESS_FINE_LOCATION", "android.permission.ACCESS_COARSE_LOCATION"}, 476);
            return null;
        }
    }
}
