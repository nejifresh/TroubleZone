package com.nejitawo.choices.Fragments;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.nejitawo.choices.Adapters.ListAdapter;
import com.nejitawo.choices.DetailsActivity;
import com.nejitawo.choices.GlobalClass;
import com.nejitawo.choices.Model.Choices;
import com.nejitawo.choices.R;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class Recommended extends Fragment {

private ListView choiceList;
    private CircularProgressView progressView;
    List<ParseObject> ob;
LinearLayout lvContent;
    public Recommended() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView =  inflater.inflate(R.layout.fragment_recommended, container, false);
        choiceList = (ListView)rootView.findViewById(R.id.recommendedList);
        progressView = (CircularProgressView)rootView. findViewById(R.id.progress_view);
lvContent = (LinearLayout)rootView.findViewById(R.id.visibleContent);
        //Now load data
        new loadData().execute();

                return rootView;
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

             ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(
             "Tasks");
             query.orderByDescending("_created_at");
             query.whereEqualTo("Status", "New");



            try{
                ob = query.find();

            }catch (Exception ex){
                ex.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            List<Choices> myChoice = new ArrayList<Choices>();
            if (ob.size()==0){
                lvContent.setVisibility(View.VISIBLE);
            }

            for (ParseObject choice : ob) {

                   // lvContent.setVisibility(View.GONE); //Dont show background content
                    try {
                        Choices t = Choices.giveFullDetails(choice, getActivity().getApplicationContext());
                        if (t != null) {
                            myChoice.add(t);
                        }
                        setupListView(myChoice);

                    } catch (Exception e) {
                        e.printStackTrace();
                        //Log.e("error",e.getMessage().toString());
                    }



            }

            progressView.stopAnimation();
            progressView.setVisibility(View.INVISIBLE);

        }
    }

    public void setupListView(final List theChoices)  {
        getActivity().runOnUiThread(new Runnable() {
            public void run() {
                List<Choices> mChoices = theChoices;
               ListAdapter adapter = new ListAdapter(getActivity().getApplicationContext(), mChoices);
                //listView1.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                choiceList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                        progressView.setVisibility(View.VISIBLE);
                        progressView.startAnimation();
                        Choices thisChoice = (Choices) (adapterView.getItemAtPosition(position));
                        // Toast.makeText(getActivity(), "Touched record", Toast.LENGTH_LONG).show();
                        final GlobalClass globalVariable = (GlobalClass) getActivity().getApplicationContext();
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
                         progressView.stopAnimation();
                        progressView.setVisibility(View.INVISIBLE);
                        startActivity(intent);



                    }
                });
                choiceList.setAdapter(adapter);

            }
        });


    }


}
