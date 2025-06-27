package com.tom.meeter.context.profile.repository.event.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.tom.meeter.context.profile.repository.event.domain.Event;
import com.tom.meeter.context.profile.repository.user.domain.User;

@Database(
      entities = {
            Event.class,
            User.class
      },
      version = 1
)
public abstract class EventDatabase extends RoomDatabase {
    public abstract EventDao eventDao();
}
