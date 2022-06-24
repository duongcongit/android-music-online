package com.duongcong.androidmusic.Model;

public class PlaylistModel {

    String id;
    String name;
    String type;

    // Set data
    public void setName(String name) {
        this.name = name;
    }

    public void setType(String type) {this.type = type; }

    // Get data

    public String getName() {
        return name;
    }

    public String getType()  {return type;}



}