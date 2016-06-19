package com.nejitawo.choices.Fragments;


import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.nejitawo.choices.Adapters.CardsAdapter;
import com.nejitawo.choices.Adapters.MyChoiceAdapter;
import com.nejitawo.choices.DetailsActivity;
import com.nejitawo.choices.Feedback;
import com.nejitawo.choices.GlobalClass;
import com.nejitawo.choices.Model.Choices;
import com.nejitawo.choices.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


public class ChoiceFragment extends Fragment {
    private RecyclerView mRecyclerView;
    private StaggeredGridLayoutManager mStaggeredGridLayoutManager;
    private MyChoiceAdapter mAdapter;
    private CircularProgressView progressView;
    List<ParseObject> ob;
    List<Choices> myList = new LinkedList<Choices>();
    private ListView choiceList;
    LinearLayout lvContent;
    public ChoiceFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.frag_mychoice, container, false);
        choiceList = (ListView) rootView.findViewById(R.id.mychoiceList);
        lvContent = (LinearLayout)rootView.findViewById(R.id.visibleContent);
        progressView = (CircularProgressView) rootView.findViewById(R.id.progress_view);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.my_recycler_view);
        mStaggeredGridLayoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mStaggeredGridLayoutManager);
        mRecyclerView.setAdapter(null);
        ob = null;
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

            //This combines two queries to show completed and inprogress tasks for the user


            ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Tasks");
            query.whereNotEqualTo("Status","New");


            query.orderByDescending("_created_at");
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
                lvContent.setVisibility(View.VISIBLE);
            }
                myList.clear();
                try {
                    for (ParseObject choices : ob) {

                        Choices t = Choices.giveFullDetails(choices, getActivity().getApplicationContext());
                        if (t != null) {
                            myList.add(t);
                        }

                        // mAdapter.notifyDataSetChanged();

                    }
                } catch (Exception ex) {
                     ex.printStackTrace();
                }

                mAdapter = new MyChoiceAdapter(getActivity(), myList);
                mAdapter.setOnItemClickListener(onItemClickListener);

                mRecyclerView.setAdapter(mAdapter);

                progressView.stopAnimation();
                progressView.setVisibility(View.INVISIBLE);


        }

        public void setupListView(final List theChoices) {
            getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    List<Choices> mChoices = theChoices;
                    CardsAdapter adapter = new CardsAdapter(getActivity().getApplicationContext(), mChoices);
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

    MyChoiceAdapter.OnItemClickListener onItemClickListener = new MyChoiceAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(View view, int position) {

            Choices thisChoice = (myList.get(position));
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

if (globalVariable.getStatus().equals("INPROGRESS")){
    Intent intent = new Intent(getActivity(), Feedback.class); //Go to instructions page
    //Mark task as done
    ParseQuery<ParseObject> doquery = new ParseQuery<ParseObject>("Tasks");
    doquery.whereEqualTo("title",globalVariable.getTitle());
    doquery.findInBackground(new FindCallback<ParseObject>() {
        @Override
        public void done(List<ParseObject> objects, ParseException e) {
            if (e==null && objects.size()>0){
                for (ParseObject ob: objects){
                    ob.put("Status","DONE");
                    ob.saveInBackground();

                }

            }
        }
    });
    startActivity(intent);
} else{
    Intent intent = new Intent(getActivity(), DetailsActivity.class);
    startActivity(intent);
}


            progressView.stopAnimation();
            progressView.setVisibility(View.INVISIBLE);



        }
    };
}
