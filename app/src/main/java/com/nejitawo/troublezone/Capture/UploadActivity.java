package com.nejitawo.troublezone.Capture;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.nejitawo.troublezone.Activities.MainActivity;
import com.nejitawo.troublezone.GlobalClass;
import com.nejitawo.troublezone.Imageutil.AndroidMultiPartEntity;
import com.nejitawo.troublezone.Imageutil.OwnConfig;
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
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

/**
 * Created by Neji on 2/5/2015.
 */
public class UploadActivity extends Activity {
    // LogCat tag
    private static final String TAG = UploadActivity.class.getSimpleName();
    private static final String DATASTORE_MANGER_DIR = "eData";
    private static final String DATASTORE_NAME = "electowatch";
    public static final String MyPREFERENCES = "MyPrefs";
    public static final String Party = "partyNameKey";
    public static final String polling = "pollingBoothKey";
    public static final String agent = "agentNameKey";
    public static final String phone = "phoneKey";
    public static final String lgaKey = "lgaKey";
    public static final String stateKey = "stateKey";
    SharedPreferences sharedpreferences;
    private ProgressBar progressBar;
    private String filePath = null;
    private TextView txtPercentage;
    private ImageView imgPreview;
    private VideoView vidPreview;
    private Button btnUpload;
    long totalSize = 0;
    private TextView videoDesc;
    String fileName = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        txtPercentage = (TextView) findViewById(R.id.txtPercentage);
        btnUpload = (Button) findViewById(R.id.btnUpload);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        imgPreview = (ImageView) findViewById(R.id.imgPreview);
        vidPreview = (VideoView) findViewById(R.id.videoPreview);
videoDesc = (TextView)findViewById(R.id.videoDesc);
        // Changing action bar background color
       // getActionBar().setBackgroundDrawable(   new ColorDrawable(Color.parseColor(getResources().getString(R.color.action_bar))));

        // Receiving the data from previous activity
        Intent i = getIntent();

        // image or video path that is captured in previous activity
        filePath = i.getStringExtra("filePath");

        File mainFile = new File(filePath);
      fileName =   mainFile.getName().toString();

        // boolean flag to identify the media type, image or video
        boolean isImage = i.getBooleanExtra("isImage", true);

        if (filePath != null) {
            // Displaying the image or video on the screen
            previewMedia(isImage);
        } else {
            Toast.makeText(getApplicationContext(),
                    "Sorry, file path is missing!", Toast.LENGTH_LONG).show();
        }

        btnUpload.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // uploading the file to server
                if(!(videoDesc.getText().toString().matches(""))) {
                    new UploadFileToServer().execute();
                } else{
                    videoDesc.setError("Enter Video Description");
                }
            }
        });

    }

    /**
     * Displaying captured image/video on the screen
     * */
    private void previewMedia(boolean isImage) {
        // Checking whether captured media is image or video
        if (isImage) {
            imgPreview.setVisibility(View.VISIBLE);
            vidPreview.setVisibility(View.GONE);
            // bimatp factory
            BitmapFactory.Options options = new BitmapFactory.Options();

            // down sizing image as it throws OutOfMemory Exception for larger
            // images
            options.inSampleSize = 8;

            final Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);

            imgPreview.setImageBitmap(bitmap);
        } else {
            imgPreview.setVisibility(View.GONE);
            vidPreview.setVisibility(View.VISIBLE);
            vidPreview.setVideoPath(filePath);
            // start playing
            vidPreview.start();
        }
    }
    /**
     * Uploading the file to server
     * */
    ProgressDialog ringProgressDialog;

    private class UploadFileToServer extends AsyncTask<Void, Integer, String> {
        @Override
        protected void onPreExecute() {
            // setting progress bar to zero
            ringProgressDialog =
                    ProgressDialog.show(UploadActivity.this, "Please wait ...", " video upload in progress..", true);
            ringProgressDialog.setCancelable(false);
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

                File sourceFile = new File(filePath);

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
            Log.e(TAG, "Response from server: " + result);
            if (result == "Success"){
                createdoc();
                ringProgressDialog.dismiss();
            } else {
                ringProgressDialog.dismiss();
                showAlert(result);
            }
            // showing the server response in an alert dialog
           // showAlert(result);

            super.onPostExecute(result);
        }

    }

    /**
     * Method to show alert dialog
     * */
    private void showAlert(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message).setTitle("Response from Servers")
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

    private URI createServerURI()
            throws URISyntaxException {
        URI uri = new URI("https://finditt.cloudant.com/electowatch");
        return uri;
    }

    private void createdoc(){
        final GlobalClass globalVariable = (GlobalClass)getApplicationContext();
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Alarms");
        query.whereEqualTo("objectId",globalVariable.getIncidentID());
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null){
                    for (ParseObject newEvent : objects){
                        newEvent.put("description",videoDesc.getText().toString());
                        newEvent.put("contenttype","VIDEO");
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

/**
    private void createdoc(){


        // Write your code here to RETRIEVE STORED PREFERENCES
        String partyName = "";
        String pollingUnit = "";
        String agentName = "";
        String agentPhone = "";
        String lgaName = "";
        String stateName = "";
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        if (sharedpreferences.contains(Party)) {
            partyName = (sharedpreferences.getString(Party, ""));

        }
        if (sharedpreferences.contains(polling)) {
            pollingUnit = (sharedpreferences.getString(polling, ""));

        }
        if (sharedpreferences.contains(agent)) {
            agentName = (sharedpreferences.getString(agent, ""));

        }
        if (sharedpreferences.contains(phone)) {
            agentPhone = (sharedpreferences.getString(phone, ""));

        }
        if (sharedpreferences.contains(lgaKey)) {
            lgaName = (sharedpreferences.getString(lgaKey, ""));
        }

        if (sharedpreferences.contains(stateKey)) {
            stateName = (sharedpreferences.getString(stateKey, ""));

        }
        //create a document
        MutableDocumentRevision rev = new MutableDocumentRevision();
        //Build up document content
        Map<String,Object> json = new HashMap<String, Object>();
        if(!(videoDesc.getText().toString().matches(""))){
            json.put("Video Description",videoDesc.getText().toString());
        } else{
            videoDesc.setError("Enter Video Description");
            return;
        }

        json.put("Party Name",partyName.toString());
        json.put("Polling Unit",pollingUnit.toString());
        json.put("Agent Name",agentName.toString());
        json.put("Agent Phone",agentPhone.toString());
        json.put("LGA",lgaName.toString());
        json.put("State",stateName.toString());
        json.put("Latitude",latitude);
        json.put("Longitude",longitude);
        json.put("Category","Video");
        json.put("URL","http://108.60.209.155:8080/AndroidFileUpload/uploads/"+fileName);
        // 1) create a java calendar instance
        Calendar calendar = Calendar.getInstance();

// 2) get a java.util.Date from the calendar instance.
//    this date will represent the current instant, or "now".
        java.util.Date now = calendar.getTime();

// 3) a java current time (now) instance
        java.sql.Timestamp currentTimestamp = new java.sql.Timestamp(now.getTime());
        //CREATE DOCUMENT BODY
        long tsLong = System.currentTimeMillis()/1000;
        //  String ts = tsLong.toString();
        json.put("Current Time",calendar.getTime().toString());

        rev.body = DocumentBodyFactory.create(json);
        //CREATE DOCUMENT BODY
        rev.body = DocumentBodyFactory.create(json);

        //Create Document
        try{
            DocumentRevision client = datastore.createDocumentFromRevision(rev);
            launchRingDialog();
        } catch(IOException e){
            Log.e("ERROR", "Error in creating document");
            e.printStackTrace();
        }

    }

 **/

    private void showSuccessDialog(){
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();

        // Setting Dialog Title
        alertDialog.setTitle("Success..");

        // Setting Dialog Message
        alertDialog.setMessage("Video Uploaded  Successful..");

        // Setting Icon to Dialog
        alertDialog.setIcon(android.R.drawable.ic_dialog_info);
        // Setting OK Button
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Write your code here to execute after dialog closed
               Intent intent = new Intent(UploadActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }

    public void showToast(final String toast)
    {
        runOnUiThread(new Runnable() {
            public void run()
            {
                Toast.makeText(UploadActivity.this, toast, Toast.LENGTH_LONG).show();
            }
        });
    }


}
