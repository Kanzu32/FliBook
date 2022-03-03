package com.kanzu.flibook;

import android.graphics.Bitmap;
import android.text.TextUtils;

import androidx.core.text.TextUtilsCompat;

import java.util.ArrayList;

public class BookData {
    String name, description = "", author, storageLink;
    boolean hasCover;
    Bitmap img;
    int id;
    ArrayList<String> downloadTypes, genres;

    BookData (int id, String name, String author, ArrayList<String> genres) {
        this.name = name;
        this.id = id;
        this.genres = genres;
        this.author = author;
    }

    BookData (int id, String name, String author, ArrayList<String> genres, ArrayList<String> downloadTypes, String description, boolean hasCover, Bitmap img) {
        this(id, name, author, genres);
        this.description = description;
        this.hasCover = hasCover;
        this.img = img;
    }

    @Override
    public String toString () {
        String res = "Book name: " + name + " ID: " + id + " Author: " + author;
        if (genres.size() > 0) {
            res += " Genres: ";
            res += TextUtils.join(", ", genres);
        }
        if (description.length() > 0) { res += " Has description"; }
        if (hasCover) res += " Has cover";

        return res;
    }

}