package com.tom.meeter.context.profile.event.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.tom.meeter.context.profile.event.domain.Event;

@Database(entities = {Event.class}, version = 1)
public abstract class EventDatabase extends RoomDatabase {
    public abstract EventDao eventDao();
}
