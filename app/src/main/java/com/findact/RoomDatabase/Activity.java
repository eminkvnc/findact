package com.findact.RoomDatabase;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "activity_post_detail", indices = @Index(value = {"firebaseId"},unique = true))
public class Activity {

    @NonNull
    @PrimaryKey
    private String firebaseId;
    private String senderName;
    @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
    private byte[] senderImage;
    private String activityId;
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
    private String owner;
    private String attendees;
    private String subCategories;
    private String category;
    private Double latitude;
    private Double longitude;

    public Activity(@NonNull String firebaseId,String senderName, byte[] senderImage, String activityId, String overview,
                    String title, String activityType, String releaseDate,
                    byte[] picturePath, Long logDate, Boolean like, Boolean dislike,
                    Boolean share, String owner, String attendees, String subCategories,
                    String category, Double latitude, Double longitude) {
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
        this.owner = owner;
        this.attendees = attendees;
        this.subCategories = subCategories;
        this.category = category;
        this.latitude = latitude;
        this.longitude = longitude;
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

    public String getActivityId() {
        return activityId;
    }

    public String getOverview() {
        return overview;
    }

    public byte[] getPicturePath() {
        return picturePath;
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
