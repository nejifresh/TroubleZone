package com.nejitawo.troublezone.Activities;

import android.content.Intent;
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

import com.nejitawo.troublezone.Fragments.TabFragment;
import com.nejitawo.troublezone.GlobalClass;
import com.nejitawo.troublezone.R;
import com.parse.ParseUser;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private FragmentManager mFragmentManager;
    private FragmentTransaction mFragmentTransaction;
    private DrawerLayout drawer;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(0xFFFFFFFF);




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

        } else if (id == R.id.nav_share)

         {
             initShareIntent();
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
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_SUBJECT,"Get the Unjoo App!!");
            shareIntent.putExtra(Intent.EXTRA_TEXT, "Are you looking for a house to rent or property for sale? Self Contained, 2 -3 Bedroom Apartment etc? Find it on Unjoo. Download on Google Play: https://play.google.com/store/apps/details?id=com.nejitawo.unjoo");
            // shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));  //optional//use this when you want to send an image
            shareIntent.setType("text/plain");
            //shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(Intent.createChooser(shareIntent, "Share via"));
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

            String verified = currentUser.getString("verified");
            if (verified == "NO"){
                //USER IS NOT VERIFIED
                Intent intent = new Intent(this,VerifyActivity.class);
                startActivity(intent);
                finish();
            }

        }
    }

    @Override
    protected void onDestroy(){
        final GlobalClass globalVariable = (GlobalClass)getApplicationContext();
        globalVariable.setScreenIndex(0);

        super.onDestroy();

    }

}
