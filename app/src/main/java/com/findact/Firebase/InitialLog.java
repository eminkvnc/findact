package com.findact.Firebase;

public class InitialLog extends UserLog {

    private String gameGenres;
    private String movieGenres;

    public InitialLog(String gameGenres, String movieGenres, String id , String date) {
        super(id,date);
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
