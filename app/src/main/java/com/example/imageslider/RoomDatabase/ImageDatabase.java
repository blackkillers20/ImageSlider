package com.example.imageslider.RoomDatabase;

import androidx.room.Dao;
import androidx.room.Database;
import androidx.room.Entity;
import androidx.room.RoomDatabase;

@Database(entities = {ImageEntity.class}, version = 1)
public abstract class ImageDatabase extends RoomDatabase {
    public abstract ImageDAO getImageDAO();
}
