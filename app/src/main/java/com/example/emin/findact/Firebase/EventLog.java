package com.example.emin.findact.Firebase;

import com.example.emin.findact.APIs.ActivityModel;
import com.example.emin.findact.APIs.GameModel;
import com.example.emin.findact.APIs.MovieModel;

public class EventLog extends UserLog {

    final public static String EVENT_TYPE_LIKE = "Like";
    final public static String EVENT_TYPE_DISLIKE = "Dislike";
    final public static String EVENT_TYPE_SHARE = "Share";
    final public static String EVENT_TYPE_CLICK = "Click";

    final public static String ACTIVITY_TYPE_MOVIE = "Movie";
    final public static String ACTIVITY_TYPE_GAME = "Game";
    final public static String ACTIVITY_TYPE_ACTIVITY = "Activity";

    private MovieModel movieModel;
    private GameModel gameModel;
    private ActivityModel activityModel;
    private String eventType;
    private String activityType;

    public EventLog(String id, String date, String eventType, String activityType, MovieModel movieModel) {
        super(id,date);
        this.movieModel = movieModel;
        this.eventType = eventType;
        this.activityType = activityType;
    }

    public EventLog(String id, String date, String eventType, String activityType, GameModel gameModel) {
        super(id,date);
        this.gameModel = gameModel;
        this.eventType = eventType;
        this.activityType = activityType;
    }

    public EventLog(String id, String date, String eventType, String activityType, ActivityModel activityModel) {
        super(id,date);
        this.activityModel = activityModel;
        this.eventType = eventType;
        this.activityType = activityType;
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
}
