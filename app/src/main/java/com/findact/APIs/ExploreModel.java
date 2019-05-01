package com.findact.APIs;

import java.util.ArrayList;

public class ExploreModel {

    public ArrayList<GameModel> gameModel;
    public ArrayList<MovieModel> movieModel;
    public String modelName;

    public ExploreModel(ArrayList<GameModel> gameModel, ArrayList<MovieModel> movieModel, String modelName) {
        this.gameModel = gameModel;
        this.movieModel = movieModel;
        this.modelName = modelName;
    }

    public ArrayList<GameModel> getGameModel() {
        return gameModel;
    }

    public ArrayList<MovieModel> getMovieModel() {
        return movieModel;
    }

    public String getModelName() {
        return modelName;
    }
}
