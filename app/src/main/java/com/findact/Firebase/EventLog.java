package com.findact.Firebase;

import com.findact.APIs.ActivityModel;
import com.findact.APIs.GameModel;
import com.findact.APIs.MovieModel;

public class EventLog extends UserLog {

    final public static String EVENT_TYPE_LIKE = "Like";
    final public static String EVENT_TYPE_DISLIKE = "Dislike";
    final public static String EVENT_TYPE_SHARE = "Share";
    final public static String EVENT_TYPE_CLICK = "Click";

    public static final String ACTIVITY_TYPE_MOVIE = "Movie";
    public static final String ACTIVITY_TYPE_GAME = "Game";
    public static final String ACTIVITY_TYPE_ACTIVITY = "Activity";

    private MovieModel movieModel;
    private GameModel gameModel;
    private ActivityModel activityModel;
    private String eventType;
    private String activityType;
    private String userRate;

    public EventLog(String id, String date, String userRate, String eventType, String activityType, MovieModel movieModel) {
        super(id,date);
        this.movieModel = movieModel;
        this.eventType = eventType;
        this.activityType = activityType;
        this.userRate = userRate;
    }

    public EventLog(String id, String date, String userRate, String eventType, String activityType, GameModel gameModel) {
        super(id,date);
        this.gameModel = gameModel;
        this.eventType = eventType;
        this.activityType = activityType;
        this.userRate = userRate;
    }

    public EventLog(String id, String date, String userRate, String eventType, String activityType, ActivityModel activityModel) {
        super(id,date);
        this.activityModel = activityModel;
        this.eventType = eventType;
        this.activityType = activityType;
        this.userRate = userRate;
    }


    public MovieModel getMovieModel() {
        return movieModel;
    }

    public GameModel getGameModel() {
        return gameModel;
    }

    public ActivityModel getActivityModel() {
        return activityModel;
    }

    public String getEventType() {
        return eventType;
    }

    public String getActivityType() {
        return activityType;
    }

    public String getUserRate() {
        return userRate;
    }
}
