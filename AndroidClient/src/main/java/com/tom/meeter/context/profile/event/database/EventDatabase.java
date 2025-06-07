package com.tom.meeter.context.profile.event.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import com.tom.meeter.context.profile.event.domain.Event;

@Database(entities = {Event.class}, version = 1)
public abstract class EventDatabase extends RoomDatabase {
    public abstract EventDao eventDao();
}
