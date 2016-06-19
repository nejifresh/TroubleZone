package com.nejitawo.choices;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.squareup.picasso.Picasso;

import java.util.List;

import static com.nejitawo.choices.R.id.imageView;
import static com.nejitawo.choices.R.id.start;

public class DetailsActivity extends AppCompatActivity {
private TextView txtDetails;
    private TextView txtMoreDetails;
    private ImageView imgMain;
    private Button btnNotDo;
    private Button btnDo;
    private TextView txtTitle;
    private TextView txtDuration;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        txtDetails = (TextView)findViewById(R.id.txtlastgist);
        txtMoreDetails = (TextView)findViewById(R.id.txtfurthgist);
        imgMain = (ImageView)findViewById(R.id.ImgMain);

        btnDo = (Button)findViewById(R.id.btnDo);
        btnNotDo = (Button)findViewById(R.id.btnNotDo);
txtTitle = (TextView)findViewById(R.id.userListItem);
        txtDuration = (TextView)findViewById(R.id.txtTiming);
        final GlobalClass globalVariable = (GlobalClass) getApplicationContext();

        txtDetails.setText(globalVariable.getSectionA());
        txtTitle.setText(globalVariable.getTitle());
        txtDuration.setText(globalVariable.getDuration());
        txtMoreDetails.setText(globalVariable.getSectionB());

        Picasso.with(this)
                .load(globalVariable.getImageURL())
                .placeholder(R.mipmap.ic_launcher)// optional
                .resize(150, 150)
                .centerCrop()
                .error(R.mipmap.ic_launcher)
                .into(imgMain);


        getSupportActionBar().setTitle(globalVariable.getMainTitle());
btnDo.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        //Choose to do
        markAsDo();
        Intent intent = new Intent(DetailsActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
});


        btnNotDo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //choose not to do
wontDo();
                Intent intent = new Intent(DetailsActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void markAsDo(){
        final GlobalClass globalVariable = (GlobalClass) getApplicationContext();
        //Now update the record
        ParseQuery<ParseObject> doquery = new ParseQuery<ParseObject>("Tasks");
        doquery.whereEqualTo("title",globalVariable.getTitle());
        doquery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e==null && objects.size()>0){
                    for (ParseObject ob: objects){
                        ob.put("Status","INPROGRESS");
                        ob.saveInBackground();

                    }

                }
            }
        });

    }

    private void wontDo(){
        final GlobalClass globalVariable = (GlobalClass) getApplicationContext();
        //Now update the record
        ParseQuery<ParseObject> doquery = new ParseQuery<ParseObject>("Tasks");
        doquery.whereEqualTo("title",globalVariable.getTitle());
        doquery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e==null && objects.size()>0){
                    for (ParseObject ob: objects){
                        ob.put("Status","REJECTED");
                        ob.deleteInBackground();
                        //delete this from list of tasks for this user
                    }
                }
            }
        });

    }
}
