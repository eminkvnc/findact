package com.example.emin.findact.APIs;

import android.os.Bundle;

import java.util.ArrayList;

public class MovieModel {

    private String firebaseId;
    private int movieId;
    private String title;
    private String release_date;
    private ArrayList<String> genre;
    private String vote_average;
    private String poster_path;
    private String overview;
    private String language;


    public MovieModel(String firebaseId, int movieId, String title, String release_date, ArrayList<String> genre, String vote_average, String poster_path, String overview, String language) {
        this.firebaseId = firebaseId;
        this.movieId = movieId;
        this.title = title;
        this.release_date = release_date;
        this.genre = genre;
        this.vote_average = vote_average;
        this.poster_path = poster_path;
        this.overview = overview;
        this.language = language;
    }

    public MovieModel(Bundle bundle){
        this.firebaseId = bundle.getString("firebase-id");
        this.movieId = bundle.getInt("movie_id");
        this.title = bundle.getString("title");
        this.release_date = bundle.getString("release_date");
        this.genre = bundle.getStringArrayList("genre");
        this.vote_average = bundle.getString("vote_average");
        this.poster_path = bundle.getString("poster_path");
        this.overview = bundle.getString("overview");
        this.language = bundle.getString("language");
    }

    public String getFirebaseId() {
        return firebaseId;
    }

    public int getMovieId(){
        return movieId;
    }

    public String getTitle() {
        return title;
    }

    public String getRelease_date() {
        return release_date;
    }

    public ArrayList<String> getGenre() {
        return genre;
    }

    public String getVote_average() {
        return vote_average;
    }

    public String getPoster_path() {
        return poster_path;
    }

    public String getOverview() {
        return overview;
    }

    public String getLanguage(){
        return language;
    }

    public Bundle MovieDataToBundle(){
        Bundle bundle = new Bundle();
        bundle.putString("firebase-id", this.firebaseId);
        bundle.putInt("movie_id",this.movieId);
        bundle.putString("title", this.title);
        bundle.putString("release_date", this.release_date);
        bundle.putStringArrayList("genre", this.genre);
        bundle.putString("vote_average", this.vote_average);
        bundle.putString("poster_path", this.poster_path);
        bundle.putString("overview", this.overview);
        bundle.putString("language", this.language);

        return bundle;

    }
}
