package com.example.emin.findact.Firebase;

public class EventLog extends UserLog {

    public static String EVENT_TYPE_LIKE = "Like";
    public static String EVENT_TYPE_DISLIKE = "Dislike";
    public static String EVENT_TYPE_SHARE = "Share";
    public static String EVENT_TYPE_CLICK = "Click";

    public static String ACTIVITY_TYPE_MOVIE = "Movie";
    public static String ACTIVITY_TYPE_GAME = "Game";
    public static String ACTIVITY_TYPE_ACTIVITY = "Activity";

    private String eventType;
    private String activityId;
    private String activityType;

    public EventLog(String id, String date, String eventType, String activityId, String activityType) {
        super(id,date);
        this.eventType = eventType;
        this.activityId = activityId;
        this.activityType = activityType;
    }

    public String getEventType() {
        return eventType;
    }

    public String getActivityId() {
        return activityId;
    }

    public String getActivityType() {
        return activityType;
    }
}
