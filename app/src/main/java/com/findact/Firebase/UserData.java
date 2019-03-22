package com.findact.Firebase;

import android.net.Uri;
import android.os.Bundle;

public class UserData {

    private String firstname, lastname, city, birthdate, username, uuidString;
    private String notification;
    private Uri profilePictureUri;

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

    public UserData(Bundle bundle){
           this.firstname = bundle.getString("firstname");
           this.lastname =  bundle.getString("lastname");
           this.city =   bundle.getString("city");
           this.birthdate =  bundle.getString("birthdate");
           this.username =   bundle.getString("username");
           this.uuidString =   bundle.getString("uuidString");
           this.notification =   bundle.getString("notification");
           this.profilePictureUri =  Uri.parse(bundle.getString("profilePictureUri"));
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

    public Bundle UserDatatoBundle(){

        Bundle bundle = new Bundle();
        bundle.putString("firstname",this.firstname);
        bundle.putString("lastname",this.lastname);
        bundle.putString("city",this.city);
        bundle.putString("birthdate",this.birthdate);
        bundle.putString("username",this.username);
        bundle.putString("uuidString",this.uuidString);
        bundle.putString("notification",this.notification);
        bundle.putString("profilePictureUri",this.profilePictureUri.toString());
        return bundle;

    }


}
