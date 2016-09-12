package com.nejitawo.troublezone.Activities;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;


import com.nejitawo.troublezone.R;
import com.nejitawo.troublezone.Services.LocationService;
import com.parse.ParseUser;


/**
 * Created by Neji on 11/17/2014.
 */
public class SplashActivity extends AppCompatActivity {
    ImageView logo;
    TextView alto;
    SharedPreferences sharedpreferences;
    public static final String Installed = "inst";
    private static final String appversion = "1.0";
    private String myImage = "";
    private int exists = 0;
    private String update = "";
    private String videoKey = "";
    private String chatKey = "";
    private ImageView mLogo;
    private TextView welcomeText;
    SharedPreferences sharedPreferences;
    public static final String MyPREFERENCES = "MyPrefs";
    public static final String Agree = "nameKey";
    public static final String Verified = "verified";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        logo = (ImageView)findViewById(R.id.profile_image);
        alto= (TextView)findViewById(R.id.textstart);
        final    ParseUser currentUser = ParseUser.getCurrentUser();
        mLogo = (ImageView) findViewById(R.id.logo);
        welcomeText = (TextView) findViewById(R.id.welcome_text);
        setAnimation();
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        if (sharedpreferences.contains(Agree)){


            if (currentUser == null){
                Intent intent = new Intent(SplashActivity.this,SignUp.class);
                startActivity(intent);
                finish();
            } else {

                if (sharedpreferences.contains(Verified)){

                    Intent intent = new Intent(SplashActivity.this,MainActivity.class);
                    Intent locIntent = new Intent(getApplicationContext(), LocationService.class);
                    startService(locIntent);
                    startActivity(intent);
                    finish();

                }
                else {
                    Intent intent = new Intent(SplashActivity.this,VerifyActivity.class);
                    startActivity(intent);
                    finish();
                }

            }



        } else{
            Intent intent = new Intent(SplashActivity.this,TermsActivity.class);
            startActivity(intent);
            finish();
        }









                }




           //Find out if this user has created a profile after registering



      //  new PuffInAnimation(logo).animate();
        //new BounceAnimation(logo).setNumOfBounces(3)                .setDuration(Animation.DURATION_LONG).animate();

        /****** Create Thread that will sleep for 5 seconds *************/





    private void showUpdate(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message).setTitle("Critical Update Required")
                .setIcon(R.drawable.downloadicon)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // go to app on play store
                        final String appName = getPackageName();
                        try {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appName)));
                        } catch (android.content.ActivityNotFoundException anfe) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + appName)));
                        }
                        finish();

                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }


    //turn gps on
    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();

    }

    private void setAnimation() {

            animation2();
            animation3();

    }

    private void animation1() {
        ObjectAnimator scaleXAnimation = ObjectAnimator.ofFloat(mLogo, "scaleX", 5.0F, 1.0F);
        scaleXAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        scaleXAnimation.setDuration(1200);
        ObjectAnimator scaleYAnimation = ObjectAnimator.ofFloat(mLogo, "scaleY", 5.0F, 1.0F);
        scaleYAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        scaleYAnimation.setDuration(1200);
        ObjectAnimator alphaAnimation = ObjectAnimator.ofFloat(mLogo, "alpha", 0.0F, 1.0F);
        alphaAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        alphaAnimation.setDuration(1200);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(scaleXAnimation).with(scaleYAnimation).with(alphaAnimation);
        animatorSet.setStartDelay(500);
        animatorSet.start();
    }

    private void animation2() {
        mLogo.setAlpha(1.0F);
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.translate_top_to_center);
        mLogo.startAnimation(anim);
    }

    private void animation3() {
        ObjectAnimator alphaAnimation = ObjectAnimator.ofFloat(welcomeText, "alpha", 0.0F, 1.0F);
        alphaAnimation.setStartDelay(1700);
        alphaAnimation.setDuration(500);
        alphaAnimation.start();
    }


}
