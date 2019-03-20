package com.example.emin.findact.APIs;

import android.os.Bundle;

import java.util.ArrayList;

public class GameModel {

    private String firebaseId;
    private int gameId;
    private String name;
    private ArrayList<String> genre;
    private String releaseDate;
    private String summary;
    private String imageId;
    private ArrayList<String> gameModeList;
    private Double rating;
    private ArrayList<String> platformList;
    private String videoId;
    private Double popularity;

    public GameModel(String firebaseId, int gameId, String name, ArrayList<String> genre, String releaseDate,
                     String summary, String imageId, ArrayList<String> gameModeList,
                     Double rating, ArrayList<String> platformList, String videoId,
                     Double popularity) {
        this.firebaseId = firebaseId;
        this.gameId = gameId;
        this.name = name;
        this.genre = genre;
        this.releaseDate = releaseDate;
        this.summary = summary;
        this.imageId = imageId;
        this.gameModeList = gameModeList;
        this.rating = rating;
        this.platformList = platformList;
        this.videoId = videoId;
        this.popularity = popularity;
    }

    public GameModel(Bundle bundle){
        this.firebaseId = bundle.getString("firebase_id");
        this.gameId = bundle.getInt("game_id");
        this.name = bundle.getString("name");
        this.genre = bundle.getStringArrayList("genre");
        this.releaseDate = bundle.getString("releaseDate");
        this.summary = bundle.getString("summary");
        this.imageId = bundle.getString("imageId");
        this.gameModeList = bundle.getStringArrayList("gameModeList");
        this.rating = bundle.getDouble("rating");
        this.platformList = bundle.getStringArrayList("platformList");
        this.videoId = bundle.getString("videoId");
        this.popularity = bundle.getDouble("popularity");

    }

    public Bundle GameDataToBundle(){
        Bundle bundle = new Bundle();
        bundle.putString("firebase_id", this.firebaseId);
        bundle.putInt("game_id", this.gameId);
        bundle.putString("name", this.name);
        bundle.putStringArrayList("genre",this.genre );
        bundle.putString("releaseDate",this.releaseDate);
        bundle.putString("summary",this.summary );
        bundle.putString("imageId",this.imageId);
        bundle.putStringArrayList("gameModeList",this.gameModeList);
        bundle.putDouble("rating",this.rating );
        bundle.putStringArrayList("platformList", this.platformList);
        bundle.putString("videoId",this.videoId);
        bundle.putDouble("popularity", this.popularity );

        return bundle;
    }

    public String getFirebaseId() {
        return firebaseId;
    }

    public int getGameId(){
        return gameId;
    }

    public String getName() {
        return name;
    }

    public ArrayList<String> getGenre() {
        return genre;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public String getSummary() {
        return summary;
    }

    public String getImageId() {
        return imageId;
    }

    public ArrayList<String> getGameModeList() {
        return gameModeList;
    }

    public Double getRating() {
        return rating;
    }

    public ArrayList<String> getPlatformList() {
        return platformList;
    }

    public String getVideoId() {
        return videoId;
    }

    public Double getPopularity() {
        return popularity;
    }

}
