package com.example.emin.findact.Firebase;

public class EventLog extends UserLog {

    private String eventType;
    private String eventWeight;

    EventLog(String date, String status, String eventType, String eventWeight) {
        super(date, status);
        this.eventType = eventType;
        this.eventWeight = eventWeight;
    }

    public String getEventType() {
        return eventType;
    }

    public String getEventWeight() {
        return eventWeight;
    }
}
