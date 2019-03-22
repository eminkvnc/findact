package com.example.emin.findact.RoomDatabase;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

@Dao
public interface MovieDao {

    @Insert
    void insert(Movie movie);

    @Update
    void update(Movie movie);

    @Query("SELECT * FROM movie_post_detail")
    Movie getData();

    @Query("SELECT firebaseId FROM movie_post_detail WHERE firebaseId =:firebaseId LIMIT 1")
    boolean getItemId(String firebaseId);

}
