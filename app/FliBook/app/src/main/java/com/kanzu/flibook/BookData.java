package com.kanzu.flibook;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import androidx.core.text.TextUtilsCompat;

import java.util.ArrayList;

public class BookData implements Parcelable {
    public String name, description = "", author, genres;
    boolean hasCover;
    Bitmap img;
    public int id;
    ArrayList<String> downloadTypes;

    BookData (int id, String name, String author, String genres) {
        this.name = name;
        this.id = id;
        this.genres = genres;
        this.author = author;
    }

    BookData (int id, String name, String author, String genres, String description, boolean hasCover, Bitmap img) {
        this(id, name, author, genres);
        this.description = description;
        this.hasCover = hasCover;
        this.img = img;
    }

    public BookData() {

    }

    @Override
    public String toString () {
        String res = "Book name: " + name + " ID: " + id + " Author: " + author + " Genres: " + genres;
        if (description.length() > 0) { res += " Has description"; }
        if (hasCover) res += " Has cover";

        return res;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(author);
        dest.writeString(genres);
        dest.writeInt(id);

    }

    public static final Creator<BookData> CREATOR = new Creator<BookData>() {
        @Override
        public BookData createFromParcel(Parcel source) {
            String name = source.readString();
            String author = source.readString();
            String genres = source.readString();
            int id = source.readInt();
            return new BookData(id, name, author, genres);
        }

        @Override
        public BookData[] newArray(int size) {
            return new BookData[size];
        }
    };
}