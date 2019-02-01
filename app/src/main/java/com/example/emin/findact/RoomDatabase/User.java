package com.example.emin.findact.RoomDatabase;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;


@Entity(tableName = "user_detail")
public class User {

    @NonNull
    @PrimaryKey(autoGenerate = true)
    private int userId;

    @NonNull
    @ColumnInfo(name = "name")
    private String name;

    @NonNull
    @ColumnInfo(name = "surname")
    private String surname;

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

    public User(int userId, @NonNull String name, @NonNull String surname, @NonNull String city, @NonNull String birthday, @NonNull String pictureUri, @NonNull String notification) {
        this.userId = userId;
        this.name = name;
        this.surname = surname;
        this.city = city;
        this.birthday = birthday;
        this.pictureUri = pictureUri;
        this.notification = notification;
    }

    public int getUserId() {
        return userId;
    }

    @NonNull
    public String getName() {
        return name;
    }

    @NonNull
    public String getSurname() {
        return surname;
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
}
