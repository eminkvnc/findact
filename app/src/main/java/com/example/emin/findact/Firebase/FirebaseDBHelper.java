package com.example.emin.findact.Firebase;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;

public class FirebaseDBHelper {

    DatabaseReference databaseReference;
    StorageReference storageReference;

    public static FirebaseDBHelper getInstance(){
        return SingletonHolder.INSTANCE;
    }

    private static class SingletonHolder {
        private static final FirebaseDBHelper INSTANCE = new FirebaseDBHelper();
    }

    public FirebaseDBHelper(){

        databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    public void addUserDetail(final UserModel userModel, final String user_email, Uri selectedImage){

        storageReference = FirebaseStorage.getInstance().getReference();
        UUID uuidImage = UUID.randomUUID();
        String imageName = "images/" + uuidImage +".jpg";

        Log.d("addUserDetail", "addUserDetail: "+userModel.name);
        final StorageReference mStorageReference = storageReference.child(user_email).child(imageName); // Hata veriyor
        mStorageReference.putFile(selectedImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                mStorageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String downloadUrl = uri.toString();
                        databaseReference.child("Users").child(user_email).setValue(userModel);
                        databaseReference.child("Users").child(user_email).child("imageUrl").setValue(downloadUrl);
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

}
