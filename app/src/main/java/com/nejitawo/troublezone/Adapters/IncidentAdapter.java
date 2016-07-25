package com.nejitawo.troublezone.Adapters;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.nejitawo.troublezone.Model.Events;
import com.nejitawo.troublezone.R;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * Created by Neji on 19/06/2016.
 */
public class IncidentAdapter extends BaseAdapter {
    final Context context;
    final List<Events> eventsList;

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

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.event_list_item, parent, false);
        }

        TextView title = (TextView) convertView.findViewById(R.id.userListItem);
       TextView description = (TextView) convertView.findViewById(R.id.txtlastgist);
       TextView duration = (TextView) convertView.findViewById(R.id.txtTiming);
         CircleImageView imageView = (CircleImageView) convertView.findViewById(R.id.choice_image);
TextView txtShowNew = (TextView) convertView.findViewById(R.id.txtNew);

        Events t = this.eventsList.get(position);
        if(!t.equals(null) ) {
if ((t.getDistanceFrom()/1000 <= 2.0 )) {
                title.setText(toCamelCase(t.getEventType()));
                description.setText(t.getDistanceAway() + " in " + t.getLocality());
                duration.setText("On " + DateFormat.getDateInstance(DateFormat.LONG).format(t.getPostedDate()));
                long timeInMilliseconds = t.getPostedDate().getTime();
                long now = Calendar.getInstance().getTimeInMillis();
                duration.setText(String.valueOf(DateUtils.getRelativeDateTimeString
                        (context, timeInMilliseconds, DateUtils.SECOND_IN_MILLIS, DateUtils.WEEK_IN_MILLIS, 1)));

                if (t.getSeenstatus().equals("NEW")) {
                    txtShowNew.setVisibility(View.VISIBLE);
                } else {
                    txtShowNew.setVisibility(View.GONE);
                }

           /* Picasso.with(context)
                    .load(t.getMainimage())
                    .placeholder(R.mipmap.ic_launcher)// optional
                    .resize(150, 150)
                    .centerCrop()
                    .error(R.mipmap.ic_launcher) // optional
                    .into(imageView);*/
                switch (t.getEventType()) {
                    case "Armed Robbery":
                        imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.robbery));

                        break;
                    case "Fire Incident":
                        imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.explosion));

                        break;
                    case "Road Accident":
                        imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.accidentcar));

                        break;
                    case "Kidnapping":
                        imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.kidnap));

                        break;
                    case "Medical Emergency":
                        imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.ambulance));

                        break;
                    case "Domestic Violence":
                        imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.domestic));

                        break;
                    case "Sexual Harrassment":
                        imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.harrass));

                        break;

                }
            }
            notifyDataSetChanged();
            return convertView;
        } else {
            return  null;
        }




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

}
