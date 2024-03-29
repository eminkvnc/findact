package com.findact.RoomDatabase;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

@Database(entities = {User.class, Post.class}, version = 1)
public abstract class UserDatabase extends RoomDatabase {

    private static volatile UserDatabase instance;

    public static synchronized UserDatabase getInstance(Context context){
        if (instance == null){
            instance = create(context);
        }
        return instance;
    }

    private static UserDatabase create(final Context context){
        return Room.databaseBuilder(context,UserDatabase.class , "user_detail.db")
                .allowMainThreadQueries()
                .build();
    }

    public abstract UserDao getUserDao();

    public abstract PostDao getPostDao();

}
