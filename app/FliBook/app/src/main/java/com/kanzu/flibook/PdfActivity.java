package com.kanzu.flibook;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.github.barteksc.pdfviewer.util.FitPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;

class Bookmark {
    public int page;
}

public class PdfActivity extends AppCompatActivity implements OnPageChangeListener {
    Bookmark bookmark = new Bookmark();
    File bookmarkFile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf);
        String filename = getIntent().getStringExtra("name");
        PDFView pdfView = findViewById(R.id.pdfView);
        bookmark = new Bookmark();
        System.out.println(new File(getBaseContext().getExternalFilesDir("books"), filename).getAbsolutePath());
        bookmarkFile = new File(getBaseContext().getExternalFilesDir("bookmarks"), filename.substring(0, filename.length()-4)+".json");

        if (!bookmarkFile.exists()) {
            try {
                bookmark = new Bookmark();
                bookmark.page = 0;
                Writer writer = new FileWriter(bookmarkFile);
                Gson gson = new GsonBuilder().create();
                gson.toJson(bookmark, writer);
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                Gson gson = new GsonBuilder().create();
                JsonReader reader = new JsonReader(new FileReader(bookmarkFile));
                bookmark = gson.fromJson(reader, Bookmark.class);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        pdfView.fromFile(new File(getBaseContext().getExternalFilesDir("books"), filename))
                .enableSwipe(true)
                .enableDoubletap(true)
                .enableAntialiasing(true)
                .autoSpacing(true)
                .pageFitPolicy(FitPolicy.WIDTH)
                .pageSnap(true)
                .fitEachPage(true)
                .pageFling(true)
                .defaultPage(bookmark.page)
                .onPageChange(this)
                .load();

    }
    @Override
    public void onPageChanged(int page, int pageCount) {
        bookmark.page = page;
    }

    @Override
    public void onStop () {
        try {
            Writer writer = new FileWriter(bookmarkFile);
            Gson gson = new GsonBuilder().create();
            gson.toJson(bookmark, writer);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        super.onStop();

    }


}