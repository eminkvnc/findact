package com.findact.Adapters;

import android.content.Context;
import android.net.Uri;
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

import com.findact.APIs.ExploreModel;
import com.findact.DisplayActivityFragment;
import com.findact.Firebase.EventLog;
import com.findact.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ExplorelistItemAdapter extends RecyclerView.Adapter<ExplorelistItemAdapter.ExplorelistItemViewHolder> {

    private ArrayList<ExploreModel> exploreModelArrayList;
    private Context context;

    public ExplorelistItemAdapter(ArrayList<ExploreModel> exploreModelArrayList, Context context) {
        this.exploreModelArrayList = exploreModelArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public ExplorelistItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_explore, viewGroup,false);
        return new ExplorelistItemAdapter.ExplorelistItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExplorelistItemViewHolder explorelistItemViewHolder, int i) {
        ExploreModel exploreModel = exploreModelArrayList.get(i);
        CustomListener customListener = new CustomListener(exploreModel, i);
        explorelistItemViewHolder.cardView.setOnClickListener(customListener);
        //
        switch (exploreModel.getModelName()){
            case EventLog.ACTIVITY_TYPE_MOVIE:
                if (!exploreModel.getMovieModel().getPoster_path().equals("null") && exploreModel.getMovieModel().getPoster_path() != null){
                    Picasso.get().load(Uri.parse("http://image.tmdb.org/t/p/w185/"+exploreModel.getMovieModel().getPoster_path())).into(explorelistItemViewHolder.imaqeView);
                } else {
                    explorelistItemViewHolder.imaqeView.setImageResource(R.drawable.default_movie);
                }
                break;
            case EventLog.ACTIVITY_TYPE_GAME:
                if (exploreModel.getGameModel().getImageId() != null && ! exploreModel.getGameModel().getImageId().equals("null") && !exploreModel.getGameModel().getImageId().equals("NaN")){
                    Picasso.get().load(Uri.parse("https://images.igdb.com/igdb/image/upload/t_cover_big/"+exploreModel.getGameModel().getImageId()+".jpg")).into(explorelistItemViewHolder.imaqeView);
                } else {
                    //explorelistItemViewHolder.imaqeView.setImageResource(R.drawable.default_game);
                    Picasso.get().load(R.drawable.default_game).into(explorelistItemViewHolder.imaqeView);
                }
                break;
        }


    }


    @Override
    public int getItemCount() {
        return exploreModelArrayList.size();
    }

    class ExplorelistItemViewHolder extends RecyclerView.ViewHolder {
        ImageView imaqeView;
        CardView cardView;
        ExplorelistItemViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.list_item_explore_cv);
            imaqeView = itemView.findViewById(R.id.list_item_explore_iv);
        }
    }


    class CustomListener implements View.OnClickListener {

        private ExploreModel exploreModel;
        DisplayActivityFragment displayActivityFragment;
        int position;

        CustomListener(ExploreModel exploreModel, int position) {
            this.exploreModel = exploreModel;
            this.position = position;
        }

        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.list_item_explore_cv) {

                displayActivityFragment = new DisplayActivityFragment();

                Bundle bundle = new Bundle();

                if (exploreModel.getModelName().equals(EventLog.ACTIVITY_TYPE_MOVIE)){
                    bundle.putBundle("MovieData", exploreModel.getMovieModel().MovieDataToBundle());
                    displayActivityFragment.setArguments(bundle);
                    displayActivityFragment.setInitMode(DisplayActivityFragment.INIT_MODE_MOVIE_ACTIVITY);
                } else {
                    bundle.putBundle("GameData", exploreModel.getGameModel().GameDataToBundle());
                    displayActivityFragment.setArguments(bundle);
                    displayActivityFragment.setInitMode(DisplayActivityFragment.INIT_MODE_GAME_ACTIVITY);
                }
                FragmentManager fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.replace(R.id.main_frame, displayActivityFragment);
                fragmentTransaction.commit();

            }
        }
    }
}
