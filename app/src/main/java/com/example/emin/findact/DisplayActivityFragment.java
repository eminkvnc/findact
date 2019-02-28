package com.example.emin.findact;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.emin.findact.APIs.GameModel;
import com.example.emin.findact.APIs.MovieModel;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;

public class DisplayActivityFragment extends Fragment {

    public static final int INIT_MODE_MOVIE_ACTIVITY = 0;
    public static final int INIT_MODE_GAME_ACTIVITY = 1;
    public static final int INIT_MODE_GROUP_ACTIVITY = 2;

    private int initMode;
    private View v;
    private String TAG = "DisplayActivityFragment";
    MovieModel movieModel;
    GameModel gameModel;

    String videoKey;
    String videoSite;


    public  DisplayActivityFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (initMode == INIT_MODE_MOVIE_ACTIVITY){
            movieModel = new MovieModel(getArguments().getBundle("MovieData"));
        }
         else if (initMode == INIT_MODE_GAME_ACTIVITY){
            gameModel = new GameModel(getArguments().getBundle("GameData"));
            videoKey = getArguments().getString("movie_key");
            videoSite = getArguments().getString("movie_site");
            Log.d(TAG, "onCreate: "+videoSite+videoKey);
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

                ImageView moviePoster = v.findViewById(R.id.fragment_display_movie_poster_iv);
                ImageView trailer = v.findViewById(R.id.fragment_display_movie_trailer_iv);

                TextView movieTitle = v.findViewById(R.id.fragment_display_movie_title_tv);
                TextView movieOverview = v.findViewById(R.id.fragment_display_movie_overview_tv);
                TextView movieRelease_date = v.findViewById(R.id.fragment_display_movie_year_tv);
                TextView movieVote_average = v.findViewById(R.id.fragment_display_movie_stars_tv);
                TextView movieLanguage = v.findViewById(R.id.fragment_display_movie_language_tv);

                LinearLayout movieGenre = v.findViewById(R.id.fragment_display_movie_genre_ll);

                movieOverview.setMovementMethod(new ScrollingMovementMethod());

                movieGenre.removeAllViews();
                for (int i = 0; i < movieModel.getGenre().size(); i++){
                    TextView textView = new TextView(getContext());
                    textView.setText(movieModel.getGenre().get(i));
                    textView.setTextSize(18);
                    movieGenre.addView(textView);
                }

                if (!movieModel.getPoster_path().equals("null") && movieModel.getPoster_path() != null ){
                    Picasso.get().load(Uri.parse("http://image.tmdb.org/t/p/w185/"+movieModel.getPoster_path())).into(moviePoster);
                } else {
                    moviePoster.setImageResource(R.drawable.default_movie);
                }


                trailer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/results?search_query="+movieModel.getTitle()+" trailer"));
                        startActivity(intent);
                    }
                });


                movieTitle.setText(movieModel.getTitle());
                movieOverview.setText(movieModel.getOverview());
                movieRelease_date.setText(movieModel.getRelease_date());
                movieVote_average.setText(movieModel.getVote_average());
                movieLanguage.setText(movieModel.getLanguage());
                break;

            case INIT_MODE_GAME_ACTIVITY:

                v = inflater.inflate(R.layout.fragment_display_game_activity,container,false);

                ImageView gamePoster = v.findViewById(R.id.fragment_display_game_poster_iv);

                TextView gameTitle = v.findViewById(R.id.fragment_display_game_title_tv);
                TextView gameReleaseDate = v.findViewById(R.id.fragment_display_game_year_tv);
                TextView gameRating = v.findViewById(R.id.fragment_display_game_stars_tv);
                TextView gameOverview = v.findViewById(R.id.fragment_display_game_overview_tv);
                ImageView gameTrailer = v.findViewById(R.id.fragment_display_game_trailer_iv);

                LinearLayout gameGenre = v.findViewById(R.id.fragment_display_game_type_ll);
                LinearLayout gameModeName = v.findViewById(R.id.fragment_display_game_modes_name_ll);
                LinearLayout gamePlatform = v.findViewById(R.id.fragment_display_game_platforms_ll);

                gameOverview.setMovementMethod(new ScrollingMovementMethod());

                if (gameModel.getImage_id() != null){
                    Picasso.get().load(Uri.parse("https://images.igdb.com/igdb/image/upload/t_cover_big/"+gameModel.getImage_id()+".jpg")).into(gamePoster);
                } else {
                    gamePoster.setImageResource(R.drawable.default_game);
                }

                gameTitle.setText(gameModel.getName());
                gameReleaseDate.setText(gameModel.getRelease_date());
                gameRating.setText(new DecimalFormat ("##.#").format(gameModel.getRating()));
                gameOverview.setText(gameModel.getSummary());
                gameTrailer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (gameModel.getVideo_id() != null){
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:"+gameModel.getVideo_id()));
                            startActivity(intent);
                        } else {
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/results?search_query="+gameModel.getName()+" trailer"));
                            startActivity(intent);
                        }

                    }
                });

                gameGenre.removeAllViews();
                for (int i = 0; i < gameModel.getGenre().size(); i++){
                    TextView textView = new TextView(getContext());
                    textView.setText(gameModel.getGenre().get(i));
                    textView.setTextSize(18);
                    gameGenre.addView(textView);
                }

                gameModeName.removeAllViews();
                for (int i = 0; i < gameModel.getGame_mode_name().size(); i++){
                    TextView textView = new TextView(getContext());
                    textView.setText(gameModel.getGame_mode_name().get(i));
                    textView.setTextSize(18);
                    gameModeName.addView(textView);
                }

                gamePlatform.removeAllViews();
                for (int i = 0; i < gameModel.getPlatform_name().size(); i++){
                    TextView textView = new TextView(getContext());
                    textView.setText(gameModel.getPlatform_name().get(i));
                    textView.setTextSize(18);
                    gamePlatform.addView(textView);
                }



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
