package com.example.emin.findact.APIs;

import android.net.Uri;
import android.os.Bundle;

public class PostModel {

    private int postId;
    private String postTitle;
    private String username;
    private Uri userPictureUri;

    private Uri postImageUri;
    private String rating;
    private String description;
    private String date;
    private String category;

    public PostModel(int postId, String postTitle, String username, Uri userPictureUri, Uri postImageUri, String rating, String description, String date, String category) {
        this.postId = postId;
        this.postTitle = postTitle;
        this.username = username;
        this.userPictureUri = userPictureUri;
        this.postImageUri = postImageUri;
        this.rating = rating;
        this.description = description;
        this.date = date;
        this.category = category;
    }

    public PostModel(Bundle bundle){
        this.postId = bundle.getInt("postId");
        this.postTitle = bundle.getString("postTitle");
        this.username = bundle.getString("username");
        this.userPictureUri = Uri.parse(bundle.getString("userPictureUri"));
        this.postImageUri = Uri.parse(bundle.getString("postImageUri"));
        this.rating = bundle.getString("rating");
        this.description = bundle.getString("description");
        this.date = bundle.getString("date");
        this.category = bundle.getString("category");
    }

    public Bundle PostDataToBundle(){
        Bundle bundle = new Bundle();

        bundle.putInt("postId", this.postId);
        bundle.putString("postTitle",this.postTitle );
        bundle.putString("username",this.username );
        bundle.putString("userPictureUri",this.userPictureUri.toString() );
        bundle.putString("postImageUri", this.postImageUri.toString());
        bundle.putString("rating", this.rating );
        bundle.putString("description",this.description );
        bundle.putString("date",this.date );
        bundle.putString("category", this.category);

        return bundle;
    }

    public int getPostId() {
        return postId;
    }

    public String getPostTitle() {
        return postTitle;
    }

    public String getUsername() {
        return username;
    }

    public Uri getUserPicture() {
        return userPictureUri;
    }

    public String getRating() {
        return rating;
    }

    public String getDescription() {
        return description;
    }

    public String getDate() {
        return date;
    }

    public String getCategory() {
        return category;
    }

    public Uri getPostImageUri() {
        return postImageUri;
    }

}
