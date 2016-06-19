package com.nejitawo.choices.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.nejitawo.choices.DetailsActivity;
import com.nejitawo.choices.GlobalClass;
import com.nejitawo.choices.Model.Choices;
import com.nejitawo.choices.R;
import com.squareup.picasso.Picasso;

import java.util.List;




public class CardsAdapter extends BaseAdapter implements View.OnClickListener
		{

	private LayoutInflater mInflater;
	final Context context;
	final List<Choices> choicesList;

	public CardsAdapter(Context context, List<Choices> choices) {
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
		return (position);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;

		if (convertView == null) {
			convertView = mInflater.inflate(
					R.layout.list_item_cards, parent, false);
			holder = new ViewHolder();
			holder.image = (ImageView) convertView
					.findViewById(R.id.imgProfile);

			holder.txtTitle = (TextView) convertView
					.findViewById(R.id.txtTitle);
			holder.txtDuration = (TextView) convertView
					.findViewById(R.id.txtDuration);
			holder.txtContent = (TextView) convertView
					.findViewById(R.id.txtContent);
			holder.btnClick = (Button) convertView
					.findViewById(R.id.btnDo);

		holder.btnClick.setOnClickListener(this);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		Choices t = this.choicesList.get(position);

		if(!t.equals(null)) {
			// final GlobalClass globalVariable = (GlobalClass) getActivity().getApplicationContext();
			holder.txtTitle.setText(t.getTitle());
			holder.txtContent.setText(t.getDescription());
			holder.txtDuration.setText("Started n hours ago");
		holder.btnClick.setTag(position);

			Picasso.with(context)
					.load(t.getImageURL())
					.placeholder(R.mipmap.ic_launcher)
					.resize(150, 150)
					.centerCrop()
					.error(R.mipmap.ic_launcher)
					.into(holder.image);


		}
		notifyDataSetChanged();


		return convertView;
	}


	private static class ViewHolder {
		public ImageView image;
		public TextView txtTitle;
		public TextView txtContent;
		public TextView txtDuration;
		public Button btnClick;

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		int position = (Integer) v.getTag();
		switch (v.getId()) {
		case R.id.btnDo:
			// click on like button
			break;
			//Choices thisChoice = (Choices)getItemId(position);

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
