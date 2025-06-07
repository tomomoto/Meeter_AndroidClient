package com.tom.meeter.context.profile.user.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import io.reactivex.Maybe;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

import com.tom.meeter.context.profile.user.domain.User;

@Dao
public interface UserDao {

    @Insert(onConflict = REPLACE)
    void save(User user);

    @Query("SELECT * FROM user WHERE id = :userId")
    Maybe<User> load(String userId);

    @Query("SELECT * FROM user WHERE id = :userId")
    LiveData<User> loadLD(String userId);
}