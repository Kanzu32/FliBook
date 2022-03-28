package com.kanzu.flibook;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.kursx.parser.fb2.Binary;
import com.kursx.parser.fb2.FictionBook;
import com.kursx.parser.fb2.Image;
import com.kursx.parser.fb2.Person;
import com.kursx.parser.fb2.Xmlns;

import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import javax.xml.parsers.ParserConfigurationException;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    @Override
    protected void onResume() {
        super.onResume();

        Context context = getApplicationContext();
//        File file = new File(context.getExternalFilesDir("books"), "Преступление и наказание. Идиот.fb2");
//        try {
//            FictionBook fictionBook = new FictionBook(file);
//            System.out.println(fictionBook.getBody().getSections().get(0).getSections().get(0).getSections().get(0).getElements().get(0).getText());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        ArrayList<String> names = new ArrayList<String>();
        ArrayList<String> authors = new ArrayList<String>();
        ArrayList<Bitmap> imges = new ArrayList<Bitmap>();
        ArrayList<String> fileNames = new ArrayList<String>();
        FictionBook book;
        File folder = context.getExternalFilesDir("books");
        File file;
        for (String item : Objects.requireNonNull(folder.list())) {
            if (item.endsWith(".fb2")) {
                fileNames.add(item);
                try {
                    file = new File(context.getExternalFilesDir("books"), item);
                    book = new FictionBook(file);
                    names.add(book.getTitle());
                    for (Person person : book.getDescription().getSrcTitleInfo().getAuthors()) {
                        authors.add(person.getFullName());
                    }
                    Binary binary = book.getBinaries().get(book.getDescription().getSrcTitleInfo().getCoverPage().get(0).getValue().substring(1));
                    byte[] bytes = binary.getBinary().getBytes();
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    imges.add(bitmap);
                } catch (Exception e) {e.printStackTrace();}
            }
        }


        CustomFileListAdapter adapter = new CustomFileListAdapter(this, names.toArray(new String[0]), authors.toArray(new String[0]), imges.toArray(new Bitmap[0]));
        ListView list = (ListView) findViewById(R.id.list);
        list.setAdapter(adapter);


        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent i = new Intent(MainActivity.this, ReadActivity.class);
                i.putExtra("name", fileNames.get(position));
                startActivity(i);
            }
        });
//        Intent intent = new Intent(MainActivity.this, ReadActivity.class);
//        startActivity(intent);

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

    public void downloadButtonClick(View view) {
        Intent intent = new Intent(MainActivity.this, searchActivity.class);
        startActivity(intent);
    }

}