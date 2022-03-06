package com.kanzu.flibook;

import android.app.ListActivity;
import android.content.Context;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class searchActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_activity);
        ArrayAdapter<String> mAdapter;
        final String[] catNamesArray = new String[] { "Рыжик", "Барсик", "Мурзик",
                "Мурка", "Васька", "Томасина", "Бобик", "Кристина", "Пушок",
                "Дымка", "Кузя", "Китти", "Барбос", "Масяня", "Симба" };
        final String[] gayNamesArray = new String[] { "Биба", "и", "Боба",
                "Два", "Долбаёба", "Томасина", "Бобик", "Кристина", "Пушок",
                "Дымка", "Кузя", "Китти", "Барбос", "Масяня", "Симба" };
        final String[] numNamesArray = new String[] { "1", "2", "3",
                "4", "Долбаёба", "Томасина", "Бобик", "Кристина", "Пушок",
                "Дымка", "Кузя", "Китти", "Барбос", "8", "Симба" };
        CustomListAdapter adapter = new CustomListAdapter(this, catNamesArray, gayNamesArray, numNamesArray);
        ListView list = (ListView) findViewById(R.id.list);
        list.setAdapter(adapter);
        //setContentView(R.layout.search_activity);
//        Context context = getApplicationContext();
//        try {
//            BookData book = new BookData(435116, "Преступление и наказание, Часть 1", "Автор", new ArrayList<String>());
//            System.out.println(Network.downloadTask(book, context));
//        } catch (IOException | InterruptedException | ExecutionException e) {
//            e.printStackTrace();
//        }
    }
}
