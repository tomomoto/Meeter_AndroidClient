package com.example.tom.meeter.context.event.domain;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

@Database(entities = {Event.class}, version = 1)
public abstract class EventDatabase extends RoomDatabase {
    public abstract EventDao eventDao();
}
