package com.nejitawo.troublezone.Activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.nejitawo.troublezone.GlobalClass;
import com.nejitawo.troublezone.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.IOException;
import java.util.List;

public class HomezoneActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,  com.google.android.gms.location.LocationListener  {
    private Double nLatitude = 0.10;
    private Double nLongitude = 0.10;

    private String currentAddress = "";

    private TextView txtLocation;

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;

    private Location mLastLocation;

    // Google client to interact with Google API
    private GoogleApiClient mGoogleApiClient;

    private boolean mRequestingLocationUpdates = false;

    private LocationRequest mLocationRequest;

    // Location updates intervals in sec
    private static int UPDATE_INTERVAL = 10000; // 10 sec
    private static int FATEST_INTERVAL = 5000; // 5 sec
    private static int DISPLACEMENT = 10; // 10 meters
    private ProgressDialog ringProgressDialog;

    private String currentCity = "";
    private String currentCountry = "";
    private String currentNation = "";
    private String currentState = "";
private FloatingActionButton fab;
    private EditText txtHomeAddress;
    private Button btnCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homezoner);

        txtHomeAddress = (EditText)findViewById(R.id.tvNumber1) ;

        btnCancel = (Button)findViewById(R.id.btnRequest);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAlert("Please ensure to setup your Home Zone when you are at your home location. ");
            }
        });
final GlobalClass globalVariable = (GlobalClass)getApplicationContext();
        fab = (FloatingActionButton)findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
          if (globalVariable.getHomeID()==1) {
              //Homezone 1
              if (!txtHomeAddress.getText().toString().isEmpty()){
                  if (!txtLocation.getText().toString().isEmpty()){

                      if (globalVariable.getEditOrCreate()==0){
                          ringProgressDialog = ProgressDialog.show(HomezoneActivity.this,"Please wait..","Setting your Home Zone");
                          ringProgressDialog.setCancelable(false);
                          ParseObject homeZone = new ParseObject("HomeZones");
                          homeZone.put("user", ParseUser.getCurrentUser().getUsername());
                          homeZone.put("phone",ParseUser.getCurrentUser().get("phone"));
                          homeZone.put("latitude",nLatitude);
                          homeZone.put("longitude",nLongitude);
                          homeZone.put("address",txtHomeAddress.getText().toString());
                          homeZone.put("geolocation",txtLocation.getText().toString());
                          homeZone.put("country",currentNation);
                          homeZone.put("state",currentState);
                          homeZone.put("city",currentCity);
                          homeZone.saveInBackground(new SaveCallback() {
                              @Override
                              public void done(ParseException e) {
                                  if (e==null){
                                      ringProgressDialog.dismiss();
                                      showSuccess("Home Zone created successfully");
                                  } else{
                                      ringProgressDialog.dismiss();
                                      showFailure("Error occurred. Please try again");
                                  }
                              }
                          });
                      }else{
                          ////User is updating the homezone not creating
                          ringProgressDialog = ProgressDialog.show(HomezoneActivity.this,"Please wait..","Setting your Home Zone");
                          ringProgressDialog.setCancelable(false);

                          ParseQuery<ParseObject> homeZone = new ParseQuery<ParseObject>("HomeZones");
                          homeZone.whereEqualTo("user", ParseUser.getCurrentUser().getUsername());
                          homeZone.findInBackground(new FindCallback<ParseObject>() {
                              @Override
                              public void done(List<ParseObject> objects, ParseException e) {
                                  if (e==null && objects.size()>0){
                                      for (ParseObject ob: objects){
                                          ob.put("user", ParseUser.getCurrentUser().getUsername());
                                          ob.put("phone",ParseUser.getCurrentUser().get("phone"));
                                          ob.put("latitude",nLatitude);
                                          ob.put("longitude",nLongitude);
                                          ob.put("country",currentNation);
                                          ob.put("state",currentState);
                                          ob.put("city",currentCity);
                                          ob.put("address",txtHomeAddress.getText().toString());
                                          ob.put("geolocation",txtLocation.getText().toString());
                                          ob.saveInBackground(new SaveCallback() {
                                              @Override
                                              public void done(ParseException e) {
                                                  if (e==null){
                                                      ringProgressDialog.dismiss();
                                                      showSuccess("Home Zone created successfully");
                                                  } else{
                                                      ringProgressDialog.dismiss();
                                                      showFailure("Error occurred. Please try again");
                                                  }
                                              }
                                          });

                                      }
                                  }
                              }
                          });
                      }


                  } else {
                      //  showAlert("");
                      txtLocation.setError("Geolocation required");
                      return;
                  }
                  //Now save the homezone


              }else {
                  txtHomeAddress.setError("Address is required");
                  return;
              }


          } else if (globalVariable.getHomeID()==2){
              //homezone 2
              if (!txtHomeAddress.getText().toString().isEmpty()){
                  if (!txtLocation.getText().toString().isEmpty()){
//User is creating
                      if (globalVariable.getEditOrCreate()==0){
                          ringProgressDialog = ProgressDialog.show(HomezoneActivity.this,"Please wait..","Setting your Home Zone");
                          ringProgressDialog.setCancelable(false);
                          ParseObject homeZone = new ParseObject("HomeZones2");
                          homeZone.put("user", ParseUser.getCurrentUser().getUsername());
                          homeZone.put("phone",ParseUser.getCurrentUser().get("phone"));
                          homeZone.put("latitude",nLatitude);
                          homeZone.put("longitude",nLongitude);
                          homeZone.put("country",currentNation);
                          homeZone.put("state",currentState);
                          homeZone.put("city",currentCity);
                          homeZone.put("address",txtHomeAddress.getText().toString());
                          homeZone.put("geolocation",txtLocation.getText().toString());
                          homeZone.saveInBackground(new SaveCallback() {
                              @Override
                              public void done(ParseException e) {
                                  if (e==null){
                                      ringProgressDialog.dismiss();
                                      showSuccess("Home Zone created successfully");
                                  } else{
                                      ringProgressDialog.dismiss();
                                      showFailure("Error occurred. Please try again");
                                  }
                              }
                          });
                      } else{
                          ////
                          //edit homezone
                          ringProgressDialog = ProgressDialog.show(HomezoneActivity.this,"Please wait..","Setting your Home Zone");
                          ringProgressDialog.setCancelable(false);
//user is updating
                          ParseQuery<ParseObject> homeZone = new ParseQuery<ParseObject>("HomeZones2");
                          homeZone.whereEqualTo("user", ParseUser.getCurrentUser().getUsername());
                          homeZone.findInBackground(new FindCallback<ParseObject>() {
                              @Override
                              public void done(List<ParseObject> objects, ParseException e) {
                                  if (e==null && objects.size()>0){
                                      for (ParseObject ob: objects){
                                          ob.put("user", ParseUser.getCurrentUser().getUsername());
                                          ob.put("phone",ParseUser.getCurrentUser().get("phone"));
                                          ob.put("latitude",nLatitude);
                                          ob.put("longitude",nLongitude);
                                          ob.put("country",currentNation);
                                          ob.put("state",currentState);
                                          ob.put("city",currentCity);
                                          ob.put("address",txtHomeAddress.getText().toString());
                                          ob.put("geolocation",txtLocation.getText().toString());

                                          ob.saveInBackground(new SaveCallback() {
                                              @Override
                                              public void done(ParseException e) {
                                                  if (e==null){
                                                      ringProgressDialog.dismiss();
                                                      showSuccess("Home Zone created successfully");
                                                  } else{
                                                      ringProgressDialog.dismiss();
                                                      showFailure("Error occurred. Please try again");
                                                  }
                                              }
                                          });

                                      }
                                  }
                              }
                          });

                      }


                  } else {
                      //  showAlert("");
                      txtLocation.setError("Geolocation required");
                      return;
                  }
                  //Now save the homezone


              }else {
                  txtHomeAddress.setError("Address is required");
                  return;
              }

          } else if (globalVariable.getHomeID()==3){
              //homezone3
              if (!txtHomeAddress.getText().toString().isEmpty()){
                  if (!txtLocation.getText().toString().isEmpty()){


                      ringProgressDialog = ProgressDialog.show(HomezoneActivity.this,"Please wait..","Setting your Home Zone");
                      ringProgressDialog.setCancelable(false);

                      //run edit or create

                      if (globalVariable.getEditOrCreate()==0){
                          //create new homezone
                          ParseObject homeZone = new ParseObject("HomeZones3");
                          homeZone.put("user", ParseUser.getCurrentUser().getUsername());
                          homeZone.put("phone",ParseUser.getCurrentUser().get("phone"));
                          homeZone.put("latitude",nLatitude);
                          homeZone.put("longitude",nLongitude);
                          homeZone.put("country",currentNation);
                          homeZone.put("state",currentState);
                          homeZone.put("city",currentCity);
                          homeZone.put("address",txtHomeAddress.getText().toString());
                          homeZone.put("geolocation",txtLocation.getText().toString());
                          homeZone.saveInBackground(new SaveCallback() {
                              @Override
                              public void done(ParseException e) {
                                  if (e==null){
                                      ringProgressDialog.dismiss();
                                      showSuccess("Home Zone created successfully");
                                  } else{
                                      ringProgressDialog.dismiss();
                                      showFailure("Error occurred. Please try again");
                                  }
                              }
                          });


                      }else{
                          //edit homezone
                          ringProgressDialog = ProgressDialog.show(HomezoneActivity.this,"Please wait..","Setting your Home Zone");
                          ringProgressDialog.setCancelable(false);

                          ParseQuery<ParseObject> homeZone = new ParseQuery<ParseObject>("HomeZones3");
                          homeZone.whereEqualTo("user", ParseUser.getCurrentUser().getUsername());
                          homeZone.findInBackground(new FindCallback<ParseObject>() {
                              @Override
                              public void done(List<ParseObject> objects, ParseException e) {
                                  if (e==null && objects.size()>0){
                                      for (ParseObject ob: objects){
                                          ob.put("user", ParseUser.getCurrentUser().getUsername());
                                          ob.put("phone",ParseUser.getCurrentUser().get("phone"));
                                          ob.put("latitude",nLatitude);
                                          ob.put("longitude",nLongitude);
                                          ob.put("country",currentNation);
                                          ob.put("state",currentState);
                                          ob.put("city",currentCity);
                                          ob.put("address",txtHomeAddress.getText().toString());
                                          ob.put("geolocation",txtLocation.getText().toString());
                                          ob.saveInBackground(new SaveCallback() {
                                              @Override
                                              public void done(ParseException e) {
                                                  if (e==null){
                                                      ringProgressDialog.dismiss();
                                                      showSuccess("Home Zone created successfully");
                                                  } else{
                                                      ringProgressDialog.dismiss();
                                                      showFailure("Error occurred. Please try again");
                                                  }
                                              }
                                          });

                                      }
                                  }
                              }
                          });


                      }



                  } else {
                      //  showAlert("");
                      txtLocation.setError("Geolocation required");
                      return;
                  }
                  //Now save the homezone


              }else {
                  txtHomeAddress.setError("Address is required");
                  return;
              }

          }

            }
        });

        txtLocation = (TextView)findViewById(R.id.tvNumber4);

        buildGoogleApiClient();
        createLocationRequest();
        if (mGoogleApiClient != null) {
        mGoogleApiClient.connect();
        }
        if (mGoogleApiClient.isConnected() && mRequestingLocationUpdates) {
        //startLocationUpdates();
        displayLocation();
        }
    }

    private void showSuccess(String message) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(HomezoneActivity.this);
        builder.setMessage(message).setTitle("Success")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // do nothing
                        //  createdoc();
                        Intent intent = new Intent(HomezoneActivity.this,MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
        android.app.AlertDialog alert = builder.create();
        alert.show();
    }

    private void showFailure(String message) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(HomezoneActivity.this);
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
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(HomezoneActivity.this);
        builder.setMessage(message).setTitle("Not at Home?")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // do nothing
                        //  createdoc();
                        Intent intent = new Intent(HomezoneActivity.this,MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
        android.app.AlertDialog alert = builder.create();
        alert.show();
    }

protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(HomezoneActivity.this)
        .addConnectionCallbacks(this)
        .addOnConnectionFailedListener(this)
        .addApi(LocationServices.API).build();
        }
/**
 * Method to verify google play services on the device
 * */
private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil
        .isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
        if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
        GooglePlayServicesUtil.getErrorDialog(resultCode,this,
        PLAY_SERVICES_RESOLUTION_REQUEST).show();
        } else {
        Toast.makeText(this. getApplicationContext(),
        "This device is not supported.", Toast.LENGTH_LONG)
        .show();
         finish();
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
        Log.d("TAG","Location has changed");

        // Displaying the new location on UI
        displayLocation();
    }


private void displayLocation() {

        mLastLocation = LocationServices.FusedLocationApi
        .getLastLocation(mGoogleApiClient);

        if (mLastLocation != null) {
        nLatitude = mLastLocation.getLatitude();
        nLongitude = mLastLocation.getLongitude();
final GlobalClass globalvariable = (GlobalClass)getApplicationContext();
        globalvariable.setLatitude(nLatitude);
        globalvariable.setLongitude(nLongitude);

        //  txtLatitude.setText((String.valueOf(latitude)));
        //  txtLongitude.setText(String.valueOf(longitude));
        ReverseGeocodingTask myTask = new ReverseGeocodingTask( getApplicationContext());
        myTask.execute();

        } else {
        // buildAlertMessageNoGps();
        // Toast.makeText(this, "(Couldn't get the location. Make sure location is enabled on the device)", Toast.LENGTH_LONG).show();
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
final AlertDialog.Builder builder = new AlertDialog.Builder(HomezoneActivity.this);
        builder.setMessage("Your location is required to search for a property, do you want to enable it?")
        .setCancelable(false)
        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
public void onClick(final DialogInterface dialog,  final int id) {
        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
        }
        })
        .setNegativeButton("No", new DialogInterface.OnClickListener() {
public void onClick(final DialogInterface dialog, final int id) {
        dialog.cancel();
         finish();
        }
        });
final AlertDialog alert = builder.create();
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
         currentAddress =  (address.getLocality() + ", " + address.getAdminArea());
            currentCountry = address.getCountryName();
            currentCity = address.getLocality();
            currentNation = address.getCountryName();
            currentState = address.getAdminArea();



        }

        return addressText;
    }

    @Override
    protected void onPostExecute (String addressText){
        // Setting address of the touched Position
        //txtCurrentCity.setText(addressText);
      //  txtCity.setText(currentCity);
//            txtCountry.setText(currentCountry);
        txtLocation.setText(currentAddress);
    }



}

}
