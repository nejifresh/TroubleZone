package com.nejitawo.troublezone.Fragments;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.nejitawo.troublezone.Activities.MainActivity;
import com.nejitawo.troublezone.Adapters.EventsAdapter;
import com.nejitawo.troublezone.Adapters.GoogleCardsSocialAdapter;
import com.nejitawo.troublezone.Capture.NewPicture;
import com.nejitawo.troublezone.Capture.VideoActivity;
import com.nejitawo.troublezone.GPSTracker;
import com.nejitawo.troublezone.GlobalClass;
import com.nejitawo.troublezone.Model.Events;
import com.nejitawo.troublezone.R;
import com.nejitawo.troublezone.Services.LocationService;
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
        GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener,
        BillingProcessor.IBillingHandler {
    LinearLayout lvContent;
    private FloatingActionButton fab;
    private String incident = "";
    private String location = "";
    private Double nLatitude = 0.01;
    private Double nLongitude = 0.01;
    private String currentCity = "";
    private String currentCountry = "";
    private String currentNation = "";
    private String currentState = "";
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;

    private Location mLastLocation;
private GPSTracker locator;

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

    List<ParseObject> userInfo;
    List<Events> myList = new LinkedList<Events>();
    private ListView choiceList;

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
    List<String> subscribtions;
    private Tracker mTracker;
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

locator = new GPSTracker(getActivity().getApplicationContext());


       // choiceList = (ListView) rootView.findViewById(R.id.mychoiceList);
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

        String base64EncodedPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAgwIbEnK/rT1odZlevMMWKbHmdr/uM0l6hVlPtMnN1Rym28WtVPRLiiU+E8uSjaTExXjSB8RlT8PDB5yfZK4q5rV4WIMZ9TQsJPp0x73R5heZh/IV7LS02gZq1ZUNGP7A6oK2fyOQiy6/3kRgSqweAEFYNlgrz+l9kzYGRiUq7n11sVjAj0q0q1LG6TSDGgKleOybWmkL9Rb3PeZVfvKmwyqoeIV+8K0exh9vquN9v9nIs1rvZJ0YlC+SqZxEEijUbBFpsNzZMbHbC+MzM/dr/JS6tLF0vqpZoASqYuRm6OAfmHOSCYhMiWHXM9h8gVnbpITqb1HLgeRz2KZ6nOdvDQIDAQAB";
        bp = new BillingProcessor(getActivity(),base64EncodedPublicKey, this);
        subscribtions=   bp.listOwnedSubscriptions();

        if (subscribtions.size()>0) {
            globalClass.setSubscribed(true);
        } else {
            globalClass.setSubscribed(false);
        }
       new getMemberInfo().execute();
        //Now load data
        Location loc = locator.getLocation();
//        Toast.makeText(getActivity(),String.valueOf(loc.getLatitude()+" , " + String.valueOf(loc.getLongitude())),Toast.LENGTH_LONG).show();


        return rootView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Obtain the shared Tracker instance.
        GlobalClass application = (GlobalClass) getActivity(). getApplication();
        mTracker = application.getDefaultTracker();
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
final GlobalClass globalVariable = (GlobalClass)getActivity().getApplicationContext();

            ParseQuery <ParseObject> eventQuery = new ParseQuery<ParseObject>("Alarms");
            eventQuery.whereEqualTo("show", "YES");
            eventQuery.whereEqualTo("country",globalVariable.getTimeLineLocation());


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
            mAdapter = new EventsAdapter(getActivity(), myList, getActivity());

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

                showAlarm();
            }
        });

        final RelativeLayout rlFire = (RelativeLayout) promptsView
                .findViewById(R.id.rlFire);
        rlFire.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                incident = "Fire Incident";
                showAlarm();
            }
        });

        final RelativeLayout rlHomicide = (RelativeLayout) promptsView
                .findViewById(R.id.rlKill);
        rlHomicide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                incident = "Murder Incident";
                showAlarm();
            }
        });

        final RelativeLayout rlRiot = (RelativeLayout) promptsView
                .findViewById(R.id.rlRiot);
        rlRiot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                incident = "Civil Unrest";
                showAlarm();
            }
        });

        final RelativeLayout rlAccident = (RelativeLayout) promptsView
                .findViewById(R.id.rlRoad);
        rlAccident.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                incident = "Road Accident";
                showAlarm();
            }
        });
        final RelativeLayout rlKidnap = (RelativeLayout) promptsView
                .findViewById(R.id.rlKidnap);
        rlKidnap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                incident = "Kidnapping";
                showAlarm();
            }
        });
        final RelativeLayout rlMedical = (RelativeLayout) promptsView
                .findViewById(R.id.rlMedical);
        rlMedical.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                incident = "Medical Emergency";
                showAlarm();
            }
        });
        final RelativeLayout rlDomestic = (RelativeLayout) promptsView
                .findViewById(R.id.rlDomestic);
        rlDomestic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                incident = "Domestic Violence";
                showAlarm();
            }
        });
        final RelativeLayout rlSexual = (RelativeLayout) promptsView
                .findViewById(R.id.rlSexual);
        rlSexual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                incident = "Sexual Harrassment";
                showAlarm();
            }
        });

        final RelativeLayout rlOthers = (RelativeLayout) promptsView
                .findViewById(R.id.rlOthers);
        rlOthers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             //   incident = "Sexual Harrassment";
             //   showAlarm();
                showOther();
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

    void showOther() {
        //Inflate layout
        final GlobalClass globalVariable = (GlobalClass)getActivity().getApplicationContext();
        LayoutInflater li = LayoutInflater.from(getActivity());
        View promptsView = li.inflate(R.layout.otherincident, null);
        final android.support.v7.app.AlertDialog.Builder alertDialogBuilder = new android.support.v7.app.AlertDialog.Builder(getActivity());
        alertDialogBuilder.setView(promptsView);
        final EditText txtIncident = (EditText) promptsView
                .findViewById(R.id.txtinctitle);


        alertDialogBuilder.setCancelable(false).setTitle("Other Incident")
                .setPositiveButton("DONE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //Now send appointment request here


                        if (txtIncident.getText().toString().isEmpty()){
Toast.makeText(getActivity(), "Enter an incident description", Toast.LENGTH_LONG).show();
                            return;
                        } else{
                        incident = txtIncident.getText().toString();
                            showAlarm();
                        }


                    }
                })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
        alertDialogBuilder.create();
        alertDialogBuilder.show();


    }

    void showSuccess() {
        //
        //Inflate layout
        final GlobalClass globalVariable = (GlobalClass)getActivity().getApplicationContext();
        LayoutInflater li = LayoutInflater.from(getActivity());
        View promptsView = li.inflate(R.layout.showsuccess, null);
        final android.support.v7.app.AlertDialog.Builder alertDialogBuilder = new android.support.v7.app.AlertDialog.Builder(getActivity());
        alertDialogBuilder.setView(promptsView);
        final Button btnUploadVideo = (Button) promptsView
                .findViewById(R.id.btnRecVideo);
        btnUploadVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), VideoActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });

        final Button btnTakePic = (Button) promptsView
                .findViewById(R.id.btnPics);
        btnTakePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), NewPicture.class);
                startActivity(intent);
                getActivity().finish();
            }
        });

        final Button btnDone = (Button) promptsView
                .findViewById(R.id.btnCancel);
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });

        alertDialogBuilder.setCancelable(false).setTitle("Alarm Triggered Sucessfully");

        alertDialogBuilder.create();
        alertDialogBuilder.show();


    }

    void showAlarm() {
        //Inflate layout
        final GlobalClass globalVariable = (GlobalClass)getActivity().getApplicationContext();
        LayoutInflater li = LayoutInflater.from(getActivity());
        View promptsView = li.inflate(R.layout.alarmdialog, null);
        final android.support.v7.app.AlertDialog.Builder alertDialogBuilder = new android.support.v7.app.AlertDialog.Builder(getActivity());
        alertDialogBuilder.setView(promptsView);
        final Button btnPrimary = (Button) promptsView
                .findViewById(R.id.btnPrimary);

        if (address1.isEmpty()){
            btnPrimary.setVisibility(View.GONE);
        } else{
            btnPrimary.setVisibility(View.VISIBLE);
            btnPrimary.setText(address1);
        }

        //click on primary homezone
        btnPrimary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                location = "HomeZone";
                final ProgressDialog progressDialog = ProgressDialog.show(getActivity(), "Raising Alarm ", "Raising Alarm around your Home Zone..");
                progressDialog.setCancelable(true);

                if (!address1.isEmpty()){
                    Calendar c = Calendar.getInstance();
                    Date today = c.getTime();
                    //Now create the alarm
                    final  ParseObject newAlarm = new ParseObject("Alarms");
                    newAlarm.put("user", ParseUser.getCurrentUser().getUsername());
                    newAlarm.put("phone", ParseUser.getCurrentUser().get("phone"));
                    newAlarm.put("userimage", ParseUser.getCurrentUser().get("Image"));
                    newAlarm.put("type", "HomeZone");
                    newAlarm.put("userAddress", address1);
                    newAlarm.put("incident", incident);
                    newAlarm.put("latitude", lat1);
                    newAlarm.put("longitude", lon1);
                    newAlarm.put("location",city1 + ", " + state1);
                    newAlarm.put("datetimeof", today);
                    newAlarm.put("country",country1);
                    newAlarm.put("state",state1);
                    newAlarm.put("status", "NEW");
                    newAlarm.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                //alertDialogBuilder..dismiss();
                                progressDialog.dismiss();
                                final GlobalClass globalVariable = (GlobalClass)getActivity().getApplicationContext();
                                globalVariable.setIncidentID(newAlarm.getObjectId());
                                mTracker.send(new HitBuilders.EventBuilder()
                                        .setCategory("Action")
                                        .setAction("Homezone Alarm")
                                        .build());
showSuccess();
                               // showAlert("Alarm raised successfully. Do you want to post pictures or videos relating to this incident?");
                            }
                        }
                    });
                } else{
                    //show error no homezone
                    showFailure("No Primary Home Zone registered");
                }




            }
        });



        final Button btnSecondary = (Button) promptsView
                .findViewById(R.id.btnSecondary);

        if (address2.isEmpty()){
            btnSecondary.setVisibility(View.GONE);
        } else{
            btnSecondary.setVisibility(View.VISIBLE);
            btnSecondary.setText(address2);
        }
        btnSecondary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                location = "HomeZone2";
                final ProgressDialog progressDialog = ProgressDialog.show(getActivity(), "Raising Alarm ", "Raising Alarm around your 2nd Home Zone..");
                progressDialog.setCancelable(true);

                if (!address2.isEmpty()){
                    Calendar c = Calendar.getInstance();
                    Date today = c.getTime();
                    //Now create the alarm
                    final  ParseObject newAlarm = new ParseObject("Alarms");
                    newAlarm.put("user", ParseUser.getCurrentUser().getUsername());
                    newAlarm.put("phone", ParseUser.getCurrentUser().get("phone"));
                    newAlarm.put("userimage", ParseUser.getCurrentUser().get("Image"));
                    newAlarm.put("type", "HomeZone");
                    newAlarm.put("userAddress", address2);
                    newAlarm.put("incident", incident);
                    newAlarm.put("latitude", lat2);
                    newAlarm.put("longitude", lon2);
                    newAlarm.put("location",city2 + ", " + state2);
                    newAlarm.put("datetimeof", today);
                    newAlarm.put("country",country2);
                    newAlarm.put("state",state2);
                    newAlarm.put("status", "NEW");
                    newAlarm.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                //alertDialogBuilder..dismiss();
                                progressDialog.dismiss();
                                final GlobalClass globalVariable = (GlobalClass)getActivity().getApplicationContext();
                                globalVariable.setIncidentID(newAlarm.getObjectId());
                                mTracker.send(new HitBuilders.EventBuilder()
                                        .setCategory("Action")
                                        .setAction("Homezone2 alarm")
                                        .build());
showSuccess();
                              //  showAlert("Alarm raised successfully. Do you want to post pictures or videos relating to this incident?");
                            }
                        }
                    });
                } else{
                    //show error no homezone
                    showFailure("No Secondary Home Zone registered");
                }

            }
        });


        final Button btnTetiary = (Button) promptsView
                .findViewById(R.id.btnTetiary);
        if (address3.isEmpty()){
            btnTetiary.setVisibility(View.GONE);
        } else{
            btnTetiary.setVisibility(View.VISIBLE);
            btnTetiary.setText(address3);
        }

        btnTetiary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                location = "HomeZone3";
                final ProgressDialog progressDialog = ProgressDialog.show(getActivity(), "Raising Alarm ", "Raising Alarm around your 3rd Home Zone..");
                progressDialog.setCancelable(true);

                if (!address3.isEmpty()){
                    Calendar c = Calendar.getInstance();
                    Date today = c.getTime();
                    //Now create the alarm
                    final  ParseObject newAlarm = new ParseObject("Alarms");
                    newAlarm.put("user", ParseUser.getCurrentUser().getUsername());
                    newAlarm.put("phone", ParseUser.getCurrentUser().get("phone"));
                    newAlarm.put("userimage", ParseUser.getCurrentUser().get("Image"));
                    newAlarm.put("type", "HomeZone");
                    newAlarm.put("userAddress", address3);
                    newAlarm.put("incident", incident);
                    newAlarm.put("latitude", lat3);
                    newAlarm.put("longitude", lon3);
                    newAlarm.put("location",city3 + ", " + state3);
                    newAlarm.put("datetimeof", today);
                    newAlarm.put("country",country3);
                    newAlarm.put("state",state3);
                    newAlarm.put("status", "NEW");
                    newAlarm.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                //alertDialogBuilder..dismiss();
                                progressDialog.dismiss();
                                final GlobalClass globalVariable = (GlobalClass)getActivity().getApplicationContext();
                                globalVariable.setIncidentID(newAlarm.getObjectId());
                                mTracker.send(new HitBuilders.EventBuilder()
                                        .setCategory("Action")
                                        .setAction("Homezone3 Alarm")
                                        .build());
showSuccess();
                               // showAlert("Alarm raised successfully. Do you want to post pictures or videos relating to this incident?");
                            }
                        }
                    });
                } else{
                    //show error no homezone
                    showFailure("No Tetiary Home Zone registered");
                }
            }
        });

        final Button btnCurrent = (Button) promptsView
                .findViewById(R.id.btnCurrent);
btnCurrent.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        final ProgressDialog progressDialog = ProgressDialog.show(getActivity(), "Raising Alarm ", "Incident alert in progress..");
        progressDialog.setCancelable(true);

        if (nLatitude==0.01 || nLatitude==null||globalVariable.getState() == null){
            progressDialog.dismiss();
           // buildAlertMessageNoGps();
            try{

            mLastLocation = locator.getLocation();
            nLatitude = mLastLocation.getLatitude();
            nLongitude = mLastLocation.getLongitude();
            final GlobalClass globalvariable = (GlobalClass)getActivity().  getApplicationContext();
            globalvariable.setLatitude(nLatitude);
            globalvariable.setLongitude(nLongitude);
            new ReverseGeocodingTask(getActivity()).execute();

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
            newAlarm.put("country",currentNation);
            newAlarm.put("state",globalVariable.getState());
            newAlarm.put("datetimeof", today);
            newAlarm.put("location",currentCity + ", "+ currentCountry);
            newAlarm.put("status", "NEW");
            newAlarm.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        // dialog.dismiss();
                        progressDialog.dismiss();
                        final GlobalClass globalVariable = (GlobalClass)getActivity().getApplicationContext();
                        globalVariable.setIncidentID(newAlarm.getObjectId());
                        mTracker.send(new HitBuilders.EventBuilder()
                                .setCategory("Action")
                                .setAction("Current Location Alarm")
                                .build());
                        showSuccess();
                        // showAlert("Alarm raised successfully. Do you want to post pictures or videos relating to this incident?");
                    }
                }
            });

            }catch (Exception e){
                buildAlertMessageNoGps();
                e.printStackTrace();
            }


            /////////////////////////////try this


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
            newAlarm.put("country",currentNation);
            newAlarm.put("state",globalVariable.getState());
            newAlarm.put("datetimeof", today);
            newAlarm.put("location",currentCity + ", "+ currentCountry);
            newAlarm.put("status", "NEW");
            newAlarm.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                       // dialog.dismiss();
                        progressDialog.dismiss();
                        final GlobalClass globalVariable = (GlobalClass)getActivity().getApplicationContext();
                        globalVariable.setIncidentID(newAlarm.getObjectId());
                        mTracker.send(new HitBuilders.EventBuilder()
                                .setCategory("Action")
                                .setAction("Current Location Alarm")
                                .build());
showSuccess();
                       // showAlert("Alarm raised successfully. Do you want to post pictures or videos relating to this incident?");
                    }
                }
            });
        }
    }
});


        alertDialogBuilder.setCancelable(false).setTitle("Where is this incident happening")
                .setPositiveButton("DONE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //Now send appointment request here
dialogInterface.dismiss();



                    }
                })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
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
                .setNeutralButton("POST 1 MINUTE VIDEO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setPositiveButton("POST CONTENT", new DialogInterface.OnClickListener() {
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

    /*    if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
            try{
                mLastLocation = locator.getLocation();
                nLatitude = mLastLocation.getLatitude();
                nLongitude = mLastLocation.getLongitude();
                final GlobalClass globalvariable = (GlobalClass)getActivity().  getApplicationContext();
                globalvariable.setLatitude(nLatitude);
                globalvariable.setLongitude(nLongitude);
                new ReverseGeocodingTask(getActivity()).execute();
              //  Toast.makeText(getActivity(), "(Couldn't get regular google location. Make sure location is enabled on the device)", Toast.LENGTH_LONG).show();

            } catch (Exception e){
                buildAlertMessageNoGps();
                e.printStackTrace();
            }


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
                currentNation = address.getCountryName();
                final GlobalClass globalVariable = (GlobalClass)getActivity().getApplicationContext();
                globalVariable.setCity(currentCity);
                globalVariable.setCountry(currentNation);
                globalVariable.setState(currentCountry);


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



    private class getMemberInfo extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
     //  count = 0;
            progressView.setVisibility(View.VISIBLE);
            progressView.startAnimation();
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

            final GlobalClass globalVariable = (GlobalClass)getActivity(). getApplicationContext();
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

            try{
                Intent locIntent = new Intent(getActivity(), LocationService.class);
              getActivity().  startService(locIntent);
            }catch (Exception e){
               e.printStackTrace();
            }

            new loadData().execute();

         /*   progressView.stopAnimation();
            progressView.setVisibility(View.INVISIBLE);*/



        }
    }

    @Override
    public void onBillingInitialized() {
        /*
         * Called when BillingProcessor was initialized and it's ready to purchase
         */
    }

    @Override
    public void onProductPurchased(String productId, TransactionDetails details) {
        /*
         * Called when requested PRODUCT ID was successfully purchased
         *
         *
         */
        GlobalClass globalVariable = (GlobalClass)getActivity().getApplicationContext();
        globalVariable.setSubscribed(true);
       /* Tracker t = ((GlobalClass)getActivity().getApplication()).getDefaultTracker();
        t.send(new HitBuilders.EventBuilder()
                .setCategory(getString(R.string.categoryId))
                .setAction(getString(R.string.actionId))
                .setLabel(getString(R.string.labelIdsnack))
                .build()


        );
        seekBar.setEnabled(true);
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(
                "Profiles");
        query.whereEqualTo("User", ParseUser.getCurrentUser().getUsername());
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (e==null){
                    for (ParseObject ob: list){
                        ob.put("Status","Trending");
                        ob.saveInBackground();
                        //Now alert the user of the purchase
                        Toast.makeText(getActivity(),"Purchase Successful", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });*/

    }

    @Override
    public void onBillingError(int errorCode, Throwable error) {
        /*
         * Called when some error occurred. See Constants class for more details
         */
    }

    @Override
    public void onPurchaseHistoryRestored() {
        /*
         * Called when purchase history was restored and the list of all owned PRODUCT ID's
         * was loaded from Google Play
         */
    }

}
