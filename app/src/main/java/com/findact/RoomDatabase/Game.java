package com.example.emin.findact.RoomDatabase;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "game_post_detail", indices = @Index(value = {"firebaseId"},unique = true))
public class Game {

    @NonNull
    @PrimaryKey
    private String firebaseId;

    private String senderName;
    @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
    private byte[] senderImage;
    private int activityId;
    private String overview;
    private String title;
    private String activityType;
    private String releaseDate;
    @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
    private byte[] picturePath;
    private Long logDate;
    private Boolean like;
    private Boolean dislike;
    private Boolean share;
    private String genres;
    private String videoId;
    private Double rating;
    private Double popularity;
    private String platforms;
    private String modes;

    public Game(@NonNull String firebaseId,String senderName,byte[] senderImage, int activityId, String overview,
                String title, String activityType, String releaseDate,
                byte[] picturePath, Long logDate, Boolean like, Boolean dislike,
                Boolean share, String genres, String videoId, Double rating,
                Double popularity, String platforms, String modes) {
        this.firebaseId = firebaseId;
        this.senderName = senderName;
        this.senderImage = senderImage;
        this.activityId = activityId;
        this.overview = overview;
        this.title = title;
        this.activityType = activityType;
        this.releaseDate = releaseDate;
        this.picturePath = picturePath;
        this.logDate = logDate;
        this.like = like;
        this.dislike = dislike;
        this.share = share;
        this.genres = genres;
        this.videoId = videoId;
        this.rating = rating;
        this.popularity = popularity;
        this.platforms = platforms;
        this.modes = modes;
    }

    @NonNull
    public String getFirebaseId() {
        return firebaseId;
    }

    public String getSenderName() {
        return senderName;
    }

    public byte[] getSenderImage() {
        return senderImage;
    }

    public int getActivityId() {
        return activityId;
    }

    public String getOverview() {
        return overview;
    }

    public String getTitle() {
        return title;
    }

    public String getActivityType() {
        return activityType;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public byte[] getPicturePath() {
        return picturePath;
    }

    public Long getLogDate() {
        return logDate;
    }

    public Boolean getLike() {
        return like;
    }

    public Boolean getDislike() {
        return dislike;
    }

    public Boolean getShare() {
        return share;
    }

    public String getGenres() {
        return genres;
    }

    public String getVideoId() {
        return videoId;
    }

    public Double getRating() {
        return rating;
    }

    public Double getPopularity() {
        return popularity;
    }

    public String getPlatforms() {
        return platforms;
    }

    public String getModes() {
        return modes;
    }
}
