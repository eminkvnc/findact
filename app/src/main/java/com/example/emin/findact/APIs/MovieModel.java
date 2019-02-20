package com.example.emin.findact.APIs;

public class MovieModel {

    String title;
    String release_date;
    String genre;
    String vote_average;
    String poster_path;
    String overview;

    public MovieModel(String title, String release_date, String genre, String vote_average, String poster_path, String overview) {
        this.title = title;
        this.release_date = release_date;
        this.genre = genre;
        this.vote_average = vote_average;
        this.poster_path = poster_path;
        this.overview = overview;
    }

    public String getTitle() {
        return title;
    }

    public String getRelease_date() {
        return release_date;
    }

    public String getGenre() {
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
}
