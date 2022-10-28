package com.example.imageslider.RoomDatabase;

import android.content.Context;

import androidx.room.Room;

public class DatabaseHelper {

    public static ImageDatabase getDatabase(Context context)
    {
        return Room.databaseBuilder(context, ImageDatabase.class, "Image.db")
                .allowMainThreadQueries()
                .build();
    }
}
