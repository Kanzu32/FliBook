package com.kanzu.flibook;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;

import com.kursx.parser.fb2.FictionBook;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ArrayList<BookData> books = null;
        try {
            books = (ArrayList<BookData>) Network.searchTask("Преступление и наказание", 1).get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Intent intent = new Intent(MainActivity.this, dataActivity.class);
        intent.putExtra(BookData.class.getSimpleName(), books.get(0));
        startActivity(intent);


//        try {
//            Network.downloadTask(book, this, "fb2").get();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        //Storage.scanBooksTask(this);
    }
}