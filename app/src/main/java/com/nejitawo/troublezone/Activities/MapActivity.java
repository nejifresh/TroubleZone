package com.nejitawo.troublezone.Activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.nejitawo.troublezone.GlobalClass;
import com.nejitawo.troublezone.R;

public class MapActivity extends AppCompatActivity {
    private static GoogleMap map;
    double mylatitude;
    double mylongitude;
    FloatingActionButton fab;
    FloatingActionButton fabComment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.incident_map);
        // getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fabComment = (FloatingActionButton) findViewById(R.id.fabcomment);
        map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.maps)).getMap();
        setUpMapIfNeeded();
        Intent intent = getIntent();
        final GlobalClass globalVariable = (GlobalClass) getApplicationContext();
        mylatitude = (globalVariable.getLatitude());
        mylongitude = (globalVariable.getLongitude());

        if (map != null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            map.setMyLocationEnabled(true);
            map.setMapType(GoogleMap.MAP_TYPE_NORMAL);


            LatLng LOC = new LatLng(mylatitude, mylongitude);
            Marker marker = map.addMarker(new MarkerOptions()
                    .position(LOC)
                    .title(globalVariable.getMainimage())
                    .snippet(globalVariable.getTitle())
                    .draggable(false)

            );
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(LOC)
                    .zoom(16.5F)
                    .bearing( 0.0f )
                    .tilt( 0.0f )// viewing angle

                    .build();
            map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

            getSupportActionBar().setTitle(globalVariable.getMainTitle());

        }

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final GlobalClass globalVariable= (GlobalClass) getApplicationContext();
                Uri uri =Uri.parse("http://maps.google.com/maps?&saddr="+globalVariable.getUserLat()+
                        ","+globalVariable.getUserLong()+"&daddr="+mylatitude+","+mylongitude +"" );         //15.653293,78.809369&daddr=15.612456,78.706055");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

        fabComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mintent = new Intent(MapActivity.this, CommentsActivity.class);
                startActivity(mintent);
            }
        });
    }

        private void setUpMapIfNeeded(){
            // Do a null check to confirm that we have not already instantiated the map.
            if (map == null) {
                // Try to obtain the map from the SupportMapFragment.
                map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                        .getMap();
                // Check if we were successful in obtaining the map.
                if (map != null) {
                    setUpMap();
                }
            }
        }

    private void setUpMap() {
        final GlobalClass globalVariable = (GlobalClass) getApplicationContext();
        mylatitude = (globalVariable.getLatitude());
        mylongitude = (globalVariable.getLongitude());
        LatLng LOC = new LatLng(mylatitude, mylongitude);

        map.addMarker(new MarkerOptions()
                .position(LOC)
                .title(globalVariable.getMainimage())
                .snippet(globalVariable.getTitle())
                .draggable(false)

        );
    }
}
