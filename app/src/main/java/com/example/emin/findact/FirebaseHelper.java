package com.example.emin.findact;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseHelper {

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;


//    public void dbHelper(){
//        firebaseDatabase = FirebaseDatabase.getInstance();
//        databaseReference = firebaseDatabase.getReference();
//    }


    public void addUserDetail(String name, String surname, String birthday, String city, String imageURL){
        //dbHelper();

        databaseReference.child("USERS").child("user_email").child("Name").setValue(name);
        databaseReference.child("USERS").child("user_email").child("Surname").setValue(surname);
        databaseReference.child("USERS").child("user_email").child("BirthDay").setValue(birthday);
        databaseReference.child("USERS").child("user_email").child("City").setValue(city);
        databaseReference.child("USERS").child("user_email").child("imageURL").setValue(imageURL);

    }

}
