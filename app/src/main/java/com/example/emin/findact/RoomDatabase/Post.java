package com.example.emin.findact.RoomDatabase;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;


@Entity(tableName = "post_detail", indices = @Index(value = {"firebaseId"},unique = true))
public class Post {

    @PrimaryKey
    private String firebaseId;

    private String activityId;
    private String overview;
    private String title;
    private String activityType;
    private String releaseDate;
    private Double rating;
    private Double popularity;
    private String imagePath;
    private Long logDate;
    private Boolean like;
    private Boolean dislike;
    private Boolean share;


    private String genres;
    private String modes;
    private String platforms;
    private String videoId;

    private String language;

    private String owner;
    private String attendees;
    private String subCategories;
    private String category;
    private Double latitude;
    private Double longitude;


    public Post(String firebaseId, String activityId, String overview, String title,
                String activityType, String releaseDate, Double rating, Double popularity,
                String imagePath, Long logDate, Boolean like, Boolean dislike, Boolean share,
                String genres, String modes, String platforms, String videoId, String language,
                String owner, String attendees, String subCategories, String category, Double latitude, Double longitude) {

        this.firebaseId = firebaseId;
        this.activityId = activityId;
        this.overview = overview;
        this.title = title;
        this.activityType = activityType;
        this.releaseDate = releaseDate;
        this.rating = rating;
        this.popularity = popularity;
        this.imagePath = imagePath;
        this.logDate = logDate;
        this.like = like;
        this.dislike = dislike;
        this.share = share;
        this.genres = genres;
        this.modes = modes;
        this.platforms = platforms;
        this.videoId = videoId;
        this.language = language;
        this.owner = owner;
        this.attendees = attendees;
        this.subCategories = subCategories;
        this.category = category;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getFirebaseId() {
        return firebaseId;
    }

    public String getActivityId() {
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

    public Double getRating() {
        return rating;
    }

    public Double getPopularity() {
        return popularity;
    }

    public String getImagePath() {
        return imagePath;
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

    public String getModes() {
        return modes;
    }

    public String getPlatforms() {
        return platforms;
    }

    public String getVideoId() {
        return videoId;
    }

    public String getLanguage() {
        return language;
    }

    public String getOwner() {
        return owner;
    }

    public String getAttendees() {
        return attendees;
    }

    public String getSubCategories() {
        return subCategories;
    }

    public String getCategory() {
        return category;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }
}
