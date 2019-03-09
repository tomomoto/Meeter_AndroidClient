package com.example.tom.meeter.context.event.domain;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Maybe;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface EventDao {

    @Insert(onConflict = REPLACE)
    void save(Event event);

    @Insert(onConflict = REPLACE)
    void saveAll(List<Event> events);

    @Query("SELECT * FROM event WHERE id = :eventId")
    Maybe<Event> load(String eventId);

    @Query("SELECT * FROM event WHERE creatorId = :userId")
    Flowable<Event> loadByCreatorId(String userId);

    @Query("SELECT * FROM event WHERE id = :eventId")
    LiveData<Event> loadLD(String eventId);

    @Query("SELECT * FROM event WHERE creatorId = :userId")
    LiveData<List<Event>> loadLDByCreatorId(String userId);
}