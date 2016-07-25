package com.nejitawo.troublezone.Capture;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Config;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.nejitawo.troublezone.GlobalClass;
import com.nejitawo.troublezone.Imageutil.AndroidMultiPartEntity;
import com.nejitawo.troublezone.Imageutil.Constants;
import com.nejitawo.troublezone.Imageutil.ImageCropActivity;
import com.nejitawo.troublezone.Imageutil.OwnConfig;
import com.nejitawo.troublezone.Imageutil.PicModeSelectDialogFragment;
import com.nejitawo.troublezone.Activities.MainActivity;
import com.nejitawo.troublezone.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Random;

public class NewPicture extends AppCompatActivity implements PicModeSelectDialogFragment.IPicModeSelectListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,  com.google.android.gms.location.LocationListener {


    private EditText txtDescription;
    private Button btnUpload;
    private LinearLayout lnProgress;

    //This is the new cropping functionality
    public static final String TEMP_PHOTO_FILE_NAME = "temp_photo.jpg";

    private  int activePicButton;
    public static final int REQUEST_CODE_UPDATE_PIC = 0x1;

    public static final int REQUEST_CODE_UPDATE_PIC2 = 0x2;

    public static final int REQUEST_CODE_UPDATE_PIC3 = 0x3;
    public static final int REQUEST_CODE_UPDATE_PIC4 = 0x4;

    public static final int REQUEST_AUDIO = 45;
    private ImageView imgProfile;
    private Uri fileUri;
    String fileName = "";
    String FilePath = "";



    long totalSize = 0;
    private int chooserType;
    private ProgressBar progressBar;
    private TextView txtPercentage;

    private TextView textViewFile;
    private ProgressBar pbar;
    private ImageView imageViewThumbnail;



    private String filePath;
private String audiofile= "";
    private String audioPath;
    private String realaudioPath;

    private static final String TAG = NewPicture.class.getSimpleName();
    private int whatImage = 0;

    private Double nLatitude = 0.10;
    private Double nLongitude = 0.10;

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
    private Button btnChooseAudio;
ProgressDialog gpsDialog;

    private TextView txtYears;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.takepicture);
        initViews();
      //  gpsDialog =  ProgressDialog.show(NewHandy.this,"Please wait..","Getting GPS Location",true);
      //  gpsDialog.setCancelable(false);
/*
        buildGoogleApiClient();
        createLocationRequest();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
        if (mGoogleApiClient.isConnected() && mRequestingLocationUpdates) {
            //startLocationUpdates();
            displayLocation();
        }

        */

    }

    private void initViews(){

lnProgress = (LinearLayout)findViewById(R.id.lnprogress);

        txtPercentage = (TextView) findViewById(R.id.txtPercentage);
        progressBar = (ProgressBar) findViewById(R.id.progressBarVid);
        txtDescription = (EditText)findViewById(R.id.txtDescription);


        btnUpload = (Button)findViewById(R.id.btn_signup);
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
buildConfirmAlert();
            }
        });

        imgProfile = (ImageView)findViewById(R.id.imgProfile);
/*
        Button buttonTakePicture = (Button)findViewById(R.id.btnPhoto);
        buttonTakePicture.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                whatImage = 1;
                // chooseImage();
                activePicButton =  REQUEST_CODE_UPDATE_PIC2;
                showAddProfilePicDialog();

            }
        });

        Button buttonTakePicture2 = (Button) findViewById(R.id.btnPhoto2);
        buttonTakePicture2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                whatImage = 2;
                //  chooseImage();
                activePicButton =  REQUEST_CODE_UPDATE_PIC3;
                showAddProfilePicDialog();
            }
        });

        Button buttonTakePicture3 = (Button) findViewById(R.id.btnPhoto3);
        buttonTakePicture3.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                whatImage = 3;
                //  chooseImage();
                activePicButton =  REQUEST_CODE_UPDATE_PIC4;
                showAddProfilePicDialog();
            }
        });

        */

        textViewFile = (TextView) findViewById(R.id.textViewFile);
        imageViewThumbnail = (ImageView) findViewById(R.id.imageViewThumb);
        pbar = (ProgressBar) findViewById(R.id.progressBar);
        pbar.setVisibility(View.GONE);
/*
        textViewFile2 = (TextView) findViewById(R.id.textViewFile2);
        imageViewThumbnail2 = (ImageView) findViewById(R.id.imageViewThumb2);
        pbar2 = (ProgressBar) findViewById(R.id.progressBar2);
        pbar2.setVisibility(View.GONE);

        textViewFile3 = (TextView) findViewById(R.id.textViewFile3);
        imageViewThumbnail3 = (ImageView) findViewById(R.id.imageViewThumb3);
        pbar3 = (ProgressBar) findViewById(R.id.progressBar3);
        pbar3.setVisibility(View.GONE);
*/
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activePicButton = REQUEST_CODE_UPDATE_PIC;
                showAddProfilePicDialog();

            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    private void buildConfirmAlert(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(NewPicture.this);
        // Setting Dialog Title
        alertDialog.setTitle("Confirm Posting...");

        // Setting Dialog Message
        alertDialog.setMessage("Are you sure you want to post this picture?");

        // Setting Icon to Dialog
        alertDialog.setIcon(android.R.drawable.ic_menu_save);

        // Setting Positive "Yes" Button
        alertDialog.setPositiveButton("YES",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // createdoc();
                        // Write your code here to invoke YES event
                        //Upload the file first



                        if (txtDescription.getText().toString().isEmpty()){
                            showError("Please enter a description");
                            return;
                        }
                      /**


                       **/

                        if ((!FilePath.isEmpty())) {
                            ringProgressDialog = ProgressDialog.show(NewPicture.this, "Please wait ...",	"Picture upload in progress ...", true);
                            ringProgressDialog.setCancelable(false);   ringProgressDialog.setCancelable(false);

                            new UploadFileToServer().execute();
                        } else{
                            //throw error message
                            showAlert("Image is required");

                        }

                    }
                });

        // Setting Negative "NO" Button
        alertDialog.setNegativeButton("NO",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Write your code here to invoke NO event
                        Toast.makeText(getApplicationContext(),
                                "Action cancelled by user", Toast.LENGTH_SHORT)
                                .show();
                        dialog.cancel();
                    }
                });

        // Showing Alert Message
        alertDialog.show();

    }


    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
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
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Toast.makeText(getApplicationContext(),
                        "This device is not supported.", Toast.LENGTH_LONG)
                        .show();
                finish();
            }
            return false;
        }
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        checkPlayServices();
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
        Log.d(TAG,"Location has changed");

        // Displaying the new location on UI
        displayLocation();
    }

    private void showAddProfilePicDialog() {
        PicModeSelectDialogFragment dialogFragment = new PicModeSelectDialogFragment();
        dialogFragment.setiPicModeSelectListener(this);
        dialogFragment.show(getFragmentManager(), "picModeSelector");
    }

    private void actionProfilePic(String action) {
        Intent intent = new Intent(this, ImageCropActivity.class);
        intent.putExtra("ACTION", action);
        if (activePicButton ==   REQUEST_CODE_UPDATE_PIC){
            startActivityForResult(intent, REQUEST_CODE_UPDATE_PIC);
        }

        if (activePicButton ==   REQUEST_CODE_UPDATE_PIC2){
            startActivityForResult(intent, REQUEST_CODE_UPDATE_PIC2);
        }

        if (activePicButton ==   REQUEST_CODE_UPDATE_PIC3){
            startActivityForResult(intent, REQUEST_CODE_UPDATE_PIC3);
        }

        if (activePicButton ==   REQUEST_CODE_UPDATE_PIC4){
            startActivityForResult(intent, REQUEST_CODE_UPDATE_PIC4);
        }

    }

    @Override
    public void onPicModeSelected(String mode) {
        String action = mode.equalsIgnoreCase(Constants.PicModes.CAMERA) ? Constants.IntentExtras.ACTION_CAMERA : Constants.IntentExtras.ACTION_GALLERY;
        actionProfilePic(action);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent result) {
        if (requestCode == REQUEST_CODE_UPDATE_PIC) {
            if (resultCode == RESULT_OK) {
                String imagePath = result.getStringExtra(Constants.IntentExtras.IMAGE_PATH);

                showCroppedImage(imagePath);

                } else if (resultCode == RESULT_CANCELED) {

            } else {
                String errorMsg = result.getStringExtra(ImageCropActivity.ERROR_MSG);
                Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show();
            }
        }



        //Audio file chooser
        if (requestCode == REQUEST_AUDIO){
            if (resultCode == RESULT_OK){
                Uri audioUri = result.getData();
                audioPath = audioUri.getPath(); //Upload the audio file

                if ( (calculateFileSize(audioPath)) > 7.1){
                    //File size is greater than 3.1 MB
                    audioPath = null;
                    Toast.makeText(getApplicationContext(), "File size is larger than 3MB",Toast.LENGTH_LONG).show();

                } else{
                    //Load file for upload
                    Cursor returnCursor = getContentResolver().query(audioUri,null,null,null,null);
                    int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
                    returnCursor.moveToFirst();
                    audiofile = returnCursor.getString(nameIndex);
                    Toast.makeText(getApplicationContext(),audiofile,Toast.LENGTH_LONG).show();

                    File original = new File(audioPath);
                    String identifier =  getRandomString(5);
                    File file = new File(
                            (original.getParent() + File.separator + "dj_" + identifier
                                    + "_mix.mp3"));

                    audiofile =   "dj_" + identifier + "_mix.mp3";
                    realaudioPath = audioUri.getPath();

                }
            }
        }


    }

    private void showCroppedImage(String mImagePath) {
        if (mImagePath != null) {
            Bitmap myBitmap = BitmapFactory.decodeFile(mImagePath);
            imgProfile.setImageBitmap(myBitmap);
            //this sets things up for upload by ASYNC Task
            try {
                FilePath = compressForSaveImage(mImagePath, 1);
                File mainFile = new File(FilePath);
                fileName = mainFile.getName().toString();
                // File mainFile = new File(FilePath);
                // fileName = mainFile.getName().toString();
                // FilePath = mImagePath;
            } catch (Exception e){
                e.printStackTrace();
            }
            File original = new File(FilePath);

        }
    }



    protected String compressForSaveImage(String fileImage, int scale)
            throws Exception {
        try {
            ExifInterface exif = new ExifInterface(fileImage);
            String width = exif.getAttribute(ExifInterface.TAG_IMAGE_WIDTH);
            String length = exif.getAttribute(ExifInterface.TAG_IMAGE_LENGTH);
            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            int rotate = 0;
            if (Config.DEBUG) {
                // Log.i(TAG, "Before: " + width + "x" + length);
            }

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate = -90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate = 90;
                    break;
            }

            int w = Integer.parseInt(width);
            int l = Integer.parseInt(length);

            int what = w > l ? w : l;

            BitmapFactory.Options options = new BitmapFactory.Options();
            if (what > 1500) {
                options.inSampleSize = scale * 4;
            } else if (what > 1000 && what <= 1500) {
                options.inSampleSize = scale * 3;
            } else if (what > 400 && what <= 1000) {
                options.inSampleSize = scale * 2;
            } else {
                options.inSampleSize = scale;
            }
            if (Config.DEBUG) {
                // Log.i(TAG, "Scale: " + (what / options.inSampleSize));
                // Log.i(TAG, "Rotate: " + rotate);
            }
            Bitmap bitmap = BitmapFactory.decodeFile(fileImage, options);
            Bitmap b2; //   Bitmap b2 = Bitmap.createScaledBitmap(bitmap,300,300,false);//i added this
            File original = new File(fileImage);
            String identifier =  getRandomString(5);
            File file = new File(
                    (original.getParent() + File.separator + "wife_" + identifier
                            + "_image1.jpg"));

            fileName =   "wife_" + identifier + "_image1.jpg";




            /**
             *   (original.getParent() + File.separator + original.getName()
             .replace(".", "_fact_" + scale + ".")));
             */


            FileOutputStream stream = new FileOutputStream(file);
            if (rotate != 0) {
                Matrix matrix = new Matrix();
                matrix.setRotate(rotate);
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                        bitmap.getHeight(), matrix, false);

                /**
                 bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                 bitmap.getHeight(), matrix, false);
                 **/
            }
            // b2.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            Bitmap b3 = Bitmap.createScaledBitmap(bitmap, 400, 400, true);
            b3.compress(Bitmap.CompressFormat.JPEG, 100, stream);

            if (Config.DEBUG) {
                ExifInterface exifAfter = new ExifInterface(
                        file.getAbsolutePath());
                String widthAfter = exifAfter
                        .getAttribute(ExifInterface.TAG_IMAGE_WIDTH);
                String lengthAfter = exifAfter
                        .getAttribute(ExifInterface.TAG_IMAGE_LENGTH);
                if (Config.DEBUG) {
                    Log.i("TAG", "After: " + widthAfter + "x" + lengthAfter);
                }
            }
            stream.flush();
            stream.close();
            return file.getAbsolutePath();

        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Corrupt or deleted file???");
        }
    }


    private static final String ALLOWED_CHARACTERS ="0123456789qwertyuiopasdfghjklzxcvbnm";

    private static String getRandomString(final int sizeOfRandomString)
    {
        final Random random=new Random();
        final StringBuilder sb=new StringBuilder(sizeOfRandomString);
        for(int i=0;i<sizeOfRandomString;++i)
            sb.append(ALLOWED_CHARACTERS.charAt(random.nextInt(ALLOWED_CHARACTERS.length())));
        return sb.toString();
    }

    private class UploadAudioFileToServer extends AsyncTask<Void, Integer, String> {
        @Override
        protected void onPreExecute() {
            // setting progress bar to zero
            progressBar.setProgress(0);
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            // Making progress bar visible
            progressBar.setVisibility(View.VISIBLE);

            // updating progress bar value
            progressBar.setProgress(progress[0]);

            // updating percentage value
            txtPercentage.setText(String.valueOf(progress[0]) + "%");
        }

        @Override
        protected String doInBackground(Void... params) {
            return uploadFile();
        }

        @SuppressWarnings("deprecation")
        private String uploadFile() {
            String responseString = null;

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(OwnConfig.FILE_UPLOAD_URL);

            try {
                AndroidMultiPartEntity entity = new AndroidMultiPartEntity(
                        new AndroidMultiPartEntity.ProgressListener() {

                            @Override
                            public void transferred(long num) {
                                publishProgress((int) ((num / (float) totalSize) * 100));
                            }
                        });

                File sourceFile = new File(realaudioPath);

                // Adding file data to http body
                entity.addPart("audio", new FileBody(sourceFile));

                // Extra parameters if you want to pass to server
                entity.addPart("website",
                        new StringBody("www.androidhive.info"));
                entity.addPart("email", new StringBody("abc@gmail.com"));

                totalSize = entity.getContentLength();
                httppost.setEntity(entity);

                // Making server call
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity r_entity = response.getEntity();

                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == 200) {
                    // Server response

                    responseString = "Success";//EntityUtils.toString(r_entity);
                } else {
                    responseString = "Error occurred! Http Status Code: "
                            + statusCode;
                }

            } catch (ClientProtocolException e) {
                responseString = e.toString();
            } catch (IOException e) {
                responseString = e.toString();
            }

            return responseString;

        }

        @Override
        protected void onPostExecute(String result) {
            Log.e(TAG, "Response from Application: " + result);
            if (result == "Success"){
                //  createdoc(); //uplaod second image
                Toast.makeText(getApplicationContext(),"Uploaded audio file",Toast.LENGTH_LONG).show();
                new UploadFileToServer().execute();
            } else {

                showAlert(result);
                ringProgressDialog.dismiss();
            }
            // showing the server response in an alert dialog


            super.onPostExecute(result);
        }

    }


    private class UploadFileToServer extends AsyncTask<Void, Integer, String> {
        @Override
        protected void onPreExecute() {
            lnProgress.setVisibility(View.VISIBLE);
            // setting progress bar to zero
            progressBar.setProgress(0);
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            // Making progress bar visible
            progressBar.setVisibility(View.VISIBLE);

            // updating progress bar value
            progressBar.setProgress(progress[0]);

            // updating percentage value
            txtPercentage.setText(String.valueOf(progress[0]) + "%");
        }

        @Override
        protected String doInBackground(Void... params) {
            return uploadFile();
        }

        @SuppressWarnings("deprecation")
        private String uploadFile() {
            String responseString = null;

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(OwnConfig.FILE_UPLOAD_URL);

            try {
                AndroidMultiPartEntity entity = new AndroidMultiPartEntity(
                        new AndroidMultiPartEntity.ProgressListener() {

                            @Override
                            public void transferred(long num) {
                                publishProgress((int) ((num / (float) totalSize) * 100));
                            }
                        });

                File sourceFile = new File(FilePath);

                // Adding file data to http body
                entity.addPart("image", new FileBody(sourceFile));

                // Extra parameters if you want to pass to server
                entity.addPart("website",
                        new StringBody("www.androidhive.info"));
                entity.addPart("email", new StringBody("abc@gmail.com"));

                totalSize = entity.getContentLength();
                httppost.setEntity(entity);

                // Making server call
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity r_entity = response.getEntity();

                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == 200) {
                    // Server response

                    responseString = "Success";//EntityUtils.toString(r_entity);
                } else {
                    responseString = "Error occurred! Http Status Code: "
                            + statusCode;
                }

            } catch (ClientProtocolException e) {
                responseString = e.toString();
            } catch (IOException e) {
                responseString = e.toString();
            }

            return responseString;

        }

        @Override
        protected void onPostExecute(String result) {
            Log.e(TAG, "Response from Application: " + result);
            lnProgress.setVisibility(View.GONE);
            if (result == "Success"){
                //  createdoc(); //uplaod second image
                Toast.makeText(getApplicationContext(),"Uploaded picture",Toast.LENGTH_LONG).show();
               // new UploadFileToServer2().execute();
                createdoc();
            } else {
ringProgressDialog.dismiss();
                showAlert("An error occurred while uploading. Please try again");
            }
            // showing the server response in an alert dialog


            super.onPostExecute(result);
        }

    }



    private void showAlert(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(NewPicture.this);
        builder.setMessage(message).setTitle("Application Response")
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
        AlertDialog alertDialog = new AlertDialog.Builder(NewPicture.this).create();

        // Setting Dialog Title
        alertDialog.setTitle("Success..");

        // Setting Dialog Message
        alertDialog.setMessage("Incident Image uploaded successfully..");

        // Setting Icon to Dialog
        alertDialog.setIcon(android.R.drawable.ic_dialog_info);
        // Setting OK Button
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Write your code here to execute after dialog closed
                // Toast.makeText(getApplicationContext(),"New Location created", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(NewPicture.this,MainActivity.class);
                startActivity(i);
                finish();
                // launchRingDialog();
               // Intent locIntent = new Intent(getApplicationContext(), LocationService.class);
               // startService(locIntent);

            }
        });

        // Showing Alert Message
        alertDialog.show();
    }

    private void showError(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message).setTitle("Registration Error")
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
            ReverseGeocodingTask myTask = new ReverseGeocodingTask(getApplicationContext());
            myTask.execute();

        } else {
           // gpsDialog.dismiss();
            buildAlertMessageNoGps();
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

    private void createdoc(){
        final  GlobalClass globalVariable = (GlobalClass)getApplicationContext();
        ParseQuery<ParseObject>query = new ParseQuery<ParseObject>("Alarms");
        query.whereEqualTo("objectId",globalVariable.getIncidentID());
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null){
                    for (ParseObject newEvent : objects){
                        newEvent.put("description",txtDescription.getText().toString());
                        newEvent.put("contenttype","IMAGE");
                        newEvent.put("show","YES");
                        newEvent.put("mainimage", "http://108.60.209.155:8080/AndroidFileUpload/uploads/" + fileName);

                        newEvent.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {
                                    //Save the installation in Shared Preferences

                                    // ParseUser me  = ParseUser.getCurrentUser();
                                    //  me.put("Image", "http://108.60.209.155:8080/AndroidFileUpload/uploads/" + fileName);
                                    //  me.saveInBackground();

                                    ringProgressDialog.dismiss();
                                    showSuccessDialog();
                                } else {
                                    ringProgressDialog.dismiss();
                                    Toast.makeText(getApplicationContext(), "Error occured while saving request, Pls try again", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }
                }
            }
        });




    }


    //turn gps on
    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(NewPicture.this);
        builder.setMessage("Your location is required  do you want to enable it?")
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
                currentCountry = address.getCountryName();
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

    private void chooseAudio(){
        Intent audiofile_chooser_intent;
        audiofile_chooser_intent = new Intent();
        audiofile_chooser_intent.setAction(Intent.ACTION_GET_CONTENT);
        audiofile_chooser_intent.setType("audio/*");

        startActivityForResult(audiofile_chooser_intent,REQUEST_AUDIO);

    }

    public Float calculateFileSize(String filepath) {
        //String filepathstr=filepath.toString();
        File file = new File(filepath);

        // Get length of file in bytes
        long fileSizeInBytes = file.length();

        float fileSizeInKB = fileSizeInBytes / 1024;
        // Convert the KB to MegaBytes (1 MB = 1024 KBytes)
        float fileSizeInMB = fileSizeInKB / 1024;

        String calString = Float.toString(roundTwoDecimals(fileSizeInMB));

        return roundTwoDecimals(fileSizeInMB);

    }

    private static float roundTwoDecimals(float d)
    {
        DecimalFormat twoDForm = new DecimalFormat("####.#");
        return Float.valueOf(twoDForm.format(d));

    }


}
