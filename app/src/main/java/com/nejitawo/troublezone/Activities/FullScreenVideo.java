package com.nejitawo.troublezone.Activities;

import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.VideoView;

import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.nejitawo.troublezone.GlobalClass;
import com.nejitawo.troublezone.R;

public class FullScreenVideo extends AppCompatActivity {
    VideoView imgDisplay;
    Button btnClose;
    CircularProgressView progressView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video_full);
        final GlobalClass globalVariable = (GlobalClass)getApplicationContext();

        imgDisplay = (VideoView) findViewById(R.id.imgDisplay);
        progressView = (CircularProgressView) findViewById(R.id.progress_view);

      //  progressView.setVisibility(View.VISIBLE);
        progressView.startAnimation();
        //SETUP VIDEO VIEW
        try{
            MediaController mediaController = new MediaController(this);
            mediaController.setAnchorView(imgDisplay);
            Uri videoUri = Uri.parse(globalVariable.getVideoclip());
           imgDisplay.setMediaController(mediaController);
           imgDisplay.setVideoURI(videoUri);



        }catch (Exception e){
            Log.e("error",e.getMessage());
        }

      imgDisplay.requestFocus();
        imgDisplay.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            // video is now ready to play
            public void onPrepared(MediaPlayer mp) {
                progressView.setVisibility(View.GONE);
                progressView.stopAnimation();
               imgDisplay.start();

            }
        });

       imgDisplay.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
            //   finish();
            }
        });

       // imgDisplay.setImageBitmap(bmp);
        getSupportActionBar().hide();
        btnClose = (Button) findViewById(R.id.dummy_button);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FullScreenVideo.this.finish();
            }
        });
    }
}
