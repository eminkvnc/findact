package com.findact.RoomDatabase;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface PostDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Post post);

    @Query("SELECT EXISTS(SELECT firebaseId FROM post_detail WHERE firebaseId=:firebaseId)")
    boolean getItemId(String firebaseId);

    @Query("SELECT * FROM post_detail")
    List<Post> getData();

    @Query("DELETE FROM post_detail WHERE firebaseId NOT IN (SELECT firebaseId FROM post_detail ORDER BY logDate DESC LIMIT 15)")
    void deleteOldestPost();
}
