package com.example.emin.findact.Firebase;

import android.net.Uri;

public class UserData {

    String firstname, lastname, city, birthdate, username, uuidString;
    String notification;
    Uri profilePictureUri;

    public UserData(String firstname, String lastname, String city, String birthdate, String username, String uuidString, String notification, Uri profilePictureUri) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.city = city;
        this.birthdate = birthdate;
        this.username = username;
        this.uuidString = uuidString;
        this.notification = notification;
        this.profilePictureUri = profilePictureUri;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public String getCity() {
        return city;
    }

    public String getBirthdate() {
        return birthdate;
    }

    public String getUsername() {
        return username;
    }

    public String getUuidString() {
        return uuidString;
    }

    public String getNotification() {
        return notification;
    }

    public Uri getProfilePictureUri() {
        return profilePictureUri;
    }
}
