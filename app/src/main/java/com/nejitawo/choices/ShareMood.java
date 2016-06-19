package com.nejitawo.choices;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.parse.ParseObject;

public class ShareMood extends AppCompatActivity {
private EditText txtmood;
    private Button btnShare;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sharemood);
        txtmood = (EditText)findViewById(R.id.txtmood);
        btnShare = (Button)findViewById(R.id.btnShareMood);
        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (txtmood.getText().toString().isEmpty()) {
                txtmood.setError("Enter how you feel");
                    return;
                }
                ParseObject moodshare = new ParseObject("Moods");
                moodshare.put("Feeling",txtmood.getText().toString());
                moodshare.saveInBackground();
                showAlert("Thank you for sharing how you feel");
            }
        });
    }

    private void showAlert(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ShareMood.this);
        builder.setMessage(message).setTitle("Application Response")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(ShareMood.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
}
