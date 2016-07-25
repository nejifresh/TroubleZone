package com.nejitawo.troublezone.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.devspark.robototextview.widget.RobotoTextView;
import com.nejitawo.troublezone.Activities.CommentsActivity;
import com.nejitawo.troublezone.Activities.FullScreenImage;
import com.nejitawo.troublezone.GlobalClass;
import com.nejitawo.troublezone.Model.Events;
import com.nejitawo.troublezone.R;
import com.parse.CountCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * Created by Neji on 10/02/2016.
 */
public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.ViewHolder> {
    Context mContext;
    final List<Events> eventsList;
    OnItemClickListener mItemClickListener;
    Bitmap[] theBitmap;

    public EventsAdapter(Context context, List<Events> eventses){
        this.mContext = context;
        this.eventsList = eventses;
        theBitmap = new Bitmap[eventsList.size()];
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public CircleImageView profileImage;
        public ImageView image;
        public TextView username;
        public TextView place;
        public TextView text;
        public TextView like;
        public TextView follow;
        public TextView postedDate;





        public ViewHolder(View itemView){
            super(itemView);
            image = (ImageView)itemView
                    .findViewById(R.id.list_item_google_cards_social_image);
            profileImage = (CircleImageView) itemView
                    .findViewById(R.id.profile_image);
            username = (RobotoTextView) itemView
                    .findViewById(R.id.list_item_google_cards_social_name);
            place = (RobotoTextView) itemView
                    .findViewById(R.id.list_item_google_cards_social_place);

            postedDate = (RobotoTextView) itemView
                    .findViewById(R.id.list_item_google_cards_social_in);

            text = (RobotoTextView) itemView
                    .findViewById(R.id.list_item_google_cards_social_text);
            like = (RobotoTextView) itemView
                    .findViewById(R.id.list_item_google_cards_social_like);
            follow = (RobotoTextView) itemView
                    .findViewById(R.id.list_item_google_cards_social_follow);



image.setOnClickListener(this);
            like.setOnClickListener(this);
            follow.setOnClickListener(this);
        }
        @Override
        public void onClick(View view){
           // if(mItemClickListener!=null){
           //   mItemClickListener.onItemClick(itemView, getPosition());

                int possition = (Integer) view.getTag();
            final GlobalClass globalVariable = (GlobalClass) mContext.getApplicationContext();
            Events thisEvent = eventsList.get(possition);

            switch (view.getId()) {

                    case R.id.list_item_google_cards_social_follow:
                          globalVariable.setImageURL(thisEvent.getImage());
/*
                        globalVariable.setTitle(thisProperty.getTitle());
                        globalVariable.setPostedDate(thisProperty.getPostedDate());
                        globalVariable.setDescription(thisProperty.getDescription());
                        globalVariable.setLocality(thisProperty.getLocality());
                        globalVariable.setCity(thisProperty.getCity());
                        globalVariable.setCountry(thisProperty.getCountry());
                        globalVariable.setBedrooms(thisProperty.getBedrooms());
                        globalVariable.setBathrooms(thisProperty.getBathrooms());
                        globalVariable.setKitchens(thisProperty.getKitchens());
                        globalVariable.setToilets(thisProperty.getToilets());
                        globalVariable.setLivingroom(thisProperty.getLivingroom());
                        globalVariable.setPrice(thisProperty.getPrice());
                        globalVariable.setTenure(thisProperty.getTenure());
                        globalVariable.setMainimage(thisProperty.getMainimage());
                        globalVariable.setImage2(thisProperty.getImage2());
                        globalVariable.setImage3(thisProperty.getImage3());
                        globalVariable.setImage4(thisProperty.getImage4());
                        globalVariable.setImage5(thisProperty.getImage5());
                        globalVariable.setImage6(thisProperty.getImage6());
                        Intent intent = new Intent(mContext, PropDetails.class);
                        mContext.startActivity(intent);
                        // click on share button
                      //  Toast.makeText(view.getContext(), "Clicked on More -  " + possition, Toast.LENGTH_SHORT).show();
                        break;
                    */
                    case R.id.list_item_google_cards_social_like:
                        globalVariable.setIncidentID(thisEvent.getId());
                        Intent mintent = new Intent(mContext, CommentsActivity.class);
                        mContext.startActivity(mintent);
                        // click on share button
                     //   Toast.makeText(view.getContext(), "Clicked on Listen -  " + possition, Toast.LENGTH_SHORT).show();
                        break;

                    case R.id.list_item_google_cards_social_image:
                        //Show full screen
                        Intent intent = new Intent(mContext, FullScreenImage.class);
                        Bundle imageExtra = new Bundle();
                        imageExtra.putParcelable("image",theBitmap[possition]);
                        intent.putExtras(imageExtra);
                        mContext.startActivity(intent);
                        break;

           }
        }

    }


    public interface OnItemClickListener{
        void onItemClick(View view, int position);
    }
public void setOnItemClickListener(final OnItemClickListener mItemClickListener){
    this.mItemClickListener = mItemClickListener;
}

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_events,parent,false);

        return new ViewHolder(view) ;

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        Events t = this.eventsList.get(position);

        ParseQuery<ParseObject> totalComments = new ParseQuery<ParseObject>("Comments");
        totalComments.whereEqualTo("incidentid", t.getId());
       totalComments.countInBackground(new CountCallback() {
           @Override
           public void done(int count, ParseException e) {
               if (e == null && count>0){
                   holder.like.setText(String.valueOf(count) +  " Comments");
               }

           }
       });

        holder.username.setText("@" + t.getSenderName());
        holder.place.setText(t.getEventType() +  " In "+  String.valueOf(t.getLocality()) );
        holder.postedDate.setText("Posted On "+ DateFormat.getDateInstance(DateFormat.LONG).format (t.getPostedDate()) );
        long timeInMilliseconds = t.getPostedDate().getTime();
        holder.postedDate.setText(DateUtils.getRelativeDateTimeString
                (mContext, timeInMilliseconds,DateUtils.SECOND_IN_MILLIS,DateUtils.WEEK_IN_MILLIS,0) );


        holder.text.setText(toCamelCase(t.getDescription()));
        holder.image.setTag(position);
        holder.like.setTag(position);
        holder.follow.setTag(position);
      //  holder.like.setText(toCamelCase(t.getDistanceAway()));

        Picasso.with(mContext)
                .load(t.getUserImage())
                .placeholder(R.mipmap.ic_launcher)// optional
                .resize(150, 150)
                .centerCrop()

                .error(R.mipmap.ic_launcher) // optional
                .into(holder.profileImage);



        Picasso.with(mContext).load(t.getImage()).into(holder.image);
        Picasso.with(mContext).load(t.getImage()).into(new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                for (int i=0; i<eventsList.size() -1;i++){
                    theBitmap[i] = bitmap;
                }

            }
            @Override
            public void onPrepareLoad(Drawable drawable) {
            }

            @Override
            public void onBitmapFailed(Drawable drawable) {
            }
        });


    }

    @Override
    public int getItemCount() {
        return  this.eventsList.size();
    }


public void clearList() {
    if (eventsList.size() > 0) {
        this.eventsList.clear();
        notifyDataSetChanged();
    }
}

    private String formatDecimal(Integer number){
        DecimalFormat decimalFormat = new DecimalFormat("#,###,###");
      return decimalFormat.format(number);
    }

    public static String toCamelCase(String inputString) {
        String result = "";
        if (inputString.length() == 0) {
            return result;
        }
        char firstChar = inputString.charAt(0);
        char firstCharToUpperCase = Character.toUpperCase(firstChar);
        result = result + firstCharToUpperCase;
        for (int i = 1; i < inputString.length(); i++) {
            char currentChar = inputString.charAt(i);
            char previousChar = inputString.charAt(i - 1);
            if (previousChar == ' ') {
                char currentCharToUpperCase = Character.toUpperCase(currentChar);
                result = result + currentCharToUpperCase;
            } else {
                char currentCharToLowerCase = Character.toLowerCase(currentChar);
                result = result + currentCharToLowerCase;
            }
        }
        return result;
    }

}
