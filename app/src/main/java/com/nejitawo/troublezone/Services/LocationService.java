package com.nejitawo.troublezone.Services;

import android.Manifest;
import android.app.IntentService;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ListView;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.nejitawo.troublezone.Audio.AudioPlayer;
import com.nejitawo.troublezone.GPSTracker;
import com.nejitawo.troublezone.GlobalClass;
import com.nejitawo.troublezone.Activities.MainActivity;
import com.nejitawo.troublezone.Model.Events;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Neji on 11/16/2014.
 */
public class LocationService extends IntentService implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
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
    private int home1counter = 0;
    private int home2counter = 0;
    private int home3counter = 0;
    private GPSTracker locator;

    private List<String> eventName;

    private List<String> homeeventName1;
    private List<String> homeeventName2;
    private List<String> homeeventName3;

    private static Timer timer = new Timer();
    private AudioPlayer mAudioPlayer;
    Intent playbackServiceIntent;

    List<ParseObject> userInfo;
    List<Events> myList = new LinkedList<Events>();

    private String address1 = "";
    private Double lat1;
    private Double lon1;
    private String country1="";
    private String state1="";
    private String city1 = "";

    private String address2 = "";
    private Double lat2;
    private Double lon2;
    private String country2;
    private String state2;
    private String city2;

    private String address3 = "";
    private Double lat3;
    private Double lon3;
    private String country3;
    private String state3;
    private String city3;
    List<ParseObject> ib;
    private int count = 0;

    List<ParseObject> ib2;

    List<ParseObject> ib3;
    BillingProcessor bp;

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
        locator = new GPSTracker(getApplicationContext());


        playbackServiceIntent = new Intent(this, AudioService.class);

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

    private class mainTask extends TimerTask {
        public void run() {
            toastHandler.sendEmptyMessage(0);
        }
    }

    private final Handler toastHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            //  Toast.makeText(getApplicationContext(), "test", Toast.LENGTH_SHORT).show();
            //Carry out the task here
            new getMemberInfo().execute(); //Retrieve all information about this user and store in global variables

            mAudioPlayer = new AudioPlayer(getApplicationContext());

            final GlobalClass globalVariable = (GlobalClass) getApplicationContext();
            displayLocation();
            final ParseQuery<ParseObject> eventAlarms = new ParseQuery<ParseObject>("Alarms");

            eventAlarms.whereEqualTo("status", "NEW");
            eventAlarms.orderByDescending("_created_at");
         //  eventAlarms.whereNotEqualTo("user", ParseUser.getCurrentUser().getUsername());
            //Add a time difference too
            eventAlarms.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {


                    if (e == null && objects.size() > 0) {
                        eventCounter = 0; //event counter variable stores number of viable alerts
                        home1counter = 0;//event counter for any events in homezone 1
                        home2counter = 0;
                        home3counter = 0;
                        eventName = new ArrayList<String>();//Initialize the array
                        Double eventLat;
                        Double eventLong;
                        Date timeOfEvent;
                        long incidentTime;
                        for (ParseObject ob : objects) {
                            eventLat = ob.getDouble("latitude");
                            eventLong = ob.getDouble("longitude");
                            timeOfEvent = ob.getDate("datetimeof");
                            //Calculate user distance to that event
                            Location locationA = new Location("point A");
                            locationA.setLatitude((currLatitude));  //(GPS CAPTURED LATITUDE
                            locationA.setLongitude((currLongitude)); //GPS CAPTURED LONGITUDE;

                            //   Location firstLocation = myLocation.getLocation();
                            Location LocationB = new Location("point B");
                            LocationB.setLatitude((eventLat));
                            LocationB.setLongitude((eventLong));

                            long timeInMilliseconds = timeOfEvent.getTime();

                            Date now = Calendar.getInstance().getTime();

                            long mills = now.getTime() - timeOfEvent.getTime();
                            String diff = String.valueOf  ((mills)/(1000 * 60 * 60));
                            int timeDifference =  Integer.valueOf (diff);


                            float mydistance = locationA.distanceTo(LocationB);
                            Log.i("distanceaway", String.valueOf(mydistance / 1000) + " km");
                            if ((mydistance / 1000) <= globalVariable.getAlertRadius() && timeDifference <= 1) {
                                //You are within 2km  to the event
                                //Show Alarm
                                //can cause errors
                                try {

                                    if (saveEvent(ob.getObjectId(), getApplicationContext())) {
                                        //increment the event counter variable
                                        eventCounter++;
                                        eventName.add(ob.getString("incident"));
                                    } else {
Log.e("Nosave", "event didnt save");


                                    }
                                    alarm = "1";

                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                }


                            } //First alarm check ends here

                            //Calculate homezone1 distance to that event
                            if (globalVariable.getLat1() == null) {
                                Log.e("novalue", "nolat value");
                            } else {
                                Location HomeLoc1 = new Location("point A");
                                HomeLoc1.setLatitude((globalVariable.getLat1()));  //(GPS CAPTURED LATITUDE
                                HomeLoc1.setLongitude((globalVariable.getLon1())); //GPS CAPTURED LONGITUDE;

                                //   Location firstLocation = myLocation.getLocation();
                                Location NewLoc1 = new Location("point B");
                                NewLoc1.setLatitude((eventLat));
                                NewLoc1.setLongitude((eventLong));

                                float home1distance = HomeLoc1.distanceTo(NewLoc1);
                                if ((home1distance / 1000) <= globalVariable.getAlertRadius()) {
                                    //You are within 2km  to the event
                                    //Show Alarm
                                    try {

                                        if (saveEvent(ob.getObjectId(), getApplicationContext())) {
                                            //increment the event counter variable
                                            home1counter++;
                                            homeeventName1.add(ob.getString("incident"));
                                        } else {


                                        }
                                        alarm = "1";

                                    } catch (Exception ex) {

                                    }


                                }
                            }

                            //Now check for second homezone

                         /*   if (globalVariable.getLat2() == null) {

                            } else {
                                Location HomeLoc2 = new Location("point A");
                                locationA.setLatitude((globalVariable.getLat2()));  //(GPS CAPTURED LATITUDE
                                locationA.setLongitude((globalVariable.getLon2())); //GPS CAPTURED LONGITUDE;

                                //   Location firstLocation = myLocation.getLocation();
                                Location NewLoc2 = new Location("point B");
                                LocationB.setLatitude((eventLat));
                                LocationB.setLongitude((eventLong));

                                float home2distance = HomeLoc2.distanceTo(NewLoc2);
                                if ((home2distance / 1000) <= globalVariable.getAlertRadius()) {
                                    //You are within 2km  to the event
                                    //Show Alarm
                                    try {
                                        if (saveEvent(ob.getObjectId(), getApplicationContext())) {
                                            //increment the event counter variable
                                            home2counter++;
                                            homeeventName2.add(ob.getString("incident"));
                                        } else {


                                        }
                                        alarm = "1";

                                    } catch (Exception ex) {

                                    }


                                }
                            }*/


                           /* if (globalVariable.getLat3() == null) {

                            } else {
                                Location HomeLoc3 = new Location("point A");
                                locationA.setLatitude((globalVariable.getLat3()));  //(GPS CAPTURED LATITUDE
                                locationA.setLongitude((globalVariable.getLon3())); //GPS CAPTURED LONGITUDE;

                                //   Location firstLocation = myLocation.getLocation();
                                Location NewLoc3 = new Location("point B");
                                LocationB.setLatitude((eventLat));
                                LocationB.setLongitude((eventLong));

                                float home3distance = HomeLoc3.distanceTo(NewLoc3);
                                if ((home3distance / 1000) <= globalVariable.getAlertRadius()) {
                                    //You are within 2km  to the event
                                    //Show Alarm
                                    try {
                                        if (saveEvent(ob.getObjectId(), getApplicationContext())) {
                                            //increment the event counter variable
                                            home3counter++;
                                            homeeventName3.add(ob.getString("incident"));
                                        } else {


                                        }
                                        alarm = "1";

                                    } catch (Exception ex) {

                                    }


                                }
                            }*/

//Check current distance/////////////////////////////////////////////////////////

                        }


                        //You are more than 2km away
                        //Ignore
                        alarm = "0";


                        //Now check for the other event around homezone
                        if (home1counter > 0) {

                            //First check if user has disablead alarm
                            if (globalVariable.getEnableAlerts() == false) {

                            } else {
                                //Alarm is not disabled
                                try {
                                    Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
                                    final Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
                                    // r.play();
                                    String alertMessage = "";
                                    //Show the dialog
                                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getApplicationContext());
                                    //  builder.setMessage("There is a serious incident occurring near your current location").setTitle("Incident Alert!!!!!!")
                                    if (home1counter == 1) {
                                        alertMessage = homeeventName1.get(0) + " has occured around your Primary Home Zone";

                                    } else {
                                        alertMessage = homeeventName1.get(0) + " and " + home1counter + " other incidents have occured around your Primary Home Zone";
                                    }
                                    builder.setMessage(alertMessage).setTitle("HomeZone Alert!!")

                                            .setCancelable(false)
                                            .setNegativeButton("IGNORE", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    dialogInterface.dismiss();
                                                    //   r.stop();
                                                }
                                            })
                                            .setPositiveButton("DISPLAY", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    // show the incident by launching the app
                                                    //  createdoc();
                                                    //    r.stop();
                                                    final GlobalClass globalVariable = (GlobalClass) getApplicationContext();
                                                    globalVariable.setScreenIndex(1);
                                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                                    intent.putExtra("ALARM", 1);
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


                            }


                        } //homecounter ends here


                        //Now check for the other event around homezone
                       /* if (home2counter > 0) {

                            //First check if user has disablead alarm
                            if (globalVariable.getEnableAlerts() == false) {

                            } else {
                                //Alarm is not disabled find a way of showing this alert for this homezone
                                try {
                                    Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
                                    final Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
                                    //   r.play();
                                    String alertMessage = "";
                                    //Show the dialog
                                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getApplicationContext());
                                    //  builder.setMessage("There is a serious incident occurring near your current location").setTitle("Incident Alert!!!!!!")
                                    if (home2counter == 1) {
                                        alertMessage = homeeventName2.get(0) + " has occured around your Secondary Home Zone";

                                    } else {
                                        alertMessage = homeeventName2.get(0) + " and " + home2counter + " other incidents have occured around your Secondary Home Zone";
                                    }
                                    builder.setMessage(alertMessage).setTitle("HomeZone Alert!!")

                                            .setCancelable(false)
                                            .setNegativeButton("IGNORE", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    dialogInterface.dismiss();
                                                    //  r.stop();
                                                }
                                            })
                                            .setPositiveButton("DISPLAY", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    // show the incident by launching the app
                                                    //  createdoc();
                                                    //    r.stop();
                                                    final GlobalClass globalVariable = (GlobalClass) getApplicationContext();
                                                    globalVariable.setScreenIndex(1);
                                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                                    intent.putExtra("ALARM", 1);
                                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                    startActivity(intent);
                                                }
                                            });

                                       *//* android.app.AlertDialog alert = builder.create();
                                        alert.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
                                        alert.show();*//*
                                } catch (Exception el) {
                                    el.printStackTrace();
                                }


                            }


                        } //homecounter ends here
*/

                     /*   //Now check for the other event around homezone
                        if (home3counter > 0) {

                            //First check if user has disablead alarm
                            if (globalVariable.getEnableAlerts() == false) {

                            } else {
                                //Alarm is not disabled
                                try {
                                    Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
                                    final Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
                                    r.play();
                                    String alertMessage = "";
                                    //Show the dialog
                                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getApplicationContext());
                                    //  builder.setMessage("There is a serious incident occurring near your current location").setTitle("Incident Alert!!!!!!")
                                    if (home3counter == 1) {
                                        alertMessage = homeeventName3.get(0) + " has occured around your Tetiary Home Zone";

                                    } else {
                                        alertMessage = homeeventName3.get(0) + " and " + home3counter + " other incidents have occured around your Tetiary Home Zone";
                                    }
                                     *//*   builder.setMessage(alertMessage).setTitle("HomeZone Alert!!")

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
                                                        final GlobalClass globalVariable = (GlobalClass) getApplicationContext();
                                                        globalVariable.setScreenIndex(1);
                                                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                                        intent.putExtra("ALARM", 1);
                                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                        startActivity(intent);
                                                    }
                                                });

                                        android.app.AlertDialog alert = builder.create();
                                        alert.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
                                        alert.show();*//*
                                } catch (Exception el) {
                                    el.printStackTrace();
                                }


                            }


                        } //homecounter ends here

*/
                        //Na here o
                        //It appears the alarm part should happen here
                        //Now check for events around me and trigger

                        if (eventCounter > 0) {

                            //First check if user has disablead alarm
                            if (globalVariable.getEnableAlerts() == false || globalVariable.getEnableAlerts() == null) {

                            } else {
                                //Alarm is not disabled
                                try {
                                    Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
                                    final Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
                                    //   r.play();
                                    startService(playbackServiceIntent);
                                    String alertMessage = "";
                                    //Show the dialog
                                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getApplicationContext());
                                    //  builder.setMessage("There is a serious incident occurring near your current location").setTitle("Incident Alert!!!!!!")
                                    if (eventCounter == 1) {
                                        alertMessage = eventName.get(0) + " Nearby";

                                    } else {
                                        alertMessage = eventName.get(0) + " and " + eventCounter + " other incidents nearby";
                                    }
                                    builder.setMessage(alertMessage).setTitle("Troublezone Alert!!")

                                            .setCancelable(false)
                                            .setNegativeButton("IGNORE", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    dialogInterface.dismiss();
                                                    // r.stop();
                                                    stopService(playbackServiceIntent);
                                                }
                                            })
                                            .setPositiveButton("DISPLAY", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    // show the incident by launching the app
                                                    //  createdoc();
                                                    // r.stop();
                                                    stopService(playbackServiceIntent);
                                                    final GlobalClass globalVariable = (GlobalClass) getApplicationContext();
                                                    globalVariable.setScreenIndex(1);
                                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                                    intent.putExtra("ALARM", 1);
                                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                    startActivity(intent);
                                                }
                                            });

                                    android.app.AlertDialog alert = builder.create();
                                    alert.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
                                  //  alert.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
                                    alert.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON|
                                            WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD|
                                            WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED|
                                            WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
                                    alert.show();
                                } catch (Exception el) {
                                    el.printStackTrace();
                                }


                            }

                        }//event counter code ends here


                    } else {
                        //Either error or no event So do nothing
                        alarm = "0";
                    }
                }

            });

        }
    };


    private class getMemberInfo extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //  count = 0;
         //   progressView.setVisibility(View.VISIBLE);
         //   progressView.startAnimation();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            //User Settings
            ParseQuery<ParseObject> userQuery = new ParseQuery<ParseObject>("UserSettings");
            userQuery.whereEqualTo("user", ParseUser.getCurrentUser().getUsername());
            try {
                userInfo= userQuery.find();
            } catch (Exception e) {
                e.printStackTrace();
            }

            ParseQuery<ParseObject> homeZone = new ParseQuery<ParseObject>("HomeZones");
            homeZone.whereEqualTo("user", ParseUser.getCurrentUser().getUsername());
            try {
                ib= homeZone.find();
            } catch (Exception e) {
                e.printStackTrace();
            }

//Homezone 2
            ParseQuery<ParseObject> homeZone2 = new ParseQuery<ParseObject>("HomeZones2");
            homeZone2.whereEqualTo("user", ParseUser.getCurrentUser().getUsername());
            try {
                ib2= homeZone2.find();
            } catch (Exception e) {
                e.printStackTrace();
            }
            //homezone3
            ParseQuery<ParseObject> homeZone3 = new ParseQuery<ParseObject>("HomeZones3");
            homeZone3.whereEqualTo("user", ParseUser.getCurrentUser().getUsername());
            try {
                ib3= homeZone3.find();
            } catch (Exception e) {
                e.printStackTrace();
            }



            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            final GlobalClass globalVariable = (GlobalClass) getApplicationContext();
            if (ib==null){

            } else{
                for (ParseObject obj : ib) {
                    address1 = (obj.getString("address"));
                    lat1 = obj.getDouble("latitude");
                    lon1 = obj.getDouble("longitude");
                    state1 = obj.getString("state");
                    country1 = obj.getString("country");
                    city1 = obj.getString("city");
                    count++;

                    globalVariable.setLat1(lat1);
                    globalVariable.setLon1(lon1);
                }

            }

            if (ib2==null){

            } else{
                for (ParseObject obj : ib2) {
                    address2 = (obj.getString("address"));
                    lat2 = obj.getDouble("latitude");
                    lon2 = obj.getDouble("longitude");
                    state2 = obj.getString("state");
                    country2 = obj.getString("country");
                    city2 = obj.getString("city");
                    count++;

                    globalVariable.setLat2(lat2);
                    globalVariable.setLon2(lon2);
                }
            }

            if (ib3==null){

            } else {
                for (ParseObject obj : ib3) {
                    address3 = (obj.getString("address"));
                    lat3 = obj.getDouble("latitude");
                    lon3 = obj.getDouble("longitude");
                    state3 = obj.getString("state");
                    country3 = obj.getString("country");
                    city3 = obj.getString("city");
                    count++;

                    globalVariable.setLat3(lat3);
                    globalVariable.setLon3(lon3);
                }
            }

            if (userInfo==null){

            }else{
                for(ParseObject user:userInfo){
                    globalVariable.setPhone1(user.getString("phone1"));
                    globalVariable.setPhone2(user.getString("phone2")) ;
                    globalVariable.setPhone3(user.getString("phone3"));
                    globalVariable.setEnableAlerts(user.getBoolean("enablealerts"));
                    globalVariable.setEnableSMS(user.getBoolean("enablesos"));
                    globalVariable.setTimeLineLocation(user.getString("currenttimeline"));
                    globalVariable.setAlertRadius(user.getInt("alertradius"));
                }
            }

            if (globalVariable.getTimeLineLocation().isEmpty()){
                globalVariable.setTimeLineLocation(globalVariable.getCountry());

            } else{
                //retain the current value gotten from the database

            }
//Fields are initialized here
           /* txtNumZones.setText(String.valueOf(count));
            smsSwitch.setChecked(enableSOS);
            alertSwitch.setChecked(enableAlerts);
            seekBar.setProgress(alertradius);
            textView.setText("Alert radius: " + String.valueOf(alertradius) + " Km" );
            if (currentTimeline.isEmpty()){
                txtMyCountry.setText(globalVariable.getCountry());

            } else{
                txtMyCountry.setText(currentTimeline);

            }*/

            //Start this service for first time installers



         /*   progressView.stopAnimation();
            progressView.setVisibility(View.INVISIBLE);*/



        }
    }


    private URI createServerURI()
            throws URISyntaxException {
        URI uri = new URI("https://finditt.cloudant.com/kabba");
        return uri;
    }


    private void displayLocation() {

     /*   if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }*/
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
//USE gps tracker utility from Finditt
            try{
                mLastLocation = locator.getLocation();
                double latitude = mLastLocation.getLatitude();
                double longitude = mLastLocation.getLongitude();
                final GlobalClass globalvariable = (GlobalClass)getApplicationContext();
                globalvariable.setLatitude(latitude);
                globalvariable.setLongitude(longitude);
                //  new ReverseGeocodingTask(getActivity()).execute();
                Log.d(TAG,"(Couldn't get the location via google location services)");

            }catch (Exception e){
                e.printStackTrace();
                Log.e(TAG,"(Couldn't get the location both ways. Make sure location is enabled on the device)");

            }


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
        SharedPreferences preferences = mContext.getSharedPreferences(eventID,MODE_PRIVATE);
        if (preferences.contains(eventID)){
            return false;
        }else{
            //This event is not on our list of already captured events so dont show the alarm;
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(eventID,null);

         return editor.commit();
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



