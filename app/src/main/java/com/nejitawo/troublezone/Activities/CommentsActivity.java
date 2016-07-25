package com.nejitawo.troublezone.Activities;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.nejitawo.troublezone.Adapters.CommentAdapter;
import com.nejitawo.troublezone.GlobalClass;
import com.nejitawo.troublezone.Model.Comments;
import com.nejitawo.troublezone.R;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class CommentsActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private StaggeredGridLayoutManager mStaggeredGridLayoutManager;
    private CommentAdapter mAdapter;
    private CircularProgressView progressView;
    List<ParseObject> ob;
    List<Comments> myList = new LinkedList<Comments>();
    private ImageView sendButton;
    private EditText txtComments;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.comments);
        txtComments = (EditText)findViewById(R.id.commenttext);
        sendButton = (ImageView)findViewById(R.id.send) ;
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                postComment();
            }
        });
        progressView = (CircularProgressView) findViewById(R.id.progress_view);
        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        mStaggeredGridLayoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mStaggeredGridLayoutManager);
        mRecyclerView.setAdapter(null);
        ob = null;
        //Now load data
        new loadData().execute();
    }

    public class loadData extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressView.setVisibility(View.VISIBLE);
            progressView.startAnimation();


        }

        @Override
        protected Void doInBackground(Void... params) {

            //This combines two queries to show completed and inprogress tasks for the user
final GlobalClass globalVariable = (GlobalClass)getApplicationContext();

            ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Comments");
           query.whereEqualTo("incidentid",globalVariable.getIncidentID());


            // query.orderByDescending("_created_at");
            try {
                ob = query.find();

            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (ob.size()==0){
               // lvContent.setVisibility(View.VISIBLE);
            }
            myList.clear();
            try {
                for (ParseObject choices : ob) {

                    Comments t = Comments.giveFullDetails(choices, getApplicationContext());
                    if (t != null) {
                        myList.add(t);
                    }else{

                    }

                //    Toast.makeText(getApplicationContext(),String.valueOf(myList.size()),Toast.LENGTH_LONG).show();

                    // mAdapter.notifyDataSetChanged();

                }
            } catch (Exception ex) {
                ex.printStackTrace();
                Toast.makeText(getApplicationContext(),"error in list",Toast.LENGTH_LONG).show();
                Log.e("terror", "Error occured - " + ex.getMessage());

            }

            mAdapter = new CommentAdapter (getApplicationContext(), myList);
            mAdapter.setOnItemClickListener(onItemClickListener);

            mRecyclerView.setAdapter(mAdapter);

            progressView.stopAnimation();
            progressView.setVisibility(View.INVISIBLE);


        }


    }

    CommentAdapter.OnItemClickListener onItemClickListener = new CommentAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(View view, int position) {
//
            Comments thisComment = (myList.get(position));
            final GlobalClass globalVariable = (GlobalClass)getApplicationContext();
            /**
             globalVariable.setTitle(thisChoice.getTitle());
             globalVariable.setDescription(thisChoice.getDescription());
             globalVariable.setImageURL(thisChoice.getImageURL());
             globalVariable.setDuration(thisChoice.getDuration());
             globalVariable.setMainTitle(thisChoice.getMainTitle());
             globalVariable.setSectionA(thisChoice.getSectionA());
             globalVariable.setSectionB(thisChoice.getSectionB());
             globalVariable.setStatus(thisChoice.getStatus());
             globalVariable.setId(thisChoice.getId());
             Intent intent = new Intent(getActivity(), DetailsActivity.class);
             startActivity(intent);


             **/


            progressView.stopAnimation();
            progressView.setVisibility(View.INVISIBLE);



        }
    };

    private void postComment(){
        if (txtComments.getText().toString().isEmpty()){
            txtComments.setError("Enter your comment");
            return;
        } else {
            final GlobalClass globalVariable = (GlobalClass)getApplicationContext();
               ParseObject newComments = new ParseObject("Comments");
            newComments.put("username", ParseUser.getCurrentUser().getUsername());
            newComments.put("userimage",ParseUser.getCurrentUser().get("Image"));
            Calendar c = Calendar.getInstance();
            Date today = c.getTime();
            newComments.put("posteddate",today);
            newComments.put("location","userLocationhere");
            newComments.put("comments", txtComments.getText().toString());
            newComments.put("incidentid",globalVariable.getIncidentID());
            newComments.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e ==null){
                        //comment posted successfully so show in listview
txtComments.setText("");
                        //myList.clear();
                        new loadData().execute();
                        mRecyclerView.smoothScrollToPosition(mRecyclerView.getAdapter().getItemCount());


                    }
                }
            });
        }
    }
}
