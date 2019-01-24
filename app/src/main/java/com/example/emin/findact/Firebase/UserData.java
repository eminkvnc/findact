package com.example.emin.findact.Firebase;

import android.net.Uri;

public class UserData {

    String name, surname, city, birthDate;
    Uri profilePicture;

    public UserData(String name, String surname, String city, String birthday, Uri profilePicture) {
        this.name = name;
        this.surname = surname;
        this.city = city;
        this.birthDate = birthday;
        this.profilePicture = profilePicture;
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

    public String getBirthDate() {
        return birthDate;
    }

    public Uri getProfilePicture() {
        return profilePicture;
    }
}
