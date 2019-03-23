package com.findact.APIs;

import android.os.Bundle;

import com.findact.Firebase.UserData;

public class PostModel {

    public static String MODEL_TYPE_MOVIE = "Movie";
    public static String MODEL_TYPE_GAME = "Post";
    public static String MODEL_TYPE_ACTIVTY = "Activity";


    private UserData userModel;
    private GameModel gameModel;
    private MovieModel movieModel;
    private ActivityModel activityModel;
    private int requestStatus;
    private String modelType;
    private Long shareDate;

    public PostModel(UserData userModel, GameModel gameModel, MovieModel movieModel, ActivityModel activityModel, int requestStatus, String modelType, Long shareDate) {
        this.userModel = userModel;
        this.gameModel = gameModel;
        this.movieModel = movieModel;
        this.activityModel = activityModel;
        this.requestStatus = requestStatus;
        this.modelType = modelType;
        this.shareDate = shareDate;
    }

    public PostModel(Bundle bundle){
        this.userModel = bundle.getParcelable("UserModel");
        this.gameModel = bundle.getParcelable("GameData");
        this.movieModel = bundle.getParcelable("MovieData");
        this.activityModel = bundle.getParcelable("ActivityData");
        this.requestStatus = bundle.getInt("RequestStatus");
        this.modelType = bundle.getString("modelType");
        this.shareDate = bundle.getLong("shareDate");
    }

    public Bundle PostModelToBundle(){
        Bundle bundle = new Bundle();

        bundle.putParcelable("GameData",this.gameModel.GameDataToBundle());
        bundle.putParcelable("MovieData", this.movieModel.MovieDataToBundle());
        bundle.putParcelable("ActivityData", this.activityModel.activityDataToBundle());
        bundle.putParcelable("UserModel", this.userModel.UserDatatoBundle() );
        bundle.putInt("RequestStatus", this.requestStatus );
        bundle.putString("modelType",this.modelType);
        bundle.putLong("shareDate",this.shareDate );
        return bundle;
    }

    public GameModel getGameModel() {
        return gameModel;
    }

    public MovieModel getMovieModel() {
        return movieModel;
    }

    public ActivityModel getActivityModel() {
        return activityModel;
    }

    public UserData getUserModel() {
        return userModel;
    }

    public int getRequestStatus() {
        return requestStatus;
    }

    public String getModelType() {
        return modelType;
    }

    public Long getShareDate() {
        return shareDate;
    }


}
