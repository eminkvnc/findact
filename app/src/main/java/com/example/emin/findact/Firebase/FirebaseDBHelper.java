package com.example.emin.findact.Firebase;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.Callable;


public class FirebaseDBHelper {

/////////////////////////REQUEST STATUS CODES/////////////////////

    public static final int FRIEND_REQUEST_STATUS_UNFOLLOWED = 50;
    public static final int FRIEND_REQUEST_STATUS_WAITING = 51;  // used in UserListItemAdapter , FirebaseDBHelper
    public static final int FRIEND_REQUEST_STATUS_ACCEPTED = 52;
    public static final int FRIEND_REQUEST_STATUS_NONE = 53;

//////////////////////FIREBASE CHILD REFERENCES//////////////////

   public static final String FIREBASE_DB_CHILD_USERS = "Users";
   public static final String FIREBASE_DB_CHILD_USER_DATA = "Data";
   public static final String FIREBASE_DB_CHILD_USER_LOG = "Logs";
   public static final String FIREBASE_DB_CHILD_USER_FOLLOWS = "Follows";
   public static final String FIREBASE_DB_CHILD_USER_REQUESTS = "Requests";
   public static final String FIREBASE_DB_CHILD_USER_FOLLOWERS = "Followers";
   public static final String FIREBASE_DB_CHILD_USER_FOLLOWING = "Following";


    private String TAG = "FirebaseDBHelper";
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private String userEmail;
    private String[] userEmailSplit;
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

/////////////////////////////////////////ADD DATA///////////////////////////////////////////////////

    public void addUserDetail(final UserData userData, final String user_email){

        String imageName = "images/profilePicture.jpg";

        Log.d("addUserDetail", "addUserDetail: "+ userData.getProfilePictureUri());
        final StorageReference mStorageReference = storageReference.child(user_email).child(imageName);

        databaseReference.child(FIREBASE_DB_CHILD_USERS).child(user_email).child(FIREBASE_DB_CHILD_USER_DATA).child("firstname").setValue(userData.getFirstname());
        databaseReference.child(FIREBASE_DB_CHILD_USERS).child(user_email).child(FIREBASE_DB_CHILD_USER_DATA).child("lastname").setValue(userData.getLastname());
        databaseReference.child(FIREBASE_DB_CHILD_USERS).child(user_email).child(FIREBASE_DB_CHILD_USER_DATA).child("birth-date").setValue(userData.getBirthdate());
        databaseReference.child(FIREBASE_DB_CHILD_USERS).child(user_email).child(FIREBASE_DB_CHILD_USER_DATA).child("city").setValue(userData.getCity());
        databaseReference.child(FIREBASE_DB_CHILD_USERS).child(user_email).child(FIREBASE_DB_CHILD_USER_DATA).child("username").setValue(userData.getUsername());
        databaseReference.child(FIREBASE_DB_CHILD_USERS).child(user_email).child(FIREBASE_DB_CHILD_USER_DATA).child("notification").setValue(userData.getNotification());
        databaseReference.child(FIREBASE_DB_CHILD_USERS).child(user_email).child(FIREBASE_DB_CHILD_USER_DATA).child("uuid-string").setValue(userData.getUuidString());

        mStorageReference.putFile(userData.getProfilePictureUri()).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                mStorageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String downloadUrl = uri.toString();
                        databaseReference.child(FIREBASE_DB_CHILD_USERS).child(user_email).child(FIREBASE_DB_CHILD_USER_DATA).child("profile-picture").setValue(downloadUrl);
                        Log.d("onSuccess", "onSuccess: "+ downloadUrl);
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

    public void updateUserDetailWithoutPicture(UserData userData){

        databaseReference.child(FIREBASE_DB_CHILD_USERS).child(getCurrentUser()).child(FIREBASE_DB_CHILD_USER_DATA).child("firstname").setValue(userData.getFirstname());
        databaseReference.child(FIREBASE_DB_CHILD_USERS).child(getCurrentUser()).child(FIREBASE_DB_CHILD_USER_DATA).child("lastname").setValue(userData.getLastname());
        databaseReference.child(FIREBASE_DB_CHILD_USERS).child(getCurrentUser()).child(FIREBASE_DB_CHILD_USER_DATA).child("birth-date").setValue(userData.getBirthdate());
        databaseReference.child(FIREBASE_DB_CHILD_USERS).child(getCurrentUser()).child(FIREBASE_DB_CHILD_USER_DATA).child("city").setValue(userData.getCity());
        databaseReference.child(FIREBASE_DB_CHILD_USERS).child(getCurrentUser()).child(FIREBASE_DB_CHILD_USER_DATA).child("username").setValue(userData.getUsername());
        databaseReference.child(FIREBASE_DB_CHILD_USERS).child(getCurrentUser()).child(FIREBASE_DB_CHILD_USER_DATA).child("notification").setValue(userData.getNotification());
        databaseReference.child(FIREBASE_DB_CHILD_USERS).child(getCurrentUser()).child(FIREBASE_DB_CHILD_USER_DATA).child("uuid-string").setValue(userData.getUuidString());
    }

//////////////////////////////////////////ADD LOG///////////////////////////////////////////////////

    public void addUserLog(final InitialLog initialLog, final String user_email){

        databaseReference.child(FIREBASE_DB_CHILD_USERS).child(user_email).child(FIREBASE_DB_CHILD_USER_LOG).child("Initial").setValue(initialLog);

    }

    public void addUserLog(EventLog eventLog, String user_email){

        databaseReference.child(FIREBASE_DB_CHILD_USERS).child(user_email).child(FIREBASE_DB_CHILD_USER_LOG).child("Event").setValue(eventLog);
    }


////////////////////////////////////////SEARCH FUNCTIONS////////////////////////////////////////////
    // MODIFY THIS FUNCTION AND ADD NEW FUNCTIONS WHEN DATA IS READY PROVIDED BY API'S

    public void searchUser(final String parameter, final ArrayList<UserData> userDataArrayList, final ArrayList<Integer> requestStatusList){
        Log.d(TAG, "searchUser: ");
        userDataArrayList.clear();
        requestStatusList.clear();
        DatabaseReference reference = databaseReference.child(FIREBASE_DB_CHILD_USERS);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    if (ds.getKey().contains(parameter) ||
                        ds.child(FIREBASE_DB_CHILD_USER_DATA).child("firstname").getValue().toString().contains(parameter) ||
                        ds.child(FIREBASE_DB_CHILD_USER_DATA).child("lastname").getValue().toString().contains(parameter)) {
                        String username = ds.child(FIREBASE_DB_CHILD_USER_DATA).child("username").getValue().toString();
                        userDataArrayList.add(mapUserData(ds));
                        if(ds.hasChild(FIREBASE_DB_CHILD_USER_FOLLOWS+"/"+FIREBASE_DB_CHILD_USER_REQUESTS)){
                            Query reference1 = databaseReference
                                    .child(FIREBASE_DB_CHILD_USERS)
                                    .child(username)
                                    .child(FIREBASE_DB_CHILD_USER_FOLLOWS)
                                    .child(FIREBASE_DB_CHILD_USER_REQUESTS)
                                    .orderByKey()
                                    .equalTo(getCurrentUser());
                            reference1.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if(dataSnapshot.exists()){
                                        requestStatusList.add(Integer.parseInt(dataSnapshot.child(getCurrentUser()).child("status").getValue().toString()));
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }else {
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

//////////////////////////////////////SENDING REQUESTS//////////////////////////////////////////////

    public void sendFollowRequest(String username){

        databaseReference.child(FIREBASE_DB_CHILD_USERS)
                .child(username)
                .child(FIREBASE_DB_CHILD_USER_FOLLOWS)
                .child(FIREBASE_DB_CHILD_USER_REQUESTS)
                .child(getCurrentUser())
                .child("status").setValue(FRIEND_REQUEST_STATUS_WAITING);

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
        // add current user to folliwings of username
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
                .child(getCurrentUser())
                .child(FIREBASE_DB_CHILD_USER_FOLLOWS)
                .child(FIREBASE_DB_CHILD_USER_REQUESTS)
                .child(username)
                .child("status").setValue(FRIEND_REQUEST_STATUS_UNFOLLOWED);
        //update request date
        databaseReference.child(FIREBASE_DB_CHILD_USERS)
                .child(getCurrentUser())
                .child(FIREBASE_DB_CHILD_USER_FOLLOWS)
                .child(FIREBASE_DB_CHILD_USER_REQUESTS)
                .child(username)
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
                        if(Integer.parseInt(ds.child("status").getValue().toString()) == FRIEND_REQUEST_STATUS_WAITING) {
                            final String username = ds.getKey();
                            databaseReference.child(FIREBASE_DB_CHILD_USERS).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                        if (ds.getKey().equals(username)) {
                                            userDataArrayList.add(mapUserData(ds));
                                            statusList.add(FRIEND_REQUEST_STATUS_NONE);
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
                if(dataSnapshot.hasChild(FIREBASE_DB_CHILD_USER_FOLLOWS+"/"+FIREBASE_DB_CHILD_USER_REQUESTS+"/"+getCurrentUser())){
                    requestStatusList.add(Integer.parseInt(dataSnapshot
                            .child(FIREBASE_DB_CHILD_USER_FOLLOWS)
                            .child(FIREBASE_DB_CHILD_USER_REQUESTS)
                            .child(getCurrentUser())
                            .child("status").getValue().toString()));

                }
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
                if(dataSnapshot.hasChild(FIREBASE_DB_CHILD_USER_FOLLOWS+"/"+FIREBASE_DB_CHILD_USER_REQUESTS+"/"+getCurrentUser())){
                    requestStatusList.add(Integer.parseInt(dataSnapshot
                            .child(FIREBASE_DB_CHILD_USER_FOLLOWS)
                            .child(FIREBASE_DB_CHILD_USER_REQUESTS)
                            .child(getCurrentUser())
                            .child("status").getValue().toString()));
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


    public String getCurrentUser(){
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        userEmail = firebaseUser.getEmail();
        userEmailSplit = userEmail.split("@");
        return userEmailSplit[0];
    }

}
