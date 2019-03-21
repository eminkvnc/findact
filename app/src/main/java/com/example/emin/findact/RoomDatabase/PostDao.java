package com.example.emin.findact.RoomDatabase;

import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

public interface PostDao {

    @Insert
    void insert(Post post);

    @Query("SELECT * FROM post_detail")
    Post getData();
}
