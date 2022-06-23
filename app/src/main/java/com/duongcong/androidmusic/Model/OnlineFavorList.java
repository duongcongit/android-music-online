package com.duongcong.androidmusic.Model;

public class OnlineFavorList {
    public String name,url,songCategory;

    public OnlineFavorList(String name, String url, String songCategory) {
        this.name = name;
        this.url = url;
        this.songCategory = songCategory;
    }

    public OnlineFavorList(){}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getSongCategory() {
        return songCategory;
    }

    public void setSongCategory(String songCategory) {
        this.songCategory = songCategory;
    }
}
