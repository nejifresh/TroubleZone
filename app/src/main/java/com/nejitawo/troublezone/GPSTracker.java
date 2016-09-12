package com.nejitawo.troublezone;

import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import java.util.List;

/**
 * Created by Neji on 8/4/2014.
 */
public class GPSTracker extends Service implements LocationListener {
    private final Context mContext;
    List<Address> geoCodeMatches = null;
    String Address1;
    String Address2;
    String State;
    String ZipCode;
    String Country;

    //flag for GPS Status
    boolean isGPSEnabled = false;
    //Flag for network Status
    boolean isNetworkEnabled = false;
    boolean canGetLocation = false;

    Location location; //location
    double latitude; //latitude
    double longitude; //longitude

    //The minimum distance to change updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; //10 Meters
    //The minimum time betweeen updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000*60*1; //Every 1 minute

    //declare a location manager
    protected LocationManager locationManager;

    //constructor
    public GPSTracker(Context context){
        this.mContext = context;
        getLocation();
    }

    public Location getLocation(){
        try{
            locationManager = (LocationManager)mContext.getSystemService(LOCATION_SERVICE);

            //get the GPS Status
            isGPSEnabled = locationManager.isProviderEnabled(locationManager.GPS_PROVIDER);

            //get the network status
            isNetworkEnabled = locationManager.isProviderEnabled(locationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled){
                //NO GPS or network available. Both are unavailable
            }
            else{
                this.canGetLocation = true; //Network and GPS is available
                //First get location from network provider
                if(isNetworkEnabled){
                    locationManager.requestLocationUpdates(locationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,MIN_DISTANCE_CHANGE_FOR_UPDATES,this);
                    Log.d("Network", "Network");
                    if(locationManager!=null){
                        location = locationManager.getLastKnownLocation(locationManager.NETWORK_PROVIDER);
                        if(location!=null){
                            latitude=location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    }
                }

                //Get GPS location using GPS SERVICES
                if (isGPSEnabled){
                    if(location==null){
                        locationManager.requestLocationUpdates(locationManager.GPS_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES,
                                this);
                        Log.d("gps enabled", "GPS ENABLED");
                        if(locationManager!=null){
                            location = locationManager.getLastKnownLocation(locationManager.GPS_PROVIDER);
                            if (location!=null){
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                            }
                        }
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return  location;
    }

    public void showDetails( double latitude, double longitude){
        // double latitude = getLatitude();
       // double longitude = getLongitude();
        try{


            geoCodeMatches = new Geocoder(this).getFromLocation(latitude,longitude,1);

        } catch (Exception e){
            e.printStackTrace();
        }

        if(!geoCodeMatches.isEmpty()){
            Address1 = geoCodeMatches.get(0).getAddressLine(0);
            Address2 = geoCodeMatches.get(0).getAddressLine(1);
            State = geoCodeMatches.get(0).getAdminArea();
            ZipCode = geoCodeMatches.get(0).getPostalCode();
            Country = geoCodeMatches.get(0).getCountryCode();
        }

        Toast.makeText(this, "You are in: " + Country + "\n" + "Your State is: " + State + "\n" + "Your Address is: " + Address1, Toast.LENGTH_LONG).show();

    }




    @Override
    public void onLocationChanged(Location location) {
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    /**
     * Function to get latitude
     * */

public double getLatitude(){
    if(location!=null){
        latitude = location.getLatitude();
    }
    return latitude;
}
    public double getLongitude(){
        if(location!=null){
            longitude = location.getLongitude();
        }
        return longitude;
    }

/**
 * Function to check GPS/wifi enabled
 * @return boolean
 * */
    public boolean canGetLocation(){
        return this.canGetLocation;
    }
    /**
     * Function to show settings alert dialog
     * On pressing Settings button will lauch Settings Options
     * */
    public void showSettingsAlert(){
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        //SET dialog title
        builder.setTitle("GPS Settings");
        //set dialog message
        builder.setMessage("GPS is not enabled, do you want to go to settings?");
        //on pressing settings button
        builder.setPositiveButton("Settings",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                mContext.startActivity(intent);
            }
        });

        //on presssing cancel button
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        //show alert dialog
        builder.show();
    }

    /**
     * Stop using GPS listener
     * Calling this function will stop using GPS in your app
     * */

public void stopUsingGPS(){
    if(locationManager!=null){
        locationManager.removeUpdates(GPSTracker.this);
    }
}
}
