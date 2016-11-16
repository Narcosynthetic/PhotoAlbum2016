package com.example.photoalbum;

/**
 * Created by user on 8/29/2016.
 */
public class Photo {

    private String title;
    private String location;
    private String image;


    public Photo(String title, String location, String image) {

        this.title = title;
        this.location = location;
        this.image = image;
    }

    public Photo(){

    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }


    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
