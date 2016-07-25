package com.nejitawo.troublezone.Activities;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.nejitawo.troublezone.GlobalClass;
import com.nejitawo.troublezone.R;

public class FullScreenImage extends AppCompatActivity {
    ImageView imgDisplay;
    Button btnClose;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen);
        final GlobalClass globalVariable = (GlobalClass)getApplicationContext();

        imgDisplay = (ImageView) findViewById(R.id.imgDisplay);
     Bundle getImageBitmap = getIntent().getExtras();
        Bitmap bmp = (Bitmap) getImageBitmap.getParcelable("image");
        imgDisplay.setImageBitmap(bmp);
        getSupportActionBar().hide();
        btnClose = (Button) findViewById(R.id.dummy_button);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FullScreenImage.this.finish();
            }
        });
    }
}
