package com.example.emin.findact.Adapters;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.emin.findact.APIs.MovieModel;
import com.example.emin.findact.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MovieListItemAdapter extends RecyclerView.Adapter<MovieListItemAdapter.MovieListItemViewHolder> {


    // Adaptera girmiyor
    private ArrayList<MovieModel> movieModelArrayList;
    private Context context;

    private String TAG = "MovieListItemAdapter";

    public MovieListItemAdapter( Context context, ArrayList<MovieModel> movieModelArrayList) {
        this.movieModelArrayList = movieModelArrayList;
        this.context = context;
        Log.d(TAG, "MovieListItemAdapter: size "+movieModelArrayList.size());
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
        CustomListener customListener = new CustomListener(movieListItemViewHolder, position, movieModel);
        movieListItemViewHolder.cardView.setOnClickListener(customListener);
        Log.d(TAG, "onBindViewHolder: "+ movieModel.getTitle());

        movieListItemViewHolder.title.setText(movieModel.getTitle());
        movieListItemViewHolder.releaseDate.setText(movieModel.getRelease_date());
        Picasso.get().load(Uri.parse("http://image.tmdb.org/t/p/w185/"+movieModel.getPoster_path())).into(movieListItemViewHolder.poster);

//        try {
//            URL url = new URL("http://image.tmdb.org/t/p/w185/"+movieModel.getPoster_path());
//
//            Log.d("onBindViewHolder", "onBindViewHolder: "+url);
//            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//            connection.setDoInput(true);
//            connection.connect();
//
//            InputStream is = connection.getInputStream();
//            Bitmap bm = BitmapFactory.decodeStream(is);
//            movieListItemViewHolder.poster.setImageBitmap(bm);
//
//
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

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
            Log.d(TAG, "MovieListItemViewHolder: "+title.getText().toString());

        }
    }

    class CustomListener implements View.OnClickListener{

        private MovieListItemViewHolder movieListItemViewHolder;
        private int position;
        private MovieModel movieModel;

        CustomListener(MovieListItemViewHolder movieListItemViewHolder, int position, MovieModel movieModel) {
            this.movieListItemViewHolder = movieListItemViewHolder;
            this.position = position;
            this.movieModel = movieModel;
        }

        @Override
        public void onClick(View view) {

        }
    }
}
