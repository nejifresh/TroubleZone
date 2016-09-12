package com.nejitawo.troublezone.Activities;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.google.android.gms.maps.model.LatLng;
import com.nejitawo.troublezone.Adapters.EventsAdapter;
import com.nejitawo.troublezone.GlobalClass;
import com.nejitawo.troublezone.Model.Events;
import com.nejitawo.troublezone.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class Settings extends AppCompatActivity implements  BillingProcessor.IBillingHandler {
    private Button btnCountries;
    private Button btnNumbers;
    private Button btnHomeZones;
    private TextView txtMyCountry;
    private Switch alertSwitch;
    private Switch smsSwitch;
    private SeekBar seekBar;
    private TextView textView;
    private TextView txtNumZones;
    private List<String> homeZones;
    private String address1 = "";
    private Double lat1;
    private Double lon1;

    private String address2 = "";
    private Double lat2;
    private Double lon2;

    private String address3 = "";
    private Double lat3;
    private Double lon3;
    CircularProgressView progressView;
    List<ParseObject> ob;

    List<ParseObject> ob2;

    List<ParseObject> ob3;

    List<ParseObject> userInfo;
    private int count = 0;

    LinearLayout linearLayout;

    private String userNumber1 = "";
    private String userNumber2 = "";
    private String userNumber3 = "";
    private Boolean enableSOS = false;
    private Boolean enableAlerts = true;
    private String currentTimeline = "";
    private int alertradius = 1;
    BillingProcessor bp;
    List<String> subscribtions;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.troublesettings);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        final GlobalClass globalVariable = (GlobalClass)getApplicationContext();
        linearLayout = (LinearLayout)findViewById(R.id.linearLayout);

        String base64EncodedPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAgwIbEnK/rT1odZlevMMWKbHmdr/uM0l6hVlPtMnN1Rym28WtVPRLiiU+E8uSjaTExXjSB8RlT8PDB5yfZK4q5rV4WIMZ9TQsJPp0x73R5heZh/IV7LS02gZq1ZUNGP7A6oK2fyOQiy6/3kRgSqweAEFYNlgrz+l9kzYGRiUq7n11sVjAj0q0q1LG6TSDGgKleOybWmkL9Rb3PeZVfvKmwyqoeIV+8K0exh9vquN9v9nIs1rvZJ0YlC+SqZxEEijUbBFpsNzZMbHbC+MzM/dr/JS6tLF0vqpZoASqYuRm6OAfmHOSCYhMiWHXM9h8gVnbpITqb1HLgeRz2KZ6nOdvDQIDAQAB";
        bp = new BillingProcessor(this,base64EncodedPublicKey, this);
        subscribtions=   bp.listOwnedSubscriptions();

        if (subscribtions.size()>0) {
            globalVariable.setSubscribed(true);
        } else {
            globalVariable.setSubscribed(false);
        }


            btnCountries = (Button) findViewById(R.id.btnChangeCountry);
        btnNumbers = (Button) findViewById(R.id.btnAddNo);
        btnHomeZones = (Button) findViewById(R.id.btnAddZone);
        progressView = (CircularProgressView) findViewById(R.id.progress_view);
        txtNumZones = (TextView)findViewById(R.id.spBathrooms) ;



        txtMyCountry = (TextView) findViewById(R.id.txtmyCountry);
         seekBar = (SeekBar) findViewById(R.id.seekBar1);
        textView = (TextView) findViewById(R.id.txtprice);
        alertSwitch = (Switch) findViewById(R.id.spBeds);
        smsSwitch = (Switch) findViewById(R.id.spToilets);
        ob = null;
        ob2= null;
        ob3 = null;
        userInfo = null;
        homeZones = new ArrayList<String>();
        btnHomeZones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showHomePrompt();
            }
        });

        btnNumbers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPrompt();
            }
        });


        smsSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(subscribtions.size()>0){

                    //  Toast.makeText(Settings.this, "Changed", Toast.LENGTH_SHORT).show();
                    ParseQuery<ParseObject> userQuery = new ParseQuery<ParseObject>("UserSettings");
                    userQuery.whereEqualTo("user", ParseUser.getCurrentUser().getUsername());
                    userQuery.findInBackground(new FindCallback<ParseObject>() {
                        @Override
                        public void done(List<ParseObject> objects, ParseException e) {
                            if (e==null && objects.size()>0){
                                for (ParseObject nums: objects){
                                    nums.put("enablesos",smsSwitch.isChecked());

                                    nums.saveInBackground();
                                }
                                // showSuccess("Alert status updated successfully");
                                Toast.makeText(Settings.this, "Send SOS messages is set to " +String.valueOf(smsSwitch.isChecked())
                                        , Toast.LENGTH_SHORT).show();
                                globalVariable.setEnableSMS(smsSwitch.isChecked());


                            } else{
                                showError("An error occured. Please try again");
                                e.printStackTrace();
                            }
                        }
                    });

                } else{
                    smsSwitch.setChecked(false);

                    Snackbar snackbar = Snackbar
                            .make(linearLayout,"Subscribe to enable SOS Messages.", Snackbar.LENGTH_LONG)
                            .setAction("SUBSCRIBE", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    //    Snackbar snackbar1 = Snackbar.make(linearLayout, "Message is restored!", Snackbar.LENGTH_SHORT);
                                    //  snackbar1.show();
                                    bp.subscribe(Settings.this,"a100");
                                }
                            });

                    snackbar.show();
                }








            }
        });
        alertSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                ParseQuery<ParseObject> userQuery = new ParseQuery<ParseObject>("UserSettings");
                userQuery.whereEqualTo("user", ParseUser.getCurrentUser().getUsername());
                userQuery.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> objects, ParseException e) {
                        if (e==null && objects.size()>0){
                            for (ParseObject nums: objects){
                                nums.put("enablealerts",alertSwitch.isChecked());

                                nums.saveInBackground();
                            }
                           // showSuccess("Alert status updated successfully");
                            Toast.makeText(Settings.this, "Alert notifications is set to " +String.valueOf(alertSwitch.isChecked())
                                  , Toast.LENGTH_SHORT).show();
globalVariable.setEnableAlerts(alertSwitch.isChecked());

                        } else{
                            showError("An error occured. Please try again");
                            e.printStackTrace();
                        }
                    }
                });
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progress = 0;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progresValue, boolean fromUser) {
                progress = progresValue;
                // Toast.makeText(getActivity(), "Changing seekbar's progress", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Toast.makeText(getActivity(), "Started tracking seekbar", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onStopTrackingTouch(final SeekBar seekBar) {
                if (subscribtions.size() <= 0) {
                    Snackbar snackbar = Snackbar
                            .make(linearLayout,"You need an active subscription to change your alert radius.", Snackbar.LENGTH_INDEFINITE)
                            .setAction("SUBSCRIBE", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    //    Snackbar snackbar1 = Snackbar.make(linearLayout, "Message is restored!", Snackbar.LENGTH_SHORT);
                                    //  snackbar1.show();
                                    bp.subscribe(Settings.this,"a100");
                                }
                            });

                    snackbar.show();
                    //Now disable trending status if exists
                    seekBar.setProgress(1);
                    //seekBar.setEnabled(false);

                } else {

                    ParseQuery<ParseObject> userQuery = new ParseQuery<ParseObject>("UserSettings");
                    userQuery.whereEqualTo("user", ParseUser.getCurrentUser().getUsername());
                    userQuery.findInBackground(new FindCallback<ParseObject>() {
                        @Override
                        public void done(List<ParseObject> objects, ParseException e) {
                            if (e == null && objects.size() > 0) {
                                for (ParseObject nums : objects) {
                                    nums.put("alertradius", seekBar.getProgress());

                                    nums.saveInBackground();
                                }
                                // showSuccess("Alert status updated successfully");
                                textView.setText("Alert radius:  " + String.valueOf(seekBar.getProgress()) + " Km ");
                                globalVariable.setAlertRadius(seekBar.getProgress());

                            } else {
                                showError("An error occured. Please try again");
                                e.printStackTrace();
                            }
                        }
                    });

              /*  if (subscribtions.size()>0){
                    // This guy has bought a subscription so set the appropriate distance
                    globalVariable.setRadius(progress);
                    globalVariable.setIsSubscribed(true);


                } else{
                    globalVariable.setIsSubscribed(false);

                    Snackbar snackbar = Snackbar
                            .make(linearLayout,"You need an active subscription to change search location.", Snackbar.LENGTH_INDEFINITE)
                            .setAction("SUBSCRIBE", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    //    Snackbar snackbar1 = Snackbar.make(linearLayout, "Message is restored!", Snackbar.LENGTH_SHORT);
                                    //  snackbar1.show();
                                    bp.subscribe(getActivity(),"w100");
                                }
                            });

                    snackbar.show();
                    //Now disable trending status if exists
                    seekBar.setProgress(100);
                    seekBar.setEnabled(false);
                }
                */
                }
            }

        });


//Now get the home zones


        Locale[] locales = Locale.getAvailableLocales();
        final ArrayList<String> countries = new ArrayList<>();

        String country;
        for (Locale loc : locales) {
            country = loc.getDisplayCountry();
            if (country.length() > 0 && !countries.contains(country)) {
                countries.add(country);
            }

        }

        Collections.sort(countries, String.CASE_INSENSITIVE_ORDER);
        btnCountries.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (subscribtions.size()>0){
                    globalVariable.setSubscribed(true);

                    //user has a subscription
                    final AlertDialog.Builder alertDialog = new AlertDialog.Builder(Settings.this);
                    final AlertDialog alert = alertDialog.create();
                    LayoutInflater inflater = getLayoutInflater();
                    final View convertView = (View) inflater.inflate(R.layout.country, null);
                    alert.setView(convertView);
                    alert.setTitle("Select a country");
                    final ListView lv = (ListView) convertView.findViewById(R.id.listView1);
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(Settings.this, android.R.layout.simple_list_item_1, countries);
                    lv.setAdapter(adapter);
                    lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            final String selectedFromList = (String) (lv.getItemAtPosition(i));
                            txtMyCountry.setText(selectedFromList);
                            //now save this selection
                            ParseQuery<ParseObject> userQuery = new ParseQuery<ParseObject>("UserSettings");
                            userQuery.whereEqualTo("user", ParseUser.getCurrentUser().getUsername());
                            userQuery.findInBackground(new FindCallback<ParseObject>() {
                                @Override
                                public void done(List<ParseObject> objects, ParseException e) {
                                    if (e==null && objects.size()>0){
                                        for (ParseObject nums: objects){
                                            nums.put("currenttimeline",selectedFromList);

                                            nums.saveInBackground();
                                        }
                                        showSuccess("Timeline location changed successfully");

                                        globalVariable.setTimeLineLocation(selectedFromList);

                                    } else{
                                        showError("An error occured. Please try again");
                                        e.printStackTrace();
                                    }
                                }
                            });


                            alert.dismiss();

                        }
                    });
                    alert.show();


                } else{

                    Snackbar snackbar = Snackbar
                            .make(linearLayout,"Subscribe to enable Multiple Timelines.", Snackbar.LENGTH_LONG)
                            .setAction("SUBSCRIBE", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    //    Snackbar snackbar1 = Snackbar.make(linearLayout, "Message is restored!", Snackbar.LENGTH_SHORT);
                                    //  snackbar1.show();
                                    bp.subscribe(Settings.this,"a100");
                                }
                            });

                    snackbar.show();

                }



             /*   } else{
                    globalVariable.setIsSubscribed(false);

                    Snackbar snackbar = Snackbar
                            .make(linearLayout,"You need an active subscription to change search location.", Snackbar.LENGTH_INDEFINITE)
                            .setAction("SUBSCRIBE", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    //    Snackbar snackbar1 = Snackbar.make(linearLayout, "Message is restored!", Snackbar.LENGTH_SHORT);
                                    //  snackbar1.show();
                                    bp.subscribe(getActivity(),"w100");
                                }
                            });

                    snackbar.show();

                }*/
//here

            }
        });


        new getMemberInfo().execute();
    }


    void showPrompt() {
        //Inflate layout
        LayoutInflater li = LayoutInflater.from(Settings.this);
        View promptsView = li.inflate(R.layout.smsnumbers, null);
        android.support.v7.app.AlertDialog.Builder alertDialogBuilder = new android.support.v7.app.AlertDialog.Builder(Settings.this);
        alertDialogBuilder.setView(promptsView);
        final EditText firstNumber = (EditText) promptsView
                .findViewById(R.id.txtNumber1);
        firstNumber.setText(userNumber1);

        final EditText secondNumber = (EditText) promptsView.findViewById(R.id.txtNumber2);
        secondNumber.setText(userNumber2);
        final EditText thirdNumber = (EditText) promptsView
                .findViewById(R.id.txtNumber3);
        thirdNumber.setText(userNumber3);

        alertDialogBuilder.setCancelable(false).setTitle("Setup SOS Mobile Nos")
                .setPositiveButton("SAVE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //Now send appointment request here

                        if (firstNumber.getText().toString().isEmpty() &&
                                secondNumber.getText().toString().isEmpty() &&
                                thirdNumber.getText().toString().isEmpty()) {
                            showError("At least 1 mobile number is required");
                            return;
                        } else{
                            ParseQuery<ParseObject> userQuery = new ParseQuery<ParseObject>("UserSettings");
                            userQuery.whereEqualTo("user", ParseUser.getCurrentUser().getUsername());
                            userQuery.findInBackground(new FindCallback<ParseObject>() {
                                @Override
                                public void done(List<ParseObject> objects, ParseException e) {
                                    if (e==null && objects.size()>0){
                                        for (ParseObject nums: objects){
                                            nums.put("phone1",firstNumber.getText().toString());
                                            nums.put("phone2",secondNumber.getText().toString());
                                            nums.put("phone3",thirdNumber.getText().toString());
                                            nums.saveInBackground();
                                        }
                                        showSent();
                                    } else{
                                        showError("An error occured. Please try again");
                                        e.printStackTrace();
                                    }
                                }
                            });

                        }


                      /*  ParseObject newRequest = new ParseObject("VisitRequests");
                        newRequest.put("DateRequested",dateInput.getText().toString());
                        newRequest.put("Message",userInput.getText().toString());
                        newRequest.put("Sender", "senderemailhere");
                        newRequest.put("Phone","phone number");
                        newRequest.saveInBackground();*/


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

    void showHomePrompt() {
        //Inflate layout
        LayoutInflater li = LayoutInflater.from(Settings.this);
        final View promptsView = li.inflate(R.layout.homezoneadder, null);
        final android.support.v7.app.AlertDialog.Builder alertDialogBuilder = new android.support.v7.app.AlertDialog.Builder(Settings.this);
        alertDialogBuilder.setView(promptsView);
        final GlobalClass globalVariable = (GlobalClass) getApplicationContext();
        final Button btnPrimary = (Button) promptsView
                .findViewById(R.id.btnChangeCountry);
        final EditText txtHome1 = (EditText) promptsView.findViewById(R.id.txtmyCountry);
        txtHome1.setText(address1);

        final Button btnSecondary = (Button) promptsView
                .findViewById(R.id.btnChangeCountry2);
        final EditText txtHome2 = (EditText) promptsView.findViewById(R.id.txtmyCountry2);
        txtHome2.setText(address2);


        final Button btnTetiary = (Button) promptsView
                .findViewById(R.id.btnChangeCountry3);
        final EditText txtHome3 = (EditText) promptsView.findViewById(R.id.txtmyCountry3);
        txtHome3.setText(address3);


        btnPrimary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (txtHome1.getText().toString().isEmpty()){
                    globalVariable.setEditOrCreate(0); //O means create, while 1 means edit
                } else {
                    globalVariable.setEditOrCreate(1); //O means create, while 1 means edit
                }
                globalVariable.setHomeID(1); //Primary Home Zone
                Intent intent = new Intent(Settings.this, HomezoneActivity.class);
                startActivity(intent);
            }
        });

        btnSecondary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (subscribtions.size()>0){
                    if (txtHome2.getText().toString().isEmpty()){
                        globalVariable.setEditOrCreate(0); //O means create, while 1 means edit
                    } else {
                        globalVariable.setEditOrCreate(1); //O means create, while 1 means edit
                    }
                    globalVariable.setHomeID(2); //Secondary Home Zone
                    Intent intent = new Intent(Settings.this, HomezoneActivity.class);
                    startActivity(intent);

                } else{
                    //Show snackbar
                    showSubscribe("Subscribe to add an extra Home Zone");
                }

            }
        });

        btnTetiary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (subscribtions.size()>0){
                    if (txtHome3.getText().toString().isEmpty()){
                        globalVariable.setEditOrCreate(0); //O means create, while 1 means edit
                    } else {
                        globalVariable.setEditOrCreate(1); //O means create, while 1 means edit
                    }
                    globalVariable.setHomeID(3); //Tetiary Home Zone
                    Intent intent = new Intent(Settings.this, HomezoneActivity.class);
                    startActivity(intent);

                }else{
                    //show snackbar
                   // Toast.makeText(Settings.this,"Subscribe to add an extra Home Zone",Toast.LENGTH_LONG).show();
                    showSubscribe("Subscribe to add an extra Home Zone");

                }


            }
        });
        alertDialogBuilder.setCancelable(false).setTitle("My Home Zones")

                .setNegativeButton("DONE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
        alertDialogBuilder.create();
        alertDialogBuilder.show();


    }


    private void showSent() {
        android.support.v7.app.AlertDialog.Builder alertDialog = new android.support.v7.app.AlertDialog.Builder(Settings.this);
        alertDialog.setTitle("Success").setMessage("Your SOS mobile numbers have been saved successfully.")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
        android.support.v7.app.AlertDialog alert = alertDialog.create();
        alert.show();
    }

    private void showError(String message) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setMessage(message).setTitle(" Error")
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

    @Override
    public void onProductPurchased(String productId, TransactionDetails details) {

    }

    @Override
    public void onPurchaseHistoryRestored() {

    }

    @Override
    public void onBillingError(int errorCode, Throwable error) {

    }

    @Override
    public void onBillingInitialized() {

    }

    private class getMemberInfo extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
count = 0;
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
               ob= homeZone.find();
            } catch (Exception e) {
                e.printStackTrace();
            }

//Homezone 2
            ParseQuery<ParseObject> homeZone2 = new ParseQuery<ParseObject>("HomeZones2");
            homeZone2.whereEqualTo("user", ParseUser.getCurrentUser().getUsername());
            try {
                ob2= homeZone2.find();
            } catch (Exception e) {
                e.printStackTrace();
            }
            //homezone3
            ParseQuery<ParseObject> homeZone3 = new ParseQuery<ParseObject>("HomeZones3");
            homeZone3.whereEqualTo("user", ParseUser.getCurrentUser().getUsername());
            try {
                ob3= homeZone3.find();
            } catch (Exception e) {
                e.printStackTrace();
            }



            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
if (ob==null){

} else{
    for (ParseObject obj : ob) {
        address1 = (obj.getString("address"));
        lat1 = obj.getDouble("latitude");
        lon1 = obj.getDouble("longitude");
        count++;
    }

}

if (ob2==null){

} else{
    for (ParseObject obj : ob2) {
        address2 = (obj.getString("address"));
        lat2 = obj.getDouble("latitude");
        lon2 = obj.getDouble("longitude");
        count++;
    }
}

if (ob3==null){

} else {
    for (ParseObject obj : ob3) {
        address3 = (obj.getString("address"));
        lat3 = obj.getDouble("latitude");
        lon3 = obj.getDouble("longitude");
        count++;
    }
}

if (userInfo==null){

}else{
   for(ParseObject user:userInfo){
       userNumber1 = user.getString("phone1");
       userNumber2 = user.getString("phone2");
       userNumber3 = user.getString("phone3");
       enableAlerts = user.getBoolean("enablealerts");
       enableSOS = user.getBoolean("enablesos");
       currentTimeline = user.getString("currenttimeline");
       alertradius = user.getInt("alertradius");
   }
}
            final GlobalClass globalVariable = (GlobalClass)getApplicationContext();
//Fields are initialized here
                txtNumZones.setText(String.valueOf(count));
            smsSwitch.setChecked(enableSOS);
            alertSwitch.setChecked(enableAlerts);
            seekBar.setProgress(alertradius);
            textView.setText("Alert radius: " + String.valueOf(alertradius) + " Km" );
            if (currentTimeline.isEmpty()){
                txtMyCountry.setText(globalVariable.getCountry());

            } else{
                txtMyCountry.setText(currentTimeline);

            }
                progressView.stopAnimation();
                progressView.setVisibility(View.INVISIBLE);



        }
    }

    private void showSuccess(String message) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(Settings.this);
        builder.setMessage(message).setTitle("Success")
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

    private void showSubscribe(String message) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(Settings.this);
        builder.setMessage(message).setTitle("Subscription Required")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // do nothing
                        //  createdoc();
                        bp.subscribe(Settings.this,"a100");

                    }
                });
        android.app.AlertDialog alert = builder.create();
        alert.show();
    }


}