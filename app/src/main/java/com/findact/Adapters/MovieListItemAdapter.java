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
import android.widget.TextView;
import com.findact.APIs.MovieModel;
import com.findact.DisplayActivityFragment;
import com.findact.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MovieListItemAdapter extends RecyclerView.Adapter<MovieListItemAdapter.MovieListItemViewHolder> {

    private ArrayList<MovieModel> movieModelArrayList;
    private Context context;
    private String TAG = "MovieListItemAdapter";


    public MovieListItemAdapter( Context context, ArrayList<MovieModel> movieModelArrayList) {
        this.movieModelArrayList = movieModelArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public MovieListItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_movie, viewGroup,false);
        return new MovieListItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieListItemViewHolder movieListItemViewHolder, int position) {

        MovieModel movieModel = movieModelArrayList.get(position);
        CustomListener customListener = new CustomListener(movieModel);
        movieListItemViewHolder.cardView.setOnClickListener(customListener);

        movieListItemViewHolder.title.setText(movieModel.getTitle());
        movieListItemViewHolder.releaseDate.setText(movieModel.getRelease_date());
        if (!movieModel.getPoster_path().equals("null") && movieModel.getPoster_path() != null){
            Picasso.get().load(Uri.parse("http://image.tmdb.org/t/p/w185/"+movieModel.getPoster_path())).into(movieListItemViewHolder.poster);
        } else {
            movieListItemViewHolder.poster.setImageResource(R.drawable.default_movie);
        }
    }


    @Override
    public int getItemCount() {
        return movieModelArrayList.size();
    }


    class MovieListItemViewHolder extends RecyclerView.ViewHolder{

        ImageView poster;
        TextView title;
        TextView releaseDate;
        CardView cardView;

        MovieListItemViewHolder(@NonNull View v) {
            super(v);
            cardView = v.findViewById(R.id.list_item_movie_cardview);
            poster = v.findViewById(R.id.list_item_movie_poster);
            title = v.findViewById(R.id.list_item_movie_title);
            releaseDate = v.findViewById(R.id.list_item_movie_releaseDate);

        }
    }


    class CustomListener implements View.OnClickListener{

        private MovieModel movieModel;
        DisplayActivityFragment displayActivityFragment;


        CustomListener(MovieModel movieModel) {
            this.movieModel = movieModel;
        }

        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.list_item_movie_cardview) {

                displayActivityFragment = new DisplayActivityFragment();

                Bundle bundle = new Bundle();
                bundle.putBundle("MovieData" ,movieModel.MovieDataToBundle());

                displayActivityFragment.setArguments(bundle);
                displayActivityFragment.setInitMode(DisplayActivityFragment.INIT_MODE_MOVIE_ACTIVITY);

                FragmentManager fragmentManager = ((FragmentActivity)context).getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.replace(R.id.main_frame,displayActivityFragment);
                fragmentTransaction.commit();

            }
        }
    }



}
