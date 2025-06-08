package com.tom.meeter.context.profile.user.database;

import static androidx.room.OnConflictStrategy.REPLACE;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.tom.meeter.context.profile.user.domain.User;

import io.reactivex.Maybe;

@Dao
public interface UserDao {

    @Insert(onConflict = REPLACE)
    void save(User user);

    @Query("SELECT * FROM user WHERE id = :userId")
    Maybe<User> load(String userId);

    @Query("SELECT * FROM user WHERE id = :userId")
    LiveData<User> loadLD(String userId);
}