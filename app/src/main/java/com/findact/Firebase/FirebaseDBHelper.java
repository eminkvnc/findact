package com.example.emin.findact.Firebase;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.emin.findact.APIs.ActivityModel;
import com.example.emin.findact.APIs.GameModel;
import com.example.emin.findact.APIs.MovieModel;
import com.example.emin.findact.APIs.PostModel;
import com.example.emin.findact.OnTaskCompletedListener;
import com.example.emin.findact.RoomDatabase.Activity;
import com.example.emin.findact.RoomDatabase.Game;
import com.example.emin.findact.RoomDatabase.Movie;
import com.example.emin.findact.RoomDatabase.User;
import com.example.emin.findact.RoomDatabase.UserDatabase;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.ExecutionException;

import javax.net.ssl.HttpsURLConnection;


public class FirebaseDBHelper {

/////////////////////////REQUEST STATUS CODES/////////////////////

    public static final int FRIEND_REQUEST_STATUS_UNFOLLOWED = 50;
    public static final int FRIEND_REQUEST_STATUS_REQUEST_WAITING = 51;  // used in UserListItemAdapter , FirebaseDBHelper
    public static final int FRIEND_REQUEST_STATUS_ACCEPTED = 52;
    public static final int FRIEND_REQUEST_STATUS_WAITING_ANSWER = 53;
    public static final int FRIEND_REQUEST_STATUS_NONE = 54;

//////////////////////FIREBASE CHILD REFERENCES//////////////////

    public static final String FIREBASE_DB_CHILD_USERS = "Users";
    public static final String FIREBASE_DB_CHILD_USER_DATA = "Data";
    public static final String FIREBASE_DB_CHILD_USER_LOG = "Logs";
    public static final String FIREBASE_DB_CHILD_USER_LOG_INITIAL = "Initial";
    public static final String FIREBASE_DB_CHILD_USER_LOG_EVENT = "Event";
    public static final String FIREBASE_DB_CHILD_USER_LOG_EVENT_SHARE = "Share";
    public static final String FIREBASE_DB_CHILD_USER_LOG_EVENT_LIKE = "Like";
    public static final String FIREBASE_DB_CHILD_USER_LOG_EVENT_DISLIKE = "Dislike";
    public static final String FIREBASE_DB_CHILD_USER_FOLLOWS = "Follows";
    public static final String FIREBASE_DB_CHILD_USER_REQUESTS = "Requests";
    public static final String FIREBASE_DB_CHILD_USER_FOLLOWERS = "Followers";
    public static final String FIREBASE_DB_CHILD_USER_FOLLOWING = "Following";
    public static final String FIREBASE_DB_CHILD_USER_ACTIVITIES = "Activities";


    private String TAG = "FirebaseDBHelper";
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    public static FirebaseDBHelper getInstance(){
        return SingletonHolder.INSTANCE;
    }

    private static class SingletonHolder {
        private static final FirebaseDBHelper INSTANCE = new FirebaseDBHelper();
    }

    public FirebaseDBHelper(){

        databaseReference = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference();
    }

/////////////////////////////////////////USER DATA//////////////////////////////////////////////////

    public void addUserDetail(final UserData userData, boolean isImageUpdated){

        String imageName = "images/profilePicture.jpg";

        Log.d("addUserDetail", "addUserDetail: "+ userData.getProfilePictureUri());
        final StorageReference mStorageReference = storageReference.child(getCurrentUser()).child(imageName);

        databaseReference.child(FIREBASE_DB_CHILD_USERS).child(getCurrentUser()).child(FIREBASE_DB_CHILD_USER_DATA).child("firstname").setValue(userData.getFirstname());
        databaseReference.child(FIREBASE_DB_CHILD_USERS).child(getCurrentUser()).child(FIREBASE_DB_CHILD_USER_DATA).child("lastname").setValue(userData.getLastname());
        databaseReference.child(FIREBASE_DB_CHILD_USERS).child(getCurrentUser()).child(FIREBASE_DB_CHILD_USER_DATA).child("birth-date").setValue(userData.getBirthdate());
        databaseReference.child(FIREBASE_DB_CHILD_USERS).child(getCurrentUser()).child(FIREBASE_DB_CHILD_USER_DATA).child("city").setValue(userData.getCity());
        databaseReference.child(FIREBASE_DB_CHILD_USERS).child(getCurrentUser()).child(FIREBASE_DB_CHILD_USER_DATA).child("username").setValue(userData.getUsername());
        databaseReference.child(FIREBASE_DB_CHILD_USERS).child(getCurrentUser()).child(FIREBASE_DB_CHILD_USER_DATA).child("notification").setValue(userData.getNotification());
        databaseReference.child(FIREBASE_DB_CHILD_USERS).child(getCurrentUser()).child(FIREBASE_DB_CHILD_USER_DATA).child("uuid-string").setValue(userData.getUuidString());
        if(isImageUpdated) {
            mStorageReference.putFile(userData.getProfilePictureUri()).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    mStorageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String downloadUrl = uri.toString();
                            databaseReference.child(FIREBASE_DB_CHILD_USERS).child(getCurrentUser()).child(FIREBASE_DB_CHILD_USER_DATA).child("profile-picture").setValue(downloadUrl);
                            Log.d("onSuccess", "onSuccess: " + downloadUrl);
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d("onFailure", "onFailure: " + e);
                }
            });
        }
    }

    public void getUserData(String userId, final ArrayList<UserData> userData){

        userData.clear();
        DatabaseReference reference = databaseReference.child(FIREBASE_DB_CHILD_USERS).child(userId);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userData.add(mapUserData(dataSnapshot));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

///////////////////////////////////////////LOGS/////////////////////////////////////////////////////

    public void addInitialUserLog(InitialLog initialLog){

        databaseReference
                .child(FIREBASE_DB_CHILD_USERS)
                .child(getCurrentUser())
                .child(FIREBASE_DB_CHILD_USER_LOG)
                .child(FIREBASE_DB_CHILD_USER_LOG_INITIAL).setValue(initialLog);

    }

    public void addEventUserLog(final EventLog eventLog){

        final DatabaseReference reference = databaseReference
                .child(FIREBASE_DB_CHILD_USERS)
                .child(getCurrentUser())
                .child(FIREBASE_DB_CHILD_USER_LOG)
                .child(FIREBASE_DB_CHILD_USER_LOG_EVENT)
                .child(eventLog.getId());

        switch (eventLog.getActivityType()){

            case EventLog.ACTIVITY_TYPE_MOVIE:

                if(eventLog.getEventType().equals(EventLog.EVENT_TYPE_LIKE)){
                    final Boolean[] isLiked = new Boolean[1];
                    isLiked(eventLog.getId(), isLiked, new OnTaskCompletedListener() {
                        @Override
                        public void onTaskCompleted() {
                            if(isLiked[0]){
                                reference.child("like").setValue(false);
                                reference.child("dislike").setValue(false);
                            }else {
                                addMovieModelLog(reference, EventLog.EVENT_TYPE_LIKE, eventLog.getMovieModel() );
                                reference.child("dislike").setValue(false);
                            }
                        }
                    });
                }

                if(eventLog.getEventType().equals(EventLog.EVENT_TYPE_DISLIKE)){
                    final Boolean[] isDisliked = new Boolean[1];
                    isDisliked(eventLog.getId(), isDisliked, new OnTaskCompletedListener() {
                        @Override
                        public void onTaskCompleted() {
                            if(isDisliked[0]){
                                reference.child("like").setValue(false);
                                reference.child("dislike").setValue(false);
                            }else {
                                addMovieModelLog(reference, EventLog.EVENT_TYPE_DISLIKE, eventLog.getMovieModel() );
                                reference.child("like").setValue(false);
                            }
                        }
                    });
                }
                if(eventLog.getEventType().equals(EventLog.EVENT_TYPE_SHARE)){
                    addMovieModelLog(reference, EventLog.EVENT_TYPE_SHARE, eventLog.getMovieModel() );
                }

                break;

            case EventLog.ACTIVITY_TYPE_GAME:

                if(eventLog.getEventType().equals(EventLog.EVENT_TYPE_LIKE)){
                    final Boolean[] isLiked = new Boolean[1];
                    isLiked(eventLog.getId(), isLiked, new OnTaskCompletedListener() {
                        @Override
                        public void onTaskCompleted() {
                            if(isLiked[0]){
                                reference.child("like").setValue(false);
                                reference.child("dislike").setValue(false);
                            }else {
                                addGameModelLog(reference, EventLog.EVENT_TYPE_LIKE, eventLog.getGameModel() );
                                reference.child("dislike").setValue(false);
                            }
                        }
                    });
                }

                if(eventLog.getEventType().equals(EventLog.EVENT_TYPE_DISLIKE)){
                    final Boolean[] isDisliked = new Boolean[1];
                    isDisliked(eventLog.getId(), isDisliked, new OnTaskCompletedListener() {
                        @Override
                        public void onTaskCompleted() {
                            if(isDisliked[0]){
                                reference.child("like").setValue(false);
                                reference.child("dislike").setValue(false);
                            }else {
                                addGameModelLog(reference, EventLog.EVENT_TYPE_DISLIKE, eventLog.getGameModel() );
                                reference.child("like").setValue(false);
                            }
                        }
                    });
                }
                if(eventLog.getEventType().equals(EventLog.EVENT_TYPE_SHARE)){
                    addGameModelLog(reference, EventLog.EVENT_TYPE_SHARE, eventLog.getGameModel() );
                }

                break;

            case EventLog.ACTIVITY_TYPE_ACTIVITY:

                if(eventLog.getEventType().equals(EventLog.EVENT_TYPE_LIKE)){
                    final Boolean[] isLiked = new Boolean[1];
                    isLiked(eventLog.getId(), isLiked, new OnTaskCompletedListener() {
                        @Override
                        public void onTaskCompleted() {
                            if(isLiked[0]){
                                reference.child("like").setValue(false);
                                reference.child("dislike").setValue(false);
                            }else {
                                addActivityModelLog(reference, EventLog.EVENT_TYPE_LIKE, eventLog.getActivityModel() );
                                reference.child("dislike").setValue(false);
                            }
                        }
                    });
                }

                if(eventLog.getEventType().equals(EventLog.EVENT_TYPE_DISLIKE)){
                    final Boolean[] isDisliked = new Boolean[1];
                    isDisliked(eventLog.getId(), isDisliked, new OnTaskCompletedListener() {
                        @Override
                        public void onTaskCompleted() {
                            if(isDisliked[0]){
                                reference.child("like").setValue(false);
                                reference.child("dislike").setValue(false);
                            }else {
                                addActivityModelLog(reference, EventLog.EVENT_TYPE_DISLIKE, eventLog.getActivityModel() );
                                reference.child("like").setValue(false);
                            }
                        }
                    });
                }
                if(eventLog.getEventType().equals(EventLog.EVENT_TYPE_SHARE)){
                    addActivityModelLog(reference, EventLog.EVENT_TYPE_SHARE, eventLog.getActivityModel() );
                }

                break;

        }

    }

    public void isLiked(final String firebaseId, final Boolean[] eventTypeStatus, final OnTaskCompletedListener listener){
        final String path = FIREBASE_DB_CHILD_USERS+"/"+getCurrentUser()+"/"+FIREBASE_DB_CHILD_USER_LOG+"/"+FIREBASE_DB_CHILD_USER_LOG_EVENT+"/"+firebaseId+"/"+"like";
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(path)){
                    if( dataSnapshot
                            .child(FIREBASE_DB_CHILD_USERS)
                            .child(getCurrentUser())
                            .child(FIREBASE_DB_CHILD_USER_LOG)
                            .child(FIREBASE_DB_CHILD_USER_LOG_EVENT)
                            .child(firebaseId).child("like").getValue().equals(true)){
                        eventTypeStatus[0] = true;
                    }
                    else {
                        eventTypeStatus[0] = false;
                    }
                }
                else {
                    eventTypeStatus[0] = false;
                }
                listener.onTaskCompleted();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void isDisliked(final String firebaseId, final Boolean[] eventTypeStatus, final OnTaskCompletedListener listener){
        final String path = FIREBASE_DB_CHILD_USERS+"/"+getCurrentUser()+"/"+FIREBASE_DB_CHILD_USER_LOG+"/"+FIREBASE_DB_CHILD_USER_LOG_EVENT+"/"+firebaseId+"/"+"dislike";
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(path)){
                    if( dataSnapshot
                            .child(FIREBASE_DB_CHILD_USERS)
                            .child(getCurrentUser())
                            .child(FIREBASE_DB_CHILD_USER_LOG)
                            .child(FIREBASE_DB_CHILD_USER_LOG_EVENT)
                            .child(firebaseId).child("dislike").getValue().equals(true)){
                        eventTypeStatus[0] = true;
                    }
                    else {
                        eventTypeStatus[0] = false;
                    }
                }
                else {
                    eventTypeStatus[0] = false;
                }
                listener.onTaskCompleted();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    public void getPosts(final Context context, final ArrayList<PostModel> postModelArrayList, final OnTaskCompletedListener listener){

        postModelArrayList.clear();
        final ArrayList<UserData> userDataArrayList = new ArrayList<>();
        final ArrayList<Integer> requestStatusList = new ArrayList<>();
        final Integer[] check = new Integer[1];
        check[0] = 0;

        OnTaskCompletedListener onTaskCompletedListener = new OnTaskCompletedListener() {
            @Override
            public void onTaskCompleted() {
                for(int i = 0; i < userDataArrayList.size(); i++){
                    final UserData userData = userDataArrayList.get(i);
                    final int requestStatus = requestStatusList.get(i);
                    DatabaseReference reference = databaseReference
                            .child(FIREBASE_DB_CHILD_USERS);
                    reference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                String path = userData.getUuidString()+"/"+FIREBASE_DB_CHILD_USER_LOG +"/"+FIREBASE_DB_CHILD_USER_LOG_EVENT;
                                if(dataSnapshot.hasChild(path)){
                                    for(DataSnapshot ds :dataSnapshot.child(userData.getUuidString()).child(FIREBASE_DB_CHILD_USER_LOG).child(FIREBASE_DB_CHILD_USER_LOG_EVENT).getChildren()){
                                        if(ds.hasChild("share")) {
                                            mapPost(context,ds, userData, requestStatus, postModelArrayList);
                                        }else{
                                            Log.d(TAG, "onDataChange: if1");
                                        }
                                    }
                                }
                                else{
                                    Log.d(TAG, "onDataChange: if2");
                                }
                                check[0] += 1;
                                if(check[0] == userDataArrayList.size()){
                                    listener.onTaskCompleted();
                                }
                            }


                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }
        };
        getFollowing(userDataArrayList,requestStatusList,onTaskCompletedListener);
    }


/////////////////////////////////////////////ACTIVITY///////////////////////////////////////////////

    public void addGroupActivity(final ActivityModel activityModel){

        String imageName = "images/activityImages/"+activityModel.getActivityId();
        ArrayList<String> subCategoriesList = activityModel.getSubCategories();
        String subCategories = "";
        for(int i = 0; i < subCategoriesList.size(); i++){
            subCategories += subCategoriesList.get(i);
            if(i != subCategoriesList.size()-1){
                subCategories += ",";
            }
        }
        final DatabaseReference reference = databaseReference
                .child(FIREBASE_DB_CHILD_USER_ACTIVITIES)
                .child(activityModel.getActivityId());

                reference.child("id").setValue(activityModel.getActivityId());
                reference.child("name").setValue(activityModel.getName());
                reference.child("latitude").setValue(activityModel.getLocation().latitude);
                reference.child("longitude").setValue(activityModel.getLocation().longitude);
                reference.child("date").setValue(activityModel.getDate());
                reference.child("category").setValue(activityModel.getCategory());
                reference.child("description").setValue(activityModel.getDescription());
                reference.child("sub-categories").setValue(subCategories);
                reference.child("owner").setValue(getCurrentUser());
                if(activityModel.getAttendees() != null) {
                    for (int j = 0; j < activityModel.getAttendees().size(); j++) {
                        reference.child("attendees").child(activityModel.getAttendees().get(j)).setValue(Calendar.getInstance().getTime().toString());
                    }
                }


        final StorageReference mStorageReference = storageReference.child(getCurrentUser()).child(imageName);
        mStorageReference.putFile(activityModel.getImageUri()).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                mStorageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String downloadUrl = uri.toString();
                        reference.child("image").setValue(downloadUrl);
                        Log.d("onSuccess", "onSuccess: " + downloadUrl);
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("onFailure", "onFailure: " + e);
            }
        });

    }

    public void joinActivity(String activityId, String userId){
        databaseReference
                .child(FIREBASE_DB_CHILD_USER_ACTIVITIES)
                .child(activityId)
                .child("attendees")
                .child(userId).setValue(Calendar.getInstance().getTime().toString());
    }

    public void getAttendees(String activityId, final ArrayList<UserData> attendees, final OnTaskCompletedListener listener){

        databaseReference
                .child(FIREBASE_DB_CHILD_USER_ACTIVITIES)
                .child(activityId)
                .child("attendees").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    databaseReference.child(FIREBASE_DB_CHILD_USERS).child(ds.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            attendees.add(mapUserData(dataSnapshot));
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
                listener.onTaskCompleted();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }


////////////////////////////////////////SEARCH FUNCTIONS////////////////////////////////////////////

    public void searchUser(final String parameter, final ArrayList<UserData> userDataArrayList, final ArrayList<Integer> requestStatusList){
        Log.d(TAG, "searchUser: ");
        userDataArrayList.clear();
        requestStatusList.clear();
        DatabaseReference reference = databaseReference.child(FIREBASE_DB_CHILD_USERS);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    if (ds.child(FIREBASE_DB_CHILD_USER_DATA).child("username").getValue().toString().contains(parameter) ||
                        ds.child(FIREBASE_DB_CHILD_USER_DATA).child("firstname").getValue().toString().contains(parameter) ||
                        ds.child(FIREBASE_DB_CHILD_USER_DATA).child("lastname").getValue().toString().contains(parameter)) {
                        userDataArrayList.add(mapUserData(ds));
                        Log.d(TAG, "onDataChange: "+FIREBASE_DB_CHILD_USER_FOLLOWS+"/"+FIREBASE_DB_CHILD_USER_REQUESTS+"/"+getCurrentUser());
                        if(ds.hasChild(FIREBASE_DB_CHILD_USER_FOLLOWS+"/"+FIREBASE_DB_CHILD_USER_REQUESTS+"/"+getCurrentUser())){
                            Log.d(TAG, "onDataChange: has request child");
                            Integer status = Integer.parseInt(ds
                                    .child(FIREBASE_DB_CHILD_USER_FOLLOWS)
                                    .child(FIREBASE_DB_CHILD_USER_REQUESTS)
                                    .child(getCurrentUser())
                                    .child("status").getValue().toString());
                            requestStatusList.add(status);
                        }else{
                            Log.d(TAG, "onDataChange: has no child");
                            requestStatusList.add(FRIEND_REQUEST_STATUS_UNFOLLOWED);
                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: "+databaseError.getMessage());
            }
        });
    }

    public void searchActivity(final String parameter, final ArrayList<ActivityModel> activities){

        activities.clear();
        DatabaseReference reference = databaseReference.child(FIREBASE_DB_CHILD_USER_ACTIVITIES);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    if (ds.child("name").getValue().toString().contains(parameter)) {
                        activities.add(mapActivity(ds));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

//////////////////////////////////////SENDING REQUESTS//////////////////////////////////////////////

    public void sendFollowRequest(String username){

        databaseReference.child(FIREBASE_DB_CHILD_USERS)
                .child(username)
                .child(FIREBASE_DB_CHILD_USER_FOLLOWS)
                .child(FIREBASE_DB_CHILD_USER_REQUESTS)
                .child(getCurrentUser())
                .child("status").setValue(FRIEND_REQUEST_STATUS_REQUEST_WAITING);

        databaseReference.child(FIREBASE_DB_CHILD_USERS)
                .child(username).child(FIREBASE_DB_CHILD_USER_FOLLOWS)
                .child(FIREBASE_DB_CHILD_USER_REQUESTS)
                .child(getCurrentUser())
                .child("date").setValue(Calendar.getInstance().getTime().toString());

    }

    public void undoFollowRequest(String username){

        databaseReference.child(FIREBASE_DB_CHILD_USERS)
                .child(username)
                .child(FIREBASE_DB_CHILD_USER_FOLLOWS)
                .child(FIREBASE_DB_CHILD_USER_REQUESTS)
                .child(getCurrentUser())
                .child("status").setValue(FRIEND_REQUEST_STATUS_UNFOLLOWED);

        databaseReference.child(FIREBASE_DB_CHILD_USERS)
                .child(username)
                .child(FIREBASE_DB_CHILD_USER_FOLLOWS)
                .child(FIREBASE_DB_CHILD_USER_REQUESTS)
                .child(getCurrentUser())
                .child("date").setValue(Calendar.getInstance().getTime().toString());
    }

    public void declineFollowRequest(String username){

        databaseReference.child(FIREBASE_DB_CHILD_USERS)
                .child(getCurrentUser())
                .child(FIREBASE_DB_CHILD_USER_FOLLOWS)
                .child(FIREBASE_DB_CHILD_USER_REQUESTS)
                .child(username)
                .child("status").setValue(FRIEND_REQUEST_STATUS_UNFOLLOWED);

        databaseReference.child(FIREBASE_DB_CHILD_USERS)
                .child(getCurrentUser())
                .child(FIREBASE_DB_CHILD_USER_FOLLOWS)
                .child(FIREBASE_DB_CHILD_USER_REQUESTS)
                .child(username)
                .child("date").setValue(Calendar.getInstance().getTime().toString());
    }

    public void acceptFollowRequest(String username){
        //update request status
        databaseReference.child(FIREBASE_DB_CHILD_USERS)
                .child(getCurrentUser())
                .child(FIREBASE_DB_CHILD_USER_FOLLOWS)
                .child(FIREBASE_DB_CHILD_USER_REQUESTS)
                .child(username)
                .child("status").setValue(FRIEND_REQUEST_STATUS_ACCEPTED);
        //update request date
        databaseReference.child(FIREBASE_DB_CHILD_USERS)
                .child(getCurrentUser())
                .child(FIREBASE_DB_CHILD_USER_FOLLOWS)
                .child(FIREBASE_DB_CHILD_USER_REQUESTS)
                .child(username)
                .child("date").setValue(Calendar.getInstance().getTime().toString());
        //add username to followers of current user
        databaseReference.child(FIREBASE_DB_CHILD_USERS)
                .child(getCurrentUser())
                .child(FIREBASE_DB_CHILD_USER_FOLLOWS)
                .child(FIREBASE_DB_CHILD_USER_FOLLOWERS)
                .child(username).
                child("date").setValue(Calendar.getInstance().getTime().toString());
        // add current user to followings of username
        databaseReference.child(FIREBASE_DB_CHILD_USERS)
                .child(username)
                .child(FIREBASE_DB_CHILD_USER_FOLLOWS)
                .child(FIREBASE_DB_CHILD_USER_FOLLOWING)
                .child(getCurrentUser()).
                child("date").setValue(Calendar.getInstance().getTime().toString());
    }

    public void unfollowUser(String username){
        //update request status
        databaseReference.child(FIREBASE_DB_CHILD_USERS)
                .child(username)
                .child(FIREBASE_DB_CHILD_USER_FOLLOWS)
                .child(FIREBASE_DB_CHILD_USER_REQUESTS)
                .child(getCurrentUser())
                .child("status").setValue(FRIEND_REQUEST_STATUS_UNFOLLOWED);
        //update request date
        databaseReference.child(FIREBASE_DB_CHILD_USERS)
                .child(username)
                .child(FIREBASE_DB_CHILD_USER_FOLLOWS)
                .child(FIREBASE_DB_CHILD_USER_REQUESTS)
                .child(getCurrentUser())
                .child("date").setValue(Calendar.getInstance().getTime().toString());
        //remove username to following of current user
        databaseReference.child(FIREBASE_DB_CHILD_USERS)
                .child(getCurrentUser())
                .child(FIREBASE_DB_CHILD_USER_FOLLOWS)
                .child(FIREBASE_DB_CHILD_USER_FOLLOWING)
                .child(username).removeValue();
        //remove current user to followers of username
        databaseReference.child(FIREBASE_DB_CHILD_USERS)
                .child(username)
                .child(FIREBASE_DB_CHILD_USER_FOLLOWS)
                .child(FIREBASE_DB_CHILD_USER_FOLLOWERS)
                .child(getCurrentUser()).removeValue();


    }

    //////////////////////////GETTING FOLLOWERS, FOLLOWINGS AND REQUESTS////////////////////////////

    public void getFollowRequests(final ArrayList<UserData> userDataArrayList, final ArrayList<Integer> statusList){

        userDataArrayList.clear();
        statusList.clear();

        DatabaseReference reference = databaseReference.child(FIREBASE_DB_CHILD_USERS).child(getCurrentUser());
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange: ds: "+dataSnapshot.toString());
                if(dataSnapshot.hasChild(FIREBASE_DB_CHILD_USER_FOLLOWS+"/"+FIREBASE_DB_CHILD_USER_REQUESTS)){
                    for(DataSnapshot ds : dataSnapshot.child(FIREBASE_DB_CHILD_USER_FOLLOWS).child(FIREBASE_DB_CHILD_USER_REQUESTS).getChildren()){
                        if(Integer.parseInt(ds.child("status").getValue().toString()) == FRIEND_REQUEST_STATUS_REQUEST_WAITING) {
                            final String username = ds.getKey();
                            databaseReference.child(FIREBASE_DB_CHILD_USERS).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                        if (ds.getKey().equals(username)) {
                                            userDataArrayList.add(mapUserData(ds));
                                            statusList.add(FRIEND_REQUEST_STATUS_WAITING_ANSWER);
                                            Log.d(TAG, "onDataChange: request: "+username);
                                        }
                                    }
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void getFollowing(String user, final ArrayList<UserData> followingList, final ArrayList<Integer> requestStatusList){
        followingList.clear();
        requestStatusList.clear();
        DatabaseReference reference = databaseReference.child(FIREBASE_DB_CHILD_USERS).child(user);
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(FIREBASE_DB_CHILD_USER_FOLLOWS+"/"+FIREBASE_DB_CHILD_USER_FOLLOWING)){
                    Log.d(TAG, "onDataChange: has child:");
                    for(DataSnapshot ds : dataSnapshot.child(FIREBASE_DB_CHILD_USER_FOLLOWS).child(FIREBASE_DB_CHILD_USER_FOLLOWING).getChildren()){
                        final String username = ds.getKey();
                        if(username!=null) {
                            databaseReference
                                    .child(FIREBASE_DB_CHILD_USERS)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for(DataSnapshot ds2 : dataSnapshot.getChildren()){
                                        if(ds2.getKey().equals(username)) {
                                            followingList.add(mapUserData(ds2));
                                            if(ds2.hasChild(FIREBASE_DB_CHILD_USER_FOLLOWS+"/"+FIREBASE_DB_CHILD_USER_REQUESTS+"/"+getCurrentUser())){
                                                requestStatusList.add(Integer.parseInt(ds2
                                                        .child(FIREBASE_DB_CHILD_USER_FOLLOWS)
                                                        .child(FIREBASE_DB_CHILD_USER_REQUESTS)
                                                        .child(getCurrentUser())
                                                        .child("status").getValue().toString()));

                                            }else{
                                                requestStatusList.add(FRIEND_REQUEST_STATUS_UNFOLLOWED);
                                            }

                                        }
                                    }
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void getFollowing(final ArrayList<UserData> userList, final ArrayList<Integer> requestStatusList,  final OnTaskCompletedListener listener) {

        userList.clear();
        requestStatusList.clear();
        DatabaseReference reference = databaseReference.child(FIREBASE_DB_CHILD_USERS).child(getCurrentUser());
        final String path2 = FIREBASE_DB_CHILD_USERS+"/"+getCurrentUser()+"/";
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(path2+FIREBASE_DB_CHILD_USER_FOLLOWS+"/"+FIREBASE_DB_CHILD_USER_FOLLOWING)){
                    Log.d(TAG, "onDataChange: has child: following");
                    for(DataSnapshot ds : dataSnapshot
                            .child(FIREBASE_DB_CHILD_USERS)
                            .child(getCurrentUser())
                            .child(FIREBASE_DB_CHILD_USER_FOLLOWS)
                            .child(FIREBASE_DB_CHILD_USER_FOLLOWING).getChildren()){
                        final String username = ds.getKey();
                        if(username!=null) {
                            DataSnapshot ds2 = dataSnapshot.child(FIREBASE_DB_CHILD_USERS).child(username);
                                userList.add(mapUserData(ds2));
                                if(ds2.hasChild(FIREBASE_DB_CHILD_USER_FOLLOWS+"/"+FIREBASE_DB_CHILD_USER_REQUESTS+"/"+getCurrentUser())){
                                    requestStatusList.add(Integer.parseInt(ds2
                                            .child(FIREBASE_DB_CHILD_USER_FOLLOWS)
                                            .child(FIREBASE_DB_CHILD_USER_REQUESTS)
                                            .child(getCurrentUser())
                                            .child("status").getValue().toString()));

                                }else{
                                    requestStatusList.add(FRIEND_REQUEST_STATUS_UNFOLLOWED);
                                }
                        }
                    }
                }
                listener.onTaskCompleted();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }

    public void getFollowers(String user, final ArrayList<UserData> followersList, final ArrayList<Integer> requestStatusList){
        followersList.clear();
        requestStatusList.clear();
        DatabaseReference reference = databaseReference.child(FIREBASE_DB_CHILD_USERS).child(user);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(FIREBASE_DB_CHILD_USER_FOLLOWS+"/"+FIREBASE_DB_CHILD_USER_FOLLOWERS)){
                    Log.d(TAG, "onDataChange: has child:");
                    for(DataSnapshot ds : dataSnapshot.child(FIREBASE_DB_CHILD_USER_FOLLOWS).child(FIREBASE_DB_CHILD_USER_FOLLOWERS).getChildren()){
                        final String username = ds.getKey();
                        if(username!=null) {
                            databaseReference
                                    .child(FIREBASE_DB_CHILD_USERS)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            for(DataSnapshot ds2 : dataSnapshot.getChildren()){
                                                if(ds2.getKey().equals(username)) {
                                                    followersList.add(mapUserData(ds2));
                                                    if(ds2.hasChild(FIREBASE_DB_CHILD_USER_FOLLOWS+"/"+FIREBASE_DB_CHILD_USER_REQUESTS+"/"+getCurrentUser())){
                                                        requestStatusList.add(Integer.parseInt(ds2
                                                                .child(FIREBASE_DB_CHILD_USER_FOLLOWS)
                                                                .child(FIREBASE_DB_CHILD_USER_REQUESTS)
                                                                .child(getCurrentUser())
                                                                .child("status").getValue().toString()));
                                                    }else{
                                                        requestStatusList.add(FRIEND_REQUEST_STATUS_UNFOLLOWED);
                                                    }
                                                }
                                            }
                                        }
                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

/////////////////////////////////////MAPPING FUNCTIONS//////////////////////////////////////////////

    //maps dataSnapshot to UserData
    private UserData mapUserData(DataSnapshot ds){

            UserData userData = new UserData(ds.child(FIREBASE_DB_CHILD_USER_DATA).child("firstname").getValue().toString(),
                    ds.child(FIREBASE_DB_CHILD_USER_DATA).child("lastname").getValue().toString(),
                    ds.child(FIREBASE_DB_CHILD_USER_DATA).child("city").getValue().toString(),
                    ds.child(FIREBASE_DB_CHILD_USER_DATA).child("birth-date").getValue().toString(),
                    ds.child(FIREBASE_DB_CHILD_USER_DATA).child("username").getValue().toString(),
                    ds.child(FIREBASE_DB_CHILD_USER_DATA).child("uuid-string").getValue().toString(),
                    ds.child(FIREBASE_DB_CHILD_USER_DATA).child("notification").getValue().toString(),
                    Uri.parse(ds.child(FIREBASE_DB_CHILD_USER_DATA).child("profile-picture").getValue().toString()));
            return userData;
    }

    private ActivityModel mapActivity(DataSnapshot ds){

        LatLng latLng = new LatLng(
                Double.parseDouble(ds.child("latitude").getValue().toString()),
                Double.parseDouble(ds.child("longitude").getValue().toString()));

        ArrayList<String> subCategories = new ArrayList<>();
        ArrayList<String> attendees = new ArrayList<>();

        String[] subCategoriesArray = ds.child("sub-categories").getValue().toString().split(",");
        for (int i = 0; i < subCategoriesArray.length; i++){
            subCategories.add(subCategoriesArray[i]);
        }

        for(DataSnapshot dataSnapshot : ds.child("attendees").getChildren()){
            attendees.add(dataSnapshot.getKey());
        }

        ActivityModel activityModel = new ActivityModel(ds.child("id").getValue().toString(),
                ds.child("name").getValue().toString(),
                Uri.parse(ds.child("image").getValue().toString()),
                latLng,
                ds.child("date").getValue().toString(),
                ds.child("category").getValue().toString(),
                subCategories,
                attendees,
                ds.child("description").getValue().toString(),
                ds.child("owner").getValue().toString());

        return activityModel;
    }



    private void addMovieModelLog(DatabaseReference reference, String eventType, MovieModel movieModel){
        String genres = "";
        for(int i = 0; i < movieModel.getGenre().size(); i++){
            genres+=movieModel.getGenre().get(i);
            if(i != movieModel.getGenre().size()-1){
                genres+=",";
            }
        }
        reference.child("activity-id").setValue(movieModel.getMovieId());
        reference.child("title").setValue(movieModel.getTitle());
        reference.child("release-date").setValue(movieModel.getRelease_date());
        reference.child("genres").setValue(genres);
        reference.child("rating").setValue(movieModel.getVote_average());
        reference.child("popularity").setValue(movieModel.getPopularity());
        reference.child("image-path").setValue(movieModel.getPoster_path());
        reference.child("language").setValue(movieModel.getLanguage());
        reference.child("overview").setValue(movieModel.getOverview());

        if(eventType.equals(EventLog.EVENT_TYPE_LIKE)){
            reference.child("like").setValue(true);
        }
        if(eventType.equals(EventLog.EVENT_TYPE_DISLIKE)){
            reference.child("dislike").setValue(true);
        }
        if(eventType.equals(EventLog.EVENT_TYPE_SHARE)){
            reference.child("share").setValue(true);
            reference.child("log-date").setValue(Calendar.getInstance().getTimeInMillis());
        }
        reference.child("activity-type").setValue(EventLog.ACTIVITY_TYPE_MOVIE);

    }

    private void addGameModelLog(DatabaseReference reference, String eventType, GameModel gameModel){
        String genres = "";
        for(int i = 0; i < gameModel.getGenre().size(); i++){
            genres+=gameModel.getGenre().get(i);
            if(i != gameModel.getGenre().size()-1){
                genres+=",";
            }
        }
        String modes = "";
        for(int i = 0; i < gameModel.getGameModeList().size(); i++){
            modes+=gameModel.getGameModeList().get(i);
            if(i != gameModel.getGameModeList().size()-1){
                modes+=",";
            }
        }
        String platforms = "";
        for(int i = 0; i < gameModel.getPlatformList().size(); i++){
            platforms+=gameModel.getPlatformList().get(i);
            if(i != gameModel.getPlatformList().size()-1){
                platforms+=",";
            }
        }

        reference.child("activity-id").setValue(gameModel.getGameId());
        reference.child("title").setValue(gameModel.getName());
        reference.child("release-date").setValue(gameModel.getReleaseDate());
        reference.child("genres").setValue(genres);
        reference.child("modes").setValue(modes);
        reference.child("platforms").setValue(platforms);
        reference.child("rating").setValue(gameModel.getRating());
        reference.child("image-path").setValue(gameModel.getImageId());
        reference.child("video-id").setValue(gameModel.getVideoId());
        reference.child("popularity").setValue(gameModel.getPopularity());
        reference.child("overview").setValue(gameModel.getSummary());

        if(eventType.equals(EventLog.EVENT_TYPE_LIKE)){
            reference.child("like").setValue(true);
        }
        if(eventType.equals(EventLog.EVENT_TYPE_DISLIKE)){
            reference.child("dislike").setValue(true);
        }
        if(eventType.equals(EventLog.EVENT_TYPE_SHARE)){
            reference.child("share").setValue(true);
            reference.child("log-date").setValue(Calendar.getInstance().getTimeInMillis());
        }
        reference.child("activity-type").setValue(EventLog.ACTIVITY_TYPE_GAME);

    }

    private void addActivityModelLog(DatabaseReference reference, String eventType, ActivityModel activityModel){


        String subCategories = "";
        for(int i = 0; i < activityModel.getSubCategories().size(); i++){
            subCategories += activityModel.getSubCategories().get(i);
            if(i != activityModel.getSubCategories().size()-1){
                subCategories += ",";
            }
        }

        String attendees = "";
        for(int j = 0; j < activityModel.getAttendees().size(); j++){
            attendees += activityModel.getAttendees().get(j);
            if(j != activityModel.getAttendees().size()-1){
                attendees += ",";
            }
        }

        reference.child("sub-categories").setValue(subCategories);
        reference.child("attendees").setValue(attendees);
        reference.child("id").setValue(activityModel.getActivityId());
        reference.child("name").setValue(activityModel.getName());
        reference.child("image").setValue(activityModel.getImageUri().toString());
        reference.child("latitude").setValue(activityModel.getLocation().latitude);
        reference.child("longitude").setValue(activityModel.getLocation().longitude);
        reference.child("category").setValue(activityModel.getCategory());
        reference.child("description").setValue(activityModel.getDescription());
        reference.child("owner").setValue(activityModel.getOwner());
        reference.child("date").setValue(activityModel.getDate());

        if(eventType.equals(EventLog.EVENT_TYPE_LIKE)){
            reference.child("like").setValue(true);
        }
        if(eventType.equals(EventLog.EVENT_TYPE_DISLIKE)){
            reference.child("dislike").setValue(true);
        }
        if(eventType.equals(EventLog.EVENT_TYPE_SHARE)){
            reference.child("share").setValue(true);
            reference.child("log-date").setValue(Calendar.getInstance().getTimeInMillis());
        }
        reference.child("activity-type").setValue(EventLog.ACTIVITY_TYPE_ACTIVITY);



    }

    private void mapPost(final Context context, final DataSnapshot ds, final UserData userData, final int requestStatus, final ArrayList<PostModel> postModelArrayList){

        if(ds.hasChild("share") && ds.child("activity-type").getValue() != null){
            final Long shareDate = (Long) ds.child("log-date").getValue();

            switch (ds.child("activity-type").getValue().toString()){

                case EventLog.ACTIVITY_TYPE_MOVIE:

                    String movieGenresString =  ds.child("genres").getValue().toString();
                    String[] movieGenres =movieGenresString.split(",");
                    ArrayList<String> movieGenreList = new ArrayList<>();
                    for(int j = 0; j < movieGenres.length; j++){
                        movieGenreList.add(movieGenres[j]);
                    }

                    MovieModel movieModel = new MovieModel(
                            ds.getKey(),
                            Integer.parseInt(ds.child("activity-id").getValue().toString()),
                            ds.child("title").getValue().toString(),
                            ds.child("release-date").getValue().toString(),
                            movieGenreList,
                            ds.child("rating").getValue().toString(),
                            ds.child("popularity").getValue().toString(),
                            ds.child("image-path").getValue().toString(),
                            ds.child("overview").getValue().toString(),
                            ds.child("language").getValue().toString());
                    postModelArrayList.add(new PostModel(userData, null, movieModel, null, requestStatus, PostModel.MODEL_TYPE_MOVIE, shareDate));


                    // For Room Database
                    if (!UserDatabase.getInstance(context).getMovieDao().getItemId(ds.getKey())){
                        Thread thread = new Thread(){
                            @Override
                            public void run() {
                                boolean like = false;
                                boolean dislike = false;
                                if (ds.hasChild("like")){
                                    like = Boolean.getBoolean(ds.child("like").toString());
                                }
                                if (ds.hasChild("dislike")){
                                    dislike = Boolean.getBoolean(ds.child("dislike").toString());
                                }


                                // For Sender Image
                                ImageDownload imageDownload5 = new ImageDownload();
                                Bitmap bitmap5 = null;
                                try {
                                    bitmap5 = imageDownload5.execute(userData.getProfilePictureUri().toString()).get();
                                } catch (ExecutionException e) {
                                    e.printStackTrace();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                ByteArrayOutputStream outputStream5 = new ByteArrayOutputStream();
                                bitmap5.compress(Bitmap.CompressFormat.JPEG,100 ,outputStream5 );
                                byte[] senderImage3 = outputStream5.toByteArray();


                                // For Post Image
                                ImageDownload imageDownload6 = new ImageDownload();
                                Bitmap bitmap6 = null;
                                try {
                                    bitmap6 = imageDownload6.execute("http://image.tmdb.org/t/p/w185/"+ds.child("image-path").getValue().toString()).get();
                                } catch (ExecutionException e) {
                                    e.printStackTrace();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                ByteArrayOutputStream outputStream6 = new ByteArrayOutputStream();
                                bitmap6.compress(Bitmap.CompressFormat.JPEG,100 ,outputStream6);
                                byte[] postImage3 = outputStream6.toByteArray();
                                // Room Database
                                Movie movie = new Movie(ds.getKey(),
                                        Integer.parseInt(ds.child("activity-id").getValue().toString()),
                                        userData.getUsername(),
                                        senderImage3,
                                        ds.child("overview").getValue().toString(),
                                        ds.child("title").getValue().toString(),
                                        EventLog.ACTIVITY_TYPE_MOVIE,
                                        ds.child("release-date").getValue().toString(),
                                        postImage3 ,
                                        Long.valueOf(ds.child("log-date").getValue().toString()),
                                        like,
                                        dislike,
                                        Boolean.getBoolean(ds.child("share").getValue().toString()),
                                        ds.child("genres").getValue().toString(),
                                        Double.valueOf(ds.child("rating").getValue().toString()),
                                        Double.valueOf(ds.child("popularity").getValue().toString()) ,
                                        ds.child("language").getValue().toString() );

                                UserDatabase.getInstance(context).getMovieDao().insert(movie);
                            }
                        };
                        thread.start();
                    }

                    break;
                case EventLog.ACTIVITY_TYPE_GAME:

                    final String gameGenresString = ds.child("genres").getValue().toString();
                    String[] gameGenres = gameGenresString.split(",");
                    ArrayList<String> gameGenreList = new ArrayList<>();
                    for(int j = 0; j < gameGenres.length; j++){
                        gameGenreList.add(gameGenres[j]);
                    }

                    final String gameModesString = ds.child("modes").getValue().toString();
                    String[] gameModes = gameModesString.split(",");
                    ArrayList<String> gameModeList = new ArrayList<>();
                    for(int j = 0; j < gameModes.length; j++){
                        gameModeList.add(gameModes[j]);
                    }

                    final String gamePlatformsString = ds.child("platforms").getValue().toString();
                    String[] gamePlatforms = gamePlatformsString.split(",");
                    ArrayList<String> gamePlatformList = new ArrayList<>();
                    for(int j = 0; j < gamePlatforms.length; j++){
                        gamePlatformList.add(gamePlatforms[j]);
                    }

                    final GameModel gameModel = new GameModel(
                            ds.getKey(),
                            Integer.parseInt(ds.child("activity-id").getValue().toString()),
                            ds.child("title").getValue().toString(),
                            gameGenreList,ds.child("release-date").getValue().toString(),
                            ds.child("overview").getValue().toString(),
                            ds.child("image-path").getValue().toString(),
                            gameModeList,
                            Double.parseDouble(ds.child("rating").getValue().toString()),
                            gamePlatformList,
                            ds.child("video-id").getValue().toString(),
                            Double.parseDouble(ds.child("popularity").getValue().toString()));

                    postModelArrayList.add(new PostModel(userData, gameModel, null, null, requestStatus, PostModel.MODEL_TYPE_GAME, shareDate));


                    // For Room Database
                    if (!UserDatabase.getInstance(context).getGameDao().getItemId(ds.getKey())) {
                        Thread thread = new Thread(){
                            @Override
                            public void run() {

                                boolean like2 = false;
                                boolean dislike2 = false;
                                if (ds.hasChild("like")){
                                    like2 = Boolean.getBoolean(ds.child("like").toString());
                                }
                                if (ds.hasChild("dislike")){
                                    dislike2 = Boolean.getBoolean(ds.child("dislike").toString());
                                }

                                // For Sender Image
                                ImageDownload imageDownload3 = new ImageDownload();
                                Bitmap bitmap3 = null;
                                try {
                                    bitmap3 = imageDownload3.execute(userData.getProfilePictureUri().toString()).get();
                                } catch (ExecutionException e) {
                                    e.printStackTrace();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                ByteArrayOutputStream outputStream3 = new ByteArrayOutputStream();
                                bitmap3.compress(Bitmap.CompressFormat.JPEG, 100, outputStream3);
                                byte[] senderImage2 = outputStream3.toByteArray();

                                // For Post Image
                                ImageDownload imageDownload4 = new ImageDownload();
                                Bitmap bitmap4 = null;
                                try {
                                    bitmap4 = imageDownload4.execute("https://images.igdb.com/igdb/image/upload/t_cover_big/" + ds.child("image-path").getValue().toString() + ".jpg").get();
                                } catch (ExecutionException e) {
                                    e.printStackTrace();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                ByteArrayOutputStream outputStream4 = new ByteArrayOutputStream();
                                bitmap4.compress(Bitmap.CompressFormat.JPEG, 100, outputStream4);
                                byte[] postImage2 = outputStream4.toByteArray();

                                // Room Database
                                Game game = new Game(ds.getKey(),
                                        userData.getUsername(),
                                        senderImage2,
                                        Integer.parseInt(ds.child("activity-id").getValue().toString()),
                                        ds.child("overview").getValue().toString(),
                                        ds.child("title").getValue().toString(),
                                        EventLog.ACTIVITY_TYPE_GAME,
                                        ds.child("release-date").getValue().toString(),
                                        postImage2,
                                        Long.valueOf(ds.child("log-date").getValue().toString()),
                                        like2,
                                        dislike2,
                                        Boolean.getBoolean(ds.child("share").getValue().toString()),
                                        ds.child("genres").getValue().toString(),
                                        ds.child("video-id").getValue().toString(),
                                        Double.parseDouble(ds.child("rating").getValue().toString()),
                                        Double.parseDouble(ds.child("popularity").getValue().toString()),
                                        ds.child("platforms").getValue().toString(),
                                        ds.child("modes").getValue().toString()
                                );
                                UserDatabase.getInstance(context).getGameDao().insert(game);
                            }
                        };
                        thread.start();

                    }
                    break;
                case EventLog.ACTIVITY_TYPE_ACTIVITY:
                    ActivityModel activityModel = mapActivity(ds);
                    postModelArrayList.add(new PostModel(userData, null, null, activityModel, requestStatus, PostModel.MODEL_TYPE_ACTIVTY, shareDate));

                    // For Room Database
                    if (!UserDatabase.getInstance(context).getActivityDao().getItemId(ds.getKey())){
                        Thread thread = new Thread(){

                            @Override
                            public void run() {

                                boolean like3 = false;
                                boolean dislike3 = false;
                                if (ds.hasChild("like")){
                                    like3 = Boolean.getBoolean(ds.child("like").toString());
                                }
                                if (ds.hasChild("dislike")){
                                    dislike3 = Boolean.getBoolean(ds.child("dislike").toString());
                                }

                                ArrayList<String> attendeesArrayList = new ArrayList<>();
                                for (DataSnapshot dataSnapshot : ds.child("attendees").getChildren()){
                                    attendeesArrayList.add(dataSnapshot.getKey());
                                }

                                String attendees ="";
                                for (int i = 0; i < attendeesArrayList.size(); i++){
                                    attendees += attendeesArrayList.get(i);
                                    if (i != attendeesArrayList.size()-1){
                                        attendees += ",";
                                    }
                                }

                                // For Sender Image
                                ImageDownload imageDownload = new ImageDownload();
                                Bitmap bitmap = null;
                                try {
                                    bitmap = imageDownload.execute(userData.getProfilePictureUri().toString()).get();
                                } catch (ExecutionException e) {
                                    e.printStackTrace();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                                bitmap.compress(Bitmap.CompressFormat.JPEG,100 ,outputStream );
                                byte[] senderImage = outputStream.toByteArray();

                                // For Post Image
                                ImageDownload imageDownload2 = new ImageDownload();
                                Bitmap bitmap2 = null;
                                try {
                                    bitmap2 = imageDownload2.execute(ds.child("image").getValue().toString()).get();
                                } catch (ExecutionException e) {
                                    e.printStackTrace();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                ByteArrayOutputStream outputStream2 = new ByteArrayOutputStream();
                                bitmap2.compress(Bitmap.CompressFormat.JPEG,100 ,outputStream2);
                                byte[] postImage = outputStream2.toByteArray();

                                // Room Database
                                Activity activity = new Activity(ds.getKey(),
                                        userData.getUsername(),
                                        senderImage,ds.child("id").getValue().toString(),
                                        ds.child("description").getValue().toString(),
                                        ds.child("name").getValue().toString(),
                                        EventLog.ACTIVITY_TYPE_ACTIVITY,
                                        ds.child("date").getValue().toString(),
                                        postImage,
                                        Long.valueOf(ds.child("log-date").getValue().toString()),
                                        like3,
                                        dislike3,
                                        Boolean.getBoolean(ds.child("share").getValue().toString()),
                                        ds.child("owner").getValue().toString() ,attendees ,
                                        ds.child("sub-categories").getValue().toString(),
                                        ds.child("category").getValue().toString(),
                                        Double.valueOf(ds.child("latitude").getValue().toString()),
                                        Double.valueOf(ds.child("longitude").getValue().toString())
                                );
                                UserDatabase.getInstance(context).getActivityDao().insert(activity);
                            }
                        };
                        thread.start();
                    }

                    break;
            }
        }else {
            Log.d(TAG, "mapPost: no post for user: "+userData.getUsername());
        }
    }



    public String getCurrentUser(){
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        return firebaseUser.getUid();
    }

    public String getUserEmailSplit(){
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        String email[] = firebaseUser.getEmail().split("@");
        return email[0];
    }

    public void saveImageToLocal(Context context, User user, UserData userData){

        ImageDownloaderTask task = new ImageDownloaderTask(context,user,userData);
        task.execute();

    }

    public class ImageDownloaderTask extends AsyncTask<Void, Void, Void> {


        private User user;
        private UserData userData;
        private Context context;

        public ImageDownloaderTask(Context context, User user, UserData userData) {
            this.context = context;
            this.user = user;
            this.userData = userData;
        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Void doInBackground(Void... params) {
            ContextWrapper cw = new ContextWrapper(context);
            File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
            File myPath = new File(directory, getCurrentUser()+".jpg");
            try {
                URL url = new URL(userData.getProfilePictureUri().toString());
                HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(input);
                FileOutputStream fos = new FileOutputStream(myPath);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100,fos );
                fos.close();
                user = new User(userData.getUuidString()
                        ,userData.getFirstname()
                        ,userData.getLastname()
                        ,userData.getCity()
                        ,userData.getBirthdate()
                        ,myPath.getAbsolutePath()
                        ,userData.getNotification()
                        ,userData.getUsername());
                UserDatabase.getInstance(context).getUserDao().insert(user);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

        }

    }

    public class ImageDownload extends AsyncTask<String,Void,Bitmap>{

        @Override
        protected Bitmap doInBackground(String... strings) {

            Bitmap bitmap = null;
            URL url;
            HttpURLConnection connection;

            try {
                url = new URL(strings[0]);
                connection = (HttpURLConnection) url.openConnection();
                InputStream stream = connection.getInputStream();
                bitmap = BitmapFactory.decodeStream(stream);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
        }
    }

}