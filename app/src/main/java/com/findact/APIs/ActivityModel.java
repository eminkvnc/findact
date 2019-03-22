package com.example.emin.findact.APIs;

import android.net.Uri;
import android.os.Bundle;

import com.google.android.gms.maps.model.LatLng;
import java.util.ArrayList;

public class ActivityModel {

    private String activityId;
    private String name;
    private Uri imageUri;
    private LatLng location;
    private String date;
    private String category;
    private ArrayList<String> subCategories;
    private ArrayList<String> attendees;
    private String description;
    private String owner;

    public ActivityModel(String activityId, String name, Uri imageUri, LatLng location, String date, String category, ArrayList<String> subCategories, ArrayList<String> attendees, String description, String owner) {
        this.activityId = activityId;
        this.name = name;
        this.imageUri = imageUri;
        this.location = location;
        this.date = date;
        this.category = category;
        this.subCategories = subCategories;
        this.attendees = attendees;
        this.description = description;
        this.owner = owner;
    }

    public ActivityModel(Bundle bundle){

        this.activityId = bundle.getString("activity_id");
        this.name = bundle.getString("name");
        this.imageUri = Uri.parse(bundle.getString("image_uri"));
        this.location = new LatLng(bundle.getDouble("latitude"),bundle.getDouble("longitude"));
        this.date = bundle.getString("date");
        this.category = bundle.getString("category");
        this.subCategories = bundle.getStringArrayList("sub_categories");
        this.attendees = bundle.getStringArrayList("attendees");
        this.description = bundle.getString("description");
        this.owner = bundle.getString("owner");
    }

    public String getActivityId() {
        return activityId;
    }

    public String getName() {
        return name;
    }

    public Uri getImageUri() {
        return imageUri;
    }

    public LatLng getLocation() {
        return location;
    }

    public String getDate() {
        return date;
    }

    public String getCategory() {
        return category;
    }

    public ArrayList<String> getSubCategories() {
        return subCategories;
    }

    public ArrayList<String> getAttendees() {
        return attendees;
    }

    public String getDescription() {
        return description;
    }

    public String getOwner() {
        return owner;
    }

    public Bundle activityDataToBundle(){
        Bundle bundle = new Bundle();

        bundle.putString("activity_id",this.activityId);
        bundle.putString("name",this.name);
        bundle.putString("image_uri",this.imageUri.toString());
        bundle.putDouble("latitude",this.location.latitude);
        bundle.putDouble("longitude",this.location.longitude);
        bundle.putString("date",this.date);
        bundle.putString("category",this.category);
        bundle.putStringArrayList("sub_categories",this.subCategories);
        bundle.putStringArrayList("attendees",this.attendees);
        bundle.putString("description",this.description);
        bundle.putString("owner",this.owner);

        return bundle;
    }

}
