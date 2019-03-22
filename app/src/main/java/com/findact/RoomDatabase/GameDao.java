package com.findact.RoomDatabase;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

@Dao
public interface GameDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Game game);

    @Query("SELECT EXISTS(SELECT firebaseId FROM game_post_detail WHERE firebaseId=:firebaseId)")
    boolean getItemId(String firebaseId);

    @Update
    void update(Game game);

    @Query("SELECT * FROM game_post_detail")
    Game getData();
}
