package com.duongcong.androidmusic.Model;

public class LocalSongModel {

    String name;
    String path;
    String album;
    String artist;
    String type;

    // Set data
    public void setPath(String path) {this.path = path;}

    public void setName(String name) {
        this.name = name;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public void setType(String type) {this.type = type; }

    // Get data
    public String getPath() {return path;}

    public String getName() {
        return name;
    }

    public String getAlbum() {
        return album;
    }

    public String getArtist() {
        return artist;
    }

    public String getType()  {return type;}



}

