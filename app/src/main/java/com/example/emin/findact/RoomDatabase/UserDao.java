package com.example.emin.findact.RoomDatabase;

        import android.arch.lifecycle.LiveData;
        import android.arch.persistence.room.Dao;
        import android.arch.persistence.room.Insert;
        import android.arch.persistence.room.OnConflictStrategy;
        import android.arch.persistence.room.Query;
        import android.arch.persistence.room.Update;

@Dao
public interface UserDao {

    @Insert
    void insert(User user);

    @Update
    void update(User user);

    @Query("SELECT * FROM user_detail WHERE uuid IN(:uuidString)")
    User getDatas(String uuidString);

}
