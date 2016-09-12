package com.nejitawo.troublezone.Adapters;

import android.content.Context;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.nejitawo.troublezone.Model.Events;
import com.nejitawo.troublezone.R;
import com.parse.CountCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * Created by Neji on 19/06/2016.
 */
public class IncidentAdapter extends BaseAdapter {
    final Context context;
    final List<Events> eventsList;
   // private int comments;
    private int comments;

    public IncidentAdapter(Context context, List<Events> eventses){
        this.context = context;
        this.eventsList = eventses;
    }
public Events giveItemPosition(int position){
    return this.eventsList.get(position);
}
    @Override
    public int getCount() {
        return this.eventsList.size();
    }

    @Override
    public Object getItem(int position) {
        return this.eventsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

  public  static class ViewHolder {
        TextView title = null;
        TextView description = null;
        TextView duration = null;
        CircleImageView imageView;
        TextView txtShowNew = null;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder vh = null;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.event_list_item, parent, false);
vh = new ViewHolder();
         vh.   title = (TextView) convertView.findViewById(R.id.userListItem);
         vh.   description = (TextView) convertView.findViewById(R.id.txtlastgist);
        vh.    duration = (TextView) convertView.findViewById(R.id.txtTiming);
          vh.  imageView = (CircleImageView) convertView.findViewById(R.id.choice_image);
        vh.    txtShowNew = (TextView) convertView.findViewById(R.id.txtNew);
          convertView.setTag(vh);

       } else{

vh = (ViewHolder)convertView.getTag();
        }
//comments = 0;

            Events t = this.eventsList.get(position);
          //  int comments = 0;
        ParseQuery<ParseObject> totalComments = new ParseQuery<ParseObject>("Comments");
        totalComments.whereEqualTo("incidentid", t.getId());
        totalComments.countInBackground(new CountCallback() {
            @Override
            public void done(int count, ParseException e) {
                if (e == null && count>0){
                 //   vh.txtShowNew.setText(String.valueOf(count)  );
                 //   comments = count;
                 //   setCount(count);
                    setComments(count);
                    Log.e("counter",String.valueOf(count));

                }



            }
        });

            if (!t.equals(null)) {
                if ((t.getDistanceFrom() / 1000 <= 2.0)) {
                vh.    title.setText(toCamelCase(t.getEventType()));
                    vh.      description.setText(t.getDistanceAway() + " in " + t.getLocality());
                    vh.      duration.setText("On " + DateFormat.getDateInstance(DateFormat.LONG).format(t.getPostedDate()));
                    long timeInMilliseconds = t.getPostedDate().getTime();

                    Date now = Calendar.getInstance().getTime();

                    long mills = now.getTime() - t.getPostedDate().getTime();
                    String diff = String.valueOf  ((mills)/(1000 * 60 * 60));
                    vh.        duration.setText(String.valueOf(DateUtils.getRelativeDateTimeString
                            (context, timeInMilliseconds, DateUtils.SECOND_IN_MILLIS, DateUtils.WEEK_IN_MILLIS, 1)));
                    vh.txtShowNew.setText(String.valueOf(getComments()));

                /*    if (t.getSeenstatus().equals("NEW")) {
                        vh.          txtShowNew.setVisibility(View.VISIBLE);
                    } else {
                        vh.     txtShowNew.setVisibility(View.GONE);
                    }*/




                    comments = 0;
           /* Picasso.with(context)
                    .load(t.getMainimage())
                    .placeholder(R.mipmap.ic_launcher)// optional
                    .resize(150, 150)
                    .centerCrop()
                    .error(R.mipmap.ic_launcher) // optional
                    .into(imageView);*/
                    switch (t.getEventType()) {
                        case "Armed Robbery":
                            vh.   imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.robbery));

                            break;
                        case "Fire Incident":
                            vh.     imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.explosion));

                            break;
                        case "Road Accident":
                            vh.    imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.accidentcar));

                            break;
                        case "Kidnapping":
                            vh.        imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.kidnap));

                            break;
                        case "Medical Emergency":
                            vh.     imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.ambulance));

                            break;
                        case "Domestic Violence":
                            vh.       imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.domestic));

                            break;
                        case "Sexual Harrassment":
                            vh.     imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.harrass));

                            break;

                        case "Civil Unrest":
                            vh.     imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.unrest));

                            break;

                        case "Murder Incident":
                            vh.     imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.homi));

                            break;

                      default:
                            vh.     imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.high));

                            break;
                    }
                    notifyDataSetChanged();


                } else {
                    // notifyDataSetChanged();
                    // return null;
                   // return convertView;
                }


            } else {
                return null;
            }

        return convertView;
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

    private String formatDecimal(Integer number){
        DecimalFormat decimalFormat = new DecimalFormat("#,###,###");
        return decimalFormat.format(number);
    }

    public int getComments() {
        return comments;
    }

    public void setComments(int comments) {
        this.comments = comments;
    }
}
