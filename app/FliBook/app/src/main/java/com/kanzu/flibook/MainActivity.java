package com.kanzu.flibook;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.kursx.parser.fb2.FictionBook;
import com.kursx.parser.fb2.Xmlns;

import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import javax.xml.parsers.ParserConfigurationException;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Context context = getApplicationContext();
//        File file = new File(context.getExternalFilesDir("books"), "Преступление и наказание. Идиот.fb2");
//        try {
//            FictionBook fictionBook = new FictionBook(file);
//            System.out.println(fictionBook.getBody().getSections().get(0).getSections().get(0).getSections().get(0).getElements().get(0).getText());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        Intent intent = new Intent(MainActivity.this, ReadActivity.class);
        startActivity(intent);

//        ArrayList<BookData> books = null;
//        try {
//            books = (ArrayList<BookData>) Network.searchTask("Преступление и наказание", 1).get();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        Intent intent = new Intent(MainActivity.this, dataActivity.class);
//        intent.putExtra(BookData.class.getSimpleName(), books.get(1));
//        startActivity(intent);


//        try {
//            Network.downloadTask(book, this, "fb2").get();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        //Storage.scanBooksTask(this);
    }

//    public void barButtonOnClick(View view) {
//        Intent intent = new Intent(MainActivity.this, searchActivity.class);
//        startActivity(intent);
//    }
}