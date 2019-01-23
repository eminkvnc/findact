package com.example.emin.findact.Firebase;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseDBHelper {

    DatabaseReference databaseReference;


    public static FirebaseDBHelper getInstance(){
        return SingletonHolder.INSTANCE;
    }

    private static class SingletonHolder {
        private static final FirebaseDBHelper INSTANCE = new FirebaseDBHelper();
    }

    public FirebaseDBHelper(){

        databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    public void addUserDetail(UserModel userModel, String user_email){

        databaseReference.child("Users").child(user_email).setValue(userModel);

    }

}
