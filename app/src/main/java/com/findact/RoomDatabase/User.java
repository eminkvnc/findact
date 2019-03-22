package com.example.emin.findact.RoomDatabase;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;



@Entity(tableName = "user_detail", indices = @Index(value = {"uuid"}, unique = true))
public class User {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "uuid")
    private String uuid;

    @NonNull
    @ColumnInfo(name = "name")
    private String firstname;

    @NonNull
    @ColumnInfo(name = "surname")
    private String lastname;

    @NonNull
    @ColumnInfo(name = "city")
    private String city;

    @NonNull
    @ColumnInfo(name = "birthday")
    private String birthday;

    @NonNull
    @ColumnInfo(name = "pictureUri")
    private String pictureUri;

    @NonNull
    @ColumnInfo(name = "notification")
    private String notification;

    @NonNull
    @ColumnInfo(name = "username")
    private String username;

    public User(@NonNull String uuid, @NonNull String firstname,
                @NonNull String lastname, @NonNull String city, @NonNull String birthday,
                @NonNull String pictureUri, @NonNull String notification, @NonNull String username) {
        this.uuid = uuid;
        this.firstname = firstname;
        this.lastname = lastname;
        this.city = city;
        this.birthday = birthday;
        this.pictureUri = pictureUri;
        this.notification = notification;
        this.username = username;
    }

    @NonNull
    public String getUuid() {
        return uuid;
    }

    @NonNull
    public String getFirstname() {
        return firstname;
    }

    @NonNull
    public String getLastname() {
        return lastname;
    }

    @NonNull
    public String getCity() {
        return city;
    }

    @NonNull
    public String getBirthday() {
        return birthday;
    }

    @NonNull
    public String getPictureUri() {
        return pictureUri;
    }

    @NonNull
    public String getNotification() {
        return notification;
    }

    @NonNull
    public String getUsername() {
        return username;
    }
}




