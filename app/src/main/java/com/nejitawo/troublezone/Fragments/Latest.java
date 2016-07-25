package com.nejitawo.troublezone.Fragments;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.nejitawo.troublezone.Adapters.EventsAdapter;
import com.nejitawo.troublezone.Adapters.GoogleCardsSocialAdapter;
import com.nejitawo.troublezone.Capture.NewPicture;
import com.nejitawo.troublezone.GlobalClass;
import com.nejitawo.troublezone.Model.Events;
import com.nejitawo.troublezone.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;


public class Latest extends Fragment implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {
    LinearLayout lvContent;
    private FloatingActionButton fab;
    private String incident = "";
    private String location = "";
    private Double nLatitude = 0.01;
    private Double nLongitude = 0.01;
    private String currentCity = "";
    private String currentCountry = "";
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;

    private Location mLastLocation;

    // Google client to interact with Google API
    private GoogleApiClient mGoogleApiClient;

    private boolean mRequestingLocationUpdates = false;

    private LocationRequest mLocationRequest;

    // Location updates intervals in sec
    private static int UPDATE_INTERVAL = 10000; // 10 sec
    private static int FATEST_INTERVAL = 5000; // 5 sec
    private static int DISPLACEMENT = 10; // 10 mete
    //   private OnFragmentInteractionListener mListener;
    private ListView listView1;
    List<ParseObject> ob;
    ProgressDialog mProgressDialog;
    CircularProgressView progressView;
    private RecyclerView mRecyclerView;
    private StaggeredGridLayoutManager mStaggeredGridLayoutManager;
    private EventsAdapter mAdapter;


    List<Events> myList = new LinkedList<Events>();
    private ListView choiceList;
    public Latest() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.frag_mychoice, container, false);
        fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPrompt();
            }
        });

        choiceList = (ListView) rootView.findViewById(R.id.mychoiceList);
        lvContent = (LinearLayout)rootView.findViewById(R.id.visibleContent);
        progressView = (CircularProgressView) rootView.findViewById(R.id.progress_view);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.my_recycler_view);
        mStaggeredGridLayoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mStaggeredGridLayoutManager);
        mRecyclerView.setAdapter(null);
        ob = null;
        final GlobalClass globalClass = (GlobalClass)getActivity().getApplicationContext();

        buildGoogleApiClient();
        createLocationRequest();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
        if (mGoogleApiClient.isConnected() && mRequestingLocationUpdates) {
            //startLocationUpdates();
            displayLocation();
        }

        //Now load data
        new loadData().execute();

        return rootView;
    }

    public class loadData extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressView.setVisibility(View.VISIBLE);
            progressView.startAnimation();


        }

        @Override
        protected Void doInBackground(Void... params) {

            //This combines two queries to show completed and inprogress tasks for the user


            ParseQuery <ParseObject> eventQuery = new ParseQuery<ParseObject>("Alarms");
            eventQuery.whereEqualTo("show", "YES");


            eventQuery.orderByDescending("_created_at");
            try {
                ob = eventQuery.find();

            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (ob.size()==0){
                lvContent.setVisibility(View.VISIBLE);
            }
            myList.clear();
            try {
                for (ParseObject choices : ob) {

                    Events t = Events.giveFullDetails(choices, getActivity().getApplicationContext());
                    if (t != null) {
                        myList.add(t);
                    }else{
                        return;

                    }

                    // Toast.makeText(getActivity(),String.valueOf(myList.size()),Toast.LENGTH_LONG).show();

                    // mAdapter.notifyDataSetChanged();

                }
            } catch (Exception ex) {
                ex.printStackTrace();
                // Toast.makeText(getActivity(),"error in list",Toast.LENGTH_LONG).show();
                Log.e("terror", "Error occured - " + ex.getMessage());

            }
           // Collections.sort(myList);
            mAdapter = new EventsAdapter(getActivity(), myList);

            mAdapter.setOnItemClickListener(onItemClickListener);

            mRecyclerView.setAdapter(mAdapter);

            progressView.stopAnimation();
            progressView.setVisibility(View.INVISIBLE);


        }


    }

    EventsAdapter.OnItemClickListener onItemClickListener = new EventsAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(View view, int position) {
//
            // Property thisDj = (myList.get(position));
            final GlobalClass globalVariable = (GlobalClass) getActivity().getApplicationContext();
            /**
             globalVariable.setTitle(thisChoice.getTitle());
             globalVariable.setDescription(thisChoice.getDescription());
             globalVariable.setImageURL(thisChoice.getImageURL());
             globalVariable.setDuration(thisChoice.getDuration());
             globalVariable.setMainTitle(thisChoice.getMainTitle());
             globalVariable.setSectionA(thisChoice.getSectionA());
             globalVariable.setSectionB(thisChoice.getSectionB());
             globalVariable.setStatus(thisChoice.getStatus());
             globalVariable.setId(thisChoice.getId());
             Intent intent = new Intent(getActivity(), DetailsActivity.class);
             startActivity(intent);


             **/


            progressView.stopAnimation();
            progressView.setVisibility(View.INVISIBLE);



        }
    };


    public void myListView(final List theStars)  {
        getActivity().runOnUiThread(new Runnable() {
            public void run() {
                List<Events> Stars = theStars;
                Collections.sort(Stars);
                GoogleCardsSocialAdapter adapter = new GoogleCardsSocialAdapter(getActivity().getApplicationContext(), Stars);
                //listView1.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                listView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                        /**
                         cProgressDialog = new ProgressDialog(getActivity());
                         // Set progressdialog title
                         cProgressDialog.setTitle("Getting information");
                         // Set progressdialog message
                         cProgressDialog.setMessage("Please wait...");
                         cProgressDialog.setIndeterminate(false);
                         cProgressDialog.setCancelable(false);
                         // Show progressdialog
                         cProgressDialog.show();
                         **/
                        progressView.setVisibility(View.VISIBLE);
                        progressView.startAnimation();
                        Events thisStar = (Events) (adapterView.getItemAtPosition(position));
                        // Toast.makeText(getActivity(), "Touched record", Toast.LENGTH_LONG).show();
                        final GlobalClass globalVariable = (GlobalClass) getActivity().getApplicationContext();
                   /**
                        globalVariable.setFullName(thisStar.getFullName());
                        globalVariable.setDescription(thisStar.getDescription());
                        globalVariable.setOccupation(thisStar.getOccupation());
                        globalVariable.setUsername(thisStar.getUsername());
                        //  globalVariable.setState(thisWife.getState());
                        globalVariable.setCurrentCity(thisStar.getCurrentCity());
                        globalVariable.setCurrentCountry(thisStar.getCurrentCountry());

                        globalVariable.setDocID(thisStar.getDocID());
                        globalVariable.setHobbies(thisStar.getHobbies());
                        globalVariable.setImage1(thisStar.getImage1());
                        globalVariable.setImage2(thisStar.getImage2());
                        globalVariable.setImage3(thisStar.getImage3());
                        globalVariable.setImage4(thisStar.getImage4());

                        globalVariable.setState(thisStar.getState());
                        //   Toast.makeText(getActivity(), thisWife.getFullName().toString() + position, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getActivity(), DetailsActivity.class);
                        intent.putExtra(DetailsActivity.EXTRA_PARAM_ID, position);
                        progressView.stopAnimation();
                        progressView.setVisibility(View.INVISIBLE);
                        startActivity(intent);
**/

                    }
                });
                listView1.setAdapter(adapter);

            }
        });


    }

    public class launchParseDialog extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Create a progressdialog
          //  mProgressDialog = new ProgressDialog(getActivity());
            // Set progressdialog title
          //  mProgressDialog.setTitle("Getting People Around you..");
            // Set progressdialog message
         //   mProgressDialog.setMessage("Please wait...");
         //   mProgressDialog.setIndeterminate(false);
          //  mProgressDialog.setCancelable(false);
            // Show progressdialog
            // mProgressDialog.show();
            progressView.setVisibility(View.VISIBLE);
            progressView.startAnimation();


        }

        @Override
        protected Void doInBackground(Void... params) {
            /**
             ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(
             "Profiles");
             // query.orderByDescending("_created_at");
             query.whereEqualTo("Status", "Trending");
             **/

            ParseQuery eventQuery = new ParseQuery("Alarms");
            eventQuery.whereEqualTo("show", "YES");
            eventQuery.orderByDescending("_created_at");


        //    ParseQuery alarmQuery = new ParseQuery("Alarms");
         //   alarmQuery.whereEqualTo("Status", "Star");

        //    List<ParseQuery<ParseObject>> queries = new ArrayList<ParseQuery<ParseObject>>();
        //    queries.add(myQuery1);
         //   queries.add(myQuery2);

          //  ParseQuery<ParseObject> query = ParseQuery.or(queries);

            //   query.whereNotEqualTo("MyStatus","UnAvailable");
            try{
                ob = eventQuery.find();
            }catch (Exception ex){
                ex.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            // Locate the listview in listview_main.xml
            List<Events> myStar = new ArrayList<Events>();
            for (ParseObject driver : ob){
                try {
                    Events t = Events.giveFullDetails(driver, getActivity().getApplicationContext());
                    if (t != null) {
                        myStar.add(t);
                    }
                    myListView(myStar);

                } catch(Exception e){
                    // Toast.makeText(getActivity(),e.getMessage(),Toast.LENGTH_LONG).show();
                    //Log.e("error",e.getMessage());
                }


            }
//            mProgressDialog.dismiss();
            progressView.stopAnimation();
            progressView.setVisibility(View.INVISIBLE);

        }
    }


    void showPrompt() {
        //Inflate layout
        LayoutInflater li = LayoutInflater.from(getActivity());
        final View promptsView = li.inflate(R.layout.alertdialog, null);
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setView(promptsView);
        final RelativeLayout rlRobbery = (RelativeLayout) promptsView
                .findViewById(R.id.rlRobbery);
        rlRobbery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // Toast.makeText(getActivity(), "Clicked robbery", Toast.LENGTH_LONG).show();
                incident = "Armed Robbery";

                buildConfirmAlert();
            }
        });

        final RelativeLayout rlFire = (RelativeLayout) promptsView
                .findViewById(R.id.rlFire);
        rlFire.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                incident = "Fire Incident";
                buildConfirmAlert();
            }
        });
        final RelativeLayout rlAccident = (RelativeLayout) promptsView
                .findViewById(R.id.rlRoad);
        rlAccident.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                incident = "Road Accident";
                buildConfirmAlert();
            }
        });
        final RelativeLayout rlKidnap = (RelativeLayout) promptsView
                .findViewById(R.id.rlKidnap);
        rlKidnap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                incident = "Kidnapping";
                buildConfirmAlert();
            }
        });
        final RelativeLayout rlMedical = (RelativeLayout) promptsView
                .findViewById(R.id.rlMedical);
        rlMedical.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                incident = "Medical Emergency";
                buildConfirmAlert();
            }
        });
        final RelativeLayout rlDomestic = (RelativeLayout) promptsView
                .findViewById(R.id.rlDomestic);
        rlDomestic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                incident = "Domestic Violence";
                buildConfirmAlert();
            }
        });
        final RelativeLayout rlSexual = (RelativeLayout) promptsView
                .findViewById(R.id.rlSexual);
        rlSexual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                incident = "Sexual Harrassment";
                buildConfirmAlert();
            }
        });


        alertDialogBuilder.setCancelable(false).setTitle("Raise Alarm!!")

                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
        alertDialogBuilder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        alertDialogBuilder.create();
        alertDialogBuilder.show();


    }

    private void buildConfirmAlert() {
        android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(getActivity());
        // Setting Dialog Title
        alertDialog.setTitle("Confirm Location...");

        // Setting Dialog Message
        alertDialog.setMessage("Where is this incident happening?");

        // Setting Icon to Dialog
        alertDialog.setIcon(android.R.drawable.ic_menu_save);

        // Setting Positive "Yes" Button
        alertDialog.setPositiveButton("MY HOME ZONE",
                new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, int which) {
                        location = "HomeZone";
                        final ProgressDialog progressDialog = ProgressDialog.show(getActivity(), "Raising Alarm ", "Incident alert in progress..");
                        progressDialog.setCancelable(false);

                        ParseQuery<ParseObject> homeZone = new ParseQuery<ParseObject>("HomeZones");
                        homeZone.whereEqualTo("user", ParseUser.getCurrentUser().getUsername());
                        homeZone.findInBackground(new FindCallback<ParseObject>() {
                            @Override
                            public void done(List<ParseObject> objects, ParseException e) {
                                if (e == null) {
                                    if (objects.size() > 0) {
                                        String homeaddress = "";
                                        Double lat = 0.01;
                                        Double lon = 0.01;
                                        String location = "";

                                        for (ParseObject ob : objects) {
                                            homeaddress = ob.getString("address");
                                            lat = ob.getDouble("latitude");
                                            lon = ob.getDouble("longitude");
                                            location = ob.getString("geolocation");
                                        }

                                        Calendar c = Calendar.getInstance();
                                        Date today = c.getTime();
                                        //Now create the alarm
                                     final  ParseObject newAlarm = new ParseObject("Alarms");
                                        newAlarm.put("user", ParseUser.getCurrentUser().getUsername());
                                        newAlarm.put("phone", ParseUser.getCurrentUser().get("phone"));
                                        newAlarm.put("userimage", ParseUser.getCurrentUser().get("Image"));
                                        newAlarm.put("type", "HomeZone");
                                        newAlarm.put("userAddress", homeaddress);
                                        newAlarm.put("incident", incident);
                                        newAlarm.put("latitude", lat);
                                        newAlarm.put("longitude", lon);
                                        newAlarm.put("location",location);
                                        newAlarm.put("datetimeof", today);
                                        newAlarm.put("status", "NEW");
                                        newAlarm.saveInBackground(new SaveCallback() {
                                            @Override
                                            public void done(ParseException e) {
                                                if (e == null) {
                                                    dialog.dismiss();
                                                    progressDialog.dismiss();
                                                    final GlobalClass globalVariable = (GlobalClass)getActivity().getApplicationContext();
                                                    globalVariable.setIncidentID(newAlarm.getObjectId());

                                                    showAlert("Alarm raised successfully. Do you want to post pictures or videos relating to this incident?");
                                                }
                                            }
                                        });

                                    } else {
//No home zone was detected
                                        Log.e("error", e.getMessage(), e);
                                        showFailure("No Home Zone detected. Please ensure you have setup one");
                                        dialog.dismiss();
                                        progressDialog.dismiss();

                                    }


                                } else {
                                    //error happened here
                                    Log.e("error", e.getMessage(), e);
                                    showFailure("Error occurred");
                                    dialog.dismiss();
                                    progressDialog.dismiss();

                                }
                            }
                        });
//now save


                    }
                });

        // Setting Negative "NO" Button
        alertDialog.setNegativeButton("CURRENT LOCATION",
                new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, int which) {
                        // Write your code here to invoke NO event
                        final ProgressDialog progressDialog = ProgressDialog.show(getActivity(), "Raising Alarm ", "Incident alert in progress..");
                        progressDialog.setCancelable(false);

                        if (nLatitude==0.01 && nLongitude == 0.01){
                            buildAlertMessageNoGps();

                        } else {

                            location = "CurrentLocation";
                            Calendar c = Calendar.getInstance();
                            Date today = c.getTime();
                            //Now create the alarm
                            final ParseObject newAlarm = new ParseObject("Alarms");
                            newAlarm.put("user", ParseUser.getCurrentUser().getUsername());
                            newAlarm.put("phone", ParseUser.getCurrentUser().get("phone"));
                            newAlarm.put("userimage", ParseUser.getCurrentUser().get("Image"));
                            newAlarm.put("type", location);
                            newAlarm.put("userAddress", location);
                            newAlarm.put("incident", incident);
                            newAlarm.put("latitude", nLatitude);
                            newAlarm.put("longitude", nLongitude);
                            newAlarm.put("datetimeof", today);
                            newAlarm.put("location",currentCity + ", "+ currentCountry);
                            newAlarm.put("status", "NEW");
                            newAlarm.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if (e == null) {
                                        dialog.dismiss();
                                        progressDialog.dismiss();
                                        final GlobalClass globalVariable = (GlobalClass)getActivity().getApplicationContext();
                                        globalVariable.setIncidentID(newAlarm.getObjectId());

                                        showAlert("Alarm raised successfully. Do you want to post pictures or videos relating to this incident?");
                                    }
                                }
                            });
                        }
                      //  dialog.cancel();
                    }
                });


        // Showing Alert Message
        alertDialog.show();

    }

    private void showFailure(String message) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity());
        builder.setMessage(message).setTitle("Error Occurred")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // do nothing
                        //  createdoc();
                    }
                });
        android.app.AlertDialog alert = builder.create();
        alert.show();
    }

    private void showAlert(String message) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity());
        builder.setMessage(message).setTitle("Success")
                .setCancelable(false)
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Post something
Intent intent = new Intent(getActivity(), NewPicture.class);
                        startActivity(intent);


                    }
                });
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                //Dont post anything

            }
        });
        android.app.AlertDialog alert = builder.create();
        alert.show();
    }


    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
    }

    /**
     * Method to verify google play services on the device
     * */
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(getActivity());
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, getActivity(),
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Toast.makeText(getActivity().getApplicationContext(),
                        "This device is not supported.", Toast.LENGTH_LONG)
                        .show();
                getActivity().finish();
            }
            return false;
        }
        return true;
    }


    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.i("TAG", "Connection failed: ConnectionResult.getErrorCode() = "
                + result.getErrorCode());
    }

    @Override
    public void onConnected(Bundle arg0) {

        // Once connected with google api, get the location
        displayLocation();
    }

    @Override
    public void onConnectionSuspended(int arg0) {
        mGoogleApiClient.connect();
    }

    protected void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
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

        //Toast.makeText(getActivity(), "Location changed!",Toast.LENGTH_SHORT).show();
        Log.d("TAG", "Location has changed");

        // Displaying the new location on UI
        displayLocation();
    }

    private void displayLocation() {

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi
                .getLastLocation(mGoogleApiClient);

        if (mLastLocation != null) {
            nLatitude = mLastLocation.getLatitude();
            nLongitude = mLastLocation.getLongitude();
            final GlobalClass globalvariable = (GlobalClass)getActivity().  getApplicationContext();
            globalvariable.setLatitude(nLatitude);
            globalvariable.setLongitude(nLongitude);

            //  txtLatitude.setText((String.valueOf(latitude)));
            //  txtLongitude.setText(String.valueOf(longitude));
new ReverseGeocodingTask(getActivity()).execute();

        } else {
          //  buildAlertMessageNoGps();
            Toast.makeText(getActivity(), "(Couldn't get the location. Make sure location is enabled on the device)", Toast.LENGTH_LONG).show();
        }
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FATEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT); // 10 meters
    }

    private void buildAlertMessageNoGps() {
        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity());
        builder.setMessage("Your location is required do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog,  final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                        getActivity().  finish();
                    }
                });
        final android.app.AlertDialog alert = builder.create();
        alert.show();

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

            try {
                addresses = geocoder.getFromLocation(nLatitude, nLongitude, 1);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (addresses != null && addresses.size() > 0) {
                Address address = addresses.get(0);

                addressText = address.getLocality() + ", " +
                        address.getCountryName();
                currentCountry = address.getAdminArea(); //I used the state here instead of country
                currentCity = address.getLocality();



            }

            return addressText;
        }

        @Override
        protected void onPostExecute (String addressText){
            // Setting address of the touched Position
            //txtCurrentCity.setText(addressText);

            // txtCity.setText(currentCity);
            //  txtCountry.setText(currentCountry);

            //gpsDialog.dismiss();
        }



    }


}
