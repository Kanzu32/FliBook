package com.kanzu.flibook;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class searchActivity extends AppCompatActivity {
    boolean loaded = false;
    int pageNumber;
    int pageCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_activity);
        //setContentView(R.layout.search_activity);
//        Context context = getApplicationContext();
//        try {
//            BookData book = new BookData(435116, "Преступление и наказание, Часть 1", "Автор", new ArrayList<String>());
//            System.out.println(Network.downloadTask(book, context));
//        } catch (IOException | InterruptedException | ExecutionException e) {
//            e.printStackTrace();
//        }
    }

    public void searchButtonClick(View view) throws InterruptedException, ExecutionException, IOException {
        ArrayList<String> names = new ArrayList<String>();
        ArrayList<String> authors = new ArrayList<String>();
        ArrayList<String> genres = new ArrayList<String>();
        TextView textField = (TextView)findViewById(R.id.searchField);
        EditText pageNumberField = (EditText)findViewById(R.id.pageNumberField);
        String numString = pageNumberField.getText().toString();

        if (numString.length() == 0) {
            pageNumber = 0;
        } else {
            pageNumber = Integer.parseInt(pageNumberField.getText().toString());
        }

        String text = textField.getText().toString();
        pageCount = (int)Network.getPageCountTask(text).get();
        if (pageNumber == 0) pageNumber = 1;
        if (pageNumber > pageCount) pageNumber = pageCount;
        ArrayList<BookData> res = (ArrayList<BookData>)Network.searchTask(text, pageNumber).get();
        for (BookData item : res) {
            names.add(item.name);
            authors.add(item.author);
            genres.add(item.genres);
        }

        TextView pageIndicator = (TextView)findViewById(R.id.pageIndicator);
        pageIndicator.setText("/" + (pageCount));

        CustomListAdapter adapter = new CustomListAdapter(this, names.toArray(new String[0]), authors.toArray(new String[0]), genres.toArray(new String[0]));
        ListView list = (ListView) findViewById(R.id.list);
        list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent i = new Intent(searchActivity.this, dataActivity.class);
                i.putExtra(BookData.class.getSimpleName(), res.get(position));
                startActivity(i);
            }
        });

        loaded = true;
    }

    public void nextPageButtonClick (View view) {
        if (loaded && pageNumber < pageCount) {
            EditText field = (EditText) findViewById(R.id.pageNumberField);
            field.setText(String.valueOf(pageNumber+1));
            ImageButton button = (ImageButton) findViewById(R.id.searchButton);
            button.performClick();
        }
    }

    public void prevPageButtonClick (View view) {
        if (loaded && pageNumber > 1) {
            EditText field = (EditText) findViewById(R.id.pageNumberField);
            field.setText(String.valueOf(pageNumber-1));
            ImageButton button = (ImageButton) findViewById(R.id.searchButton);
            button.performClick();
        }
    }
}
