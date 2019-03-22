package com.example.emin.findact.RoomDatabase;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

@Dao
public interface ActivityDao {

    @Insert
    void insert(Activity activity);

    @Update
    void update(Activity activity);

    @Query("SELECT * FROM activity_post_detail")
    Activity getData();

    @Query("SELECT firebaseId FROM activity_post_detail WHERE firebaseId=:firebaseId LIMIT 1")
    boolean getItemId(String firebaseId);
}