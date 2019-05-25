package com.findact.APIs;

import java.util.ArrayList;

public class ExploreModel {

    public GameModel gameModel;
    public MovieModel movieModel;
    public String modelName;

    public ExploreModel(GameModel gameModel,MovieModel movieModel, String modelName) {
        this.gameModel = gameModel;
        this.movieModel = movieModel;
        this.modelName = modelName;
    }

    public GameModel getGameModel() {
        return gameModel;
    }

    public MovieModel getMovieModel() {
        return movieModel;
    }

    public String getModelName() {
        return modelName;
    }
}
