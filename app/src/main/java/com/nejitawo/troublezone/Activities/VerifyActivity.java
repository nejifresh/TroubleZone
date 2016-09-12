package com.nejitawo.troublezone.Activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.nejitawo.troublezone.GlobalClass;
import com.nejitawo.troublezone.R;
import com.nejitawo.troublezone.Services.LocationService;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

public class VerifyActivity extends AppCompatActivity {
    public static final String MyPREFERENCES = "MyPrefs";
    public static final String Agree = "nameKey";
    SharedPreferences sharedpreferences;
    public static final String Verified = "verified";

    private Button btnVerify;
    private EditText txtCode;
    private ProgressDialog ringProgressDialog;
private Button btnResend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_verify);
        btnResend = (Button)findViewById(R.id.resend);
        btnResend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ringProgressDialog = ProgressDialog.show(VerifyActivity.this, "Please wait ...",	"Sending verification code to your email ...", true);
                ringProgressDialog.setCancelable(false);
                ParseQuery<ParseObject> newQuery = new ParseQuery<ParseObject>("SentCodes");
                newQuery.whereEqualTo("user",ParseUser.getCurrentUser().getUsername());
                newQuery.whereEqualTo("codesent","YES");

                newQuery.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> objects, ParseException e) {
                        if (e==null && objects.size()>0){
                            for (ParseObject ob : objects ){
                             //   ob.put("codesent","NO");
                                ob.put("mailsent","NO");
                                ob.saveInBackground();
                            }
                          //  Toast.makeText(getApplicationContext(),"correct code ",Toast.LENGTH_LONG).show();

                            ringProgressDialog.dismiss();
                   showAlert("Verification code has been sent to your email ");


                        } else{
                            ringProgressDialog.dismiss();
                        //    Toast.makeText(getApplicationContext(),"invalid code ",Toast.LENGTH_LONG).show();
                            showAlert("Error occured. Try again");
                        }
                    }
                });
            }
        });
        btnVerify = (Button)findViewById(R.id.btn_verify);
        txtCode = (EditText)findViewById(R.id.txtcode) ;
        btnVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!txtCode.getText().toString().isEmpty()){
                    ringProgressDialog = ProgressDialog.show(VerifyActivity.this, "Please wait ...",	"User verification in progress ...", true);
                    ringProgressDialog.setCancelable(false);
                    //  new verifyCode().execute();
                    String code = txtCode.getText().toString();
                    ParseQuery<ParseUser> newQuery = ParseUser.getQuery();
                    newQuery.whereEqualTo("username",ParseUser.getCurrentUser().getUsername());
                    newQuery.whereEqualTo("code",code);
                    newQuery.findInBackground(new FindCallback<ParseUser>() {
                        @Override
                        public void done(List<ParseUser> objects, ParseException e) {
                            if (e==null && objects.size()>0){
                                for (ParseUser ob : objects ){
                                    ob.put("verified","YES");
                                    ob.saveInBackground();
                                }
                               // Toast.makeText(getApplicationContext(),"correct code ",Toast.LENGTH_LONG).show();
                                SharedPreferences.Editor editor = sharedpreferences.edit();
                                editor.putString(Verified, "YES");
                                editor.commit();
                                ringProgressDialog.dismiss();
                                showSuccessDialog();


                            } else{
                                ringProgressDialog.dismiss();
                               // Toast.makeText(getApplicationContext(),"invalid code ",Toast.LENGTH_LONG).show();
                                showAlert("Invalid verification code entered");
                            }
                        }
                    });

                } else {
                    txtCode.setError("Enter verification code");
                    return;
                }

            }
        });
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);


    }

  private class verifyCode extends AsyncTask<Void,Void,String>{
      @Override
      protected void onPreExecute() {


      }


      @Override
      protected String doInBackground(Void... voids) {
          String code = txtCode.getText().toString();
          //Now verify the code on codesent table
        /*  ParseQuery<ParseObject> verify = new ParseQuery<ParseObject>("SentCodes");
          verify.whereEqualTo("user",ParseUser.getCurrentUser().getUsername());
          verify.whereEqualTo("code", code);
          verify.findInBackground(new FindCallback<ParseObject>() {
              @Override
              public void done(List<ParseObject> objects, ParseException e) {
                  if (e==null && objects.size()>0) {
                      for (ParseObject o : objects) {
                          o.put("codesent", "YES");
                          o.saveInBackground();
                      }
                  }
              }
          });
          */

         //verify the user on user verified
          ParseQuery<ParseUser> newQuery = ParseUser.getQuery();
          newQuery.whereEqualTo("username",ParseUser.getCurrentUser().getUsername());
          newQuery.whereEqualTo("code",code);
          newQuery.findInBackground(new FindCallback<ParseUser>() {
              @Override
              public void done(List<ParseUser> objects, ParseException e) {
                  if (e==null && objects.size()>0){
                      for (ParseUser ob : objects ){
                          ob.put("verified","YES");
                          ob.saveInBackground();
                      }
                      Toast.makeText(getApplicationContext(),"correct code ",Toast.LENGTH_LONG).show();
                      SharedPreferences.Editor editor = sharedpreferences.edit();
                      editor.putString(Verified, "YES");
                      editor.commit();
                      ringProgressDialog.dismiss();
                      showSuccessDialog();


                  } else{
                      ringProgressDialog.dismiss();
                      Toast.makeText(getApplicationContext(),"invalid code ",Toast.LENGTH_LONG).show();
                      showAlert("Invalid or wrong code");
                  }
              }
          });
          return null;
      }



      @Override
      protected void onPostExecute(String result){
          super.onPostExecute(result);
ringProgressDialog.dismiss();
      }



    }

    private void showAlert(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(VerifyActivity.this);
        builder.setMessage(message).setTitle("Troublezone")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // do nothing
                        //  createdoc();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void showSuccessDialog(){
        AlertDialog alertDialog = new AlertDialog.Builder(VerifyActivity.this).create();

        // Setting Dialog Title
        alertDialog.setTitle("Success..");

        // Setting Dialog Message
        alertDialog.setMessage("Verification successful..");
alertDialog.setCancelable(false);
        // Setting Icon to Dialog
        alertDialog.setIcon(android.R.drawable.ic_dialog_info);
        // Setting OK Button
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Write your code here to execute after dialog closed
                // Toast.makeText(getApplicationContext(),"New Location created", Toast.LENGTH_SHORT).show();
                //Intent i = new Intent(Basic_Activity.this,Main_Activity.class);
                //startActivity(i);
                // launchRingDialog();

                ParseObject userSettings = new ParseObject("UserSettings");
                userSettings.put("phone1","");
                userSettings.put("phone2","");
                userSettings.put("phone3","");
                userSettings.put("enablealerts",true);
                userSettings.put("enablesos",false);
                userSettings.put("currenttimeline","");
                userSettings.put("alertradius",1);
                userSettings.put("user",ParseUser.getCurrentUser().getUsername());
                userSettings.saveInBackground();


                final GlobalClass globalVariable = (GlobalClass)getApplicationContext();
                globalVariable.setHomeID(1);


Intent servIntent = new Intent(VerifyActivity.this, LocationService.class);
                startService(servIntent);

                Intent i = new Intent(VerifyActivity.this, HomezoneActivity.class);
                startActivity(i);

                finish();
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }
}
