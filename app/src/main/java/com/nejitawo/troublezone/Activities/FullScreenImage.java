package com.nejitawo.troublezone.Activities;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.nejitawo.troublezone.GlobalClass;
import com.nejitawo.troublezone.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

public class FullScreenImage extends AppCompatActivity{
    ImageView imgDisplay;
    Button btnClose;
    CircularProgressView progressView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen);
        final GlobalClass globalVariable = (GlobalClass)getApplicationContext();
        progressView = (CircularProgressView) findViewById(R.id.progress_view);

        imgDisplay = (ImageView) findViewById(R.id.imgDisplay);

    // Bundle getImageBitmap = getIntent().getExtras();
     //   Bitmap bmp = (Bitmap) getImageBitmap.getParcelable("image");
       // imgDisplay.setImageBitmap(bmp);
        getSupportActionBar().hide();
        progressView.startAnimation();
        btnClose = (Button) findViewById(R.id.dummy_button);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FullScreenImage.this.finish();
            }
        });

        Picasso.with(this).load(globalVariable.getImageURL()).into(imgDisplay, new Callback() {
            @Override
            public void onSuccess() {
                progressView.stopAnimation();
                progressView.setVisibility(View.GONE);
            }

            @Override
            public void onError() {
               Toast.makeText(FullScreenImage.this,
                       "Error occured, pls check your connection", Toast.LENGTH_LONG).show();
            }
        });


    }


}
