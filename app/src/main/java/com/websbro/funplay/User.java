package com.websbro.funplay;

import java.util.ArrayList;

public class User {
    public String email;
    public String name;
    public String photoUrl;
    public String watching;
    public ArrayList<String> genres;
    public ArrayList<String> current;
    public ArrayList<String> recentTvShows;

    public User(String email, String name, String photoUrl,String watching, ArrayList<String> genres, ArrayList<String> current, ArrayList<String> recentTvShows) {
        this.email = email;
        this.name = name;
        this.photoUrl = photoUrl;
        this.watching = watching;
        this.genres = genres;
        this.current = current;
        this.recentTvShows = recentTvShows;
    }

    public ArrayList<String> getGenres() {
        return genres;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public ArrayList<String> getCurrent() {
        return current;
    }

    public ArrayList<String> getRecentTvShows() {
        return recentTvShows;
    }
}
