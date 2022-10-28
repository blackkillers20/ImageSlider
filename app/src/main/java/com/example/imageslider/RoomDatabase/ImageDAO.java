package com.example.imageslider.RoomDatabase;


import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface ImageDAO {
    @Insert
    void insert(ImageEntity Images);

    @Update
    void update(ImageEntity Images);

    @Query("DELETE FROM ImageEntity WHERE ImageId = :id")
    void delete(int id);

    @Query("SELECT * FROM ImageEntity WHERE ImageId = :id")
    ImageEntity getImageByID(int id);
}
