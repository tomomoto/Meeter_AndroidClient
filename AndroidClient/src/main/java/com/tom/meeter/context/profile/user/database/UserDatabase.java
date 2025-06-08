package com.tom.meeter.context.profile.user.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.tom.meeter.context.profile.user.domain.User;

@Database(entities = {User.class}, version = 2)
public abstract class UserDatabase extends RoomDatabase {
    public abstract UserDao userDao();
}
