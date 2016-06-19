package com.nejitawo.choices.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nejitawo.choices.Model.Choices;
import com.nejitawo.choices.R;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * Created by Neji on 19/06/2016.
 */
public class ListAdapter extends BaseAdapter {
    final Context context;
    final List<Choices> choicesList;

    public ListAdapter(Context context, List<Choices> choices){
        this.context = context;
        this.choicesList = choices;
    }
public Choices giveItemPosition(int position){
    return this.choicesList.get(position);
}
    @Override
    public int getCount() {
        return this.choicesList.size();
    }

    @Override
    public Object getItem(int position) {
        return this.choicesList.get(position);
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
            convertView = inflater.inflate(R.layout.choice_list_item, parent, false);
        }

        TextView title = (TextView) convertView.findViewById(R.id.userListItem);
       TextView description = (TextView) convertView.findViewById(R.id.txtlastgist);
       TextView duration = (TextView) convertView.findViewById(R.id.txtTiming);
         CircleImageView imageView = (CircleImageView) convertView.findViewById(R.id.choice_image);


        Choices t = this.choicesList.get(position);
        if(!t.equals(null)) {

            title.setText(toCamelCase(t.getTitle()));
            description.setText(toCamelCase(t.getDescription()));
            duration.setText(t.getDuration());

            Picasso.with(context)
                    .load(t.getImageURL())
                    .placeholder(R.mipmap.ic_launcher)// optional
                    .resize(150, 150)
                    .centerCrop()
                    .error(R.mipmap.ic_launcher) // optional
                    .into(imageView);

        }
      notifyDataSetChanged();

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

}
