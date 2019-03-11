package com.example.emin.findact;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.example.emin.findact.APIs.ActivityModel;
import com.example.emin.findact.APIs.GameModel;
import com.example.emin.findact.APIs.MovieModel;
import com.example.emin.findact.Firebase.EventLog;
import com.example.emin.findact.Firebase.FirebaseDBHelper;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.UUID;

public class DisplayActivityFragment extends Fragment {

    FirebaseDBHelper firebaseDBHelper = FirebaseDBHelper.getInstance();

    public static final int INIT_MODE_MOVIE_ACTIVITY = 0;
    public static final int INIT_MODE_GAME_ACTIVITY = 1;
    public static final int INIT_MODE_GROUP_ACTIVITY = 2;

    private int initMode;
    private View v;
    private String TAG = "DisplayActivityFragment";
    MovieModel movieModel;
    GameModel gameModel;
    ActivityModel activityModel;

    String videoKey;
    String videoSite;
    GoogleMap mMap;
    MapView activityMap;

    ImageView movieLikeImageView;
    ImageView movieDislikeImageView;
    ImageView movieShareImageView;

    ImageView gameLikeImageView;
    ImageView gameDislikeImageView;
    ImageView gemeShareImageView;

    ImageView activityLikeImageView;
    ImageView activityDislikeImageView;
    ImageView activityShareImageView;

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
            Log.d(TAG, "onCreate: "+videoSite+videoKey);
        }
        else if(initMode == INIT_MODE_GROUP_ACTIVITY){
            activityModel = new ActivityModel(getArguments().getBundle("ActivityData"));
        }
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        switch (initMode){
            case INIT_MODE_MOVIE_ACTIVITY:
                v = inflater.inflate(R.layout.fragment_display_movie_activity,container,false);
                initDisplayForMovie(v);
                break;

            case INIT_MODE_GAME_ACTIVITY:
                v = inflater.inflate(R.layout.fragment_display_game_activity,container,false);
                initDisplayForGame(v);
                break;
            case INIT_MODE_GROUP_ACTIVITY:
                v = inflater.inflate(R.layout.fragment_display_group_activity,container,false);
                initDisplayForActivity(v, savedInstanceState);
                break;
            default:
                v = null;
        }
        return v;
    }

    class EventLogOnClickListener implements View.OnClickListener{

        String activityId;

        public EventLogOnClickListener(String activityId) {
            this.activityId = activityId;
        }

        final Boolean[] isLiked = new Boolean[1];
        final Boolean[] isDisliked = new Boolean[1];

        @Override
        public void onClick(View v) {
            EventLog eventLog;
            switch (v.getId()){
                case R.id.fragment_display_movie_like_iv:
                    eventLog = new EventLog(UUID.randomUUID().toString(),
                            Calendar.getInstance().getTime().toString(),
                            EventLog.EVENT_TYPE_LIKE,
                            activityId,
                            EventLog.ACTIVITY_TYPE_MOVIE);
                    firebaseDBHelper.addEventUserLog(eventLog);
                    firebaseDBHelper.isLiked(EventLog.ACTIVITY_TYPE_MOVIE, activityId, isLiked, new OnTaskCompletedListener() {
                        @Override
                        public void onTaskCompleted() {
                            if(isLiked[0]){
                                movieLikeImageView.setImageResource(R.drawable.like);
                            }
                            else {
                                movieLikeImageView.setImageResource(R.drawable.like_green);
                            }
                        }
                    });
                    movieDislikeImageView.setImageResource(R.drawable.dislike);
                    break;
                case R.id.fragment_display_movie_dislike_iv:
                    movieDislikeImageView.setImageResource(R.drawable.dislike_green);
                    movieLikeImageView.setImageResource(R.drawable.like);
                    eventLog = new EventLog(UUID.randomUUID().toString(),
                            Calendar.getInstance().getTime().toString(),
                            EventLog.EVENT_TYPE_DISLIKE,
                            activityId,
                            EventLog.ACTIVITY_TYPE_MOVIE);
                    firebaseDBHelper.addEventUserLog(eventLog);
                    firebaseDBHelper.isDisliked(EventLog.ACTIVITY_TYPE_MOVIE, activityId, isDisliked, new OnTaskCompletedListener() {
                        @Override
                        public void onTaskCompleted() {
                            if(isDisliked[0]){
                                movieDislikeImageView.setImageResource(R.drawable.dislike);
                            }
                            else {
                                movieDislikeImageView.setImageResource(R.drawable.dislike_green);
                            }
                        }
                    });
                    break;
                case R.id.fragment_display_movie_share_iv:
                    eventLog = new EventLog(UUID.randomUUID().toString(),
                            Calendar.getInstance().getTime().toString(),
                            EventLog.EVENT_TYPE_SHARE,
                            activityId,
                            EventLog.ACTIVITY_TYPE_MOVIE);
                    firebaseDBHelper.addEventUserLog(eventLog);
                    break;
                case R.id.fragment_display_game_like_iv:
                    gameLikeImageView.setImageResource(R.drawable.like_green);
                    gameDislikeImageView.setImageResource(R.drawable.dislike);
                    eventLog = new EventLog(UUID.randomUUID().toString(),
                            Calendar.getInstance().getTime().toString(),
                            EventLog.EVENT_TYPE_LIKE,
                            activityId,
                            EventLog.ACTIVITY_TYPE_GAME);
                    firebaseDBHelper.addEventUserLog(eventLog);
                    firebaseDBHelper.isLiked(EventLog.ACTIVITY_TYPE_GAME, activityId, isLiked, new OnTaskCompletedListener() {
                        @Override
                        public void onTaskCompleted() {
                            if(isLiked[0]){
                                gameLikeImageView.setImageResource(R.drawable.like);
                            }
                            else {
                                gameLikeImageView.setImageResource(R.drawable.like_green);
                            }
                        }
                    });
                    break;
                case R.id.fragment_display_game_dislike_iv:
                    gameDislikeImageView.setImageResource(R.drawable.dislike_green);
                    gameLikeImageView.setImageResource(R.drawable.like);
                    eventLog = new EventLog(UUID.randomUUID().toString(),
                            Calendar.getInstance().getTime().toString(),
                            EventLog.EVENT_TYPE_DISLIKE,
                            activityId,
                            EventLog.ACTIVITY_TYPE_GAME);
                    firebaseDBHelper.addEventUserLog(eventLog);
                    firebaseDBHelper.isDisliked(EventLog.ACTIVITY_TYPE_GAME, activityId, isDisliked, new OnTaskCompletedListener() {
                        @Override
                        public void onTaskCompleted() {
                            if(isDisliked[0]){
                                gameDislikeImageView.setImageResource(R.drawable.dislike);
                            }
                            else {
                                gameDislikeImageView.setImageResource(R.drawable.dislike_green);
                            }
                        }
                    });
                    break;
                case R.id.fragment_display_game_share_iv:
                    eventLog = new EventLog(UUID.randomUUID().toString(),
                            Calendar.getInstance().getTime().toString(),
                            EventLog.EVENT_TYPE_SHARE,
                            activityId,
                            EventLog.ACTIVITY_TYPE_GAME);
                    firebaseDBHelper.addEventUserLog(eventLog);
                    break;
                case R.id.fragment_display_group_like_iv:
                    activityLikeImageView.setImageResource(R.drawable.like_green);
                    activityDislikeImageView.setImageResource(R.drawable.dislike);
                    eventLog = new EventLog(UUID.randomUUID().toString(),
                            Calendar.getInstance().getTime().toString(),
                            EventLog.EVENT_TYPE_LIKE,
                            activityId,
                            EventLog.ACTIVITY_TYPE_ACTIVITY);
                    firebaseDBHelper.addEventUserLog(eventLog);
                    firebaseDBHelper.isLiked(EventLog.ACTIVITY_TYPE_ACTIVITY, activityId, isLiked, new OnTaskCompletedListener() {
                        @Override
                        public void onTaskCompleted() {
                            if(isLiked[0]){
                                activityLikeImageView.setImageResource(R.drawable.like);
                            }
                            else {
                                activityLikeImageView.setImageResource(R.drawable.like_green);
                            }
                        }
                    });
                    break;
                case R.id.fragment_display_group_dislike_iv:
                    activityDislikeImageView.setImageResource(R.drawable.dislike_green);
                    activityLikeImageView.setImageResource(R.drawable.like);
                    eventLog = new EventLog(UUID.randomUUID().toString(),
                            Calendar.getInstance().getTime().toString(),
                            EventLog.EVENT_TYPE_DISLIKE,
                            activityId,
                            EventLog.ACTIVITY_TYPE_ACTIVITY);
                    firebaseDBHelper.addEventUserLog(eventLog);
                    firebaseDBHelper.isDisliked(EventLog.ACTIVITY_TYPE_ACTIVITY, activityId, isDisliked, new OnTaskCompletedListener() {
                        @Override
                        public void onTaskCompleted() {
                            if(isDisliked[0]){
                                activityDislikeImageView.setImageResource(R.drawable.dislike);
                            }
                            else {
                                activityDislikeImageView.setImageResource(R.drawable.dislike_green);
                            }
                        }
                    });
                    break;
                case R.id.fragment_display_group_share_iv:
                    eventLog = new EventLog(UUID.randomUUID().toString(),
                            Calendar.getInstance().getTime().toString(),
                            EventLog.EVENT_TYPE_SHARE,
                            activityId,
                            EventLog.ACTIVITY_TYPE_ACTIVITY);
                    firebaseDBHelper.addEventUserLog(eventLog);
                    break;
            }
        }
    }

    void initDisplayForMovie(View v){

        ImageView moviePoster = v.findViewById(R.id.fragment_display_movie_poster_iv);
        ImageView trailer = v.findViewById(R.id.fragment_display_movie_trailer_iv);
        TextView movieTitle = v.findViewById(R.id.fragment_display_movie_title_tv);
        TextView movieOverview = v.findViewById(R.id.fragment_display_movie_overview_tv);
        TextView movieRelease_date = v.findViewById(R.id.fragment_display_movie_release_date_tv);
        TextView movieVote_average = v.findViewById(R.id.fragment_display_movie_vote_average_tv);
        TextView movieLanguage = v.findViewById(R.id.fragment_display_movie_language_tv);
        movieLikeImageView = v.findViewById(R.id.fragment_display_movie_like_iv);
        movieDislikeImageView = v.findViewById(R.id.fragment_display_movie_dislike_iv);
        movieShareImageView = v.findViewById(R.id.fragment_display_movie_share_iv);

        LinearLayout movieGenre = v.findViewById(R.id.fragment_display_movie_genre_ll);

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


        final Boolean[] isLiked = new Boolean[1];
        firebaseDBHelper.isLiked(EventLog.ACTIVITY_TYPE_MOVIE,String.valueOf(movieModel.getMovieId()),isLiked, new OnTaskCompletedListener() {
            @Override
            public void onTaskCompleted() {
                if(isLiked[0]){
                    movieLikeImageView.setImageResource(R.drawable.like_green);
                }
                else {
                    movieLikeImageView.setImageResource(R.drawable.like);
                }
            }
        });
        final Boolean[] isDisliked = new Boolean[1];
        firebaseDBHelper.isDisliked(EventLog.ACTIVITY_TYPE_MOVIE,String.valueOf(movieModel.getMovieId()),isDisliked, new OnTaskCompletedListener() {
            @Override
            public void onTaskCompleted() {
                if(isDisliked[0]){
                    movieDislikeImageView.setImageResource(R.drawable.dislike_green);
                }
                else {
                    movieDislikeImageView.setImageResource(R.drawable.dislike);
                }
            }
        });

        EventLogOnClickListener listenerMovie = new EventLogOnClickListener(String.valueOf(movieModel.getMovieId()));
        movieLikeImageView.setOnClickListener(listenerMovie);
        movieDislikeImageView.setOnClickListener(listenerMovie);
        movieShareImageView.setOnClickListener(listenerMovie);

        movieTitle.setText(movieModel.getTitle());
        movieOverview.setText(movieModel.getOverview());
        movieRelease_date.setText(movieModel.getRelease_date());
        movieVote_average.setText(movieModel.getVote_average());
        movieLanguage.setText(movieModel.getLanguage());


    }

    void initDisplayForGame(View v){
        ImageView gamePoster = v.findViewById(R.id.fragment_display_game_poster_iv);
        ImageView gameTrailer = v.findViewById(R.id.fragment_display_game_trailer_iv);

        TextView gameTitle = v.findViewById(R.id.fragment_display_game_title_tv);
        TextView gameReleaseDate = v.findViewById(R.id.fragment_display_game_release_date_tv);
        TextView gameRating = v.findViewById(R.id.fragment_display_game_rating_tv);
        TextView gameOverview = v.findViewById(R.id.fragment_display_game_overview_tv);
        TextView gamePopularity = v.findViewById(R.id.fragment_display_game_popularity_tv);

        LinearLayout gameGenre = v.findViewById(R.id.fragment_display_game_genre_ll);
        LinearLayout gameModeName = v.findViewById(R.id.fragment_display_game_modes_name_ll);
        LinearLayout gamePlatform = v.findViewById(R.id.fragment_display_game_platforms_ll);

        gameLikeImageView = v.findViewById(R.id.fragment_display_game_like_iv);
        gameDislikeImageView = v.findViewById(R.id.fragment_display_game_dislike_iv);
        gemeShareImageView = v.findViewById(R.id.fragment_display_game_share_iv);

        if (gameModel.getImage_id() != null){
            Picasso.get().load(Uri.parse("https://images.igdb.com/igdb/image/upload/t_cover_big/"+gameModel.getImage_id()+".jpg")).into(gamePoster);
        } else {
            gamePoster.setImageResource(R.drawable.default_game);
        }


        gameTitle.setText(gameModel.getName());
        gameReleaseDate.setText(gameModel.getRelease_date());
        gameRating.setText(new DecimalFormat ("##.#").format(gameModel.getRating()));
        gameOverview.setText(gameModel.getSummary());
        gamePopularity.setText(new DecimalFormat ("##.#").format(gameModel.getPopularity()));

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

        final Boolean[] isLiked = new Boolean[1];
        firebaseDBHelper.isLiked(EventLog.ACTIVITY_TYPE_GAME,String.valueOf(gameModel.getGameId()),isLiked, new OnTaskCompletedListener() {
            @Override
            public void onTaskCompleted() {
                if(isLiked[0]){
                    gameLikeImageView.setImageResource(R.drawable.like_green);
                }
                else {
                    gameLikeImageView.setImageResource(R.drawable.like);
                }
            }
        });
        final Boolean[] isDisliked = new Boolean[1];
        firebaseDBHelper.isDisliked(EventLog.ACTIVITY_TYPE_GAME,String.valueOf(gameModel.getGameId()),isDisliked, new OnTaskCompletedListener() {
            @Override
            public void onTaskCompleted() {
                if(isDisliked[0]){
                    gameDislikeImageView.setImageResource(R.drawable.dislike_green);
                }
                else {
                    gameDislikeImageView.setImageResource(R.drawable.dislike);
                }
            }
        });

        EventLogOnClickListener listenerGame = new EventLogOnClickListener(String.valueOf(gameModel.getGameId()));
        gameLikeImageView.setOnClickListener(listenerGame);
        gameDislikeImageView.setOnClickListener(listenerGame);
        gemeShareImageView.setOnClickListener(listenerGame);

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
    }

    void initDisplayForActivity(View v, @Nullable Bundle savedInstanceState){
        ImageView activityImage = v.findViewById(R.id.fragment_display_group_poster_iv);
        TextView activityName = v.findViewById(R.id.fragment_display_group_title_tv);
        TextView activityDate = v.findViewById(R.id.fragment_display_group_date_tv);
        TextView activityCategory = v.findViewById(R.id.fragment_display_group_categoty_tv);
        LinearLayout activitySubCategories = v.findViewById(R.id.fragment_display_group_sub_categoty_ll);
        TextView activityDescription = v.findViewById(R.id.fragment_display_group_description_tv);

        activityLikeImageView = v.findViewById(R.id.fragment_display_group_like_iv);
        activityDislikeImageView = v.findViewById(R.id.fragment_display_group_dislike_iv);
        activityShareImageView = v.findViewById(R.id.fragment_display_group_share_iv);

        activityMap = v.findViewById(R.id.fragment_display_group_mapview);
        activityMap.onCreate(savedInstanceState);
        final LatLng location = activityModel.getLocation();

        Picasso.get().load(activityModel.getImageUri()).into(activityImage);

        activityName.setText(activityModel.getName());
        activityDate.setText(activityModel.getDate());
        activityCategory.setText(activityModel.getCategory());
        activityDescription.setText(activityModel.getDescription());

        for(int i = 0; i < activityModel.getSubCategories().size(); i++){
            TextView textView = new TextView(getContext());
            textView.setTextSize(18);
            textView.setText(activityModel.getSubCategories().get(i));
            activitySubCategories.addView(textView);
        }

        activityMap.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;
                mMap.clear();
                mMap.addMarker(new MarkerOptions().title("Selected Place").position(location));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 17f));
            }
        });

        final Boolean[] isLiked = new Boolean[1];
        firebaseDBHelper.isLiked(EventLog.ACTIVITY_TYPE_ACTIVITY,String.valueOf(activityModel.getActivityId()),isLiked, new OnTaskCompletedListener() {
            @Override
            public void onTaskCompleted() {
                if(isLiked[0]){
                    activityLikeImageView.setImageResource(R.drawable.like_green);
                }
                else {
                    activityLikeImageView.setImageResource(R.drawable.like);
                }
            }
        });
        final Boolean[] isDisliked = new Boolean[1];
        firebaseDBHelper.isDisliked(EventLog.ACTIVITY_TYPE_ACTIVITY,String.valueOf(activityModel.getActivityId()),isDisliked, new OnTaskCompletedListener() {
            @Override
            public void onTaskCompleted() {
                if(isDisliked[0]){
                    activityDislikeImageView.setImageResource(R.drawable.dislike_green);
                }
                else {
                    activityDislikeImageView.setImageResource(R.drawable.dislike);
                }
            }
        });

        EventLogOnClickListener listenerActivity = new EventLogOnClickListener(activityModel.getActivityId());
        activityLikeImageView.setOnClickListener(listenerActivity);
        activityDislikeImageView.setOnClickListener(listenerActivity);
        activityShareImageView.setOnClickListener(listenerActivity);
    }
    public void setInitMode(int initMode) {
        this.initMode = initMode;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(initMode == INIT_MODE_GROUP_ACTIVITY){
            activityMap.onResume();
        }
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        ActionBar actionBar = activity.getSupportActionBar();
        actionBar.setTitle(R.string.title_display_activity);
    }

    @Override
    public void onPause() {
        if(initMode == INIT_MODE_GROUP_ACTIVITY) {
            activityMap.onPause();
        }
        super.onPause();
    }

    @Override
    public void onDestroy() {
        if(initMode == INIT_MODE_GROUP_ACTIVITY) {
            activityMap.onDestroy();
        }
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        if(initMode == INIT_MODE_GROUP_ACTIVITY){
            activityMap.onLowMemory();
        }
        super.onLowMemory();
    }
}
