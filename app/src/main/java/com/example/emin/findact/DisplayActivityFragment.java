package com.example.emin.findact;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.emin.findact.APIs.MovieModel;
import com.squareup.picasso.Picasso;

public class DisplayActivityFragment extends Fragment {

    public static final int INIT_MODE_MOVIE_ACTIVITY = 0;
    public static final int INIT_MODE_GAME_ACTIVITY = 1;
    public static final int INIT_MODE_GROUP_ACTIVITY = 2;

    private int initMode;
    private View v;
    private String TAG = "DisplayActivityFragment";
    MovieModel movieModel;


    public  DisplayActivityFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (initMode == INIT_MODE_MOVIE_ACTIVITY){
            movieModel = new MovieModel(getArguments().getBundle("MovieData"));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        ActionBar actionBar = activity.getSupportActionBar();
        actionBar.setTitle(R.string.title_display_activity);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        switch (initMode){
            case INIT_MODE_MOVIE_ACTIVITY:
                v = inflater.inflate(R.layout.fragment_display_movie_activity,container,false);

                ImageView poster = v.findViewById(R.id.fragment_display_movie_poster_iv);
                TextView title = v.findViewById(R.id.fragment_display_movie_title_tv);
                TextView overview = v.findViewById(R.id.fragment_display_movie_overview_tv);
                TextView release_date = v.findViewById(R.id.fragment_display_movie_year_tv);
                TextView vote_average = v.findViewById(R.id.fragment_display_movie_stars_tv);
                TextView genre = v.findViewById(R.id.fragment_display_movie_type_tv);
                TextView language = v.findViewById(R.id.fragment_display_movie_language_tv);

                overview.setMovementMethod(new ScrollingMovementMethod());

                if (!movieModel.getPoster_path().equals("null") && movieModel.getPoster_path() != null ){
                    Picasso.get().load(Uri.parse("http://image.tmdb.org/t/p/w185/"+movieModel.getPoster_path())).into(poster);
                }
                title.setText(movieModel.getTitle());
                overview.setText(movieModel.getOverview());
                release_date.setText(movieModel.getRelease_date());
                vote_average.setText(movieModel.getVote_average());
                genre.setText(movieModel.getGenre());
                language.setText(movieModel.getLanguage());
                break;
            case INIT_MODE_GAME_ACTIVITY:
                v = inflater.inflate(R.layout.fragment_display_game_activity,container,false);
                break;
            case INIT_MODE_GROUP_ACTIVITY:
                v = inflater.inflate(R.layout.fragment_display_group_activity,container,false);
                break;
            default:
                v = null;
        }

        return v;
    }

    public int getInitMode() {
        return initMode;
    }

    public void setInitMode(int initMode) {
        this.initMode = initMode;
    }

}
