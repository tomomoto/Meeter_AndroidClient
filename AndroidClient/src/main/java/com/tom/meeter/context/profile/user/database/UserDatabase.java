package com.tom.meeter.context.profile.user.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import com.tom.meeter.context.profile.user.domain.User;

@Database(entities = {User.class}, version = 2)
public abstract class UserDatabase extends RoomDatabase {
    public abstract UserDao userDao();
}
