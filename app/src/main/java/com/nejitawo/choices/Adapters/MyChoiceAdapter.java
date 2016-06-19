package com.nejitawo.choices.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nejitawo.choices.Model.Choices;
import com.nejitawo.choices.R;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.List;




/**
 * Created by Neji on 10/02/2016.
 */
public class MyChoiceAdapter extends RecyclerView.Adapter<MyChoiceAdapter.ViewHolder> {
    Context mContext;
    final List<Choices> choicesList;
    OnItemClickListener mItemClickListener;
    Bitmap theBitmap = null;
    public MyChoiceAdapter(Context context, List<Choices> choices){
        this.mContext = context;
        this.choicesList = choices;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public ImageView image;
        public TextView txtTitle;
        public TextView txtContent;
        public TextView txtDuration;
        public TextView txtMainTitle;
        public Button btnClick;




        public ViewHolder(View itemView){
            super(itemView);
           image = (ImageView) itemView.findViewById(R.id.imgProfile);
            txtTitle = (TextView) itemView.findViewById(R.id.txtTitle);
            txtMainTitle = (TextView)itemView.findViewById(R.id.txtMainTitle);
            txtContent = (TextView) itemView.findViewById(R.id.txtContent);
            txtDuration = (TextView) itemView.findViewById(R.id.txtDuration);
            btnClick = (Button)itemView.findViewById(R.id.btnDo);

            btnClick.setOnClickListener(this);
        }
        @Override
        public void onClick(View view){
            if(mItemClickListener!=null){
                mItemClickListener.onItemClick(itemView, getPosition());
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_choices,parent,false);

        return new ViewHolder(view) ;

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        Choices t = this.choicesList.get(position);

        holder.txtTitle.setText(t.getTitle());
        holder.txtContent.setText(t.getDescription());
        holder.txtDuration.setText("Started x hours ago");
        holder.btnClick.setTag(position);
        if (t.getStatus().equals("INPROGRESS")){
            holder.btnClick.setText("Complete");
        } else if (t.getStatus().equals("DONE")){
            holder.btnClick.setText("Once More");
            holder.btnClick.setBackgroundColor(mContext. getResources().getColor(R.color.off_white));
            holder.btnClick.setTextColor(mContext. getResources().getColor(R.color.black));
            holder.txtDuration.setText("Completed x hours ago");

        }
        holder.txtMainTitle.setText(t.getMainTitle());

            Picasso.with(mContext).load(t.getImageURL()).into(holder.image);

            Picasso.with(mContext).load(t.getImageURL()).into(new Target() {
                @Override
                public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {

                    Palette.generateAsync(bitmap, new Palette.PaletteAsyncListener() {
                        @Override
                        public void onGenerated(Palette palette) {
                            int bgColor = palette.getVibrantColor(mContext.getResources().getColor(android.R.color.black));
                           // holder.placeNameHolder.setBackgroundColor(bgColor);
                            //holder.placeImage.setImageDrawable(new BitmapDrawable(bitmap));
                        }
                    });
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
        return  this.choicesList.size();
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
    if (choicesList.size() > 0) {
        this.choicesList.clear();
        notifyDataSetChanged();
    }
}

}
