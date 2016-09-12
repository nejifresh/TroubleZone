package com.nejitawo.troublezone.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.devspark.robototextview.widget.RobotoTextView;
import com.nejitawo.troublezone.Model.Events;
import com.nejitawo.troublezone.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class GoogleCardsSocialAdapter extends BaseAdapter
		{

	private LayoutInflater mInflater;
	final Context context;
	final List<Events> eventsList;

	public GoogleCardsSocialAdapter(Context context, List<Events> eventses) {
		//super(context, 0, wifes);
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
		return (position);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;

		if (convertView == null) {
			convertView = mInflater.inflate(
					R.layout.goolecards, parent, false);
			holder = new ViewHolder();
			holder.image = (ImageView) convertView
					.findViewById(R.id.list_item_google_cards_social_image);
			holder.profileImage = (CircleImageView) convertView
					.findViewById(R.id.list_item_google_cards_social_profile_image);
			holder.username = (RobotoTextView) convertView
					.findViewById(R.id.list_item_google_cards_social_name);
			holder.place = (RobotoTextView) convertView
					.findViewById(R.id.list_item_google_cards_social_place);

			holder.postedDate = (RobotoTextView) convertView
					.findViewById(R.id.list_item_google_cards_social_in);

			holder.text = (RobotoTextView) convertView
					.findViewById(R.id.list_item_google_cards_social_text);
			holder.like = (RobotoTextView) convertView
					.findViewById(R.id.list_item_google_cards_social_like);
			holder.follow = (RobotoTextView) convertView
					.findViewById(R.id.list_item_google_cards_social_follow);
		//	holder.like.setOnClickListener(this);
		//	holder.follow.setOnClickListener(this);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		Events t = this.eventsList.get(position);

		if(!t.equals(null)) {
			// final GlobalClass globalVariable = (GlobalClass) getActivity().getApplicationContext();
			holder.username.setText("@" + t.getSenderName());
			holder.place.setText(t.getEventType() +  " In  "+  String.valueOf(t.getLocality()) );
			holder.postedDate.setText("Posted On  "+  String.valueOf(t.getPostedDate()) );
			holder.text.setText(toCamelCase(t.getDescription()));

		//	wifename.setText(toCamelCase(t.getFullName()));
		//	location.setText(toCamelCase(t.getDistanceAway()));
			holder.like.setTag(position);
			holder.follow.setTag(position);
			holder.like.setText(toCamelCase(t.getDistanceAway()));

			Picasso.with(context)
					.load(t.getImage())
					.placeholder(R.mipmap.ic_launcher)// optional
					.resize(150, 150)
					.centerCrop()
					.error(R.mipmap.ic_launcher) // optional
					.into(holder.profileImage);



			// imageView.setImageBitmap(t.getImage());
			// ratingBar.setRating(2);
			// distanceAway.setText(t.getDistanceAway());
		}
		// distanceAway.setText("Approx. "+ (String.valueOf(roundTwoDecimals(t.getDistanceFrom()/1000)  +" km away"))); //provide distance in km
		// distanceAway.setText(t.getTimeDifference());
		notifyDataSetChanged();


		return convertView;
	}


	private static class ViewHolder {
		public CircleImageView profileImage;
		public ImageView image;
		public TextView username;
		public TextView place;
		public TextView text;
		public TextView like;
		public TextView follow;
		public TextView postedDate;
	}

	//@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		int possition = (Integer) v.getTag();
		switch (v.getId()) {
		case R.id.list_item_google_cards_social_like:
			// click on like button
			break;
		case R.id.list_item_google_cards_social_follow:
			// click on follow button

			break;
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
}
