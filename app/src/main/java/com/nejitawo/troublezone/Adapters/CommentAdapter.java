package com.nejitawo.troublezone.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nejitawo.troublezone.GlobalClass;
import com.nejitawo.troublezone.Model.Comments;
import com.nejitawo.troublezone.R;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * Created by Neji on 10/02/2016.
 */
public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {
    Context mContext;
    final List<Comments> comList;
    OnItemClickListener mItemClickListener;
    Bitmap theBitmap = null;

    public CommentAdapter(Context context, List<Comments> comments){
        this.mContext = context;
        this.comList = comments;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public CircleImageView image;
        public TextView txtUsername;
        public TextView txtPostedDate;
        public TextView txtLocation;
       public TextView txtComments;
       //public TextView btnClick;





        public ViewHolder(View itemView){
            super(itemView);
           image = (CircleImageView) itemView.findViewById(R.id.choice_image);
            txtUsername = (TextView) itemView.findViewById(R.id.userListItem);
            txtLocation = (TextView) itemView.findViewById(R.id.txtLocation);
            txtPostedDate = (TextView) itemView.findViewById(R.id.txtTiming);
           txtComments = (TextView) itemView.findViewById(R.id.txtlastgist);


          //  btnClick.setOnClickListener(this);
        }
        @Override
        public void onClick(View view){
           // if(mItemClickListener!=null){
           //   mItemClickListener.onItemClick(itemView, getPosition());

                int possition = (Integer) view.getTag();

                switch (view.getId()) {
                    case R.id.btnMore:
                        final GlobalClass globalVariable = (GlobalClass) mContext.getApplicationContext();
                   /*
                        Property thisProperty = propList.get(possition);

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
                    case R.id.layoutListen:
                        // click on share button
                        Toast.makeText(view.getContext(), "Clicked on Listen -  " + possition, Toast.LENGTH_SHORT).show();
                        break;
*/
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comments_item,parent,false);

        return new ViewHolder(view) ;

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        Comments t = this.comList.get(position);

        holder.txtUsername.setText("@"+t.getUserName());
         long timeInMilliseconds = t.getPostedDate().getTime();
        long now = Calendar.getInstance().getTimeInMillis();
        holder.txtPostedDate.setText(String.valueOf(DateUtils.getRelativeDateTimeString
                (mContext, timeInMilliseconds, DateUtils.SECOND_IN_MILLIS, DateUtils.WEEK_IN_MILLIS, 1)));

      //  holder.txtLocation.setText(t.getLocality()+ ", " + t.getCity()+ " , "+ t.getCountry());
      //  holder.txtDistance.setText(t.getDistanceAway());
        holder.txtComments.setText(t.getComments());

        Picasso.with(mContext).load(t.getUserImageURL()).into(holder.image);



    }

    @Override
    public int getItemCount() {
        return  this.comList.size();
    }

    private Target target = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            theBitmap = bitmap;
        }
        @Override
        public void onPrepareLoad(Drawable drawable) {
        }

        @Override
        public void onBitmapFailed(Drawable drawable) {
        }
    };
public void clearList() {
    if (comList.size() > 0) {
        this.comList.clear();
        notifyDataSetChanged();
    }
}

    private String formatDecimal(Integer number){
        DecimalFormat decimalFormat = new DecimalFormat("#,###,###");
      return decimalFormat.format(number);
    }

}
