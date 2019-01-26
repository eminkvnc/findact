package com.example.emin.findact.Firebase;

public class InitialLog extends UserLog{

    private String gameGenres;
    private String movieGenres;

    public InitialLog(String gameGenres, String movieGenres, String date, String status) {
        super(date, status);
        this.gameGenres = gameGenres;
        this.movieGenres = movieGenres;
    }

    public String getGameGenres() {
        return gameGenres;
    }

    public void setGameGenres(String gameGenres) {
        this.gameGenres = gameGenres;
    }

    public String getMovieGenres() {
        return movieGenres;
    }

    public void setMovieGenres(String movieGenres) {
        this.movieGenres = movieGenres;
    }
}
