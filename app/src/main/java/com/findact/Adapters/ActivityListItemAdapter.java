package com.findact.Adapters;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.findact.APIs.ActivityModel;
import com.findact.DisplayActivityFragment;
import com.findact.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ActivityListItemAdapter extends RecyclerView.Adapter<ActivityListItemAdapter.ActivityListItemViewHolder> {

    //list_item_activity layout'unu doldur ve adapter'ı düzenle.
    private Context context;
    private ArrayList<ActivityModel> activityModelArrayList;

    public ActivityListItemAdapter(Context context, ArrayList<ActivityModel> activityModelArrayList) {
        this.context = context;
        this.activityModelArrayList = activityModelArrayList;
    }

    @NonNull
    @Override
    public ActivityListItemAdapter.ActivityListItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(context).inflate(R.layout.list_item_activity,viewGroup,false);
        return new ActivityListItemViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ActivityListItemAdapter.ActivityListItemViewHolder activityListItemViewHolder, int i) {

        ActivityModel activityModel = activityModelArrayList.get(i);
        CustomListener customListener = new CustomListener(activityModel);
        activityListItemViewHolder.cardView.setOnClickListener(customListener);
        activityListItemViewHolder.name.setText(activityModel.getName());
        activityListItemViewHolder.date.setText(activityModel.getDate());
        if(activityModel.getImageUri() != null){
            Picasso.get().load(activityModel.getImageUri()).into(activityListItemViewHolder.image);
        }else{
            activityListItemViewHolder.image.setImageResource(R.drawable.default_group);
        }
    }

    @Override
    public int getItemCount() {
        return activityModelArrayList.size();
    }

    class ActivityListItemViewHolder extends RecyclerView.ViewHolder{

        ImageView image;
        TextView name;
        TextView date;
        CardView cardView;


        public ActivityListItemViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.list_item_activity_image);
            name = itemView.findViewById(R.id.list_item_activity_name);
            date = itemView.findViewById(R.id.list_item_activity_date);
            cardView = itemView.findViewById(R.id.list_item_activity_cardview);
        }
    }

    class CustomListener implements View.OnClickListener {

        private ActivityModel activityModel;
        private DisplayActivityFragment displayActivityFragment;

        CustomListener(ActivityModel activityModel) {
            this.activityModel = activityModel;
        }


        @Override
        public void onClick(View v) {
            if(v.getId() == R.id.list_item_activity_cardview){

                displayActivityFragment = new DisplayActivityFragment();
                Bundle bundle = new Bundle();
                bundle.putBundle("ActivityData",activityModel.activityDataToBundle());
                displayActivityFragment.setArguments(bundle);
                displayActivityFragment.setInitMode(DisplayActivityFragment.INIT_MODE_GROUP_ACTIVITY);

                FragmentManager fragmentManager = ((FragmentActivity)context).getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.replace(R.id.main_frame,displayActivityFragment);
                fragmentTransaction.commit();

            }
        }
    }




}
