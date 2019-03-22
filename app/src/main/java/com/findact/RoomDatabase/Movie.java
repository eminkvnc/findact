package com.findact.RoomDatabase;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "movie_post_detail", indices = @Index(value = {"firebaseId"},unique = true))

public class Movie {

    @NonNull
    @PrimaryKey
    private String firebaseId;

    @NonNull
    private int activityId;
    @NonNull
    private String senderName;

    @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
    @NonNull
    private byte[] senderImage;
    @NonNull
    private String overview;
    @NonNull
    private String title;
    @NonNull
    private String activityType;
    @NonNull
    private String releaseDate;
    @NonNull
    @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
    private byte[] picturePath;
    @NonNull
    private Long logDate;
    @NonNull
    private Boolean like;
    @NonNull
    private Boolean dislike;
    @NonNull
    private Boolean share;
    @NonNull
    private String genres;
    @NonNull
    private Double rating;
    @NonNull
    private Double popularity;
    @NonNull
    private String language;


    public Movie(@NonNull String firebaseId, int activityId, @NonNull String senderName, @NonNull byte[] senderImage,
                 @NonNull String overview, @NonNull String title, @NonNull String activityType, @NonNull String releaseDate,
                 @NonNull byte[] picturePath, @NonNull Long logDate, @NonNull Boolean like, @NonNull Boolean dislike,
                 @NonNull Boolean share, @NonNull String genres, @NonNull Double rating, @NonNull Double popularity, @NonNull String language) {
        this.firebaseId = firebaseId;
        this.activityId = activityId;
        this.senderName = senderName;
        this.senderImage = senderImage;
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
        this.rating = rating;
        this.popularity = popularity;
        this.language = language;
    }

    @NonNull
    public String getFirebaseId() {
        return firebaseId;
    }

    public int getActivityId() {
        return activityId;
    }

    @NonNull
    public String getSenderName() {
        return senderName;
    }

    @NonNull
    public byte[] getSenderImage() {
        return senderImage;
    }

    @NonNull
    public String getOverview() {
        return overview;
    }

    @NonNull
    public String getTitle() {
        return title;
    }

    @NonNull
    public String getActivityType() {
        return activityType;
    }

    @NonNull
    public String getReleaseDate() {
        return releaseDate;
    }

    @NonNull
    public byte[] getPicturePath() {
        return picturePath;
    }

    @NonNull
    public Long getLogDate() {
        return logDate;
    }

    @NonNull
    public Boolean getLike() {
        return like;
    }

    @NonNull
    public Boolean getDislike() {
        return dislike;
    }

    @NonNull
    public Boolean getShare() {
        return share;
    }

    @NonNull
    public String getGenres() {
        return genres;
    }

    @NonNull
    public Double getRating() {
        return rating;
    }

    @NonNull
    public Double getPopularity() {
        return popularity;
    }

    @NonNull
    public String getLanguage() {
        return language;
    }
}
