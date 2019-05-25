package com.findact;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import com.findact.APIs.ActivityModel;
import com.findact.APIs.GameModel;
import com.findact.APIs.MovieModel;
import com.findact.Firebase.EventLog;
import com.findact.Firebase.FirebaseDBHelper;
import com.findact.Firebase.UserData;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.collection.LLRBNode;
import com.squareup.picasso.Picasso;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class DisplayActivityFragment extends Fragment {

    FirebaseDBHelper firebaseDBHelper = FirebaseDBHelper.getInstance();
    FirebaseDBHelper firebaseDBHelper2 = new FirebaseDBHelper(getContext());
    public static final int INIT_MODE_MOVIE_ACTIVITY = 0;
    public static final int INIT_MODE_GAME_ACTIVITY = 1;
    public static final int INIT_MODE_GROUP_ACTIVITY = 2;

    private int initMode;
    private View v;
    public static String TAG = "DisplayActivityFragment";
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
    ImageView gameShareImageView;

    ImageView activityLikeImageView;
    ImageView activityDislikeImageView;
    ImageView activityShareImageView;

    TextView movieUserRate;
    SeekBar movieRateSeekBar;
    Button movieVoteButton;

    TextView gameUserRate;
    SeekBar gameRateSeekBar;
    Button gameVoteButton;

    double userVote;

    public  DisplayActivityFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainActivity.setDisplayingFragment(DisplayActivityFragment.TAG);
        //setHasOptionsMenu(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
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

        EventLogOnClickListener(String activityId) {
            this.activityId = activityId;
        }

        final Boolean[] isLiked = new Boolean[1];
        final Boolean[] isDisliked = new Boolean[1];

        @Override
        public void onClick(View v) {
            EventLog eventLog;
            long time = Calendar.getInstance().getTimeInMillis() + (3600*3*1000);
            switch (v.getId()){

                // MOVIE
                case R.id.fragment_display_movie_like_iv:
                    eventLog = new EventLog(activityId,
                            Calendar.getInstance().getTime().toString(),
                            null,
                            EventLog.EVENT_TYPE_LIKE,
                            EventLog.ACTIVITY_TYPE_MOVIE,
                            movieModel);
                    firebaseDBHelper.addEventUserLog(eventLog);
                    firebaseDBHelper.isLiked(activityId, isLiked, new OnTaskCompletedListener() {
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
                    eventLog = new EventLog(activityId,
                            Calendar.getInstance().getTime().toString(),
                            null,
                            EventLog.EVENT_TYPE_DISLIKE,
                            EventLog.ACTIVITY_TYPE_MOVIE,
                            movieModel);
                    firebaseDBHelper.addEventUserLog(eventLog);
                    firebaseDBHelper.isDisliked(activityId, isDisliked, new OnTaskCompletedListener() {
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
                    movieShareImageView.setImageResource(R.drawable.share_green);
                    eventLog = new EventLog(activityId,
                            Long.valueOf(time).toString(),
                            null,
                            EventLog.EVENT_TYPE_SHARE,
                            EventLog.ACTIVITY_TYPE_MOVIE,
                            movieModel);
                    firebaseDBHelper.addEventUserLog(eventLog);
                    break;
                case R.id.fragment_display_movie_vote_btn:
                    final int id[] = new int[1];
                    movieVoteButton.setBackgroundColor(Color.rgb(0,133 ,119 ));
                    eventLog = new EventLog(activityId,
                            Long.valueOf(time).toString(),
                            movieUserRate.getText().toString(),
                            "Vote",
                            EventLog.ACTIVITY_TYPE_MOVIE,
                            movieModel);
                    firebaseDBHelper.addEventUserLog(eventLog);
                    firebaseDBHelper.getCurrentUserIntId(id, new OnTaskCompletedListener() {
                        @Override
                        public void onTaskCompleted() {
                            firebaseDBHelper2.addRateLog(id[0],
                                    String.valueOf(movieModel.getMovieId()),
                                    movieUserRate.getText().toString(),
                                    EventLog.ACTIVITY_TYPE_MOVIE);
                        }
                    });

                    break;

                    // GAME
                case R.id.fragment_display_game_like_iv:
                    gameLikeImageView.setImageResource(R.drawable.like_green);
                    gameDislikeImageView.setImageResource(R.drawable.dislike);
                    eventLog = new EventLog(activityId,
                            Calendar.getInstance().getTime().toString(),
                            null,
                            EventLog.EVENT_TYPE_LIKE,
                            EventLog.ACTIVITY_TYPE_GAME,
                            gameModel);
                    firebaseDBHelper.addEventUserLog(eventLog);
                    firebaseDBHelper.isLiked(activityId, isLiked, new OnTaskCompletedListener() {
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
                    eventLog = new EventLog(activityId,
                            Calendar.getInstance().getTime().toString(),
                            null,
                            EventLog.EVENT_TYPE_DISLIKE,
                            EventLog.ACTIVITY_TYPE_GAME,
                            gameModel);
                    firebaseDBHelper.addEventUserLog(eventLog);
                    firebaseDBHelper.isDisliked(activityId, isDisliked, new OnTaskCompletedListener() {
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
                    gameShareImageView.setImageResource(R.drawable.share_green);
                    eventLog = new EventLog(activityId,
                            Long.valueOf(time).toString(),
                            null,
                            EventLog.EVENT_TYPE_SHARE,
                            EventLog.ACTIVITY_TYPE_GAME,
                            gameModel);
                    firebaseDBHelper.addEventUserLog(eventLog);
                    break;
                case R.id.fragment_display_game_vote_btn:
                    final int g_id[] = new int[1];
                    gameVoteButton.setBackgroundColor(Color.rgb(0,133 ,119 ));
                    eventLog = new EventLog(activityId,
                            Long.valueOf(time).toString(),
                            gameUserRate.getText().toString(),
                            "Vote",
                            EventLog.ACTIVITY_TYPE_GAME,
                            gameModel);
                    firebaseDBHelper.addEventUserLog(eventLog);
                    firebaseDBHelper.getCurrentUserIntId(g_id, new OnTaskCompletedListener() {
                        @Override
                        public void onTaskCompleted() {
                            firebaseDBHelper2.addGameRateLog(g_id[0],
                                    String.valueOf(gameModel.getGameId()),
                                    gameUserRate.getText().toString(),
                                    EventLog.ACTIVITY_TYPE_GAME);
                        }
                    });
                    break;
                    ///// ACTIVITY
                case R.id.fragment_display_group_like_iv:
                    activityLikeImageView.setImageResource(R.drawable.like_green);
                    activityDislikeImageView.setImageResource(R.drawable.dislike);
                    eventLog = new EventLog(activityId,
                            Calendar.getInstance().getTime().toString(),
                            null,
                            EventLog.EVENT_TYPE_LIKE,
                            EventLog.ACTIVITY_TYPE_ACTIVITY,
                            activityModel); //Like ve dislike oranına göre rating belirlenecek.
                    firebaseDBHelper.addEventUserLog(eventLog);
                    firebaseDBHelper.isLiked(activityId, isLiked, new OnTaskCompletedListener() {
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
                    eventLog = new EventLog(activityId,
                            Calendar.getInstance().getTime().toString(),
                            null,
                            EventLog.EVENT_TYPE_DISLIKE,
                            EventLog.ACTIVITY_TYPE_ACTIVITY,
                            activityModel); //Like ve dislike oranına göre rating belirlenecek.
                    firebaseDBHelper.addEventUserLog(eventLog);
                    firebaseDBHelper.isDisliked(activityId, isDisliked, new OnTaskCompletedListener() {
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
                    movieShareImageView.setImageResource(R.drawable.share_green);
                    eventLog = new EventLog(activityId,
                            Long.valueOf(time).toString(),
                            null,
                            EventLog.EVENT_TYPE_SHARE,
                            EventLog.ACTIVITY_TYPE_ACTIVITY,
                            activityModel); //Like ve dislike oranına göre rating belirlenecek.
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

        movieRateSeekBar = v.findViewById(R.id.fragment_display_movie_rate_seekBar);
        movieUserRate = v.findViewById(R.id.fragment_display_movie_userVote_tv);
        movieVoteButton = v.findViewById(R.id.fragment_display_movie_vote_btn);

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
        firebaseDBHelper.isLiked(String.valueOf(movieModel.getFirebaseId()),isLiked, new OnTaskCompletedListener() {
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
        firebaseDBHelper.isDisliked(movieModel.getFirebaseId(),isDisliked, new OnTaskCompletedListener() {
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

        final Boolean[] isShared = new Boolean[1];
        firebaseDBHelper.isShared(movieModel.getFirebaseId(), isShared, new OnTaskCompletedListener() {
            @Override
            public void onTaskCompleted() {
                if (isShared[0]){
                    movieShareImageView.setImageResource(R.drawable.share_green);
                } else {
                    movieShareImageView.setImageResource(R.drawable.share);
                }
            }
        });

        final String[] isRated = new String[1];
        firebaseDBHelper.isRated(String.valueOf(movieModel.getFirebaseId()), isRated, new OnTaskCompletedListener() {
            @Override
            public void onTaskCompleted() {
                if (Double.valueOf(isRated[0])> 0.0 || isRated[0] != null){
                    movieUserRate.setText(String.valueOf(isRated[0]));
                    movieRateSeekBar.setProgress((int)(Double.valueOf(isRated[0])*2));
                    movieVoteButton.setBackgroundColor(Color.rgb(0,133 ,119 ));
                } else {
                    movieUserRate.setText("0.0");
                    movieRateSeekBar.setProgress(0);
                }
            }
        });

        EventLogOnClickListener listenerMovie = new EventLogOnClickListener(movieModel.getFirebaseId());
        movieLikeImageView.setOnClickListener(listenerMovie);
        movieDislikeImageView.setOnClickListener(listenerMovie);
        movieShareImageView.setOnClickListener(listenerMovie);
        movieVoteButton.setOnClickListener(listenerMovie);
        movieTitle.setText(movieModel.getTitle());
        movieOverview.setText(movieModel.getOverview());
        movieRelease_date.setText(movieModel.getRelease_date());
        movieVote_average.setText(movieModel.getVote_average());
        movieLanguage.setText(movieModel.getLanguage());

        movieRateSeekBar.setMax(10);
        movieRateSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                userVote = (double) i / 2;
                movieUserRate.setText(String.valueOf(userVote));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

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
        gameShareImageView = v.findViewById(R.id.fragment_display_game_share_iv);

        gameRateSeekBar = v.findViewById(R.id.fragment_display_game_rate_seekBar);
        gameUserRate = v.findViewById(R.id.fragment_display_game_userVote_tv);
        gameVoteButton = v.findViewById(R.id.fragment_display_game_vote_btn);

        gameRateSeekBar.setMax(10);
        gameRateSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                userVote = (double) i / 2;
                gameUserRate.setText(String.valueOf(userVote));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        if (gameModel.getImageId() != null){
            Picasso.get().load(Uri.parse("https://images.igdb.com/igdb/image/upload/t_cover_big/"+gameModel.getImageId()+".jpg")).into(gamePoster);
        } else {
            gamePoster.setImageResource(R.drawable.default_game);
        }


        gameTitle.setText(gameModel.getName());
        gameReleaseDate.setText(gameModel.getReleaseDate());
        gameRating.setText(new DecimalFormat ("##.#").format(gameModel.getRating()));
        gameOverview.setText(gameModel.getSummary());
        gamePopularity.setText(new DecimalFormat ("##.#").format(gameModel.getPopularity()));

        gameTrailer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (gameModel.getVideoId() != null){
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:"+gameModel.getVideoId()));
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/results?search_query="+gameModel.getName()+" trailer"));
                    startActivity(intent);
                }
            }
        });

        final Boolean[] isLiked = new Boolean[1];
        firebaseDBHelper.isLiked(String.valueOf(gameModel.getFirebaseId()),isLiked, new OnTaskCompletedListener() {
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
        firebaseDBHelper.isDisliked(gameModel.getFirebaseId(),isDisliked, new OnTaskCompletedListener() {
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

        final Boolean[] isShared = new Boolean[1];
        firebaseDBHelper.isShared(gameModel.getFirebaseId(), isShared, new OnTaskCompletedListener() {
            @Override
            public void onTaskCompleted() {
                if (isShared[0]){
                    gameShareImageView.setImageResource(R.drawable.share_green);
                } else {
                    gameShareImageView.setImageResource(R.drawable.share);
                }
            }
        });

        final String[] isRated = new String[1];
        firebaseDBHelper.isRated(String.valueOf(gameModel.getFirebaseId()), isRated, new OnTaskCompletedListener() {
            @Override
            public void onTaskCompleted() {
                if (Double.valueOf(isRated[0])> 0.0){
                    gameUserRate.setText(String.valueOf(isRated[0]));
                    gameRateSeekBar.setProgress((int)(Double.valueOf(isRated[0])*2));
                    gameVoteButton.setBackgroundColor(Color.rgb(0,133 ,119 ));
                } else {
                    gameUserRate.setText("0.0");
                    gameRateSeekBar.setProgress(0);
                }
            }
        });

        EventLogOnClickListener listenerGame = new EventLogOnClickListener(gameModel.getFirebaseId());
        gameLikeImageView.setOnClickListener(listenerGame);
        gameDislikeImageView.setOnClickListener(listenerGame);
        gameShareImageView.setOnClickListener(listenerGame);
        gameVoteButton.setOnClickListener(listenerGame);

        gameGenre.removeAllViews();
        for (int i = 0; i < gameModel.getGenre().size(); i++){
            TextView textView = new TextView(getContext());
            textView.setText(gameModel.getGenre().get(i));
            textView.setTextSize(18);
            gameGenre.addView(textView);
        }

        gameModeName.removeAllViews();
        for (int i = 0; i < gameModel.getGameModeList().size(); i++){
            TextView textView = new TextView(getContext());
            textView.setText(gameModel.getGameModeList().get(i));
            textView.setTextSize(18);
            gameModeName.addView(textView);
        }

        gamePlatform.removeAllViews();
        for (int i = 0; i < gameModel.getPlatformList().size(); i++){
            TextView textView = new TextView(getContext());
            textView.setText(gameModel.getPlatformList().get(i));
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
        Button attendeesButton = v.findViewById(R.id.fragment_display_group_attendees_btn);
        final ImageView joinImageView = v.findViewById(R.id.fragment_display_group_join_iv);
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
        firebaseDBHelper.isLiked(activityModel.getActivityId(),isLiked, new OnTaskCompletedListener() {
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
        firebaseDBHelper.isDisliked(activityModel.getActivityId(),isDisliked, new OnTaskCompletedListener() {
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

        final String activityId = activityModel.getActivityId();
        joinImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseDBHelper.joinActivity(activityId,firebaseDBHelper.getCurrentUser());
                joinImageView.setVisibility(View.GONE);
            }
        });

        attendeesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ArrayList<UserData> attendees = new ArrayList<>();
                firebaseDBHelper.getAttendees(activityModel.getActivityId(), attendees, new OnTaskCompletedListener() {
                    @Override
                    public void onTaskCompleted() {
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                UsersListDialog dialog = new UsersListDialog();
                                Bundle bundle = new Bundle();
                                bundle.putString("Title",getResources().getText(R.string.attendees).toString());
                                Bundle attendeesBundle = new Bundle();
                                for(int i = 0; i < attendees.size(); i++){
                                    attendeesBundle.putBundle(String.valueOf(i),attendees.get(i).UserDatatoBundle());
                                }
                                bundle.putBundle("UserDataArrayList",attendeesBundle);
                                bundle.putIntegerArrayList("StatusArrayList",null);
                                bundle.putIntegerArrayList("Attendees",null);
                                dialog.setArguments(bundle);
                                dialog.show(getActivity().getSupportFragmentManager(),"dialog");
                            }
                        },500);
                    }
                });

            }
        });

    }
    public void setInitMode(int initMode) {
        this.initMode = initMode;
    }

    @Override
    public void onResume() {
        super.onResume();
        MainActivity.setDisplayingFragment(DisplayActivityFragment.TAG);
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
