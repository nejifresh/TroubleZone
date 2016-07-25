package com.nejitawo.troublezone.Services;

import android.app.IntentService;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.WindowManager;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.nejitawo.troublezone.GlobalClass;
import com.nejitawo.troublezone.Activities.MainActivity;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Neji on 11/16/2014.
 */
public class LocationService extends IntentService implements GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener {
    private static final String DATASTORE_MANGER_DIR = "kabooData";
    private static final String DATASTORE_NAME = "kabba";
    public static final String MyPREFERENCES = "MyPrefs";
    public static final String DocID = "myID";
    SharedPreferences sharedpreferences;
    private static final String TAG = LocationService.class.getSimpleName();
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;
    private Double currLatitude = 0.0;
    private Double currLongitude = 0.0;
    private Location mLastLocation;
    // Google client to interact with Google API
    private GoogleApiClient mGoogleApiClient;
    // boolean flag to toggle periodic location updates
    private boolean mRequestingLocationUpdates = true;
    private LocationRequest mLocationRequest;
    // Location updates intervals in sec
    private static int UPDATE_INTERVAL = 300000; // 300 sec - 5minutes interval
    private static int FATEST_INTERVAL = 50000; // 50 sec - 4 minutes interval
    private static int DISPLACEMENT = 10; // 10 meters
    private String docID = "";
    private String currentCity = "";
    private String currentCountry = "";
    String alarm = "0";
    private int eventCounter = 0;

    private static Timer timer = new Timer();

    public LocationService() {
        super(LocationService.class.getName());
    }
    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = "
                + result.getErrorCode());
    }

    @Override
    public void onConnected(Bundle arg0) {

        // Once connected with google api, get the location
        displayLocation();

          //  startLocationUpdates();

    }

    @Override
    public void onConnectionSuspended(int arg0) {
        mGoogleApiClient.connect();
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        timer.scheduleAtFixedRate(new mainTask(), 5000, 20000); //1000 equals 1second

        Log.d("SERV", "Service Started");
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        if (sharedpreferences.contains(DocID)) {
            docID = (sharedpreferences.getString(DocID, ""));

        }

        // Building the GoogleApi client
        buildGoogleApiClient();
        createLocationRequest();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
        if (mGoogleApiClient.isConnected() && mRequestingLocationUpdates) {
         //   startLocationUpdates();
        }


}

    private class mainTask extends TimerTask
    {
        public void run()
        {
            toastHandler.sendEmptyMessage(0);
        }
    }

    private final Handler toastHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
          //  Toast.makeText(getApplicationContext(), "test", Toast.LENGTH_SHORT).show();
            //Carry out the task here
            displayLocation();
            final ParseQuery<ParseObject> eventAlarms = new ParseQuery<ParseObject>("Alarms");
            //Add a time difference too
            eventAlarms.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {


                    if (e== null && objects.size() >0){
                        eventCounter = 0; //event counter variable stores number of viable alerts

                        Double eventLat;
                        Double eventLong;
                        for(ParseObject ob : objects){
                            eventLat = ob.getDouble("latitude");
                            eventLong = ob.getDouble("longitude");
                            //Calculate distance to that event
                            Location locationA = new Location("point A");
                            locationA.setLatitude((currLatitude));  //(GPS CAPTURED LATITUDE
                            locationA.setLongitude((currLongitude)); //GPS CAPTURED LONGITUDE;

                            //   Location firstLocation = myLocation.getLocation();
                            Location LocationB = new Location("point B");
                            LocationB.setLatitude((eventLat));
                            LocationB.setLongitude((eventLong));

                            float   mydistance = locationA.distanceTo(LocationB);
                            Log.i("distanceaway",String.valueOf(mydistance/1000) + " km");

                            if  ((mydistance / 1000) <= 2.0){
                                //You are within 2km  to the event
                                //Show Alarm
                                if (saveEvent(ob.getObjectId(),getApplicationContext())){
                                    //increment the event counter variable
                                    eventCounter ++;
                                } else{


                                }
                                alarm = "1";



                            }
                            //You are more than 2km away
                            //Ignore
                            alarm = "0";
                        }

                        if (eventCounter >0){
                            //we have at least one viable alarm around me
                         //   Toast.makeText(getApplicationContext(),"You are within a danger zone with " + String.valueOf(eventCounter) + " incidents"
                          //          ,Toast.LENGTH_SHORT).show();
                          //  showIncident("There is a serious incident occurring near your current location");
                            //play notication or alarm sound
                            try {
                                Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
                             final Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
                                r.play();

                                //Show the dialog
                                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getApplicationContext());
                                builder.setMessage("There is a serious incident occurring near your current location").setTitle("Incident Alert!!!!!!")
                                        .setCancelable(false)
                                        .setNegativeButton("IGNORE", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                dialogInterface.dismiss();
                                                r.stop();
                                            }
                                        })
                                        .setPositiveButton("DISPLAY", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                // show the incident by launching the app
                                                //  createdoc();
                                                r.stop();
                                                final GlobalClass globalVariable = (GlobalClass)getApplicationContext();
                                                globalVariable.setScreenIndex(1);
                                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                                intent.putExtra("ALARM",1);
                                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                startActivity(intent);
                                            }
                                        });

                                android.app.AlertDialog alert = builder.create();
                                alert.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
                                alert.show();
                            } catch (Exception el) {
                                el.printStackTrace();
                            }

                        } else{
                            //no viable incident nearby
                         /*   Toast.makeText(getApplicationContext(),"No incidents nearby"
                                    ,Toast.LENGTH_SHORT).show();*/
                        }


                    }else {
                        //Either error or not event So do nothing
                        alarm = "0";
                    }
                }
            });

        }
    };

    private URI createServerURI()
            throws URISyntaxException {
        URI uri = new URI("https://finditt.cloudant.com/kabba");
        return uri;
    }


    private void displayLocation() {

        mLastLocation = LocationServices.FusedLocationApi
                .getLastLocation(mGoogleApiClient);

        if (mLastLocation != null) {
            double latitude = mLastLocation.getLatitude();
            double longitude = mLastLocation.getLongitude();

           currLatitude = latitude;
            currLongitude = (longitude);
            final GlobalClass globalvariable = (GlobalClass)getApplicationContext();

            globalvariable.setLatitude(latitude);
            globalvariable.setLongitude(longitude);
      //      ReverseGeocodingTask myTask = new ReverseGeocodingTask(getApplicationContext());
      //      myTask.execute();// Display current city and country
          // SyncNow();

        } else {

           Log.d(TAG,"(Couldn't get the location. Make sure location is enabled on the device)");
        }
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FATEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT); // 10 meters
    }
    protected void startLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

    }
    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
    }

    @Override
    public void onLocationChanged(Location location) {
        // Assign the new location
        mLastLocation = location;

      //  Toast.makeText(getApplicationContext(), "Location changed!",
        //        Toast.LENGTH_SHORT).show();
        Log.d(TAG,"Location has changed");

        // Displaying the new location on UI
        displayLocation();
    }

    public void SyncNow(){
        try {
            // Here you should write your time consuming task...
            // Create a replicator that replicates changes from the local
// datastore to the remote database.
         //   File path = getApplicationContext().getDir(DATASTORE_MANGER_DIR, MODE_PRIVATE);
          //  DatastoreManager manager = new DatastoreManager(path.getAbsolutePath());
          //  Datastore datastore = manager.openDatastore(DATASTORE_NAME);
         //   BasicDocumentRevision retrieved = datastore.getDocument(docID);

            ParseQuery<ParseObject> query = ParseQuery.getQuery("Profiles");
            query.whereEqualTo("User", ParseUser.getCurrentUser().getUsername());
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> list, ParseException e) {
                    if (e == null) {
                        // Now let's update it with some new data. In this case, only cheatMode and score
                        // will get sent to the Parse Cloud. playerName hasn't changed.
                    for (ParseObject ob : list){
                        ob.put("Latitude", currLatitude);
                        ob.put("Longitude", currLongitude);
                        ob.put("CurrentCity",currentCity);
                        ob.put("CurrentCountry",currentCountry);
                        ob.saveInBackground();

                    }



                    }
                }
            });

// Retrieve the object by id

        } catch (Exception e) {
        }
        /**
            //do the update on latitude and longitude
            MutableDocumentRevision update = retrieved.mutableCopy();
            Map<String, Object> json = retrieved.getBody().asMap();
            json.put("Latitude", currLatitude);
            json.put("Longitude", currLongitude);
            java.util.Date date= new java.util.Date();
            java.sql.Timestamp theTime = new Timestamp(date.getTime());
            json.put("Time",theTime.toString());
            update.body = DocumentBodyFactory.create(json);

            datastore.updateDocumentFromRevision(update);
            PushReplication push = new PushReplication();
            push.username = "stedualareakindayeaselle";
            push.password = "40LB1WEG0XcxT5jo47cssuO2";
            push.source = datastore;
            try{
                push.target = createServerURI();
            } catch (URISyntaxException e){
                e.printStackTrace();
            }

            final Replicator replicator = ReplicatorFactory.oneway(push);
            CountDownLatch latch = new CountDownLatch(1);
            Listener listener = new Listener(latch);
            replicator.getEventBus().register(listener);

            try{
                replicator.start();

                latch.await();
                replicator.getEventBus().unregister(listener);
            } catch (Exception e) {

            }
            //ringProgressDialog.dismiss();
            if (replicator.getState() != Replicator.State.COMPLETE) {
                System.out.println("Error replicating TO remote");
                System.out.println(listener.error);
                Log.d(TAG,"Unable to synchronize, please try again later");


            }
            else{
//Log.d("TAG","Replication completed successfully");
                Log.d(TAG,"Location Update Concluded Successfully");


            }

        } catch(Exception e){
            e.printStackTrace();
        }
    }
         **/
}


    private class ReverseGeocodingTask extends AsyncTask<Double, Void, String> {
        Context mContext;

        public ReverseGeocodingTask(Context context) {
            super();
            mContext = context;
        }

        @Override
        protected String doInBackground (Double...params){
            Geocoder geocoder = new Geocoder(mContext);
            //double latitude = Double.parseDouble(txtLatitude.getText().toString());
            //  double longitude = Double.parseDouble(txtLongitude.getText().toString());

            List<Address> addresses = null;
            String addressText = "";

/*
            try {
                addresses = geocoder.getFromLocation(currLatitude, currLongitude, 1);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (addresses != null && addresses.size() > 0) {
                Address address = addresses.get(0);

                addressText = address.getLocality() + ", " +
                        address.getCountryName();
                currentCountry = address.getCountryName();
                currentCity = address.getLocality();

            }

*/

            final ParseQuery<ParseObject> eventAlarms = new ParseQuery<ParseObject>("Alarms");
            //Add a time difference too
            eventAlarms.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {

                    if (e== null && objects.size() >0){
                        Double eventLat;
                        Double eventLong;
                        for(ParseObject ob : objects){
                            eventLat = ob.getDouble("latitude");
                            eventLong = ob.getDouble("longitude");
                            //Calculate distance to that event
                            Location locationA = new Location("point A");
                            locationA.setLatitude((currLatitude));  //(GPS CAPTURED LATITUDE
                            locationA.setLongitude((currLongitude)); //GPS CAPTURED LONGITUDE;

                            //   Location firstLocation = myLocation.getLocation();
                            Location LocationB = new Location("point B");
                            LocationB.setLatitude((eventLat));
                            LocationB.setLongitude((eventLong));

                         float   mydistance = locationA.distanceTo(LocationB);
                            Log.i("distanceaway",String.valueOf(mydistance/1000) + " km");

                         if  ((mydistance / 1000) <= 2.0){
                             //You are within 2km  to the event
                             //Show Alarm
                            alarm = "1";
                            // Toast.makeText(getApplicationContext(),"You are within danger zone " + String.valueOf(mydistance/1000) + " km"
                             //        ,Toast.LENGTH_LONG).show();
                             showIncident("There is an incident occurring around you");

                         }
                            //You are more than 2km away
                            //Ignore
                            alarm = "0";
                        }


                    }else {
                        //Either error or not event So do nothing
                        alarm = "0";
                    }
                }
            });







            return alarm;
        }

        @Override
        protected void onPostExecute (String addressText){
            // Setting address of the touched Position
            //txtCurrentCity.setText(addressText);
          //  SyncNow();
            //SHOUT NOW TO ALERT USER
          //  Toast.makeText(getApplicationContext(),"You are within danger zone",Toast.LENGTH_LONG).show();
            showIncident("There is an incident occurring around you");
        }



    }

    private void showIncident(String message) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getApplicationContext());
        builder.setMessage(message).setTitle("Incident Alert!!!!!!")
                .setCancelable(false)
                .setNegativeButton("IGNORE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .setPositiveButton("DISPLAY", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // show the incident by launching the app
                        //  createdoc();
                    }
                });

        android.app.AlertDialog alert = builder.create();
        alert.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        alert.show();
    }

    private Boolean saveArrayData(String[] array, String arrayName,Context mContext ){
        SharedPreferences pref = mContext.getSharedPreferences("objectID",0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt(arrayName + "_size",array.length);
        for (int i = 0;i<array.length;i++){
editor.putString(arrayName +"_" + i, array[i]);
        }
return editor.commit();
    }

    private Boolean saveEvent(String eventID,Context mContext){
        SharedPreferences preferences = mContext.getSharedPreferences(eventID,0);
        if (preferences.contains(eventID)){
            return false;
        }else{
            //This event is not on our list of already captured events so dont show the alarm;
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(eventID,null);
         return    editor.commit();
        }
    }

    private String[] getArrayContent(String arrayName, Context mContext){
        SharedPreferences pref = mContext.getSharedPreferences("objectID",0);
      String [] array = null;
        int size = pref.getInt(arrayName + "_size",0);
        for (int i=0;i<size;i++){
        array[i] = pref.getString(arrayName+ "_" + i,null);
        }
        return array;

    }


}
