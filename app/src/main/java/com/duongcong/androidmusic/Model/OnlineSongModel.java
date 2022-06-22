package com.duongcong.androidmusic.Model;

public class OnlineSongModel {

    String id;
    String name;
    String path;
    String album;
    String artist;
    String type;

    // Set data
    public void setId(String id) {this.id = id; }

    public void setName(String name) { this.name = name;}

    public void setPath(String path) {this.path = path;}

    public void setAlbum(String album) { this.album = album; }

    public void setArtist(String artist) {this.artist = artist;}

    public void setType(String type) {this.type = type; }

    // Get data
    public String getId() {return id; }

    public String getPath() {return path;}

    public String getName() {return name;}

    public String getAlbum() {return album;}

    public String getArtist() {return artist;}

    public String getType()  {return type;}

}

