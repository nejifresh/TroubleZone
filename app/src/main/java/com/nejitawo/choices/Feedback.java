package com.nejitawo.choices;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Feedback extends AppCompatActivity {
private Button btnOnceMore;
    private Button btnShareMood;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.feedback);
        btnOnceMore = (Button)findViewById(R.id.btnOnceMore);
        btnShareMood = (Button)findViewById(R.id.btnShareMood);

        btnOnceMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Feedback.this,MainActivity.class);
                intent.putExtra("gotomychoices",1);
                startActivity(intent);
                finish();
            }
        });

        btnShareMood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//Open share interface
                Intent intent = new Intent(Feedback.this,ShareMood.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
