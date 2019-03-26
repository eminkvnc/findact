package com.findact.APIs;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;

import com.findact.Firebase.EventLog;
import com.findact.Firebase.UserData;
import com.findact.RoomDatabase.Post;
import com.findact.RoomDatabase.UserDatabase;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.UUID;

public class PostModel {

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

    // resimler için download task yap
    public void addPostToRoomDatabase(final Context context){
        switch (modelType){
            case EventLog.ACTIVITY_TYPE_MOVIE:

                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        if (!UserDatabase.getInstance(context).getPostDao().getItemId(movieModel.getFirebaseId())
                                || !UserDatabase.getInstance(context).getPostDao().getSenderName(userModel.getUsername())) {

                            String movieGenres = "";
                            for(int i = 0; i < movieModel.getGenre().size(); i++){
                                movieGenres += movieModel.getGenre().get(i);
                                if(i != movieModel.getGenre().size()-1){
                                    movieGenres += ",";
                                }
                            }
                            // For Sender Image
                            Bitmap bitmap5 = null;
                            try {
                                bitmap5 = Picasso.get().load(userModel.getProfilePictureUri().toString()).get();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            ByteArrayOutputStream outputStream5 = new ByteArrayOutputStream();
                            bitmap5.compress(Bitmap.CompressFormat.JPEG, 100, outputStream5);
                            byte[] senderImage3 = outputStream5.toByteArray();
                            // For Post Image
                            Bitmap bitmap6 = null;
                            try {
                                bitmap6 = Picasso.get().load("http://image.tmdb.org/t/p/w185/" + movieModel.getPoster_path()).get();
                            }catch (IOException e) {
                                e.printStackTrace();
                            }
                            ByteArrayOutputStream outputStream6 = new ByteArrayOutputStream();
                            bitmap6.compress(Bitmap.CompressFormat.JPEG, 100, outputStream6);
                            byte[] postImage3 = outputStream6.toByteArray();
                            // Room Database
                            Post movie = new Post(UUID.randomUUID().toString(),
                                    movieModel.getFirebaseId(),
                                    userModel.getUsername(),
                                    senderImage3,
                                    String.valueOf(movieModel.getMovieId()),
                                    movieModel.getOverview(),
                                    movieModel.getTitle(),
                                    EventLog.ACTIVITY_TYPE_MOVIE,
                                    movieModel.getRelease_date(),
                                    postImage3,
                                    shareDate,
                                    null,
                                    null,
                                    null,
                                    movieGenres,
                                    null,
                                    Double.valueOf(movieModel.getVote_average()),
                                    Double.valueOf(movieModel.getPopularity()),
                                    null,
                                    null,
                                    movieModel.getLanguage(),
                                    null,
                                    null,
                                    null,
                                    null,
                                    null,
                                    null);

                            UserDatabase.getInstance(context).getPostDao().insert(movie);
                        }
                    }
                }).start();
                break;

            case EventLog.ACTIVITY_TYPE_GAME:

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (!UserDatabase.getInstance(context).getPostDao().getItemId(gameModel.getFirebaseId())
                                || !UserDatabase.getInstance(context).getPostDao().getSenderName(userModel.getUsername())) {

                            String gameGenres = "";
                            for (int i = 0; i < gameModel.getGenre().size(); i++) {
                                gameGenres += gameModel.getGenre().get(i);
                                if (i != gameModel.getGenre().size() - 1) {
                                    gameGenres += ",";
                                }
                            }
                            String gameModes = "";
                            for (int i = 0; i < gameModel.getGameModeList().size(); i++) {
                                gameModes += gameModel.getGameModeList().get(i);
                                if (i != gameModel.getGameModeList().size() - 1) {
                                    gameModes += ",";
                                }
                            }
                            String gamePlatforms = "";
                            for (int i = 0; i < gameModel.getPlatformList().size(); i++) {
                                gamePlatforms += gameModel.getPlatformList().get(i);
                                if (i != gameModel.getPlatformList().size() - 1) {
                                    gamePlatforms += ",";
                                }
                            }

                            // For Sender Image
                            Bitmap bitmap3 = null;
                            try {
                                bitmap3 = Picasso.get().load(userModel.getProfilePictureUri().toString()).get();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            ByteArrayOutputStream outputStream3 = new ByteArrayOutputStream();
                            bitmap3.compress(Bitmap.CompressFormat.JPEG, 100, outputStream3);
                            byte[] senderImage2 = outputStream3.toByteArray();

                            // For Post Image
                            Bitmap bitmap4 = null;
                            try {
                                bitmap4 = Picasso.get().load("https://images.igdb.com/igdb/image/upload/t_cover_big/" + gameModel.getImageId() + ".jpg").get();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            ByteArrayOutputStream outputStream4 = new ByteArrayOutputStream();
                            bitmap4.compress(Bitmap.CompressFormat.JPEG, 100, outputStream4);
                            byte[] postImage2 = outputStream4.toByteArray();

                            Post game = new Post(UUID.randomUUID().toString(),
                                    gameModel.getFirebaseId(),
                                    userModel.getUsername(),
                                    senderImage2,
                                    String.valueOf(gameModel.getGameId()),
                                    gameModel.getSummary(),
                                    gameModel.getName(),
                                    EventLog.ACTIVITY_TYPE_GAME,
                                    gameModel.getReleaseDate(),
                                    postImage2,
                                    shareDate,
                                    null,
                                    null,
                                    null,
                                    gameGenres,
                                    gameModel.getVideoId(),
                                    gameModel.getRating(),
                                    gameModel.getPopularity(),
                                    gamePlatforms,
                                    gameModes,
                                    null,
                                    null,
                                    null,
                                    null,
                                    null,
                                    null,
                                    null);

                            UserDatabase.getInstance(context).getPostDao().insert(game);
                        }
                    }
                }).start();

                break;
            case EventLog.ACTIVITY_TYPE_ACTIVITY:

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (!UserDatabase.getInstance(context).getPostDao().getItemId(activityModel.getActivityId())
                                || !UserDatabase.getInstance(context).getPostDao().getSenderName(userModel.getUsername())) {

                            String attendees = "";
                            for (int i = 0; i < activityModel.getAttendees().size(); i++) {
                                attendees += activityModel.getAttendees().get(i);
                                if (i != activityModel.getAttendees().size() - 1) {
                                    attendees += ",";
                                }
                            }

                            String subcategories = "";
                            for (int i = 0; i < activityModel.getSubCategories().size(); i++) {
                                subcategories += activityModel.getSubCategories().get(i);
                                if (i != activityModel.getSubCategories().size() - 1) {
                                    subcategories += ",";
                                }
                            }

                            // For sender image
                            Bitmap bitmap = null;
                            try {
                                bitmap = Picasso.get().load(userModel.getProfilePictureUri().toString()).get();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                            byte[] senderImage = outputStream.toByteArray();

                            // For Post Image
                            Bitmap bitmap2 = null;
                            try {
                                bitmap2 = Picasso.get().load(activityModel.getImageUri()).get();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            ByteArrayOutputStream outputStream2 = new ByteArrayOutputStream();
                            byte[] postImage = null;
                            if(bitmap2 != null){
                                bitmap2.compress(Bitmap.CompressFormat.JPEG, 100, outputStream2);
                                postImage = outputStream2.toByteArray();
                            }



                            Post activity = new Post(UUID.randomUUID().toString(),
                                    activityModel.getActivityId(),
                                    userModel.getUsername(),
                                    senderImage,
                                    activityModel.getActivityId(),
                                    activityModel.getDescription(),
                                    activityModel.getName(),
                                    EventLog.ACTIVITY_TYPE_ACTIVITY,
                                    activityModel.getDate(),
                                    postImage,
                                    shareDate,
                                    null,
                                    null,
                                    null,
                                    null,
                                    null,
                                    0.0,
                                    null,
                                    null,
                                    null,
                                    null,
                                    activityModel.getOwner(),
                                    attendees,
                                    subcategories,
                                    activityModel.getCategory(),
                                    activityModel.getLocation().latitude,
                                    activityModel.getLocation().longitude);

                            UserDatabase.getInstance(context).getPostDao().insert(activity);
                        }
                    }
                }).start();
                break;
        }
    }
}
