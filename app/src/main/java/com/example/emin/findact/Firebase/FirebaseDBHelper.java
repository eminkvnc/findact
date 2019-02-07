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
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.util.ArrayList;
import java.util.Calendar;



public class FirebaseDBHelper {

    public static final int FRIEND_REQUEST_STATUS_WAITING = 50;  // convert them to integer and use them in
    public static final int FRIEND_REQUEST_STATUS_NONE = 51;        // UserListItemAdapter , FirebaseDBHelpper
    public static final int FRIEND_REQUEST_STATUS_ACCEPTED = 52;// and modify Firebase Database
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

    public void addUserDetail(final UserData userData, final String user_email){

        String imageName = "images/profilePicture.jpg";

        Log.d("addUserDetail", "addUserDetail: "+ userData.getProfilePictureUri());
        final StorageReference mStorageReference = storageReference.child(user_email).child(imageName);

        databaseReference.child("Users").child(user_email).child("Data").child("firstname").setValue(userData.getFirstname());
        databaseReference.child("Users").child(user_email).child("Data").child("lastname").setValue(userData.getLastname());
        databaseReference.child("Users").child(user_email).child("Data").child("birth-date").setValue(userData.getBirthdate());
        databaseReference.child("Users").child(user_email).child("Data").child("city").setValue(userData.getCity());
        databaseReference.child("Users").child(user_email).child("Data").child("username").setValue(userData.getUsername());
        databaseReference.child("Users").child(user_email).child("Data").child("notification").setValue(userData.getNotification());
        databaseReference.child("Users").child(user_email).child("Data").child("uuid-string").setValue(userData.getUuidString());

        mStorageReference.putFile(userData.getProfilePictureUri()).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                mStorageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String downloadUrl = uri.toString();
                        databaseReference.child("Users").child(user_email).child("Data").child("profile-picture").setValue(downloadUrl);
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

        databaseReference.child("Users").child(getCurrentUser()).child("Data").child("firstname").setValue(userData.getFirstname());
        databaseReference.child("Users").child(getCurrentUser()).child("Data").child("lastname").setValue(userData.getLastname());
        databaseReference.child("Users").child(getCurrentUser()).child("Data").child("birth-date").setValue(userData.getBirthdate());
        databaseReference.child("Users").child(getCurrentUser()).child("Data").child("city").setValue(userData.getCity());
        databaseReference.child("Users").child(getCurrentUser()).child("Data").child("username").setValue(userData.getUsername());
        databaseReference.child("Users").child(getCurrentUser()).child("Data").child("notification").setValue(userData.getNotification());
        databaseReference.child("Users").child(getCurrentUser()).child("Data").child("uuid-string").setValue(userData.getUuidString());
    }

    public void addUserLog(final InitialLog initialLog, final String user_email){

        databaseReference.child("Users").child(user_email).child("Logs").child("Initial").setValue(initialLog);

    }

    public void addUserLog(EventLog eventLog, String user_email){

        databaseReference.child("Users").child(user_email).child("Logs").child("Event").setValue(eventLog);
    }

    public void searchUser(final String parameter, final ArrayList<UserData> userDataArrayList, final ArrayList<Integer> statusList){

        userDataArrayList.clear();
        statusList.clear();
        final DatabaseReference reference = databaseReference.child("Users");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    if (ds.getKey().contains(parameter) ||
                        ds.child("Data").child("firstname").getValue().toString().contains(parameter) ||
                        ds.child("Data").child("lastname").getValue().toString().contains(parameter)) {
                        userDataArrayList.add(mapUserData(ds));
                        int status;
                        if (ds.hasChild("Friends/Requests/" + getCurrentUser())) {
                            status = Integer.parseInt(ds.child("Friends").child("Requests").child(getCurrentUser()).child("status").getValue().toString());
                        } else {
                            status = FRIEND_REQUEST_STATUS_NONE;
                        }
                        Log.d(TAG, "onDataChange: status: " + status);
                        statusList.add(status);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

                      /////////////////////
                     // FRIEND REQUESTS //
                    /////////////////////

    public void sendFriendRequest(String username){

        databaseReference.child("Users").child(username).child("Friends").child("Requests").child(getCurrentUser())
                .child("status").setValue(FRIEND_REQUEST_STATUS_WAITING);
        databaseReference.child("Users").child(username).child("Friends").child("Requests").child(getCurrentUser())
                .child("date").setValue(Calendar.getInstance().getTime().toString());

    }

    public void undoFriendRequest(String username){
        databaseReference.child("Users").child(username).child("Friends").child("Requests").child(getCurrentUser())
                .child("status").setValue(FRIEND_REQUEST_STATUS_NONE);
        databaseReference.child("Users").child(username).child("Friends").child("Requests").child(getCurrentUser())
                .child("date").setValue(Calendar.getInstance().getTime().toString());
    }

    public void declineFriendRequest(String username){
        databaseReference.child("Users").child(getCurrentUser()).child("Friends").child("Requests").child(username)
                .child("status").setValue(FRIEND_REQUEST_STATUS_NONE);
        databaseReference.child("Users").child(getCurrentUser()).child("Friends").child("Requests").child(username)
                .child("date").setValue(Calendar.getInstance().getTime().toString());
    }

    public void acceptFriendRequest(String username){
        databaseReference.child("Users").child(getCurrentUser()).child("Friends").child("Requests").child(username)
                .child("status").setValue(FRIEND_REQUEST_STATUS_ACCEPTED);
        databaseReference.child("Users").child(getCurrentUser()).child("Friends").child("Requests").child(username)
                .child("date").setValue(Calendar.getInstance().getTime().toString());
        databaseReference.child("Users").child(getCurrentUser()).child("Friends").child("MyFriends").child(username).
                child("date").setValue(Calendar.getInstance().getTime().toString());
    }

    public void removeFriend(String username){
        databaseReference.child("Users").child(getCurrentUser()).child("Friends")
                .child("MyFriends").child(username).removeValue();
        databaseReference.child("Users").child(getCurrentUser()).child("Friends")
                .child("Requests").child(username).child("status").setValue(FRIEND_REQUEST_STATUS_NONE);
        databaseReference.child("Users").child(getCurrentUser()).child("Friends")
                .child("Requests").child(username).child("date").setValue(Calendar.getInstance().getTime().toString());
    }

    public void getFriendRequests(final ArrayList<UserData> userDataArrayList){

        userDataArrayList.clear();
        DatabaseReference reference = databaseReference.child("Users").child(getCurrentUser());
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild("Friends/Requests")){
                    for(DataSnapshot ds : dataSnapshot.child("Friends").child("Requests").getChildren()){
                        if(Integer.parseInt(ds.child("status").getValue().toString()) == FRIEND_REQUEST_STATUS_WAITING) {
                            final String username = ds.getKey();
                            Log.d(TAG, "onDataChange: requests: " + ds.getKey());
                            databaseReference.child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                        if (ds.getKey().equals(username)) {
                                            userDataArrayList.add(mapUserData(ds));
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
                        /////////////////////
                       //MAPPING FUNCTIONS//
                      /////////////////////

    //maps dataSnapshot to UserData
    private UserData mapUserData(DataSnapshot ds){

            UserData userData = new UserData(ds.child("Data").child("firstname").getValue().toString(),
                    ds.child("Data").child("lastname").getValue().toString(),
                    ds.child("Data").child("city").getValue().toString(),
                    ds.child("Data").child("birth-date").getValue().toString(),
                    ds.child("Data").child("username").getValue().toString(),
                    ds.child("Data").child("uuid-string").getValue().toString(),
                    ds.child("Data").child("notification").getValue().toString(),
                    Uri.parse(ds.child("Data").child("profile-picture").getValue().toString()));
            return userData;
    }

    //maps dataSnapshot to UserData Array
    private void mapUserData(DatabaseReference databaseReference, final ArrayList<UserData> userDataArrayList){

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    UserData userData = new UserData(ds.child("Data").child("firstname").getValue().toString(),
                                                     ds.child("Data").child("lastname").getValue().toString(),
                                                     ds.child("Data").child("city").getValue().toString(),
                                                     ds.child("Data").child("birth-date").getValue().toString(),
                                                     ds.child("Data").child("username").getValue().toString(),
                                                     ds.child("Data").child("uuid-string").getValue().toString(),
                                                     ds.child("Data").child("notification").getValue().toString(),
                                                     Uri.parse(ds.child("Data").child("profile-picture").getValue().toString()));
                    userDataArrayList.add(userData);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    public String getCurrentUser(){
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        userEmail = firebaseUser.getEmail();
        userEmailSplit = userEmail.split("@");
        return userEmailSplit[0];
    }

}
