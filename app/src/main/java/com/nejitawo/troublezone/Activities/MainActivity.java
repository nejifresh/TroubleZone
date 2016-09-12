package com.nejitawo.troublezone.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.nejitawo.troublezone.Fragments.TabFragment;
import com.nejitawo.troublezone.GlobalClass;
import com.nejitawo.troublezone.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private FragmentManager mFragmentManager;
    private FragmentTransaction mFragmentTransaction;
    private DrawerLayout drawer;
    private NavigationView navigationView;

    SharedPreferences sharedpreferences;
    public static final String Verified = "verified";
    public static final String MyPREFERENCES = "MyPrefs";
private Tracker mTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(0xFFFFFFFF);
        toolbar.setTitle("Troublezone");
        GlobalClass application = (GlobalClass)  getApplication();
        mTracker = application.getDefaultTracker();
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);



         drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mFragmentManager = getSupportFragmentManager();
        mFragmentTransaction = mFragmentManager.beginTransaction();
        mFragmentTransaction.replace(R.id.containerView,new TabFragment()).commit();


    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
         //   return true;
            initShareIntent();
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        // Handle navigation view item clicks here.
        int id = item.getItemId();
         if (id == R.id.nav_slideshow) {

             FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
             fragmentTransaction.replace(R.id.containerView,new TabFragment()).commit();

        } else if (id == R.id.nav_manage) {
             final String appName = getPackageName();
             try {
                 startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appName)));
             } catch (android.content.ActivityNotFoundException anfe) {
                 startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + appName)));
             }

        } else if (id == R.id.nav_settings)


         {
            // initShareIntent();
             Intent intent = new Intent(MainActivity.this,Settings.class);
             startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void initShareIntent(){
        try{
            //  AssetManager am = getAssets();
            //  InputStream inputStream = am.open("nairababe.jpg");
            //  File file = createFileFromInputStream(inputStream);
            // File filePath = getFileStreamPath(file);
            final String appName = getPackageName();

            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_SUBJECT,"Get the Troublezone App!!");
            shareIntent.putExtra(Intent.EXTRA_TEXT, "Hi dear, do you know there's now an app that tells you when there is danger and trouble around you? " + "\n" + "\n" +  "Don't be caught unaware!. Download the Troublezone App today on Google Play:" + "\n"+   "https://play.google.com/store/apps/details?id=" + appName);
            // shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));  //optional//use this when you want to send an image
            shareIntent.setType("text/plain");
            //shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(Intent.createChooser(shareIntent, "Invite via"));
            mTracker.send(new HitBuilders.EventBuilder()
                    .setCategory("Action")
                    .setAction("App Invites")
                    .build());
        } catch (Exception e){
            Log.e("Errorshare",e.getMessage());
        }


    }

    @Override
    protected void onStart(){
        super.onStart();
        ParseUser currentUser = ParseUser.getCurrentUser();

        if (currentUser == null){
            Intent intent = new Intent(this,SignUp.class);
            startActivity(intent);
            finish();

        }else{

            if (sharedpreferences.contains(Verified)){

            } else{
                Intent intent = new Intent(MainActivity.this,VerifyActivity.class);
                startActivity(intent);
                finish();
            }

          /*  ParseQuery<ParseUser> userParseQuery = ParseQuery.getUserQuery();
            userParseQuery.findInBackground(new FindCallback<ParseUser>() {
                @Override
                public void done(List<ParseUser> objects, ParseException e) {
                    if (e==null){
                        for (ParseUser user: objects){

                            String         verified =  user.getString("verified");
                            if (verified == "NO"){
                                //USER IS NOT VERIFIED
                                Intent intent = new Intent(MainActivity.this,VerifyActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        }
                    }
                }
            });*/

           /* String verified = currentUser.getString("verified");
            if (verified == "NO"){
                //USER IS NOT VERIFIED
                Intent intent = new Intent(this,VerifyActivity.class);
                startActivity(intent);
                finish();
            }*/

        }
    }

    @Override
    protected void onDestroy(){
        final GlobalClass globalVariable = (GlobalClass)getApplicationContext();
        globalVariable.setScreenIndex(0);

        super.onDestroy();

    }


}
