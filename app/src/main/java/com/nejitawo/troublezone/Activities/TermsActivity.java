package com.nejitawo.troublezone.Activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import com.nejitawo.troublezone.R;
import com.parse.ParseInstallation;


/**
 * Created by Neji on 11/17/2014.
 */
public class TermsActivity extends Activity {
    public static final String MyPREFERENCES = "MyPrefs";
    public static final String Agree = "nameKey";
    SharedPreferences sharedpreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.terms);
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

    }


    public void agreeClick(View view){
       buildConfirmAlert();
    }

    public void refuseClick(View view){
finish();
    }

    private void buildConfirmAlert(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        // Setting Dialog Title
        alertDialog.setTitle("Terms and Conditions...");

        // Setting Dialog Message
        alertDialog.setMessage("Do you agree to these Terms and Conditions? ");

        // Setting Icon to Dialog
        alertDialog.setIcon(android.R.drawable.ic_menu_save);

        // Setting Positive "Yes" Button
        alertDialog.setPositiveButton("YES",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        SharedPreferences.Editor editor = sharedpreferences.edit();
                        editor.putString(Agree, "YES");
                        editor.commit();

                        ParseInstallation.getCurrentInstallation().saveInBackground();
                        Intent intent = new Intent(TermsActivity.this,SplashActivity.class);
                        startActivity(intent);
                        finish();

                    }
                });

        // Setting Negative "NO" Button
        alertDialog.setNegativeButton("NO",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                      finish();
                    }
                });

        // Showing Alert Message
        alertDialog.show();

    }
}
