package com.tom.meeter.context.profile.repository.event.database;

import static androidx.room.OnConflictStrategy.REPLACE;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.tom.meeter.context.profile.repository.event.domain.Event;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Maybe;

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

    @Query("DELETE FROM event WHERE creatorId = :userId")
    void deleteByUserId(String userId);

    @Query("SELECT * FROM event WHERE id = :eventId")
    LiveData<Event> loadLD(String eventId);

    @Query("SELECT * FROM event WHERE creatorId = :userId")
    LiveData<List<Event>> loadLDByCreatorId(String userId);
}
