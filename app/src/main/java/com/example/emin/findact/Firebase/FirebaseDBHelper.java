package com.example.emin.findact.Firebase;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;

public class FirebaseDBHelper {

    private DatabaseReference databaseReference;
    private StorageReference storageReference;

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


    public void setUserData(final UserData userData, final String user_email){
        UUID uuidImage = UUID.randomUUID();
        String imageName = "images/" + uuidImage +".jpg";

        Log.d("addUserDetail", "addUserDetail: "+ userData.name);
        final StorageReference mStorageReference = storageReference.child(user_email).child(imageName); // Hata veriyor
        mStorageReference.putFile(userData.getProfilePicture()).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                mStorageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String downloadUrl = uri.toString();
                        databaseReference.child("Users").child(user_email).child("Data").child("name").setValue(userData.getName());
                        databaseReference.child("Users").child(user_email).child("Data").child("surname").setValue(userData.getSurname());
                        databaseReference.child("Users").child(user_email).child("Data").child("birth-date").setValue(userData.getBirthDate());
                        databaseReference.child("Users").child(user_email).child("Data").child("city").setValue(userData.getCity());
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

    public void addUserLog(final InitialLog initialLog, final String user_email){

        databaseReference.child("Users").child(user_email).child("Logs").child("Initial").setValue(initialLog);

    }


    public void addUserLog(EventLog eventLog, String user_email){

        databaseReference.child("Users").child(user_email).child("Logs").child("Event").setValue(eventLog);

    }

    //ARKADAŞLAR VE ARKADAŞLIK İSTEKLERİ İÇİN METODLAR OLUŞTURULACAK.
    //PAYLAŞILAN POSTLARI İÇİN METODLAR OLUŞTURULACAK.

}
