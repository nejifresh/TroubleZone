package com.nejitawo.troublezone.Activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Config;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.rahatarmanahmed.cpv.CircularProgressView;

import com.nejitawo.troublezone.GlobalClass;
import com.nejitawo.troublezone.Imageutil.AndroidMultiPartEntity;
import com.nejitawo.troublezone.Imageutil.Constants;
import com.nejitawo.troublezone.Imageutil.ImageCropActivity;
import com.nejitawo.troublezone.Imageutil.OwnConfig;
import com.nejitawo.troublezone.Imageutil.PicModeSelectDialogFragment;
import com.nejitawo.troublezone.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

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
import java.util.List;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;




public class SignUp extends AppCompatActivity implements PicModeSelectDialogFragment.IPicModeSelectListener {
    TextView username;
    TextView password;
    TextView phone;
    TextView referee;
    TextView email;
    TextView pass2;
    private Toolbar toolbar;
    private EditText inputName, inputEmail, inputPassword;
    private TextInputLayout inputLayoutName, inputLayoutEmail, inputLayoutPassword;
    private Button btnSignUp;
    private static final String TAG = SignUp.class.getSimpleName();
    public static final String MyPREFERENCES = "MyPrefs";
    public static final String Agree = "nameKey";
    SharedPreferences sharedpreferences;
    public static final String TEMP_PHOTO_FILE_NAME = "temp_photo.jpg";

    private  int activePicButton;
    public static final int REQUEST_CODE_UPDATE_PIC = 0x1;
    private CircleImageView imgProfile;
    String code = "";
    String fileName = "";
    String FilePath = "";
    long totalSize = 0;
    private int chooserType;
    private ProgressBar progressBar;
    private TextView txtPercentage;
    private Button btnProfilePic;
    CircularProgressView progressView;
    private EditText txtPhone;
    final  ParseUser currentUser = ParseUser.getCurrentUser();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        imgProfile = (CircleImageView) findViewById(R.id.profile_image);
     btnProfilePic = (Button)findViewById(R.id.btnprofilepic);
        progressView = (CircularProgressView) findViewById(R.id.progress_view);
        txtPhone = (EditText)findViewById(R.id.inputPhone);

        if (sharedpreferences.contains(Agree)) {


            email = (TextView) findViewById(R.id.input_email);
            password = (TextView) findViewById(R.id.input_password);
          //  phone = (TextView) findViewById(R.id.txtphone);
            username = (TextView) findViewById(R.id.input_name);
            pass2 = (TextView) findViewById(R.id.input_password);




        }else{
            Intent intent = new Intent(SignUp.this,TermsActivity.class);
            startActivity(intent);
            finish();
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
       // setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        inputLayoutName = (TextInputLayout) findViewById(R.id.input_layout_name);
        inputLayoutEmail = (TextInputLayout) findViewById(R.id.input_layout_email);
        inputLayoutPassword = (TextInputLayout) findViewById(R.id.input_layout_password);
        inputName = (EditText) findViewById(R.id.input_name);
        inputEmail = (EditText) findViewById(R.id.input_email);
        inputPassword = (EditText) findViewById(R.id.input_password);
        btnSignUp = (Button) findViewById(R.id.btn_signup);

        inputName.addTextChangedListener(new MyTextWatcher(inputName));
        inputEmail.addTextChangedListener(new MyTextWatcher(inputEmail));
        inputPassword.addTextChangedListener(new MyTextWatcher(inputPassword));
        btnProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activePicButton = REQUEST_CODE_UPDATE_PIC;
                showAddProfilePicDialog();
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitForm();
            }
        });
    }





    public void signUpOnClickListener(){



        if (!txtPhone.getText().toString().isEmpty()){

        }else {
            txtPhone.setError("Mobile Number required");
            return;
        }



        if (!email.getText().toString().isEmpty()){



        }else{
            email.setError("Enter your email");
            return;
        }

        if (!password.getText().toString().isEmpty()){
            if (pass2.getText().toString().equals(password.getText().toString())){


            } else{
                password.setError("Error - Passwords don't match");
                return;
            }

        }else{
            password.setError("Enter a password");
            return;
        }





        if (!username.getText().toString().isEmpty()){

        }else{
            username.setError("Enter a Username");
            return;
        }

        if (!FilePath.isEmpty() ) {
            code = getRandomString(4);

            new UploadFileToServer().execute();
        } else{
            //throw error message
            showAlert("Pls Provide Profile Image");
            return;
        }




    }

    private class UploadFileToServer extends AsyncTask<Void, Integer, String> {
        @Override
        protected void onPreExecute() {
            // setting progress bar to zero
          //  progressBar.setProgress(0);
            progressView.setVisibility(View.VISIBLE);

            progressView.startAnimation();
getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            // Making progress bar visible
         //   progressBar.setVisibility(View.VISIBLE);

            // updating progress bar value
         //   progressBar.setProgress(progress[0]);

            // updating percentage value
         //   txtPercentage.setText(String.valueOf(progress[0]) + "%");
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
            if (result == "Success"){
                //  createdoc(); //uplaod second image
                Toast.makeText(getApplicationContext(),"Uploading profile picture",Toast.LENGTH_LONG).show();
               createDoc();
            } else {
                progressView.stopAnimation();
                progressView.setVisibility(View.INVISIBLE);
                showAlert(result);
            }
            // showing the server response in an alert dialog


            super.onPostExecute(result);
        }

    }


    private void showAlert(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(SignUp.this);
        builder.setMessage(message).setTitle("Alert")
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

    private void createDoc(){

        ParseUser user = new ParseUser();
        user.setEmail(email.getText().toString());
        user.setUsername(username.getText().toString().toLowerCase());
        user.setPassword(password.getText().toString());
user.put("Image","http://108.60.209.155:8080/AndroidFileUpload/uploads/" + fileName);
        user.put("phone",txtPhone.getText().toString());
        user.put("code",code);
        user.put("verified","NO");
        user.put("homezoned","NO");
        user.put("codesent","NO"); //When app sends the code, change this to yes

        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    // Hooray! Let them use the app now.
                    final GlobalClass globalVariable = (GlobalClass)getApplicationContext();
                    progressView.stopAnimation();
                    progressView.setVisibility(View.INVISIBLE);
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    ParseObject saveSender = new ParseObject("SentCodes");
                    saveSender.put("user", ParseUser.getCurrentUser().getUsername());
                    saveSender.put("code",code);
                    saveSender.put("codesent","NO");
                    saveSender.put("phone",txtPhone.getText().toString());
                    saveSender.put("email",email.getText().toString());
                   // saveSender.put("mailsent","NO"); Mail only sent as a resend
                    saveSender.saveInBackground();
                    showSuccessDialog();
                } else {
                    // Sign up didn't succeed. Look at the ParseException
                    // to figure out what went wrong
                    progressView.stopAnimation();
                    progressView.setVisibility(View.INVISIBLE);
                    switch (e.getCode()){
                        case (ParseException.USERNAME_TAKEN) :{
//Username is taken
                            showFailDialog("Username is already taken");


                        }

                        case(ParseException.EMAIL_TAKEN):{
//Email is taken
                            showFailDialog("Email is already taken");

                        }
                        default:{
                            //Some other error
                            showFailDialog("An error occured, please check your connection or try again");

                        }
                    }

                    e.printStackTrace();
                }
            }
        });





    }


    private void showSuccessDialog(){
        AlertDialog alertDialog = new AlertDialog.Builder(SignUp.this).create();

        // Setting Dialog Title
        alertDialog.setTitle("Success..");

        // Setting Dialog Message
        alertDialog.setMessage("Registration successful..");

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
                Intent i = new Intent(SignUp.this, VerifyActivity.class);
                startActivity(i);
                finish();
            }
        });

        // Showing Alert Message
        alertDialog.show();
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
    }

    private void showCroppedImage(String mImagePath) {
        if (mImagePath != null) {
            Bitmap myBitmap = BitmapFactory.decodeFile(mImagePath);
            imgProfile.setImageBitmap(myBitmap);
            //this sets things up for upload by ASYNC Task
            try {
                FilePath = compressForSaveImage(mImagePath, 2);
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
                    (original.getParent() + File.separator + username.getText().toString() + "secuser_" + identifier
                            + "_image1.jpg"));

            fileName =  username.getText().toString() + "secuser_" + identifier + "_image1.jpg";




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
            Bitmap b3 = Bitmap.createScaledBitmap(bitmap, 800, 800, true);
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


    private void showFailDialog(String message){
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();

        // Setting Dialog Title
        alertDialog.setTitle("Error..");

        // Setting Dialog Message
        alertDialog.setMessage(message);

        // Setting Icon to Dialog
        alertDialog.setIcon(android.R.drawable.ic_dialog_info);
        // Setting OK Button
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Write your code here to execute after dialog closed
                // Toast.makeText(getApplicationContext(),"New Location created", Toast.LENGTH_SHORT).show();

            }
        });

        // Showing Alert Message
        alertDialog.show();
    }


    private static String getRandomString(final int sizeOfRandomString)
    {
        final Random random=new Random();
        final StringBuilder sb=new StringBuilder(sizeOfRandomString);
        for(int i=0;i<sizeOfRandomString;++i)
            sb.append(ALLOWED_CHARACTERS.charAt(random.nextInt(ALLOWED_CHARACTERS.length())));
        return sb.toString();
    }

    /**
     * Validating form
     */
    private void submitForm() {
        if (!validateName()) {
            return;
        }

        if (!validateEmail()) {
            return;
        }

        if (!validatePassword()) {
            return;
        }

        signUpOnClickListener();
    }

    private boolean validateName() {
       inputName.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        if (inputName.getText().toString().trim().isEmpty()) {
            inputLayoutName.setError(getString(R.string.err_msg_name));
//            inputName.setCompoundDrawablesWithIntrinsicBounds(0, 0,R.drawable.cancelbutton , 0);
            requestFocus(inputName);
            return false;
        } else {
            inputLayoutName.setErrorEnabled(false);
            final ParseQuery<ParseUser> usernameQuery = ParseUser.getQuery();
            usernameQuery.whereEqualTo("username", inputName.getText().toString());
            usernameQuery.findInBackground(new FindCallback<ParseUser>() {
                @Override
                public void done(List<ParseUser> objects, ParseException e) {
                    if (e==null){
                        if (objects.size()==0){
                            //No user with this name exists
                            //The order of params corresponding to the drawable location is: left, top, right, bottom
//                            inputName.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.okbutton, 0);
                        }else{
                            //User with this name exists
//                            inputName.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.cancelbutton, 0);
                            inputLayoutName.setErrorEnabled(true);
                            inputLayoutName.setError("Username is taken");
                            return;
                        }
                    }
                }
            });
        }

        return true;
    }

    private boolean validateEmail() {
        inputEmail.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);

        String email = inputEmail.getText().toString().trim();

        if (email.isEmpty() || !isValidEmail(email)) {
            inputLayoutEmail.setError(getString(R.string.err_msg_email));
//            inputEmail.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.cancelbutton, 0);
            requestFocus(inputEmail);
            return false;
        } else {
            inputLayoutEmail.setErrorEnabled(false);
            final ParseQuery<ParseUser> usernameQuery = ParseUser.getQuery();
            usernameQuery.whereEqualTo("email", email);
            usernameQuery.findInBackground(new FindCallback<ParseUser>() {
                @Override
                public void done(List<ParseUser> objects, ParseException e) {
                    if (e==null){
                        if (objects.size()==0){
                            //No user with this name exists
                            //The order of params corresponding to the drawable location is: left, top, right, bottom
                  //          inputEmail.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.okbutton, 0);
                        }else{
                            //User with this name exists
                    //        inputEmail.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.cancelbutton, 0);
                            inputLayoutEmail.setErrorEnabled(true);
                            inputLayoutEmail.setError("Email already exists");
                            return;
                        }
                    }
                }
            });
        }

        return true;
    }

    private boolean validatePassword() {
        if (inputPassword.getText().toString().trim().isEmpty()) {
            inputLayoutPassword.setError(getString(R.string.err_msg_password));
            requestFocus(inputPassword);
            return false;
        } else {
            inputLayoutPassword.setErrorEnabled(false);
        }

        return true;
    }


    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private class MyTextWatcher implements TextWatcher {

        private View view;

        private MyTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.input_name:
                    validateName();
                    break;
                case R.id.input_email:
                    validateEmail();
                    break;
                case R.id.input_password:
                    validatePassword();
                    break;
            }
        }
    }




    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private static final String ALLOWED_CHARACTERS ="0123456789";

    private static final String mALLOWED_CHARACTERS ="0123456789qwertyuiopasdfghjklzxcvbnm";

}
