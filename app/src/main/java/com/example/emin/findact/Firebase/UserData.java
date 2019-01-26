package com.example.emin.findact.Firebase;

import android.net.Uri;

public class UserData {

    String name, surname, city, birthdate;
    String notification;
    Uri profilePicture;

    public UserData(String name, String surname, String city, String birthdate, Uri profilePicture, String notification) {
        this.name = name;
        this.surname = surname;
        this.city = city;
        this.birthdate = birthdate;
        this.profilePicture = profilePicture;
        this.notification = notification;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public String getCity() {
        return city;
    }

    public String getBirthdate() {
        return birthdate;
    }

    public Uri getProfilePicture() {
        return profilePicture;
    }

    public String getNotification() {
        return notification;
    }
}
