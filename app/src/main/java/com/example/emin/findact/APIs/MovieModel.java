package com.example.emin.findact.APIs;

import java.util.ArrayList;

public class MovieModel {

    ArrayList<String> title;
    ArrayList<String> release_date;
    ArrayList<String> genre;
    ArrayList<Integer> popularity;
    ArrayList<Double> vote_average;
    ArrayList<String> poster_path;
    ArrayList<Boolean> adult;

    public MovieModel(ArrayList<String> title, ArrayList<String> release_date,
                      ArrayList<String> genre, ArrayList<Integer> popularity,
                      ArrayList<Double> vote_average, ArrayList<String> poster_path, ArrayList<Boolean> adult) {
        this.title = title;
        this.release_date = release_date;
        this.genre = genre;
        this.popularity = popularity;
        this.vote_average = vote_average;
        this.poster_path = poster_path;
        this.adult = adult;
    }

    public ArrayList<String> getTitle() {
        return title;
    }

    public ArrayList<String> getRelease_date() {
        return release_date;
    }

    public ArrayList<String> getGenre() {
        return genre;
    }

    public ArrayList<Integer> getPopularity() {
        return popularity;
    }

    public ArrayList<Double> getVote_average() {
        return vote_average;
    }

    public ArrayList<String> getPoster_path() {
        return poster_path;
    }

    public ArrayList<Boolean> getAdult() {
        return adult;
    }
}
