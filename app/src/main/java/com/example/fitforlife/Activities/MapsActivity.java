package com.example.fitforlife.Activities;
///

import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.example.fitforlife.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    SupportMapFragment supportMapFragment;
    FusedLocationProviderClient client;
    //Our Map
    GoogleMap map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        supportMapFragment.getMapAsync(this);


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                //creating marker
                MarkerOptions markerOptions = new MarkerOptions();
                //set marker position
                markerOptions.position(latLng);
                //set latitude and longitude on marker
                markerOptions.title(latLng.latitude + " : " + latLng.longitude);

                //clear the previously click position
                map.clear();
                //zoom the marker
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
                //add marker on map
                map.addMarker(markerOptions);
            }
        });
//         LatLng GoshHalav=new LatLng(33.021986, 35.446048);
//          map.addMarker(new MarkerOptions().position((GoshHalav)).title("GOSH HALAV"));
//          map.moveCamera(CameraUpdateFactory.newLatLng(GoshHalav));
    }
}
