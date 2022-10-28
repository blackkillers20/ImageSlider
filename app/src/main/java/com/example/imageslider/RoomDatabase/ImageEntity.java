package com.example.imageslider.RoomDatabase;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class ImageEntity {
    @PrimaryKey(autoGenerate = true)
    private int ImageId;
    private String ImageName;
    private String ImageURL;

    public ImageEntity(String imageName, String imageURL) {
        ImageName = imageName;
        ImageURL = imageURL;
    }

    public ImageEntity() {
    }

    public int getImageId() {
        return ImageId;
    }

    public void setImageId(int imageId) {
        ImageId = imageId;
    }

    public String getImageName() {
        return ImageName;
    }

    public void setImageName(String imageName) {
        ImageName = imageName;
    }

    public String getImageURL() {
        return ImageURL;
    }

    public void setImageURL(String imageURL) {
        ImageURL = imageURL;
    }
}
