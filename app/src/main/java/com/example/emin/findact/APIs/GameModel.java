package com.example.emin.findact.APIs;

import android.os.Bundle;

import java.util.ArrayList;

public class GameModel {

    private int gameId;
    private String name;
    private ArrayList<String> genre;
    private String release_date;
    private String summary;
    private String image_id;
    private ArrayList<String> game_mode_name;
    private Double rating;
    private ArrayList<String> platform_name;
    private String video_id;
    private Double popularity;

    public GameModel(int gameId,String name,ArrayList<String>  genre, String release_date,
                     String summary, String image_id, ArrayList<String>  game_mode_name,
                     Double rating, ArrayList<String>  platform_name, String video_id,
                     Double popularity) {
        this.gameId = gameId;
        this.name = name;
        this.genre = genre;
        this.release_date = release_date;
        this.summary = summary;
        this.image_id = image_id;
        this.game_mode_name = game_mode_name;
        this.rating = rating;
        this.platform_name = platform_name;
        this.video_id = video_id;
        this.popularity = popularity;
    }

    public GameModel(Bundle bundle){
        this.gameId = bundle.getInt("game_id");
        this.name = bundle.getString("name");
        this.genre = bundle.getStringArrayList("genre");
        this.release_date = bundle.getString("release_date");
        this.summary = bundle.getString("summary");
        this.image_id = bundle.getString("image_id");
        this.game_mode_name = bundle.getStringArrayList("game_mode_name");
        this.rating = bundle.getDouble("rating");
        this.platform_name = bundle.getStringArrayList("platform_name");
        this.video_id = bundle.getString("video_id");
        this.popularity = bundle.getDouble("popularity");

    }

    public Bundle GameDataToBundle(){
        Bundle bundle = new Bundle();

        bundle.putInt("game_id", this.gameId);
        bundle.putString("name", this.name);
        bundle.putStringArrayList("genre",this.genre );
        bundle.putString("release_date",this.release_date );
        bundle.putString("summary",this.summary );
        bundle.putString("image_id",this.image_id );
        bundle.putStringArrayList("game_mode_name",this.game_mode_name );
        bundle.putDouble("rating",this.rating );
        bundle.putStringArrayList("platform_name", this.platform_name);
        bundle.putString("video_id",this.video_id );
        bundle.putDouble("popularity", this.popularity );

        return bundle;
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

    public String getRelease_date() {
        return release_date;
    }

    public String getSummary() {
        return summary;
    }

    public String getImage_id() {
        return image_id;
    }

    public ArrayList<String> getGame_mode_name() {
        return game_mode_name;
    }

    public Double getRating() {
        return rating;
    }

    public ArrayList<String> getPlatform_name() {
        return platform_name;
    }

    public String getVideo_id() {
        return video_id;
    }

    public Double getPopularity() {
        return popularity;
    }

}
